package com.supermall.backend.domain.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Getter;
import com.baomidou.mybatisplus.annotation.EnumValue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("products")
public class Product {
    @TableId(value = "product_id", type = IdType.AUTO)
    private Integer id;

    @TableField("merchant_id")
    private Integer merchantId;

    @TableField("category_id")
    private Integer categoryId;

    private String name;
    private String description;
    private BigDecimal price;

    @TableField("original_price")
    private BigDecimal originalPrice;

    private Integer stock;
    private Integer sales;

    @TableField("main_image")
    private String mainImage;

    @TableField("`status`")
    private Status status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @Getter
    public enum Status {
        DRAFT("DRAFT"),      // 草稿
        PENDING("PENDING"),    // 待审核
        REJECTED("REJECTED"),   // 已拒绝
        APPROVED("APPROVED"),   // 已通过
        ON_SALE("ON_SALE"),    // 在售
        OFF_SALE("OFF_SALE");   // 下架

        @EnumValue
        private final String value;

        Status(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
} 