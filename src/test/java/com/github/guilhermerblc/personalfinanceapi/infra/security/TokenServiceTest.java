package com.github.guilhermerblc.personalfinanceapi.infra.security;

import com.github.guilhermerblc.personalfinanceapi.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private User user;

    @BeforeEach
    void setUp() {
        // Injetando manualmente o segredo na propriedade privada 'secret' antes de cada teste
        ReflectionTestUtils.setField(tokenService, "secret", "segredo-de-teste-super-seguro-com-mais-de-32-caracteres");

        user = new User(1L, "Guilherme", "gui@mail.com", "senha_hash");
    }

    @Test
    @DisplayName("Should generate a valid JWT token successfully")
    void generateTokenSuccess() {
        // Act
        String token = tokenService.generateToken(user);

        // Assert
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Should validate token and return the correct subject (email)")
    void validateTokenSuccess() {
        // Arrange
        String token = tokenService.generateToken(user);

        // Act
        String subject = tokenService.validateToken(token);

        // Assert
        assertThat(subject).isEqualTo("gui@mail.com");
    }

    @Test
    @DisplayName("Should return an empty string when token is invalid or expired")
    void validateTokenReturnsEmptyStringWhenTokenIsInvalid() {
        // Arrange
        String tokenInvalido = "um-token-completamente-falso-e-mal-formatado";

        // Act
        String subject = tokenService.validateToken(tokenInvalido);

        // Assert
        assertThat(subject).isEmpty();
    }

}
