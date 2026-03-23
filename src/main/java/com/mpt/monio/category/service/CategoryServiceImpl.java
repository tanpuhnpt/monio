package com.mpt.monio.category.service;

import com.mpt.monio.auth.entity.User;
import com.mpt.monio.auth.repo.UserRepository;
import com.mpt.monio.category.dto.CategoryRequest;
import com.mpt.monio.category.dto.CategoryResponse;
import com.mpt.monio.category.entity.Category;
import com.mpt.monio.category.mapper.CategoryMapper;
import com.mpt.monio.category.repo.CategoryRepository;
import com.mpt.monio.exception.AppException;
import com.mpt.monio.exception.ErrorCode;
import com.mpt.monio.redis.RedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    RedisService redisService;
    UserRepository userRepository;

    @Override
    public List<CategoryResponse> getAllCategories() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        String key = String.format("category:user:%d", userId);
        List<CategoryResponse> responses = redisService.getAll(key, CategoryResponse.class);

        if (responses == null) {
            log.info("query category");
            responses = categoryRepository
                    .findAllByUserIdAndIsActiveTrue(userId)
                    .stream().map(categoryMapper::toResponse)
                    .toList();

            redisService.saveAll(key, responses);
        }

        return responses;
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setUser(user);

        try {
            return categoryMapper.toResponse(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!userRepository.existsById(userId))
            throw new AppException(ErrorCode.USER_NOT_FOUND);

        // verify user ownership
        if (!Objects.equals(category.getUser().getId(), userId))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        category.setName(categoryRequest.getName());

        try {
            return categoryMapper.toResponse(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!userRepository.existsById(userId))
            throw new AppException(ErrorCode.USER_NOT_FOUND);

        // verify user ownership
        if (!Objects.equals(category.getUser().getId(), userId))
            throw new AppException(ErrorCode.UNAUTHORIZED);

        category.setActive(false);
        categoryRepository.save(category);
    }
}
