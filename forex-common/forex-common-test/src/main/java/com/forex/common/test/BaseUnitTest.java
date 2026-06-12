package com.forex.common.test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 单元测试基类
 * 用于不需要 Spring 容器的纯逻辑测试
 */
@ExtendWith(MockitoExtension.class)
public class BaseUnitTest {
    // 基类：自动初始化 Mockito
}
