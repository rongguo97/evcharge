package com.simplecoding.evcharge.payment.repository;

import com.simplecoding.evcharge.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 📍 [추가] 관리자용: 전체 결제 내역을 최신순(생성일 기준)으로 조회
    List<Payment> findAllByOrderByCreatedAtDesc();

    /**
     * 1) 회원별 결제 내역 최신순 조회
     * 필드명이 createdAt 이므로 메서드명도 이에 맞춰야 함
     */
    List<Payment> findByEmailOrderByCreatedAtDesc(String email);

    /**
     * 2) 특정 예약 번호에 해당하는 결제 내역 찾기
     * 수정 포인트: Payment 엔티티에 'reservation' 객체가 있으므로, 그 안의 'reservationId'를 찾으려면 언더바(_)를 사용하거나
     * 객체 참조 경로를 정확히 명시해야함.
     */
    List<Payment> findByReservation_ReservationId(Long reservationId);

    // 나중에 리액트에서 "더보기" 기능을 만들 때 사용하세요!
    //    Page<Payment> findByEmailOrderByCreatedAtDesc(String email, Pageable pageable);

    // 💡 이메일로 결제 내역 조회 (ID를 기준으로 최신순 정렬)
    // 만약 엔티티에 createdAt(생성일시)이 있다면 findByEmailOrderByCreatedAtDesc 도 좋습니다.
    List<Payment> findByEmailOrderByPayIdDesc(String email);
}