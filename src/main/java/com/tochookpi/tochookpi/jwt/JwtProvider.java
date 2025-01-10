package com.tochookpi.tochookpi.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {
    private final SecretKey accessSecretKey;
    private final SecretKey refreshSecretKey;

    public JwtProvider(@Value("${jwt.access-secret}") String accessSecretKey, @Value("${jwt.refresh-secret}") String refreshSecretKey) {
        // HS256 알고리즘을 사용하여 SecretKey를 초기화
        this.accessSecretKey = new SecretKeySpec(accessSecretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.refreshSecretKey = new SecretKeySpec(refreshSecretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createAccessJwt(String username, String role, Long expiredMs) {
        // 사용자 이름, 역할 및 만료 시간(ms 단위)으로 JWT 생성
        return Jwts.builder()
                .claim("username", username) // claim으로 payload에 데이터를 넣어줄 수 있음.
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(accessSecretKey)
                .compact();
    }

    public String createRefreshJwt(String username, Long expiredMs) {
        // 사용자 이름, 만료 시간(ms 단위)으로 JWT 생성
        return Jwts.builder()
                .claim("username", username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(refreshSecretKey)
                .compact();
    }
}
