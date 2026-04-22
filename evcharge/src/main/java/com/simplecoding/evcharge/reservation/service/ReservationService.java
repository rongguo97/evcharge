    package com.simplecoding.evcharge.reservation.service;

    import com.simplecoding.evcharge.common.MapStruct;
    import com.simplecoding.evcharge.payment.service.PaymentService;
    import com.simplecoding.evcharge.reservation.dto.FeeResult;
    import com.simplecoding.evcharge.reservation.dto.ReservationDto;
    import com.simplecoding.evcharge.reservation.entity.ChargeType;
    import com.simplecoding.evcharge.reservation.entity.Reservation;
    import com.simplecoding.evcharge.reservation.entity.Status;
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


        public Page<ReservationDto> getReservationList(String email,
                                                       Status status,
                                                       Pageable pageable) {

            return repository.findReservationList(email, status, pageable);
        }
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

            if (!repository.findOverlapping(stationId, startTime, endTime, rDate).isEmpty()) {
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
                    .status(Status.valueOf("RESERVED"))
                    .build();

            return repository.save(reservation);
        }
        public ReservationDto getReservation(Long id) {

            Reservation reservation = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("예약 없음"));

            return mapper.toDto(reservation);
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
            List<Reservation> reservations = repository.findReservedSlotsByDate(chargerId,rDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            return reservations.stream()
                    .map(r -> r.getStartTime().format(formatter) + " - " + r.getEndTime().format(formatter))
                    .collect(Collectors.toList());
        }

        @jakarta.transaction.Transactional
        public void cancelReservation(Long id) {

            Reservation reservation = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("예약 없음"));

            if (reservation.getStatus() != Status.RESERVED) {
                throw new RuntimeException("예약 상태만 취소 가능");
            }

            reservation.setStatus(Status.CANCELLED);
        }

        @jakarta.transaction.Transactional
        public void startCharging(Long id) {

            Reservation reservation = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("예약 없음"));

            LocalDateTime now = LocalDateTime.now();

            // 1. 시작 가능 조건 (-10분 허용)
            if (reservation.getStartTime().minusMinutes(10).isAfter(now)) {
                throw new RuntimeException("아직 시작 시간이 아닙니다.");
            }

            if (reservation.getStatus() != Status.RESERVED) {
                throw new RuntimeException("예약 상태만 시작 가능");
            }

            // 2. 상태 변경
            reservation.setStatus(Status.CHARGING);
        }
        @jakarta.transaction.Transactional
        public void endCharging(Long id) {

            Reservation reservation = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("예약 없음"));

            if (reservation.getStatus() != Status.CHARGING) {
                throw new RuntimeException("충전 중 상태만 종료 가능");
            }

            LocalDateTime now = LocalDateTime.now();

            // 1. 초과 여부 판단
            if (reservation.getEndTime().isBefore(now)) {
                reservation.setStatus(Status.OVERSTAY);
            } else {
                reservation.setStatus(Status.COMPLETED);
            }
        }

        public Object calculateFee(Long id) {

            Reservation reservation = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("예약 없음"));
            ChargeType type = reservation.getChargeType();
            LocalDateTime start = reservation.getStartTime();
            LocalDateTime end = reservation.getEndTime() != null
                    ? reservation.getEndTime()
                    : LocalDateTime.now();

            long minutes = java.time.Duration.between(start, end).toMinutes();

            //  분당 요금
            double pricePerMinute = type.getPricePer10Min() / 10.0;

            //  기본 요금 (완충 시간까지만)
            long normalMinutes = Math.min(minutes, type.getFullChargeMinutes());
            int baseFee = (int) Math.round(normalMinutes * pricePerMinute);

            //  초과 요금 (무조건 1분당 100원)
            int overstayFee = 0;
            if (minutes > type.getFullChargeMinutes()) {

                long overMinutes = minutes - type.getFullChargeMinutes();
                overstayFee = (int) (overMinutes * 100);
            }

            return new FeeResult(minutes, baseFee, overstayFee);
        }

        @jakarta.transaction.Transactional
        public void pay(Long id, Object paymentRequest) {

            Reservation reservation = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("예약 없음"));

            if (reservation.getStatus() == Status.CANCELLED) {
                throw new RuntimeException("취소된 예약은 결제 불가");
            }
            if (reservation.getStatus() != Status.COMPLETED &&
                    reservation.getStatus() != Status.OVERSTAY) {
                throw new RuntimeException("결제 가능한 상태가 아닙니다.");
            }
            // 결제 성공 가정
            reservation.setStatus(Status.COMPLETED);
        }}


//1. 충전소/충전기 조회 (상태 포함)

//2. 예약
//   - 중복 예약 방지
//   - 동시성 처리
//   - 예약 유효시간 관리

//3. 시작
//   - 자동 시작 (예약 시간 도달) 충전시작
//   - 수동 시작 (예약 전 도달) -10분
//   - 상태 연동

//4. 충전 진행
//   - 상태 실시간 업데이트
//   - 최대 시간 / 완충 감지

//5. 종료
//   - 수동 종료
//   - 자동 종료 (완충/시간초과/(오류-회의 필요))

//6. 이용시간 & 요금 계산
//
//7. 결제 처리
//   - 성공/실패/재시도

//8. 후처리
//   - 알림
    // -예약 전
    // 오버차지
//   - 로그 저장

//예약 생성
//  ↓
//중복 체크
//  ↓
//RESERVED
//
//→ 시작 (-10분 허용)
//  ↓
//CHARGING
//
//→ 종료
//  ↓
//COMPLETED or OVERSTAY
//
//→ 요금 계산
//  ↓
//결제
