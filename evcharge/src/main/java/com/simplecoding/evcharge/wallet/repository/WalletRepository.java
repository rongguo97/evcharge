package com.simplecoding.evcharge.wallet.repository;

import com.simplecoding.evcharge.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // 이 지갑의 주인(Email)을 기반으로 지갑을 찾는 기능 추가
    // JPA가 메서드 이름을 분석해서 "SELECT * FROM TB_WALLET WHERE EMAIL = ?" 쿼리를 자동으로 생성.
    Optional<Wallet> findByMemberEmail(String email);
}