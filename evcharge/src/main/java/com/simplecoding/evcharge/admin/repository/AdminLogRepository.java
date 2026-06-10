package com.simplecoding.evcharge.admin.repository;

import com.simplecoding.evcharge.admin.entity.AdminLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {

    // 1. 기본 정렬 조회: 최신순으로 전체 보기
    List<AdminLog> findAllByOrderByCreatedAtDesc();

    // 2. 검색 및 필터링 (기존 유지)
    List<AdminLog> findByAdminId(Long adminId);
    List<AdminLog> findByAdminEmail(String adminEmail);
    List<AdminLog> findByTargetType(String targetType);
    List<AdminLog> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    List<AdminLog> findByActionContainingIgnoreCase(String keyword);

    /* 3. [추가 추천] 대량의 로그를 대비한 페이징 버전
       프론트엔드에서 1, 2, 3 페이지 버튼을 구현할 때 사용됩니다.
    */
    Page<AdminLog> findAll(Pageable pageable);
}