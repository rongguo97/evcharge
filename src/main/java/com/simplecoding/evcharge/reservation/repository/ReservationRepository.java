package com.simplecoding.evcharge.reservation.repository;

import com.simplecoding.evcharge.reservation.entity.Reservation;
import com.simplecoding.evcharge.reservation.entity.Status;
import com.simplecoding.evcharge.reservation.dto.ReservationDto;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
SELECT new com.simplecoding.evcharge.reservation.dto.ReservationDto(
    r.reservationId,
    r.station.id,
    r.email,
    r.startTime,
    r.endTime,
    r.status,
    r.station.name,
    r.station.address
)
FROM Reservation r
WHERE (:email IS NULL OR r.email = :email)
AND (:status IS NULL OR r.status = :status)
""")
    Page<ReservationDto> findReservationList(
            @Param("email") String email,
            @Param("status") Status status,
            Pageable pageable);

    @Query("""
SELECT COUNT(r) > 0 FROM Reservation r
WHERE r.station.id = :stationId
AND r.startTime < :endTime
AND r.endTime > :startTime
AND r.status = 'RESERVED'
""")
    boolean existsOverlapReservation(
            @Param("stationId") Long stationId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Station s WHERE s.id = :stationId")
    Object lockStation(@Param("stationId") Long stationId);
    @Query("""
SELECT r FROM Reservation r
WHERE r.startTime <= :now
AND r.status = 'RESERVED'
""")
    List<Reservation> findStartTargets(@Param("now") LocalDateTime now);
    @Query("""
SELECT r FROM Reservation r
WHERE r.reservationId = :id
AND r.startTime <= :nowPlus10
AND r.status = 'RESERVED'
""")
    Reservation findStartableReservation(
            @Param("id") Long id,
            @Param("nowPlus10") LocalDateTime nowPlus10);
    List<Reservation> findByStatus(Status status);
    @Query("""
SELECT r FROM Reservation r
WHERE r.endTime <= :now
AND r.status = 'CHARGING'
""")
    List<Reservation> findEndTargets(@Param("now") LocalDateTime now);

    @Query("""
SELECT r FROM Reservation r
WHERE r.endTime < :now
AND r.status = 'CHARGING'
""")
    List<Reservation> findOverstayTargets(@Param("now") LocalDateTime now);}