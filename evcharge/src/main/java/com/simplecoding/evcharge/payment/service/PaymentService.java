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
     * 포인트 충전 처리
     */
    @Transactional
    public void chargePointWithHistory(Member member, Long amount, String payMethod) {

        // 1. 결제 기록 생성 (수정된 부분: member 객체 대신 email 문자열을 넣음)
        Payment payment = Payment.builder()
                .email(member.getEmail())  // [수정] .member(member) -> .email(member.getEmail())
                .amount(amount)
                .method(payMethod)
                .paymentType("CHARGE")
                .reservationId(null)
                .build();

        paymentRepository.save(payment);

        // 2. 실제 지갑 포인트 증가
        // walletService 내부에서도 Member 객체에서 email을 뽑아 쓰도록 수정.
        walletService.chargePoint(member, amount);
    }
}