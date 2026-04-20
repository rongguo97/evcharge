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
    /**
     * 4. 멤버십 구독 업데이트 (등급 변경)
     * 결제 완료 후 호출하여 사용자의 등급을 변경합니다.
     */
    @Transactional
    public void upgradeMembership(String email, String newGrade) {
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new RuntimeException(util.getMessage("errors.not.found")));

        // 등급 업데이트 (더티 체킹)
        member.setGrade(newGrade.toUpperCase());
    }


//   등급별 예약 수수료 계산
//   비즈니스 로직: BASIC(1000), SILVER(500), GOLD(0)
    @Transactional(readOnly = true)
    public long getReservationFee(String email) {
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new RuntimeException(util.getMessage("errors.not.found")));

        String grade = member.getGrade() != null ? member.getGrade().toUpperCase() : "BASIC";

        return switch (grade) {
            case "GOLD" -> 0L;         // 100% 감면
            case "SILVER" -> 500L;     // 50% 감면
            default -> 1000L;          // BASIC
        };
    }


//  등급별 월간/생일 쿠폰 혜택 금액 조회
    @Transactional(readOnly = true)
    public long getBenefitAmount(String email, String type) {
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new RuntimeException(util.getMessage("errors.not.found")));

        String grade = member.getGrade() != null ? member.getGrade().toUpperCase() : "BASIC";

        if ("MONTHLY".equals(type)) {
            return switch (grade) {
                case "GOLD" -> 5000L;   //5000원
                case "SILVER" -> 3000L; //3000원
                default -> 0L;
            };
        } else if ("BIRTHDAY".equals(type)) {
            return switch (grade) {
                case "GOLD" -> 10000L; //10000원
                case "SILVER" -> 5000L; //5000원
                default -> 0L;
            };
        }
        return 0L;
    }
}
