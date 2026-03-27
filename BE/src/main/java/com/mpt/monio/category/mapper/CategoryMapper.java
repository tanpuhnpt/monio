package com.mpt.monio.category.mapper;

import com.mpt.monio.category.dto.CategoryResponse;
import com.mpt.monio.category.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category entity);
}
