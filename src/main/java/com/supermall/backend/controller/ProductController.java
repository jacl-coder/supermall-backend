package com.supermall.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermall.backend.common.response.Result;
import com.supermall.backend.dto.ProductDTO;
import com.supermall.backend.service.ProductService;
import com.supermall.backend.vo.ProductVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public Result<ProductVO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        return Result.success(productService.createProduct(productDTO));
    }

    @PutMapping("/{id}")
    public Result<ProductVO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        return Result.success(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success(null);
    }

    @GetMapping("/{id}")
    public Result<ProductVO> getProduct(@PathVariable Long id) {
        return Result.success(productService.getProduct(id));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return Result.success(null);
    }

    @GetMapping
    public Result<IPage<ProductVO>> getProductPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(productService.getProductPage(pageNum, pageSize, keyword, categoryId));
    }

    @PostMapping("/images")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return Result.success(productService.uploadImage(file));
    }

    @PostMapping("/{id}/images")
    public Result<List<String>> uploadSubImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {
        return Result.success(productService.uploadSubImages(id, files));
    }
}