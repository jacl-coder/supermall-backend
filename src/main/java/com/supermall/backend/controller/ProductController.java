package com.supermall.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.response.Result;
import com.supermall.backend.dto.ProductDTO;
import com.supermall.backend.dto.IncreaseSalesDTO;
import com.supermall.backend.entity.Product;
import com.supermall.backend.service.ProductService;
import com.supermall.backend.vo.ProductVO;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
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
    public Result<ProductVO> createProduct(@RequestBody ProductDTO productDTO) {
        // 转换DTO为实体
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        
        // 处理子图
        if (productDTO.getSubImages() != null && !productDTO.getSubImages().isEmpty()) {
            product.setSubImages(String.join(",", productDTO.getSubImages()));
        }
        
        return Result.success(productService.createProduct(product));
    }

    @GetMapping
    public Result<Page<ProductVO>> getProductList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(productService.getProductList(keyword, categoryId, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<ProductVO> getProductDetail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }

    @PutMapping("/{id}")
    public Result<Void> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        // 转换DTO为实体
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        
        // 处理子图
        if (productDTO.getSubImages() != null && !productDTO.getSubImages().isEmpty()) {
            product.setSubImages(String.join(",", productDTO.getSubImages()));
        }
        
        productService.updateProduct(id, product);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateProductStatus(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        productService.updateProductStatus(id, productDTO.getStatus());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @PostMapping("/upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return Result.success(productService.uploadImage(file));
    }

    @PostMapping("/{id}/images")
    public Result<List<String>> uploadSubImages(@PathVariable Long id, @RequestParam("files") List<MultipartFile> files) {
        return Result.success(productService.uploadSubImages(id, files));
    }

    @PutMapping("/{id}/sales")
    public ResponseEntity<Result<Void>> increaseSales(@PathVariable Long id, @RequestBody @Valid IncreaseSalesDTO increaseSalesDTO) {
        productService.increaseSales(id, increaseSalesDTO.getQuantity());
        return ResponseEntity.ok(Result.success());
    }
}