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
    ApiResponse<MemberDto> response = new ApiResponse<>(true, "상세조회 성공", dto, 0, 0);
    return new ResponseEntity<>(response, HttpStatus.OK);
}

    @PutMapping("")
    public ResponseEntity<ApiResponse<String>> updateMember(@RequestBody MemberDto memberDto) {
        memberService.updateMember(memberDto);
        ApiResponse<String> response = new ApiResponse<>(true, "수정 성공", "SUCCESS", 0, 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
// TODO: 멤버쉽 컨트롤러

//      1. 사용자의 등급별 예약 수수료 조회
//      GET /api/member/benefit/fee?email=user@test.com
//    @GetMapping("/benefit/fee")
//    public ResponseEntity<ApiResponse<Long>> getMemberFee(@RequestParam("email") String email) {
//        long fee = memberService.getReservationFee(email);
//
//        ApiResponse<Long> response = new ApiResponse<>(
//                true, "등급별 수수료 조회 성공", fee, 1, 0);
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//     2. 사용자의 등급별 쿠폰 혜택 금액 조회 (월간/생일)
//    @GetMapping("/benefit/amount")
//    public ResponseEntity<ApiResponse<Long>> getMemberBenefitAmount(
//            @RequestParam("email") String email,
//            @RequestParam("type") String type) {
//
//        long amount = memberService.getBenefitAmount(email, type);
//
//        String message = type.equals("MONTHLY") ? "이달의 충전권 혜택" : "생일 축하 쿠폰 혜택";
//        ApiResponse<Long> response = new ApiResponse<>(
//                true, message + " 조회 성공", amount, 1, 0);
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}
