package com.github.guilhermerblc.personalfinanceapi.dto;

import com.github.guilhermerblc.personalfinanceapi.domain.enums.TransactionType;
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
public class TransactionResponseDTO {
    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private TransactionType type;
    private String category;
    private Long accountId;
}
