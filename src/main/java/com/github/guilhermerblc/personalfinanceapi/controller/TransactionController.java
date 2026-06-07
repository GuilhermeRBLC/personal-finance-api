package com.github.guilhermerblc.personalfinanceapi.controller;

import com.github.guilhermerblc.personalfinanceapi.dto.TransactionRequestDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.TransactionResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TransactionResponseDTO> create(@Valid @RequestBody TransactionRequestDTO requestDTO) {
        TransactionResponseDTO responseDTO = transactionService.createTransaction(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponseDTO>> getByUserId(@PathVariable Long userId) {
        List<TransactionResponseDTO> transactions = transactionService.findTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

}
