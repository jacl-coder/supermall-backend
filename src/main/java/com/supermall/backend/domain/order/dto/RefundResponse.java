package com.supermall.backend.domain.order.dto;

import com.supermall.backend.domain.order.enums.RefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "退款申请响应")
public class RefundResponse {
    @Schema(description = "退款ID")
    private Integer id;
    
    @Schema(description = "订单ID")
    private Integer orderId;
    
    @Schema(description = "订单项ID")
    private Integer orderItemId;
    
    @Schema(description = "用户ID")
    private Integer userId;
    
    @Schema(description = "商家ID")
    private Integer merchantId;
    
    @Schema(description = "退款金额")
    private BigDecimal amount;
    
    @Schema(description = "退款原因")
    private String reason;
    
    @Schema(description = "详细说明")
    private String description;
    
    @Schema(description = "图片列表")
    private List<String> images;
    
    @Schema(description = "商家回复")
    private String merchantReply;
    
    @Schema(description = "退款状态")
    private RefundStatus status;
    
    @Schema(description = "完成时间")
    private LocalDateTime completionTime;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "商品图片")
    private String productImage;
    
    @Schema(description = "商品规格")
    private String productSpec;
} 