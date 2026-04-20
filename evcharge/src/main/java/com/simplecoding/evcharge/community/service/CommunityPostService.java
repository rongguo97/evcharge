package com.simplecoding.evcharge.community.service;

import com.simplecoding.evcharge.community.dto.CommunityPostDto;
import com.simplecoding.evcharge.community.entity.CommunityPost;
import com.simplecoding.evcharge.community.repository.CommunityPostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private final CommunityPostRepository communityPostRepository;

    @Transactional
    public CommunityPostDto.Response createPost(CommunityPostDto.CreateRequest request) {
        CommunityPost post = CommunityPost.builder()
                .email(request.getEmail())
                .title(request.getTitle())
                .content(request.getContent())
                .isNotice(request.getIsNotice() != null ? request.getIsNotice() : "N")
                .isDeleted("N")
                .build();
        return toResponse(communityPostRepository.save(post));
    }

    @Transactional(readOnly = true)
    public List<CommunityPostDto.Response> getAllPosts() {
        return communityPostRepository.findByIsDeletedOrderByInsertTimeDesc("N")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommunityPostDto.Response> getNoticePosts() {
        return communityPostRepository
                .findByIsNoticeAndIsDeletedOrderByInsertTimeDesc("Y", "N")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommunityPostDto.Response getPost(Long id) {
        CommunityPost post = communityPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + id));
        return toResponse(post);
    }

    @Transactional
    public CommunityPostDto.Response updatePost(Long id, CommunityPostDto.UpdateRequest request) {
        CommunityPost post = communityPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + id));
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        if (request.getIsNotice() != null) post.setIsNotice(request.getIsNotice());
        return toResponse(post);
    }

    @Transactional
    public void deletePost(Long id) {
        CommunityPost post = communityPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + id));
        post.setIsDeleted("Y");
    }

    @Transactional(readOnly = true)
    public List<CommunityPostDto.Response> searchPosts(String keyword) {
        return communityPostRepository
                .findByTitleContainingIgnoreCaseAndIsDeleted(keyword, "N")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private CommunityPostDto.Response toResponse(CommunityPost entity) {
        return CommunityPostDto.Response.builder()
                .cUuid(entity.getCUuid())
                .email(entity.getEmail())
                .title(entity.getTitle())
                .content(entity.getContent())
                .isNotice(entity.getIsNotice())
                .isDeleted(entity.getIsDeleted())
                .insertTime(entity.getInsertTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}