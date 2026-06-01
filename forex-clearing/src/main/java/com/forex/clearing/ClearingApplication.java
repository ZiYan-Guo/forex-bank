package com.forex.clearing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.forex.clearing.infrastructure.mapper")
public class ClearingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClearingApplication.class, args);
    }
}
