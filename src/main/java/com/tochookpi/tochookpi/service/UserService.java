package com.tochookpi.tochookpi.service;

import com.tochookpi.tochookpi.dto.UserAuthDTO;
import com.tochookpi.tochookpi.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO registerUser(UserAuthDTO userAuthDTO);
}
