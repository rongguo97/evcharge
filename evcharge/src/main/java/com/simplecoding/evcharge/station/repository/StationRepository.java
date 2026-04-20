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

    @Query(value = "SELECT s FROM Station s " +
            "WHERE (s.stationName LIKE %:searchKeyword% OR s.address LIKE %:searchKeyword%) " +
            "AND (:status IS NULL OR :status = '' OR s.status = :status) " +
            "AND (:chargerType IS NULL OR :chargerType = '' OR s.chargerType = :chargerType) " +
            "AND (:chargerMethod IS NULL OR :chargerMethod = '' OR s.chargerMethod = :chargerMethod)")
    Page<Station> selectStationList(
            @Param("searchKeyword") String searchKeyword,
            @Param("status") String status,
            @Param("chargerType") String chargerType,
            @Param("chargerMethod") String chargerMethod,
            Pageable pageable);


//    내 위치에서 주변 조회
    // StationRepository.java

    @Query(value = "SELECT " +
            "s.STATION_ID as stationId, " +
            "s.STATION_NAME as stationName, " +
            "s.ADDRESS as address, " +
            "s.LAT as lat, " +
            "s.LNG as lng, " +
            "s.STATUS as status, " +
            "s.CHARGER_TYPE as chargerType, " +
            "s.CHARGER_METHOD as chargerMethod, " +
            "(6371 * acos(cos(:userLat * 3.141592653589793 / 180) * cos(s.LAT * 3.141592653589793 / 180) " +
            "* cos((s.LNG * 3.141592653589793 / 180) - (:userLng * 3.141592653589793 / 180)) " +
            "+ sin(:userLat * 3.141592653589793 / 180) * sin(s.LAT * 3.141592653589793 / 180))) AS distance " +
            "FROM TB_STATION s " +
            "WHERE (6371 * acos(cos(:userLat * 3.141592653589793 / 180) * cos(s.LAT * 3.141592653589793 / 180) " +
            "* cos((s.LNG * 3.141592653589793 / 180) - (:userLng * 3.141592653589793 / 180)) " +
            "+ sin(:userLat * 3.141592653589793 / 180) * sin(s.LAT * 3.141592653589793 / 180))) <= :radius " +
            "ORDER BY distance ASC",
            nativeQuery = true)
    List<StationDto> selectStationListByLocation(
            @Param("userLat") Double userLat,
            @Param("userLng") Double userLng,
            @Param("radius") Double radius);
}