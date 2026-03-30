package com.mpt.monio.category.service;

import com.mpt.monio.category.dto.CategoryResponse;
import com.mpt.monio.category.entity.CategoryType;
import com.mpt.monio.category.mapper.CategoryMapper;
import com.mpt.monio.category.repo.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getAllCategories(CategoryType type) {
        return categoryRepository
                .findAllByType(type)
                .stream().map(categoryMapper::toResponse)
                .toList();
    }
}
