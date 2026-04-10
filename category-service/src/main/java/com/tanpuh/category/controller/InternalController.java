package com.tanpuh.category.controller;

import com.tanpuh.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class InternalController {
    private final CategoryService service;

    @GetMapping("/list")
    @Operation(summary = "Show all categories by ids")
    public ResponseEntity<?> getAllCategoriesByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(service.getAllCategoriesByIds(ids));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCategoryById(id));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<?> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(service.existsById(id));
    }
}
