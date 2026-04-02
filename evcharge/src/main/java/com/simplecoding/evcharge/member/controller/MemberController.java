package com.simplecoding.evcharge.member.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.member.dto.MemberDto;
import com.simplecoding.evcharge.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/station") // 요청하신 대로 /station 주소 사용
@RequiredArgsConstructor
@Slf4j // 로그 확인용 (필요시)
public class MemberController {

    private final MemberService memberService;

//    회원정보 상세조회
@GetMapping("/{email}")
public ResponseEntity<ApiResponse<MemberDto>> getMemberDetail(@PathVariable("email") String email) {
    MemberDto dto = memberService.selectMemberDetail(email);

    // 기존 방식: 데이터가 1개여도 페이징 자리에 0, 0을 넣어줍니다.
    ApiResponse<MemberDto> response = new ApiResponse<>(true, "상세조회 성공", dto, 0, 0);

    return new ResponseEntity<>(response, HttpStatus.OK);
}

    @PutMapping("")
    public ResponseEntity<ApiResponse<String>> updateMember(@RequestBody MemberDto memberDto) {
        memberService.updateMember(memberDto);

        // 수정 성공 시에도 동일한 구조 유지
        ApiResponse<String> response = new ApiResponse<>(true, "수정 성공", "SUCCESS", 0, 0);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
