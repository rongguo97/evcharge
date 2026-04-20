package com.simplecoding.evcharge.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeeResult {

    private long usageMinutes;
    private int baseFee;
    private int overstayFee;
}