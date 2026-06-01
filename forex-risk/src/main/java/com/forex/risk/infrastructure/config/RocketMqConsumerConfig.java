package com.forex.risk.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * RocketMQ consumer configuration for risk event processing.
 * RocketMQ 消费者配置，用于处理风险事件。
 * Enabled via: forex.rocketmq.risk-consumer-enabled=true
 */
@Configuration
@ConditionalOnProperty(name = "forex.rocketmq.risk-consumer-enabled", havingValue = "true")
public class RocketMqConsumerConfig {

    // Future: configure RocketMQ consumer beans for FOREX_RISK_EVENT topic
    // with tags: MARGIN_CALL, POSITION_BREACH, LARGE_PAYMENT, FORCE_LIQUIDATION
}
