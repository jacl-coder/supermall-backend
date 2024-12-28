package com.supermall.backend.domain.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("return_orders")
public class ReturnOrder {
    @TableId(value = "return_id", type = IdType.AUTO)
    private Integer id;
    
    private Integer orderId;
    private Integer orderItemId;
    private Integer userId;
    private Integer merchantId;
    private BigDecimal returnAmount;
    
    @TableField(value = "`status`")
    private ReturnStatus status;
    
    @TableField(value = "reason_type")
    private ReasonType reasonType;
    
    private String reasonDetail;
    private String handlingNotes;
    
    // 物流信息字段
    private String logisticsInfo;  // 物流信息，可以是文本描述
    private String logisticsStatus; // 物流状态：PENDING(待发货)、SHIPPED(已发货)、RECEIVED(已收货)
    private String logisticsRemark; // 物流备注
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    public enum ReturnStatus {
        PENDING("待审核"),
        APPROVED("已通过"),
        REJECTED("已拒绝"),
        RETURNED("已退货"),
        REFUNDED("已退款");
        
        private final String description;
        
        ReturnStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum ReasonType {
        QUALITY_ISSUE("质量问题"),
        WRONG_PRODUCT("错误商品"),
        DAMAGED("商品损坏"),
        OTHER("其他原因");
        
        private final String description;
        
        ReasonType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
} 