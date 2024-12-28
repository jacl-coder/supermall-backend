package com.supermall.backend.domain.review.exception;

import com.supermall.backend.common.exception.BusinessException;

public class ReviewException extends BusinessException {
    public static final String ALREADY_REVIEWED = "该订单商品已评价";
    public static final String REVIEW_NOT_FOUND = "评论不存在";
    public static final String NO_PERMISSION = "您没有权限操作此评论";
    public static final String ORDER_NOT_COMPLETED = "订单尚未完成，无法评价";
    public static final String INVALID_RATING = "评分必须在1-5之间";

    public ReviewException(String message) {
        super(message);
    }
} 