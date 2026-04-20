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
    // paymentService와 walletService는 나중에 결제/취소 로직 확장 시 활용하세요.
    private final PaymentService paymentService;
    private final WalletService walletService;

    @Transactional
    public Reservation createReservation(String email, Long stationId, LocalDateTime startTime) {
        // 1. 충전소 존재 확인
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 충전소 ID입니다: " + stationId));

        // 2. 이용 시간 계산 (버퍼 타임 포함)
        int durationMinutes = calculateDuration(station.getChargerType());
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes + 10);

        // 3. 중복 예약 체크
        if (!reservationRepository.findOverlapping(stationId, startTime, endTime).isEmpty()) {
            throw new IllegalStateException("선택하신 시간대에 이미 다른 예약이 존재하여 예약이 불가능합니다.");
        }

        // 4. 포인트 차감 (비즈니스 정책에 따라 추가)
        // 예: 기본 예약금 5,000원 선결제 로직이 필요하다면 여기서 호출
        // walletService.subtractPoint(email, 5000L);

        // 5. 예약 저장
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
     * 충전기 타입에 따른 이용 시간 계산 (안정성 강화)
     */
    private int calculateDuration(String chargerType) {
        if (chargerType == null || chargerType.isEmpty()) return 60;

        String type = chargerType.trim();

        // 📍 보내주신 case "type" 로직을 그대로 반영합니다.
        switch (type) {
            case "2": // 급속
                return 40; // ⚡ 급속은 짧게 (40분)

            case "1": // 완속
                return 60; // 🐌 완속은 길게 (4시간 - 필요시 60~120분으로 조절)

            default:
                // 만약 "01"~"08" 같은 상세 method 코드가 들어올 경우를 대비
                if (type.equals("05") || type.equals("06") || type.equals("07") || type.equals("08")) {
                    return 40; // 급속 계열 커넥터들
                }
                return 60; // 기본값
        }
    }
}