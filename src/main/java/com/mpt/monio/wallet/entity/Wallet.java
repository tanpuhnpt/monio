package com.mpt.monio.wallet.entity;

import com.mpt.monio.auth.entity.User;
import com.mpt.monio.redis.RedisListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "active_name"}))
@EntityListeners(RedisListener.class)
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Column(precision = 13, scale = 2)
    BigDecimal balance;

    String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    boolean isActive;

    @Column(name = "active_name", insertable = false, updatable = false,
            columnDefinition = "VARCHAR(255) GENERATED ALWAYS AS (CASE WHEN is_active THEN name ELSE NULL END) STORED")
    @Generated
    String activeName;

    @PrePersist
    protected void prePersist() {
        this.isActive = true;
    }
}
