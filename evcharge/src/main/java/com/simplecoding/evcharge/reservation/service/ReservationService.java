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

    // paymentService와 walletService는 나중에 결제/취소 로직 확장 시 활용하세요.
    private final PaymentService paymentService;
    private final WalletService walletService;


    public Page<ReservationDto> getReservationList(String email, String status, Pageable pageable) {
        return repository.findReservationList(email, status, pageable);
    }


    @Transactional
    public Reservation createReservation(String email, Long stationId, LocalDateTime startTime, LocalDateTime endTime) {
        // 0. 예약 가능 시간 검증
        if (startTime.isBefore(LocalDateTime.now().plusMinutes(10))) {
            throw new IllegalArgumentException("예약은 최소 시작 10분 전까지만 가능합니다.");
        }

        // 1. 충전소 존재 확인
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 충전소 ID입니다: " + stationId));

        // 2. 중복 예약 체크
        String rDate = startTime.toLocalDate().toString();
        if (!repository.findOverlapping(stationId, startTime, endTime, rDate).isEmpty()) {
            throw new IllegalStateException("선택하신 시간대에 이미 다른 예약이 존재하여 예약이 불가능합니다.");
        } // 📍 중복 체크는 여기서 끝내야 합니다!

        // 3. 📍 결제 금액 계산 (중복이 없을 때만 여기로 내려옵니다)
        FeeResult estimate = calculateEstimatedFee(stationId, startTime, endTime);
        int totalFee = estimate.getBaseFee();

        // 4. 📍 실제 포인트 차감
        walletService.spendReserveFund(email, (long) totalFee); // 📍 세미콜론 추가 완료!

        // 5. 예약 저장
        Reservation reservation = Reservation.builder()
                .email(email)
                .station(station)
                .startTime(startTime)
                .endTime(endTime)
                .rDate(rDate)
                .status("PAID") // 📍 결제가 끝났으니 "PAID"로 저장하는 게 더 정확합니다!
                .build();

        return repository.save(reservation);
    }

    public ReservationDto getReservation(Long id) {
        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("예약 없음"));
        return mapper.toDto(reservation);
    }

    /**
     * 날짜 및 시간 별 예약 확인
     */
    public List<String> getReservedTimeSlots(Long chargerId, LocalDate date) {
        String rDate = date.toString();
        List<Reservation> reservations = repository.findReservedSlotsByDate(chargerId, rDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return reservations.stream()
                .map(r -> r.getStartTime().format(formatter) + " - " + r.getEndTime().format(formatter))
                .collect(Collectors.toList());
    }

    @jakarta.transaction.Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        if (!"RESERVED".equals(reservation.getStatus())) {
            throw new RuntimeException("예약 대기 상태인 경우에만 취소할 수 있습니다.");
        }
        reservation.setStatus("CANCELLED");
    }

    // 📍 1. 충전 시작 로직 수정 완료
    @jakarta.transaction.Transactional
    public void startCharging(Long reservationId) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        LocalDateTime now = LocalDateTime.now();

        // 1. 시작 가능 조건 (-10분 허용)
        if (reservation.getStartTime().minusMinutes(10).isAfter(now)) {
            throw new RuntimeException("아직 시작 시간이 아닙니다.");
        }

        if (!"RESERVED".equals(reservation.getStatus())) {
            throw new RuntimeException("예약 대기 상태에서만 충전을 시작할 수 있습니다.");
        }

        // 2. 상태 변경 (CHARGING으로 정상 변경)
        reservation.setStatus("CHARGING");
    }

    // 📍 2. 충전 종료 로직 수정 완료
    @jakarta.transaction.Transactional
    public void endCharging(Long reservationId) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        // 충전 중일 때만 종료 가능하도록 검증
        if (!"CHARGING".equals(reservation.getStatus())) {
            throw new RuntimeException("충전 중인 상태에서만 종료할 수 있습니다.");
        }

        // 상태를 충전 완료(COMPLETED)로 변경
        reservation.setStatus("COMPLETED");
    }

    // 📍 3. 요금 계산 로직 수정 완료 (에러 없는 DB 타입 조회 방식으로 복구)
    public Object calculateFee(Long reservationId) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        LocalDateTime start = reservation.getStartTime();
        LocalDateTime end = reservation.getEndTime() != null
                ? reservation.getEndTime()
                : LocalDateTime.now();

        long minutes = java.time.Duration.between(start, end).toMinutes();

        // 충전소 정보에서 타입 가져오기
        String chargerType = reservation.getStation().getChargerType();

        int pricePer10Min = 1000; // 기본 단가
        long fullChargeMinutes = 60; // 기본 허용 시간

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

        // 분당 요금
        double pricePerMinute = pricePer10Min / 10.0;

        // 기본 요금 (완충 시간까지만)
        long normalMinutes = Math.min(minutes, fullChargeMinutes);
        int baseFee = (int) Math.round(normalMinutes * pricePerMinute);

        // 초과 요금 (무조건 1분당 100원)
        int overstayFee = 0;
        if (minutes > fullChargeMinutes) {
            long overMinutes = minutes - fullChargeMinutes;
            overstayFee = (int) (overMinutes * 100);
        }

        return new FeeResult(minutes, baseFee, overstayFee);
    }

    // 📍 4. 결제 로직 수정 완료
    @jakarta.transaction.Transactional
    public void pay(Long reservationId, Object paymentRequest) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        String currentStatus = reservation.getStatus();

        if ("CANCELLED".equals(currentStatus)) {
            throw new RuntimeException("취소된 예약은 결제 불가");
        }

        // 상태가 'COMPLETED'도 아니고 'OVERSTAY'도 아니면 에러!
        if (!"COMPLETED".equals(currentStatus) && !"OVERSTAY".equals(currentStatus)) {
            throw new RuntimeException("결제 가능한 상태가 아닙니다.");
        }

        // 결제 성공 가정
        reservation.setStatus("COMPLETED");
    }
    // 1. 📍 예상 요금 계산 메서드 (리턴 타입을 FeeResult로 명시!)
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
        long normalMinutes = Math.min(minutes, fullChargeMinutes);
        int baseFee = (int) Math.round(normalMinutes * pricePerMinute);

        return new FeeResult(minutes, baseFee, 0); // 📍 FeeResult 객체 반환
    }
}