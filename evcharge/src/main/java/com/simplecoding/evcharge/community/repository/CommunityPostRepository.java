package com.simplecoding.evcharge.community.repository;

import com.simplecoding.evcharge.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    List<CommunityPost> findByIsDeletedOrderByInsertTimeDesc(String isDeleted);
    List<CommunityPost> findByIsNoticeAndIsDeletedOrderByInsertTimeDesc(String isNotice, String isDeleted);
    List<CommunityPost> findByEmailAndIsDeleted(String email, String isDeleted);
    List<CommunityPost> findByTitleContainingIgnoreCaseAndIsDeleted(String keyword, String isDeleted);
}