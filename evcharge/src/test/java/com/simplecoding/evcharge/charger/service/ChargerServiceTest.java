package com.simplecoding.evcharge.charger.service;

import com.simplecoding.evcharge.charger.dto.ChargerDto;
import com.simplecoding.evcharge.charger.entity.Charger;
import com.simplecoding.evcharge.charger.repository.ChargerRepository;
import com.simplecoding.evcharge.common.MapStruct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class) // Mockito 사용 설정
class ChargerServiceTest {

    @Mock
    private ChargerRepository chargerRepository;

    @Mock
    private MapStruct chargerstruct;

    @InjectMocks
    private ChargerService chargerService;

    @Test
    @DisplayName("공공데이터 저장 테스트")
    void save() throws Exception {
        // given: 테스트용 JSON 데이터
        String json = "{\"data\": [{\"시도\": \"부산\", \"군구\": \"해운대구\", \"주소\": \"우동\", \"충전소명\": \"해운대충전소\", \"충전기ID\": 1, \"충전소ID\": \"ST_01\"}]}";

        // Mock 설정
        given(chargerstruct.toEntity(any(ChargerDto.class))).willReturn(new Charger());

        // when
        chargerService.save(json);

        // then: 저장 메서드가 호출되었는지 검증
        verify(chargerRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("전체 목록 조회 테스트")
    void selectChargerList() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<ChargerDto> page = new PageImpl<>(List.of(new ChargerDto()));
        given(chargerRepository.selectChargerList(anyString(), any())).willReturn(page);

        // when
        Page<ChargerDto> result = chargerService.selectChargerList("테스트", pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("ID로 단일 상세 조회 테스트")
    void findById() {
        // given
        long testId = 1L;
        Charger charger = new Charger();
        ChargerDto dto = new ChargerDto();
        given(chargerRepository.findById(testId)).willReturn(Optional.of(charger));
        given(chargerstruct.toDto(charger)).willReturn(dto);

        // when
        ChargerDto result = chargerService.findById(testId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("충전소 ID로 목록 조회 테스트")
    void findByStationId() {
        // given
        String stationId = "ST_01";
        given(chargerRepository.findByStationIdDto(stationId)).willReturn(List.of(new ChargerDto()));

        // when
        List<ChargerDto> result = chargerService.findByStationId(stationId);

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("지역별 조회 테스트")
    void findBySidoAndGungguDto() {
        // given
        given(chargerRepository.findBySidoAndGungguDto("부산", "해운대구")).willReturn(List.of(new ChargerDto()));

        // when
        List<ChargerDto> result = chargerService.findBySidoAndGungguDto("부산", "해운대구");

        // then
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("기종별 조회 테스트")
    void findByModelLDto() {
        // given
        given(chargerRepository.findByModelLDto("급속")).willReturn(List.of(new ChargerDto()));

        // when
        List<ChargerDto> result = chargerService.findByModelLDto("급속");

        // then
        assertThat(result).isNotEmpty();
    }
//    @Test
//    @DisplayName("충전기 타입별(커넥터) 조회 테스트")
//    void findByChargerTypeDto() {
//        // given
//        String chargerType = "DC콤보";
//        // 가짜 결과 데이터 생성
//        List<ChargerDto> mockList = List.of(new ChargerDto());
//
//        // 레포지토리 호출 시 mockList를 반환하도록 설정
//        given(chargerRepository.findByChargerTypeDto(chargerType)).willReturn(mockList);
//
//        // when
//        List<ChargerDto> result = chargerService.findByChargerTypeDto(chargerType);
//
//        // then
//        assertThat(result).isNotEmpty();
//        assertThat(result.get(0)).isInstanceOf(ChargerDto.class);
//        // 실제로 레포지토리가 해당 파라미터로 호출되었는지 검증
//        verify(chargerRepository).findByChargerTypeDto(chargerType);
//    }
//}
}

