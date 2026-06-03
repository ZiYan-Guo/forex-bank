package com.forex.payment.application.service;

import com.forex.payment.application.command.CreatePaymentCmd;
import com.forex.payment.domain.model.aggregate.CrossBorderPayment;
import com.forex.payment.domain.repository.PaymentRepository;
import com.forex.payment.domain.service.PaymentDomainService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchPaymentService {
    private final PaymentDomainService paymentDomainService;
    private final PaymentRepository paymentRepository;

    public static final BigDecimal CIPS_MAX_AMOUNT = new BigDecimal("100000000");
    public static final BigDecimal SWIFT_MAX_AMOUNT = new BigDecimal("50000000");

    public BatchResult processBatch(List<CreatePaymentCmd> commands, String channel) {
        BatchResult result = new BatchResult();
        for (CreatePaymentCmd cmd : commands) {
            try {
                List<CrossBorderPayment> splits = splitOversized(cmd);
                for (CrossBorderPayment payment : splits) {
                    paymentDomainService.createPayment(payment);
                    result.addSuccess(payment.getPaymentNo());
                }
            } catch (Exception e) {
                result.addFailure(cmd.getPayCurrency() + "_" + cmd.getPayAmount(), e.getMessage());
            }
        }
        log.info("Batch processed: {} success, {} failures", result.getSuccessCount(), result.getFailureCount());
        return result;
    }

    public List<CrossBorderPayment> splitOversized(CreatePaymentCmd cmd) {
        List<CrossBorderPayment> payments = new ArrayList<>();
        BigDecimal maxAmount = "CIPS".equals(cmd.getPaymentType()) ? CIPS_MAX_AMOUNT : SWIFT_MAX_AMOUNT;
        BigDecimal remaining = cmd.getPayAmount();
        int splitIndex = 1;
        while (remaining.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal chunk = remaining.compareTo(maxAmount) > 0 ? maxAmount : remaining;
            CreatePaymentCmd chunkCmd = cloneWithAmount(cmd, chunk, splitIndex);
            CrossBorderPayment payment = buildPayment(chunkCmd);
            payments.add(payment);
            remaining = remaining.subtract(chunk);
            splitIndex++;
        }
        return payments;
    }

    private CreatePaymentCmd cloneWithAmount(CreatePaymentCmd cmd, BigDecimal amount, int index) {
        CreatePaymentCmd clone = new CreatePaymentCmd();
        clone.setCustomerId(cmd.getCustomerId());
        clone.setPaymentType(cmd.getPaymentType());
        clone.setPayAmount(amount);
        clone.setPayCurrency(cmd.getPayCurrency());
        clone.setBeneficiaryName(cmd.getBeneficiaryName());
        clone.setBeneficiaryAccount(cmd.getBeneficiaryAccount());
        clone.setBeneficiaryBank(cmd.getBeneficiaryBank());
        clone.setBeneficiarySwift(cmd.getBeneficiarySwift());
        clone.setBeneficiaryCountry(cmd.getBeneficiaryCountry());
        clone.setReceivingBankCode(cmd.getReceivingBankCode());
        clone.setPaymentPurpose(cmd.getPaymentPurpose() + " (Split " + index + ")");
        clone.setChargeBearer(cmd.getChargeBearer());
        clone.setValueDate(cmd.getValueDate());
        return clone;
    }

    private CrossBorderPayment buildPayment(CreatePaymentCmd cmd) {
        String beneficiaryInfo = String.format("{\"name\":\"%s\",\"account\":\"%s\",\"bank\":\"%s\",\"swift\":\"%s\"}",
                cmd.getBeneficiaryName() != null ? cmd.getBeneficiaryName() : "",
                cmd.getBeneficiaryAccount() != null ? cmd.getBeneficiaryAccount() : "",
                cmd.getBeneficiaryBank() != null ? cmd.getBeneficiaryBank() : "",
                cmd.getBeneficiarySwift() != null ? cmd.getBeneficiarySwift() : "");
        return CrossBorderPayment.create(cmd.getCustomerId(), "OUTWARD",
                cmd.getPaymentType(), cmd.getPayAmount(), cmd.getPayCurrency(),
                BigDecimal.ZERO, BigDecimal.ZERO, null,
                beneficiaryInfo, null, null, cmd.getReceivingBankCode(),
                null, null, cmd.getPaymentPurpose(), null,
                cmd.getChargeBearer(), cmd.getValueDate(), null, null);
    }
}
