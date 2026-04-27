package com.simplecoding.evcharge.auth.repository;

import com.simplecoding.evcharge.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * TB_MEMBER 테이블에 접근하기 위한 레포지토리입니다.
 * JpaRepository<엔티티타입, ID타입>을 상속받습니다.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    // 이메일이 @Id(PK)이므로, 기본 제공되는 findById(String email)를 통해
    // 로그인 시 사용자 조회가 가능.

    // 만약 나중에 이름으로 회원을 검색하고 싶다면 아래와 같은 메서드를 추가 가능.
    // Optional<Member> findByName(String name);
    // 이메일로 회원 정보를 찾는 쿼리 메서드입니다.
    Optional<Member> findByEmail(String email);


}