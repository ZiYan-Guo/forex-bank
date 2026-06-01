package com.forex.risk.infrastructure.event;

import com.forex.risk.domain.model.aggregate.RiskMonitorLog;
import com.forex.risk.domain.service.RiskDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Cross-module risk event listener.
 * Listens for margin calls, position breaches, and large payments
 * from other modules, and automatically creates risk evaluation logs.
 * 跨模块风险事件监听器。监听保证金追缴、敞口超限、大额支付事件。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RiskEventListener {

    private final RiskDomainService riskDomainService;

    /**
     * Handle margin call events - create risk evaluation log.
     * 处理保证金追缴事件 - 自动创建风险评估日志。
     */
    @EventListener
    public void onMarginCalled(Object marginCalledEvent) {
        try {
            String logNo = "RK" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
            RiskMonitorLog riskLog = RiskMonitorLog.create(
                    logNo, null, "MARGIN",
                    extractBizNo(marginCalledEvent),
                    BigDecimal.ZERO, "CNY", LocalDateTime.now(),
                    "MARGIN_SHORTFALL", "保证金追缴",
                    "AML", "HIGH", BigDecimal.valueOf(75));
            riskDomainService.evaluateTransaction(riskLog);
            log.info("Created risk log for margin call event");
        } catch (Exception e) {
            log.error("Failed to process margin call event", e);
        }
    }

    @EventListener
    public void onPositionBreach(Object positionBreachEvent) {
        try {
            String logNo = "RK" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
            RiskMonitorLog riskLog = RiskMonitorLog.create(
                    logNo, null, "POSITION",
                    extractBizNo(positionBreachEvent),
                    BigDecimal.ZERO, "CNY", LocalDateTime.now(),
                    "POSITION_BREACH", "敞口超限",
                    "AML", "CRITICAL", BigDecimal.valueOf(95));
            riskDomainService.evaluateTransaction(riskLog);
            log.info("Created risk log for position breach event");
        } catch (Exception e) {
            log.error("Failed to process position breach event", e);
        }
    }

    @EventListener
    public void onLargePayment(Object paymentEvent) {
        try {
            String bizNo = extractBizNo(paymentEvent);
            String logNo = "RK" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
            RiskMonitorLog riskLog = RiskMonitorLog.create(
                    logNo, null, "PAYMENT",
                    bizNo,
                    BigDecimal.ZERO, "CNY", LocalDateTime.now(),
                    "LARGE_AMOUNT", "大额交易",
                    "AML", "MEDIUM", BigDecimal.valueOf(45));
            riskDomainService.evaluateTransaction(riskLog);
            log.info("Created risk log for large payment event");
        } catch (Exception e) {
            log.error("Failed to process payment event", e);
        }
    }

    private String extractBizNo(Object event) {
        try {
            return (String) event.getClass().getMethod("getBizNo").invoke(event);
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private Long extractCustomerId(Object event) {
        try {
            return (Long) event.getClass().getMethod("getCustomerId").invoke(event);
        } catch (Exception e) {
            return null;
        }
    }
}
