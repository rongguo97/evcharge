package com.simplecoding.evcharge.wallet.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.member.entity.Member;
import com.simplecoding.evcharge.member.repository.MemberRepository; // 1. 중요: MemberRepository 주소
import com.simplecoding.evcharge.wallet.dto.WalletDto;
import com.simplecoding.evcharge.wallet.entity.Wallet;
import com.simplecoding.evcharge.wallet.service.WalletService; // 2. 중요: WalletService 주소
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor // 3. 중요: 아래 final 필드들을 스프링이 자동으로 채워줌
public class WalletController {

    private final WalletService walletService;
    private final MemberRepository memberRepository;

    /**
     * 내 지갑 정보 조회 (DTO 빌더 패턴 적용)
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<WalletDto>> getMyPoint(@RequestParam("email") String email) {
// 이메일로 회원 정보를 찾습니다.
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

        Wallet wallet = walletService.getOrCreateWallet(member);

        // 💡 수정된 부분
        WalletDto walletDto = WalletDto.builder()
                .walletId(wallet.getId())
                .email(wallet.getMember().getEmail())
                .point(wallet.getPoint())
                .build();

        ApiResponse<WalletDto> response = new ApiResponse<>(true, "지갑 조회 성공", walletDto, 0, 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}