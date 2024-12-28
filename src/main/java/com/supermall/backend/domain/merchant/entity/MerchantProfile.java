package com.supermall.backend.domain.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("merchant_profiles")
public class MerchantProfile {
    @TableId(value = "merchant_id", type = IdType.AUTO)
    private Long id;
    private Long authId;
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
        PENDING("待审核", "等待管理员审核"),
        APPROVED("已审核", "审核通过，可以正常经营"),
        REJECTED("已拒绝", "审核未通过"),
        SUSPENDED("已暂停", "暂停营业"),
        TERMINATED("已终止", "终止合作");

        private final String value;
        private final String description;

        Status(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static Status fromValue(String value) {
            for (Status status : Status.values()) {
                if (status.getValue().equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("无效的商家状态: " + value);
        }
    }
} 