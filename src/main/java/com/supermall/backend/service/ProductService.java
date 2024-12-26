package com.supermall.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.entity.Product;
import com.supermall.backend.vo.ProductVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    /**
     * 创建商品
     */
    ProductVO createProduct(Product product);

    /**
     * 获取商品列表
     */
    Page<ProductVO> getProductList(String keyword, Long categoryId, Integer pageNum, Integer pageSize);

    /**
     * 获取商品详情
     */
    ProductVO getProductDetail(Long id);

    /**
     * 更新商品
     */
    void updateProduct(Long id, Product product);

    /**
     * 更新商品状态
     */
    void updateProductStatus(Long id, Integer status);

    /**
     * 删除商品
     */
    void deleteProduct(Long id);

    /**
     * 上传商品图片
     */
    String uploadImage(MultipartFile file);

    /**
     * 上传商品子图
     */
    List<String> uploadSubImages(Long id, List<MultipartFile> files);

    /**
     * 增加商品销量
     */
    void increaseSales(Long productId, Integer quantity);
}