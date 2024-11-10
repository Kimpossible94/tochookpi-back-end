package com.tochookpi.tochookpi.jwt;

import com.tochookpi.tochookpi.dto.CustomUserDetails;
import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        if(authorization == null || !authorization.startsWith("Bearer ")) {
            // request와 response를 다음 필터로 넘겨줌.
            filterChain.doFilter(request, response);

            // Authorization의 value가 null이거나 Bearer로 시작하지 않는다면 메소드 종료
            return;
        }

        String token = authorization.substring("Bearer ".length());

        // 토큰 소멸시간 검증
        if(jwtTokenProvider.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtTokenProvider.getUsername(token);
        String role = jwtTokenProvider.getRole(token);
        Role roleEnum = Role.valueOf(role);

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(username);
        userEntity.setPassword("tempPassword");
        userEntity.setRole(roleEnum);

        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
