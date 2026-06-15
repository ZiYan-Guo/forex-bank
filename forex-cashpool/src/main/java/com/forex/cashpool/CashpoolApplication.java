package com.forex.cashpool;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 资金池管理模块 - 启动类
 * Forex Cashpool Module - Application Entry Point
 * 提供资金池管理、额度计算、境外放款、轧差结算等功能
 * Provides cash pool management, quota calculation, overseas lending, netting settlement
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.forex.cashpool.infrastructure.mapper")
public class CashpoolApplication {
    public static void main(String[] args) {
        SpringApplication.run(CashpoolApplication.class, args);
    }
}
