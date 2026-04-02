package com.simplecoding.evcharge.wallet.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.member.entity.Member;
import com.simplecoding.evcharge.member.repository.MemberRepository;
import com.simplecoding.evcharge.wallet.entity.Wallet;
import com.simplecoding.evcharge.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet") // 주소창에 /api/wallet 으로 시작하는 요청을 처리함
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final MemberRepository memberRepository; // 회원을 찾기 위해 필요함

    /**
     * 내 지갑 정보 조회 (잔액 확인)
     * GET http://localhost:8000/api/wallet/my?email=user@test.com
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Long>> getMyPoint(@RequestParam("email") String email) {
        // 1. 먼저 이메일로 회원 정보를 찾음
        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

        // 2. 서비스에게 지갑을 가져오라고 시킴
        Wallet wallet = walletService.getOrCreateWallet(member);

        // 3. ApiResponse 형식에 맞춰 잔액(Point)만 돌려줌
        ApiResponse<Long> response = new ApiResponse<>(true, "지갑 조회 성공", wallet.getPoint(), 0, 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 포인트 충전하기
     * POST http://localhost:8000/api/wallet/charge?email=user@test.com&amount=10000
     */
    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<String>> chargePoint(
            @RequestParam("email") String email,
            @RequestParam("amount") Long amount) {

        Member member = memberRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

        walletService.chargePoint(member, amount);

        ApiResponse<String> response = new ApiResponse<>(true, "포인트 충전 완료", "SUCCESS", 0, 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}