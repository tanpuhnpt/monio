package com.mpt.monio.category.service;

import com.mpt.monio.category.dto.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
}
