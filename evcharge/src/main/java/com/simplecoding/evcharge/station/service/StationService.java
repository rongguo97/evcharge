package com.simplecoding.evcharge.station.service;

import com.simplecoding.evcharge.common.MapStruct;
import com.simplecoding.evcharge.station.dto.StationDto;
import com.simplecoding.evcharge.station.entity.Station;
import com.simplecoding.evcharge.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;
    private final MapStruct mapStruct;

    /**
     * 1. 충전소 목록 조회 (검색 및 페이징)
     * 레포지토리에서 이미 Page<StationDto>로 반환하므로 추가 변환이 필요 없음
     */
    @Transactional(readOnly = true)
    public Page<StationDto> selectStationList(String searchKeyword, Pageable pageable) {

        // 검색어가 비어있는 경우 빈 문자열("")을 넣어 LIKE %% 조회가 되도록 처리하거나,
        // 별도의 전체 조회 메서드를 호출합니다.
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            // 레포지토리에 이 이름으로 @Query를 만드셨다면 호출 가능
            return stationRepository.selectStationList("", pageable);
        }

        // 키워드가 있으면 해당 키워드로 DTO 목록 조회
        return stationRepository.selectStationList(searchKeyword, pageable);
    }

    /**
     * 2. 특정 충전소 상세 조회
     * 상세 조회는 ID로 한 건만 가져오는 기본 기능을 쓰고, MapStruct로 변환하는 것이 효율적입니다.
     */
    @Transactional(readOnly = true)
    public StationDto selectStationDetail(Long stationId) {
        // 상세 조회는 JPA 기본 findById를 사용 (엔티티 반환)
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 충전소를 찾을 수 없습니다: " + stationId));

        // 상세 데이터는 MapStruct 스타일로 변환해서 리턴
        return mapStruct.toDto(station);
    }
}