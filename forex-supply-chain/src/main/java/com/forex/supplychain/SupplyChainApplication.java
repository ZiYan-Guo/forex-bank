package com.forex.supplychain;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.forex.supplychain", "com.forex.common"})
@EnableDiscoveryClient
@MapperScan("com.forex.supplychain.infrastructure.mapper")
public class SupplyChainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SupplyChainApplication.class, args);
    }
}
