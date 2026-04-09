package com.simplecoding.evcharge.community.repository;

import com.simplecoding.evcharge.community.entity.CommunityPost;
import com.simplecoding.evcharge.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    // 삭제되지 않은 게시글 목록
    List<CommunityPost> findByIsDeletedOrderByInsertTimeDesc(String isDeleted);

    // 공지사항 조회
    List<CommunityPost> findByIsNoticeAndIsDeletedOrderByInsertTimeDesc(
            String isNotice, String isDeleted);

    // 이메일로 게시글 조회
    List<CommunityPost> findByEmailAndIsDeleted(String email, String isDeleted);

    // 제목 검색
    List<CommunityPost> findByTitleContainingIgnoreCaseAndIsDeleted(
            String keyword, String isDeleted);
}