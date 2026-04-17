package com.simplecoding.evcharge.payment.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment Controller", description = "결제 및 충전 API")
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 적립금 충전 (외부 페이 결제 성공 후 호출)
     */
    @Operation(summary = "적립금 충전", description = "외부 결제 성공 후 사용자의 적립금을 충전하고 내역을 남깁니다.")
    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<String>> chargeReserveFund(
            @RequestParam("email") String email,
            @RequestParam("amount") Long amount) { // payMethod는 요구사항에 따라 삭제 가능

        // 1. 서비스 호출 (Member 객체 대신 email 문자열 전달)
        // 메서드명도 point에서 reserveFund로 변경 제안
        paymentService.chargeReserveFundWithHistory(email, amount);

        ApiResponse<String> response = new ApiResponse<>(true, "적립금 충전 및 결제 내역 저장 완료", "SUCCESS", 0, 0);
        return ResponseEntity.ok(response);
    }
}