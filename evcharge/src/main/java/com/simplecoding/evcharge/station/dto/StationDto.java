package com.simplecoding.evcharge.station.dto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StationDto {
    @Schema(description = "충전소 ID")
    private Long stationId;
    @Schema(description = "충전기 ID")
    private Long chargerId;
    @Schema(description = "주소")
    private String address;
    @Schema(description = "충전소 이름")
    private String stationName;
    @Schema(description = "충전기 이름")
    private String chargerName;
    @Schema(description = "충전유형")
    private String chargerType;
    @Schema(description = "현재상태")
    private String status;
    @Schema(description = "충전방식")
    private String chargerMethod;
    @Schema(description = "위도")
    private Double lat;
    @Schema(description = "경도")
    private Double lng;
    @Schema(description = "수정시간")
    private String statUpdateDatetime;
//    db에 추가안하고 dto에만 추가한 이유: 충전기위치는 고정되어 있지만 사용자 위치는 매번 변하기에
//    따로 db에 저장해두는게 아니라순간 sql에서 계산해서 던져주는 가상의 값
//    distance를 담을 바구니 역할
@Schema(description = "거리", example = "내 주변 충전소 찾기에 사용 ")
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



