package com.supermall.backend.domain.order.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    private List<OrderStatus> nextStatus;

    private static final Map<OrderStatus, List<OrderStatus>> NEXT_STATUS_MAP = new HashMap<>();

    static {
        NEXT_STATUS_MAP.put(PENDING_PAYMENT, Arrays.asList(PAID, CANCELED));
        NEXT_STATUS_MAP.put(PAID, Arrays.asList(SHIPPED, CANCELED, REFUNDED));
        NEXT_STATUS_MAP.put(SHIPPED, Collections.singletonList(COMPLETED));
        NEXT_STATUS_MAP.put(COMPLETED, Arrays.asList(REFUNDED, PARTIALLY_REFUNDED));
        NEXT_STATUS_MAP.put(CANCELED, Collections.emptyList());
        NEXT_STATUS_MAP.put(REFUNDED, Collections.emptyList());
        NEXT_STATUS_MAP.put(PARTIALLY_REFUNDED, Collections.singletonList(REFUNDED));

        for (OrderStatus status : values()) {
            status.nextStatus = NEXT_STATUS_MAP.get(status);
        }
    }

    OrderStatus(String value, String description) {
        this.value = value;
        this.description = description;
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