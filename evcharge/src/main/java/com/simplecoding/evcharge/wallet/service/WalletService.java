package com.simplecoding.evcharge.wallet.service;

import com.simplecoding.evcharge.member.entity.Member;
import com.simplecoding.evcharge.wallet.entity.Wallet;
import com.simplecoding.evcharge.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    /**
     * 특정 회원의 지갑 정보 가져오기
     * 만약 지갑이 없다면 새로 만들어서 저장함 (최초 1회)
     */
    @Transactional
    public Wallet getOrCreateWallet(Member member) {
        // 1. [수정] 메서드명을 findByEmail로 변경하고 이메일 문자열을 전달합니다.
        return walletRepository.findByEmail(member.getEmail())
                .orElseGet(() -> {
                    // 2. [수정] new Wallet(member) 대신 빌더를 사용하여 email을 넣어줍니다.
                    Wallet newWallet = Wallet.builder()
                            .email(member.getEmail())
                            .point(0L) // 초기 포인트 0
                            .build();
                    return walletRepository.save(newWallet);
                });
    }

    /**
     * 포인트 충전 로직
     */
    @Transactional
    public void chargePoint(Member member, Long amount) {
        Wallet wallet = getOrCreateWallet(member);
        wallet.addPoint(amount);
        // Dirty Checking으로 인해 별도의 save 호출 없이 반영됩니다.
    }
}