package com.simplecoding.evcharge.reservation.service;

import com.simplecoding.evcharge.charger.entity.Charger;
import com.simplecoding.evcharge.charger.repository.ChargerRepository;
import com.simplecoding.evcharge.member.entity.Member;
import com.simplecoding.evcharge.member.repository.MemberRepository;
import com.simplecoding.evcharge.reservation.entity.Reservation;
import com.simplecoding.evcharge.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ChargerRepository chargerRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Reservation createReservation(String email, Long chargerId, LocalDateTime startTime) {
        // 1. 회원 및 충전기 존재 확인
        Member member = memberRepository.findById(email).orElseThrow();
        Charger charger = chargerRepository.findById(chargerId).orElseThrow();

        // 2. 이용 시간 계산
        int durationMinutes = calculateDuration(charger.getChargerType()); // (예: 70분)
        int bufferMinutes = 10; // 버퍼 타임 10분
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes + bufferMinutes);

        // 3. 중복 예약 체크
        if (!reservationRepository.findOverlapping(chargerId, startTime, endTime).isEmpty()) {
            throw new RuntimeException("이미 해당 시간에 예약이 존재합니다.");
        }

        // 4. 예약 저장
        Reservation reservation = Reservation.builder()
                .member(member)
                .charger(charger)
                .startTime(startTime)
                .endTime(endTime)
                .status("RESERVED")
                .build();

        return reservationRepository.save(reservation);
    }

    // 💡 PDF 정책 반영 메서드
    private int calculateDuration(String type) {
        if (type.contains("50kW")) return 70;
        if (type.contains("100kW")) return 40;
        if (type.contains("7kW")) return 240; // 4시간
        return 60; // 기본값
    }
}