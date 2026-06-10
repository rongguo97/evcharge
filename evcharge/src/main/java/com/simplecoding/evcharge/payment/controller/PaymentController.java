package com.simplecoding.evcharge.payment.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.payment.dto.PaymentDto;
import com.simplecoding.evcharge.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // 💡 추가
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Payment Controller", description = "결제 및 충전 API")
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 적립금 충전 (외부 페이 결제 성공 후 호출)
     */
    @Operation(summary = "적립금 충전", description = "인증된 사용자의 적립금을 충전하고 결제 내역을 남깁니다.")
    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<String>> chargeReserveFund(
            Authentication authentication,         // 수정 1: 보안을 위해 인증 객체 주입
            @RequestParam("amount") Long amount) { // 수정 2: email 파라미터 삭제

        // 💡 수정 3: 토큰(Session)에서 안전하게 이메일을 추출
        // 이제 다른 사람이 내 이메일을 넣고 가짜로 충전 요청을 보낼 수 없습니다.
        String email = authentication.getName();

        // 서비스 호출
        paymentService.chargeReserveFundWithHistory(email, amount);

        ApiResponse<String> response = new ApiResponse<>(true, "적립금 충전 및 결제 내역 저장 완료", "SUCCESS", 0, 0);
        return ResponseEntity.ok(response);
    }
    /**
     * 결제 내역 조회 (마이페이지용)
     */
    @Operation(summary = "결제 내역 조회", description = "인증된 사용자의 결제/충전 내역을 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getPaymentHistory(Authentication authentication) {
        String email = authentication.getName();

        // 서비스 호출해서 내역 가져오기
        List<PaymentDto> history = paymentService.getPaymentHistory(email);

        // ApiResponse에 담아서 리턴 (제네릭 타입은 프로젝트 설정에 맞게 Object 등으로 변경 가능)
        ApiResponse<List<PaymentDto>> response = new ApiResponse<>(true, "결제 내역 조회 완료", history, 0, 0);
        return ResponseEntity.ok(response);
    }
}