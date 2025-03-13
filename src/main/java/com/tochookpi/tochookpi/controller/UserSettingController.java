package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.dto.auth.CustomUserDetails;
import com.tochookpi.tochookpi.dto.user.UserSettingDTO;
import com.tochookpi.tochookpi.service.user.UserSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-settings")
public class UserSettingController {
    private final UserSettingService userSettingService;

    public UserSettingController(UserSettingService userSettingService) {
        this.userSettingService = userSettingService;
    }

    @GetMapping
    public ResponseEntity<UserSettingDTO> getUserSettings(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserSettingDTO userSetting = userSettingService.getUserSetting(customUserDetails.getUsername());

        return ResponseEntity.ok(userSetting);
    }

    @PutMapping
    public ResponseEntity<Void> modifyUserSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                  @RequestBody UserSettingDTO userSettingDTO) {
        userSettingService.modifyUserSetting(customUserDetails.getUsername(), userSettingDTO);

        return ResponseEntity.ok().build();
    }
}
