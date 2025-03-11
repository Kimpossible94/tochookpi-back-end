package com.tochookpi.tochookpi.service.user;

import com.tochookpi.tochookpi.dto.user.UserSettingDTO;
import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.entity.UserSettingEntity;
import com.tochookpi.tochookpi.enums.ErrorCode;
import com.tochookpi.tochookpi.exception.TochookpiException;
import com.tochookpi.tochookpi.repository.UserRepository;
import com.tochookpi.tochookpi.repository.UserSettingRepository;
import org.springframework.stereotype.Service;

@Service
public class UserSettingServiceImpl implements UserSettingService {
    private final UserRepository userRepository;
    private final UserSettingRepository userSettingRepository;

    public UserSettingServiceImpl(UserRepository userRepository, UserSettingRepository userSettingRepository) {
        this.userRepository = userRepository;
        this.userSettingRepository = userSettingRepository;
    }

    @Override
    public UserSettingDTO getUserSetting(String loggedInUserEmail) {
        UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail).orElseThrow(() ->
                new TochookpiException(ErrorCode.USER_NOT_FOUND));

        UserSettingEntity userSettingEntity = userSettingRepository.findById(userEntity.getUserSetting().getId()).orElseThrow(() ->
                new TochookpiException(ErrorCode.FAIL_MODIFY_USER_SETTING));

        return new UserSettingDTO(userSettingEntity.isNotificationDisabled(), userSettingEntity.isNotificationDisabled());
    }

    @Override
    public void modifyUserSetting(String loggedInUserEmail, UserSettingDTO userSettingDTO) {
        UserEntity userEntity = userRepository.findByEmail(loggedInUserEmail).orElseThrow(() ->
                new TochookpiException(ErrorCode.USER_NOT_FOUND));

        UserSettingEntity userSettingEntity = new UserSettingEntity(
                userEntity.getUserSetting().getId(),
                userSettingDTO.isNotificationDisabled(),
                userSettingDTO.isInviteDisabled()
        );

        userSettingRepository.save(userSettingEntity);
    }
}
