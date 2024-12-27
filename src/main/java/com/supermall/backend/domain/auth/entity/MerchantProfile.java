package com.supermall.backend.domain.auth.entity;

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
    
    @TableField("status")
    private String status; // pending, approved, rejected
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
} 