package com.tanpuh.category.mapper;

import com.tanpuh.category.dto.CategoryResponse;
import com.tanpuh.category.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category entity);
}
