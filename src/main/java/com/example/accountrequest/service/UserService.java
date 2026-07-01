package com.example.accountrequest.service;

import com.example.accountrequest.dto.UserResponseDto;
import com.example.accountrequest.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDto(user.getId(), user.getName(), user.getEmail(), user.getRole()))
                .toList();
    }
}