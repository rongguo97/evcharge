package com.simplecoding.evcharge.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_WALLET")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_seq")
    @SequenceGenerator(name = "wallet_seq", sequenceName = "SQ_WALLET", allocationSize = 1)
    @Column(name = "WALLET_ID")
    private Long walletId;

    @Builder.Default
    @Column(name = "RESERVEFUND", nullable = false)
    private Long reserveFund = 0L;

    @Builder.Default
    @Column(name = "POINT", nullable = false)
    private Long point = 0L;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    /**
     * 적립금 충전
     */
    public void addReserveFund(Long amount) {
        if (amount == null || amount <= 0) return;
        this.reserveFund = (this.reserveFund == null ? 0L : this.reserveFund) + amount;
    }

    /**
     * 적립금 사용
     */
    public void subtractReserveFund(Long amount) {
        if (amount == null || amount <= 0) return;
        if (this.reserveFund < amount) {
            throw new RuntimeException("적립금이 부족합니다. 충전 후 이용해주세요.");
        }
        this.reserveFund -= amount;
    }

    /**
     * 포인트 적립 (10%)
     */
    public void earnPoint(Long paymentAmount) {
        if (paymentAmount == null || paymentAmount <= 0) return;
        long bonus = (long) (paymentAmount * 0.1);
        this.point = (this.point == null ? 0L : this.point) + bonus;
    }

    /**
     * 포인트를 적립금으로 전환
     */
    public void convertPointToReserveFund(Long amount) {
        if (amount == null || amount <= 0) return;
        if (this.point < amount) {
            throw new RuntimeException("전환할 포인트가 부족합니다.");
        }
        this.point -= amount;
        this.reserveFund += amount;
    }
}