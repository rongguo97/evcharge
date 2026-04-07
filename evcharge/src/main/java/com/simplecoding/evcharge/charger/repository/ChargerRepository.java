package com.simplecoding.evcharge.charger.repository;

import com.simplecoding.evcharge.charger.dto.ChargerDto;
import com.simplecoding.evcharge.charger.entity.Charger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

    @Repository
    public interface ChargerRepository extends JpaRepository<Charger, Long> {

        // 1. 전체 목록 조회 (충전소명 검색 + 페이징)
        @Query("""
        SELECT new com.simplecoding.evcharge.charger.dto.ChargerDto(
            c.id, c.stationId, c.chargerId, c.sido, c.gunggu, c.address, c.stationName,
            c.facilityL, c.facilityS, c.modelL, c.modelS, c.operatorL, c.operatorS,
            c.fastChargeAmount, c.chargerType, c.userRestriction)
         FROM Charger c
        WHERE c.stationName LIKE %:searchKeyword%
    """)
        Page<ChargerDto> selectChargerList(@Param("searchKeyword") String searchKeyword, Pageable pageable);

        // 2. 특정 STATION_ID에 속한 모든 충전기 상세 조회
        @Query("""
        SELECT new com.simplecoding.evcharge.charger.dto.ChargerDto(
            c.id, c.stationId, c.chargerId, c.sido, c.gunggu, c.address, c.stationName,
            c.facilityL, c.facilityS, c.modelL, c.modelS, c.operatorL, c.operatorS,
            c.fastChargeAmount, c.chargerType, c.userRestriction
        ) FROM Charger c
        WHERE c.stationId = :stationId
    """)
        List<ChargerDto> findByStationIdDto(@Param("stationId") String stationId);

        // 3. 지역별(시도/군구) 조회
        @Query("""
        SELECT new com.simplecoding.evcharge.charger.dto.ChargerDto(
            c.id, c.stationId, c.chargerId, c.sido, c.gunggu, c.address, c.stationName,
            c.facilityL, c.facilityS, c.modelL, c.modelS, c.operatorL, c.operatorS,
            c.fastChargeAmount, c.chargerType, c.userRestriction
        ) FROM Charger c
        WHERE c.sido = :sido 
          AND c.gunggu = :gunggu
    """)
        List<ChargerDto> findBySidoAndGungguDto(@Param("sido") String sido, @Param("gunggu") String gunggu);

        // 4. 기종으로 충전기 찾기 (급속/완속)
        @Query("""
        SELECT new com.simplecoding.evcharge.charger.dto.ChargerDto(
            c.id, c.stationId, c.chargerId, c.sido, c.gunggu, c.address, c.stationName,
            c.facilityL, c.facilityS, c.modelL, c.modelS, c.operatorL, c.operatorS,
            c.fastChargeAmount, c.chargerType, c.userRestriction
        ) FROM Charger c
        WHERE c.modelL = :modelL
    """)
        List<ChargerDto> findByModelLDto(@Param("modelL") String modelL);

        // 5. 용량으로 충전기 찾기 (kW)
        @Query("""
        SELECT new com.simplecoding.evcharge.charger.dto.ChargerDto(
            c.id, c.stationId, c.chargerId, c.sido, c.gunggu, c.address, c.stationName,
            c.facilityL, c.facilityS, c.modelL, c.modelS, c.operatorL, c.operatorS,
            c.fastChargeAmount, c.chargerType, c.userRestriction
        ) FROM Charger c
        WHERE c.modelS = :modelS
    """)
        List<ChargerDto> findByModelSDto(@Param("modelS") String modelS);

        // 6. 타입으로 충전기 찾기 (커넥터 타입)
        @Query("""
        SELECT new com.simplecoding.evcharge.charger.dto.ChargerDto(
            c.id, c.stationId, c.chargerId, c.sido, c.gunggu, c.address, c.stationName,
            c.facilityL, c.facilityS, c.modelL, c.modelS, c.operatorL, c.operatorS,
            c.fastChargeAmount, c.chargerType, c.userRestriction
        ) FROM Charger c
        WHERE c.chargerType = :chargerType
    """)
        List<ChargerDto> findByChargerTypeDto(@Param("chargerType") String chargerType);
    }

