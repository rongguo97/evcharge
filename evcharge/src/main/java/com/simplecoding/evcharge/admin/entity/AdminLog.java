package com.simplecoding.evcharge.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ADMIN_LOG")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_log_seq")
    @SequenceGenerator(name = "admin_log_seq", sequenceName = "ADMIN_LOG_SEQ", allocationSize = 1)
    @Column(name = "LOG_ID")  // 각 로그의 고유 식별자 (PK).
    private Long logId;

    @Column(name = "ADMIN_EMAIL", nullable = false, length = 100) // 작업을 수행한 관리자의 이메일 → 관리자 식별.
    private String adminEmail;

    @Column(name = "ACTION", nullable = false, length = 100) // 수행한 작업의 종류 (예: DELETE POST, BAN USER).
    private String action;

    @Column(name = "TARGET_ID", length = 100) //  작업 대상의 ID (게시글, 댓글, 사용자 등).
    private String targetId;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false) // 작업이 발생한 시각 → 자동 기록.
    private LocalDateTime createdAt;

    @Column(name = "ADMIN_ID", nullable = false)  // 관리자의 고유 ID → TB_USERS와 FK로 연결 가능.
    private Long adminId;

    @Column(name = "TARGET_TYPE", length = 20)  // 작업 대상의 유형 (POST, COMMENT, USER, REPORT).
    private String targetType;

    @Column(name = "IP_ADDRESS", length = 45)  // 작업을 수행한 관리자의 접속 IP → 보안 추적에 유용.
    private String ipAddress;
}