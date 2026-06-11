package com.github.guilhermerblc.personalfinanceapi.service;

import com.github.guilhermerblc.personalfinanceapi.domain.User;
import com.github.guilhermerblc.personalfinanceapi.dto.LoginRequestDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.LoginResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.UserRequestDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.UserResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.infra.security.TokenService;
import com.github.guilhermerblc.personalfinanceapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should register a user successfully when email is unique")
    void registerUserSuccess() {
        // Arrange
        UserRequestDTO request = new UserRequestDTO("Guilherme", "gui@mail.com", "senha123");
        User savedUser = new User(1L, "Guilherme", "gui@mail.com", "senha_criptografada");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("senha_criptografada");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponseDTO response = userService.registerUser(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("gui@mail.com");

        // Verifica se a segurança agiu criptografando a senha antes de salvar
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email is already registered")
    void registerUserThrowsExceptionWhenEmailExists() {
        // Arrange
        UserRequestDTO request = new UserRequestDTO("Guilherme", "duplicado@mail.com", "senha123");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(request);
        });

        assertThat(exception.getMessage()).isEqualTo("Email already registered!");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return user when ID exists")
    void findByIdSuccess() {
        // Arrange
        Long userId = 1L;
        User user = new User(userId, "Guilherme", "gui@mail.com", "hash");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserResponseDTO response = userService.findById(userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Guilherme");
    }

    @Test
    @DisplayName("Should throw exception when user ID does not exist")
    void findByIdThrowsExceptionWhenUserNotFound() {
        // Arrange
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(userId);
        });

        assertThat(exception.getMessage()).contains("User not found with id: " + userId);
    }

    @Test
    @DisplayName("Should authenticate user and return JWT token successfully")
    void authUserSuccess() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO("gui@mail.com", "senha123");
        User userPrincipal = new User(1L, "Guilherme", "gui@mail.com", "hash");
        String mockToken = "jwt-token-gerado-de-teste";

        // Simulando o comportamento em cadeia do Spring Security:
        // 1. O manager autentica e retorna um objeto Authentication
        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);

        // 2. O objeto Authentication retorna o nosso usuário customizado no getPrincipal()
        when(authenticationMock.getPrincipal()).thenReturn(userPrincipal);

        // 3. O tokenService gera a String do token baseado no usuário
        when(tokenService.generateToken(userPrincipal)).thenReturn(mockToken);

        // Act
        LoginResponseDTO response = userService.authUser(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getUserName()).isEqualTo("Guilherme");
        assertThat(response.getToken()).isEqualTo(mockToken);

        verify(tokenService, times(1)).generateToken(userPrincipal);
    }

}
