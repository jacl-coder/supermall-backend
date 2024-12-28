package com.supermall.backend.domain.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payments")
public class Payment {
    @TableId(value = "payment_id", type = IdType.AUTO)
    private Integer id;
    
    private String paymentNo;
    private Integer orderId;
    private Integer userId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String transactionId;
    
    @TableField("`status`")
    private Status status;
    
    private String failureReason;
    private String callbackContent;
    
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

        private final String description;

        Status(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum PaymentMethod {
        ALIPAY("支付宝"),
        WECHAT("微信支付"),
        BANK_CARD("银行卡");

        private final String description;

        PaymentMethod(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
} 