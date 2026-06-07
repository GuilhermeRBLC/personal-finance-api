package com.github.guilhermerblc.personalfinanceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponseDTO {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal currentBalance;
    private List<TransactionResponseDTO> recentTransactions;
}
