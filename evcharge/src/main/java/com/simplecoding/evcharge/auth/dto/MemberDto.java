package com.simplecoding.evcharge.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;
    private String memberName;
    private String carNumber;   // 차량 번호 추가
    private String phoneNumber; // 전화번호 추가
    private String role = "ROLE_USER";
    private String insertTime;   // 최초 가입일로 사용
    private String accessToken;
}