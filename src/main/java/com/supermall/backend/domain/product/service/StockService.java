package com.supermall.backend.domain.product.service;

import com.supermall.backend.domain.product.entity.StockMovement;
import com.supermall.backend.domain.product.entity.StockMovement.MovementType;

public interface StockService {
    /**
     * 创建库存变动记录
     */
    StockMovement createMovement(Long productId, Integer quantity, MovementType type, 
                                Long referenceId, Long operatorId, String notes);

    /**
     * 检查商品库存是否足够
     */
    boolean checkStock(Long productId, Integer quantity);

    /**
     * 锁定商品库存（下单时）
     */
    boolean lockStock(Long productId, Integer quantity, Long orderId, Long operatorId);

    /**
     * 释放商品库存（取消订单时）
     */
    boolean releaseStock(Long productId, Integer quantity, Long orderId, Long operatorId);

    /**
     * 确认库存扣减（支付成功时）
     */
    boolean confirmStockDeduction(Long orderId);

    /**
     * 手动调整库存
     */
    boolean adjustStock(Long productId, Integer quantity, Long operatorId, String notes);
} 