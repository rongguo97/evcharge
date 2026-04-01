package com.simplecoding.evcharge.station.entity;

import com.practice.practice.charger.entity.Charger;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_STATION")
@Getter
@Setter
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stationId; // 충전소 고유 번호 (PK)

    @Column(nullable = false)
    private String stationName; // 충전소 이름

    private String address; // 상세 주소

    @Column(precision = 10, scale = 8)    //전체 숫자개수(소수점 포함 10개까지 허용)
    private BigDecimal lat; // 위도 (지도 표시용)

    @Column(precision = 11, scale = 8)    //그 10개중 소수점 아래에 8개만 배정하겠다
    private BigDecimal lng; // 경도 (지도 표시용)

    private String isDeleted = "N"; // 폐쇄 여부

    // [중요] 한 충전소는 여러 개의 충전기를 가짐 (1:N 관계)
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
    private List<Charger> chargers = new ArrayList<>();
}