package com.supermall.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("order_log")
public class OrderLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long userId;
    private Integer orderStatus;
    private String action;
    private String note;
    private LocalDateTime createTime;
} 