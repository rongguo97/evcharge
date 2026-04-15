package com.simplecoding.evcharge.charger.service;

import com.simplecoding.evcharge.charger.dto.ChargerDto;
import com.simplecoding.evcharge.charger.entity.Charger;
import com.simplecoding.evcharge.charger.repository.ChargerRepository;
import com.simplecoding.evcharge.common.CommonUtil;
import com.simplecoding.evcharge.common.MapStruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChargerService {

    private final ChargerRepository chargerRepository;
    private final MapStruct mapStruct;
    private final CommonUtil util; // StationService와 동일하게 util 주입

//     1. 특정 충전소의 충전기 목록 조회
    @Transactional(readOnly = true)
    public List<ChargerDto> selectChargerList(Long stationId) {
        // 특정 충전소 ID에 해당하는 삭제되지 않은 충전기 목록 조회
        return chargerRepository.selectChargerList(stationId);
    }
//     2. 특정 충전기 상세 조회
    @Transactional(readOnly = true)
    public ChargerDto selectChargerDetail(Long chargerId) {
        Charger charger = chargerRepository.findById(chargerId)
                .orElseThrow(() -> new RuntimeException(util.getMessage("errors.not.found")));

        return mapStruct.toDto(charger);
    }

//      특정 타입과 상태를 기준으로 충전기 목록 조회 (예: FAST & AVAILABLE)
    @Transactional(readOnly = true)
    public List<ChargerDto> selectAvailableChargers(String cType, String cStatus) {
        List<Charger> chargers = chargerRepository.findAvailableChargers(cType, cStatus);

        return chargers.stream()
                .map(mapStruct::toDto)
                .collect(Collectors.toList());
    }

//      3. 충전기 정보 수정 (상태 변경 등)
//      StationService의 updateFromDto 방식과 동일하게 더티 체킹 활용
    @Transactional
    public void updateFromDto(ChargerDto dto) {
        Charger charger = chargerRepository.findById(dto.getChargerId())
                .orElseThrow(() -> new RuntimeException(util.getMessage("errors.not.found")));
        mapStruct.updateFromDto(dto, charger);
    }
//      4. 충전기 삭제 (소프트 딜리트)
//      StationService의 deleteStation 방식과 동일하게 구현
    @Transactional
    public void deleteCharger(long chargerId) {
        // 1. 존재 여부 확인
        Charger charger = chargerRepository.findById(chargerId)
                .orElseThrow(() -> new RuntimeException(util.getMessage("errors.not.found")));
        charger.setIsDeleted("Y");
    }
}