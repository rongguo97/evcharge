package com.simplecoding.evcharge.station.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StationDto {
    private Long stationId;
    private String stationName;
    private String address;
    private BigDecimal lat;
    private BigDecimal lng;
    private String isDeleted;



}
