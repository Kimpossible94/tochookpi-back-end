package com.tochookpi.tochookpi.service;

import com.tochookpi.tochookpi.dto.UserDTO;

public interface UserService {
    public UserDTO findById(Long id);
}
