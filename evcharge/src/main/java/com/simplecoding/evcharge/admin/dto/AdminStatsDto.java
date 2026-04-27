package com.simplecoding.evcharge.admin.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AdminStatsDto {
    private String date;        // "2024-05-20" 형식
    private Long reservCount;   // 해당 날짜의 예약 건수
    private Long registCount;   // 해당 날짜의 신규 가입자 수
}