package com.github.guilhermerblc.personalfinanceapi.controller;

import com.github.guilhermerblc.personalfinanceapi.domain.User;
import com.github.guilhermerblc.personalfinanceapi.dto.AccountRequestDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.AccountResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> create(@Valid @RequestBody AccountRequestDTO requestDTO, @AuthenticationPrincipal User logUser) {
        AccountResponseDTO responseDTO = accountService.createAccount(logUser.getId(), requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/user")
    public ResponseEntity<List<AccountResponseDTO>> getByUser(@AuthenticationPrincipal User logUser) {
        List<AccountResponseDTO> transactions = accountService.findAccountsByUserId(logUser.getId());
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<AccountResponseDTO> update(@PathVariable Long accountId, @Valid @RequestBody AccountRequestDTO requestDTO, @AuthenticationPrincipal User logUser) {
        AccountResponseDTO responseDTO = accountService.updateAccount(logUser.getId(), accountId, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Boolean> delete(@PathVariable Long accountId, @AuthenticationPrincipal User logUser) {
        accountService.deleteAccount(logUser.getId(), accountId);
        return ResponseEntity.noContent().build();
    }

}
