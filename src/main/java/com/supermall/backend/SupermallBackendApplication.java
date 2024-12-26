package com.supermall.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 主应用类
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.supermall.backend.domain.*.mapper")
public class SupermallBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SupermallBackendApplication.class, args);
    }
}
