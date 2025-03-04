package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.dto.CustomUserDetails;
import com.tochookpi.tochookpi.dto.UserAuthDTO;
import com.tochookpi.tochookpi.dto.UserDTO;
import com.tochookpi.tochookpi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
}
