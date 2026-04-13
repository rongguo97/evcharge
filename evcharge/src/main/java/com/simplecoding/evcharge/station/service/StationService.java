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
    /**
     * [변환 로직] 프론트의 한글 명칭을 DB 코드로 변환
     */
    private String convertLabelToCode(String type, String label) {
        if (label == null || label.isEmpty() || "전체".equals(label)) return "";

        switch (type) {
            case "status":
                if ("충전가능".equals(label)) return "1";
                if ("충전중".equals(label)) return "2";
                if ("고장/점검".equals(label)) return "3";
                if ("통신장애".equals(label)) return "4";
                if ("통신미연결".equals(label)) return "5";
                if ("충전종료".equals(label)) return "6";
                if ("계획정지".equals(label)) return "7";
                break;
            case "type":
                if ("완속".equals(label)) return "1";
                if ("급속".equals(label)) return "2";
                break;
            case "method":
                if ("B타입(5핀)".equals(label)) return "01";
                if ("C타입(5핀)".equals(label)) return "02";
                if ("BC타입(5핀)".equals(label)) return "03";
                if ("BC타입(7핀)".equals(label)) return "04";
                if ("DC차데모".equals(label)) return "05";
                if ("AC3상".equals(label)) return "06";
                if ("DC콤보".equals(label)) return "07";
                if ("DC차데모+DC콤보".equals(label)) return "08";
                break;
        }
        return label; // 매칭되는 게 없으면 그대로 반환
    }
    // 1. 키워드 전체 조회
    public Page<StationDto> selectStationList(
            String searchKeyword,
            String status,
            String chargerType,
            String chargerMethod,
            Pageable pageable) {
        // 📍 여기서 통역(변환)을 거칩니다.
        String statusCode = convertLabelToCode("status", status);
        String typeCode = convertLabelToCode("type", chargerType);
        String methodCode = convertLabelToCode("method", chargerMethod);

        // 1. 레포지토리의 확장된 메서드를 호출하여 4가지 조건으로 DB 조회
        Page<Station> page = stationRepository.selectStationList(
                searchKeyword,
                statusCode,   // 📍 status 대신 statusCode
                typeCode,     // 📍 chargerType 대신 typeCode
                methodCode,
                pageable
        );

        // 2. 조회된 Entity 결과를 DTO로 변환하여 반환
        // (struct::toDto는 기존에 사용하시던 MapStruct 또는 변환 로직을 그대로 유지합니다)
        return page.map(struct::toDto);
    }

    // 2. 단일상세조회
    public StationDto findById(long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("해당 충전소 정보를 찾을 수 없습니다."));
        return struct.toDto(station);
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

