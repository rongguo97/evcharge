package com.simplecoding.evcharge.reservation.schedule;

import com.simplecoding.evcharge.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationSchedule {
    private final ReservationService reservationService;

    // 📍 1분마다 실행 (초 분 시 일 월 요일)
    @Scheduled(cron = "0 * * * * *")
    public void runAutoCancellation() {
        reservationService.cancelReservations();
    }
}
