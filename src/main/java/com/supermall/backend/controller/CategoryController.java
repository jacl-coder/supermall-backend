package com.supermall.backend.controller;

import com.supermall.backend.common.api.CommonResult;
import com.supermall.backend.domain.category.service.CategoryService;
import com.supermall.backend.domain.category.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 */
@Tag(name = "商品分类管理", description = "商品分类管理接口")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "获取分类列表")
    @GetMapping
    public CommonResult<List<CategoryVO>> list() {
        List<CategoryVO> categories = categoryService.listWithTree();
        return CommonResult.success(categories);
    }

    @Operation(summary = "创建分类")
    @PostMapping
    public CommonResult<Void> create(@RequestBody CreateCategoryParam param) {
        categoryService.create(param.getName(), param.getParentId(), param.getSort(), param.getIcon());
        return CommonResult.success(null);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public CommonResult<Void> update(@PathVariable Long id, @RequestBody UpdateCategoryParam param) {
        categoryService.update(id, param.getName(), param.getSort(), param.getIcon());
        return CommonResult.success(null);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return CommonResult.success(null);
    }

    @Operation(summary = "更新分类状态")
    @PutMapping("/{id}/status")
    public CommonResult<Void> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusParam param) {
        categoryService.updateStatus(id, param.getStatus());
        return CommonResult.success(null);
    }

    @Data
    public static class CreateCategoryParam {
        private String name;
        private Long parentId;
        private Integer sort;
        private String icon;
    }

    @Data
    public static class UpdateCategoryParam {
        private String name;
        private Integer sort;
        private String icon;
    }

    @Data
    public static class UpdateStatusParam {
        private Integer status;
    }
} 