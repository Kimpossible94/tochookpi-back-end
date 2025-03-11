package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.dto.auth.CustomUserDetails;
import com.tochookpi.tochookpi.dto.user.UserAuthDTO;
import com.tochookpi.tochookpi.dto.user.UserDTO;
import com.tochookpi.tochookpi.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserAuthDTO userAuthDTO) {
        UserDTO createdUser = userService.registerUser(userAuthDTO);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping
    public ResponseEntity<UserDTO> getUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserDTO userDTO = userService.getUserInfo(customUserDetails.getUsername());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping
    public ResponseEntity<Void> modifyUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                               @RequestBody UserDTO userDTO) {
        String loggedInUserEmail = customUserDetails.getUsername();
        userService.modifyUserInfo(loggedInUserEmail, userDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<String> modifyUserProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                               @RequestParam("file") MultipartFile multipartFile) {
        String loggedInUserEmail = customUserDetails.getUsername();
        String profileUrl = userService.modifyUserProfile(loggedInUserEmail, multipartFile);
        return ResponseEntity.ok(profileUrl);
    }
}
