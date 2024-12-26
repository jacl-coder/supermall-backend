package com.supermall.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    /**
     * JWT加解密使用的密钥
     */
    private String secret;
    /**
     * JWT的超期限时间(单位：秒)
     */
    private Long expiration;
    /**
     * JWT负载中拿到开头
     */
    private String tokenPrefix;
    /**
     * JWT存储的请求头
     */
    private String header;
} 