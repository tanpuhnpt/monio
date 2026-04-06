package com.tanpuh.category.repo;

import com.tanpuh.category.entity.Category;
import com.tanpuh.category.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByType(CategoryType type);
}
