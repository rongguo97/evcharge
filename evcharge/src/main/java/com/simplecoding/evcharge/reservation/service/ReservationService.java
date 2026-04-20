package com.simplecoding.evcharge.reservation.service;

import com.simplecoding.evcharge.common.BaseTimeEntity;
import com.simplecoding.evcharge.reservation.dto.FeeResult;
import com.simplecoding.evcharge.reservation.dto.ReservationDto;
import com.simplecoding.evcharge.reservation.entity.Reservation;
import com.simplecoding.evcharge.reservation.entity.Status;
import com.simplecoding.evcharge.reservation.repository.ReservationRepository;
import com.simplecoding.evcharge.common.MapStruct;
import com.simplecoding.evcharge.station.entity.Station;
import com.simplecoding.evcharge.station.repository.StationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService extends BaseTimeEntity {
    private final StationRepository stationRepository;
    private final ReservationRepository repository;
    private final MapStruct mapper;
    

    public Page<ReservationDto> getReservationList(String email,
                                                   Status status,
                                                   Pageable pageable) {

        return repository.findReservationList(email, status, pageable);
    }
    @Transactional
    public void createReservation(ReservationDto dto) {

        // 1. 중복 예약 체크
        boolean isOverlap = repository.existsOverlapReservation(
                dto.getStationId(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (isOverlap) {
            throw new RuntimeException("이미 예약된 시간입니다.");
        }

        // 2. Entity 변환
        Reservation reservation = mapper.toEntity(dto);
        Station station = stationRepository.findById(dto.getStationId())
                .orElseThrow(() -> new RuntimeException("충전소 없음"));

        reservation.setStation(station);

        // 3. 상태 기본값
        reservation.setStatus(Status.RESERVED);

        // 4. 저장
        repository.save(reservation);
    }
    public ReservationDto getReservation(Long id) {

        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        return mapper.toDto(reservation);
    }

    @Transactional
    public void cancelReservation(Long id) {

        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        if (reservation.getStatus() != Status.RESERVED) {
            throw new RuntimeException("예약 상태만 취소 가능");
        }

        reservation.setStatus(Status.CANCELLED);
    }

    @Transactional
    public void startCharging(Long id) {

        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        LocalDateTime now = LocalDateTime.now();

        // 1. 시작 가능 조건 (-10분 허용)
        if (reservation.getStartTime().minusMinutes(10).isAfter(now)) {
            throw new RuntimeException("아직 시작 시간이 아닙니다.");
        }

        if (reservation.getStatus() != Status.RESERVED) {
            throw new RuntimeException("예약 상태만 시작 가능");
        }

        // 2. 상태 변경
        reservation.setStatus(Status.CHARGING);
    }
    @Transactional
    public void endCharging(Long id) {

        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        if (reservation.getStatus() != Status.CHARGING) {
            throw new RuntimeException("충전 중 상태만 종료 가능");
        }

        LocalDateTime now = LocalDateTime.now();

        // 1. 초과 여부 판단
        if (reservation.getEndTime().isBefore(now)) {
            reservation.setStatus(Status.OVERSTAY);
        } else {
            reservation.setStatus(Status.COMPLETED);
        }
    }

//    public Object calculateFee(Long id) {
//
//        Reservation reservation = repository.findById(id)
//                .orElseThrow(() -> new RuntimeException("예약 없음"));
//
//        LocalDateTime start = reservation.getStartTime();
//        LocalDateTime end = reservation.getEndTime() != null
//                ? reservation.getEndTime()
//                : LocalDateTime.now();
//
//        long minutes = java.time.Duration.between(start, end).toMinutes();
//
//        int baseFee = (int) minutes * 100; // 예: 1분 = 100원
//
//        int overstayFee = reservation.getOverstayFee();
//
//        return new FeeResult(minutes, baseFee, overstayFee);
//    }

    @Transactional
    public void pay(Long id, Object paymentRequest) {

        Reservation reservation = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("예약 없음"));

        if (reservation.getStatus() == Status.CANCELLED) {
            throw new RuntimeException("취소된 예약은 결제 불가");
        }
        if (reservation.getStatus() != Status.COMPLETED &&
                reservation.getStatus() != Status.OVERSTAY) {
            throw new RuntimeException("결제 가능한 상태가 아닙니다.");
        }
        // 결제 성공 가정
        reservation.setStatus(Status.COMPLETED);
    }}


//1. 충전소/충전기 조회 (상태 포함)

//2. 예약
//   - 중복 예약 방지
//   - 동시성 처리
//   - 예약 유효시간 관리

//3. 시작
//   - 자동 시작 (예약 시간 도달) 충전시작
//   - 수동 시작 (예약 전 도달) -10분
//   - 상태 연동

//4. 충전 진행
//   - 상태 실시간 업데이트
//   - 최대 시간 / 완충 감지

//5. 종료
//   - 수동 종료
//   - 자동 종료 (완충/시간초과/(오류-회의 필요))

//6. 이용시간 & 요금 계산
//
//7. 결제 처리
//   - 성공/실패/재시도

//8. 후처리
//   - 알림
      // -예약 전
    // 오버차지
//   - 로그 저장

//예약 생성
//  ↓
//중복 체크
//  ↓
//RESERVED
//
//→ 시작 (-10분 허용)
//  ↓
//CHARGING
//
//→ 종료
//  ↓
//COMPLETED or OVERSTAY
//
//→ 요금 계산
//  ↓
//결제
