package com.simplecoding.evcharge.station.schedule;

import com.simplecoding.evcharge.station.service.StationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class StationJob {

    private final StationService service;
    private final OkHttpClient client = new OkHttpClient();

    // 1. 제공해주신 한전 API 인증키 적용
    private final String apiKey = "14103CgPaFg2IrUGi3TAKHDtO8k415Y5jA62b5ar";

    // 2. 한전 오픈 API 기본 주소 (이미지 b22654.png 참고)
    private final String baseUrl = "https://bigdata.kepco.co.kr/openapi/v1/EVchargeManage.do";

    /**
     * 10초 간격으로 한전 API를 호출하여 DB를 업데이트합니다.
     */
    @Scheduled(cron = "*/10 * * * * ?")
    public void run() {
        // 3. 한전 API 규격에 맞는 파라미터 조립 (apiKey, returnType 필수)
        String url = baseUrl
                + "?apiKey=" + apiKey
                + "&returnType=json";

        log.info("수집 시작 URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        // 4. API 호출 및 서비스 전달
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("API 호출 실패 - 상태코드: " + response.code());
                return;
            }

            String json = response.body().string();

            // 데이터가 비어있지 않은지 확인 후 저장
            if (json != null && !json.isEmpty()) {
                service.save(json);
                log.info("한전 충전소 데이터 수집 및 저장 완료");
            }

        } catch (Exception e) {
            log.error("스케줄러 실행 중 심각한 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}