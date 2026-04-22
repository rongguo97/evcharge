package com.simplecoding.evcharge.reservation.entity;

import lombok.Getter;

@Getter
public enum ChargeType {

    // 완속
    SLOW_6H(360, 3, Category.SLOW),
    SLOW_4H(240, 7, Category.SLOW),
    SLOW_3H(180, 11, Category.SLOW),

    // 급속
    FAST_70M(70, 50, Category.FAST),
    FAST_40M(40, 100, Category.FAST),
    FAST_30M(30, 150, Category.FAST),
    FAST_20M(20, 300, Category.FAST); // 평균값 사용 (200~350)

    private final int fullChargeMinutes;
    private final int pricePer10Min;
    private final Category category;

    ChargeType(int fullChargeMinutes, int pricePer10Min, Category category) {
        this.fullChargeMinutes = fullChargeMinutes;
        this.pricePer10Min = pricePer10Min;
        this.category = category;
    }

    public enum Category {
        SLOW, FAST
    }
}