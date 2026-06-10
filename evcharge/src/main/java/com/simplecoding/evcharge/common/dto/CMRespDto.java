package com.simplecoding.evcharge.common.dto; // 프로젝트 패키지 구조에 맞게 수정하세요

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CMRespDto<T> {
    private int code;       // 1: 성공, -1: 실패
    private String msg;     // 응답 메시지
    private T result;       // 실제 데이터 (리스트, 객체 등)
}