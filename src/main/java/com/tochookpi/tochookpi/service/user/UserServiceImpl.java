package com.tochookpi.tochookpi.service.user;

import com.tochookpi.tochookpi.dto.user.UserAuthDTO;
import com.tochookpi.tochookpi.dto.user.UserDTO;
import com.tochookpi.tochookpi.dto.user.UserSummaryDTO;
import com.tochookpi.tochookpi.entity.MeetingEntity;
import com.tochookpi.tochookpi.entity.MeetingParticipantEntity;
import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.entity.UserSettingEntity;
import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.enums.Role;
import com.tochookpi.tochookpi.exception.S3OrphanFileException;
import com.tochookpi.tochookpi.exception.TochookpiException;
import com.tochookpi.tochookpi.repository.MeetingRepository;
import com.tochookpi.tochookpi.repository.UserRepository;
import com.tochookpi.tochookpi.service.storage.S3Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    public UserServiceImpl(UserRepository userRepository, MeetingRepository meetingRepository, PasswordEncoder passwordEncoder, S3Service s3Service) {
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
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

        UserEntity user = new UserEntity(
                userAuthDTO.getEmail(),
                encodedPassword,
                userAuthDTO.getName(),
                userAuthDTO.getPhone(),
                new UserSettingEntity(),
                userRole);
        UserEntity savedUser = userRepository.save(user);

        return new UserDTO(savedUser);
    }

    @Override
    public UserDTO getUserInfo(String id) {
        UserEntity userEntity = userRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));
        return new UserDTO(userEntity);
    }

    @Override
    public void modifyUserInfo(String loggedInUserId, UserDTO userDTO) {
        UserEntity userEntity = userRepository.findById(Long.parseLong(loggedInUserId))
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        userEntity.setAddress(userDTO.getAddress());
        userEntity.setBio(userDTO.getBio());

        userRepository.save(userEntity);
    }

    @Override
    public List<UserSummaryDTO> getAllUserSummaries(String loggedInUserId) {
        return userRepository.findAll().stream()
                .filter(userEntity -> !userEntity.getId().equals(Long.parseLong(loggedInUserId)))
                .map(UserSummaryDTO::new).collect(Collectors.toList());
    }

    @Override
    public String modifyUserProfile(String loggedInUserId, MultipartFile multipartFile) {
        UserEntity userEntity = userRepository.findById(Long.parseLong(loggedInUserId))
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        String profileUrl = s3Service.uploadFile(multipartFile, "profile");
        userEntity.setProfileImage(profileUrl);

        try {
            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new S3OrphanFileException(ErrorCode.FAIL_IMAGE_UPLOAD, userEntity.getProfileImage());
        }

        return profileUrl;
    }

    @Override
    public void deleteUser(String loggedInUserId) {
        UserEntity userEntity = userRepository.findById(Long.parseLong(loggedInUserId))
                .orElseThrow(() -> new TochookpiException(ErrorCode.USER_NOT_FOUND));

        userRepository.delete(userEntity);

        // 유저가 만든 모임 조회
        List<MeetingEntity> meetingEntityList = meetingRepository.findByOrganizerId(userEntity.getId()).orElse(new ArrayList<>());

        for(MeetingEntity meetingEntity : meetingEntityList) {
            List<MeetingParticipantEntity> participants = meetingEntity.getParticipants();

            if(participants.isEmpty()) {
                // 참가자가 없으면 모임 제거
                meetingRepository.delete(meetingEntity);
            } else {
                // 참가자가 있으면 다른 유저에게 모임 주최자 권한 위임
                MeetingParticipantEntity newOrganizerParticipant = participants.get(0);
                UserEntity newOrganizer = newOrganizerParticipant.getUser();

                meetingEntity.setOrganizer(newOrganizer);
                participants.remove(newOrganizerParticipant);

                meetingRepository.save(meetingEntity);
            }
        }

        userRepository.delete(userEntity);
    }
}
