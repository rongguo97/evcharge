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


        // 📍 이 필드들이 추가되어야 프런트엔드에서 데이터를 받을 수 있습니다!
        private Long point;        // 보유 포인트
        private Long reserveFund;  // 예치금
    }
}