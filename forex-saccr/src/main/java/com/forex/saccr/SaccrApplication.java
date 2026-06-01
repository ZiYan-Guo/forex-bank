package com.forex.saccr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.forex.saccr.infrastructure.mapper")
public class SaccrApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaccrApplication.class, args);
    }
}
