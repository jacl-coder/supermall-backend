package com.supermall.backend.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "退款申请请求")
public class RefundRequest {
    @Schema(description = "订单项ID", required = true)
    private Integer orderItemId;
    
    @Schema(description = "退款金额", required = true)
    private BigDecimal amount;
    
    @Schema(description = "退款原因", required = true)
    private String reason;
    
    @Schema(description = "详细说明")
    private String description;
    
    @Schema(description = "图片列表")
    private List<String> images;
} 