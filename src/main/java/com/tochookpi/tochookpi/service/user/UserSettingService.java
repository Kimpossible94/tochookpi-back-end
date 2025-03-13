package com.tochookpi.tochookpi.service.user;

import com.tochookpi.tochookpi.dto.user.UserSettingDTO;

public interface UserSettingService {
    UserSettingDTO getUserSetting(String loggedInUserEmail);

    void modifyUserSetting(String loggedInUserEmail, UserSettingDTO userSettingDTO);
}
