package com.tochookpi.tochookpi.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private SecretKey secretKey;

    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secret) {
        // HS256 알고리즘을 사용하여 SecretKey를 초기화
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        // JWT에서 사용자 이름을 추출
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        // JWT에서 사용자 역할(role)을 추출
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        // JWT의 만료 여부를 확인
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(String username, String role, Long expiredMs) {
        // 사용자 이름, 역할 및 만료 시간(ms 단위)으로 JWT 생성
        return Jwts.builder()
                .claim("username", username) // claim으로 payload에 데이터를 넣어줄 수 있음.
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
