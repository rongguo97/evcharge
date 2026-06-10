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

    /**
     * 게시글 생성
     * 프론트에서 넘어온 isNotice(1 또는 0)를 그대로 저장합니다.
     */
    @Transactional
    public CommunityPostDto.Response createPost(CommunityPostDto.CreateRequest request) {
        CommunityPost post = CommunityPost.builder()
                .email(request.getEmail())
                .title(request.getTitle())
                .content(request.getContent())
                // 프론트에서 보낸 0 또는 1을 숫자로 확실히 세팅
                .isNotice(request.getIsNotice() != null ? request.getIsNotice() : 0)
                .isDeleted("N") // 문자열 "N"을 명시
                .build();

        return toResponse(communityPostRepository.save(post));
    }

    @Transactional(readOnly = true)
    public List<CommunityPostDto.Response> getAllPosts() {
        return communityPostRepository.findByIsDeletedOrderByInsertTimeDesc("N")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * 공지사항 조회
     * [수정] "Y" 대신 숫자 1을 조건으로 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<CommunityPostDto.Response> getNoticePosts() {
        return communityPostRepository
                .findByIsNoticeAndIsDeletedOrderByInsertTimeDesc(1, "N")
                .stream().map(this::toResponse).collect(Collectors.toList());
    }
//단건조회
@Transactional(readOnly = true)
public CommunityPostDto.Response getPost(Long cUuid) {
    CommunityPost post = communityPostRepository.findById(cUuid)
            .filter(p -> "N".equals(p.getIsDeleted()))
            .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. (ID: " + cUuid + ")"));

    return toResponse(post);
}

    @Transactional
    public CommunityPostDto.Response updatePost(Long cUuid, CommunityPostDto.UpdateRequest request) {
        CommunityPost post = communityPostRepository.findById(cUuid)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + cUuid));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        // [수정] 업데이트 시에도 Integer 값 적용
        if (request.getIsNotice() != null) {
            post.setIsNotice(request.getIsNotice());
        }

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

    /**
     * Entity -> DTO 변환
     */
    private CommunityPostDto.Response toResponse(CommunityPost entity) {
        return CommunityPostDto.Response.builder()
                .cUuid(entity.getCUuid())
                .email(entity.getEmail())
                .title(entity.getTitle())
                .content(entity.getContent())
                // null 체크 후 기본값 0(일반글) 반환
                .isNotice(entity.getIsNotice() != null ? entity.getIsNotice() : 0)
                .isDeleted(entity.getIsDeleted())
                .insertTime(entity.getInsertTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}