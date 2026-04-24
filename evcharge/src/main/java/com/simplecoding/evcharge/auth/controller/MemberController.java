package com.simplecoding.evcharge.auth.controller;

import com.simplecoding.evcharge.auth.dto.MemberDto;
import com.simplecoding.evcharge.auth.service.MemberService;
import com.simplecoding.evcharge.common.CommonUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService service;
    private final CommonUtil util;

    /**
     * 1) 로그인: ID/PWD 확인 후 JWT를 쿠키와 Body 모두에 담아 응답합니다.
     */
    @PostMapping("/auth/login")
    public ResponseEntity<MemberDto> login(@RequestBody MemberDto memberDto) {
        // 1. 서비스에서 인증 후 JWT 생성
        String jwt = service.login(memberDto);

        // 2. 중요: DB에서 사용자 상세 정보(이름, 권한 등)를 다시 가져옵니다.
        // login 메서드만 실행하면 password 정보만 있고 이름은 비어있을 수 있습니다.
        MemberDto loginUser = service.findByEmail(memberDto.getEmail());

        // 3. 핵심: 프론트엔드 Door.tsx가 찾을 수 있도록 Body에 토큰 세팅
        loginUser.setAccessToken(jwt);

        // 4. 쿠키 생성 (보안 강화)
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(false) // HTTPS 적용 전까지 false
                .path("/")
                .maxAge(60 * 60 * 24) // 1일
                .build();

        log.info("사용자 [{}] 로그인 성공: 토큰 발급 완료", loginUser.getEmail());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginUser); // ⭐ 이제 response.data.accessToken이 존재합니다!
    }

    /**
     * 2) 로그아웃: 쿠키 만료
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0) // 즉시 삭제
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    /**
     * 3) 회원가입
     */
    @PostMapping("/auth/register")
    public ResponseEntity<Void> register(@Valid @RequestBody MemberDto memberDto,
                                         BindingResult result) {
        util.checkBindingResult(result);
        service.register(memberDto); // 서비스 메서드명이 register인지 꼭 확인!
        return ResponseEntity.ok().build();
    }

    /**
     * 4) 상태 확인: 이름(memberName)까지 포함된 DTO 반환
     */
    @GetMapping("/me")
    public ResponseEntity<MemberDto> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        MemberDto memberDto = service.findByEmail(email);
        return ResponseEntity.ok(memberDto);
    }
}