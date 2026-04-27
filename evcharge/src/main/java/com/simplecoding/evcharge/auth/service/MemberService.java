package com.simplecoding.evcharge.auth.service;

import com.simplecoding.evcharge.auth.dto.MemberDto;
import com.simplecoding.evcharge.auth.entity.Member;
import com.simplecoding.evcharge.auth.repository.MemberRepository;
import com.simplecoding.evcharge.common.CommonUtil;
import com.simplecoding.evcharge.common.MapStruct;
import com.simplecoding.evcharge.common.jwt.JwtUtils;
import com.simplecoding.evcharge.wallet.entity.Wallet;
import com.simplecoding.evcharge.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;
    private final WalletRepository walletRepository; // 지갑 레포지토리 주입
    private final MapStruct struct;
    private final CommonUtil util;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManagerBuilder managerBuilder;
    private final MemberRepository memberRepository;

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
     * 2) 회원가입 + 지갑 생성
     * @Transactional: 회원저장은 성공했는데 지갑생성이 실패하면
     * 전부 취소(Rollback)해서 데이터 꼬임을 방지합니다.
     */
    @Transactional
    public void register(MemberDto memberDto) {
        // 1. 중복 체크
        if (repository.existsById(memberDto.getEmail())) {
            throw new RuntimeException(util.getMessage("errors.register"));
        }

        // 2. 비밀번호 해싱
        memberDto.setPassword(encoder.encode(memberDto.getPassword()));

        // 3. 회원 정보 저장
        Member member = struct.toEntity(memberDto);
        repository.save(member);

        Wallet wallet = new Wallet();
        wallet.setEmail(member.getEmail()); // 회원 이메일 연결
        wallet.setPoint(0L);                // 초기 포인트 0원
        wallet.setReserveFund(0L);          // 초기 적립금 0원

        walletRepository.save(wallet);      // 지갑 저장
    }
    /**
     * 이메일로 회원 정보를 조회하여 DTO로 반환합니다.
     */
    public MemberDto findByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. email=" + email));

        // DB의 INSERT_TIME을 "yyyy.MM.dd" 형식의 문자열로 변환
        String formattedDate = "";
        if (member.getInsertTime() != null) {
            formattedDate = member.getInsertTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        }

        // 요청하신 컬럼들로만 구성된 빌더
        return MemberDto.builder()
                .email(member.getEmail())
                .memberName(member.getMemberName())
                .carNumber(member.getCarNumber())
                .phoneNumber(member.getPhoneNumber())
                .role(member.getRole())
                .insertTime(formattedDate) // 포맷팅된 날짜 삽입
                .build();
    }
    @Transactional
    public void updateMember(MemberDto memberDto) {
        // 1. 수정할 기존 회원 정보 조회
        Member member = memberRepository.findByEmail(memberDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. email=" + memberDto.getEmail()));

        // 2. 비밀번호 변경 처리 (새 비밀번호가 입력된 경우에만 해싱하여 업데이트)
        if (memberDto.getPassword() != null && !memberDto.getPassword().isEmpty()) {
            member.setPassword(encoder.encode(memberDto.getPassword()));
        }

        // 3. 나머지 정보 업데이트 (필요한 컬럼만 선별)
        member.setMemberName(memberDto.getMemberName());
        member.setCarNumber(memberDto.getCarNumber());
        member.setPhoneNumber(memberDto.getPhoneNumber());

       
    }
}