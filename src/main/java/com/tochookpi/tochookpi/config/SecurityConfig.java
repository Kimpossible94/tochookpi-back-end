package com.tochookpi.tochookpi.config;

import com.tochookpi.tochookpi.jwt.JwtFilter;
import com.tochookpi.tochookpi.jwt.JwtProvider;
import com.tochookpi.tochookpi.jwt.JwtValidator;
import com.tochookpi.tochookpi.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtProvider jwtProvider, JwtValidator jwtValidator) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtProvider = jwtProvider;
        this.jwtValidator = jwtValidator;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // LoginFilter의 매개변수로 넣어줄 AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()
                .requestMatchers(HttpMethod.POST, "/users", "/login").permitAll()
                .requestMatchers("/users").hasRole("USER")
                .requestMatchers("/meetings/**", "/my/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated()
//                .denyAll() // 모든 사용자의 접근을 거부
            )
                // UsernamePasswordAuthenticationFilter 자리에 커스텀 클래스 등록
                .addFilterAt(new LoginFilter(authenticationManager(this.authenticationConfiguration), this.jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(jwtValidator), LoginFilter.class);

        return http.build();
    }
}
