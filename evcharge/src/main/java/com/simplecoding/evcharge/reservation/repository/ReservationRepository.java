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

    // charger.id -> station.stationId로 변경
    @Query("SELECT r FROM Reservation r WHERE r.station.stationId = :stationId " +
            "AND r.status = 'RESERVED' " +
            "AND ((r.startTime < :end AND r.endTime > :start))")
    List<Reservation> findOverlapping(@Param("stationId") Long stationId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    // member.email -> email로 변경
    List<Reservation> findByEmailOrderByStartTimeDesc(String email);
}