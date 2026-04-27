package com.simplecoding.evcharge.admin.service;

import com.simplecoding.evcharge.admin.dto.AdminStatsDto;
import com.simplecoding.evcharge.admin.repository.AdminStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import com.simplecoding.evcharge.admin.repository.AdminLogRepository;

@Service
@RequiredArgsConstructor
public class AdminStatsService {
    private final AdminStatsRepository adminStatsRepository;

    public List<AdminStatsDto> getDailyStats() {
        List<Map<String, Object>> results = adminStatsRepository.getAdminDashboardStats();

        return results.stream().map(m -> AdminStatsDto.builder()
                .date((String) m.get("date"))
                .registCount(((Number) m.get("registCount")).longValue())
                .reservCount(((Number) m.get("reservCount")).longValue())
                .cancelCount(((Number) m.get("cancelCount")).longValue())
                .revenue(((Number) m.get("revenue")).longValue())
                .build()
        ).collect(Collectors.toList());
    }
}