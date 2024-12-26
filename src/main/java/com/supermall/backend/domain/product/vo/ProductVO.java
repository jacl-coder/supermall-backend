package com.supermall.backend.domain.product.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@Data
public class ProductVO {
    private Long id;
    private String name;
    private String subtitle;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private String mainImage;
    private List<String> subImages;
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    // 从字符串设置子图片列表
    public void setSubImages(String subImages) {
        if (subImages != null) {
            this.subImages = Arrays.asList(subImages.split(","));
        }
    }

    // 直接设置子图片列表
    public void setSubImagesList(List<String> subImages) {
        this.subImages = subImages;
    }
} 