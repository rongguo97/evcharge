package com.simplecoding.evcharge.charger.controller;

import com.simplecoding.evcharge.charger.dto.ChargerDto;
import com.simplecoding.evcharge.charger.service.ChargerService;
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

    // 목록 조회 (특정 충전소 기준)
    @GetMapping("charger")
    public ResponseEntity<List<ChargerDto>> selectChargerList(@RequestParam("stationId") Long stationId) {
        List<ChargerDto> list = chargerService.selectChargerList(stationId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 상세 조회
    @GetMapping("/{chargerId}")
    public ResponseEntity<ChargerDto> selectChargerDetail(@PathVariable("chargerId") Long chargerId) {
        ChargerDto dto = chargerService.selectChargerDetail(chargerId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
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