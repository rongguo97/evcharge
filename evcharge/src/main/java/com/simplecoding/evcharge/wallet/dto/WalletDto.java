package com.simplecoding.evcharge.wallet.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WalletDto {
    private Long id;         // 지갑 고유 번호
    private String email;    // 회원 이메일
    private Long point;      // 현재 잔액
}