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

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class ReservationService {

        private final ReservationRepository reservationRepository;
        private final StationRepository stationRepository;
        // paymentService와 walletService는 나중에 결제/취소 로직 확장 시 활용하세요.
        private final PaymentService paymentService;
        private final WalletService walletService;


        @Transactional
        public Reservation createReservation(String email, Long stationId, LocalDateTime startTime, LocalDateTime endTime) {
            // [추가] 0. 예약 가능 시간 검증 (현재 시간 + 10분 여유 체크)
            // 9분 남은 시점부터 자동으로 막기 위함
            if (startTime.isBefore(LocalDateTime.now().plusMinutes(10))) {
                throw new IllegalArgumentException("예약은 최소 시작 10분 전까지만 가능합니다.");
            }
            // 1. 충전소 존재 확인
            Station station = stationRepository.findById(stationId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 충전소 ID입니다: " + stationId));

            // 3. 중복 예약 체크
            String rDate = startTime.toLocalDate().toString();

            if (!reservationRepository.findOverlapping(stationId, startTime, endTime, rDate).isEmpty()) {
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
                    .rDate(rDate)
                    .status("RESERVED")
                    .build();

            return reservationRepository.save(reservation);
        }

        /**
         * 충전기 타입에 따른 이용 시간 계산 (안정성 강화)
         */
        private int calculateDuration(String chargerType) {
            if (chargerType == null || chargerType.isEmpty()) return 60;

            // 대소문자 무시 및 공백 제거 후 비교
            String type = chargerType.toUpperCase().replace(" ", "");

            if (type.contains("100KW") || type.contains("급속")) {
                return 40;
            } else if (type.contains("50KW")) {
                return 70;
            } else if (type.contains("완속") || type.contains("7KW")) {
                return 240; // 완속은 보통 시간이 훨씬 오래 걸리므로 4시간 등으로 설정 가능
            }

            return 60; // 기본값
        }
    //         날짜 및 시간 별 예약 확인
        public List<String> getReservedTimeSlots(Long chargerId, LocalDate date) {
            String rDate = date.toString();
            List<Reservation> reservations = reservationRepository.findReservedSlotsByDate(chargerId,rDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            return reservations.stream()
                    .map(r -> r.getStartTime().format(formatter) + " - " + r.getEndTime().format(formatter))
                    .collect(Collectors.toList());
        }
    }