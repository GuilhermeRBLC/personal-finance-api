package com.github.guilhermerblc.personalfinanceapi.service;

import com.github.guilhermerblc.personalfinanceapi.domain.Account;
import com.github.guilhermerblc.personalfinanceapi.domain.Transaction;
import com.github.guilhermerblc.personalfinanceapi.domain.User;
import com.github.guilhermerblc.personalfinanceapi.domain.enums.TransactionType;
import com.github.guilhermerblc.personalfinanceapi.dto.TransactionRequestDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.TransactionResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.repository.AccountRepository;
import com.github.guilhermerblc.personalfinanceapi.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Should create an INCOME transaction and increase account balance")
    void createIncomeTransactionSuccess() {
        // Arrange
        Long userId = 1L;
        Long accountId = 10L;

        User user = new User(userId, "Thiago", "thiago@mail.com", "123456");
        Account account = new Account(accountId, "Corrente", new BigDecimal("1000.00"), user);

        TransactionRequestDTO requestDTO = new TransactionRequestDTO(
                "Salário", new BigDecimal("3500.00"), LocalDate.now(), TransactionType.INCOME, "Trabalho", accountId
        );

        Transaction savedTransaction = Transaction.builder()
                .id(100L)
                .description("Salário")
                .amount(new BigDecimal("3500.00"))
                .date(LocalDate.now())
                .type(TransactionType.INCOME)
                .category("Trabalho")
                .account(account)
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // Act
        TransactionResponseDTO response = transactionService.createTransaction(userId, requestDTO);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);

        // Regra de Negócio Crucial: O saldo antigo era 1000.00 + 3500.00 do salário = 4500.00
        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("4500.00"));

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should create an EXPENSE transaction and decrease account balance")
    void createExpenseTransactionSuccess() {
        // Arrange
        Long userId = 1L;
        Long accountId = 10L;

        User user = new User(userId, "Thiago", "thiago@mail.com", "123456");
        Account account = new Account(accountId, "Corrente", new BigDecimal("1000.00"), user);

        TransactionRequestDTO requestDTO = new TransactionRequestDTO(
                "Mercado", new BigDecimal("250.00"), LocalDate.now(), TransactionType.EXPENSE, "Alimentação", accountId
        );

        Transaction savedTransaction = Transaction.builder()
                .id(101L)
                .description("Mercado")
                .amount(new BigDecimal("250.00"))
                .date(LocalDate.now())
                .type(TransactionType.EXPENSE)
                .account(account)
                .build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        // Act
        transactionService.createTransaction(userId, requestDTO);

        // Assert
        // Regra de Negócio Crucial: 1000.00 - 250.00 = 750.00
        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("750.00"));
    }

    @Test
    @DisplayName("Should throw exception when account does not belong to the logged user")
    void createTransactionThrowsExceptionWhenAccountBelongsToOtherUser() {
        // Arrange
        Long loggedUserId = 1L;
        Long strangerUserId = 99L;
        Long accountId = 10L;

        User stranger = new User(strangerUserId, "Estranho", "estranho@mail.com", "123");
        Account account = new Account(accountId, "Conta Alheia", new BigDecimal("500.00"), stranger);

        TransactionRequestDTO requestDTO = new TransactionRequestDTO(
                "Ataque", new BigDecimal("10.00"), LocalDate.now(), TransactionType.EXPENSE, "Hack", accountId
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.createTransaction(loggedUserId, requestDTO);
        });

        assertThat(exception.getMessage()).contains("Account not found with id: " + accountId);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should return a list of transactions for the user")
    void findTransactionsByUserIdSuccess() {
        // Arrange
        Long userId = 1L;
        Account account = new Account(10L, "Carteira", BigDecimal.ZERO, null);
        Transaction t1 = new Transaction(100L, "Freela", new BigDecimal("500.00"), LocalDate.now(), TransactionType.INCOME, "Job", account);

        when(transactionRepository.findAllByUserId(userId)).thenReturn(List.of(t1));

        // Act
        List<TransactionResponseDTO> response = transactionService.findTransactionsByUserId(userId);

        // Assert
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(0).getDescription()).isEqualTo("Freela");
    }

    @Test
    @DisplayName("Should delete transaction successfully when it belongs to the user")
    void deleteTransactionSuccess() {
        // Arrange
        Long userId = 1L;
        Long transactionId = 100L;

        User user = new User(userId, "Thiago", "thiago@mail.com", "123456");
        Account account = new Account(10L, "Carteira", BigDecimal.ZERO, user);
        Transaction transaction = new Transaction(transactionId, "Lanche", new BigDecimal("30.00"), LocalDate.now(), TransactionType.EXPENSE, "Food", account);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        // Act
        boolean result = transactionService.deleteTransaction(userId, transactionId);

        // Assert
        assertThat(result).isTrue();
        verify(transactionRepository, times(1)).deleteById(transactionId);
    }

    @Test
    @DisplayName("Should throw exception when trying to delete another user's transaction")
    void deleteTransactionThrowsExceptionWhenNotOwner() {
        // Arrange
        Long loggedUserId = 1L;
        Long hackerUserId = 2L;
        Long transactionId = 100L;

        User owner = new User(loggedUserId, "Thiago", "thiago@mail.com", "123456");
        Account account = new Account(10L, "Carteira", BigDecimal.ZERO, owner);
        Transaction transaction = new Transaction(transactionId, "Lanche", new BigDecimal("30.00"), LocalDate.now(), TransactionType.EXPENSE, "Food", account);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.deleteTransaction(hackerUserId, transactionId);
        });

        assertThat(exception.getMessage()).isEqualTo("User not found");
        verify(transactionRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should update transaction and update account balance successfully")
    void updateTransactionSuccess() {
        // Arrange
        Long userId = 1L;
        Long transactionId = 100L;
        Long accountId = 10L;

        User user = new User(userId, "Thiago", "thiago@mail.com", "123456");
        Account account = new Account(accountId, "Corrente", new BigDecimal("1000.00"), user);

        Transaction existingTransaction = new Transaction(transactionId, "Antiga", new BigDecimal("50.00"), LocalDate.now(), TransactionType.INCOME, "Diverso", account);
        TransactionRequestDTO updateRequest = new TransactionRequestDTO("Nova Descrição", new BigDecimal("200.00"), LocalDate.now(), TransactionType.INCOME, "Outros", accountId);

        Transaction updatedTransaction = new Transaction(transactionId, "Nova Descrição", new BigDecimal("200.00"), LocalDate.now(), TransactionType.INCOME, "Outros", account);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingTransaction));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(updatedTransaction);

        // Act
        TransactionResponseDTO response = transactionService.updateTransaction(userId, transactionId, updateRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getDescription()).isEqualTo("Nova Descrição");

        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("1200.00"));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

}
