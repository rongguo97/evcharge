package com.simplecoding.evcharge.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 목적 : QueryDsl 플러그인 : sql 작성하는 또다른 플러그인 설정
@Configuration
public class QuerydslConfig {
    @PersistenceContext                          // JPA 클래스만 제공받습니다.(@Autowired 와 비슷합니다.)
    private EntityManager em;                    // JPA 클래스

    @Bean                                        // bean 으로 스프링에 만들어 집니다.
    public JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(em);          // JPA 를 queryDsl 에서 사용할 수 있게 설정합니다.
    }

}
