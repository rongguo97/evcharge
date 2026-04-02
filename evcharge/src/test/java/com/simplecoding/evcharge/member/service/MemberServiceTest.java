package com.simplecoding.evcharge.member.service;

import com.simplecoding.evcharge.common.CommonUtil;
import com.simplecoding.evcharge.common.MapStruct;
import com.simplecoding.evcharge.member.dto.MemberDto;
import com.simplecoding.evcharge.member.entity.Member;
import com.simplecoding.evcharge.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 환경 설정
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MapStruct mapStruct;

    @Mock
    private CommonUtil util;

    @InjectMocks
    private MemberService memberService; // Mock들을 주입받은 서비스 객체

    private Member member;
    private MemberDto memberDto;
    private final String email = "test@naver.com";

    @BeforeEach
    void setUp() {
        // 테스트에 공통으로 사용할 데이터 세팅
        member = new Member();
        member.setEmail(email);
        member.setMemberName("홍길동");
        member.setIsDeleted("N");

        memberDto = new MemberDto();
        memberDto.setEmail(email);
        memberDto.setMemberName("홍길동");
    }

    @Test
    @DisplayName("마이페이지 상세 조회 테스트")
    void selectMemberDetail() {
        // given (준비): 가짜 객체의 행동 정의
        given(memberRepository.findById(email)).willReturn(Optional.of(member));
        given(mapStruct.toDto(member)).willReturn(memberDto);

        // when (실행): 테스트할 메서드 호출
        MemberDto result = memberService.selectMemberDetail(email);

        // then (검증): 결과가 예상과 같은지 확인
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(memberRepository, times(1)).findById(email);
    }

    @Test
    @DisplayName("회원 정보 수정 테스트")
    void updateMember() {
        // given
        given(memberRepository.findById(anyString())).willReturn(Optional.of(member));

        // when
        memberService.updateMember(memberDto);

        // then
        verify(memberRepository, times(1)).findById(memberDto.getEmail());

        // ✨ 수정 포인트: any(MemberDto.class) 처럼 타입을 명시하여 모호성 제거
        verify(mapStruct, times(1)).updateFromDto(any(MemberDto.class), any(Member.class));
    }

    @Test
    @DisplayName("회원 탈퇴(소프트 딜리트) 테스트")
    void withdrawMember() {
        // given
        given(memberRepository.findById(email)).willReturn(Optional.of(member));

        // when
        memberService.withdrawMember(email);

        // then
        assertEquals("Y", member.getIsDeleted()); // 엔티티 상태가 'Y'로 바뀌었는지 확인
        verify(memberRepository, times(1)).findById(email);
    }

    @Test
    @DisplayName("회원이 없을 때 상세 조회 실패 테스트")
    void selectMemberDetail_Fail() {
        // given
        given(memberRepository.findById(anyString())).willReturn(Optional.empty());
        given(util.getMessage(anyString())).willReturn("Not Found");

        // when & then (예외가 발생하는지 확인)
        assertThrows(RuntimeException.class, () -> {
            memberService.selectMemberDetail("none@test.com");
        });
    }
}