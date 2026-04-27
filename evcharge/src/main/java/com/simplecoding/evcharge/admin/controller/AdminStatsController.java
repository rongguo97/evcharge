package com.simplecoding.evcharge.admin.controller;

import com.simplecoding.evcharge.admin.dto.AdminStatsDto;
import com.simplecoding.evcharge.admin.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @GetMapping("/daily")
    public ResponseEntity<List<AdminStatsDto>> getDailyStats() {
        return ResponseEntity.ok(adminStatsService.getDailyStats());
    }
}