package com.simplecoding.evcharge.payment.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.member.entity.Member;
import com.simplecoding.evcharge.member.repository.MemberRepository;
import com.simplecoding.evcharge.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MemberRepository memberRepository;

    /**
     * 포인트 충전 요청
     * POST http://localhost:8000/api/payment/charge
     */
    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<String>> chargePoint(
            @RequestParam("email") String email,
            @RequestParam("amount") Long amount,
            @RequestParam("payMethod") String payMethod)
    {

        // 1. 회원 찾기
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        // 2. 결제 및 포인트 충전 처리
        paymentService.processPayment(member, amount, payMethod);

        ApiResponse<String> response = new ApiResponse<>(true, "결제 및 충전 완료", "SUCCESS", 0, 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//      TODO: 멤버십 구독 결제 및 승급 요청
//    @PostMapping("/subscribe")
//    public ResponseEntity<ApiResponse<String>> subscribeMembership(
//            @RequestParam("email") String email,
//            @RequestParam("amount") Long amount,
//            @RequestParam("payMethod") String payMethod,
//            @RequestParam("targetGrade") String targetGrade) {
//
//        // 1. 회원 찾기 (기존과 동일한 방식)
//        Member member = memberRepository.findById(email)
//                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
//
//        // 2. 멤버십 전용 결제 및 승급 처리 호출
//        paymentService.processMembershipSubscription(member, amount, payMethod, targetGrade);
//
//        ApiResponse<String> response = new ApiResponse<>(
//                true,
//                targetGrade.toUpperCase() + " 등급으로 멤버십 가입이 완료되었습니다.",
//                "SUCCESS",
//                0, 0
//        );
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

//    TODO: 중복코드 제거 통합본
//    @RestController
//@RequestMapping("/api/payment")
//@RequiredArgsConstructor
//public class PaymentController {
//
//    private final PaymentService paymentService;
//    private final MemberRepository memberRepository;
//
//    // 공통 회원 조회 메서드 (중복 제거)
//    private Member findMember(String email) {
//        return memberRepository.findById(email)
//                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
//    }
//
//    @PostMapping("/charge")
//    public ResponseEntity<ApiResponse<String>> chargePoint(...) {
//        Member member = findMember(email); // 한 줄로 해결
//        paymentService.processPayment(member, amount, payMethod);
//        return new ResponseEntity<>(new ApiResponse<>(true, "충전 완료", "SUCCESS", 0, 0), HttpStatus.OK);
//    }
//
//    @PostMapping("/subscribe")
//    public ResponseEntity<ApiResponse<String>> subscribeMembership(...) {
//        Member member = findMember(email); // 한 줄로 해결
//        paymentService.processMembershipSubscription(member, amount, payMethod, targetGrade);
//        return new ResponseEntity<>(new ApiResponse<>(true, "승급 완료", "SUCCESS", 0, 0), HttpStatus.OK);
//    }
//}
}