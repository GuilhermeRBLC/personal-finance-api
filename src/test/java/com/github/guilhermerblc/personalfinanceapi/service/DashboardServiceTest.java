package com.github.guilhermerblc.personalfinanceapi.service;

import com.github.guilhermerblc.personalfinanceapi.domain.Account;
import com.github.guilhermerblc.personalfinanceapi.domain.Transaction;
import com.github.guilhermerblc.personalfinanceapi.domain.enums.TransactionType;
import com.github.guilhermerblc.personalfinanceapi.dto.DashboardResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DashboardServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("Should generate dashboard summary successfully when data exists")
    void summarySuccessWhenDataExists() {
        // Arrange
        Long userId = 1L;
        BigDecimal incomeValue = new BigDecimal("5000.00");
        BigDecimal expenseValue = new BigDecimal("2000.00");

        Account account = new Account(10L, "Carteira", BigDecimal.ZERO, null);
        Transaction recentTx = new Transaction(100L, "Internet", new BigDecimal("150.00"), LocalDate.now(), TransactionType.EXPENSE, "Contas", account);

        when(transactionRepository.sumAmountByTypeAndPeriod(eq(userId), eq(TransactionType.INCOME), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(incomeValue);
        when(transactionRepository.sumAmountByTypeAndPeriod(eq(userId), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expenseValue);
        when(transactionRepository.findRecentTransactions(eq(userId), any(LocalDate.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(List.of(recentTx));

        // Act
        DashboardResponseDTO summary = dashboardService.summary(userId);

        // Assert
        assertThat(summary).isNotNull();
        assertThat(summary.getTotalIncome()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(summary.getTotalExpense()).isEqualByComparingTo(new BigDecimal("2000.00"));

        // Valida a regra de negócio: 5000 - 2000 = 3000
        assertThat(summary.getCurrentBalance()).isEqualByComparingTo(new BigDecimal("3000.00"));

        assertThat(summary.getRecentTransactions().size()).isEqualTo(1);
        assertThat(summary.getRecentTransactions().get(0).getDescription()).isEqualTo("Internet");
    }

    @Test
    @DisplayName("Should handle null values safely and return zeros when database has no transactions")
    void summaryHandlesNullValuesSafely() {
        // Arrange
        Long userId = 1L;

        when(transactionRepository.sumAmountByTypeAndPeriod(eq(userId), eq(TransactionType.INCOME), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);
        when(transactionRepository.sumAmountByTypeAndPeriod(eq(userId), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);
        when(transactionRepository.findRecentTransactions(eq(userId), any(LocalDate.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        // Act
        DashboardResponseDTO summary = dashboardService.summary(userId);

        // Assert
        assertThat(summary).isNotNull();

        assertThat(summary.getTotalIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getTotalExpense()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getCurrentBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(summary.getRecentTransactions().isEmpty()).isTrue();
    }

}
