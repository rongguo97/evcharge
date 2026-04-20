package com.simplecoding.evcharge.reservation.entity;


public enum Status {
    RESERVED,
    CHARGING,
    COMPLETED,
    CANCELLED,
    OVERSTAY

//    RESERVED
//   ↓
//    CHARGING
//   ↓
//    COMPLETED
//
//            (예외)
//    RESERVED → CANCELLED
//    CHARGING → OVERSTAY
}