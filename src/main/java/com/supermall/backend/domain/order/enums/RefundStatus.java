package com.supermall.backend.domain.order.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public enum RefundStatus {
    PENDING("pending", "待处理"),
    APPROVED("approved", "已同意"),
    REJECTED("rejected", "已拒绝"),
    PROCESSING("processing", "处理中"),
    COMPLETED("completed", "已完成"),
    FAILED("failed", "退款失败"),
    CANCELED("canceled", "已取消");

    @EnumValue
    private final String value;
    private final String description;
    private final List<RefundStatus> nextStatus;

    RefundStatus(String value, String description) {
        this.value = value;
        this.description = description;
        this.nextStatus = initNextStatus();
    }

    private List<RefundStatus> initNextStatus() {
        return switch (this) {
            case PENDING -> Arrays.asList(APPROVED, REJECTED, CANCELED);
            case APPROVED -> Collections.singletonList(PROCESSING);
            case REJECTED -> Collections.emptyList();
            case PROCESSING -> Arrays.asList(COMPLETED, FAILED);
            case COMPLETED -> Collections.emptyList();
            case FAILED -> Arrays.asList(PROCESSING, CANCELED);
            case CANCELED -> Collections.emptyList();
        };
    }

    public static RefundStatus fromValue(String value) {
        for (RefundStatus status : RefundStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown RefundStatus value: " + value);
    }

    public boolean canTransitionTo(RefundStatus nextStatus) {
        return this.nextStatus.contains(nextStatus);
    }

    public String getStatusChangeMessage(RefundStatus nextStatus) {
        if (!canTransitionTo(nextStatus)) {
            return String.format("%s状态的退款不能变更为%s状态", 
                this.getDescription(), nextStatus.getDescription());
        }
        return null;
    }
} 