package com.supermall.backend.entity;

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
    
    private String description;
    
    @TableField("category_id")
    private Long categoryId;
    
    private BigDecimal price;
    
    private Integer stock;
    
    private Integer sales;
    
    @TableField("main_image")
    private String mainImage;
    
    @TableField("sub_images")
    private String subImages;
    
    private Integer status; // 0-下架 1-上架
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 