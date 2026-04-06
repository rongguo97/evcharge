package com.simplecoding.evcharge.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReservationDto {

    @NotNull
    private Long reservationId;

    @NotNull
    private Long chargerId;

    @NotNull
    private String email;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private String status;

}
