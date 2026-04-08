package com.simplecoding.evcharge.wallet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_WALLET")
@Getter @Setter // 💡 MapStruct 연동을 위해 Setter 추가 제안
@NoArgsConstructor @AllArgsConstructor @Builder // 💡 생성자 및 빌더 추가
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_seq")
    @SequenceGenerator(name = "wallet_seq", sequenceName = "SQ_WALLET", allocationSize = 1)
    @Column(name = "WALLET_ID") //
    private Long walletId; // 💡 변수명을 ERD와 맞춰 walletId로 추천

    @Column(name = "POINT") //
    private Long point = 0L;

    @Column(name = "EMAIL") // 💡 ERD에 명시된 EMAIL 컬럼 직접 매핑
    private String email;

    // 포인트 충전 및 사용 로직은 그대로 유지하시면 됩니다.
    public void addPoint(Long amount) {
        if (this.point == null) this.point = 0L;
        this.point += amount;
    }

    public void subtractPoint(Long amount) {
        if (this.point < amount) {
            throw new RuntimeException("잔액이 부족합니다.");
        }
        this.point -= amount;
    }
}