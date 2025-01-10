package com.tochookpi.tochookpi.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtValidator {
    private final SecretKey accessSecretKey;
    private final SecretKey refreshSecretKey;

    public JwtValidator(@Value("${jwt.access-secret}") String accessSecretKey, @Value("${jwt.refresh-secret}") String refreshSecretKey) {
        // HS256 알고리즘을 사용하여 SecretKey를 초기화
        this.accessSecretKey = new SecretKeySpec(accessSecretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.refreshSecretKey = new SecretKeySpec(refreshSecretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token, boolean isAccessToken) {
        // JWT에서 사용자 이름을 추출
        SecretKey key = isAccessToken ? accessSecretKey : refreshSecretKey;
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token, boolean isAccessToken) {
        // JWT에서 사용자 역할(role)을 추출
        SecretKey key = isAccessToken ? accessSecretKey : refreshSecretKey;
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token, boolean isAccessToken) {
        // JWT의 만료 여부를 확인
        SecretKey key = isAccessToken ? accessSecretKey : refreshSecretKey;
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }
}
