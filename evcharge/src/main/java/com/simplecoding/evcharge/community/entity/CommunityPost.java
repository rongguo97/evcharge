package com.simplecoding.evcharge.community.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_COMMUNITY")
@Getter @Setter
@NoArgsConstructor // JPA를 위한 기본 생성자
@AllArgsConstructor // Builder를 위한 모든 필드 생성자 (빨간줄 해결 핵심)
@Builder // 빌더 패턴 적용
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_post_seq")
    @SequenceGenerator(name = "community_post_seq", sequenceName = "COMMUNITY_POST_SEQ", allocationSize = 1)
    @Column(name = "C_UUID")
    private Long cUuid;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name = "CONTENT")
    private String content;

    @Builder.Default
    @Column(name = "IS_NOTICE", columnDefinition = "NUMBER(1,0)")
    private Integer isNotice = 0;

    @Builder.Default
    @Column(name = "IS_DELETED", columnDefinition = "CHAR(1)")
    private String isDeleted = "N";

    @CreationTimestamp
    @Column(name = "INSERT_TIME", updatable = false)
    private LocalDateTime insertTime;

    @UpdateTimestamp
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
}