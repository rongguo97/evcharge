package com.simplecoding.evcharge.reservation.repository;

import com.simplecoding.evcharge.reservation.dto.ReservationDto;
import com.simplecoding.evcharge.reservation.entity.Reservation;
import com.simplecoding.evcharge.reservation.entity.Status;
import com.simplecoding.evcharge.station.entity.Station;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * 예약 목록 조회 (검색 + 페이징)
     * - email / status가 null이면 해당 조건은 무시
     * - Entity → DTO 바로 변환
     */
    @Query("""
        SELECT new com.simplecoding.evcharge.reservation.dto.ReservationDto(
            r.reservationId,
            r.station.stationId,
            r.email,
            r.startTime,
            r.endTime,
            r.status,
            r.station.stationName,
            r.station.address

        )
        FROM Reservation r
        WHERE (:email IS NULL OR r.email = :email)
          AND (:status IS NULL OR r.status = :status)
    """)
    Page<ReservationDto> findReservationList(
            @Param("email") String email,
            @Param("status") Status status,
            Pageable pageable
    );

    /**
     * 예약 시간 충돌 여부 체크
     * - 같은 충전소 내에서 시간 겹치는 예약 존재 여부 확인
     * - RESERVED 상태만 대상
     *
     * 겹침 조건:
     * 기존.start < 신규.end AND 기존.end > 신규.start
     */
    @Query("""
        SELECT COUNT(r) > 0
        FROM Reservation r
        WHERE r.station.stationId = :stationId
          AND r.startTime < :endTime
          AND r.endTime > :startTime
          AND r.status = 'RESERVED'
    """)
    boolean existsOverlapReservation(
            @Param("stationId") Long stationId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 충전소 단위 동시성 제어 (PESSIMISTIC WRITE LOCK)
     * - 동일 station에 대한 동시 예약/변경 충돌 방지
     * - 트랜잭션 내에서 row lock
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Station s WHERE s.stationId = :stationId")
    Station lockStation(@Param("stationId") Long stationId);

    /**
     * 현재 시각 기준 "시작 가능한 예약" 조회
     * - startTime <= now
     * - RESERVED 상태
     * - 스케줄러에서 "충전 시작 처리" 용도
     */
    @Query("""
        SELECT r
        FROM Reservation r
        WHERE r.startTime <= :now
          AND r.status = 'RESERVED'
    """)
    List<Reservation> findStartTargets(@Param("now") LocalDateTime now);

    /**
     * 단건 예약이 시작 가능한 상태인지 확인
     * - 예약 ID 기준 조회
     * - startTime이 nowPlus10 이전인지 체크 (유예/검증 로직)
     * - RESERVED 상태만 허용
     */
    @Query("""
        SELECT r
        FROM Reservation r
        WHERE r.reservationId = :id
          AND r.startTime <= :nowPlus10
          AND r.status = 'RESERVED'
    """)
    Reservation findStartableReservation(
            @Param("id") Long id,
            @Param("nowPlus10") LocalDateTime nowPlus10
    );

    /**
     * 상태 기준 예약 전체 조회
     * - 단순 조회용 (RESERVED, CHARGING, FINISHED 등)
     */
    List<Reservation> findByStatus(Status status);

    /**
     * 종료 처리 대상 조회
     * - endTime <= now
     * - CHARGING 상태
     * - 정상 종료 처리용 스케줄러
     */
    @Query("""
        SELECT r
        FROM Reservation r
        WHERE r.endTime <= :now
          AND r.status = 'CHARGING'
    """)
    List<Reservation> findEndTargets(@Param("now") LocalDateTime now);

    /**
     * 초과 사용(Overstay) 대상 조회
     * - 종료 시간이 지났는데도 CHARGING 상태 유지 중
     * - 과금 / 패널티 처리 대상
     */
    @Query("""
        SELECT r
        FROM Reservation r
        WHERE r.endTime < :now
          AND r.status = 'CHARGING'
    """)
    List<Reservation> findOverstayTargets(@Param("now") LocalDateTime now);
}