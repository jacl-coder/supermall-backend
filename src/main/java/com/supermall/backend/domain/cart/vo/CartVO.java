package com.supermall.backend.domain.cart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "购物车VO")
public class CartVO {
    @Schema(description = "购物车ID")
    private Long id;
    
    @Schema(description = "商品ID")
    private Long productId;
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "商品图片")
    private String productImage;
    
    @Schema(description = "商品单价")
    private BigDecimal price;
    
    @Schema(description = "数量")
    private Integer quantity;
    
    @Schema(description = "是否选中")
    private Boolean checked;
    
    @Schema(description = "小计金额")
    private BigDecimal totalAmount;
} 