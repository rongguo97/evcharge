package com.simplecoding.evcharge.admin.schedule;

import com.simplecoding.evcharge.admin.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminLogCleanupScheduler {

    private final AdminLogRepository adminLogRepository;

    // 매일 새벽 2시 - 6개월 이상 된 로그 삭제
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanOldAdminLogs() {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        List<com.simplecoding.evcharge.admin.entity.AdminLog> oldLogs =
                adminLogRepository.findByCreatedAtBetween(
                        LocalDateTime.of(2000, 1, 1, 0, 0),
                        sixMonthsAgo
                );
        adminLogRepository.deleteAll(oldLogs);
        log.info("[AdminLogCleanup] {}개 오래된 로그 삭제 완료", oldLogs.size());
    }
}