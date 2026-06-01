package com.forex.clearing.domain.service;

import com.forex.clearing.domain.event.InstructionSentEvent;
import com.forex.clearing.domain.model.aggregate.ClearingInstruction;
import com.forex.clearing.domain.model.valueobject.ClearingAmount;
import com.forex.clearing.domain.repository.ClearingInstructionRepository;
import com.forex.common.base.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClearingDomainService {

    private final ClearingInstructionRepository clearingInstructionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ClearingInstruction createInstruction(ClearingInstruction instruction) {
        ClearingInstruction saved = clearingInstructionRepository.save(instruction);
        log.info("Created clearing instruction: bizNo={}, channel={}, payAmount={} {}",
                saved.getBizNo(), saved.getClearingChannel(),
                saved.getPayAmount(), saved.getPayCurrency());
        return saved;
    }

    public void generateInstruction(ClearingInstruction instruction) {
        String instructionNo = generateInstructionNo(instruction.getClearingChannel());
        BigDecimal nostroBalanceBefore = getNostroBalance(instruction.getNostroAccount(),
                instruction.getPayCurrency());

        instruction.generate(instructionNo, nostroBalanceBefore);
        clearingInstructionRepository.save(instruction);

        log.info("Generated clearing instruction: instructionNo={}, nostroBalanceBefore={}",
                instructionNo, nostroBalanceBefore);
    }

    public void sendInstruction(ClearingInstruction instruction) {
        instruction.send();
        clearingInstructionRepository.save(instruction);

        eventPublisher.publishEvent(new InstructionSentEvent(
                instruction.getId(), instruction.getInstructionNo(),
                instruction.getClearingChannel()));

        log.info("Sent clearing instruction: instructionNo={}, channel={}",
                instruction.getInstructionNo(), instruction.getClearingChannel());
    }

    public void acknowledgeInstruction(ClearingInstruction instruction, String swiftRef) {
        if (swiftRef == null || swiftRef.isBlank()) {
            throw new BusinessException("SWIFT参考号不能为空");
        }
        instruction.acknowledge(swiftRef);
        clearingInstructionRepository.save(instruction);

        log.info("Acknowledged clearing instruction: instructionNo={}, swiftRef={}",
                instruction.getInstructionNo(), swiftRef);
    }

    public void settleInstruction(ClearingInstruction instruction) {
        BigDecimal nostroBalanceAfter = calculateNostroBalance(instruction.getNostroBalanceBefore(),
                instruction.getPayAmount(), instruction.getReceiveAmount());

        instruction.settle(nostroBalanceAfter);
        clearingInstructionRepository.save(instruction);

        log.info("Settled clearing instruction: instructionNo={}, nostroBalanceAfter={}",
                instruction.getInstructionNo(), nostroBalanceAfter);
    }

    private String generateInstructionNo(String channel) {
        String prefix = switch (channel) {
            case "SWIFT" -> "SW";
            case "CIPS" -> "CP";
            case "CFXPS" -> "CF";
            case "LOCAL" -> "LC";
            default -> "CL";
        };
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String random = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        return prefix + timestamp + random;
    }

    private BigDecimal getNostroBalance(String nostroAccount, String currency) {
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateNostroBalance(BigDecimal balanceBefore, BigDecimal payAmount,
                                              BigDecimal receiveAmount) {
        if (balanceBefore == null) {
            balanceBefore = BigDecimal.ZERO;
        }
        if (payAmount == null) {
            payAmount = BigDecimal.ZERO;
        }
        if (receiveAmount == null) {
            receiveAmount = BigDecimal.ZERO;
        }
        return balanceBefore.subtract(payAmount).add(receiveAmount);
    }
}
