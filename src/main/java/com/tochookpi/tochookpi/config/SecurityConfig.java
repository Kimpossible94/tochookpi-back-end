package com.tochookpi.tochookpi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // 5.7 이후 버전의 보안 설정 방식
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/login").permitAll() // "/" 경로와 "/login" 경로는 모든 사용자에게 접근 가능 허용
                .requestMatchers(HttpMethod.POST, "/users").permitAll() // "/users" 경로는 특정 역할(ADMIN)만 접근 가능 허용
                .requestMatchers("/users").hasRole("USER") // "/users" 경로는 특정 역할(ADMIN)만 접근 가능 허용
                .requestMatchers("/meetings/**", "/my/**").hasAnyRole("ADMIN", "USER") // "meetings/, "/my" 경로는 특정 역할들(ADMIN, USER)만 접근 가능
                .anyRequest().authenticated() // 위에서 설정한 경로외에는 로그인을 해야 접근 허용
//                .denyAll() // 모든 사용자의 접근을 거부
        ).csrf(csrf -> csrf.disable());
        return http.build();
    }
}
