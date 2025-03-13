package com.tochookpi.tochookpi.dto.user;

import com.tochookpi.tochookpi.entity.UserEntity;
import com.tochookpi.tochookpi.entity.UserSettingEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserDTO {
    private String username;
    private String email;
    private String profileImage;
    private String bio;
    private String address;
    private UserSettingDTO userSetting;

    public UserDTO(UserEntity userEntity) {
        this.username = userEntity.getName();
        this.email = userEntity.getEmail();
        this.profileImage = userEntity.getProfileImage();
        this.bio = userEntity.getBio();
        this.address = userEntity.getAddress();

        UserSettingEntity setting = userEntity.getUserSetting();
        if (setting != null) {
            this.userSetting = new UserSettingDTO(setting.isNotificationDisabled(), setting.isInviteDisabled());
        }
    }
}
