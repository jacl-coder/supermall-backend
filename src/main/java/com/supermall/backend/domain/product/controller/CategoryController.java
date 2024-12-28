package com.supermall.backend.domain.product.controller;

import com.supermall.backend.common.api.Result;
import com.supermall.backend.common.security.annotation.RequirePermission;
import com.supermall.backend.domain.product.dto.CategoryRequest;
import com.supermall.backend.domain.product.entity.Category;
import com.supermall.backend.domain.product.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品分类管理", description = "包括商品分类的增删改查、分类层级管理等功能")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @RequirePermission(role = "ADMIN")
    public Result<Category> createCategory(@Valid @RequestBody CategoryRequest request) {
        return Result.success(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    @RequirePermission(role = "ADMIN")
    public Result<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        Category category = categoryService.updateCategory(id, request);
        if (category == null) {
            return Result.fail("分类不存在");
        }
        return Result.success(category);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(role = "ADMIN")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        if (!categoryService.deleteCategory(id)) {
            return Result.fail("删除失败，可能存在子分类");
        }
        return Result.success(true);
    }

    @GetMapping("/sub/{parentId}")
    public Result<List<Category>> getSubCategories(@PathVariable Long parentId) {
        return Result.success(categoryService.getSubCategories(parentId));
    }

    @GetMapping
    public Result<List<Category>> getAllCategories() {
        return Result.success(categoryService.getAllCategories());
    }
} 