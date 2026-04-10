package com.tanpuh.wallet.repo;

import com.tanpuh.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query("""
        SELECT w FROM Wallet w
        WHERE w.userId = :userId AND w.isActive = true
    """)
    List<Wallet> findAllByUserIdAndIsActiveTrue(Long userId);

    @Query("""
        SELECT w FROM Wallet w
        WHERE w.id = :id AND w.userId = :userId AND w.isActive = true
    """)
    Optional<Wallet> findByIdAndUserIdAndIsActiveTrue(Long id, Long userId);

    List<Wallet> findAllByIdIn(List<Long> ids);

    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance - :amount WHERE w.id = :id")
    void decreaseBalance(Long id, BigDecimal amount);

    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance + :amount WHERE w.id = :id")
    void increaseBalance(Long id, BigDecimal amount);
}
