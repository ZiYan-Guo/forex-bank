package com.forex.payment.domain.service;

import com.forex.payment.domain.event.PaymentSentEvent;
import com.forex.payment.domain.event.PaymentSubmittedEvent;
import com.forex.payment.domain.model.aggregate.CrossBorderPayment;
import com.forex.payment.domain.model.valueobject.PaymentNo;
import com.forex.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentDomainService {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CrossBorderPayment createPayment(CrossBorderPayment payment) {
        String paymentNo = PaymentNo.generate(payment.getPaymentDirection()).getValue();
        payment.setPaymentNo(paymentNo);

        CrossBorderPayment saved = paymentRepository.save(payment);

        log.info("创建跨境支付: paymentNo={}, amount={} {}",
                saved.getPaymentNo(), saved.getPayAmount(), saved.getPayCurrency());
        return saved;
    }

    public void submitPayment(CrossBorderPayment payment) {
        payment.submit();
        paymentRepository.save(payment);

        eventPublisher.publishEvent(new PaymentSubmittedEvent(
                payment.getId(), payment.getPaymentNo(),
                payment.getPayAmount(), payment.getPayCurrency()));

        log.info("跨境支付已提交: paymentNo={}", payment.getPaymentNo());
    }

    public void approvePayment(CrossBorderPayment payment) {
        payment.approve(null);
        paymentRepository.save(payment);

        log.info("跨境支付已审批: paymentNo={}", payment.getPaymentNo());
    }

    public void processAmlResult(CrossBorderPayment payment, boolean passed, String reason) {
        if (passed) {
            payment.markAmlCheckPassed();
        } else {
            payment.markAmlCheckRejected();
        }
        paymentRepository.save(payment);

        log.info("反洗钱检查结果: paymentNo={}, passed={}, reason={}",
                payment.getPaymentNo(), passed, reason);
    }

    public void sendPayment(CrossBorderPayment payment, String swiftRef, String cipsRef) {
        payment.send();
        if (swiftRef != null && !swiftRef.isBlank()) {
            payment.updateSwiftRef(swiftRef);
        }
        if (cipsRef != null && !cipsRef.isBlank()) {
            payment.setCipsRef(cipsRef);
        }
        paymentRepository.save(payment);

        eventPublisher.publishEvent(new PaymentSentEvent(
                payment.getId(), payment.getPaymentNo(), payment.getSwiftRef()));

        log.info("跨境支付已发送: paymentNo={}, swiftRef={}", payment.getPaymentNo(), swiftRef);
    }

    public void cancelPayment(CrossBorderPayment payment, String reason) {
        payment.cancel(reason);
        paymentRepository.save(payment);

        log.info("跨境支付已取消: paymentNo={}, reason={}", payment.getPaymentNo(), reason);
    }

    public BigDecimal calculateFees(BigDecimal amount, String chargeBearer) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("金额必须大于0");
        }
        BigDecimal feeRate = getFeeRate(chargeBearer);
        return amount.multiply(feeRate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateSettlementAmount(BigDecimal payAmount, BigDecimal exchangeRate) {
        if (payAmount == null || payAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("支付金额必须大于0");
        }
        if (exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("汇率必须大于0");
        }
        return payAmount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getFeeRate(String chargeBearer) {
        if ("OUR".equalsIgnoreCase(chargeBearer)) {
            return new BigDecimal("0.002");
        } else if ("SHA".equalsIgnoreCase(chargeBearer)) {
            return new BigDecimal("0.001");
        }
        return new BigDecimal("0.0005");
    }
}
