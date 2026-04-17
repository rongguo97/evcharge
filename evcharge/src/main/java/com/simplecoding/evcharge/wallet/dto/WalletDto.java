package com.simplecoding.evcharge.wallet.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class WalletDto {
    private Long walletId;      // 지갑 고유 번호
    private String email;       // 회원 이메일

    //  추가: 실제 예약 결제에 사용하는 금액
    private Long reserveFund;

    //  용도 변경: 결제 시 10%씩 쌓이는 보너스 포인트
    private Long point;
}