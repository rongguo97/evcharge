package com.simplecoding.evcharge.reservation.dto;

import com.simplecoding.evcharge.reservation.entity.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReservationDto {

    private Long reservationId;
    private Long stationId;

    @NonNull
    private String email;

    @NonNull
    private LocalDateTime startTime;

    private LocalDateTime endTime;
    private Status status;

    private String stationName;
    private String address;




}