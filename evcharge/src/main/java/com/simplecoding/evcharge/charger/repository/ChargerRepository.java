package com.simplecoding.evcharge.charger.repository;

import com.simplecoding.evcharge.charger.entity.Charger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargerRepository extends JpaRepository<Charger, Long> {
    //  특정 지역(시도/군구)으로 충전소 찾기
    List<Charger> findBySidoAndGunggu(String sido, String gunggu);

    //  충전소 이름으로 검색 (키워드 포함)
    List<Charger> findByStationNameContaining(String stationName);

    //  특정 STATION_ID에 속한 모든 충전기 상세 조회
    List<Charger> findByStationId(String stationId);
    //  기종으로 충전기 찾기(급속, 완속)
    List<Charger> findByModelL(String modelL);
    //  기종으로 충전기 찾기(kw)
    List<Charger> findByModelS(String modelS);
    //  타입으로 충전기 찾기
    List<Charger> findByChargerType(String chargerType);


}
