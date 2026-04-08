package com.simplecoding.evcharge.station.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecoding.evcharge.common.MapStruct;
import com.simplecoding.evcharge.station.dto.StationDto;
import com.simplecoding.evcharge.station.entity.Station;
import com.simplecoding.evcharge.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationService {
    private final StationRepository stationRepository;
    private final MapStruct struct;
    private final ObjectMapper om = new ObjectMapper();

    /**
     * 한전 공공데이터(JSON)를 받아 TB_STATION 테이블에 저장
     */
    @Transactional
    public void save(String json) throws Exception {
        JsonNode root = om.readTree(json);
        JsonNode nodes = root.get("data");

        if (nodes == null || !nodes.isArray()) {
            log.error("API 응답에 data 노드가 없거나 배열이 아닙니다.");
            return;
        }

        // [중요] 스케줄러가 돌 때마다 데이터가 중복되지 않도록 기존 데이터 삭제
        stationRepository.deleteAll();
        log.info("기존 충전소 데이터를 삭제했습니다.");

        for (JsonNode data : nodes) {
            try {
                StationDto dto = new StationDto();

                // 1. 기본 정보 매핑
                dto.setAddress(data.path("addr").asText());
                dto.setStationName(data.path("csNm").asText());
                dto.setChargerName(data.path("cpNm").asText());
                dto.setChargerId(data.path("cpId").asLong());
                dto.setChargerType(data.path("chargeTp").asText());
                dto.setStatus(data.path("cpStat").asText());
                dto.setChargerMethod(data.path("cpTp").asText());

                // 2. 위도/경도 매핑
                dto.setLat(data.path("lat").asDouble());
                dto.setLng(data.path("longi").asDouble());

                // 3. 날짜 데이터 예외 처리 (Text '' could not be parsed 에러 방지)
                String rawDate = data.path("statUpdateDatetime").asText();

                // 날짜가 비어있거나(""), 문자열 "null"인 경우 처리
                if (rawDate == null || rawDate.trim().isEmpty() || rawDate.equals("null")) {
                    // 날짜가 없으면 MapStruct가 에러를 내지 않도록 임의의 과거 날짜나 null 세팅
                    // 여기서는 안전하게 null로 두거나, 형식에 맞는 기본값을 넣습니다.
                    dto.setStatUpdateDatetime(null);
                } else {
                    dto.setStatUpdateDatetime(rawDate);
                }

                // 4. MapStruct 변환 및 저장
                // dto.getStatUpdateDatetime()이 null이면 MapStruct의 dateFormat 변환 시
                // 에러가 날 수 있으므로, try-catch로 한 번 더 감싸서 안전하게 처리합니다.
                Station entity = struct.toEntity(dto);
                stationRepository.save(entity);

            } catch (Exception e) {
                // 특정 데이터 한 건이 에러가 나도 전체 루프가 멈추지 않도록 로그만 남김
                log.warn("데이터 개별 처리 중 오류 발생 (무시하고 다음 데이터 진행): {}", e.getMessage());
            }
        }
    }

    // 1. 키워드 전체 조회
    public Page<StationDto> selectStationList(String searchKeyword, Pageable pageable) {
        Page<Station> page = stationRepository.selectStationList(searchKeyword, pageable);
        return page.map(struct::toDto);
    }

    // 2. 단일상세조회
    public StationDto findById(long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("해당 충전소 정보를 찾을 수 없습니다."));
        return struct.toDto(station);
    }

    // 3. 현재상태별 조회
    public Page<StationDto> selectStationListByStatus(String status, Pageable pageable) {
        Page<Station> page = stationRepository.selectStationListByStatus(status, pageable);
        return page.map(struct::toDto);
    }

    // 4. 충전 타입별 조회
    public Page<StationDto> selectStationListByType(String chargerType, Pageable pageable) {
        Page<Station> page = stationRepository.selectStationListByType(chargerType, pageable);
        return page.map(struct::toDto);
    }

    // 5. 충전 방식별 조회
    public Page<StationDto> selectStationListByMethod(String chargerType, Pageable pageable) {
        Page<Station> page = stationRepository.selectStationListByMethod(chargerType, pageable);
        return page.map(struct::toDto);
    }

    //  6. 내 위치에서 주변 조회
    public List<StationDto> selectStationListByLocation(Double userLat, Double userLng, Double radius) {
        Double searchRadius = (radius == null) ? 5.0 : radius;
        List<Object[]> result = stationRepository.selectStationListByLocation(userLat, userLng, searchRadius);
        return result.stream()
                .map(objects -> {
                    // Oracle Native Query 결과: 첫 번째 요소는 엔티티, 두 번째는 거리(BigDecimal일 수 있음)
                    Station entity = (Station) objects[0];

                    // Oracle은 숫자를 BigDecimal로 던지는 경우가 많으므로 안전하게 변환
                    Number distNum = (Number) objects[1];
                    double distance = distNum.doubleValue();

                    StationDto dto = struct.toDto(entity);
                    dto.setDistance(Math.round(distance * 100) / 100.0);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}

