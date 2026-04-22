package com.simplecoding.evcharge.payment.entity;

import com.simplecoding.evcharge.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PAYMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pay_seq")
    @SequenceGenerator(name = "pay_seq", sequenceName = "SQ_PAYMENT", allocationSize = 1)
    @Column(name = "PAY_ID")
    private Long payId;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    // 1. 단순 ID 대신 예약 엔티티와 연관 관계 매핑 추천
    // 💡 예약 정보가 필요 없을 수도 있는 '충전(TOPUP)' 시에는 null이 허용되어야 합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESERVATION_ID", nullable = true)
    private Reservation reservation;

    // 2. 결제 구분 (TOPUP: 적립금 충전, RESERVE_USAGE: 예약 시 적립금 사용)
    @Column(name = "PAYMENT_TYPE", nullable = false, length = 20)
    private String paymentType;

    @Column(name = "AMOUNT", nullable = false)
    private Long amount;
    // 💡 [삭제] private String method; (요구사항에 따라 삭제)

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}