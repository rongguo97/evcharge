package com.simplecoding.evcharge.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeeResult {

    private long usageMinutes;
    private int baseFee;
    private int overstayFee;
// 출력별 시간
    // 완충 6시간 (10분) 2-3원
    // 완충 속도 4시간 (10분) 7원
    // 고출력 완충 3시간 (10분) 11원
    // DC 급속 70분 (10분) 50원
    // DC 고속 40분 (10분) 100원
    // DC 고속 30분 (10분) 150원
    // DC 초급속 20분 (10분) 200-350원
    // 테슬라 20분 (10분) 250원

    //다음 예약과 점유로 인한 예약 취소 에러 알림
//예약 가능여부 사용중, 예약가능
    //예약 관리

}