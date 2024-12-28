package com.supermall.backend.common.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    /**
     * 需要的角色，可以是 USER, MERCHANT, ADMIN
     */
    String role() default "";

    /**
     * 是否需要商家权限
     */
    boolean requireMerchant() default false;
} 