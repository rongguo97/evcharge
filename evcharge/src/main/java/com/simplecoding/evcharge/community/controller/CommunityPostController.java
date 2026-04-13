package com.simplecoding.evcharge.community.controller;

import com.simplecoding.evcharge.community.dto.CommunityPostDto;
import com.simplecoding.evcharge.community.service.CommunityPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    // 게시글 등록
    @PostMapping
    public ResponseEntity<CommunityPostDto.Response> createPost(
            @RequestBody CommunityPostDto.CreateRequest request) {
        return ResponseEntity.ok(communityPostService.createPost(request));
    }

    // 전체 게시글 조회 (삭제 제외)
    @GetMapping
    public ResponseEntity<List<CommunityPostDto.Response>> getAllPosts() {
        return ResponseEntity.ok(communityPostService.getAllPosts());
    }

    // 공지사항만 조회
    @GetMapping("/notice")
    public ResponseEntity<List<CommunityPostDto.Response>> getNoticePosts() {
        return ResponseEntity.ok(communityPostService.getNoticePosts());
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<CommunityPostDto.Response> getPost(
            @PathVariable Long id) {
        return ResponseEntity.ok(communityPostService.getPost(id));
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<CommunityPostDto.Response> updatePost(
            @PathVariable Long id,
            @RequestBody CommunityPostDto.UpdateRequest request) {
        return ResponseEntity.ok(communityPostService.updatePost(id, request));
    }

    // 게시글 삭제 (소프트 삭제)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        communityPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // 키워드 검색
    @GetMapping("/search")
    public ResponseEntity<List<CommunityPostDto.Response>> searchPosts(
            @RequestParam String keyword) {
        return ResponseEntity.ok(communityPostService.searchPosts(keyword));
    }
}