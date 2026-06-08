package com.github.guilhermerblc.personalfinanceapi.controller;

import com.github.guilhermerblc.personalfinanceapi.dto.LoginRequestDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.LoginResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.UserRequestDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.UserResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.service.AuthorizationService;
import com.github.guilhermerblc.personalfinanceapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthorizationService authorizationService;

    public UserController(UserService userService, AuthorizationService authorizationService) {
        this.userService = userService;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO responseDTO = userService.authUser(loginRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO responseDTO = userService.registerUser(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

}
