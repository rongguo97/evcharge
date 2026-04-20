package com.simplecoding.evcharge.auth.service;

import com.simplecoding.evcharge.auth.dto.MemberDto;
import com.simplecoding.evcharge.auth.entity.Member;
import com.simplecoding.evcharge.auth.repository.MemberRepository;
import com.simplecoding.evcharge.common.CommonUtil;
import com.simplecoding.evcharge.common.MapStruct;
import com.simplecoding.evcharge.common.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;
    private final MapStruct struct;       // DTO <-> Entity 변환기
    private final CommonUtil util;
    private final PasswordEncoder encoder; // 암호화

    private final JwtUtils jwtUtils;
    private final AuthenticationManagerBuilder managerBuilder;

    /**
     * 1) 로그인: 인증 후 웹토큰(JWT) 반환
     */
    public String login(MemberDto memberDto) {
        // id/pwd 검증을 위한 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberDto.getEmail(), memberDto.getPassword());

        // 검증 실행 (UserDetailsServiceImpl 호출됨)
        Authentication auth = managerBuilder.getObject().authenticate(authenticationToken);

        // 인증 성공 유저를 홀더에 저장
        SecurityContextHolder.getContext().setAuthentication(auth);

        // JWT 발급
        return jwtUtils.generateJwtToken(auth);
    }

    /**
     * 2) 회원가입
     */
    public void save(MemberDto memberDto) {
        // 중복 체크
        if (repository.existsById(memberDto.getEmail())) {
            throw new RuntimeException(util.getMessage("errors.register"));
        }

        // 비밀번호 해싱(암호화)
        memberDto.setPassword(encoder.encode(memberDto.getPassword()));

        // DTO -> Entity 변환 및 저장
        Member member = struct.toEntity(memberDto);
        repository.save(member);
    }
}