package com.simplecoding.evcharge.payment.entity;

import com.simplecoding.evcharge.common.BaseTimeEntity;
import com.simplecoding.evcharge.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_PAYMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pay_seq")
    @SequenceGenerator(name = "pay_seq", sequenceName = "SQ_PAYMENT", allocationSize = 1)
    @Column(name = "PAYMENT_ID")
    private Long id;

    @Column(nullable = false)
    private Long amount; // 결제 금액

    @Column(nullable = false, length = 50)
    private String payMethod; // 결제 수단 (CARD, POINT 등)

    @Column(nullable = false, length = 20)
    private String status; // 결제 상태 (DONE, CANCEL 등)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMAIL")
    private Member member;

    @Builder
    public Payment(Long amount, String payMethod, String status, Member member) {
        this.amount = amount;
        this.payMethod = payMethod;
        this.status = status;
        this.member = member;
    }
}