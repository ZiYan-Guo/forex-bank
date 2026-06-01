package com.forex.payment.application.service;

import com.forex.common.base.dto.PageResp;
import com.forex.payment.application.command.BlacklistHitCmd;
import com.forex.payment.application.command.CreatePaymentCmd;
import com.forex.payment.application.command.SendPaymentCmd;
import com.forex.payment.domain.model.dto.PaymentQuery;
import com.forex.payment.domain.model.aggregate.CrossBorderPayment;
import com.forex.payment.domain.model.entity.BlacklistHit;
import com.forex.payment.domain.repository.BlacklistHitRepository;
import com.forex.payment.domain.repository.PaymentRepository;
import com.forex.payment.domain.service.PaymentDomainService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentAppService {

    private final PaymentRepository paymentRepository;
    private final BlacklistHitRepository blacklistHitRepository;
    private final PaymentDomainService paymentDomainService;

    public CrossBorderPayment createOutwardPayment(CreatePaymentCmd cmd) {
        CrossBorderPayment payment = CrossBorderPayment.create(
                cmd.getCustomerId(),
                "OUTWARD",
                cmd.getPaymentType(),
                cmd.getPayAmount(),
                cmd.getPayCurrency(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                cmd.getSenderName(),
                cmd.getBeneficiaryName(),
                cmd.getIntermediaryBank(),
                cmd.getPayingBankCode(),
                cmd.getReceivingBankCode(),
                null,
                null,
                cmd.getPaymentPurpose(),
                cmd.getBankPurposeCode(),
                cmd.getChargeBearer(),
                cmd.getValueDate(),
                null,
                cmd.getRemark()
        );
        return paymentDomainService.createPayment(payment);
    }

    public CrossBorderPayment createInwardPayment(CreatePaymentCmd cmd) {
        CrossBorderPayment payment = CrossBorderPayment.create(
                cmd.getCustomerId(),
                "INWARD",
                cmd.getPaymentType(),
                cmd.getPayAmount(),
                cmd.getPayCurrency(),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                cmd.getSenderName(),
                cmd.getBeneficiaryName(),
                cmd.getIntermediaryBank(),
                cmd.getPayingBankCode(),
                cmd.getReceivingBankCode(),
                null,
                null,
                cmd.getPaymentPurpose(),
                cmd.getBankPurposeCode(),
                cmd.getChargeBearer(),
                cmd.getValueDate(),
                null,
                cmd.getRemark()
        );
        return paymentDomainService.createPayment(payment);
    }

    public CrossBorderPayment getPaymentDetail(String paymentNo) {
        return paymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new IllegalArgumentException("支付记录不存在"));
    }

    public PageResp<CrossBorderPayment> pageQuery(PaymentQuery query) {
        com.forex.payment.domain.model.dto.PaymentQuery domainQuery =
                new com.forex.payment.domain.model.dto.PaymentQuery();
        domainQuery.setPageNum(query.getPageNum());
        domainQuery.setPageSize(query.getPageSize());
        domainQuery.setPaymentNo(query.getPaymentNo());
        domainQuery.setCustomerId(query.getCustomerId());
        domainQuery.setPaymentDirection(query.getPaymentDirection());
        domainQuery.setPaymentType(query.getPaymentType());
        domainQuery.setPaymentStatus(query.getPaymentStatus());
        domainQuery.setStartDate(query.getStartDate());
        domainQuery.setEndDate(query.getEndDate());
        return paymentRepository.pageQuery(domainQuery);
    }

    @Transactional
    public void submitPayment(String paymentNo) {
        CrossBorderPayment payment = paymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new IllegalArgumentException("支付记录不存在"));
        paymentDomainService.submitPayment(payment);
    }

    @Transactional
    public void approvePayment(String paymentNo) {
        CrossBorderPayment payment = paymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new IllegalArgumentException("支付记录不存在"));
        paymentDomainService.approvePayment(payment);
    }

    @Transactional
    public void processAmlCheck(String paymentNo, boolean passed, String reason) {
        CrossBorderPayment payment = paymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new IllegalArgumentException("支付记录不存在"));
        paymentDomainService.processAmlResult(payment, passed, reason);
    }

    @Transactional
    public CrossBorderPayment sendPayment(String paymentNo, String swiftRef, String cipsRef) {
        CrossBorderPayment payment = paymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new IllegalArgumentException("支付记录不存在"));
        paymentDomainService.sendPayment(payment, swiftRef, cipsRef);
        return payment;
    }

    @Transactional
    public void cancelPayment(String paymentNo, String reason) {
        CrossBorderPayment payment = paymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new IllegalArgumentException("支付记录不存在"));
        paymentDomainService.cancelPayment(payment, reason);
    }

    public CrossBorderPayment updateGpiStatus(String paymentNo, String gpiStatus, String trackingId) {
        CrossBorderPayment payment = paymentRepository.findByPaymentNo(paymentNo)
                .orElseThrow(() -> new IllegalArgumentException("支付记录不存在"));
        payment.updateGpiStatus(gpiStatus);
        if (trackingId != null && !trackingId.isBlank()) {
            payment.setCipsRef(null);
        }
        paymentRepository.save(payment);
        return payment;
    }

    public void recordBlacklistHit(Long paymentId, String paymentNo, BlacklistHitCmd cmd) {
        BlacklistHit hit = new BlacklistHit(
                null,
                paymentId,
                paymentNo,
                cmd.getHitType(),
                cmd.getHitListName(),
                cmd.getHitField(),
                cmd.getHitValue(),
                cmd.getMatchScore(),
                LocalDateTime.now(),
                cmd.getCheckResult(),
                null,
                null,
                null
        );
        blacklistHitRepository.save(hit);
    }
}
