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

    // 1. 적립금 (실제 예약 시 차감되는 금액)
    @Builder.Default
    @Column(name = "RESERVE_FUND", nullable = false)
    private Long reserveFund = 0L;

    // 2. 포인트 (결제 시 10% 쌓이는 보너스)
    @Builder.Default
    @Column(name = "POINT", nullable = false)
    private Long point = 0L;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    /**
     * 적립금 충전 (외부 결제 등을 통해 충전할 때)
     */
    public void addReserveFund(Long amount) {
        if (amount == null || amount <= 0) return;
        this.reserveFund += amount;
    }

    /**
     * 적립금 사용 (실제 예약 시 차감)
     */
    public void subtractReserveFund(Long amount) {
        if (this.reserveFund < amount) {
            throw new RuntimeException("적립금이 부족합니다. 충전 후 이용해주세요.");
        }
        this.reserveFund -= amount;
    }

    /**
     * 포인트 적립 (결제 성공 시 결제 금액의 10%를 쌓을 때 사용)
     */
    public void earnPoint(Long paymentAmount) {
        if (paymentAmount == null || paymentAmount <= 0) return;
        // 결제 금액의 10% 계산
        long bonus = (long) (paymentAmount * 0.1);
        this.point += bonus;
    }

    /**
     * 포인트를 적립금으로 전환 (포인트 -> 적립금 결제 사용)
     */
    public void convertPointToReserveFund(Long amount) {
        if (this.point < amount) {
            throw new RuntimeException("전환할 포인트가 부족합니다.");
        }
        this.point -= amount;      // 포인트 차감
        this.reserveFund += amount; // 적립금 증가
    }
}