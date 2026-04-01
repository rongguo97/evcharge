package com.simplecoding.evcharge.charger.repository;

import com.simplecoding.evcharge.charger.entity.Charger;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargerRepository extends JpaRepository<Charger, Long> {

    // 1. [연관 관계 조회] 특정 충전소(STATION_ID)에 속한 충전기 리스트만 쫙 뽑아올 때
    List<Charger> findByStation_StationId(Long stationId);

    // 2. [조건 조회] 현재 상태(AVAILABLE 등)가 특정 값인 충전기만 찾을 때
    List<Charger> findBycStatus(String status);
}

