package com.simplecoding.evcharge.admin.service;

import com.simplecoding.evcharge.admin.dto.AdminStatsDto;
import com.simplecoding.evcharge.admin.repository.AdminStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminStatsService {
    private final AdminStatsRepository adminStatsRepository;

    public List<AdminStatsDto> getDailyStats() {
        // 실제 운영 환경에서는 두 데이터를 날짜 기준으로 매칭하는 로직이 들어갑니다.
        // 여기서는 간단하게 가입자 통계를 기준으로 DTO를 생성하는 예시입니다.
        List<Map<String, Object>> regStats = adminStatsRepository.getDailyRegistrationStats();
        List<Map<String, Object>> resStats = adminStatsRepository.getDailyReservationStats();

        List<AdminStatsDto> result = new ArrayList<>();

        // 데이터 가공 로직 (생략 - regStats와 resStats를 날짜별로 병합)
        // 예시 데이터 생성
        for(Map<String, Object> map : regStats) {
            result.add(AdminStatsDto.builder()
                    .date((String) map.get("DT"))
                    .registCount(((Number) map.get("CNT")).longValue())
                    .reservCount(5L) // 임시 데이터 (실제론 resStats에서 해당 날짜를 찾아 매칭)
                    .build());
        }
        return result;
    }
}