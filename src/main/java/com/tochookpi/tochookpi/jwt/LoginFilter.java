package com.tochookpi.tochookpi.jwt;

import com.tochookpi.tochookpi.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public LoginFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 1. 사용자의 요청에서 아이디와 비밀번호 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // 2. username과 password로 토큰을 만듬
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, null);

        // 3. AuthenticationManager에게 토큰을 넘겨주어 검증을 진행함.
        return authenticationManager.authenticate(authenticationToken);
    }

    // 인증이 성공했을 때
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 인증된 사용자 정보를 호출
        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

        // 사용자의 권한 정보 추출
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
        String username = userDetails.getUsername();
        // username, role로 1시간(60*60*1L) 동안 유효한 JWT 생성
        String token = jwtProvider.createJwt(username, role, 60*60*1L);
        // 응답 헤더에 Authorization으로 토큰 설정
        response.addHeader("Authorization", "Bearer " + token);
    }

    // 인증이 실패했을 때
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(401);
    }
}

