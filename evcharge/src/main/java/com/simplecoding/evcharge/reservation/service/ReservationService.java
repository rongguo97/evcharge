package com.simplecoding.evcharge.reservation.service;

import com.simplecoding.evcharge.common.MapStruct;
import com.simplecoding.evcharge.payment.service.PaymentService;
import com.simplecoding.evcharge.reservation.dto.FeeResult;
import com.simplecoding.evcharge.reservation.dto.ReservationDto;
import com.simplecoding.evcharge.reservation.entity.Reservation;
import com.simplecoding.evcharge.reservation.repository.ReservationRepository;
import com.simplecoding.evcharge.station.entity.Station;
import com.simplecoding.evcharge.station.repository.StationRepository;
import com.simplecoding.evcharge.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository repository;
    private final StationRepository stationRepository;
    private final MapStruct mapper;
    private final PaymentService paymentService;
    private final WalletService walletService;

    public Page<ReservationDto> getReservationList(String email, String status, Pageable pageable) {
        return repository.findReservationList(email, status, pageable);
    }

    /**
     * [POST] 신규 예약 등록 (결제 및 이용 내역 포함)
     */
    @Transactional
    public Reservation createReservation(String email, Long stationId, LocalDateTime startTime, LocalDateTime endTime) {
        List<String> activeStatuses = List.of("RESERVED", "CHARGING");
            List<Reservation> userActiveReservations = repository.findByEmailAndStatusIn(email, activeStatuses);
        if (startTime.isBefore(LocalDateTime.now().plusMinutes(10))) {
            throw new IllegalArgumentException("예약은 최소 시작 10분 전까지만 가능합니다.");
        }
        if (!userActiveReservations.isEmpty()) {
            throw new IllegalStateException("이미 진행 중인 예약이나 충전 내역이 있습니다.");
        }

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 충전소 ID입니다: " + stationId));

        String rDate = startTime.toLocalDate().toString();
        if (!repository.findOverlapping(stationId, startTime, endTime, rDate).isEmpty()) {
            throw new IllegalStateException("선택하신 시간대에 이미 다른 예약이 존재하여 예약이 불가능합니다.");
        }

        FeeResult estimate = calculateEstimatedFee(stationId, startTime, endTime);
        int totalFee = estimate.getBaseFee();

        // 1. 지갑 잔액 차감
        walletService.useBalance(email, (long) totalFee);

        Reservation reservation = Reservation.builder()
                .email(email)
                .station(station)
                .startTime(startTime)
                .endTime(endTime)
                .rDate(rDate)
                .status("RESERVED")
                .build();

        // 2. 예약 저장
        Reservation savedReservation = repository.save(reservation);

        // 3. 결제 내역 저장 (타입: RESERVE_USAGE 명시) 📍 수정됨
        if (totalFee > 0) {
            paymentService.saveUsageHistory(email, (long) totalFee, savedReservation, "RESERVE_USAGE");
        }

        return savedReservation;
    }

    public ReservationDto getReservation(Long id) {
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        ReservationDto dto = mapper.toDto(reservation);
        if (reservation.getStation() != null) {
            dto.setStationName(reservation.getStation().getStationName());
        }
        return dto;
    }

    public List<String> getReservedTimeSlots(Long chargerId, LocalDate date) {
        String rDate = date.toString();
        List<Reservation> reservations = repository.findReservedSlotsByDate(chargerId, rDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return reservations.stream()
                .map(r -> r.getStartTime().format(formatter) + " - " + r.getEndTime().format(formatter))
                .collect(Collectors.toList());
    }

    /**
     * [PUT] 예약 취소 (환불 및 환불 내역 포함)
     */
    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약 정보를 찾을 수 없습니다."));

        if (!"RESERVED".equals(reservation.getStatus())) {
            throw new RuntimeException("이미 취소되었거나 완료된 예약은 취소할 수 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(reservation.getStartTime().minusMinutes(10))) {
            throw new RuntimeException("예약 취소는 시작 시간 10분 전까지만 가능합니다.");
        }

        // 💰 [환불 로직 시작] 📍 수정됨
        FeeResult feeResult = calculateEstimatedFee(
                reservation.getStation().getStationId(),
                reservation.getStartTime(),
                reservation.getEndTime()
        );

        int refundAmount = feeResult.getBaseFee();

        if (refundAmount > 0) {
            // 1. 지갑 잔액 복구
            walletService.chargeReserveFund(reservation.getEmail(), (long) refundAmount);

            // 2. 이용 내역에 환불 기록 추가 (타입: REFUND 명시)
            paymentService.saveUsageHistory(reservation.getEmail(), (long) refundAmount, reservation, "REFUND");
        }
        // [환불 로직 종료]

        // 4. 예약 상태 변경 (Dirty Checking)
        reservation.setStatus("CANCELLED");
    }

    @Transactional
    public void startCharging(Long reservationId) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        LocalDateTime now = LocalDateTime.now();
        if (reservation.getStartTime().minusMinutes(10).isAfter(now)) {
            throw new RuntimeException("아직 시작 시간이 아닙니다.");
        }

        if (!"RESERVED".equals(reservation.getStatus())) {
            throw new RuntimeException("예약 대기 상태에서만 충전을 시작할 수 있습니다.");
        }

        reservation.setStatus("CHARGING");
    }

    @Transactional
    public void endCharging(Long reservationId) {
        Reservation res = repository.findById(reservationId).orElseThrow();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime plannedEnd = res.getEndTime();

        long diffMinutes = java.time.Duration.between(plannedEnd, now).toMinutes();

        if (diffMinutes < 0) {
            long unusedMinutes = Math.abs(diffMinutes);
            long refundAmount = unusedMinutes * 100;
            walletService.chargeReserveFund(res.getEmail(), refundAmount);
        } else if (diffMinutes > 0) {
            long penaltyAmount = diffMinutes * 150;
            walletService.spendReserveFund(res.getEmail(), penaltyAmount);
        }

        res.setStatus("COMPLETED");
    }

    public FeeResult calculateFee(Long reservationId) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        LocalDateTime start = reservation.getStartTime();
        LocalDateTime end = reservation.getEndTime() != null ? reservation.getEndTime() : LocalDateTime.now();

        long minutes = java.time.Duration.between(start, end).toMinutes();
        String chargerType = reservation.getStation().getChargerType();

        int pricePer10Min = 1000;
        long fullChargeMinutes = 60;

        if (chargerType != null) {
            String typeStr = chargerType.toUpperCase().replace(" ", "");
            if (typeStr.contains("100KW") || typeStr.contains("급속")) {
                pricePer10Min = 2000;
                fullChargeMinutes = 40;
            } else if (typeStr.contains("50KW")) {
                pricePer10Min = 1500;
                fullChargeMinutes = 70;
            } else if (typeStr.contains("완속") || typeStr.contains("7KW")) {
                pricePer10Min = 500;
                fullChargeMinutes = 240;
            }
        }

        double pricePerMinute = pricePer10Min / 10.0;
        long normalMinutes = Math.min(minutes, fullChargeMinutes);
        int baseFee = (int) Math.round(normalMinutes * pricePerMinute);
        int overstayFee = (minutes > fullChargeMinutes) ? (int) ((minutes - fullChargeMinutes) * 100) : 0;

        return new FeeResult(minutes, baseFee, overstayFee);
    }

    @Transactional
    public void pay(Long reservationId, Object paymentRequest) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        String currentStatus = reservation.getStatus();
        if ("CANCELLED".equals(currentStatus)) throw new RuntimeException("취소된 예약은 결제 불가");
        if (!"COMPLETED".equals(currentStatus) && !"OVERSTAY".equals(currentStatus)) {
            throw new RuntimeException("결제 가능한 상태가 아닙니다.");
        }
        reservation.setStatus("COMPLETED");
    }

    public FeeResult calculateEstimatedFee(Long stationId, LocalDateTime start, LocalDateTime end) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 충전소입니다."));

        long minutes = java.time.Duration.between(start, end).toMinutes();
        String chargerType = station.getChargerType();

        int pricePer10Min = 1000;
        long fullChargeMinutes = 60;

        if (chargerType != null) {
            String typeStr = chargerType.toUpperCase().replace(" ", "");
            if (typeStr.contains("100KW") || typeStr.contains("급속")) {
                pricePer10Min = 2000;
                fullChargeMinutes = 40;
            } else if (typeStr.contains("50KW")) {
                pricePer10Min = 1500;
                fullChargeMinutes = 70;
            } else if (typeStr.contains("완속") || typeStr.contains("7KW")) {
                pricePer10Min = 500;
                fullChargeMinutes = 240;
            }
        }

        double pricePerMinute = pricePer10Min / 10.0;
        int baseFee = (int) Math.round(Math.min(minutes, fullChargeMinutes) * pricePerMinute);

        return new FeeResult(minutes, baseFee, 0);
    }

    @Transactional
    public void cancelReservations() {
        LocalDateTime limitTime = LocalDateTime.now().minusMinutes(10);
        List<Reservation> expiredList = repository.findExpiredReservations(limitTime);
        for (Reservation res : expiredList) {
            res.setStatus("CANCELLED");
        }
    }

    public ReservationDto findCurrentReservationDto(String email) {
        List<Reservation> list = repository.findCurrentReservationByEmail(email);
        if (list.isEmpty()) return null;

        Reservation res = list.get(0);
        ReservationDto dto = mapper.toDto(res);
        if (res.getStation() != null) {
            dto.setStationName(res.getStation().getStationName());
            dto.setStationId(res.getStation().getStationId());
        }
        return dto;
    }

    public List<ReservationDto> getReservationHistory(String email) {
        List<Reservation> reservations = repository.findMyReservations(email);

        return reservations.stream()
                .map(res -> {
                    ReservationDto dto = ReservationDto.builder()
                            .reservationId(res.getReservationId())
                            .email(res.getEmail())
                            .startTime(res.getStartTime())
                            .endTime(res.getEndTime())
                            .status(res.getStatus())
                            .rDate(res.getRDate())
                            .build();

                    if (res.getStation() != null) {
                        dto.setStationName(res.getStation().getStationName());
                        dto.setAddress(res.getStation().getAddress());
                        dto.setStationId(res.getStation().getStationId());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}