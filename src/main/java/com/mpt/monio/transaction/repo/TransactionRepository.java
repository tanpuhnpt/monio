package com.mpt.monio.transaction.repo;

import com.mpt.monio.transaction.entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("""
        SELECT t FROM Transaction t
        JOIN FETCH t.category
        JOIN FETCH t.wallet
        WHERE t.user.id = :userId
    """)
    List<Transaction> findAllByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT t FROM Transaction t
        JOIN FETCH t.category
        JOIN FETCH t.wallet
        WHERE t.id = :id AND t.user.id = :userId
    """)
    Optional<Transaction> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Transaction t WHERE t.id = :id AND t.user.id = :userId")
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
