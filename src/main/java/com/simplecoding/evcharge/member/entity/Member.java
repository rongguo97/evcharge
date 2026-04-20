package com.simplecoding.evcharge.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_MEMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@ToString
@EqualsAndHashCode(of = "email", callSuper = false)
public class Member {

    @Id // SQL 설계에 따라 EMAIL을 PK로 설정
    @Column(length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "MEMBER_CODE", length = 100)
    private String memberCode;

    @Column(name = "MEMBER_NAME", length = 100)
    private String memberName;

    @Column(name = "CAR_NUMBER", length = 100)
    private String carNumber;

    @Column(name = "PHONE_NUMBER", length = 100)
    private String phoneNumber;

    @Column(length = 20)
    @ColumnDefault("'USER'")
    private String role;

    @Column(name = "IS_DELETED", length = 1)
    @ColumnDefault("'N'")
    private String isDeleted;

    @Column(name = "INSERT_TIME", updatable = false)
    private LocalDateTime insertTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
//SQL의 DEFAULT는 DB를 위한 보험이고,
//@PrePersist는 자바 코드를 위한 편의 장치입니다.
    @PrePersist
    public void prePersist() {
        this.insertTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        // SQL Default값이 있지만 안전하게 한 번 더 세팅
        if (this.role == null) this.role = "USER";
        if (this.isDeleted == null) this.isDeleted = "N";
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}