package com.simplecoding.evcharge.admin.dto;

import lombok.*;

public class AdminWalletDto {
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long walletId;
        private String email;
        private Long point;
        private Long reserveFund;
    }

    @Getter @Builder @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UpdateRequest {
        private Long amount;        // 변경할 금액
        private String adminEmail;  // 로그용 관리자 이메일
        private Long adminId;      // 로그용 관리자 ID
    }
}