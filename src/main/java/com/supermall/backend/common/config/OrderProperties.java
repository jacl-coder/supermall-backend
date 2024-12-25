package com.supermall.backend.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "order")
public class OrderProperties {
    private Integer autoCancelTimeout = 30;
    private Integer autoConfirmTimeout = 15;
} 