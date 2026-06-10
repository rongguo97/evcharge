        package com.simplecoding.evcharge.common.jwt;

        import com.simplecoding.evcharge.auth.service.UserDetailsServiceImpl;
        import jakarta.servlet.FilterChain;
        import jakarta.servlet.ServletException;
        import jakarta.servlet.http.HttpServletRequest;
        import jakarta.servlet.http.HttpServletResponse;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
        import org.springframework.web.filter.OncePerRequestFilter;

        import java.io.IOException;
        import java.util.Optional;

        @Slf4j
        public class AuthTokenFilter extends OncePerRequestFilter {

            @Autowired
            private JwtUtils jwtUtils;

            @Autowired
            private UserDetailsServiceImpl userDetailsService;

            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                try {
                    // 1. 쿠키에서 토큰 꺼내기
                    Optional<String> jwt = jwtUtils.getJwtFromCookies(request);

                    // 2. 토큰 검증
                    if (jwt.isPresent() && jwtUtils.validateJwtToken(jwt.get())) {
                        String email = jwtUtils.getUserNameFromJwt(jwt.get());

                        // 3. DB 사용자 확인
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                        // 4. 시큐리티 인증 객체 생성
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 5. 시큐리티 홀더에 저장 (로그인 상태 유지)
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception e) {
                    log.error("JWT 인증 실패: {}", e.getMessage());
                }

                filterChain.doFilter(request, response);
            }
        }