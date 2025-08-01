package com.tochookpi.tochookpi.service.auth;

import com.tochookpi.tochookpi.dto.auth.PhoneVerificationDTO;
import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.exception.TochookpiException;
import com.tochookpi.tochookpi.jwt.JwtProvider;
import com.tochookpi.tochookpi.jwt.JwtValidator;
import com.tochookpi.tochookpi.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {
    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public AuthServiceImpl(JwtProvider jwtProvider, JwtValidator jwtValidator, UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
        this.jwtProvider = jwtProvider;
        this.jwtValidator = jwtValidator;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        // Refresh Token 검증
        if(!jwtValidator.isExpired(refreshToken, false)) {
            // 토큰의 username 조회
            Long id = jwtValidator.getId(refreshToken, false);

            // DB에서 UserEntity 조회
            UserEntity userEntity = userRepository.findById(id)
                    .orElseThrow(() -> new TochookpiException(ErrorCode.EXPIRED_REFRESH_TOKEN));

            String role = userEntity.getRole().name();

            return jwtProvider.createAccessJwt(String.valueOf(id), role, 60*60*10L);
        }

        throw new TochookpiException(ErrorCode.EXPIRED_REFRESH_TOKEN);
    }

    @Override
    public void sendVerificationCode(PhoneVerificationDTO phoneVerificationDTO) {
        checkRequestLimit(phoneVerificationDTO.getPhone());
        String verificationCode = String.valueOf((int) (Math.random() * 1000000));
        phoneVerificationDTO.setVerificationCode(verificationCode);

        try {
            sendSms(phoneVerificationDTO);
            redisTemplate.opsForValue().set(
                    phoneVerificationDTO.getPhone(),
                    phoneVerificationDTO.getVerificationCode(),
                    5,
                    TimeUnit.MINUTES); // 5분간 유효
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SMS 전송 실패");
        }
    }

    @Override
    public boolean verifyVerificationCode(PhoneVerificationDTO phoneVerificationDTO) {
        String storedVerificationCode = (String) redisTemplate.opsForValue().get(phoneVerificationDTO.getPhone());

        if (storedVerificationCode != null && storedVerificationCode.equals(phoneVerificationDTO.getVerificationCode())) {
            redisTemplate.delete(phoneVerificationDTO.getPhone());  // 인증 후 삭제
            return true;
        }

        return false;
    }

    private void checkRequestLimit(String phone) {
        String limitKey = "verification:" + phone + ":limit";

        // 요청 횟수 증가 및 제한 초과 확인
        Long requestCount = redisTemplate.opsForValue().increment(limitKey);
        if (requestCount == 1) {
            // 최초 요청 시 만료 시간 설정
            redisTemplate.expire(limitKey, 1, TimeUnit.MINUTES); // 1분 제한 시간
        }

        if (requestCount > 10) { // 1분 동안 최대 10회 제한
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS, "요청 횟수를 초과했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    private void sendSms(PhoneVerificationDTO phoneVerificationDTO) {
        // 실제 SMS 전송 로직 구현 부분
        System.out.println("Sending verification code "
                + phoneVerificationDTO.getVerificationCode()
                + " to phone: "
                + phoneVerificationDTO.getPhone());
    }
}