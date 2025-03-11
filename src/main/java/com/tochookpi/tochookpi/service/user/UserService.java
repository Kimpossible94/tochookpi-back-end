package com.tochookpi.tochookpi.service.user;

import com.tochookpi.tochookpi.dto.user.UserAuthDTO;
import com.tochookpi.tochookpi.dto.user.UserDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserDTO registerUser(UserAuthDTO userAuthDTO);

    UserDTO getUserInfo(String email);

    void modifyUserInfo(String loggedInUserEmail, UserDTO userDTO);

    String modifyUserProfile(String loggedInUserEmail, MultipartFile multipartFile);
}
