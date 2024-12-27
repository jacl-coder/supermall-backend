package com.supermall.backend.domain.product.service;

import com.supermall.backend.domain.product.entity.StockMovement;
import com.supermall.backend.domain.product.entity.StockMovement.MovementType;

public interface StockService {
    /**
     * 创建库存变动记录
     */
    StockMovement createMovement(Integer productId, Integer quantity, MovementType type, 
                                Integer referenceId, Integer operatorId, String notes);

    /**
     * 检查商品库存是否足够
     */
    boolean checkStock(Integer productId, Integer quantity);

    /**
     * 锁定商品库存（下单时）
     */
    boolean lockStock(Integer productId, Integer quantity, Integer orderId, Integer operatorId);

    /**
     * 释放商品库存（取消订单时）
     */
    boolean releaseStock(Integer productId, Integer quantity, Integer orderId, Integer operatorId);

    /**
     * 确认库存扣减（支付成功时）
     */
    boolean confirmStockDeduction(Integer orderId);

    /**
     * 手动调整库存
     */
    boolean adjustStock(Integer productId, Integer quantity, Integer operatorId, String notes);
} 