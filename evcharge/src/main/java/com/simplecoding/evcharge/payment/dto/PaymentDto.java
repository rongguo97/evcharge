package com.simplecoding.evcharge.payment.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private Long payId;            // id -> payId (선택사항이나 일관성을 위해 추천)
    private Long amount;
    private String paymentType;    // 신규 추가: CHARGE / USE
    private Long reservationId;    // 신규 추가
    private String email;
    private LocalDateTime createdAt; // insertTime -> createdAt 으로 변경
}