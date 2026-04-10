package com.tanpuh.transaction.repo;

import com.tanpuh.common.enums.TransactionType;
import com.tanpuh.transaction.entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.userId = :userId
        AND t.type IN :types
        AND t.createdAt BETWEEN :startDate AND :endDate
    """)
    List<Transaction> findAllByUserId(
            @Param("userId") Long userId,
            @Param("types") List<TransactionType> types,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Sort sort);

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.id = :id AND t.userId = :userId
    """)
    Optional<Transaction> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Transaction t WHERE t.id = :id AND t.userId = :userId")
    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
