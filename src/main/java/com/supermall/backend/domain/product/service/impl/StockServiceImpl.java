package com.supermall.backend.domain.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermall.backend.common.exception.BusinessException;
import com.supermall.backend.domain.product.entity.Product;
import com.supermall.backend.domain.product.entity.StockMovement;
import com.supermall.backend.domain.product.entity.StockMovement.MovementType;
import com.supermall.backend.domain.product.mapper.ProductMapper;
import com.supermall.backend.domain.product.mapper.StockMovementMapper;
import com.supermall.backend.domain.product.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockServiceImpl extends ServiceImpl<StockMovementMapper, StockMovement> implements StockService {

    private final ProductMapper productMapper;

    @Override
    @Transactional
    public StockMovement createMovement(Integer productId, Integer quantity, MovementType type,
                                      Integer referenceId, Integer operatorId, String notes) {
        StockMovement movement = new StockMovement();
        movement.setProductId(productId);
        movement.setQuantity(quantity);
        movement.setType(type);
        movement.setReferenceId(referenceId);
        movement.setOperatorId(operatorId);
        movement.setNotes(notes);
        
        save(movement);
        return movement;
    }

    @Override
    public boolean checkStock(Integer productId, Integer quantity) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return product.getStock() >= quantity;
    }

    @Override
    @Transactional
    public boolean lockStock(Integer productId, Integer quantity, Integer orderId, Integer operatorId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        if (product.getStock() < quantity) {
            throw new BusinessException("商品库存不足");
        }
        
        // 扣减库存
        product.setStock(product.getStock() - quantity);
        productMapper.updateById(product);
        
        // 创建库存变动记录
        createMovement(productId, -quantity, MovementType.ORDER_CREATE, orderId, operatorId, "订单创建锁定库存");
        
        return true;
    }

    @Override
    @Transactional
    public boolean releaseStock(Integer productId, Integer quantity, Integer orderId, Integer operatorId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 恢复库存
        product.setStock(product.getStock() + quantity);
        productMapper.updateById(product);
        
        // 创建库存变动记录
        createMovement(productId, quantity, MovementType.ORDER_CANCEL, orderId, operatorId, "订单取消释放库存");
        
        return true;
    }

    @Override
    @Transactional
    public boolean confirmStockDeduction(Integer orderId) {
        // 订单支付成功后，库存已经扣减，只需要修改库存变动记录的状态
        return true;
    }

    @Override
    @Transactional
    public boolean adjustStock(Integer productId, Integer quantity, Integer operatorId, String notes) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 调整库存
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new BusinessException("库存调整后不能小于0");
        }
        
        product.setStock(newStock);
        productMapper.updateById(product);
        
        // 创建库存变动记录
        createMovement(productId, quantity, MovementType.MANUAL_ADJUSTMENT, null, operatorId, notes);
        
        return true;
    }
} 