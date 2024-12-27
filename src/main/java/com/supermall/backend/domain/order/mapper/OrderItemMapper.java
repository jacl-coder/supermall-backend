package com.supermall.backend.domain.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.domain.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
    /**
     * 批量插入订单项
     *
     * @param items 订单项列表
     * @return 插入的记录数
     */
    int insertBatch(@Param("items") List<OrderItem> items);
} 