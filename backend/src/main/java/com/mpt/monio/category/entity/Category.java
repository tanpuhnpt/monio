package com.mpt.monio.category.entity;

import com.mpt.monio.redis.RedisListener;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(RedisListener.class)
@SQLRestriction("is_active = true")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    boolean isActive;

    @Column(name = "active_name", insertable = false, updatable = false, unique = true,
            columnDefinition = "VARCHAR(255) GENERATED ALWAYS AS (CASE WHEN is_active THEN name ELSE NULL END) STORED")
    @Generated
    String activeName;

    @PrePersist
    protected void prePersist(){
        this.isActive = true;
    }
}
