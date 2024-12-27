package com.supermall.backend.domain.order.dto;

import com.supermall.backend.domain.order.entity.ReturnOrder.ReasonType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReturnOrderRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    @NotNull(message = "订单项ID不能为空")
    private Long orderItemId;
    
    @NotNull(message = "退货原因类型不能为空")
    private ReasonType reasonType;
    
    @Size(max = 500, message = "退货原因详情不能超过500字")
    private String reasonDetail;
} 