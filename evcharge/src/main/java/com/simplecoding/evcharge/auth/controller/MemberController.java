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

<<<<<<< HEAD
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
=======
    // 📍 [추가] 회원가입 메서드
    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@RequestBody MemberDto memberDto) {
        log.info("회원가입 요청: {}", memberDto.getEmail());

        // 서비스의 회원가입 로직 호출 (메서드 명은 프로젝트 상황에 맞게 수정하세요)
        service.register(memberDto);

        return ResponseEntity.ok("회원가입 성공");
    }

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
>>>>>>> 2c55f9a7ae29ddf60f6db46bdf5f0cc055b31f8c

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginUser); // ⭐ 이제 response.data.accessToken이 존재합니다!
    }
<<<<<<< HEAD

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
=======
>>>>>>> 2c55f9a7ae29ddf60f6db46bdf5f0cc055b31f8c
}