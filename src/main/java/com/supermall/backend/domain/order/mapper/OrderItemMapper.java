package com.supermall.backend.domain.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermall.backend.domain.order.entity.OrderItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface OrderItemMapper extends BaseMapper<OrderItem> {
    void insertBatch(@Param("items") List<OrderItem> items);
} 