package com.simplecoding.evcharge.charger.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChargerDto {
    private long id;                    // id
    private String stationId;           //충전소id
    private int chargerId;             //충전기id
    private String sido;                // 시도
    private String gunggu;              // 군구
    private String address;             // 주소
    private String stationName;         // 충전소명
    private String facilityL;           // 시설구분(대)
    private String facilityS;           // 시설구분(소)
    private String modelL;              // 기종(대)
    private String modelS;              // 기종(소)
    private String operatorL;           // 운영기관(대)
    private String operatorS;           // 운영기관(소)
    private String fastChargeAmount;    // 급속충전량
    private String chargerType;         // 충전기타입
    private String userRestriction;     // 이용자제한
}
