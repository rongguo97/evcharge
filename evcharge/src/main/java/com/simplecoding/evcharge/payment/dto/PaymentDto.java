package com.simplecoding.evcharge.payment.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private Long id;
    private Long amount;
    private String payMethod;
    private String status;
    private String email;
    private LocalDateTime insertTime; // 결제 일시
}