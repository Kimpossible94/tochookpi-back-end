package com.tochookpi.tochookpi.service;

import com.tochookpi.tochookpi.dto.UserAuthDTO;
import com.tochookpi.tochookpi.dto.UserDTO;
import com.tochookpi.tochookpi.entity.User;
import com.tochookpi.tochookpi.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO registerUser(UserAuthDTO userAuthDTO) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(userAuthDTO.getPassword());

        User user = new User(userAuthDTO.getUsername(), userAuthDTO.getEmail(), encodedPassword);
        User saveduser = userRepository.save(user);

        return new UserDTO(saveduser.getId(), saveduser.getUsername(), saveduser.getEmail());
    }
}
