package com.simplecoding.evcharge.charger.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChargerDto {
    private Long chargerId;    // 충전기 PK
    private Long stationId;    // [중요] 소속된 충전소의 ID (FK 역할)
    private String cType;      // 충전 방식 (급속/완속)
    private String cStatus;    // 현재 상태 (AVAILABLE, CHARGING 등)
    // 필요하다면 충전소 이름을 함께 보여주기 위해 추가할 수 있습니다.
//    private String stationName;
}