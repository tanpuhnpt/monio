package com.mpt.monio.category.repo;

import com.mpt.monio.category.entity.Category;
import com.mpt.monio.category.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByType(CategoryType type);
}
