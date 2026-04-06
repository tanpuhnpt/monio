package com.tanpuh.category.controller;

import com.tanpuh.category.entity.CategoryType;
import com.tanpuh.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @GetMapping
    @Operation(summary = "Show all categories by type")
    public ResponseEntity<?> getAllCategories(@RequestParam CategoryType type) {
        return ResponseEntity.ok(service.getAllCategoriesByType(type));
    }
}
