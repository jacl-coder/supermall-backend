package com.supermall.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.supermall.backend.repository")
public class SupermallBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupermallBackendApplication.class, args);
    }

}
