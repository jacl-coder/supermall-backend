package com.supermall.backend.domain.order.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public enum OrderStatus {
    PENDING_PAYMENT("pending_payment", "待付款"),
    PAID("paid", "已付款"),
    SHIPPED("shipped", "已发货"),
    COMPLETED("completed", "已完成"),
    CANCELED("canceled", "已取消"),
    REFUNDED("refunded", "已退款"),
    PARTIALLY_REFUNDED("partially_refunded", "部分退款");

    @EnumValue
    private final String value;
    private final String description;
    private final List<OrderStatus> nextStatus;

    OrderStatus(String value, String description) {
        this.value = value;
        this.description = description;
        this.nextStatus = initNextStatus();
    }

    private List<OrderStatus> initNextStatus() {
        return switch (this) {
            case PENDING_PAYMENT -> Arrays.asList(PAID, CANCELED);
            case PAID -> Arrays.asList(SHIPPED, CANCELED, REFUNDED);
            case SHIPPED -> Collections.singletonList(COMPLETED);
            case COMPLETED -> Arrays.asList(REFUNDED, PARTIALLY_REFUNDED);
            case CANCELED, REFUNDED -> Collections.emptyList();
            case PARTIALLY_REFUNDED -> Collections.singletonList(REFUNDED);
        };
    }

    public static OrderStatus fromValue(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown OrderStatus value: " + value);
    }

    public boolean canTransitionTo(OrderStatus nextStatus) {
        return this.nextStatus.contains(nextStatus);
    }

    public String getStatusChangeMessage(OrderStatus nextStatus) {
        if (!canTransitionTo(nextStatus)) {
            return String.format("%s状态的订单不能变更为%s状态", 
                this.getDescription(), nextStatus.getDescription());
        }
        return null;
    }
} 