package com.tochookpi.tochookpi.jwt;

import com.tochookpi.tochookpi.dto.auth.CustomUserDetails;
import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.enums.Role;
import com.tochookpi.tochookpi.exception.TochookpiException;
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
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("/login".equals(path)) {
            return true;
        } else if ("/users".equals(path) && "POST".equalsIgnoreCase(method)) {
            return true;
        } else if ("/auth/verification-code".equals(path) || "/auth/verification-code/verify".equals(path) || "/auth/refresh".equals(path)) {
            return true;
        } else if (path.contains("/swagger-ui/") || path.contains("/v3/api-docs")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청에서 token값을 찾음
        String token = getJwtFromRequest(request);

        if (token == null) throw new TochookpiException(ErrorCode.TOKEN_NOT_PROVIDED);

        if (!jwtValidator.isExpired(token, true)) {
            String username = jwtValidator.getUsername(token, true);
            String role = jwtValidator.getRole(token, true);
            Role roleEnum = Role.valueOf(role);

            UserEntity userEntity = new UserEntity();
            userEntity.setEmail(username);
            userEntity.setPassword(null);
            userEntity.setRole(roleEnum);

            CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            // SecurityContextHolder에 인증정보 저장
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // 토큰이 유효하면 요청과 응답을 다음 필터로 넘겨줌
            filterChain.doFilter(request, response);
        } else {
            // 토큰이 유효하지 않다면 401 Unauthorized 응답
            throw new TochookpiException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
