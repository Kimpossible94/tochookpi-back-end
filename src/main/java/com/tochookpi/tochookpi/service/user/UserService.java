package com.tochookpi.tochookpi.service.user;

import com.tochookpi.tochookpi.dto.user.UserAuthDTO;
import com.tochookpi.tochookpi.dto.user.UserDTO;
import com.tochookpi.tochookpi.dto.user.UserSummaryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserDTO registerUser(UserAuthDTO userAuthDTO);

    UserDTO getUserInfo(String id);

    void modifyUserInfo(String loggedInUserId, UserDTO userDTO);

    List<UserSummaryDTO> getAllUserSummaries(String loggedInUserId);

    String modifyUserProfile(String loggedInUserId, MultipartFile multipartFile);

    void deleteUser(String loggedInUserId);
}
