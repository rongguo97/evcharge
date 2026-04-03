package com.simplecoding.evcharge.wallet.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.member.entity.Member;
import com.simplecoding.evcharge.member.repository.MemberRepository; // 1. 중요: MemberRepository 주소
import com.simplecoding.evcharge.wallet.dto.WalletDto;
import com.simplecoding.evcharge.wallet.entity.Wallet;
import com.simplecoding.evcharge.wallet.service.WalletService;      // 2. 중요: WalletService 주소
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor // 3. 중요: 아래 final 필드들을 스프링이 자동으로 채워줌
public class WalletController {

    // 도구함에 도구들을 넣어둡니다. (final 필수)
    private final WalletService walletService;
    private final MemberRepository memberRepository;

    /**
     * 내 지갑 정보 조회 (DTO 활용 버전)
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<WalletDto>> getMyPoint(@RequestParam("email") String email) {
        // 이메일로 회원 정보를 찾습니다.
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

        // 지갑을 가져오거나 새로 만듭니다.
        Wallet wallet = walletService.getOrCreateWallet(member);

        // DTO 가방에 담아서 전달합니다.
        WalletDto walletDto = new WalletDto(
                wallet.getId(),
                wallet.getMember().getEmail(),
                wallet.getPoint()
        );

        ApiResponse<WalletDto> response = new ApiResponse<>(true, "지갑 조회 성공", walletDto, 0, 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 포인트 충전하기
     */
    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<String>> chargePoint(
            @RequestParam("email") String email,
            @RequestParam("amount") Long amount) {

        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없" +
                        "습니다."));

        walletService.chargePoint(member, amount);

        ApiResponse<String> response = new ApiResponse<>(true, "포인트 충전 완료", "SUCCESS", 0, 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}