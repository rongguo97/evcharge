package com.simplecoding.evcharge.reservation.service;

import com.simplecoding.evcharge.payment.service.PaymentService;
import com.simplecoding.evcharge.reservation.entity.Reservation;
import com.simplecoding.evcharge.reservation.repository.ReservationRepository;
import com.simplecoding.evcharge.station.entity.Station;
import com.simplecoding.evcharge.station.repository.StationRepository;
import com.simplecoding.evcharge.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StationRepository stationRepository;
    private final PaymentService paymentService;
    private final WalletService walletService;

    @Transactional
    public Reservation createReservation(String email, Long stationId, LocalDateTime startTime) {
        // 1. 충전소 존재 확인
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("충전소를 찾을 수 없습니다."));

        // 2. 이용 시간 계산 (메서드 내용 보완)
        int durationMinutes = calculateDuration(station.getChargerType());
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes + 10); // 버퍼 10분

        // 3. 중복 예약 체크 (Repository 메서드와 stationId 매칭 확인 필요)
        if (!reservationRepository.findOverlapping(stationId, startTime, endTime).isEmpty()) {
            throw new RuntimeException("이미 해당 시간에 예약이 존재합니다.");
        }

        // 4. 결제 로직 연동 (필요 시 주석 해제)
        // walletService에 subtractPoint(String email, Long amount) 메서드가 있다면 사용 가능
        // walletService.subtractPoint(email, 5000L);

        // 5. 예약 저장 (Lombok Builder 사용)
        Reservation reservation = Reservation.builder()
                .email(email)
                .station(station)
                .startTime(startTime)
                .endTime(endTime)
                .status("RESERVED")
                .build();

        return reservationRepository.save(reservation);
    }

    /**
     * 충전기 타입에 따른 이용 시간 계산 로직 보완
     */
    private int calculateDuration(String chargerType) {
        if (chargerType == null) return 60; // 기본값

        if (chargerType.contains("급속") || chargerType.contains("100kW")) {
            return 40; // 급속은 보통 40분
        } else if (chargerType.contains("50kW")) {
            return 70; // 50kW는 70분
        } else {
            return 60; // 완속 및 기타 60분
        }
    }
}