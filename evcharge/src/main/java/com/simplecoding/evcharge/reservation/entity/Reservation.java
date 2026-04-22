package com.simplecoding.evcharge.reservation.entity;

import com.simplecoding.evcharge.station.entity.Station;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_RESERVATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "res_seq")
    @SequenceGenerator(name = "res_seq", sequenceName = "SQ_RESERVATION", allocationSize = 1)
    @Column(name = "RESERVATION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    // 2. nullable = false 추가 (예약에는 반드시 충전소가 있어야 함)
    @JoinColumn(name = "STATION_ID", nullable = false)
    private Station station;

    // 1. nullable = false 추가 (ERD에서 NOT NULL 스펙)
    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;



    @Column(name = "START_TIME", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "END_TIME", nullable = false)
    private LocalDateTime endTime;

    // 3. length 제한 및 기본값 명시


    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "R_Date", length = 20)
    private String rDate;

    @Enumerated(EnumType.STRING)
    private ChargeType chargeType;
}