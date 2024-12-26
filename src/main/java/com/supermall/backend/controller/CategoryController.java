package com.supermall.backend.controller;

import com.supermall.backend.common.api.CommonResult;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.category.entity.Category;
import com.supermall.backend.domain.category.service.CategoryService;
import com.supermall.backend.domain.category.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "分类管理", description = "分类管理接口")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @Operation(summary = "创建分类")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Category> create(@RequestBody Category category) {
        try {
            log.debug("创建分类: {}", category);
            categoryService.save(category);
            return CommonResult.success(category);
        } catch (ApiException e) {
            log.error("创建分类失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("创建分类失败", e);
            return CommonResult.failed("创建分类失败");
        }
    }
    
    @Operation(summary = "获取分类列表")
    @GetMapping
    public CommonResult<List<CategoryVO>> list() {
        try {
            log.debug("获取分类列表");
            List<CategoryVO> categories = categoryService.listCategories();
            return CommonResult.success(categories);
        } catch (ApiException e) {
            log.error("获取分类列表失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return CommonResult.failed("获取分类列表失败");
        }
    }
    
    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public CommonResult<CategoryVO> getDetail(@PathVariable Long id) {
        try {
            log.debug("获取分类详情: id={}", id);
            CategoryVO category = categoryService.getDetail(id);
            return CommonResult.success(category);
        } catch (ApiException e) {
            log.error("获取分类详情失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("获取分类详情失败", e);
            return CommonResult.failed("获取分类详情失败");
        }
    }
    
    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Void> update(
            @PathVariable Long id,
            @RequestBody Category category) {
        try {
            log.debug("更新分类: id={}, category={}", id, category);
            category.setId(id);
            categoryService.updateById(category);
            return CommonResult.success(null);
        } catch (ApiException e) {
            log.error("更新分类失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("更新分类失败", e);
            return CommonResult.failed("更新分类失败");
        }
    }
    
    @Operation(summary = "更新分类状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            log.debug("更新分类状态: id={}, status={}", id, status);
            categoryService.updateStatus(id, status);
            return CommonResult.success(null);
        } catch (ApiException e) {
            log.error("更新分类状态失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("更新分类状态失败", e);
            return CommonResult.failed("更新分类状态失败");
        }
    }
    
    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Void> delete(@PathVariable Long id) {
        try {
            log.debug("删除分类: id={}", id);
            // 检查是否有子分类
            List<Category> children = categoryService.listChildren(id);
            if (!children.isEmpty()) {
                return CommonResult.failed("请先删除子分类");
            }
            categoryService.removeById(id);
            return CommonResult.success(null);
        } catch (ApiException e) {
            log.error("删除分类失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("删除分类失败", e);
            return CommonResult.failed("删除分类失败");
        }
    }
} 