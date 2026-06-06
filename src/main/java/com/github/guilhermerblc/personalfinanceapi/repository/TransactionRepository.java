package com.github.guilhermerblc.personalfinanceapi.repository;

import com.github.guilhermerblc.personalfinanceapi.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByAccountId(Integer accountId);

    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId ORDER BY t.date DESC")
    List<Transaction> findAllByUserId(@Param("userId") Integer userId);
    
}
