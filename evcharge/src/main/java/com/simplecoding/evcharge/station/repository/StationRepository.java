package com.simplecoding.evcharge.station.repository;

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

    // 1. 키워드 전체 조회 (기존 유지)
    @Query(value = "SELECT s FROM Station s " +
            "WHERE s.stationName LIKE %:searchKeyword% " +
            "OR s.address LIKE %:searchKeyword%")
    Page<Station> selectStationList(@Param("searchKeyword") String searchKeyword,
                                    Pageable pageable);

    // 2. 현재 상태별 조회 (필터만 적용)
    @Query(value = "SELECT s FROM Station s WHERE s.status = :status")
    Page<Station> selectStationListByStatus(@Param("status") String status, Pageable pageable);

    // 3. 충전기 타입별 조회 (필터만 적용)
    @Query(value = "SELECT s FROM Station s WHERE s.chargerType = :chargerType")
    Page<Station> selectStationListByType(@Param("chargerType") String chargerType, Pageable pageable);

    // 4. 충전 방식별 조회 (필터만 적용)
    @Query(value = "SELECT s FROM Station s WHERE s.chargerMethod = :chargerMethod")
    Page<Station> selectStationListByMethod(@Param("chargerMethod") String chargerMethod, Pageable pageable);

//    내 위치에서 주변 조회
    // StationRepository.java

    @Query(value = "SELECT s.*, " +
            "(6371 * acos(cos(:userLat * 3.141592653589793 / 180) * cos(s.LAT * 3.141592653589793 / 180) " +
            "* cos((s.LNG * 3.141592653589793 / 180) - (:userLng * 3.141592653589793 / 180)) " +
            "+ sin(:userLat * 3.141592653589793 / 180) * sin(s.LAT * 3.141592653589793 / 180))) AS distance " +
            "FROM TB_STATION s " +
            "WHERE (6371 * acos(cos(:userLat * 3.141592653589793 / 180) * cos(s.LAT * 3.141592653589793 / 180) " +
            "* cos((s.LNG * 3.141592653589793 / 180) - (:userLng * 3.141592653589793 / 180)) " +
            "+ sin(:userLat * 3.141592653589793 / 180) * sin(s.LAT * 3.141592653589793 / 180))) <= :radius " +
            "ORDER BY distance ASC",
            nativeQuery = true)
    List<Object[]> selectStationListByLocation(@Param("userLat") Double userLat,
                                               @Param("userLng") Double userLng,
                                               @Param("radius") Double radius);
}