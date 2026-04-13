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
    @Column(name = "LOG_ID")
    private Long logId;

    @Column(name = "ADMIN_EMAIL", nullable = false, length = 100)
    private String adminEmail;

    @Column(name = "ACTION", nullable = false, length = 100)
    private String action;

    @Column(name = "TARGET_ID", length = 100)
    private String targetId;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "ADMIN_ID", nullable = false)
    private Long adminId;

    @Column(name = "TARGET_TYPE", length = 20)
    private String targetType;

    @Column(name = "IP_ADDRESS", length = 45)
    private String ipAddress;
}