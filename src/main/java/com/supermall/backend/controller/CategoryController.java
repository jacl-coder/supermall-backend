package com.supermall.backend.controller;

import com.supermall.backend.common.response.Result;
import com.supermall.backend.dto.CategoryDTO;
import com.supermall.backend.entity.Category;
import com.supermall.backend.service.CategoryService;
import com.supermall.backend.vo.CategoryVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Result<Category> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return Result.success(categoryService.createCategory(categoryDTO));
    }

    @PutMapping("/{id}")
    public Result<Category> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        return Result.success(categoryService.updateCategory(id, categoryDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<Category> getCategory(@PathVariable Long id) {
        return Result.success(categoryService.getCategory(id));
    }

    @GetMapping("/tree")
    public Result<List<CategoryVO>> getCategoryTree() {
        return Result.success(categoryService.getCategoryTree());
    }

    @GetMapping
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryService.getAllCategories());
    }
}