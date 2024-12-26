package com.supermall.backend.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductVO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private String mainImage;
    private List<String> subImages;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 