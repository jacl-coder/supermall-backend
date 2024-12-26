package com.supermall.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.api.CommonResult;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.service.ProductService;
import com.supermall.backend.domain.product.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "商品管理", description = "商品管理接口")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "创建商品")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Product> create(@RequestBody Product product) {
        productService.save(product);
        return CommonResult.success(product);
    }

    @Operation(summary = "分页查询商品")
    @GetMapping
    public CommonResult<Page<ProductVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        try {
            log.debug("分页查询商品: pageNum={}, pageSize={}, keyword={}", pageNum, pageSize, keyword);
            Page<ProductVO> page = productService.page(pageNum, pageSize, keyword);
            return CommonResult.success(page);
        } catch (Exception e) {
            log.error("分页查询商品失败", e);
            return CommonResult.failed("分页查询商品失败");
        }
    }

    @Operation(summary = "更新商品")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Void> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        productService.updateById(product);
        return CommonResult.success(null);
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Void> delete(@PathVariable Long id) {
        productService.removeById(id);
        return CommonResult.success(null);
    }

    @Operation(summary = "批量删除商品")
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Void> deleteBatch(@RequestBody List<Long> ids) {
        productService.deleteBatch(ids);
        return CommonResult.success(null);
    }

    @Operation(summary = "更新商品状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return CommonResult.success(null);
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public CommonResult<ProductVO> getDetail(@PathVariable Long id) {
        try {
            log.debug("获取商品详情: id={}", id);
            ProductVO vo = productService.getDetail(id);
            return CommonResult.success(vo);
        } catch (ApiException e) {
            log.error("获取商品详情失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("获取商品详情失败", e);
            return CommonResult.failed("获取商品详情失败");
        }
    }
} 