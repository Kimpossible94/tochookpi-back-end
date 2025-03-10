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
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, S3Service s3Service) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.s3Service = s3Service;
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
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getProfileImage(),
                userEntity.getBio(),
                userEntity.getAddress()
        );
    }

    @Override
    public void modifyUserInfo(String loggedInUserEmail, UserDTO userDTO) {
        UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail).orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        userEntity.setAddress(userDTO.getAddress());
        userEntity.setBio(userDTO.getBio());

        userRepository.save(userEntity);
    }

    @Override
    public String modifyUserProfile(String loggedInUserEmail, MultipartFile multipartFile) {
        UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail).orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        String profileUrl = s3Service.uploadFile(multipartFile, "profile");
        userEntity.setProfileImage(profileUrl);
        userRepository.save(userEntity);

        return profileUrl;
    }
}
