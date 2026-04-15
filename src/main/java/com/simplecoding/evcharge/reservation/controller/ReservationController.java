package com.simplecoding.evcharge.reservation.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.reservation.dto.ReservationDto;
import com.simplecoding.evcharge.reservation.entity.Status;
import com.simplecoding.evcharge.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService service;
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservationDto>>> getList(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Status status,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {

        Page<ReservationDto> page = service.getReservationList(email, status, pageable);

        ApiResponse<List<ReservationDto>> response =
                new ApiResponse<>(
                        true,
                        "예약 조회 성공",
                        page.getContent(),
                        page.getNumber(),
                        page.getTotalElements()
                );

        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ReservationDto dto) {

        service.createReservation(dto);

        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationDto>> detail(@PathVariable Long id) {

        ReservationDto dto = service.getReservation(id);

        ApiResponse<ReservationDto> response =
                new ApiResponse<>(true, "상세 조회 성공", dto, 0, 0);

        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {

        service.cancelReservation(id);

        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}/start")
    public ResponseEntity<Void> start(@PathVariable Long id) {

        service.startCharging(id);

        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}/end")
    public ResponseEntity<Void> end(@PathVariable Long id) {

        service.endCharging(id);

        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/payment")
    public ResponseEntity<Void> payment(@PathVariable Long id,
                                        @RequestBody Object paymentRequest) {

        service.pay(id, paymentRequest);

        return ResponseEntity.ok().build();
    }}
//
//| API     | 상태                   |
//        | ------- | -------------------- |
//        | /start  | RESERVED → CHARGING  |
//        | /end    | CHARGING → COMPLETED |
//        | /cancel | RESERVED → CANCELLED |

//GET    /reservations
//POST   /reservations
//GET    /reservations/{id}
//PUT    /reservations/{id}/start
//PUT    /reservations/{id}/end
//PUT    /reservations/{id}/cancel