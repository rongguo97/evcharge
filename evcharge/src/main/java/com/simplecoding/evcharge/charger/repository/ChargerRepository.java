package com.simplecoding.evcharge.charger.repository;

import com.simplecoding.evcharge.charger.dto.ChargerDto;
import com.simplecoding.evcharge.charger.entity.Charger;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargerRepository extends JpaRepository<Charger, Long> {
//    충전기 상세조회
@Query("""
    SELECT new com.simplecoding.evcharge.charger.dto.ChargerDto(
        c.chargerId, 
        c.station.stationId, 
        c.cType, 
        c.cStatus,
        c.cConnector
    )
    FROM Charger c
    WHERE c.station.stationId = :stationId
    AND c.isDeleted = 'N'
""")
    List<ChargerDto> selectChargerList(@Param("stationId") Long stationId);
// 충전기 타입별 조회
    @Query("SELECT c FROM Charger c " +
            "WHERE c.cType = :cType " +
            "AND c.cStatus = :cStatus")
    List<Charger> findByCTypeAndCStatus(@Param("cType") String cType,
                                        @Param("cStatus") String cStatus);
//    충전기 커넥터별 조회
    @Query(value = "SELECT * FROM TB_CHARGER " +
            "WHERE C_CONNECTOR = :cConnector " +
            "AND C_STATUS = :cStatus",
            nativeQuery = true)
    List<Charger> findByCConnectorAndCStatus(@Param("cConnector") String cConnector,
                                             @Param("cStatus") String cStatus);

//    통합 필터
// 💡 가장 강력한 한 방: 타입 + 커넥터 + 상태를 모두 만족하는 충전기 찾기
@Query("SELECT c FROM Charger c " +
        "WHERE c.cType = :cType " +
        "AND c.cConnector = :cConnector " +
        "AND c.cStatus = :cStatus")
List<Charger> findFilteredChargers(@Param("cType") String cType,
                                   @Param("cConnector") String cConnector,
                                   @Param("cStatus") String cStatus);
}