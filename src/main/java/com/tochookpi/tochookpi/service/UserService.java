package com.tochookpi.tochookpi.service;

import com.tochookpi.tochookpi.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO findById(Long id);
    List<UserDTO> findAllUsers();
}
