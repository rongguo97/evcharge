package com.simplecoding.evcharge.payment.entity;

import com.simplecoding.evcharge.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PAYMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// BaseTimeEntity 상속 제거: 신규 ERD에 맞춰 CREATED_AT을 직접 관리합니다.
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pay_seq")
    @SequenceGenerator(name = "pay_seq", sequenceName = "SQ_PAYMENT", allocationSize = 1)
    @Column(name = "PAY_ID") // 1. 컬럼명 PAY_ID로 변경
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMAIL")
    private Member member;

    @Column(name = "RESERVATION_ID") // 2. 신규 추가: 예약 내역과 연결
    private Long reservationId;

    @Column(name = "PAYMENT_TYPE", length = 20) // 3. 신규 추가: 충전(CHARGE)인지 사용(USE)인지 구분
    private String paymentType;

    @Column(name = "AMOUNT", nullable = false)
    private Long amount; // 결제 또는 사용 금액

    @Column(name = "METHOD", length = 50) // 4. 컬럼명 METHOD로 변경
    private String method; // 결제 수단 (CARD, POINT 등)

    @Column(name = "CREATED_AT") // 5. 신규 추가: 결제 일시
    private LocalDateTime createdAt;

    @Builder
    public Payment(Member member, Long reservationId, String paymentType, Long amount, String method) {
        this.member = member;
        this.reservationId = reservationId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.method = method;
        this.createdAt = LocalDateTime.now(); // 객체 생성 시 현재 시간 자동 입력
    }
}