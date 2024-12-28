package com.supermall.backend.domain.merchant.dto;

import com.supermall.backend.domain.merchant.entity.MerchantProfile;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MerchantResponse {
    private Integer id;
    private String shopName;
    private String shopDescription;
    private String businessLicense;
    private String contactPhone;
    private String contactEmail;
    private MerchantProfile.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 