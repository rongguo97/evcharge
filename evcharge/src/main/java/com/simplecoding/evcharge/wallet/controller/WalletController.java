package com.simplecoding.evcharge.wallet.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.wallet.dto.WalletDto;
import com.simplecoding.evcharge.wallet.entity.Wallet;
import com.simplecoding.evcharge.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Wallet Controller", description = "지갑 및 포인트 관리 API")
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "지갑 조회", description = "인증된 사용자의 적립금 및 포인트 잔액을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<WalletDto>> getMyWallet(Authentication authentication) {
        String email = authentication.getName();
        Wallet wallet = walletService.getOrCreateWallet(email);

        WalletDto walletDto = WalletDto.builder()
                .walletId(wallet.getWalletId())
                .email(wallet.getEmail())
                .reserveFund(wallet.getReserveFund())
                .point(wallet.getPoint())
                .build();

        return ResponseEntity.ok(new ApiResponse<>(true, "조회 성공", walletDto, 0, 0));
    }

    @Operation(summary = "적립금 충전", description = "적립금을 충전하고 10% 보너스 포인트를 얻습니다.")
    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<Void>> charge(Authentication authentication, @RequestParam Long amount) {
        walletService.chargeReserveFund(authentication.getName(), amount);
        return ResponseEntity.ok(new ApiResponse<>(true, "충전 성공", null, 0, 0));
    }

    @Operation(summary = "포인트 전환", description = "보유 포인트를 적립금으로 1:1 전환합니다.")
    @PostMapping("/convert")
    public ResponseEntity<ApiResponse<Void>> convertPoint(Authentication authentication, @RequestParam Long amount) {
        try {
            walletService.convertPointToFund(authentication.getName(), amount);
            return ResponseEntity.ok(new ApiResponse<>(true, "전환 성공", null, 0, 0));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null, 0, 0));
        }
    }
}