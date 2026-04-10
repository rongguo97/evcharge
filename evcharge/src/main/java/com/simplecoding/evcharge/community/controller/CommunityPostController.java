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

    @PostMapping
    public ResponseEntity<CommunityPostDto.Response> createPost(
            @RequestBody CommunityPostDto.CreateRequest request) {
        return ResponseEntity.ok(communityPostService.createPost(request));
    }

    @GetMapping
    public ResponseEntity<List<CommunityPostDto.Response>> getAllPosts() {
        return ResponseEntity.ok(communityPostService.getAllPosts());
    }

    @GetMapping("/notice")
    public ResponseEntity<List<CommunityPostDto.Response>> getNoticePosts() {
        return ResponseEntity.ok(communityPostService.getNoticePosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityPostDto.Response> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(communityPostService.getPost(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityPostDto.Response> updatePost(
            @PathVariable Long id,
            @RequestBody CommunityPostDto.UpdateRequest request) {
        return ResponseEntity.ok(communityPostService.updatePost(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        communityPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<CommunityPostDto.Response>> searchPosts(
            @RequestParam String keyword) {
        return ResponseEntity.ok(communityPostService.searchPosts(keyword));
    }
}