package com.simplecoding.evcharge.charger.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecoding.evcharge.charger.dto.ChargerDto;
import com.simplecoding.evcharge.charger.entity.Charger;
import com.simplecoding.evcharge.charger.repository.ChargerRepository;
import com.simplecoding.evcharge.common.MapStruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 공공데이터 → DB 저장 서비스
 */
@Service
@RequiredArgsConstructor
public class ChargerService {
    private final ChargerRepository repository;                    // 레포지토리 DI
    private final MapStruct struct;                           // 복사 플러그인 DI
    private final ObjectMapper om = new ObjectMapper();       // 공공데이터(JSON(js)) 해석하는 플로그인

    /**
     * 공공데이터(JSON 데이터)를 받아 DB에 저장
     */
    public void save(String json) throws Exception {
        JsonNode root = om.readTree(json);                    // 공공데이터(json: 글자) -> 객체형태로 변경
        JsonNode nodes = root.get("data");                    // 공공데이터의 결과를 받기(data 필드(속성, 배열)에 있음)

        for (JsonNode data : nodes) {

            ChargerDto dto = new ChargerDto();
            dto.setSido(data.get("시도").asText());                     // 시도
            dto.setGunggu(data.get("군구").asText());                    // 군구
            dto.setAddress(data.get("주소").asText());                  // 주소
            dto.setStationName(data.get("충전소명").asText());           // 충전소명

            dto.setFacilityL(data.get("시설구분(대)").asText());        // 시설구분(대)
            dto.setFacilityS(data.get("시설구분(소)").asText());        // 시설구분(소)

            dto.setModelL(data.get("기종(대)").asText());               // 기종(대)
            dto.setModelS(data.get("기종(소)").asText());               // 기종(소)

            dto.setOperatorL(data.get("운영기관(대)").asText());        // 운영기관(대)
            dto.setOperatorS(data.get("운영기관(소)").asText());        // 운영기관(소)

            dto.setFastChargeAmount(data.get("급속충전량").asText());    // 급속충전량
            dto.setChargerType(data.get("충전기타입").asText());        // 충전기타입
            dto.setUserRestriction(data.get("이용자제한").asText());    // 이용자제한

            dto.setChargerId(data.get("충전기ID").asLong());             // 충전기ID (int 형변환)

// 💡 STATION_ID 생성 (주소와 이름을 합쳐서 고유값으로 활용)
            String generatedStationId = dto.getAddress() + "_" + dto.getStationName();
            dto.setStationId(generatedStationId);
            Charger entity = struct.toEntity(dto);                 // 위의 dto -> entity 로 복사

//          중복되면 무결성 에러(Unique 제약) -> 에러난것은 무시하고 계속 처리
            try {
                repository.save(entity);                      // db 저장

            } catch (Exception e) {
//                에러나면 무시하고 계속 진행
            }
        }
    }
}