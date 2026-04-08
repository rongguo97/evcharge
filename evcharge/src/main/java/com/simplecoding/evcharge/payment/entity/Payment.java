package com.simplecoding.evcharge.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PAYMENT") // 💡 결제 테이블 연결
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pay_seq")
    @SequenceGenerator(name = "pay_seq", sequenceName = "SQ_PAYMENT", allocationSize = 1)
    @Column(name = "PAY_ID") //
    private Long payId;

    @Column(name = "EMAIL", nullable = false) //
    private String email;

    @Column(name = "RESERVATION_ID") // 💡 예약과 연결되는 핵심 컬럼
    private Long reservationId;

    @Column(name = "PAYMENT_TYPE") //
    private String paymentType; // 예: 'POINT_USAGE'

    @Column(name = "AMOUNT", nullable = false) //
    private Long amount;

    @Column(name = "METHOD") //
    private String method; // 예: 'POINT'

    @Column(name = "CREATED_AT") //
    private LocalDateTime createdAt;

    @PrePersist // 저장 전 자동으로 현재 시간 세팅
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}