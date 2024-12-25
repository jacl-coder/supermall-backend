package com.supermall.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("order_delivery")
public class OrderDelivery {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private String receiverName;
    
    private String receiverPhone;
    
    private String receiverProvince;
    
    private String receiverCity;
    
    private String receiverDistrict;
    
    private String receiverAddress;
    
    private String deliveryCompany;
    
    private String deliverySn;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 