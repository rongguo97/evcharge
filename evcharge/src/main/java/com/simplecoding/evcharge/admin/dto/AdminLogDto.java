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

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Request {
        private String adminEmail;
        private String action;
        private String targetId;
        private Long adminId;
        private String targetType;
        private String ipAddress;
    }
}