package com.simplecoding.evcharge.admin.dto;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AdminStatsDto {
    private String date;         // 프론트엔드의 dataKey="date"와 일치시킴
    private Long reservCount;    // 예약 수
    private Long registCount;    // 신규 가입 수
    private Long cancelCount;    // 취소 수
    private Long revenue;        // 매출액 (AdminChart에서는 사용 안 하지만 StatsAnalysis에서 사용)
}