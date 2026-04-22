package com.simplecoding.evcharge.auth.service;

import com.simplecoding.evcharge.auth.dto.MemberDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@Log4j2
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService service;

    @Test
    void login() {//        1) 가짜 조건: db 회원 사용
        MemberDto dto=new MemberDto();
        dto.setEmail("bossman@example.com");
        dto.setPassword("123456");
//        2) 실행, 결과(jwt: 웹토큰)
        String jwt=service.login(dto);
//        3) 검증
        log.info(jwt);
    }

    @Test
    void save() {
//        1) 가짜 회원: 참조키(db 있는걸로)
        MemberDto dto=new MemberDto();
        dto.setEmail("ZergKong@SC.com");
        dto.setPhoneNumber("010-2222-2222");
        dto.setPassword("123456");
        dto.setMemberName("콩진호");
        dto.setRole("ROLE_USER"); // ROLE_ADMIN(관리자),ROLE_USER(사용자)
        dto.setMemberCode("2222a");
        dto.setCarNumber("2222이2222");        // 참조키: 사원테이블에 있는 걸로 해주세요
//        2) 저장
        service.save(dto);
    }
}