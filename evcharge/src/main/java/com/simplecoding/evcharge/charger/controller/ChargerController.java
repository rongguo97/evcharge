package com.simplecoding.evcharge.charger.controller;

import com.simplecoding.evcharge.charger.dto.ChargerDto;
import com.simplecoding.evcharge.charger.service.ChargerService;
import com.simplecoding.evcharge.common.ApiResponse; // ApiResponse 패키지 경로 확인!
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

@Tag(name = "Charger Controller", description = "전기차 충전소 관리 API")
@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChargerController {

    private final ChargerService chargerService;

    /**
     * 1. 전체 목록 조회 (페이징 + 검색)
     */
    @Operation(summary = "충전소 전체 조회", description = "검색 키워드로 충전소 목록을 페이징하여 조회합니다.")
    @GetMapping("/charger")
    public ResponseEntity<ApiResponse<List<ChargerDto>>> selectChargerList(
            @Parameter(description = "검색 키워드(충전소명)") @RequestParam(defaultValue = "") String searchKeyword,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Page<ChargerDto> page = chargerService.selectChargerList(searchKeyword, pageable);

        // 박스 포장: (성공유무, 메세지, 결과리스트, 현재페이지, 총개수)
        ApiResponse<List<ChargerDto>> response = new ApiResponse<>(
                true,
                "조회 성공",
                page.getContent(),
                page.getNumber(),
                page.getTotalElements()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 2. 단일 상세 조회 (ID 기준 - 파라미터 방식)
     */
    @Operation(summary = "충전기 상세 조회", description = "ID를 이용해 특정 충전기의 상세 정보를 조회합니다.")
    @GetMapping("/charger/{id}")
    public ResponseEntity<ApiResponse<ChargerDto>> findById(@PathVariable long id) {

        ChargerDto dto = chargerService.findById(id);

        // 상세조회는 페이지 정보가 없으므로 0, 0 전달
        ApiResponse<ChargerDto> response = new ApiResponse<>(true, "상세조회 성공", dto, 0, 0);

        return ResponseEntity.ok(response);
    }

    /**
     * 3. 특정 STATION_ID에 속한 모든 충전기 조회
     */
    @Operation(summary = "충전소별 충전기 목록 조회", description = "STATION_ID를 이용해 해당 충전소의 모든 충전기를 조회합니다.")
    @GetMapping("/charger/station/{stationId}")
    public ResponseEntity<ApiResponse<List<ChargerDto>>> findByStationId(@PathVariable String stationId) {

        List<ChargerDto> list = chargerService.findByStationId(stationId);
        ApiResponse<List<ChargerDto>> response = new ApiResponse<>(true, "충전소별 조회 성공", list, 0, (long) list.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 4. 지역별 조회 (쿼리스트링 방식)
     */
    @Operation(summary = "지역별 충전소 조회", description = "시도와 군구 정보를 이용해 충전소를 조회합니다.")
    @GetMapping("/charger/location")
    public ResponseEntity<ApiResponse<List<ChargerDto>>> findByLocation(
            @RequestParam String sido,
            @RequestParam String gunggu) {

        List<ChargerDto> list = chargerService.findBySidoAndGungguDto(sido, gunggu);
        ApiResponse<List<ChargerDto>> response = new ApiResponse<>(true, "지역별 조회 성공", list, 0, (long) list.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 5. 타입별 조회 (쿼리스트링 방식)
     */
    @Operation(summary = "타입별 충전기 조회", description = "충전기 타입(커넥터) 정보를 이용해 조회합니다.")
    @GetMapping("/charger/type")
    public ResponseEntity<ApiResponse<List<ChargerDto>>> findByChargerType(@RequestParam String chargerType) {

        List<ChargerDto> list = chargerService.findByChargerTypeDto(chargerType);
        ApiResponse<List<ChargerDto>> response = new ApiResponse<>(true, "타입별 조회 성공", list, 0, (long) list.size());

        return ResponseEntity.ok(response);
    }
}