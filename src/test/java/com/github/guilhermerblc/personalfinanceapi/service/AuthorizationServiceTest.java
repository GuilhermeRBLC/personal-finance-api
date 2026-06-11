package com.github.guilhermerblc.personalfinanceapi.service;

import com.github.guilhermerblc.personalfinanceapi.domain.User;
import com.github.guilhermerblc.personalfinanceapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    @DisplayName("Should return UserDetails when user exists with the given email")
    void loadUserByUsernameSuccess() {
        // Arrange
        String email = "gui@mail.com";
        User user = new User(1L, "Guilherme", email, "hash_senha");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails result = authorizationService.loadUserByUsername(email);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(email);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when email does not exist")
    void loadUserByUsernameThrowsExceptionWhenUserNotFound() {
        // Arrange
        String email = "nao_existe@mail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            authorizationService.loadUserByUsername(email);
        });

        assertThat(exception.getMessage()).isEqualTo("User not found");
    }

}
