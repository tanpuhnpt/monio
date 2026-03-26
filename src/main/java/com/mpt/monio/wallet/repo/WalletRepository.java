package com.mpt.monio.wallet.repo;

import com.mpt.monio.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query("""
        SELECT w FROM Wallet w
        WHERE w.user.id = :userId AND w.isActive = true
    """)
    List<Wallet> findAllByUserIdAndIsActiveTrue(Long userId);

    @Query("""
        SELECT w FROM Wallet w
        WHERE w.id = :id AND w.user.id = :userId AND w.isActive = true
    """)
    Optional<Wallet> findByIdAndUserIdAndIsActiveTrue(Long id, Long userId);
}
