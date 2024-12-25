package com.supermall.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermall.backend.dto.ProductDTO;
import com.supermall.backend.vo.ProductVO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProductService {
    // 创建商品
    ProductVO createProduct(ProductDTO productDTO);
    
    // 更新商品
    ProductVO updateProduct(Long id, ProductDTO productDTO);
    
    // 删除商品
    void deleteProduct(Long id);
    
    // 获取商品详情
    ProductVO getProduct(Long id);
    
    // 商品上下架
    void updateStatus(Long id, Integer status);
    
    // 分页查询商品
    IPage<ProductVO> getProductPage(Integer pageNum, Integer pageSize, String keyword, Long categoryId);

    // 上传主图
    String uploadImage(MultipartFile file);

    // 上传子图
    List<String> uploadSubImages(Long id, List<MultipartFile> files);
}