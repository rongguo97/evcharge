package com.simplecoding.evcharge.reservation.entity;

import com.simplecoding.evcharge.charger.entity.Charger;
import jakarta.persistence.*;
import jakarta.transaction.Status;

import java.time.LocalDateTime;

public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESERVATION_ID")
    private Long id;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "CHARGER_ID")
    private Charger charger;
}
