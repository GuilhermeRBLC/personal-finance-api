package com.github.guilhermerblc.personalfinanceapi.dto;

import com.github.guilhermerblc.personalfinanceapi.domain.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDTO {

    @NotNull(message = "Description is required")
    @Size(min = 3, max = 150)
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Transaction type (INCOME/EXPENSE) is required")
    private TransactionType type;

    private String category;

    @NotNull(message = "Account ID is required")
    private Long accountId;

}
