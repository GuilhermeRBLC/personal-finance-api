package com.github.guilhermerblc.personalfinanceapi.controller;

import com.github.guilhermerblc.personalfinanceapi.domain.User;
import com.github.guilhermerblc.personalfinanceapi.dto.TransactionRequestDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.TransactionResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create(@Valid @RequestBody TransactionRequestDTO requestDTO, @AuthenticationPrincipal User logUser) {
        TransactionResponseDTO responseDTO = transactionService.createTransaction(logUser.getId(), requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/user")
    public ResponseEntity<List<TransactionResponseDTO>> getByUser(@AuthenticationPrincipal User logUser) {
        List<TransactionResponseDTO> transactions = transactionService.findTransactionsByUserId(logUser.getId());
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> update(@PathVariable Long transactionId, @Valid @RequestBody TransactionRequestDTO requestDTO, @AuthenticationPrincipal User logUser) {
        TransactionResponseDTO responseDTO = transactionService.updateTransaction(logUser.getId(), transactionId, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Boolean> delete(@PathVariable Long transactionId, @AuthenticationPrincipal User logUser) {
        transactionService.deleteTransaction(logUser.getId(), transactionId);
        return ResponseEntity.noContent().build();
    }

}
