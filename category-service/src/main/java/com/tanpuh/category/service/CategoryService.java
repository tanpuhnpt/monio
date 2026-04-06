package com.tanpuh.category.service;

import com.tanpuh.category.dto.CategoryResponse;
import com.tanpuh.category.entity.CategoryType;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategoriesByType(CategoryType type);
}
