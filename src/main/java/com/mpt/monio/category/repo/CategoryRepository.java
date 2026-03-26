package com.mpt.monio.category.repo;

import com.mpt.monio.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("""
        SELECT c FROM Category c
        WHERE c.user.id = :userId AND c.isActive = true
    """)
    List<Category> findAllByUserIdAndIsActiveTrue(Long userId);

    @Query("""
        SELECT c FROM Category c
        WHERE c.id = :id AND c.user.id = :userId AND c.isActive = true
    """)
    Optional<Category> findByIdAndUserIdAndIsActiveTrue(Long id, Long userId);
}
