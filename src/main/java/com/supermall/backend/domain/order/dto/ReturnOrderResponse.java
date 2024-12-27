package com.supermall.backend.domain.order.dto;

import com.supermall.backend.domain.order.entity.ReturnOrder.ReturnStatus;
import com.supermall.backend.domain.order.entity.ReturnOrder.ReasonType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReturnOrderResponse {
    private Long id;
    private Long orderId;
    private Long orderItemId;
    private Long userId;
    private Long merchantId;
    private BigDecimal returnAmount;
    private ReturnStatus status;
    private ReasonType reasonType;
    private String reasonDetail;
    private String handlingNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 附加信息
    private String productName;
    private String productImage;
    private Integer quantity;
    private BigDecimal price;
} 