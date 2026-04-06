package com.simplecoding.evcharge.charger.service;

import com.simplecoding.evcharge.charger.dto.ChargerDto;
import com.simplecoding.evcharge.charger.entity.Charger;
import com.simplecoding.evcharge.charger.repository.ChargerRepository;
import com.simplecoding.evcharge.common.CommonUtil;
import com.simplecoding.evcharge.common.MapStruct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 사용 설정
class ChargerServiceTest {

    @Mock
    private ChargerRepository chargerRepository;

    @Mock
    private MapStruct mapStruct;

    @Mock
    private CommonUtil util;

    @InjectMocks
    private ChargerService chargerService; // Mock들을 주입받은 실제 서비스 객체

    @Test
    @DisplayName("1. 특정 충전소의 충전기 목록 조회 테스트")
    void selectChargerList() {
        // given (가짜 데이터 설정)
        Long stationId = 1L;
        List<ChargerDto> mockList = List.of(new ChargerDto(1L, stationId, "FAST", "AVAILABLE"));
        given(chargerRepository.selectChargerList(stationId)).willReturn(mockList);

        // when (실행)
        List<ChargerDto> result = chargerService.selectChargerList(stationId);

        // then (검증)
        assertEquals(1, result.size());
        assertEquals("FAST", result.get(0).getCType());
        verify(chargerRepository, times(1)).selectChargerList(stationId);
    }

    @Test
    @DisplayName("2. 충전기 상세 조회 테스트")
    void selectChargerDetail() {
        // given
        Long chargerId = 1L;
        Charger charger = new Charger();
        ChargerDto dto = new ChargerDto();
        dto.setChargerId(chargerId);

        given(chargerRepository.findById(chargerId)).willReturn(Optional.of(charger));
        given(mapStruct.toDto(charger)).willReturn(dto);

        // when
        ChargerDto result = chargerService.selectChargerDetail(chargerId);

        // then
        assertNotNull(result);
        assertEquals(chargerId, result.getChargerId());
    }

    @Test
    @DisplayName("3. 정보 수정(더티 체킹) 테스트")
    void updateFromDto() {
        // given
        ChargerDto dto = new ChargerDto();
        dto.setChargerId(1L);
        Charger charger = new Charger();

        given(chargerRepository.findById(anyLong())).willReturn(Optional.of(charger));

        // when
        chargerService.updateFromDto(dto);

        // then
        // mapStruct의 updateFromDto가 실행되었는지 확인
        verify(mapStruct, times(1)).updateFromDto(dto, charger);
    }

    @Test
    @DisplayName("4. 소프트 딜리트 테스트")
    void deleteCharger() {
        // given
        Long chargerId = 1L;
        Charger charger = new Charger();
        charger.setIsDeleted("N");

        given(chargerRepository.findById(chargerId)).willReturn(Optional.of(charger));

        // when
        chargerService.deleteCharger(chargerId);

        // then
        assertEquals("Y", charger.getIsDeleted()); // 상태값이 Y로 바뀌었는지 확인
    }
}