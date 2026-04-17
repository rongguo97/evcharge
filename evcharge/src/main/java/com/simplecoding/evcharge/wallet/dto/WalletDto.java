package com.simplecoding.evcharge.wallet.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class WalletDto {

    private Long walletId;   // 지갑 고유 번호 (id에서 명칭 변경)
    private String email;    // 회원 이메일
    private Long reserveFund; //현재 적립금
    private Long point;      // 현재 포인트
}