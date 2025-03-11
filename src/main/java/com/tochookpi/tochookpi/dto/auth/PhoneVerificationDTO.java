package com.tochookpi.tochookpi.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PhoneVerificationDTO {
    private String phone;
    private String verificationCode;
}
