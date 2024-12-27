package com.supermall.backend.domain.merchant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MerchantRegisterRequest {
    @NotBlank(message = "店铺名称不能为空")
    private String shopName;
    
    private String shopDescription;
    
    @NotBlank(message = "营业执照不能为空")
    private String businessLicense;
    
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;
    
    @NotBlank(message = "联系邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String contactEmail;
} 