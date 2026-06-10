package com.simplecoding.evcharge.common.jwt;

import com.simplecoding.evcharge.auth.dto.SecurityUserDto;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtUtils {

    @Value("${simpleDms.app.jwtSecret}")
    private String jwtSecret;

    @Value("${simpleDms.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // 1) 웹토큰(JWT) 생성: 로그인 성공 시 호출
    public String generateJwtToken(Authentication authentication) {
        // 우리가 만든 SecurityUserDto로 형변환
        SecurityUserDto securityUserDto = (SecurityUserDto) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(securityUserDto.getUsername())          // 이메일 저장
                .setIssuedAt(new Date())                            // 발급일
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs)) // 만료일
                .signWith(SignatureAlgorithm.HS512, jwtSecret)      // 서명
                .compact();
    }

    // 2) 웹토큰에서 이메일 추출
    public String getUserNameFromJwt(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 3) 웹토큰 유효성 검사
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("JWT 서명 오류: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("잘못된 JWT 형식: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT 만료: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT 클레임이 비어 있음: {}", e.getMessage());
        }
        return false;
    }

    // JwtUtils.java 내부
    public Optional<String> getJwtFromCookies(HttpServletRequest request) {
        // 1. 요청에서 모든 쿠키를 가져옴
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // 2. 쿠키 이름이 "jwt"인 것을 찾음 (MemberController에서 정한 이름)
                if ("jwt".equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }
    //  목적: html 문서 헤더에서 웹토큰(JWT)을 뽑아내는 함수(이 예제에서는 안씁니다)
    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        // null 체크 및 "Bearer " 접두사 확인
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            // "Bearer " 이후 문자열(토큰) 반환
            return headerAuth.substring(7);
        }
        return null; // 토큰 없으면 null 반환
    }
}