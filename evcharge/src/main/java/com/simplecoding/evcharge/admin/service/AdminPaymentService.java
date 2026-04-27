package com.simplecoding.evcharge.admin.service;

import com.simplecoding.evcharge.admin.dto.AdminPaymentDto;
import com.simplecoding.evcharge.payment.entity.Payment; // 엔티티 경로 확인
import com.simplecoding.evcharge.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminPaymentService {
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public List<AdminPaymentDto.Response> getPaymentHistory() {
        // 최근 결제순으로 전체 조회
        return paymentRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(p -> AdminPaymentDto.Response.builder()
                        .payId(p.getPayId())
                        .email(p.getEmail())
                        .reservationId(p.getReservation() != null ? p.getReservation().getReservationId() : null)
                        .paymentType(p.getPaymentType())
                        .amount(p.getAmount())
                        .createdAt(p.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}