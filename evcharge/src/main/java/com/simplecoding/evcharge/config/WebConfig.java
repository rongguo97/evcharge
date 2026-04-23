package com.simplecoding.evcharge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.react.ip}")
    String reactIp;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 만약 reactIp가 "*"라면 allowCredentials(true)와 함께 쓸 수 없음.
                // 그럴 경우 .allowedOriginPatterns(reactIp)로 바꾸는 걸 AI가 추천.
                .allowedOrigins("http://localhost:5173")
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.PATCH.name()
                )
                .allowCredentials(true) // 쿠키 전송을 허용하는 핵심 설정
                .maxAge(3600); // 브라우저가 이 설정을 1시간 동안 기억하도록 설정
    }
}