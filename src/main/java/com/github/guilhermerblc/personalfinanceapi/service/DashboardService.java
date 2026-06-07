package com.github.guilhermerblc.personalfinanceapi.service;

import com.github.guilhermerblc.personalfinanceapi.domain.Transaction;
import com.github.guilhermerblc.personalfinanceapi.domain.enums.TransactionType;
import com.github.guilhermerblc.personalfinanceapi.dto.DashboardResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.TransactionResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.repository.AccountRepository;
import com.github.guilhermerblc.personalfinanceapi.repository.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public DashboardService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponseDTO summary(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        BigDecimal totalIncome = transactionRepository
                .sumAmountByTypeAndPeriod(userId, TransactionType.INCOME, firstDayOfMonth, lastDayOfMonth);
        BigDecimal totalExpense = transactionRepository
                .sumAmountByTypeAndPeriod(userId, TransactionType.EXPENSE, firstDayOfMonth, lastDayOfMonth);

        totalIncome = (totalIncome != null) ? totalIncome : BigDecimal.ZERO;
        totalExpense = (totalExpense != null) ? totalExpense : BigDecimal.ZERO;

        BigDecimal currentBalance = totalIncome.subtract(totalExpense);

        var recentTransactions = transactionRepository.findRecentTransactions(
                userId,
                firstDayOfMonth,
                lastDayOfMonth,
                PageRequest.of(0, 3) // Limita a página 0 com 3 itens
        );
        var recentTransactionsDTO = recentTransactions.stream().map(
                t -> TransactionResponseDTO.builder()
                        .id(t.getId())
                        .amount(t.getAmount())
                        .date(t.getDate())
                        .type(t.getType())
                        .category(t.getCategory())
                        .description(t.getDescription())
                        .accountId(t.getAccount().getId())
                        .build()
        ).toList();

        return DashboardResponseDTO.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .currentBalance(currentBalance)
                .recentTransactions(recentTransactionsDTO)
                .build();
    }

}
