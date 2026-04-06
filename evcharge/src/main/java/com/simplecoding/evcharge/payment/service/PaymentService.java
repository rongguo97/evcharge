package com.simplecoding.evcharge.payment.service;

import com.simplecoding.evcharge.member.entity.Member;
import com.simplecoding.evcharge.payment.entity.Payment;
import com.simplecoding.evcharge.payment.repository.PaymentRepository;
import com.simplecoding.evcharge.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.simplecoding.evcharge.member.entity.QMember.member;

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

//TODO: 혹시나 모를 멤버쉽 로직 추가 버전
//@Service
//@RequiredArgsConstructor
//public class PaymentService {
//
//    private final PaymentRepository paymentRepository;
//    private final WalletService walletService;
//
//    /**
//     * 1. 일반 포인트 충전 결제 (기존 코드)
//     */
//    @Transactional
//    public void processPayment(Member member, Long amount, String payMethod) {
//        // 결제 기록 저장 (공통 로직)
//        savePaymentRecord(member, amount, payMethod, "DONE");
//
//        // 포인트 충전 (지갑 연동)
//        walletService.chargePoint(member, amount);
//    }
//
//    /**
//     * 2. 멤버십 구독 결제 및 승급 (새로 추가할 로직)
//     */
//    @Transactional
//    public void processMembershipSubscription(Member member, Long amount, String payMethod, String targetGrade) {
//        // 결제 기록 저장 (공통 로직 재사용)
//        savePaymentRecord(member, amount, payMethod, "SUBSCRIPTION_" + targetGrade);
//
//        // ✨ 사용자님이 말씀하신 바로 그 로직!
//        // 직접 결제가 성공했으니 멤버 엔티티의 등급만 바꿔주면 끝납니다.
//        member.setGrade(targetGrade.toUpperCase());
//    }
//
//    // [공통 로직] 결제 테이블에 이력을 남기는 메서드 (중복 제거용)
//    private void savePaymentRecord(Member member, Long amount, String payMethod, String status) {
//        Payment payment = Payment.builder()
//                .amount(amount)
//                .payMethod(payMethod)
//                .status(status)
//                .member(member)
//                .build();
//        paymentRepository.save(payment);
//    }
//}