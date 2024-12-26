package com.supermall.backend.domain.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String subtitle;
    
    @TableField("category_id")
    private Long categoryId;
    
    @TableField("brand_id")
    private Long brandId;
    
    @TableField("main_image")
    private String mainImage;
    
    @TableField("sub_images")
    private String subImages;
    
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    
    @TableField("created_time")
    private LocalDateTime createdTime;
    
    @TableField("updated_time")
    private LocalDateTime updatedTime;
    
    @TableLogic
    private Integer deleted;
} 