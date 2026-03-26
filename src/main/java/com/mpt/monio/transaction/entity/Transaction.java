package com.mpt.monio.transaction.entity;

import com.mpt.monio.auth.entity.User;
import com.mpt.monio.category.entity.Category;
import com.mpt.monio.redis.RedisListener;
import com.mpt.monio.wallet.entity.Wallet;
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
@EntityListeners(RedisListener.class)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(precision = 13, scale = 2)
    BigDecimal amount;

    String note;

    LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    Wallet wallet;
}
