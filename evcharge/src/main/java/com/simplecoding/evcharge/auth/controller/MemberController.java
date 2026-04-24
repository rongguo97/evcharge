package com.simplecoding.evcharge.auth.controller;

import com.simplecoding.evcharge.auth.dto.MemberDto;
import com.simplecoding.evcharge.auth.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // 📍 이 경로의 RequestBody가 필요합니다.

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService service;

    @PostMapping("/auth/login")
    public ResponseEntity<MemberDto> me(@org.springframework.web.bind.annotation.RequestBody MemberDto memberDto) {
        // 1. JWT 생성
        String jwt = service.login(memberDto);

        // 2. DTO에 토큰 세팅 (MemberDto에 이 필드가 있어야 오류가 안 납니다!)
        memberDto.setAccessToken(jwt);

        // 3. 쿠키 생성
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60 * 24)
                .build();

        log.info("로그인 성공: {}", memberDto.getEmail());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(memberDto);
    }
}