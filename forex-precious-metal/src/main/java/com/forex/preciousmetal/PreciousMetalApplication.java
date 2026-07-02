package com.forex.preciousmetal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.forex.preciousmetal", "com.forex.common"})
@EnableDiscoveryClient
@MapperScan("com.forex.preciousmetal.infrastructure.mapper")
public class PreciousMetalApplication {
    public static void main(String[] args) {
        SpringApplication.run(PreciousMetalApplication.class, args);
    }
}
