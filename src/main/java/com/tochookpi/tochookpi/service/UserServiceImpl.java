package com.tochookpi.tochookpi.service;

import com.tochookpi.tochookpi.dto.UserAuthDTO;
import com.tochookpi.tochookpi.dto.UserDTO;
import com.tochookpi.tochookpi.entity.User;
import com.tochookpi.tochookpi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO registerUser(UserAuthDTO userAuthDTO) {
        String encodedPassword = passwordEncoder.encode(userAuthDTO.getPassword());

        User user = new User(userAuthDTO.getUsername(), userAuthDTO.getEmail(), encodedPassword);
        User saveduser = userRepository.save(user);

        return new UserDTO(saveduser.getId(), saveduser.getUsername(), saveduser.getEmail());
    }
}
