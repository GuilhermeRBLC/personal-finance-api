package com.github.guilhermerblc.personalfinanceapi.service;

import com.github.guilhermerblc.personalfinanceapi.domain.Account;
import com.github.guilhermerblc.personalfinanceapi.domain.User;
import com.github.guilhermerblc.personalfinanceapi.dto.AccountRequestDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.AccountResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.repository.AccountRepository;
import com.github.guilhermerblc.personalfinanceapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("Should create a account successfully for a valid user")
    void createAccountSuccess() {
        // Arrange
        Long userId = 10L;
        BigDecimal amount = new BigDecimal("123.45");
        AccountRequestDTO accountRequestDTO =  new AccountRequestDTO("Corrente", amount);
        User user = new User(10L, "Thiago", "thiago@mail.com", "123456");
        Account account = Account.builder()
                .name(accountRequestDTO.getName())
                .balance(accountRequestDTO.getBalance())
                .user(user)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        AccountResponseDTO responseDTO = accountService.createAccount(userId, accountRequestDTO);

        // Assert
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getBalance()).isEqualByComparingTo(amount);
        assertThat(responseDTO.getName()).isEqualTo("Corrente");

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when the user doesn't exists")
    void createAccountThrowsExceptionWhenUserNotFound() {
        // Arrange
        Long userId = 15L;
        BigDecimal amount = new BigDecimal("123.45");
        AccountRequestDTO accountRequestDTO =  new AccountRequestDTO("Poupança", amount);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            AccountResponseDTO responseDTO = accountService.createAccount(userId, accountRequestDTO);
        });

        assertThat(exception.getMessage()).isEqualTo("User not found");

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should return a list of accounts for a specific user")
    void findAccountsByUserIdSuccess() {
        // Arrange
        Long userId = 1L;
        User user = new User(userId, "Thiago", "thiago@mail.com", "123456");
        Account acc1 = new Account(10L, "Poupança", new BigDecimal("1000.00"), user);
        Account acc2 = new Account(11L, "Corrente", new BigDecimal("150.00"), user);

        when(accountRepository.findAllByUserId(userId)).thenReturn(List.of(acc1, acc2));

        // Act
        List<AccountResponseDTO> response = accountService.findAccountsByUserId(userId);

        // Assert
        assertThat(response.size()).isEqualTo(2);
        assertThat(response.get(0).getName()).isEqualTo("Poupança");
        assertThat(response.get(1).getName()).isEqualTo("Corrente");
    }

    @Test
    @DisplayName("Should return empty list when user has no accounts")
    void findAccountsByUserIdReturnsEmptyList() {
        // Arrange
        Long userId = 1L;
        when(accountRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<AccountResponseDTO> response = accountService.findAccountsByUserId(userId);

        // Assert
        assertThat(response.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Should delete account successfully when account exists and belongs to the user")
    void deleteAccountSuccess() {
        // Arrange
        Long userId = 1L;
        Long accountId = 10L;
        User user = new User(userId, "Thiago", "thiago@mail.com", "123456");
        Account account = new Account(accountId, "Investimentos", new BigDecimal("5000.00"), user);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act
        boolean result = accountService.deleteAccount(userId, accountId);

        // Assert
        assertThat(result).isTrue();
        verify(accountRepository, times(1)).deleteById(accountId);
    }

    @Test
    @DisplayName("Should throw exception when trying to delete non-existing account")
    void deleteAccountThrowsExceptionWhenAccountNotFound() {
        // Arrange
        Long userId = 1L;
        Long accountId = 99L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.deleteAccount(userId, accountId);
        });

        assertThat(exception.getMessage()).isEqualTo("Account not found");
        verify(accountRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when user tries to delete an account that belongs to someone else")
    void deleteAccountThrowsExceptionWhenAccountBelongsToOtherUser() {
        // Arrange
        Long loggedUserId = 1L;
        Long hackerUserId = 2L;
        Long accountId = 10L;

        User ownerUser = new User(loggedUserId, "Thiago", "thiago@mail.com", "123456");
        Account account = new Account(accountId, "Conta Secreta", new BigDecimal("100.00"), ownerUser);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.deleteAccount(hackerUserId, accountId);
        });

        assertThat(exception.getMessage()).isEqualTo("User not found");
        verify(accountRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should update account successfully when details are valid and user owns it")
    void updateAccountSuccess() {
        // Arrange
        Long userId = 1L;
        Long accountId = 10L;
        AccountRequestDTO updateRequest = new AccountRequestDTO("Novo Nome", new BigDecimal("600.00"));

        User user = new User(userId, "Thiago", "thiago@mail.com", "123456");
        Account existingAccount = new Account(accountId, "Nome Antigo", new BigDecimal("100.00"), user);
        Account updatedAccount = new Account(accountId, "Novo Nome", new BigDecimal("600.00"), user);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        // Act
        AccountResponseDTO response = accountService.updateAccount(userId, accountId, updateRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Novo Nome");
        assertThat(response.getBalance()).isEqualByComparingTo(new BigDecimal("600.00"));

        assertThat(existingAccount.getName()).isEqualTo("Novo Nome");
        assertThat(existingAccount.getBalance()).isEqualByComparingTo(new BigDecimal("600.00"));
    }

    @Test
    @DisplayName("Should throw exception when user tries to update an account that belongs to someone else")
    void updateAccountThrowsExceptionWhenAccountBelongsToOtherUser() {
        // Arrange
        Long loggedUserId = 1L;
        Long hackerUserId = 2L;
        Long accountId = 10L;
        AccountRequestDTO updateRequest = new AccountRequestDTO("Invasão", new BigDecimal("0.00"));

        User ownerUser = new User(loggedUserId, "Thiago", "thiago@mail.com", "123456");
        Account account = new Account(accountId, "Conta Protegida", new BigDecimal("100.00"), ownerUser);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.updateAccount(hackerUserId, accountId, updateRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("User not found");
        verify(accountRepository, never()).save(any(Account.class));
    }

}
