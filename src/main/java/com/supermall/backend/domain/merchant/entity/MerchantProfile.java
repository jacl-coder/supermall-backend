package com.supermall.backend.domain.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("merchant_profiles")
public class MerchantProfile {
    @TableId(value = "merchant_id", type = IdType.AUTO)
    private Integer id;
    private Integer authId;
    private String shopName;
    private String shopDescription;
    private String businessLicense;
    private String contactPhone;
    private String contactEmail;
    
    @TableField("`status`")
    private Status status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public enum Status {
        PENDING("PENDING", "待审核"),
        APPROVED("APPROVED", "已审核"),
        REJECTED("REJECTED", "已拒绝"),
        SUSPENDED("SUSPENDED", "已暂停"),
        TERMINATED("TERMINATED", "已终止");

        @EnumValue
        private final String value;
        private final String description;

        Status(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return this.value;
        }

        public String getDescription() {
            return this.description;
        }
    }
} 