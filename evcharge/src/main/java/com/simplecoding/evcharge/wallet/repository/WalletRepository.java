package com.simplecoding.evcharge.wallet.repository;

import com.simplecoding.evcharge.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // 엔티티 필드명이 email이므로 findByEmail만 있으면됨.
    // JPA가 "SELECT * FROM TB_WALLET WHERE EMAIL = ?" 쿼리를 자동으로 생성합니다.
    Optional<Wallet> findByEmail(String email);

}