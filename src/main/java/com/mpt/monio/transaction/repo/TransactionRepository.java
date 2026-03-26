package com.mpt.monio.transaction.repo;

import com.mpt.monio.report.dto.ReportSummaryResponse;
import com.mpt.monio.transaction.entity.Transaction;
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
        JOIN FETCH t.category
        JOIN FETCH t.wallet
        WHERE t.user.id = :userId
        AND t.createdAt BETWEEN :startDate AND :endDate
    """)
    List<Transaction> findAllByUserId(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Sort sort);

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
    void deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("""
        SELECT new com.mpt.monio.report.dto.ReportSummaryResponse(
            COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0)
        )
        FROM Transaction t
        WHERE t.user.id = :userId
        AND t.createdAt BETWEEN :startDate AND :endDate
        """)
    ReportSummaryResponse getReportSummary(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
