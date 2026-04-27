package com.simplecoding.evcharge.admin.controller;

import com.simplecoding.evcharge.admin.dto.AdminMemberDto;
import com.simplecoding.evcharge.admin.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {
    private final AdminMemberService adminMemberService;

    @GetMapping
    public ResponseEntity<List<AdminMemberDto.Response>> getAllMembers() {
        return ResponseEntity.ok(adminMemberService.getAllMembers());
    }

    // 회원 탈퇴/복구 처리 (isDeleted 변경)
    @PutMapping("/{email}/status")
    public ResponseEntity<Void> updateMemberStatus(@PathVariable String email, @RequestParam String status) {
        adminMemberService.updateStatus(email, status);
        return ResponseEntity.ok().build();
    }
}