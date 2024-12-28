package com.supermall.backend.domain.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("payments")
public class Payment {
    @TableId(value = "payment_id", type = IdType.AUTO)
    private Integer id;
    
    private String paymentNo;
    private Integer orderId;
    private Integer userId;
    private BigDecimal amount;
    
    @TableField(value = "payment_method")
    private PaymentMethod paymentMethod;
    
    private String transactionId;
    
    @TableField(value = "`status`")
    private Status status;
    
    private String failureReason;
    private String callbackContent;
    
    // 退款相关字段
    private Integer refundForPaymentId;  // 原支付记录ID（仅退款时使用）
    private Boolean isRefund;            // 是否为退款记录
    
    // 新增字段
    private LocalDateTime expireTime;    // 支付超时时间
    private String channelConfig;        // 支付渠道配置（JSON格式）
    private Integer retryCount;          // 重试��数
    private String notifyUrl;            // 支付回调通知地址
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    private LocalDateTime paidAt;
    
    public enum Status {
        PENDING("待支付"),
        PROCESSING("处理中"),
        SUCCESS("支付成功"),
        FAILED("支付失败"),
        REFUND_PENDING("退款中"),
        REFUNDED("已退款"),
        CLOSED("已关闭");
        
        @EnumValue
        private final String value;
        private final String description;
        
        Status(String description) {
            this.value = this.name().toLowerCase();
            this.description = description;
        }
        
        public String getValue() {
            return value;
        }
        
        public String getDescription() {
            return description;
        }
        
        public boolean canTransitionTo(Status nextStatus) {
            return switch (this) {
                case PENDING -> List.of(PROCESSING, FAILED, CLOSED).contains(nextStatus);
                case PROCESSING -> List.of(SUCCESS, FAILED).contains(nextStatus);
                case SUCCESS -> List.of(REFUND_PENDING).contains(nextStatus);
                case REFUND_PENDING -> List.of(REFUNDED, FAILED).contains(nextStatus);
                case FAILED, CLOSED, REFUNDED -> false;
            };
        }
    }
    
    public enum PaymentMethod {
        ALIPAY("支付宝"),
        WECHAT("微信支付"),
        BANK_CARD("银行卡");
        
        @EnumValue
        private final String value;
        private final String description;
        
        PaymentMethod(String description) {
            this.value = this.name().toLowerCase();
            this.description = description;
        }
        
        public String getValue() {
            return value;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 金额验证
    public void validateAmount() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("支付金额必须大于0");
        }
        if (isRefund != null && isRefund && amount.compareTo(BigDecimal.ZERO) >= 0) {
            throw new IllegalArgumentException("退款金额必须小于0");
        }
    }
    
    // 检查是否已超时
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }
    
    // 检查是否可以重试
    public boolean canRetry() {
        return retryCount != null && retryCount < 3;  // 最多重试3次
    }
    
    // 增加重试次数
    public void incrementRetryCount() {
        if (retryCount == null) {
            retryCount = 0;
        }
        retryCount++;
    }
} 