package com.simplecoding.evcharge.admin.controller;

import com.simplecoding.evcharge.admin.dto.AdminLogDto;
import com.simplecoding.evcharge.admin.service.AdminLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 활동 로그와 관련된 REST API 요청을 처리하는 컨트롤러 클래스입니다.
 * 기본 URL 경로는 "/api/admin/logs"로 설정되어 있습니다.
 */
@Slf4j
@RestController // 이 클래스가 REST 컨트롤러임을 명시하며, 반환값은 자동으로 JSON 형태로 변환됩니다.
@RequestMapping("/api/admin/logs") // 공통 URL 매핑
@RequiredArgsConstructor // final 필드에 대해 생성자를 자동으로 생성해 의존성을 주입받습니다.
public class AdminLogController {

    // 비즈니스 로직 처리를 위한 서비스 클래스 (Lombok에 의해 생성자 주입됨)
    private final AdminLogService adminLogService;

    /**
     * 새로운 관리자 활동 로그를 생성(저장)합니다.
     * * @param request HTTP 요청 바디(JSON)로 전달된 로그 데이터
     * @return 생성된 로그 데이터와 HTTP 상태 코드 200(OK)
     */
    @PostMapping
    public ResponseEntity<AdminLogDto.Response> saveLog(
            @RequestBody AdminLogDto.Request request) {
        // 서비스 계층에 저장을 위임하고, 결과를 ResponseEntity로 감싸서 반환
        return ResponseEntity.ok(adminLogService.saveLog(request));
    }

    /**
     * 시스템에 기록된 전체 관리자 활동 로그 목록을 조회합니다.
     * * @return 전체 로그 목록 리스트와 HTTP 상태 코드 200(OK)
     */
    @GetMapping
    public ResponseEntity<List<AdminLogDto.Response>> getAllLogs() {
        return ResponseEntity.ok(adminLogService.getAllLogs());
    }

    /**
     * 특정 관리자 ID를 기준으로 활동 로그 목록을 조회합니다.
     * URL 경로 변수(Path Variable)를 사용하여 대상을 지정합니다.
     * * @param adminId 조회할 관리자의 고유 ID (예: /api/admin/logs/admin/1)
     * @return 해당 관리자의 로그 목록 리스트와 HTTP 상태 코드 200(OK)
     */
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<AdminLogDto.Response>> getLogsByAdminId(
            @PathVariable Long adminId) {
        return ResponseEntity.ok(adminLogService.getLogsByAdminId(adminId));
    }

    /**
     * 특정 기간 내에 발생한 관리자 활동 로그 목록을 조회합니다.
     * 쿼리 파라미터(Query Parameter)를 통해 시작 시간과 종료 시간을 받습니다.
     * * @param from 조회 시작 일시 (ISO 표준 포맷, 예: 2026-04-17T09:30:00)
     * @param to   조회 종료 일시 (ISO 표준 포맷)
     * @return 지정된 기간 내의 로그 목록 리스트와 HTTP 상태 코드 200(OK)
     */
    @GetMapping("/period")
    public ResponseEntity<List<AdminLogDto.Response>> getLogsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(adminLogService.getLogsByPeriod(from, to));
    }
}