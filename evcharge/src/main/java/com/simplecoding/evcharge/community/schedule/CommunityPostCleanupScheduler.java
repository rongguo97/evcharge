package com.simplecoding.evcharge.community.schedule;

import com.simplecoding.evcharge.community.entity.CommunityPost;
import com.simplecoding.evcharge.community.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommunityPostCleanupScheduler {

    private final CommunityPostRepository communityPostRepository;

    @Scheduled(cron = "0 0 3 * * SUN")
    @Transactional
    public void permanentlyDeletePosts() {
        List<CommunityPost> deletedPosts =
                communityPostRepository.findByIsDeletedOrderByInsertTimeDesc("Y");
        communityPostRepository.deleteAll(deletedPosts);
        log.info("[CommunityPostCleanup] {}개 게시글 영구 삭제 완료", deletedPosts.size());
    }
}