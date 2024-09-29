package com.tochookpi.tochookpi.service;

import com.tochookpi.tochookpi.dto.UserDTO;
import com.tochookpi.tochookpi.entity.User;
import com.tochookpi.tochookpi.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO findById(Long id) {
        User userEntity = userRepository.findById(id).get();
        UserDTO userDTO = new UserDTO(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), userEntity.getPassword());
        return userDTO;
    }
}
