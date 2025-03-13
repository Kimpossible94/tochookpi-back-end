package com.tochookpi.tochookpi.service.auth;

import com.tochookpi.tochookpi.dto.auth.PhoneVerificationDTO;

public interface AuthService {
    String refreshAccessToken(String refreshToken);
    void sendVerificationCode(PhoneVerificationDTO phoneVerificationDTO);
    boolean verifyVerificationCode(PhoneVerificationDTO phoneVerificationDTO);
}