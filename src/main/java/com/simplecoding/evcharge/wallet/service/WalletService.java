package com.simplecoding.evcharge.wallet.service;

import com.simplecoding.evcharge.member.entity.Member;
import com.simplecoding.evcharge.wallet.entity.Wallet;
import com.simplecoding.evcharge.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // 1. "이 클래스는 비즈니스 로직을 담당해"라고 스프링에 등록
@RequiredArgsConstructor // 2. Repository를 자동으로 가져올 수 있게 해줌 (Lombok)
public class WalletService {

    private final WalletRepository walletRepository;

    /**
     * 특정 회원의 지갑 정보 가져오기
     * 만약 지갑이 없다면 새로 만들어서 저장함 (최초 1회)
     */
    @Transactional // 3. DB 작업이 안전하게 완료되도록 보장 (매우 중요!)
    public Wallet getOrCreateWallet(Member member) {
        // 이메일로 지갑을 찾아보고, 없으면(orElse) 새로 생성함
        return walletRepository.findByMemberEmail(member.getEmail())
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet(member);
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
        // 4. 별도의 save() 호출 없이도 JPA가 변경을 감지해서 DB에 반영합니다. (Dirty Checking)
    }
}