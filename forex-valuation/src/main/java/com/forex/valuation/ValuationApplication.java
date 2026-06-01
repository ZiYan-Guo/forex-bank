package com.forex.valuation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.forex.valuation.infrastructure.mapper")
public class ValuationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ValuationApplication.class, args);
    }
}
