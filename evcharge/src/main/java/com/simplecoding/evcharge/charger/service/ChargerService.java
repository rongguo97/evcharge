package com.simplecoding.evcharge.charger.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecoding.evcharge.charger.dto.ChargerDto;
import com.simplecoding.evcharge.charger.entity.Charger;
import com.simplecoding.evcharge.charger.repository.ChargerRepository;
import com.simplecoding.evcharge.common.MapStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChargerService {
    private final ChargerRepository chargerRepository;
    private final MapStruct chargerstruct;
    private final ObjectMapper om = new ObjectMapper();

    /**
     * 공공데이터(JSON 데이터)를 받아 DB에 저장
     */
    @Transactional // 저장 로직이므로 readOnly가 아닌 일반 Transactional 필요
    public void save(String json) throws Exception {
        JsonNode root = om.readTree(json);
        JsonNode nodes = root.get("data");

        for (JsonNode data : nodes) {
            ChargerDto dto = new ChargerDto();
            dto.setSido(data.get("시도").asText());
            dto.setGunggu(data.get("군구").asText());
            dto.setAddress(data.get("주소").asText());
            dto.setStationName(data.get("충전소명").asText());
            dto.setFacilityL(data.get("시설구분(대)").asText());
            dto.setFacilityS(data.get("시설구분(소)").asText());
            dto.setModelL(data.get("기종(대)").asText());
            dto.setModelS(data.get("기종(소)").asText());
            dto.setOperatorL(data.get("운영기관(대)").asText());
            dto.setOperatorS(data.get("운영기관(소)").asText());
            dto.setFastChargeAmount(data.get("급속충전량").asText());
            dto.setChargerType(data.get("충전기타입").asText());
            dto.setUserRestriction(data.get("이용자제한").asText());
            dto.setChargerId(data.get("충전기ID").asInt());

            String generatedStationId = dto.getAddress() + "_" + dto.getStationName();
            dto.setStationId(generatedStationId);

            Charger entity = chargerstruct.toEntity(dto);

            try {
                chargerRepository.save(entity);
            } catch (Exception e) {
                // 중복 데이터 발생 시 무시
            }
        } // 👈 for문 종료
    } // 👈 save 메서드 종료


//1. 전체 목록 조회(키워드 검색+페이징)
    public Page<ChargerDto> selectChargerList(String searchKeyword, Pageable pageable) {
        return chargerRepository.selectChargerList(searchKeyword, pageable);
    }


//     2. 단일 상세 조회 (ID 기준)
    public ChargerDto findById(long id) {
        Charger charger = chargerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 충전기 정보를 찾을 수 없습니다."));
        return chargerstruct.toDto(charger);
    }


//     3. ID로 상세 조회
    public List<ChargerDto> findByStationId(String stationId) {
        return chargerRepository.findByStationIdDto(stationId);
    }


//     4. 시도/군구로 조회
    public List<ChargerDto> findBySidoAndGungguDto(String sido, String gunggu) {
        return chargerRepository.findBySidoAndGungguDto(sido, gunggu);
    }


//     5. 기종별(급속/완속) 조회
    public List<ChargerDto> findByModelLDto(String modelL) {
        return chargerRepository.findByModelLDto(modelL);
    }

    //     6. 기종별(kwh) 조회
    public List<ChargerDto> findByModelSDto(String modelS) {
        return chargerRepository.findByModelSDto(modelS);
    }

//   7.커넥터별 조회
    public List<ChargerDto> findByChargerTypeDto(String chargerType) {
        return chargerRepository.findByChargerTypeDto(chargerType);
    }
}