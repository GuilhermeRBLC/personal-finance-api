package com.github.guilhermerblc.personalfinanceapi.dto;

import com.github.guilhermerblc.personalfinanceapi.domain.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequestDTO {

    @NotNull(message = "Name is required")
    @Size(min = 3, max = 150)
    private String name;

    @NotNull(message = "Balance is required")
    @Positive(message = "Balance must be greater than zero")
    private BigDecimal balance;

    @NotNull(message = "User ID is required")
    private Long userId;
}
