package com.mpt.monio.category.repo;

import com.mpt.monio.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUserIdAndIsActiveTrue(Long userId);
    Optional<Category> findByIdAndUserIdAndIsActiveTrue(Long id, Long userId);
}
