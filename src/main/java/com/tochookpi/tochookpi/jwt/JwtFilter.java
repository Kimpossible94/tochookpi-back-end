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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    private final JwtValidator jwtValidator;

    public JwtFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청에서 token값을 찾음
        String token = getJwtFromRequest(request);

        if(token != null && !jwtValidator.isExpired(token)) {
            String username = jwtValidator.getUsername(token);
            String role = jwtValidator.getRole(token);
            Role roleEnum = Role.valueOf(role);

            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(username);
            userEntity.setPassword("tempPassword");
            userEntity.setRole(roleEnum);

            CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            // SecurityContextHolder에 인증정보 저장
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 요청과 응답을 다음 필터로 넘겨줌
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
