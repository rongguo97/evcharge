package com.simplecoding.evcharge.config;

import com.simplecoding.evcharge.common.jwt.AuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthTokenFilter JwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // JWT 사용 시 비활성화 필수
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable());

        http.authorizeHttpRequests(auth -> auth
                // 1. CORS Preflight 요청 허용
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 2. 누구나 접근 가능한 공개 API (로그인, 회원가입, 충전소 조회 등)
                .requestMatchers(
                        "/api/auth/**",
                        "/api/station/**",
                        "/station/**",
                        "/main",
                        "/main/**",
                        "/"
                ).permitAll()

                // 3. 정적 리소스 및 문서 허용
                .requestMatchers(
                        "/api/download/**",
                        "/images/**",
                        "/css/**",
                        "/js/**",
                        "/favicon.ico",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ).permitAll()

                // 4. 관리자 전용 권한
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                // 5. 로그인(인증)이 반드시 필요한 경로
                .requestMatchers(
                        "/api/me",
                        "/api/reservation/**",
                        "/api/reservations/**",
                        "/api/member/update"
                ).authenticated()

                // 6. 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
        );

        // JWT 필터 배치
        http.addFilterBefore(JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 프론트엔드 포트 허용 (3000, 5173 모두 추가)
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}