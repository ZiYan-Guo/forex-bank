package com.forex.limit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.forex.limit", "com.forex.common"})
@EnableDiscoveryClient
@MapperScan("com.forex.limit.infrastructure.mapper")
public class LimitApplication {
    public static void main(String[] args) {
        SpringApplication.run(LimitApplication.class, args);
    }
}
