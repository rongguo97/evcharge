package com.simplecoding.evcharge.admin.service;

import com.simplecoding.evcharge.admin.dto.AdminLogDto;
import com.simplecoding.evcharge.admin.dto.AdminWalletDto;
import com.simplecoding.evcharge.wallet.entity.Wallet;
import com.simplecoding.evcharge.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminWalletService {
    private final WalletRepository walletRepository;
    private final AdminLogService adminLogService; // 📍 로그 기록 연동

    @Transactional(readOnly = true)
    public AdminWalletDto.Response getWallet(String email) {
        Wallet wallet = walletRepository.findByEmail(email).orElseThrow();
        return AdminWalletDto.Response.builder()
                .walletId(wallet.getWalletId())
                .email(wallet.getEmail())
                .point(wallet.getPoint())
                .reserveFund(wallet.getReserveFund())
                .build();
    }

    @Transactional
    public void updateReserveFund(String email, AdminWalletDto.UpdateRequest request) {
        Wallet wallet = walletRepository.findByEmail(email).orElseThrow();
        Long oldFund = wallet.getReserveFund();
        wallet.setReserveFund(request.getAmount());

        // 📍 관리자 활동 로그 자동 기록
        adminLogService.saveLog(AdminLogDto.Request.builder()
                .adminEmail(request.getAdminEmail())
                .adminId(request.getAdminId())
                .action("UPDATE_WALLET_FUND")
                .targetId(email)
                .targetType("WALLET")
                .ipAddress("INTERNAL")
                .build());
    }
}