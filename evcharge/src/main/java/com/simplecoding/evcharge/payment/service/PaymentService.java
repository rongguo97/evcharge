package com.simplecoding.evcharge.payment.service;

import com.simplecoding.evcharge.member.entity.Member;
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
     * 포인트 충전 처리 (충전은 보통 카드로 하므로 PAYMENT_TYPE은 'CHARGE', METHOD는 'CARD')
     */
    @Transactional
    public void chargePointWithHistory(Member member, Long amount, String payMethod) {

        // 1. 결제 기록 생성 (신규 필드 반영)
        Payment payment = Payment.builder()
                .member(member)
                .amount(amount)
                .method(payMethod)         // payMethod -> method 로 변경됨
                .paymentType("CHARGE")    // 충전임을 명시
                .reservationId(null)      // 충전 시에는 예약 번호가 없음
                .build();

        paymentRepository.save(payment);

        // 2. 실제 지갑 포인트 증가
        walletService.chargePoint(member, amount);
    }
}