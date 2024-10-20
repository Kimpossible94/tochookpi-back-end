package com.tochookpi.tochookpi.controller;

import com.tochookpi.tochookpi.dto.UserAuthDTO;
import com.tochookpi.tochookpi.dto.UserDTO;
import com.tochookpi.tochookpi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
