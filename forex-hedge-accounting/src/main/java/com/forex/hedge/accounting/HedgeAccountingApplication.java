package com.forex.hedge.accounting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 套保会计微服务启动类.
 * Hedge Accounting Microservice Application.
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.forex.hedge.accounting.infrastructure.mapper")
public class HedgeAccountingApplication {
    public static void main(String[] args) {
        SpringApplication.run(HedgeAccountingApplication.class, args);
    }
}
