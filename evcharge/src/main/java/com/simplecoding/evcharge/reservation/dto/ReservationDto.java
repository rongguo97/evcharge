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

    private ChargeType chargeType;


    // 초과요금 추가 (서비스에서 쓰고 있었음)
    private int overstayFee;

    public ReservationDto(Long reservationId, Long stationId, @NonNull String email, @NonNull LocalDateTime startTime, LocalDateTime endTime, Status status, String stationName, String address) {
        this.reservationId = reservationId;
        this.stationId = stationId;
        this.email = email;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.stationName = stationName;
        this.address = address;
    }
}