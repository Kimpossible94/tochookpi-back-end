package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.dto.auth.PhoneVerificationDTO;
import com.tochookpi.tochookpi.service.auth.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshAccessToken(@CookieValue("refresh_token") String refreshToken) {
        String newAccessToken = authService.refreshAccessToken(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + newAccessToken);

        return ResponseEntity.ok().headers(headers).build();
    }

    @PostMapping("/verification-code")
    public ResponseEntity<Void> sendVerificationCode(@RequestBody PhoneVerificationDTO phoneVerificationDTO) {
        authService.sendVerificationCode(phoneVerificationDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verification-code/verify")
    public ResponseEntity<String> verifyVerificationCode(@RequestBody PhoneVerificationDTO phoneVerificationDTO) {
        boolean isVerified = authService.verifyVerificationCode(phoneVerificationDTO);
        if (isVerified) {
            return ResponseEntity.ok("인증 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증번호가 잘못되었습니다.");
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Void> validateToken() {
        return ResponseEntity.ok().build();
    }
}
