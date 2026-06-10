package com.simplecoding.evcharge.admin.dto;

import lombok.*;
import java.time.LocalDateTime;

public class AdminLogDto {

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long logId;
        private String adminEmail;
        private String action;
        private String targetId;
        private LocalDateTime createdAt;
        private Long adminId;
        private String targetType;
        private String ipAddress;
    }

    // 📍 여기에 @Builder를 추가해야 서비스에서 .builder()를 사용가능.
    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class Request {
        private String adminEmail;
        private String action;
        private String targetId;
        private Long adminId;
        private String targetType;
        private String ipAddress;
    }
}