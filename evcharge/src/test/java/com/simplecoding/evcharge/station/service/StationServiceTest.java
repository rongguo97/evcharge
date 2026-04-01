package com.simplecoding.evcharge.station.service;

import com.simplecoding.evcharge.station.dto.StationDto;
import com.simplecoding.evcharge.station.entity.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StationServiceTest {

    @Autowired
    private StationService stationService;

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
            Station station = stationService.selectStationDetail(targetId);
            System.out.println("상세 정보 - 이름: " + station.getStationName());
            System.out.println("상세 정보 - 주소: " + station.getAddress());
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
        }
    }
}