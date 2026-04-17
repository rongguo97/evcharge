package com.simplecoding.evcharge.community.schedule;

import com.simplecoding.evcharge.community.entity.CommunityPost;
import com.simplecoding.evcharge.community.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 커뮤니티 게시글 정리(Cleanup)를 위한 스케줄러 컴포넌트입니다.
 * 사용자가 '삭제'를 눌러 논리적으로 삭제된(Soft Delete, isDeleted = "Y") 게시글들을
 * 주기적으로 데이터베이스에서 완전히 삭제(Hard Delete)하여 용량을 확보합니다.
 */
@Slf4j
@Component // 스프링 컨테이너가 관리하는 빈(Bean)으로 등록합니다.
@RequiredArgsConstructor
public class CommunityPostCleanupScheduler {

    // 게시글 데이터 접근을 위한 Repository
    private final CommunityPostRepository communityPostRepository;

    /**
     * 주기적으로 삭제 상태인 게시글을 찾아 데이터베이스에서 영구적으로 삭제합니다.
     * * Cron 표현식 설명: "0 0 3 * * SUN"
     * - 초(0) 분(0) 시간(3) 일(*) 월(*) 요일(SUN)
     * - 즉, 매주 일요일 새벽 3시 0분 0초에 실행됩니다. (트래픽이 적은 시간에 수행)
     */
    @Scheduled(cron = "0 0 3 * * SUN")
    @Transactional // 삭제 작업 중 문제가 발생하면 롤백하기 위해 트랜잭션 처리합니다.
    public void permanentlyDeletePosts() {
        // 1. 논리적 삭제 상태(isDeleted == "Y")인 게시글 목록을 데이터베이스에서 조회합니다.
        List<CommunityPost> deletedPosts =
                communityPostRepository.findByIsDeletedOrderByInsertTimeDesc("Y");

        // 2. 조회된 게시글 엔티티들을 데이터베이스에서 완전히(영구) 삭제합니다.
        communityPostRepository.deleteAll(deletedPosts);

        // 3. 스케줄러 실행 결과를 로그로 남깁니다. (실제 운영 환경에서 모니터링 용도)
        log.info("[CommunityPostCleanup] {}개 게시글 영구 삭제 완료", deletedPosts.size());
    }
}