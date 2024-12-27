package com.supermall.backend.domain.merchant.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MerchantResponse {
    private Long id;
    private String shopName;
    private String shopDescription;
    private String businessLicense;
    private String contactPhone;
    private String contactEmail;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 