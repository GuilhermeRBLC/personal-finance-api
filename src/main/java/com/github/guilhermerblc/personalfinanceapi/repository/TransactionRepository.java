package com.github.guilhermerblc.personalfinanceapi.repository;

import com.github.guilhermerblc.personalfinanceapi.domain.Transaction;
import com.github.guilhermerblc.personalfinanceapi.domain.enums.TransactionType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);

    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId ORDER BY t.date DESC")
    List<Transaction> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account.user.id = :userId AND t.type = :type AND t.date BETWEEN :start AND :end")
    BigDecimal sumAmountByTypeAndPeriod(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId AND t.date BETWEEN :start AND :end ORDER BY t.date DESC")
    List<Transaction> findRecentTransactions(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable
    );
}
