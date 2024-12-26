package com.supermall.backend.domain.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.domain.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    /**
     * 获取订单项列表
     */
    List<OrderItem> listByOrderId(@Param("orderId") Long orderId);
} 