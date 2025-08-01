package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.dto.auth.CustomUserDetails;
import com.tochookpi.tochookpi.dto.user.UserAuthDTO;
import com.tochookpi.tochookpi.dto.user.UserDTO;
import com.tochookpi.tochookpi.dto.user.UserSummaryDTO;
import com.tochookpi.tochookpi.service.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
        String loggedInUserId = customUserDetails.getUsername();
        userService.modifyUserInfo(loggedInUserId, userDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<List<UserSummaryDTO>> getAllUserSummaries(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String loggedInUserId = customUserDetails.getUsername();
        List<UserSummaryDTO> allUserSummaries = userService.getAllUserSummaries(loggedInUserId);
        return ResponseEntity.ok(allUserSummaries);
    }

    @PutMapping("/profile")
    public ResponseEntity<String> modifyUserProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                               @RequestParam("file") MultipartFile multipartFile) {
        String loggedInUserEmail = customUserDetails.getUsername();
        String profileUrl = userService.modifyUserProfile(loggedInUserEmail, multipartFile);
        return ResponseEntity.ok(profileUrl);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           HttpServletResponse response) {
        String loggedInUserEmail = customUserDetails.getUsername();
        userService.deleteUser(loggedInUserEmail);

        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // 즉시 만료
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok().build();
    }
}
