package com.forex.margin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.forex.margin.infrastructure.mapper")
public class MarginApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarginApplication.class, args);
    }
}
