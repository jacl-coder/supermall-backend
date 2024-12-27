package com.supermall.backend.domain.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("return_orders")
public class ReturnOrder {
    @TableId(value = "return_id", type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    private Long orderItemId;
    private Long userId;
    private Long merchantId;
    private BigDecimal returnAmount;
    
    @TableField(value = "status")
    private ReturnStatus status;
    
    @TableField(value = "reason_type")
    private ReasonType reasonType;
    
    private String reasonDetail;
    private String handlingNotes;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    public enum ReturnStatus {
        PENDING,    // 待处理
        APPROVED,   // 已批准
        REJECTED,   // 已拒绝
        RETURNED,   // 已退货
        REFUNDED    // 已退款
    }
    
    public enum ReasonType {
        QUALITY_ISSUE,   // 质量问题
        WRONG_ITEM,      // 错误商品
        NOT_SATISFIED,   // 不满意
        OTHER           // 其他原因
    }
} 