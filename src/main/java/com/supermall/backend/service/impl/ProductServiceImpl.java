package com.supermall.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.dto.ProductDTO;
import com.supermall.backend.entity.Category;
import com.supermall.backend.entity.Product;
import com.supermall.backend.repository.CategoryRepository;
import com.supermall.backend.repository.ProductRepository;
import com.supermall.backend.service.ProductService;
import com.supermall.backend.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

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
    public ProductVO createProduct(ProductDTO productDTO) {
        // 检查分类是否存在
        Category category = categoryRepository.selectById(productDTO.getCategoryId());
        if (category == null) {
            throw new BusinessException("商品分类不存在");
        }

        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        
        // 处理子图
        if (productDTO.getSubImages() != null && !productDTO.getSubImages().isEmpty()) {
            product.setSubImages(String.join(",", productDTO.getSubImages()));
        }

        productRepository.insert(product);
        return getProduct(product.getId());
    }

    @Override
    @Transactional
    public ProductVO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查分类是否存在
        Category category = categoryRepository.selectById(productDTO.getCategoryId());
        if (category == null) {
            throw new BusinessException("商品分类不存在");
        }

        BeanUtils.copyProperties(productDTO, product);
        
        // 处理子图
        if (productDTO.getSubImages() != null && !productDTO.getSubImages().isEmpty()) {
            product.setSubImages(String.join(",", productDTO.getSubImages()));
        }

        productRepository.updateById(product);
        return getProduct(id);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductVO getProduct(Long id) {
        Product product = productRepository.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return convertToVO(product);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        Product product = productRepository.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        if (status != 0 && status != 1) {
            throw new BusinessException("商品状态不正确");
        }
        
        Product updateProduct = new Product();
        updateProduct.setId(id);
        updateProduct.setStatus(status);
        productRepository.updateById(updateProduct);
    }

    @Override
    public IPage<ProductVO> getProductPage(Integer pageNum, Integer pageSize, String keyword, Long categoryId) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Product::getName, keyword)
                      .or()
                      .like(Product::getDescription, keyword);
        }
        
        if (categoryId != null) {
            queryWrapper.eq(Product::getCategoryId, categoryId);
        }
        
        // 按创建时间倒序
        queryWrapper.orderByDesc(Product::getCreateTime);
        
        IPage<Product> productPage = productRepository.selectPage(page, queryWrapper);
        
        return productPage.convert(this::convertToVO);
    }

    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        
        // 设置分类名称
        Category category = categoryRepository.selectById(product.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        
        // 处理子图
        if (StringUtils.hasText(product.getSubImages())) {
            vo.setSubImages(List.of(product.getSubImages().split(",")));
        } else {
            vo.setSubImages(new ArrayList<>());
        }
        
        return vo;
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
        if (product == null) {
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
}