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

    // 특정 충전기의 예약 시간이 겹치는지 확인하는 쿼리
    @Query("SELECT r FROM Reservation r WHERE r.charger.id = :chargerId " +
            "AND r.status = 'RESERVED' " +
            "AND ((r.startTime < :end AND r.endTime > :start))")
    List<Reservation> findOverlapping(@Param("chargerId") Long chargerId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    List<Reservation> findByMemberEmailOrderByStartTimeDesc(String email);
}