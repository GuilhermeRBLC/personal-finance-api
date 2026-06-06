package com.github.guilhermerblc.personalfinanceapi.repository;

import com.github.guilhermerblc.personalfinanceapi.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserId(Integer userId);

}
