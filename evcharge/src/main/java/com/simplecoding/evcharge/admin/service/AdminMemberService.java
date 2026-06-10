package com.simplecoding.evcharge.admin.service;

import com.simplecoding.evcharge.admin.dto.AdminLogDto;
import com.simplecoding.evcharge.admin.dto.AdminMemberDto;
import com.simplecoding.evcharge.auth.entity.Member;
import com.simplecoding.evcharge.auth.repository.MemberRepository;
import com.simplecoding.evcharge.wallet.entity.Wallet; // 📍 지갑 엔티티 경로 확인 필요
import com.simplecoding.evcharge.wallet.repository.WalletRepository; // 📍 지갑 레포지토리 경로 확인 필요
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminMemberService {
    private final MemberRepository memberRepository;
    private final WalletRepository walletRepository; // 📍 추가: 지갑 정보를 가져오기 위해 주입
    private final AdminLogService adminLogService;

    @Transactional(readOnly = true)
    public List<AdminMemberDto.Response> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(m -> {
                    // 1. 해당 회원의 지갑 정보를 조회 (지갑이 없으면 null 반환)
                    // findByMemberEmail 등 실제 WalletRepository에 정의된 메서드명을 사용하세요.
                    Wallet wallet = walletRepository.findByEmail(m.getEmail()).orElse(null);

                    // 2. 지갑 정보가 있으면 해당 값을, 없으면 0을 설정
                    Long currentPoint = (wallet != null) ? wallet.getPoint() : 0L;
                    Long currentReserve = (wallet != null) ? wallet.getReserveFund() : 0L;

                    // 3. DTO 빌더에 포인트와 예치금 정보를 포함하여 생성
                    return AdminMemberDto.Response.builder()
                            .email(m.getEmail())
                            .memberName(m.getMemberName())
                            .carNumber(m.getCarNumber())
                            .role(m.getRole())
                            .grade(m.getGrade())
                            .isDeleted(m.getIsDeleted())
                            .insertTime(m.getInsertTime())
                            .point(currentPoint)     // 📍 AdminMemberDto.Response에 추가한 필드
                            .reserveFund(currentReserve) // 📍 AdminMemberDto.Response에 추가한 필드
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 회원 상태 업데이트 및 로그 기록 (기존 코드 유지)
     */
    @Transactional
    public void updateStatus(String email, String status) {
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다. email: " + email));

        member.setIsDeleted(status);

        adminLogService.saveLog(AdminLogDto.Request.builder()
                .adminEmail("admin@test.com")
                .adminId(1L)
                .action(status.equals("Y") ? "MEMBER_DISABLE" : "MEMBER_ENABLE")
                .targetId(email)
                .targetType("MEMBER")
                .ipAddress("127.0.0.1")
                .build());
    }
}