package com.simplecoding.evcharge.wallet.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.member.entity.Member;
import com.simplecoding.evcharge.member.repository.MemberRepository;
import com.simplecoding.evcharge.wallet.dto.WalletDto;
import com.simplecoding.evcharge.wallet.entity.Wallet;
import com.simplecoding.evcharge.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final MemberRepository memberRepository;

    /**
     * 내 지갑 정보 조회 (필드명 수정 반영)
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<WalletDto>> getMyPoint(@RequestParam("email") String email) {
        // 1. 이메일로 회원 정보를 찾습니다.
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

        // 2. 지갑 서비스 호출 (없으면 생성, 있으면 조회)
        Wallet wallet = walletService.getOrCreateWallet(member);

        // 💡 에러 발생 지점 수정:
        // 엔티티 필드명이 walletId와 email로 변경되었으므로 getter 이름을 그에 맞게 수정합니다.
        WalletDto walletDto = WalletDto.builder()
                .walletId(wallet.getWalletId()) // .getId() -> .getWalletId()
                .email(wallet.getEmail())       // .getMember().getEmail() -> .getEmail()
                .point(wallet.getPoint())
                .build();

        ApiResponse<WalletDto> response = new ApiResponse<>(true, "지갑 조회 성공", walletDto, 0, 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}