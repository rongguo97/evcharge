package com.simplecoding.evcharge.charger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CHARGER")
@SequenceGenerator(
        name = "CHARGER_SEQ_GEN",
        sequenceName = "SEQ_CHARGER_ID",   // 시퀀스 이름 맞춤
        allocationSize = 1
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Charger {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHARGER_SEQ_GEN")

        private long id;           // 충전소id

        @Column(name = "STATION_ID")
        private String stationId; // 공공데이터에서 만든 ID

        @Column(name = "CHARGER_ID")
        private int chargerId;              // 충전기id

        @Column(name = "SIDO")
        private String sido;                // 시도

        @Column(name = "GUNGGU")
        private String gunggu;              // 군구

        @Column(name = "ADDRESS")
        private String address;             // 주소

        @Column(name = "STATION_NAME")
        private String stationName;         // 충전소명

        @Column(name = "FACILITY_L")
        private String facilityL;           // 시설구분(대)

        @Column(name = "FACILITY_S")
        private String facilityS;           // 시설구분(소)

        @Column(name = "MODEL_L")
        private String modelL;              // 기종(대)

        @Column(name = "MODEL_S")
        private String modelS;              // 기종(소)

        @Column(name = "OPERATOR_L")
        private String operatorL;           // 운영기관(대)

        @Column(name = "OPERATOR_S")
        private String operatorS;           // 운영기관(소)

        @Column(name = "FAST_CHARGE_AMOUNT")
        private String fastChargeAmount;    // 급속충전량

        @Column(name = "CHARGER_TYPE")
        private String chargerType;         // 충전기타입

        @Column(name = "USER_RESTRICTION")
        private String userRestriction;     // 이용자제한

        @Column(name = "CREATE_DATE", insertable = false, updatable = false)
        private LocalDateTime createDate;   // DB DEFAULT SYSDATE 사용 시 설정

}
