package com.simplecoding.evcharge.admin.service;

import com.simplecoding.evcharge.admin.dto.AdminLogDto;
import com.simplecoding.evcharge.admin.entity.AdminLog;
import com.simplecoding.evcharge.admin.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminLogService {

    private final AdminLogRepository adminLogRepository;

    // 로그 저장
    @Transactional
    public AdminLogDto.Response saveLog(AdminLogDto.Request request) {
        AdminLog log = AdminLog.builder()
                .adminEmail(request.getAdminEmail())
                .action(request.getAction())
                .targetId(request.getTargetId())
                .adminId(request.getAdminId())
                .targetType(request.getTargetType())
                .ipAddress(request.getIpAddress())
                .build();

        AdminLog saved = adminLogRepository.save(log);
        return toResponse(saved);
    }

    // 전체 로그 조회
    @Transactional(readOnly = true)
    public List<AdminLogDto.Response> getAllLogs() {
        return adminLogRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 관리자 ID로 조회
    @Transactional(readOnly = true)
    public List<AdminLogDto.Response> getLogsByAdminId(Long adminId) {
        return adminLogRepository.findByAdminId(adminId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 기간 조회
    @Transactional(readOnly = true)
    public List<AdminLogDto.Response> getLogsByPeriod(
            LocalDateTime from, LocalDateTime to) {
        return adminLogRepository.findByCreatedAtBetween(from, to)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AdminLogDto.Response toResponse(AdminLog entity) {
        return AdminLogDto.Response.builder()
                .logId(entity.getLogId())
                .adminEmail(entity.getAdminEmail())
                .action(entity.getAction())
                .targetId(entity.getTargetId())
                .createdAt(entity.getCreatedAt())
                .adminId(entity.getAdminId())
                .targetType(entity.getTargetType())
                .ipAddress(entity.getIpAddress())
                .build();
    }
}