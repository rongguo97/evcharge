package com.simplecoding.evcharge.charger.controller;

import com.simplecoding.evcharge.charger.dto.ChargerDto;
import com.simplecoding.evcharge.charger.service.ChargerService;
import com.simplecoding.evcharge.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charger")
@RequiredArgsConstructor
public class ChargerController {

    private final ChargerService chargerService;

    // 1. 목록 조회 (특정 충전소 기준)
    @GetMapping("/charger")
    public ResponseEntity<ApiResponse<List<ChargerDto>>> selectChargerList(@RequestParam("stationId") Long stationId) {
        List<ChargerDto> list = chargerService.selectChargerList(stationId);

        // ApiResponse 형식으로 감싸기 (데이터 개수, 페이지 정보 포함)
        ApiResponse<List<ChargerDto>> response =
                new ApiResponse<>(true, "충전기 목록 조회 성공", list, list.size(), 0);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 2. 상세 조회 (특정 충전기 하나)
    @GetMapping("/{chargerId}")
    public ResponseEntity<ApiResponse<ChargerDto>> selectChargerDetail(@PathVariable("chargerId") Long chargerId) {
        ChargerDto dto = chargerService.selectChargerDetail(chargerId);

        // 상세 조회는 단건이므로 페이징은 0, 0으로 통일
        ApiResponse<ChargerDto> response =
                new ApiResponse<>(true, "충전기 상세 조회 성공", dto, 0, 0);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<ChargerDto>>> getFilteredChargers(
            @RequestParam("type") String cType,
            @RequestParam("status") String cStatus) {

        List<ChargerDto> list = chargerService.selectAvailableChargers(cType, cStatus);

        ApiResponse<List<ChargerDto>> response =
                new ApiResponse<>(true, "필터링된 충전기 조회 성공", list, list.size(), 0);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    // 수정
    @PutMapping("")
    public ResponseEntity<String> updateCharger(@RequestBody ChargerDto dto) {
        chargerService.updateFromDto(dto);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    // 삭제 (소프트 딜리트)
    @DeleteMapping("/{chargerId}")
    public ResponseEntity<String> deleteCharger(@PathVariable("chargerId") Long chargerId) {
        chargerService.deleteCharger(chargerId);
        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }
}