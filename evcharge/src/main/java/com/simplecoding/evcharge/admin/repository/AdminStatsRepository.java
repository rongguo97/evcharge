// AdminStatsRepository.java
package com.simplecoding.evcharge.admin.repository;

import com.simplecoding.evcharge.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface AdminStatsRepository extends JpaRepository<Member, Long> {

    // 📍 이 메서드가 없거나 이름이 다르면 서비스에서 빨간 줄이 뜹니다.
    @Query(value =
            "SELECT " +
                    "    D.DT AS \"date\", " +
                    "    NVL(M.CNT, 0) AS \"registCount\", " +
                    "    NVL(R.CNT, 0) AS \"reservCount\", " +
                    "    NVL(R.CANCEL_CNT, 0) AS \"cancelCount\", " +
                    "    NVL(P.AMT, 0) AS \"revenue\" " +
                    "FROM ( " +
                    "    SELECT TO_CHAR(TRUNC(SYSDATE) - (LEVEL - 1), 'YYYY-MM-DD') AS DT " +
                    "    FROM DUAL CONNECT BY LEVEL <= 7 " +
                    ") D " +
                    "LEFT JOIN ( " +
                    "    SELECT TO_CHAR(INSERT_TIME, 'YYYY-MM-DD') AS DAY, COUNT(*) AS CNT " +
                    "    FROM TB_MEMBER GROUP BY TO_CHAR(INSERT_TIME, 'YYYY-MM-DD') " +
                    ") M ON D.DT = M.DAY " +
                    "LEFT JOIN ( " +
                    "    SELECT TO_CHAR(START_TIME, 'YYYY-MM-DD') AS DAY, " +
                    "           COUNT(CASE WHEN STATUS = 'RESERVED' THEN 1 END) AS CNT, " +
                    "           COUNT(CASE WHEN STATUS = 'CANCELLED' THEN 1 END) AS CANCEL_CNT " +
                    "    FROM TB_RESERVATION GROUP BY TO_CHAR(START_TIME, 'YYYY-MM-DD') " +
                    ") R ON D.DT = R.DAY " +
                    "LEFT JOIN ( " +
                    "    SELECT TO_CHAR(CREATED_AT, 'YYYY-MM-DD') AS DAY, SUM(AMOUNT) AS AMT " +
                    "    FROM TB_PAYMENT GROUP BY TO_CHAR(CREATED_AT, 'YYYY-MM-DD') " +
                    ") P ON D.DT = P.DAY " +
                    "ORDER BY D.DT ASC", nativeQuery = true)
    List<Map<String, Object>> getAdminDashboardStats(); // 👈 메서드명 확인!
}