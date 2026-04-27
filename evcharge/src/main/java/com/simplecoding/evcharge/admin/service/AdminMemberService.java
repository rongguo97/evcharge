package com.simplecoding.evcharge.admin.service;

import com.simplecoding.evcharge.admin.dto.AdminLogDto;
import com.simplecoding.evcharge.admin.dto.AdminMemberDto;
import com.simplecoding.evcharge.auth.entity.Member; // Member 엔티티 경로 확인 필요
import com.simplecoding.evcharge.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMemberService {
    private final MemberRepository memberRepository;
    private final AdminLogService adminLogService; // 📍 로그 기록을 위해 추가 주입

    @Transactional(readOnly = true)
    public List<AdminMemberDto.Response> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(m -> AdminMemberDto.Response.builder()
                        .email(m.getEmail())
                        .memberName(m.getMemberName())
                        .carNumber(m.getCarNumber())
                        .role(m.getRole())
                        .grade(m.getGrade())
                        .isDeleted(m.getIsDeleted())
                        .insertTime(m.getInsertTime())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 📍 추가된 메서드: 회원 상태 업데이트 및 로그 기록
     */
    @Transactional
    public void updateStatus(String email, String status) {
        // 1. 회원 조회 (없으면 에러 발생)
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다. email: " + email));

        // 2. 상태 변경 (Dirty Checking으로 자동 업데이트)
        member.setIsDeleted(status);

        // 3. 관리자 활동 로그 저장
        adminLogService.saveLog(AdminLogDto.Request.builder()
                .adminEmail("admin@test.com") // 추후 세션/SecurityContext에서 가져오도록 수정
                .adminId(1L)                  // 추후 실제 관리자 ID로 수정
                .action(status.equals("Y") ? "MEMBER_DISABLE" : "MEMBER_ENABLE")
                .targetId(email)
                .targetType("MEMBER")
                .ipAddress("127.0.0.1")
                .build());
    }
}