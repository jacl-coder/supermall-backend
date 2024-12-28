package com.supermall.backend.domain.user.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AddressResponse {
    private Integer id;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String street;
    private String postalCode;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 