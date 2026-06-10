package com.simplecoding.evcharge.reservation.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.common.dto.CMRespDto;
import com.simplecoding.evcharge.reservation.dto.FeeResult;
import com.simplecoding.evcharge.reservation.dto.ReservationDto;
import com.simplecoding.evcharge.reservation.entity.Reservation;
import com.simplecoding.evcharge.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "예약 추가")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Long>> addReservation(
            @RequestParam String email,
            @RequestParam Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        try {
            Reservation res = reservationService.createReservation(email, stationId, startTime, endTime);
            return new ResponseEntity<>(new ApiResponse<>(true, "예약 완료", res.getReservationId(), 0, 0), HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null, 0, 0), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "서버 오류", null, 0, 0), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "예약 전 예상 요금 조회")
    @GetMapping("/estimate-fee")
    public ResponseEntity<ApiResponse<FeeResult>> estimateFee(
            @RequestParam Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        FeeResult result = reservationService.calculateEstimatedFee(stationId, startTime, endTime);
        return ResponseEntity.ok(new ApiResponse<>(true, "조회 성공", result, 0, 0));
    }

    @Operation(summary = "날짜별 예약 확인")
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
        return ResponseEntity.ok(new ApiResponse<>(true, "조회 성공", page.getContent(), page.getNumber(), page.getTotalElements()));
    }

    @Operation(summary = "최종 요금 조회")
    @GetMapping("/{reservationId}/fee")
    public ResponseEntity<ApiResponse<FeeResult>> getFee(@PathVariable Long reservationId) {
        FeeResult result = reservationService.calculateFee(reservationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "계산 성공", result, 0, 0));
    }

    @Operation(summary = "예약 상세 조회")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ApiResponse<ReservationDto>> detail(@PathVariable Long reservationId) {
        ReservationDto dto = reservationService.getReservation(reservationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "상세 조회 성공", dto, 0, 0));
    }

    @Operation(summary = "예약 취소")
    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable Long reservationId) {
        try {
            reservationService.cancelReservation(reservationId);
            return ResponseEntity.ok(new ApiResponse<>(true, "취소 및 환불 완료", null, 0, 0));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null, 0, 0));
        }
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

    @Operation(summary = "현재 활성화된 예약 조회")
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<ReservationDto>> getCurrent(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String email = authentication.getName();
        ReservationDto currentDto = reservationService.findCurrentReservationDto(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "조회 성공", currentDto, 0, 0));
    }

    @Operation(summary = "내 예약 내역 전체 조회")
    @GetMapping("/history")
    public CMRespDto<List<ReservationDto>> getHistory(Authentication authentication) {
        if (authentication == null) throw new RuntimeException("로그인 정보 없음");
        List<ReservationDto> history = reservationService.getReservationHistory(authentication.getName());
        return new CMRespDto<>(1, "조회 성공", history);
    }
}