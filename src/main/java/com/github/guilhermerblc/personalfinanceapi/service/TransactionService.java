package com.github.guilhermerblc.personalfinanceapi.service;

import com.github.guilhermerblc.personalfinanceapi.domain.Account;
import com.github.guilhermerblc.personalfinanceapi.domain.Transaction;
import com.github.guilhermerblc.personalfinanceapi.domain.enums.TransactionType;
import com.github.guilhermerblc.personalfinanceapi.dto.TransactionRequestDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.TransactionResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.repository.AccountRepository;
import com.github.guilhermerblc.personalfinanceapi.repository.TransactionRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public TransactionResponseDTO createTransaction(Long userId, TransactionRequestDTO requestDTO) {

        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + requestDTO.getAccountId()));

        if(account.getUser().getId() != userId) {
            throw new RuntimeException("Account not found with id: " + requestDTO.getAccountId());
        }

        Transaction transaction = Transaction.builder()
                .description(requestDTO.getDescription())
                .amount(requestDTO.getAmount())
                .date(requestDTO.getDate())
                .type(requestDTO.getType())
                .category(requestDTO.getCategory())
                .account(account)
                .build();

        BigDecimal amount = transaction.getAmount();
        if (transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(amount));
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(amount));
        }

        Transaction savedTransaction = transactionRepository.save(transaction);

        return TransactionResponseDTO.builder()
                .id(savedTransaction.getId())
                .description(savedTransaction.getDescription())
                .amount(savedTransaction.getAmount())
                .date(savedTransaction.getDate())
                .type(savedTransaction.getType())
                .category(savedTransaction.getCategory())
                .accountId(account.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> findTransactionsByUserId(Long userId) {
        return transactionRepository.findAllByUserId(userId)
                .stream()
                .map(transaction -> TransactionResponseDTO.builder()
                        .id(transaction.getId())
                        .description(transaction.getDescription())
                        .amount(transaction.getAmount())
                        .date(transaction.getDate())
                        .type(transaction.getType())
                        .category(transaction.getCategory())
                        .accountId(transaction.getAccount().getId())
                        .build())
                .toList();
    }

    @Transactional
    public boolean deleteTransaction(Long userId, Long id) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        if(!transaction.getAccount().getUser().getId().equals(userId)) {
            throw new RuntimeException("User not found");
        }

        transactionRepository.deleteById(id);
        return true;
    }

    public TransactionResponseDTO updateTransaction(Long userId, Long transactionId, TransactionRequestDTO requestDTO) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));

        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + requestDTO.getAccountId()));

        if(account.getUser().getId() != userId) {
            throw new RuntimeException("Account not found with id: " + requestDTO.getAccountId());
        }

        transaction.setDescription(requestDTO.getDescription());
        transaction.setAmount(requestDTO.getAmount());
        transaction.setDate(requestDTO.getDate());
        transaction.setType(requestDTO.getType());
        transaction.setCategory(requestDTO.getCategory());
        transaction.setAccount(account);

        BigDecimal amount = requestDTO.getAmount();
        if (transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(amount));
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(amount));
        }

        Transaction savedTransaction = transactionRepository.save(transaction);

        return TransactionResponseDTO.builder()
                .id(savedTransaction.getId())
                .description(savedTransaction.getDescription())
                .amount(savedTransaction.getAmount())
                .date(savedTransaction.getDate())
                .type(savedTransaction.getType())
                .category(savedTransaction.getCategory())
                .accountId(account.getId())
                .build();
    }
}
