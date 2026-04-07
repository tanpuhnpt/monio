package com.tanpuh.wallet.repo;

import com.tanpuh.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
