package com.simplecoding.evcharge.admin.repository;

import com.simplecoding.evcharge.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface AdminStatsRepository extends JpaRepository<Member, Long> {

    // 최근 7일간의 가입자 수 통계
    @Query(value = "SELECT TO_CHAR(INSERT_TIME, 'YYYY-MM-DD') as \"date\", COUNT(*) as \"cnt\" " +
            "FROM TB_MEMBER " +
            "WHERE INSERT_TIME >= TRUNC(SYSDATE) - 7 " +
            "GROUP BY TO_CHAR(INSERT_TIME, 'YYYY-MM-DD') " +
            "ORDER BY \"date\"", nativeQuery = true)
    List<Map<String, Object>> getDailyRegistrationStats();

    // 최근 7일간의 예약 건수 통계
    @Query(value = "SELECT TO_CHAR(RESERVE_TIME, 'YYYY-MM-DD') as \"date\", COUNT(*) as \"cnt\" " +
            "FROM TB_RESERVATION " +
            "WHERE RESERVE_TIME >= TRUNC(SYSDATE) - 7 " +
            "GROUP BY TO_CHAR(RESERVE_TIME, 'YYYY-MM-DD') " +
            "ORDER BY \"date\"", nativeQuery = true)
    List<Map<String, Object>> getDailyReservationStats();
}