package com.simplecoding.evcharge.wallet.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.wallet.dto.WalletDto;
import com.simplecoding.evcharge.wallet.entity.Wallet;
import com.simplecoding.evcharge.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Wallet Controller", description = "지갑 및 포인트 관리 API")
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * 내 지갑 정보 조회
     */
    @Operation(summary = "지갑 조회", description = "사용자의 이메일로 적립금 및 포인트 잔액을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<WalletDto>> getMyWallet(@RequestParam("email") String email) {

        Wallet wallet = walletService.getOrCreateWallet(email);

        WalletDto walletDto = WalletDto.builder()
                .walletId(wallet.getWalletId())
                .email(wallet.getEmail())
                .reserveFund(wallet.getReserveFund())
                .point(wallet.getPoint())
                .build();

        return ResponseEntity.ok(new ApiResponse<>(true, "지갑 정보 조회 성공", walletDto, 0, 0));
    }

    /**
     * 💡 추가 제안: 포인트를 적립금으로 전환
     * 포인트를 1:1 비율로 적립금(Reserve Fund)으로 변환합니다.
     */
    @Operation(summary = "포인트 전환", description = "보유한 포인트를 실제 예약에 사용 가능한 적립금으로 전환합니다.")
    @PostMapping("/convert")
    public ResponseEntity<ApiResponse<Void>> convertPoint(
            @RequestParam String email,
            @RequestParam Long amount) {

        try {
            walletService.convertPointToFund(email, amount);
            return ResponseEntity.ok(new ApiResponse<>(true, "적립금 전환 성공", null, 0, 0));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null, 0, 0));
        }
    }
}