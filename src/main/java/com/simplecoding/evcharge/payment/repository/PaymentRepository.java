package com.simplecoding.evcharge.payment.repository;

import com.simplecoding.evcharge.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 특정 회원의 결제 내역을 최신순으로 모두 가져오는 기능 추가
    // findBy + Member(필드명) + Email(그 안의 필드명) + OrderBy + InsertTime + Desc
    List<Payment> findByMemberEmailOrderByInsertTimeDesc(String email);
}