package com.supermall.backend.domain.notification.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("notifications")
public class Notification {
    @TableId(value = "notification_id", type = IdType.AUTO)
    private Integer id;
    
    private Integer userId;
    private String title;
    private String content;
    
    @TableField(value = "`type`")
    private NotificationType type;
    
    private Integer referenceId;
    private Boolean isRead;
    
    private Priority priority;
    private NotificationStatus status;
    private LocalDateTime expireTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    public enum NotificationType {
        // 订单相关
        ORDER_CREATED("订单创建"),
        ORDER_PAID("订单支付成功"),
        ORDER_SHIPPED("订单已发货"),
        ORDER_COMPLETED("订单已完成"),
        ORDER_CANCELLED("订单已取消"),
        
        // 退货相关
        RETURN_CREATED("退货申请已提交"),
        RETURN_APPROVED("退货申请已通过"),
        RETURN_REJECTED("退货申请已拒绝"),
        RETURN_COMPLETED("退货已完成"),
        
        // 支付相关
        PAYMENT_SUCCESS("支付成功"),
        PAYMENT_FAILED("支付失败"),
        REFUND_SUCCESS("退款成功"),
        REFUND_FAILED("退款失败"),
        
        // 系统相关
        SYSTEM_MAINTENANCE("系统维护"),
        SYSTEM_ANNOUNCEMENT("系统公告");
        
        private final String description;
        
        NotificationType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum Priority {
        HIGH("高"),
        MEDIUM("中"),
        LOW("低");
        
        private final String description;
        
        Priority(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum NotificationStatus {
        PENDING("待发送"),
        SENT("已发送"),
        READ("已读"),
        EXPIRED("已过期"),
        FAILED("发送失败");
        
        private final String description;
        
        NotificationStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
} 