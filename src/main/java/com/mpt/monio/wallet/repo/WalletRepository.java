package com.mpt.monio.wallet.repo;

import com.mpt.monio.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findAllByUserIdAndIsActiveTrue(Long userId);
    Optional<Wallet> findByIdAndIsActiveTrue(Long id);
    Optional<Wallet> findByIdAndUserIdAndIsActiveTrue(Long id, Long userId);
}
