package com.github.guilhermerblc.personalfinanceapi.service;

import com.github.guilhermerblc.personalfinanceapi.domain.Account;
import com.github.guilhermerblc.personalfinanceapi.domain.Transaction;
import com.github.guilhermerblc.personalfinanceapi.domain.enums.TransactionType;
import com.github.guilhermerblc.personalfinanceapi.repository.AccountRepository;
import com.github.guilhermerblc.personalfinanceapi.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction, Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        BigDecimal amount = transaction.getAmount();
        if (transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(amount));
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(amount));
        }

        transaction.setAccount(account);

        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<Transaction> findTransactionsByUserId(Long userId) {
        return transactionRepository.findAllByUserId(userId);
    }

}
