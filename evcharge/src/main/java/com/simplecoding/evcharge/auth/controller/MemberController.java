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

/**
 * 회원 인증(로그인, 로그아웃, 회원가입)을 처리하는 컨트롤러입니다.
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService service;
    private final CommonUtil util;

    /**
     * 1) 로그인: ID/PWD 확인 후 JWT를 쿠키에 구워서 응답합니다.
     */
    @PostMapping("/auth/login")
    public ResponseEntity<MemberDto> login(@RequestBody MemberDto memberDto) {
        // 1. 서비스에서 로그인 인증 후 JWT 토큰 생성
        String jwt = service.login(memberDto);

        // 2. 쿠키 생성 (보안 옵션 적용)
        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)                // 자바스크립트로 접근 불가 (보안)
                .secure(false)                 // TODO: HTTPS 환경이라면 true로 변경하세요.
                .path("/")                     // 모든 경로에서 쿠키 유효
                .maxAge(60 * 60 * 24)          // 쿠키 수명 (예: 1일)
//                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(memberDto);
    }

    /**
     * 2) 로그아웃: 저장된 쿠키를 즉시 만료시킵니다.
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        // 만료시간이 0인 쿠키를 보내서 브라우저의 쿠키를 삭제함
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
     * 3) 회원가입: 유효성 검사 후 DB 저장
     */
    @PostMapping("/auth/register")
    public ResponseEntity<Void> save(@Valid @RequestBody MemberDto memberDto,
                                     BindingResult result) {
        // 서버 유효성 체크 (MemberDto의 @NotBlank 등 검사)
        util.checkBindingResult(result);

        service.save(memberDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 4) 로그인 상태 확인 (새로고침 시 사용)
     * SecurityConfig에서 이 주소는 authenticated() 설정이 되어 있어야 합니다.
     */
    @GetMapping("/me")
    public ResponseEntity<MemberDto> me(Authentication authentication) {
        // 1. 인증 객체에서 이메일(ID) 추출
        String email = authentication.getName();

        // 2. DB에서 해당 이메일로 회원 정보를 조회 (Service 이용)
        // 이 단계에서 DB에 저장된 memberName, role 등이 담긴 DTO를 가져옵니다.
        MemberDto memberDto = service.findByEmail(email);

        log.info("인증된 사용자 [{}] 정보 반환 완료", email);

        // 3. DB에서 가져온 데이터(이름 포함)를 반환
        return ResponseEntity.ok(memberDto);
    }
}