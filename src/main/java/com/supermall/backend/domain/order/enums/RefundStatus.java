package com.supermall.backend.domain.order.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum RefundStatus {
    PENDING("pending", "待处理"),
    APPROVED("approved", "已同意"),
    REJECTED("rejected", "已拒绝"),
    COMPLETED("completed", "已完成"),
    CANCELED("canceled", "已取消");

    @EnumValue
    private final String value;
    private final String description;

    RefundStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static RefundStatus fromValue(String value) {
        for (RefundStatus status : RefundStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown RefundStatus value: " + value);
    }
} 