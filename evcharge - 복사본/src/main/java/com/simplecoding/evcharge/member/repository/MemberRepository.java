package com.simplecoding.evcharge.member.repository;

import com.simplecoding.evcharge.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    // JpaRepository<Entity, ID타입> 이므로 ID타입을 String으로 지정했습니다.

    // 가입 시 중복 확인을 위해 필요합니다.
    boolean existsByEmail(String email);
}