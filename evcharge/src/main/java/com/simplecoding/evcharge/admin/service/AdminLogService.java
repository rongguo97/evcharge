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

/**
 * 관리자 활동 로그를 처리하는 서비스 클래스입니다.
 * 관리자의 시스템 내 모든 작업(생성, 수정, 삭제 등)을 기록하고 조회하는 비즈니스 로직을 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminLogService {

    // 관리자 로그 데이터 접근을 위한 Repository (Lombok에 의해 생성자 주입됨)
    private final AdminLogRepository adminLogRepository;

    /**
     * 새로운 관리자 활동 로그를 저장합니다.
     * * @param request 저장할 로그 데이터가 담긴 DTO 요청 객체
     * @return 저장된 로그 데이터가 담긴 DTO 응답 객체
     */
    @Transactional
    public AdminLogDto.Response saveLog(AdminLogDto.Request request) {
        // DTO를 Entity로 변환 (Builder 패턴 사용)
        AdminLog log = AdminLog.builder()
                .adminEmail(request.getAdminEmail())
                .action(request.getAction())          // 수행한 작업 (예: UPDATE, DELETE)
                .targetId(request.getTargetId())      // 작업의 대상이 된 데이터 ID
                .adminId(request.getAdminId())        // 작업을 수행한 관리자 ID
                .targetType(request.getTargetType())  // 작업 대상의 유형 (예: USER, STATION)
                .ipAddress(request.getIpAddress())    // 접속 IP 주소
                .build();

        // Entity 저장 후 DTO로 변환하여 반환
        return toResponse(adminLogRepository.save(log));
    }

    /**
     * 시스템에 기록된 모든 관리자 활동 로그를 조회합니다.
     * 데이터 변경이 일어나지 않으므로 readOnly = true 속성을 사용하여 성능을 최적화합니다.
     * * @return 전체 로그 목록 (DTO 리스트)
     */
    @Transactional(readOnly = true)
    public List<AdminLogDto.Response> getAllLogs() {
        return adminLogRepository.findAll()
                .stream()
                .map(this::toResponse) // 각각의 Entity를 Response DTO로 변환
                .collect(Collectors.toList());
    }

    /**
     * 특정 관리자의 활동 로그 목록을 조회합니다.
     * * @param adminId 조회할 관리자의 고유 ID
     * @return 해당 관리자의 로그 목록 (DTO 리스트)
     */
    @Transactional(readOnly = true)
    public List<AdminLogDto.Response> getLogsByAdminId(Long adminId) {
        return adminLogRepository.findByAdminId(adminId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 지정된 기간 내의 관리자 활동 로그 목록을 조회합니다.
     * * @param from 조회 시작 일시
     * @param to 조회 종료 일시
     * @return 해당 기간 내의 로그 목록 (DTO 리스트)
     */
    @Transactional(readOnly = true)
    public List<AdminLogDto.Response> getLogsByPeriod(LocalDateTime from, LocalDateTime to) {
        return adminLogRepository.findByCreatedAtBetween(from, to)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * AdminLog Entity 객체를 클라이언트에게 반환하기 위한 AdminLogDto.Response 객체로 변환합니다.
     * 서비스 클래스 내부에서만 사용되는 유틸리티 메서드입니다.
     * * @param entity 변환할 원본 Entity 객체
     * @return 변환된 Response DTO 객체
     */
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