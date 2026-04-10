package com.tanpuh.transaction.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(precision = 13, scale = 2)
    BigDecimal amount;

    String note;

    LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    com.tanpuh.common.enums.TransactionType type;

    Long userId;

    Long categoryId;

    Long walletId;

    Long destinationWalletId;
}
