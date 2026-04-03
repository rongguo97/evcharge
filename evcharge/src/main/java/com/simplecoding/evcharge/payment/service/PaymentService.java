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
    private final WalletService walletService; // 중요: 지갑 서비스와 협업함.

    /**
     * 포인트 충전 결제 처리
     * 1. 결제 내역(Payment)을 저장한다.
     * 2. 회원의 지갑(Wallet) 포인트를 업데이트한다.
     */
    @Transactional // 중요: 두 작업 중 하나라도 실패하면 모두 취소(Rollback)됨.
    public void processPayment(Member member, Long amount, String payMethod) {

        // 1. 결제 기록 생성 및 저장
        Payment payment = Payment.builder()
                .amount(amount)
                .payMethod(payMethod)
                .status("DONE") // 일단 성공으로 가정
                .member(member)
                .build();

        paymentRepository.save(payment);

        // 2. 지갑 서비스에 충전 요청 (이전에 만든 메서드 활용)
        walletService.chargePoint(member, amount);
    }
}