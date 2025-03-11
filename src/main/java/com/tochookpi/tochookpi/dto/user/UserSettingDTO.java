package com.tochookpi.tochookpi.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserSettingDTO {
    @JsonProperty("isNotificationDisabled")
    private boolean isNotificationDisabled;

    @JsonProperty("isInviteDisabled")
    private boolean isInviteDisabled;
}
