package com.simplecoding.evcharge.payment.service;

import com.simplecoding.evcharge.payment.entity.Payment;
import com.simplecoding.evcharge.payment.repository.PaymentRepository;
import com.simplecoding.evcharge.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final WalletService walletService;

    /**
     * 적립금 충전 처리 (외부 결제 완료 후 호출)
     */
    @Transactional
    public void chargeReserveFundWithHistory(String email, Long amount) {

        // 1. 실제 지갑 데이터 업데이트 (WalletService 호출)
        // 💡 내부적으로 적립금 증가 + 10% 포인트 적립이 동시에 일어납니다.
        walletService.chargeReserveFund(email, amount);

        // 2. 결제 기록 생성 및 저장
        Payment payment = Payment.builder()
                .email(email)
                .amount(amount)

                // .method(payMethod) // 요구사항에 따라 삭제됨
                .paymentType("TOPUP") // 'CHARGE' 보다 'TOPUP'(충전)이 더 명확.
                .reservation(null)    // 충전 시에는 연결된 예약이 없음

    }

    /**
     * 예약 결제 내역 저장 (ReservationService에서 호출용)
     */
    @Transactional
    public void saveUsageHistory(String email, Long amount, com.simplecoding.evcharge.reservation.entity.Reservation reservation) {
        Payment payment = Payment.builder()
                .email(email)
                .amount(amount)
                .paymentType("RESERVE_USAGE") // 적립금 사용을 통한 예약
                .reservation(reservation)      // 어떤 예약에 쓴 건지 연결
                .build();

        paymentRepository.save(payment);
    }
}