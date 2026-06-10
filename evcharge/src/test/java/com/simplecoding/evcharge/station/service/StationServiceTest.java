//package com.simplecoding.evcharge.station.service;
//
//import com.simplecoding.evcharge.station.dto.StationDto;
//import com.simplecoding.evcharge.station.entity.Station;
//import com.simplecoding.evcharge.station.repository.StationRepository;
//import com.simplecoding.evcharge.common.MapStruct;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.*;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.times;
//
//@ExtendWith(MockitoExtension.class) // Mockito 사용 설정
//class StationServiceTest {
//
//    @Mock
//    private StationRepository stationRepository;
//
//    @Mock
//    private MapStruct chargerstruct;
//
//    @InjectMocks
//    private StationService stationService;
//
//    @Test
//    @DisplayName("공공데이터 저장 테스트")
//    void save() throws Exception {
//        // given: 테스트용 JSON 데이터
//        String json = "{\"data\": [{\"시도\": \"부산\", \"군구\": \"해운대구\", \"주소\": \"우동\", \"충전소명\": \"해운대충전소\", \"충전기ID\": 1, \"충전소ID\": \"ST_01\"}]}";
//
//        // Mock 설정
//        given(chargerstruct.toEntity(any(StationDto.class))).willReturn(new Station());
//
//        // when
//        stationService.save(json);
//
//        // then: 저장 메서드가 호출되었는지 검증
//        verify(stationRepository, times(1)).save(any());
//    }
//
//    @Test
//    @DisplayName("전체 목록 조회 테스트")
//    void selectChargerList() {
//        // given
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<StationDto> page = new PageImpl<>(List.of(new StationDto()));
//        given(stationRepository.selectChargerList(anyString(), any())).willReturn(page);
//
//        // when
//        Page<StationDto> result = stationService.selectChargerList("테스트", pageable);
//
//        // then
//        assertThat(result.getContent()).hasSize(1);
//    }
//
//    @Test
//    @DisplayName("ID로 단일 상세 조회 테스트")
//    void findById() {
//        // given
//        long testId = 1L;
//        Station station = new Station();
//        StationDto dto = new StationDto();
//        given(stationRepository.findById(testId)).willReturn(Optional.of(station));
//        given(chargerstruct.toDto(station)).willReturn(dto);
//
//        // when
//        StationDto result = stationService.findById(testId);
//
//        // then
//        assertThat(result).isNotNull();
//    }
//
//
//
//    @Test
//    @DisplayName("지역별 조회 테스트")
//    void findBySidoAndGungguDto() {
//        // given
//        given(stationRepository.findBySidoAndGungguDto("부산", "해운대구")).willReturn(List.of(new StationDto()));
//
//        // when
//        List<StationDto> result = stationService.findBySidoAndGungguDto("부산", "해운대구");
//
//        // then
//        assertThat(result).isNotEmpty();
//    }
//
//    @Test
//    @DisplayName("기종별 조회 테스트")
//    void findByModelLDto() {
//        // given
//        given(stationRepository.findByModelLDto("급속")).willReturn(List.of(new StationDto()));
//
//        // when
//        List<StationDto> result = stationService.findByModelLDto("급속");
//
//        // then
//        assertThat(result).isNotEmpty();
//    }
////    @Test
////    @DisplayName("충전기 타입별(커넥터) 조회 테스트")
////    void findByChargerTypeDto() {
////        // given
////        String chargerType = "DC콤보";
////        // 가짜 결과 데이터 생성
////        List<ChargerDto> mockList = List.of(new ChargerDto());
////
////        // 레포지토리 호출 시 mockList를 반환하도록 설정
////        given(chargerRepository.findByChargerTypeDto(chargerType)).willReturn(mockList);
////
////        // when
////        List<ChargerDto> result = chargerService.findByChargerTypeDto(chargerType);
////
////        // then
////        assertThat(result).isNotEmpty();
////        assertThat(result.get(0)).isInstanceOf(ChargerDto.class);
////        // 실제로 레포지토리가 해당 파라미터로 호출되었는지 검증
////        verify(chargerRepository).findByChargerTypeDto(chargerType);
////    }
////}
//}
//
