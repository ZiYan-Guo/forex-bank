package com.forex.payment.infrastructure.event;

import com.forex.payment.domain.event.PaymentSentEvent;
import com.forex.payment.domain.event.PaymentSubmittedEvent;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Cross-module payment event listener.
 * Listens for payment lifecycle events and triggers downstream processes
 * such as clearing instruction generation or notification dispatch.
 * 跨模块支付事件监听器。监听支付生命周期事件，触发清算指令生成或通知等下游流程。
 */
@Slf4j
@Component
public class PaymentEventListener {

    /**
     * Handle payment submitted events — log and optionally trigger AML re-check or booking.
     * 处理支付提交事件 —— 记录日志，可选触发反洗钱复核或记账。
     */
    @EventListener
    public void onPaymentSubmitted(PaymentSubmittedEvent event) {
        log.info("Payment submitted: paymentId={}, paymentNo={}, amount={} {}",
                event.getPaymentId(), event.getPaymentNo(), event.getAmount(), event.getCurrency());
    }

    /**
     * Handle payment sent events — log and optionally trigger clearing or SWIFT GPI tracking.
     * 处理支付发送事件 —— 记录日志，可选触发清算或SWIFT GPI追踪。
     */
    @EventListener
    public void onPaymentSent(PaymentSentEvent event) {
        log.info("Payment sent: paymentId={}, paymentNo={}, swiftRef={}",
                event.getPaymentId(), event.getPaymentNo(), event.getSwiftRef());
    }
}
