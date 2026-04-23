package com.simplecoding.evcharge.reservation.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.reservation.dto.FeeResult;
import com.simplecoding.evcharge.reservation.dto.ReservationDto;
import com.simplecoding.evcharge.reservation.entity.Reservation;
import com.simplecoding.evcharge.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    // 📍 중복되었던 서비스를 하나로 통일했습니다!
    private final ReservationService reservationService;

    @Operation(summary = "예약 추가 (프론트 연동용)", description = "충전소 ID와 시작/종료 시간을 받아 예약을 생성합니다.")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Long>> addReservation(
            @RequestParam String email,
            @RequestParam Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        try {
            Reservation res = reservationService.createReservation(email, stationId, startTime, endTime);
            return new ResponseEntity<>(new ApiResponse<>(
                    true, "예약이 성공적으로 완료되었습니다.", res.getReservationId(), 0, 0
            ), HttpStatus.CREATED);

        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new ApiResponse<>(
                    false, e.getMessage(), null, 0, 0
            ), HttpStatus.CONFLICT);
        } catch (Exception e) {
            // 📍 서버 에러 발생 시 무조건 콘솔에 원인을 출력하도록 함정 설치!
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(
                    false, "서버 오류: " + e.getMessage(), null, 0, 0
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "날짜 및 시간 별 예약 확인")
    @GetMapping("/slots")
    public ResponseEntity<List<String>> getReservedSlots(
            @RequestParam("chargerId") Long chargerId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<String> reservedSlots = reservationService.getReservedTimeSlots(chargerId, date);
        return ResponseEntity.ok(reservedSlots);
    }

    @Operation(summary = "예약 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getList(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String status,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<ReservationDto> page = reservationService.getReservationList(email, status, pageable);
        ApiResponse<List<ReservationDto>> response = new ApiResponse<>(
                true, "예약 조회 성공", page.getContent(), page.getNumber(), page.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "예약 상세 조회")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationDto>> detail(@PathVariable Long reservationId) {
        ReservationDto dto = reservationService.getReservation(reservationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "상세 조회 성공", dto, 0, 0));
    }

    @Operation(summary = "예약 취소")
    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "충전 시작")
    @PutMapping("/{reservationId}/start")
    public ResponseEntity<Void> start(@PathVariable Long reservationId) {
        reservationService.startCharging(reservationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "충전 종료")
    @PutMapping("/{reservationId}/end")
    public ResponseEntity<Void> end(@PathVariable Long reservationId) {
        reservationService.endCharging(reservationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "결제 처리")
    @PostMapping("/{reservationId}/payment")
    public ResponseEntity<Void> payment(@PathVariable Long reservationId,
                                        @RequestBody Object paymentRequest) {
        reservationService.pay(reservationId, paymentRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "요금 조회")
    @GetMapping("/{reservationId}/fee")
    public ResponseEntity<ApiResponse<FeeResult>> getFee(@PathVariable Long reservationId) {
        FeeResult result = (FeeResult) reservationService.calculateFee(reservationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "요금 계산 성공", result, 0, 0));
    }
}