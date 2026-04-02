package com.simplecoding.evcharge.station.service;

import com.simplecoding.evcharge.station.dto.StationDto;
import com.simplecoding.evcharge.station.entity.Station;
import com.simplecoding.evcharge.station.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StationServiceTest {

    @Autowired
    private StationService stationService;
    @Autowired
    private  StationRepository stationRepository;



    @Test
    void selectStationList() {
        // 1. 페이징 설정 (0번째 페이지, 10개씩)
        Pageable pageable = PageRequest.of(0, 10);

        // 2. 검색어 없이 전체 조회 테스트
        Page<StationDto> allStations = stationService.selectStationList("", pageable);
        System.out.println("전체 충전소 개수: " + allStations.getTotalElements());

        // 3. 검색어 포함 조회 테스트 (예: '강남'이 포함된 충전소)
        Page<StationDto> searchStations = stationService.selectStationList("강남", pageable);
        searchStations.forEach(s -> System.out.println("검색된 충전소: " + s.getStationName()));
    }

    @Test
    void selectStationDetail() {
        // 1. 상세 조회 테스트 (실제 DB에 있는 ID 하나를 넣으세요. 예: 1L)
        Long targetId = 1L;

        try {
            StationDto station = stationService.selectStationDetail(targetId);
            System.out.println("상세 정보 - 이름: " + station.getStationName());
            System.out.println("상세 정보 - 주소: " + station.getAddress());
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
        }
    }

    @Test
    @Transactional
    @Rollback(true) // 테스트 후 데이터 복구
    void updateFromDto() {
        // 1. 기존 데이터 준비 (DB에 1번 데이터가 있다고 가정)
        Long targetId = 1L;
        StationDto updateDto = new StationDto();
        updateDto.setStationId(targetId);
        updateDto.setStationName("강남역 저속 충전소");
        updateDto.setAddress("서울시 강남구 역삼동 123-46");

        // 2. 수정 서비스 호출
        stationService.updateFromDto(updateDto);

        // 3. 검증: 다시 조회해서 값이 바뀌었는지 확인
        StationDto result = stationService.selectStationDetail(targetId);
        System.out.println("강남역 저속 충전소: " + result.getStationName());

        // Assertions를 사용한다면:
        // assertEquals("수정된 충전소 이름", result.getStationName());
    }

    @Test
    @Transactional
    @Rollback(true)
    void deleteStation() {
        // 1. 삭제할 ID 준비
        Long targetId = 1L;

        // 2. 삭제 서비스 호출 (isDeleted -> 'Y')
        stationService.deleteStation(targetId);

        // 3. 검증: 상세 조회를 하면 isDeleted가 'Y'여야 함
        // (주의: selectStationList에서는 'N'만 조회되므로 결과가 안 나올 것입니다)
        Station station = stationRepository.findById(targetId).get();
        System.out.println("삭제 여부 상태(Y여야 함): " + station.getIsDeleted());

        // Assertions를 사용한다면:
        // assertEquals("Y", station.getIsDeleted());
    }
}