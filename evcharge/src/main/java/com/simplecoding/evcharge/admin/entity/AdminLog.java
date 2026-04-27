package com.simplecoding.evcharge.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ADMIN_LOG", indexes = {
        @Index(name = "idx_admin_log_email", columnList = "ADMIN_EMAIL"),
        @Index(name = "idx_admin_log_created", columnList = "CREATED_AT")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_log_seq")
    @SequenceGenerator(name = "admin_log_seq", sequenceName = "ADMIN_LOG_SEQ", allocationSize = 1)
    @Column(name = "LOG_ID")
    private Long logId;

    @Column(name = "ADMIN_EMAIL", nullable = false, length = 100)
    private String adminEmail;

    @Column(name = "ACTION", nullable = false, length = 100)
    private String action;

    // 상세 내용을 담을 수 있는 필드 추가 (추천)
    // 예: "등급 변경: GOLD -> VIP"
    @Column(name = "DESCRIPTION", length = 500)
    @Transient
    private String description;

    @Column(name = "TARGET_ID", length = 255) // 길이를 조금 더 여유 있게 조정
    private String targetId;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "ADMIN_ID", nullable = false)
    private Long adminId;

    @Column(name = "TARGET_TYPE", length = 50) // 유연성을 위해 조금 확장
    private String targetType;

    @Column(name = "IP_ADDRESS", length = 45)
    private String ipAddress;
}