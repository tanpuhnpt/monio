package com.tanpuh.category.service;

import com.tanpuh.category.dto.CategoryResponse;
import com.tanpuh.category.entity.CategoryType;
import com.tanpuh.category.mapper.CategoryMapper;
import com.tanpuh.category.repo.CategoryRepository;
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
    CategoryRepository repository;
    CategoryMapper mapper;

    @Override
    public List<CategoryResponse> getAllCategoriesByType(CategoryType type) {
        return repository.findAllByType(type).stream().map(mapper::toResponse).toList();
    }
}
