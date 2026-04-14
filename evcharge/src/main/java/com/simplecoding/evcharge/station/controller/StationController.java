package com.simplecoding.evcharge.station.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.station.dto.StationDto;
import com.simplecoding.evcharge.station.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Station Controller", description = "전기차 충전소 조회 API")
@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class StationController {

    private final StationService stationService;

    // 1. 키워드 전체 조회
    @Operation(summary = "충전소 전체 조회", description = "키워드로 충전소 목록을 페이징 조회합니다.")
    // StationController.java

    @GetMapping("/station")
    public ResponseEntity<ApiResponse<List<StationDto>>> selectStationList(
            @Parameter(description = "검색 키워드") @RequestParam(defaultValue = "") String searchKeyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String chargerType,
            @RequestParam(required = false) String chargerMethod,
            @PageableDefault(size = 100000)  Pageable pageable) {

        // 📍 수정된 부분: 파라미터들을 모두 서비스로 넘겨줘야 합니다.
        Page<StationDto> page = stationService.selectStationList(
                searchKeyword,
                status,
                chargerType,
                chargerMethod,
                pageable
        );

        ApiResponse<List<StationDto>> response = new ApiResponse<>(
                true, "조회 성공", page.getContent(), page.getNumber(), page.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    // 2. 단일상세조회
    @Operation(summary = "충전소 상세 조회", description = "ID를 이용해 특정 충전소 정보를 조회합니다.")
    @GetMapping("/station/{stationId}")
    public ResponseEntity<ApiResponse<StationDto>> findById(
            @Parameter(description = "충전소 ID") @PathVariable long stationId) {

        StationDto dto = stationService.findById(stationId);

        ApiResponse<StationDto> response = new ApiResponse<>(true, "상세조회 성공", dto, 0, 0);
        return ResponseEntity.ok(response);
    }


    // 6. 내 위치에서 주변 조회
    @Operation(summary = "내 주변 조회 (리스트)", description = "내 위치 기준 반경 내 충전소를 거리순 리스트로 반환합니다.")
    @GetMapping("/station/nearby")
    public ResponseEntity<ApiResponse<List<StationDto>>> selectStationListByLocation(
            @RequestParam Double userLat,
            @RequestParam Double userLng,
            @RequestParam(defaultValue = "5.0") Double radius) {

        List<StationDto> list = stationService.selectStationListByLocation(userLat, userLng, radius);

        // 리스트 방식이므로 페이지 번호와 총 개수는 0으로 세팅하거나 리스트 사이즈를 넣어줌
        ApiResponse<List<StationDto>> response = new ApiResponse<>(
                true, "내 주변 조회 성공", list, 0, list.size()
        );
        return ResponseEntity.ok(response);
    }
}