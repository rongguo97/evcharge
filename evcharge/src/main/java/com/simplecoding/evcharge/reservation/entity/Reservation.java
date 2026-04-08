package com.simplecoding.evcharge.reservation.entity;

import com.simplecoding.evcharge.station.entity.Station;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_RESERVATION")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "res_seq")
    @SequenceGenerator(name = "res_seq", sequenceName = "SQ_RESERVATION", allocationSize = 1)
    @Column(name = "RESERVATION_ID")
    private Long id;

    @Column(name = "EMAIL") // Member 객체 대신 email 문자열 사용 (Wallet/Payment와 통일)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATION_ID") // Charger -> Station으로 변경
    private Station station;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    @Column(name = "STATUS")
    private String status; // 기본값 "RESERVED"
}