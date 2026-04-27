package com.simplecoding.evcharge.admin.dto;

import lombok.*;
import java.time.LocalDateTime;

public class AdminMemberDto {
    @Getter @Setter @Builder
    public static class Response {
        private String email;
        private String memberName;
        private String carNumber;
        private String phoneNumber;
        private String role;
        private String grade;
        private String isDeleted;
        private LocalDateTime insertTime;
    }
}