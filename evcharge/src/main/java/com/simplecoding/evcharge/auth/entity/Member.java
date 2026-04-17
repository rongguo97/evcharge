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
    @Id                                  // 기본키임을 알려주는 어노테이션
    private String email;                // 기본키
    private String password;
    private String name;
    private String codeName="ROLE_USER"; // 권한명(ROLE_USER(기본), ROLE_ADMIN)
}