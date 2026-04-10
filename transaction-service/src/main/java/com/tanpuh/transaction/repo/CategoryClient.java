package com.tanpuh.transaction.repo;

import com.tanpuh.transaction.config.RequestInterceptorImpl;
import com.tanpuh.transaction.dto.CategoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "category-service", configuration = { RequestInterceptorImpl.class })
public interface CategoryClient {

    @GetMapping("/categories/{id}/exists")
    Boolean existsById(@PathVariable Long id);

    @GetMapping("/categories/list")
    List<CategoryResponse> getCategoriesByIds(@RequestParam("ids") List<Long> ids);

    @GetMapping("/categories/{id}")
    CategoryResponse getCategoryById(@PathVariable Long id);
}
