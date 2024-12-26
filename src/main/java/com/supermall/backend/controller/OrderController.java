package com.supermall.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.supermall.backend.common.api.CommonResult;
import com.supermall.backend.common.exception.ApiException;
import com.supermall.backend.domain.order.entity.Order;
import com.supermall.backend.domain.order.service.OrderService;
import com.supermall.backend.domain.order.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "订单管理", description = "订单管理接口")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @Operation(summary = "创建订单")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Order> create(@RequestBody Order order) {
        try {
            log.debug("创建订单: {}", order);
            Order savedOrder = orderService.createOrder(order);
            return CommonResult.success(savedOrder);
        } catch (ApiException e) {
            log.error("创建订单失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return CommonResult.failed("创建订单失败");
        }
    }
    
    @Operation(summary = "获取订单列表")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<IPage<OrderVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            log.debug("获取订单列表: pageNum={}, pageSize={}", pageNum, pageSize);
            IPage<OrderVO> page = orderService.listOrders(pageNum, pageSize);
            return CommonResult.success(page);
        } catch (ApiException e) {
            log.error("获取订单列表失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("获取订单列表失败", e);
            return CommonResult.failed("获取订单列表失败");
        }
    }
    
    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<OrderVO> getDetail(@PathVariable Long id) {
        try {
            log.debug("获取订单详情: id={}", id);
            OrderVO order = orderService.getOrderDetail(id);
            return CommonResult.success(order);
        } catch (ApiException e) {
            log.error("获取订单详情失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return CommonResult.failed("获取订单详情失败");
        }
    }
    
    @Operation(summary = "更新订单状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            log.debug("更新订单状态: id={}, status={}", id, status);
            orderService.updateStatus(id, status);
            return CommonResult.success(null);
        } catch (ApiException e) {
            log.error("更新订单状态失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("更新订单状态失败", e);
            return CommonResult.failed("更新订单状态失败");
        }
    }
    
    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResult<Void> delete(@PathVariable Long id) {
        try {
            log.debug("删除订单: id={}", id);
            orderService.removeById(id);
            return CommonResult.success(null);
        } catch (ApiException e) {
            log.error("删除订单失败: {}", e.getMessage());
            return CommonResult.failed(e.getMessage());
        } catch (Exception e) {
            log.error("删除订单失败", e);
            return CommonResult.failed("删除订单失败");
        }
    }
} 