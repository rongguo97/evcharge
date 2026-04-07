package com.simplecoding.evcharge.payment.repository;

import com.simplecoding.evcharge.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // insertTime -> createdAt 으로 필드명이 바뀌었다면 메서드명도 수정!
    // 회원별 결제 내역을 최신순으로 조회
    List<Payment> findByMemberEmailOrderByCreatedAtDesc(String email);

    // 특정 예약 번호에 해당하는 결제 내역 찾기 (신규 추가된 기능)
    List<Payment> findByReservationId(Long reservationId);
}