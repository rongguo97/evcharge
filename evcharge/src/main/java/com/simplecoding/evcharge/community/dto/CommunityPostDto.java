package com.simplecoding.evcharge.community.dto;

import lombok.*;

import java.time.LocalDateTime;

public class CommunityPostDto {

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long cUuid;
        private String email;
        private String title;
        private String content;
        private String isNotice;
        private String isDeleted;
        private LocalDateTime insertTime;
        private LocalDateTime updateTime;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateRequest {
        private String email;
        private String title;
        private String content;
        private String isNotice;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String content;
        private String isNotice;
    }
}