package com.simplecoding.evcharge.reservation.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.reservation.entity.Reservation;
import com.simplecoding.evcharge.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Reservation Controller", description = "예약 관련 API") // Swagger 문서화 추가
@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 신규 예약 등록
     * chargerId를 stationId로 변경하여 일관성을 맞춤
     */
    @Operation(summary = "예약 추가", description = "충전소 ID와 시작 시간을 받아 예약을 생성합니다.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Long>> addReservation(
            @RequestParam String email,
            @RequestParam Long stationId, // 💡 chargerId -> stationId로 변경
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime) {

        // 서비스의 메서드 파라미터와 일치시킴
        Reservation res = reservationService.createReservation(email, stationId, startTime);

        // 생성된 예약의 ID를 반환
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "예약이 성공적으로 완료되었습니다.",
                res.getId(),
                0,
                0
        ));
    }
}