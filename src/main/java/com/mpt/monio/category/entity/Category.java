package com.mpt.monio.category.entity;

import com.mpt.monio.redis.RedisListener;
import com.mpt.monio.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "active_name"}))
@EntityListeners(RedisListener.class)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    boolean isActive;

    @Column(name = "active_name", insertable = false, updatable = false,
            columnDefinition = "VARCHAR(255) GENERATED ALWAYS AS (CASE WHEN is_active THEN name ELSE NULL END) STORED")
    @Generated
    String activeName;

    @PrePersist
    protected void onCreate(){
        this.isActive = true;
    }
}
