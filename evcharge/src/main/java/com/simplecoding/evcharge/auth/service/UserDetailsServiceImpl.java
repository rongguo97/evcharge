package com.simplecoding.evcharge.auth.service;

import com.simplecoding.evcharge.auth.dto.SecurityUserDto;
import com.simplecoding.evcharge.auth.entity.Member;
import com.simplecoding.evcharge.auth.repository.MemberRepository;
import com.simplecoding.evcharge.common.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository repository;
    private final CommonUtil util;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1) DB에서 계정 확인 (PK가 email이므로 findById 사용)
        Member member = repository.findById(email)
                .orElseThrow(() -> new RuntimeException(util.getMessage("errors.not.found")));

        // 2) 권한 객체 생성 (우리 엔티티의 codeName 필드 사용)
        GrantedAuthority authority = new SimpleGrantedAuthority(member.getRole());
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(authority);

        // 3) SecurityUserDto 반환 (email, 암호, 권한, 차량번호)
        return new SecurityUserDto(
                member.getEmail(),
                member.getPassword(),
                authorities,
                member.getCarNumber() // 우리 프로젝트의 특징인 차량번호를 신분증에 기록
        );
    }
}