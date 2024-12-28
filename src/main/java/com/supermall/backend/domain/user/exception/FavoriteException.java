package com.supermall.backend.domain.user.exception;

import com.supermall.backend.common.exception.BusinessException;

public class FavoriteException extends BusinessException {
    public static final String ALREADY_FAVORITE = "该商品已在收藏夹中";
    public static final String NOT_FOUND = "收藏记录不存在";
    public static final String NO_PERMISSION = "您没有权限操作此收藏";
    public static final String PRODUCT_NOT_FOUND = "商品不存在或已下架";

    public FavoriteException(String message) {
        super(message);
    }
} 