package com.simplecoding.evcharge.reservation.entity;


import com.simplecoding.evcharge.station.entity.Station;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_RESERVATION")
@Getter
@Setter
@NoArgsConstructor // ✅ JPA 필수
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "res_seq")
    @SequenceGenerator(name = "res_seq", sequenceName = "SQ_RESERVATION", allocationSize = 1)
    private Long reservationId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATION_ID")
    private Station station;

    private String email;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private Status status;


}