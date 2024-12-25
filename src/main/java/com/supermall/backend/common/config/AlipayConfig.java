package com.supermall.backend.common.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.alipay")
public class AlipayConfig {
    private String appId;
    private String privateKey;
    private String publicKey;
    private String alipayPublicKey;
    private String notifyUrl;
    private String returnUrl;
    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    
    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(
            gatewayUrl,
            appId,
            privateKey,
            "json",
            "UTF-8",
            alipayPublicKey,
            "RSA2"
        );
    }
} 