package com.simplecoding.evcharge.reservation.repository;

import com.simplecoding.evcharge.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * 특정 충전소의 예약 중복 확인
     * status가 'RESERVED'인 것뿐만 아니라, 실제 충전 중인 'CHARGING' 상태도
     * 중복으로 간주해야 안전합니다. (ERD Ver2의 STATUS 확장 고려)
     */
    @Query("SELECT r FROM Reservation r WHERE r.station.stationId = :stationId " +
            "AND r.status IN ('RESERVED', 'CHARGING') " + // 상태값 확장
            "AND r.startTime < :end " +
            "AND r.endTime > :start " +
            "AND r.rDate = :rDate")
    List<Reservation> findOverlapping(@Param("stationId") Long stationId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end, String rDate);

    /**
     * 회원의 예약 목록 조회
     * findByEmailOrderByStartTimeDesc 도 좋지만,
     * 최신순으로 정렬하기 위해 Desc(내림차순)가 정확히 작동하는지 확인합니다.
     */
    List<Reservation> findByEmailOrderByStartTimeDesc(String email);

    // 추가 내용: 특정 충전소의 오늘 예약 현황만 보기
    List<Reservation> findByStationStationIdAndStartTimeAfter(Long stationId, LocalDateTime now);

    //   날짜 및 시간 별 예약 확인
    @Query("SELECT r FROM Reservation r WHERE r.station.stationId = :chargerId " +
            "AND r.rDate = :rDate " +
            "AND r.status = 'RESERVED'")
    List<Reservation> findReservedSlotsByDate(@Param("chargerId") Long chargerId,
                                              @Param("rDate") String rDate);
}