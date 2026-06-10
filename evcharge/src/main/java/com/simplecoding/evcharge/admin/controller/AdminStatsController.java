package com.simplecoding.evcharge.admin.controller;

import com.simplecoding.evcharge.admin.dto.AdminStatsDto;
import com.simplecoding.evcharge.admin.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로그 확인용
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    /**
     * AdminChart.tsx에서 호출하는 경로
     * 일별 예약 및 가입자 추이 데이터를 반환합니다.
     */
    @GetMapping("/daily")
    public ResponseEntity<List<AdminStatsDto>> getDailyStats() {
        log.info("Admin Dashboard Daily Stats Requested");
        List<AdminStatsDto> stats = adminStatsService.getDailyStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * StatsAnalysis.tsx에서 호출하는 경로
     * 통합 통계 데이터를 반환합니다. (기존 getDailyStats와 동일한 로직 활용)
     */
    @GetMapping("/analysis")
    public ResponseEntity<List<AdminStatsDto>> getAnalysisStats() {
        log.info("Admin Stats Analysis Data Requested");
        // 동일한 통계 서비스 로직을 호출하되, 프론트엔드의 요청 경로에 맞춰 응답합니다.
        List<AdminStatsDto> stats = adminStatsService.getDailyStats();
        return ResponseEntity.ok(stats);
    }
}