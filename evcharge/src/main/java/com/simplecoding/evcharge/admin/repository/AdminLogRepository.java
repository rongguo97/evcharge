package com.simplecoding.evcharge.admin.repository;

import com.simplecoding.evcharge.admin.entity.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {

    List<AdminLog> findByAdminId(Long adminId);
    List<AdminLog> findByAdminEmail(String adminEmail);
    List<AdminLog> findByTargetType(String targetType);
    List<AdminLog> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    List<AdminLog> findByActionContainingIgnoreCase(String keyword);
}