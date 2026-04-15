package com.simplecoding.evcharge.station.repository;

import com.simplecoding.evcharge.station.dto.StationDto;
import com.simplecoding.evcharge.station.entity.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    // JPQL을 사용하여 조회 시점에 바로 DTO로 변환
    @Query("""
        SELECT new com.simplecoding.evcharge.station.dto.StationDto(
            s.stationId, s.stationName, s.address, s.lat, s.lng, s.isDeleted
        ) 
        FROM Station s
        WHERE s.stationName LIKE %:searchKeyword%
        AND s.isDeleted = 'N'
    """)
    Page<StationDto> selectStationList(@Param("searchKeyword") String searchKeyword,
                                       Pageable pageable);

    @Query("SELECT DISTINCT s FROM Station s JOIN s.chargers c " +
            "WHERE c.cType = :cType AND s.isDeleted = 'N'")
    List<Station> findByChargerType(@Param("cType") String cType);
}