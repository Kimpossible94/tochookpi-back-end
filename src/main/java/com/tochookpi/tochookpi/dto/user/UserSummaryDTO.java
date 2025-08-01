package com.tochookpi.tochookpi.dto.user;

import com.tochookpi.tochookpi.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserSummaryDTO {
    private Long id;
    private String username;
    private String profileImage;

    public UserSummaryDTO(UserEntity userEntity) {
        this.id = userEntity.getId();
        this.username = userEntity.getName();
        this.profileImage = userEntity.getProfileImage();
    }
}
