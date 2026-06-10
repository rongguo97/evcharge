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
@RequestMapping("/api/community")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // ✅ 프론트엔드 주소 허용 (필수!!)
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

    // 게시글 단건 조회 (cUuid 기반)
    @GetMapping("/{cUuid}")
    public ResponseEntity<CommunityPostDto.Response> getPost(
            @PathVariable("cUuid") Long cUuid) { // 명시적으로 cUuid를 받음
        return ResponseEntity.ok(communityPostService.getPost(cUuid));
    }

    // 게시글 수정
    @PutMapping("/{cUuid}/update")
    public ResponseEntity<CommunityPostDto.Response> updatePost(
            @PathVariable("cUuid") Long id, // 경로의 {cUuid}를 'id'라는 변수에 담음
            @RequestBody CommunityPostDto.UpdateRequest request) {

        // 서비스의 updatePost(Long id, ...)와 타입을 일치시킴
        CommunityPostDto.Response result = communityPostService.updatePost(id, request);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{cUuid}/delete")
    public ResponseEntity<Void> deletePost(@PathVariable("cUuid") Long id) { // "cUuid"를 id 변수로 매핑
        communityPostService.deletePost(id);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    // 키워드 검색
    @GetMapping("/search")
    public ResponseEntity<List<CommunityPostDto.Response>> searchPosts(
            @RequestParam String keyword) {
        return ResponseEntity.ok(communityPostService.searchPosts(keyword));
    }
}