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

@Slf4j
@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class AdminLogController {

    private final AdminLogService adminLogService;

    @PostMapping
    public ResponseEntity<AdminLogDto.Response> saveLog(
            @RequestBody AdminLogDto.Request request) {
        return ResponseEntity.ok(adminLogService.saveLog(request));
    }

    @GetMapping
    public ResponseEntity<List<AdminLogDto.Response>> getAllLogs() {
        return ResponseEntity.ok(adminLogService.getAllLogs());
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<AdminLogDto.Response>> getLogsByAdminId(
            @PathVariable Long adminId) {
        return ResponseEntity.ok(adminLogService.getLogsByAdminId(adminId));
    }

    @GetMapping("/period")
    public ResponseEntity<List<AdminLogDto.Response>> getLogsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(adminLogService.getLogsByPeriod(from, to));
    }
}