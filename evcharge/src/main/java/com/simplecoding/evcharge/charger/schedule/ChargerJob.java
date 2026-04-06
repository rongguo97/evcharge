package com.simplecoding.evcharge.charger.schedule;

import com.simplecoding.evcharge.charger.service.ChargerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * 스케줄러: 메모리 기반 페이지 증가 수집
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class ChargerJob {

    private final ChargerService service;                                                                                         // 서비스 DI
    private final OkHttpClient client = new OkHttpClient();                                                                  // 공공데이터 가져오기 플러그인 DI

    String serviceKey = "INH5JlH9iuKNiuZVX2tblTV7m9CqLf0rNKopVhVq8vF0LpzZNp658j7xXeucRpukCmqE%2BekjfJk9g3%2BPWAGWZA%3D%3D";  // 인증키(공공데이터)
    String baseUrl = "https://api.odcloud.kr/api/15119741/v1/uddi:fe904caf-636f-4a49-aa94-e9064a446b3e";                     // 전기차 주소
    private int page = 1;                                                                                                    // 공공데이터 1페이지
    private int perPage = 1000;                                                                                              // 공공데이터 페이지당개수 1000

    /**
     * 10초 간격으로 실행
     */
    @Scheduled(cron = "*/10 * * * * ?")                                                                                      // 스케줄 표현식(cron)
//    @Scheduled(cron = "0 * * * * ?")             // 매 분마다 실행
//    @Scheduled(cron = "0 0 * * * ?")             // 매 시마다 실행
//    @Scheduled(cron = "0 0 5 * * ?")             // 매일 5시에 실행
    public void run() throws Exception {

        String url = baseUrl                                                                                                 // 전기차 기본주소
                + "?page=" + page                                                                                            // 1페이지
                + "&perPage=" + perPage                                                                                      // 1000개(페이지당)
                + "&serviceKey=" + serviceKey;                                                                               // 인증키
        Request request = new Request.Builder().url(url).build();                                                            // 공공데이터 요청 문서

//      try(실행문){} : 파일, 네트웍은 사용이 끝나면 close() 닫기해야함, 아래 try 에 넣으면 자동으로 닫기가(close) 됨
        try (Response response = client.newCall(request).execute()) {                                                        // 요청 문서를 실행
            String json = response.body().string();                                                                          // 공공데이터 결과
            log.info("page 수집: " + page);
            service.save(json);                                                                                             // db 저장(공공데이터)
        }
    }
}