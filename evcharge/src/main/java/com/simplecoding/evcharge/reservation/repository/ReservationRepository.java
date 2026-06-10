package com.simplecoding.evcharge.reservation.repository;

import com.simplecoding.evcharge.reservation.dto.ReservationDto;
import com.simplecoding.evcharge.reservation.entity.Reservation;
import com.simplecoding.evcharge.reservation.entity.Status;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * 특정 충전소의 예약 중복 확인
     */
    @Query("SELECT r FROM Reservation r WHERE r.station.stationId = :stationId " +
            "AND r.status IN ('RESERVED', 'CHARGING') " +
            "AND r.startTime < :end " +
            "AND r.endTime > :start " +
            "AND r.rDate = :rDate")
    List<Reservation> findOverlapping(@Param("stationId") Long stationId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("rDate") String rDate);

    /**
     * 관리자/목록 조회용 (DTO로 직접 조회)
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
        ORDER BY r.startTime DESC
    """)
    Page<ReservationDto> findReservationList(
            @Param("email") String email,
            @Param("status") String status,
            Pageable pageable
    );

    /**
     * 회원의 예약 목록 조회 (최신순)
     */
    List<Reservation> findByEmailOrderByStartTimeDesc(String email);

    /**
     * 종료 처리 대상 조회 (스케줄러용)
     */
    @Query("SELECT r FROM Reservation r WHERE r.endTime <= :now AND r.status = 'CHARGING'")
    List<Reservation> findEndTargets(@Param("now") LocalDateTime now);

    /**
     * 지연(Overstay) 대상 조회
     */
    @Query("SELECT r FROM Reservation r WHERE r.endTime < :now AND r.status = 'CHARGING'")
    List<Reservation> findOverstayTargets(@Param("now") LocalDateTime now);

    /**
     * 노쇼/만료 예약 조회 (시작 시간 10분 경과 등)
     */
    @Query("SELECT r FROM Reservation r WHERE r.status = 'RESERVED' AND r.startTime < :limitTime")
    List<Reservation> findExpiredReservations(@Param("limitTime") LocalDateTime limitTime);

    /**
     * 마이페이지 '충전 제어'용 현재 진행 중인 예약 조회
     */
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.email = :email " +
            "AND r.status IN ('RESERVED', 'CHARGING') " +
            "ORDER BY r.startTime ASC")
    List<Reservation> findCurrentReservationByEmail(@Param("email") String email);

    /**
     * 📍 마이페이지 '최근 예약 리포트'용 전체 내역 조회
     * JOIN FETCH를 써서 Station 정보를 한 번에 가져오고, 최신순으로 정렬합니다.
     */
    // 📍 마이페이지 리포트용 (JOIN FETCH)
    @Query("SELECT r FROM Reservation r JOIN FETCH r.station WHERE r.email = :email ORDER BY r.startTime DESC")
    List<Reservation> findMyReservations(@Param("email") String email);

    // 📍 날짜 및 충전소별 예약된 슬롯 조회
    // @Param의 이름("chargerId", "rDate")이 쿼리문의 :이름과 반드시 같아야 합니다.
    @Query("SELECT r FROM Reservation r WHERE r.station.stationId = :chargerId " +
            "AND r.rDate = :rDate " +
            "AND r.status = 'RESERVED'")
    List<Reservation> findReservedSlotsByDate(@Param("chargerId") Long chargerId,
                                              @Param("rDate") String rDate);

    List<Reservation> findByEmailAndStatusIn(String email, List<String> statuses);
    }
