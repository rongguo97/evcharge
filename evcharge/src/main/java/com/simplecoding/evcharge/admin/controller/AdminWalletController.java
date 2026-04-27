package com.simplecoding.evcharge.admin.controller;

import com.simplecoding.evcharge.admin.dto.AdminWalletDto;
import com.simplecoding.evcharge.admin.service.AdminWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자가 사용자의 지갑(예치금, 포인트)을 관리하기 위한 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/admin/wallets")
@RequiredArgsConstructor
public class AdminWalletController {

    private final AdminWalletService adminWalletService;

    /**
     * 1. 특정 사용자의 지갑 정보 조회
     * @param email 조회할 사용자 이메일
     */
    @GetMapping("/{email}")
    public ResponseEntity<AdminWalletDto.Response> getWalletInfo(@PathVariable String email) {
        return ResponseEntity.ok(adminWalletService.getWallet(email));
    }

    /**
     * 2. 사용자의 예치금(Reserve Fund) 직접 수정
     * @param email 수정할 사용자 이메일
     * @param request 수정할 금액 및 관리자 정보가 담긴 DTO
     */
    @PutMapping("/{email}/reserve-fund")
    public ResponseEntity<Void> updateReserveFund(
            @PathVariable String email,
            @RequestBody AdminWalletDto.UpdateRequest request) {
        adminWalletService.updateReserveFund(email, request);
        return ResponseEntity.ok().build();
    }
}