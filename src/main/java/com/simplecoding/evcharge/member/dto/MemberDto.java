package com.simplecoding.evcharge.member.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberDto {
    private String email;          // PK
    private String password;       // 가입/수정 시 사용
    private String memberCode;
    private String memberName;
    private String carNumber;
    private String phoneNumber;
    private String role;
    private String isDeleted;
    private LocalDateTime insertTime;
    private LocalDateTime updateTime;
}
