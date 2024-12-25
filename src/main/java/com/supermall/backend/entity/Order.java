package com.supermall.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("`order`")  // order是MySQL关键字，需要加引号
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private BigDecimal totalAmount;
    
    private BigDecimal payAmount;
    
    private BigDecimal freightAmount;
    
    private Integer status;  // 0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
    
    private Integer payType;  // 0->未支付；1->支付宝；2->微信
    
    private LocalDateTime payTime;
    
    private LocalDateTime deliveryTime;
    
    private LocalDateTime receiveTime;
    
    private LocalDateTime commentTime;
    
    private LocalDateTime cancelTime;  // 订单取消时间
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    
} 