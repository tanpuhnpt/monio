package com.mpt.monio.category.controller;

import com.mpt.monio.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Show all categories")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}
