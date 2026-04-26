package com.simplecoding.evcharge.payment.service;

import com.simplecoding.evcharge.payment.dto.PaymentDto;
import com.simplecoding.evcharge.payment.entity.Payment;
import com.simplecoding.evcharge.payment.repository.PaymentRepository;
import com.simplecoding.evcharge.reservation.entity.Reservation; // 💡 임포트 추가
import com.simplecoding.evcharge.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WalletService walletService;

    /**
     * 1) 적립금 충전 처리 (외부 결제 완료 후 호출)
     */
    @Transactional
    public void chargeReserveFundWithHistory(String email, Long amount) {
        // 💡 방어 코드 추가: 충전 금액 검증
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("충전 금액이 올바르지 않습니다.");
        }

        // 1. 실제 지갑 데이터 업데이트 (Dirty Checking으로 DB 자동 반영)
        walletService.chargeReserveFund(email, amount);

        // 2. 결제 기록(영수증) 생성 및 저장
        Payment payment = Payment.builder()
                .email(email)
                .amount(amount)
                .paymentType("TOPUP")
                .reservation(null) // 충전 시에는 예약 정보 없음
                .build();

        paymentRepository.save(payment);
    }

    /**
     * 2) 예약 시 적립금 사용 내역 저장 (ReservationService에서 호출)
     * 수정 포인트: 파라미터에서 긴 패키지 경로를 지우고 Reservation 객체를 직접 받습니다.
     */
    @Transactional
    public void saveUsageHistory(String email, Long amount, Reservation reservation) {
        // 💡 방어 코드: 지갑 잔액 차감은 이미 WalletService나 호출 측에서 검증되었다고 가정하지만,
        // 이력을 남길 때 예약 정보가 없으면 안 되므로 체크합니다.
        if (reservation == null) {
            throw new IllegalArgumentException("결제 내역을 남기기 위한 예약 정보가 없습니다.");
        }

        Payment payment = Payment.builder()
                .email(email)
                .amount(amount)
                .paymentType("RESERVE_USAGE") // 적립금 사용
                .reservation(reservation)      // 연관 관계 매핑
                .build();

        paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentHistory(String email) {
        // 1. DB에서 내 이메일로 된 모든 결제 이력을 최신순으로 가져옵니다.
        List<Payment> payments = paymentRepository.findByEmailOrderByPayIdDesc(email);

        // 2. Entity를 DTO로 변환해서 반환합니다.
        return payments.stream().map(payment -> {
            String stationName = null;
            Long resId = null;

            // 💡 예약 정보(RESERVE_USAGE)가 연결되어 있다면 충전소 이름을 꺼내옵니다.
            if (payment.getReservation() != null) {
                resId = payment.getReservation().getReservationId();
                if (payment.getReservation().getStation() != null) {
                    stationName = payment.getReservation().getStation().getStationName();
                }
            }

            return PaymentDto.builder()
                    .payId(payment.getPayId()) // PK 변수명(id)에 맞춰서 작성해주세요!
                    .amount(payment.getAmount())
                    .paymentType(payment.getPaymentType())
                    .email(payment.getEmail())
                    .createdAt(payment.getCreatedAt()) // 엔티티에 시간 필드가 있다면 활성화
                    .reservationId(resId)
                    .stationName(stationName)
                    .build();
        }).collect(Collectors.toList());
    }
}