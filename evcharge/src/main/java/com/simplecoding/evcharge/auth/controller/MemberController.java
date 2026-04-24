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
     * 1) 로그인: 인증 후 JWT를 쿠키와 바디(Body) 모두에 담아 반환
     */
    @PostMapping("/auth/login")
    public ResponseEntity<MemberDto> login(@RequestBody MemberDto memberDto) {
        // 1. 인증 및 JWT 생성
        String jwt = service.login(memberDto);

        // 2. DB에서 사용자 상세 정보(이름, 권한 등) 로드
        MemberDto loginUser = service.findByEmail(memberDto.getEmail());

        // 3. 프론트엔드 저장용 토큰 세팅
        loginUser.setAccessToken(jwt);

        // 4. 보안 쿠키 생성
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(false) // HTTPS 사용 시 true로 변경
                .path("/")
                .maxAge(60 * 60 * 24)
                .build();

        log.info("로그인 성공: [{}], 권한: [{}]", loginUser.getEmail(), loginUser.getRole());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginUser);
    }

    /**
     * 2) 회원가입: 유효성 검사 후 저장
     */
    @PostMapping("/auth/register")
    public ResponseEntity<Void> register(@Valid @RequestBody MemberDto memberDto,
                                         BindingResult result) {
        // 유효성 체크 (CommonUtil 활용)
        util.checkBindingResult(result);

        service.register(memberDto);
        log.info("회원가입 완료: {}", memberDto.getEmail());

        return ResponseEntity.ok().build();
    }

    /**
     * 3) 로그아웃: 쿠키 삭제
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    /**
     * 4) 상태 확인: 현재 로그인한 사용자의 정보를 반환
     * (이 정보를 통해 프론트에서 ROLE_ADMIN 여부를 판단합니다.)
     */
    @GetMapping("/me")
    public ResponseEntity<MemberDto> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        MemberDto memberDto = service.findByEmail(email);

        return ResponseEntity.ok(memberDto);
    }
}