package com.simplecoding.evcharge.charger.entity;

import com.simplecoding.evcharge.station.entity.Station;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_CHARGER")
@Getter
@Setter
public class Charger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chargerId; // 충전기 고유 번호 (PK)
    // [중요] 어떤 충전소에 소속되어 있는지 연결 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATION_ID")
    private Station station;
    private String cType; // 충전 방식 (급속/완속)
    @Column(columnDefinition = "VARCHAR2(20) DEFAULT 'AVAILABLE'")    //
    private String cStatus; // 현재 상태 (AVAILABLE, CHARGING, FAULT 등)
}