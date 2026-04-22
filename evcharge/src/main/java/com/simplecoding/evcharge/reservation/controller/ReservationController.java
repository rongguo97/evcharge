package com.simplecoding.evcharge.reservation.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.reservation.dto.FeeResult;
import com.simplecoding.evcharge.reservation.dto.ReservationDto;
import com.simplecoding.evcharge.reservation.entity.Status;
import com.simplecoding.evcharge.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            // 📍 1. 프론트가 보내는 endTime을 받도록 파라미터 추가!
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        try {
            // 📍 2. 서비스로 endTime도 같이 넘겨주기!
            Reservation res = reservationService.createReservation(email, stationId, startTime, endTime);

            // 3. HTTP 상태 코드를 201 Created로 반환하는 것이 RESTful 규약에 더 가깝습니다.
            return new ResponseEntity<>(new ApiResponse<>(
                    true,
                    "예약이 성공적으로 완료되었습니다.",
                    res.getId(),
                    0,
                    0
            ), HttpStatus.CREATED);

        } catch (IllegalStateException e) {
            // 중복 예약 등 비즈니스 로직 에러 처리
            return new ResponseEntity<>(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null,
                    0,
                    0
            ), HttpStatus.CONFLICT); // 409 Conflict
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(
                    false,
                    "예약 중 서버 오류가 발생했습니다.",
                    null,
                    0,
                    0
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //         날짜 및 시간 별 예약 확인
    @GetMapping("/slots")
    public ResponseEntity<List<String>> getReservedSlots(
            @RequestParam("chargerId") Long chargerId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<String> reservedSlots = reservationService.getReservedTimeSlots(chargerId, date);
        return ResponseEntity.ok(reservedSlots);
    }
}
