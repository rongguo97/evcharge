package com.simplecoding.evcharge.auth.entity;

import com.simplecoding.evcharge.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity                           // jpa 에게 이 클래스가 엔티티임을 알려줌
@Table(name = "TB_MEMBER")        // 연결할 db 테이블 작성
@Getter                           // 롬북 플러그인
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "email", callSuper = false)
public class Member extends BaseTimeEntity {
    //    @Id : jakarta ~ 폴더 선택
    @Id
    @Column(name = "EMAIL") // 컬럼명 명시
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    private String memberCode;

    // 1. DB에는 MEMBER_NAME으로 되어 있으므로 매핑
    @Column(name = "MEMBER_NAME")
    private String memberName;

    // 2. DB에는 ROLE로 되어 있으므로 매핑
    @Column(name = "ROLE")
    private String role = "ROLE_USER";

    // 3. 전기차 서비스라면 차량 번호 추가
    @Column(name = "CAR_NUMBER")
    private String carNumber;

    // (선택) 전화번호나 삭제 여부도 ERD에 있으므로 추가
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "IS_DELETED")
    private String isDeleted = "N";
}