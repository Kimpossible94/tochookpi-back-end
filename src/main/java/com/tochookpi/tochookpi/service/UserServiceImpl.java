package com.tochookpi.tochookpi.service;

import com.tochookpi.tochookpi.dto.UserAuthDTO;
import com.tochookpi.tochookpi.dto.UserDTO;
import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.enums.Role;
import com.tochookpi.tochookpi.exception.TochookpiException;
import com.tochookpi.tochookpi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO registerUser(UserAuthDTO userAuthDTO) {
        String encodedPassword = passwordEncoder.encode(userAuthDTO.getPassword());
        Role userRole = Role.ROLE_USER;

        // 이메일 중복체크
        if(userRepository.existsByEmail(userAuthDTO.getEmail())) {
            throw new TochookpiException(ErrorCode.DUPLICATE_EMAIL);
        }

        UserEntity user = new UserEntity(userAuthDTO.getEmail(), encodedPassword, userAuthDTO.getName(), userAuthDTO.getPhone(), userRole);
        UserEntity savedUser = userRepository.save(user);

        return new UserDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getProfileImage(),
                savedUser.getBio(),
                savedUser.getAddress()
        );
    }

    @Override
    public UserDTO getUserInfo(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));
        return new UserDTO(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getProfileImage(),
                userEntity.getBio(),
                userEntity.getAddress()
        );
    }
}
