package com.supermall.backend.domain.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.supermall.backend.domain.order.enums.RefundStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_refunds")
public class OrderRefund {
    @TableId(value = "refund_id", type = IdType.AUTO)
    private Integer id;
    
    @TableField("order_id")
    private Integer orderId;
    
    @TableField("order_item_id")
    private Integer orderItemId;
    
    @TableField("user_id")
    private Integer userId;
    
    @TableField("merchant_id")
    private Integer merchantId;
    
    private BigDecimal amount;
    private String reason;
    private String description;
    private String images;
    
    @TableField("merchant_reply")
    private String merchantReply;
    
    @TableField("`status`")
    private RefundStatus status;
    
    @TableField("completion_time")
    private LocalDateTime completionTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 