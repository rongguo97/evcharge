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

    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<String>> chargePoint(
            @RequestParam("email") String email,
            @RequestParam("amount") Long amount,
            @RequestParam("payMethod") String payMethod) {

        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        // 수정된 서비스 메서드 호출
        paymentService.chargePointWithHistory(member, amount, payMethod);

        ApiResponse<String> response = new ApiResponse<>(true, "결제 및 충전 완료", "SUCCESS", 0, 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}