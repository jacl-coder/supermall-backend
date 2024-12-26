package com.supermall.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.CommonResult;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "商品管理", description = "商品管理接口")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "创建商品")
    @PostMapping
    public CommonResult<Void> create(@RequestBody CreateProductParam param) {
        productService.create(param.getName(), param.getSubtitle(), param.getCategoryId(),
                param.getBrandId(), param.getMainImage(), param.getSubImages(),
                param.getDetail(), param.getPrice(), param.getStock());
        return CommonResult.success(null);
    }

    @Operation(summary = "更新商品")
    @PutMapping("/{id}")
    public CommonResult<Void> update(@PathVariable Long id, @RequestBody UpdateProductParam param) {
        productService.update(id, param.getName(), param.getSubtitle(),
                param.getMainImage(), param.getSubImages(), param.getDetail(),
                param.getPrice(), param.getStock());
        return CommonResult.success(null);
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public CommonResult<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return CommonResult.success(null);
    }

    @Operation(summary = "更新商品状态")
    @PutMapping("/{id}/status")
    public CommonResult<Void> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusParam param) {
        productService.updateStatus(id, param.getStatus());
        return CommonResult.success(null);
    }

    @Operation(summary = "获取商品列表")
    @GetMapping
    public CommonResult<Page<Product>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        return CommonResult.success(productService.page(pageNum, pageSize));
    }

    @Operation(summary = "根据分类获取商品列表")
    @GetMapping("/category/{categoryId}")
    public CommonResult<List<Product>> listByCategory(@PathVariable Long categoryId) {
        return CommonResult.success(productService.listByCategory(categoryId));
    }

    @Operation(summary = "根据品牌获取商品列表")
    @GetMapping("/brand/{brandId}")
    public CommonResult<List<Product>> listByBrand(@PathVariable Long brandId) {
        return CommonResult.success(productService.listByBrand(brandId));
    }

    @Operation(summary = "搜索商品")
    @GetMapping("/search")
    public CommonResult<List<Product>> search(@RequestParam String keyword) {
        return CommonResult.success(productService.search(keyword));
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public CommonResult<Product> detail(@PathVariable Long id) {
        return CommonResult.success(productService.getById(id));
    }

    @Data
    public static class CreateProductParam {
        private String name;
        private String subtitle;
        private Long categoryId;
        private Long brandId;
        private String mainImage;
        private String subImages;
        private String detail;
        private BigDecimal price;
        private Integer stock;
    }

    @Data
    public static class UpdateProductParam {
        private String name;
        private String subtitle;
        private String mainImage;
        private String subImages;
        private String detail;
        private BigDecimal price;
        private Integer stock;
    }

    @Data
    public static class UpdateStatusParam {
        private Integer status;
    }
} 