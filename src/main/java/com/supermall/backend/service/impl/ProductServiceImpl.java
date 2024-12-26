package com.supermall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.entity.Category;
import com.supermall.backend.entity.Product;
import com.supermall.backend.repository.CategoryRepository;
import com.supermall.backend.repository.ProductRepository;
import com.supermall.backend.service.ProductService;
import com.supermall.backend.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public ProductVO createProduct(Product product) {
        // 检查分类是否存在
        Category category = categoryRepository.selectById(product.getCategoryId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 保存商品
        productRepository.insert(product);

        // 转换为VO并返回
        return convertToVO(product, category.getName());
    }

    @Override
    public Page<ProductVO> getProductList(String keyword, Long categoryId, Integer pageNum, Integer pageSize) {
        // 创建分页对象
        Page<Product> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getDeleted, 0);

        // 添加关键词搜索条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Product::getName, keyword)
                    .or()
                    .like(Product::getDescription, keyword));
        }

        // 添加分类过滤条件
        if (categoryId != null) {
            queryWrapper.eq(Product::getCategoryId, categoryId);
        }

        // 按创建时间倒序排序
        queryWrapper.orderByDesc(Product::getCreateTime);

        // 执行分页查询
        Page<Product> productPage = productRepository.selectPage(page, queryWrapper);

        // 转换为VO
        List<ProductVO> productVOs = productPage.getRecords().stream()
                .map(product -> {
                    Category category = categoryRepository.selectById(product.getCategoryId());
                    return convertToVO(product, category != null ? category.getName() : null);
                })
                .collect(Collectors.toList());

        // 创建VO分页对象
        Page<ProductVO> voPage = new Page<>();
        BeanUtils.copyProperties(productPage, voPage, "records");
        voPage.setRecords(productVOs);

        return voPage;
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        Product product = productRepository.selectById(id);
        if (product == null || product.getDeleted() == 1) {
            throw new BusinessException("商品不存在");
        }

        Category category = categoryRepository.selectById(product.getCategoryId());
        return convertToVO(product, category != null ? category.getName() : null);
    }

    @Override
    @Transactional
    public void updateProduct(Long id, Product product) {
        // 检查商品是否存在
        Product existingProduct = productRepository.selectById(id);
        if (existingProduct == null || existingProduct.getDeleted() == 1) {
            throw new BusinessException("商品不存在");
        }

        // 检查分类是否存在
        Category category = categoryRepository.selectById(product.getCategoryId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 设置ID并更新
        product.setId(id);
        productRepository.updateById(product);
    }

    @Override
    @Transactional
    public void updateProductStatus(Long id, Integer status) {
        // 检查商品是否存在
        Product product = productRepository.selectById(id);
        if (product == null || product.getDeleted() == 1) {
            throw new BusinessException("商品不存在");
        }

        // 检查状态值是否合法
        if (status != 0 && status != 1) {
            throw new BusinessException("状态值不合法");
        }

        // 更新状态
        Product updateProduct = new Product();
        updateProduct.setId(id);
        updateProduct.setStatus(status);
        productRepository.updateById(updateProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.selectById(id);
        if (product == null || product.getDeleted() == 1) {
            throw new BusinessException("商品不存在");
        }

        productRepository.deleteById(id);
    }

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            // 获取原始文件名并检查
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new BusinessException("文件名不能为空");
            }

            // 获取项目根目录
            String projectPath = System.getProperty("user.dir");
            File uploadDir = new File(projectPath, uploadPath + "/products");
            
            // 创建上传目录
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    throw new BusinessException("创建上传目录失败");
                }
            }
            
            // 获取文件扩展名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            
            // 生成新文件名
            String filename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            File destFile = new File(uploadDir, filename);
            file.transferTo(destFile);
            
            return "/uploads/products/" + filename;
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new BusinessException("上传图片失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<String> uploadSubImages(Long id, List<MultipartFile> files) {
        // 检查商品是否存在
        Product product = productRepository.selectById(id);
        if (product == null || product.getDeleted() == 1) {
            throw new BusinessException("商品不存在");
        }
        
        List<String> imageUrls = new ArrayList<>();
        
        for (MultipartFile file : files) {
            String imageUrl = uploadImage(file);
            imageUrls.add(imageUrl);
        }
        
        // 更新商品子图
        String subImages = product.getSubImages();
        List<String> existingImages = StringUtils.hasText(subImages) 
            ? new ArrayList<>(List.of(subImages.split(",")))
            : new ArrayList<>();
        
        existingImages.addAll(imageUrls);
        
        product.setSubImages(String.join(",", existingImages));
        productRepository.updateById(product);
        
        return imageUrls;
    }

    @Override
    public void increaseSales(Long productId, Integer quantity) {
        Product product = productRepository.selectById(productId);
        if (product == null || Boolean.TRUE.equals(product.getDeleted())) {
            throw new BusinessException("商品不存在");
        }
        
        if (quantity <= 0) {
            throw new BusinessException("数量不合法");
        }
        
        int rows = productRepository.increaseSales(productId, quantity);
        if (rows != 1) {
            throw new BusinessException("更新销量失败");
        }
    }

    private ProductVO convertToVO(Product product, String categoryName) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        vo.setCategoryName(categoryName);
        
        // 处理子图
        if (StringUtils.hasText(product.getSubImages())) {
            vo.setSubImages(List.of(product.getSubImages().split(",")));
        } else {
            vo.setSubImages(new ArrayList<>());
        }
        
        return vo;
    }
}