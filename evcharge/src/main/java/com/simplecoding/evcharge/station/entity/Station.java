package com.simplecoding.evcharge.station.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_STATION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder // 빌더 패턴을 쓰면 데이터 저장 시 편리합니다.
@ToString
@EqualsAndHashCode
// 1. 시퀀스 제너레이터 설정 (DB의 SEQ_STATION_ID와 연결)
@SequenceGenerator(
        name = "STATION_SEQ_GEN",
        sequenceName = "SEQ_STATION_ID", // 사진 속 SEQUENCE_NAME과 일치 시킴
        initialValue = 1,
        allocationSize = 1
)
public class Station {

        @Id
        // 2. 전략을 SEQUENCE로 변경하고 위에서 만든 제너레이터 연결
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STATION_SEQ_GEN")
        @Column(name = "STATION_ID")
        private Long stationId;

        @Column(name = "CHARGER_ID")
        private Long chargerId;

        @Column(name = "ADDRESS", length = 255)
        private String address;

        @Column(name = "STATION_NAME", length = 100)
        private String stationName;

        @Column(name = "CHARGER_NAME", length = 100)
        private String chargerName;

        @Column(name = "CHARGER_TYPE", length = 100)
        private String chargerType;

        @Column(name = "STATUS", length = 100)
        private String status;

        @Column(name = "CHARGER_METHOD", length = 100)
        private String chargerMethod;

        @Column(name = "LAT")
        private Double lat;

        @Column(name = "LNG")
        private Double lng;

        // ERD와 대소문자까지 동일하게 맞춤
        @Column(name = "statUpdateDatetime")
        private LocalDateTime statUpdateDatetime;
}