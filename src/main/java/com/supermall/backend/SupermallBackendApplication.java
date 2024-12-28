package com.supermall.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主应用类
 */
@SpringBootApplication
@MapperScan("com.supermall.backend.domain.*.mapper")
@EnableConfigurationProperties
@EnableAsync
@EnableScheduling
public class SupermallBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SupermallBackendApplication.class, args);
    }
}
