package com.simplecoding.evcharge.auth.entity;

import com.simplecoding.evcharge.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_MEMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "email", callSuper = false)
public class Member extends BaseTimeEntity {

    @Id
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    private String memberCode;

    @Column(name = "MEMBER_NAME")
    private String memberName;

    @Column(name = "ROLE")
    private String role = "ROLE_USER";

    //  ERD에 맞춰 GRADE 필드 추가
    @Column(name = "GRADE", nullable = false)
    private String grade = "BRONZE";

    @Column(name = "CAR_NUMBER")
    private String carNumber;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "IS_DELETED")
    private String isDeleted = "N";



    // (참고) BaseTimeEntity를 상속받고 있다면 insertTime이 중복될 수 있음.
    // 만약 DB의 INSERT_TIME 컬럼과 명시적으로 연결해야 한다면 아래처럼 두도록함
    @Column(name = "INSERT_TIME", updatable = false)
    private LocalDateTime insertTime;
}