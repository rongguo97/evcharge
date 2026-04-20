package com.simplecoding.evcharge.member.service;

import com.simplecoding.evcharge.common.CommonUtil;
import com.simplecoding.evcharge.common.MapStruct;
import com.simplecoding.evcharge.member.dto.MemberDto;
import com.simplecoding.evcharge.member.entity.Member;
import com.simplecoding.evcharge.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MapStruct mapStruct;
    private final CommonUtil util;
    /**
     * 1. 마이페이지 상세 조회
     * 이제 ID가 String(Email)임에 유의하세요!
     */
    @Transactional(readOnly = true)
    public MemberDto selectMemberDetail(String email) {
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new RuntimeException(util.getMessage("errors.not.found")));

        return mapStruct.toDto(member);
    }
//  회원 정보 수정

    @Transactional
    public void updateMember(MemberDto dto) {
        // PK인 email로 기존 회원 찾기
        Member member = memberRepository.findById(dto.getEmail())
                .orElseThrow(() -> new RuntimeException(util.getMessage("errors.not.found")));
        // 더티 체킹을 이용한 정보 업데이트
        mapStruct.updateFromDto(dto, member);
    }
//    회원 탈퇴: 삭제
@Transactional
public void withdrawMember(String email) {
    Member member = memberRepository.findById(email)
            .orElseThrow(() -> new RuntimeException(util.getMessage("errors.not.found")));

    member.setIsDeleted("Y");
}
}
