package com.github.guilhermerblc.personalfinanceapi.service;

import com.github.guilhermerblc.personalfinanceapi.domain.Account;
import com.github.guilhermerblc.personalfinanceapi.domain.User;
import com.github.guilhermerblc.personalfinanceapi.dto.AccountRequestDTO;
import com.github.guilhermerblc.personalfinanceapi.dto.AccountResponseDTO;
import com.github.guilhermerblc.personalfinanceapi.repository.AccountRepository;
import com.github.guilhermerblc.personalfinanceapi.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AccountResponseDTO createAccount(Long userId, AccountRequestDTO accountRequestDTO) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = Account.builder()
                .name(accountRequestDTO.getName())
                .balance(accountRequestDTO.getBalance())
                .user(user)
                .build();

        Account savedAccount = accountRepository.save(account);

        return AccountResponseDTO.builder()
                .id(savedAccount.getId())
                .name(savedAccount.getName())
                .balance(savedAccount.getBalance())
                .build();

    }

    @Transactional(readOnly = true)
    public List<AccountResponseDTO> findAccountsByUserId(Long userId) {
        return accountRepository.findAllByUserId(userId)
                .stream()
                .map(account -> AccountResponseDTO.builder()
                        .id(account.getId())
                        .name(account.getName())
                        .balance(account.getBalance())
                        .build())
                .toList();
    }

    @Transactional
    public boolean deleteAccount(Long userId, Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if(!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("User not found");
        }

        accountRepository.deleteById(accountId);
        return true;
    }

    @Transactional
    public AccountResponseDTO updateAccount(Long userId, Long accountId, @Valid AccountRequestDTO requestDTO) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if(!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("User not found");
        }

        account.setName(requestDTO.getName());
        account.setBalance(requestDTO.getBalance());

        Account savedAccount = accountRepository.save(account);

        return AccountResponseDTO.builder()
                .id(savedAccount.getId())
                .name(savedAccount.getName())
                .balance(savedAccount.getBalance())
                .build();
    }
}
