package com.simplecoding.evcharge.charger.repository;

import com.simplecoding.evcharge.charger.dto.ChargerDto;
import com.simplecoding.evcharge.charger.entity.Charger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargerRepository extends JpaRepository<Charger, Long> {

    // StationRepository와 형식을 맞춰서 작성!
    @Query("""
        SELECT new com.simplecoding.evcharge.charger.dto.ChargerDto(
            c.chargerId, 
            c.station.stationId, 
            c.cType, 
            c.cStatus
        ) 
        FROM Charger c
        WHERE c.station.stationId = :stationId
        AND c.isDeleted = 'N'
    """)
    List<ChargerDto> selectChargerList(@Param("stationId") Long stationId);

}