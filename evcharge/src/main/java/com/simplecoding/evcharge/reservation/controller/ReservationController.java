package com.simplecoding.evcharge.reservation.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.reservation.entity.Reservation;
import com.simplecoding.evcharge.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 📍 로그를 위해 추가
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j // 📍 로그 어노테이션 추가
@Tag(name = "Reservation Controller", description = "예약 관련 API")
@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 신규 예약 등록
     */
    @Operation(summary = "예약 추가", description = "충전소 ID와 시작 시간을 받아 예약을 생성합니다.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Long>> addReservation(
            @RequestParam String email,
            @RequestParam Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {

        // 📍 데이터가 컨트롤러 입구까지 잘 들어오는지 확인용 (가장 먼저 찍힘)
        System.out.println("======= [예약 요청 발생] =======");
        System.out.println("Email: " + email);
        System.out.println("StationId: " + stationId);
        System.out.println("StartTime: " + startTime);
        System.out.println("===============================");

        try {
            Reservation res = reservationService.createReservation(email, stationId, startTime);

            return new ResponseEntity<>(new ApiResponse<>(
                    true,
                    "예약이 성공적으로 완료되었습니다.",
                    res.getId(),
                    0,
                    0
            ), HttpStatus.CREATED);

        } catch (IllegalStateException e) {
            // 📍 비즈니스 로직 에러 (중복 예약 등) 콘솔에 출력
            System.err.println("!!! [비즈니스 로직 에러] : " + e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null,
                    0,
                    0
            ), HttpStatus.CONFLICT);

        } catch (Exception e) {
            // 📍 500 에러의 진짜 원인을 콘솔에 '빨간 글씨'로 쏟아냅니다.
            System.err.println("!!! [서버 내부 오류 발생] - 아래 StackTrace를 확인하세요 !!!");
            e.printStackTrace(); // 📍 이게 핵심입니다. 에러의 근원지를 알려줍니다.

            return new ResponseEntity<>(new ApiResponse<>(
                    false,
                    "예약 중 서버 오류가 발생했습니다: " + e.getMessage(),
                    null,
                    0,
                    0
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}