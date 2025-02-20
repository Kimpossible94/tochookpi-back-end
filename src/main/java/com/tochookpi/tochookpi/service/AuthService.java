package com.tochookpi.tochookpi.service;

import com.tochookpi.tochookpi.dto.PhoneVerificationDTO;

public interface AuthService {
    String refreshAccessToken(String refreshToken);
    void sendVerificationCode(PhoneVerificationDTO phoneVerificationDTO);
    boolean verifyVerificationCode(PhoneVerificationDTO phoneVerificationDTO);
}