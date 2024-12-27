package com.supermall.backend.domain.invoice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class InvoiceRequest {
    @NotNull(message = "订单ID不能为空")
    private Integer orderId;
    
    @NotNull(message = "发票类型不能为空")
    private Integer type;
    
    @NotNull(message = "发票抬头类型不能为空")
    private Integer titleType;
    
    @NotNull(message = "发票抬头不能为空")
    @Size(min = 1, max = 100, message = "发票抬头长度必须在1-100之间")
    private String title;
    
    @Pattern(regexp = "^[0-9A-Z]{15,20}$", message = "纳税人识别号格式不正确")
    private String taxNumber;
    
    @Size(max = 200, message = "企业地址长度不能超过200")
    private String companyAddress;
    
    @Pattern(regexp = "^[0-9-]{7,20}$", message = "企业电话格式不正确")
    private String companyPhone;
    
    @Size(max = 100, message = "开户银行名称长度不能超过100")
    private String bankName;
    
    @Pattern(regexp = "^[0-9]{12,19}$", message = "银行账号格式不正确")
    private String bankAccount;
    
    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;
} 