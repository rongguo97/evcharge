package com.simplecoding.evcharge.reservation.repository;

import com.simplecoding.evcharge.reservation.dto.ReservationDto;
import com.simplecoding.evcharge.reservation.entity.Reservation;
import com.simplecoding.evcharge.reservation.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            @Param("status") String status, // 📍 여기를 Status 객체에서 String으로 바꿨습니다!
            Pageable pageable
    );


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

    // 📍 상태가 'RESERVED'이면서 시작시간(startTime)에 10분을 더한 시간이 현재시간보다 이전인 것들 조회
    @Query("SELECT r FROM Reservation r WHERE r.status = 'RESERVED' AND r.startTime < :limitTime")
    List<Reservation> findExpiredReservations(@Param("limitTime") LocalDateTime limitTime);

    // ReservationRepository.java 에 추가

    // ReservationRepository.java 에 추가
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.email = :email " +
            "AND r.status IN ( 'RESERVED', 'CHARGING') " +
            "ORDER BY r.startTime ASC")
    List<Reservation> findCurrentReservationByEmail(@Param("email") String email);
}