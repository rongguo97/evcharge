package com.simplecoding.evcharge.wallet.entity;

import com.simplecoding.evcharge.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_WALLET")
@Getter
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_seq")
    @SequenceGenerator(name = "wallet_seq", sequenceName = "SQ_WALLET", allocationSize = 1)
    @Column(name = "WALLET_ID") // ERD 반영
    private Long id;

    @Column(name = "POINT") // ERD 반영
    private Long point = 0L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMAIL") // Member의 PK인 email과 연결
    private Member member;

    public Wallet(Member member) {
        this.member = member;
        this.point = 0L;
    }

    // 포인트 충전 메서드
    public void addPoint(Long amount) {
        if (this.point == null) this.point = 0L;
        this.point += amount;
    }

    // 포인트 사용 메서드 (추후 결제 시 필요)
    public void subtractPoint(Long amount) {
        if (this.point < amount) {
            throw new RuntimeException("잔액이 부족합니다.");
        }
        this.point -= amount;
    }
}