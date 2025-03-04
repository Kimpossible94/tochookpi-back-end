package com.tochookpi.tochookpi.service;

import com.tochookpi.tochookpi.dto.UserAuthDTO;
import com.tochookpi.tochookpi.dto.UserDTO;

public interface UserService {

    UserDTO registerUser(UserAuthDTO userAuthDTO);

    UserDTO getUserInfo(String email);

    void modifyUserInfo(String loggedInUserEmail, UserDTO userDTO);
}
