package com.simplecoding.evcharge.config;
// 목적: 시큐리티(인증/권한) 설정 파일, 346페이지 참고

import com.simplecoding.evcharge.common.jwt.AuthTokenFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//임시 테스트용 허용 import
import org.springframework.http.HttpMethod; // ⭐ 추가된 임포트
import org.springframework.web.cors.CorsConfiguration; // ⭐ 추가된 임포트
import org.springframework.web.cors.CorsConfigurationSource; // ⭐ 추가된 임포트
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // ⭐ 추가된 임포트
import java.util.Arrays; // ⭐ 추가된 임포트

@Configuration           // 자바파일을 설정파일로 사용하게하는 어노테이션
@EnableWebSecurity       // 시큐리티 설정을 위한 어노테이션
public class SecurityConfig {

// 1) 암호화 함수: BCrypt 암호화 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2) 웹토큰 자동 검사 필터: AuthTokenFilter
        @Bean                                      // IOC
        public AuthTokenFilter JwtTokenFilter() {
            return new AuthTokenFilter();
        }

// 3) 인증/권한 설정: 로그인과 권한 설정은 여기서 하세요
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())                      // csrf(사이트 위조) 해킹공격 방어 비활성화
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션인증 안씀(웹토큰인증 사용)
                .formLogin(form -> form.disable());           // 스프링의 기본로그인화면 안씀

//      권한 관리: permitAll(): 모두 허용(로그인 없이 화면 보기), /api/auth/** (**: 하위폴더 여러개를 의미)
//      /api/auth/** : 로그인, 회원가입 화면들은 모두 볼수 있어야 합니다.(인증 없음)
//      /api/admin/**: 관리자 화면들은 로그인과 ROLE_ADMIN(권한명) 이 있어야 볼 수 있습니다. , hasAuthority("권한") : 권한 체크 함수
//      /images/**, /css/**, /js/**, /favicon.ico, /api/download/** : 이미지, css, js, 파비콘아이콘, 첨부파일 등은 모두 볼 수 있어야 합니다.(인증 없음)
//      /swagger-ui.html ~  : api 문서 자동 생성 플러그인에서 사용하는 주소는 모두 볼 수 있어야 합니다.(인증 없음)
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() //테스트용 임시 허용
                .requestMatchers("/api/auth/**","/station/**").permitAll()
                .requestMatchers("/station/**", "/api/station/**", "/api/reservation/**").permitAll() //테스트용 임시허용
                .requestMatchers("/api/auth/** /station/**\", \"/reservation/**\", \"/api/reservations/**", "/api/api/**").permitAll()                    // /api/auth/** 주소는 모두 허용(로그인 없이) // ← 이 줄 추가 lss 260427
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")            // /api/admin/** 주소는 관리자만 허용합니다.
                .requestMatchers("/api/download/**", "/images/**", "/css/**","/js/**", "/favicon.ico").permitAll() // 이미지등은 모두 허용

                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**","/v3/api-docs.yaml").permitAll()
                .requestMatchers("/api/reservation/**").authenticated() // 예약 관련 모든 API는 로그인 시 허용
                .requestMatchers("/api/reservation/current , /api/me").authenticated() //  이 주소는 로그인한 사람만!
                .requestMatchers("/main").permitAll()                                       // / (첫페이지)는 로그인 없이 모두 허용합니다.
                .anyRequest().authenticated());                                           // 위의 주소 이외의 주소는 모두 로그인해야 볼 수 있습니다.

//      4) 웹토큰 검사 필터 자동 실행
//        참고) 사용법) http.addFilterBefore(웹토큰필터, id검사필터); // id검사 필터 앞에 웹토큰필터를 넣으시오
//        용어: 시큐리티: Authentication == UserDetails == principal (사용자계정을 담는 클래스들)
        http.addFilterBefore(JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();                                                              // 위의 설정 실행 끝
    }
    //  3. CORS 전역 설정 (프론트엔드 포트의 접근을 완벽히 허용)    테스트용 임시허용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 실제 프론트엔드 주소를 명시해야함 (Vite 기본 포트 5173)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://127.0.0.1:5173"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true); // 쿠키 허용 필수!

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
