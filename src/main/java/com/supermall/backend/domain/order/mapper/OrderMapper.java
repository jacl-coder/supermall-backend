package com.supermall.backend.domain.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    /**
     * 获取订单详情
     */
    OrderVO getOrderById(@Param("id") Long id);
    
    /**
     * 分页获取订单列表
     */
    IPage<OrderVO> listOrders(IPage<OrderVO> page);
} 