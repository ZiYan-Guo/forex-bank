package com.forex.common.test;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 集成测试基类
 * 用于需要 Spring 容器的集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BaseIntegrationTest {
    // 基类：自动加载 Spring 容器
}
