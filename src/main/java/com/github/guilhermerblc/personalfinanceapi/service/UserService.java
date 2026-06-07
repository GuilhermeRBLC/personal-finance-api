package com.github.guilhermerblc.personalfinanceapi.service;

import com.github.guilhermerblc.personalfinanceapi.domain.User;
import com.github.guilhermerblc.personalfinanceapi.dto.LoginRequestDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.UserRequestDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.UserResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO requestDTO) {

        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        User user = User.builder()
                .name(requestDTO.getName())
                .email(requestDTO.getEmail())
                .password(requestDTO.getPassword())
                .build();

        User savedUser = userRepository.save(user);

        return UserResponseDTO.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO authUser(@Valid LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + loginRequestDTO.getEmail()));

        if(!loginRequestDTO.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Wrong password!");
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
