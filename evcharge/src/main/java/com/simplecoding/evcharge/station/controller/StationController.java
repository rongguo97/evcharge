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
    @GetMapping("/station")
    public ResponseEntity<ApiResponse<List<StationDto>>> selectStationList(
            @Parameter(description = "검색 키워드") @RequestParam(defaultValue = "") String searchKeyword,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<StationDto> page = stationService.selectStationList(searchKeyword, pageable);

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

    // 3. 현재상태별 조회
    @Operation(summary = "상태별 조회", description = "충전기 상태(1:가능, 2:충전중 등)별로 조회합니다.")
    @GetMapping("/station/status/{status}")
    public ResponseEntity<ApiResponse<List<StationDto>>> selectStationListByStatus(
            @Parameter(description = "상태코드") @PathVariable String status,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<StationDto> page = stationService.selectStationListByStatus(status, pageable);

        ApiResponse<List<StationDto>> response = new ApiResponse<>(
                true, "상태별 조회 성공", page.getContent(), page.getNumber(), page.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }

    // 4. 충전 타입별 조회
    @Operation(summary = "타입별 조회", description = "충전기 타입(1:완속, 2:급속)별로 조회합니다.")
    @GetMapping("/station/type/{chargerType}")
    public ResponseEntity<ApiResponse<List<StationDto>>> selectStationListByType(
            @Parameter(description = "타입코드") @PathVariable String chargerType,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<StationDto> page = stationService.selectStationListByType(chargerType, pageable);

        ApiResponse<List<StationDto>> response = new ApiResponse<>(
                true, "타입별 조회 성공", page.getContent(), page.getNumber(), page.getTotalElements()
        );
        return ResponseEntity.ok(response);
    }
    // 5. 충전 방식별 조회
    @Operation(summary = "방식별 조회", description = "충전방식별로 조회합니다.")
    @GetMapping("/station/type/{chargerMethod}")
    public ResponseEntity<ApiResponse<List<StationDto>>> selectStationListByMethod(
            @Parameter(description = "타입코드") @PathVariable String chargerMethod,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<StationDto> page = stationService.selectStationListByType(chargerMethod, pageable);

        ApiResponse<List<StationDto>> response = new ApiResponse<>(
                true, "타입별 조회 성공", page.getContent(), page.getNumber(), page.getTotalElements()
        );
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