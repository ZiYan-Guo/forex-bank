package com.forex.rate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.forex.rate.infrastructure.mapper")
public class RateApplication {
    public static void main(String[] args) {
        SpringApplication.run(RateApplication.class, args);
    }
}
