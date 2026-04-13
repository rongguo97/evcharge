package com.simplecoding.evcharge.station.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StationDto {
    private Long stationId;
    private Long chargerId;
    private String address;
    private String stationName;
    private String chargerName;
    private String chargerType;
    private String status;
    private String chargerMethod;
    private Double lat;
    private Double lng;
    private String statUpdateDatetime;
//    db에 추가안하고 dto에만 추가한 이유: 충전기위치는 고정되어 있지만 사용자 위치는 매번 변하기에
//    따로 db에 저장해두는게 아니라순간 sql에서 계산해서 던져주는 가상의 값
//    distance를 담을 바구니 역할
    private Double distance;

    // StationDto.java 내부의 9개짜리 생성자를 아래와 같이 교체하세요.

    public StationDto(Object stationId, String stationName, String address,
                      Object lat, Object lng, String status,
                      String chargerType, String chargerMethod, Object distance) {
        // 1. Long 타입 변환 (stationId)
        this.stationId = (stationId instanceof Number) ? ((Number) stationId).longValue() : null;

        this.stationName = stationName;
        this.address = address;

        // 2. Double 타입 변환 (lat, lng, distance)
        this.lat = (lat instanceof Number) ? ((Number) lat).doubleValue() : null;
        this.lng = (lng instanceof Number) ? ((Number) lng).doubleValue() : null;

        this.status = status;
        this.chargerType = chargerType;
        this.chargerMethod = chargerMethod;

        // 3. 거리 변환
        this.distance = (distance instanceof Number) ? ((Number) distance).doubleValue() : null;
    }
}



