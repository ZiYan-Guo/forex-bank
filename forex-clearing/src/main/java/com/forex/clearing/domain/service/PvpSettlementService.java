package com.forex.clearing.domain.service;

import com.forex.clearing.domain.model.aggregate.ClearingInstruction;
import com.forex.clearing.domain.model.aggregate.PvpSettlementPair;
import com.forex.clearing.domain.repository.ClearingInstructionRepository;
import com.forex.clearing.domain.repository.PvpSettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PvpSettlementService {

    private final PvpSettlementRepository pairRepository;
    private final ClearingInstructionRepository instructionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PvpSettlementPair createPvpPair(Long payInstructionId, Long receiveInstructionId) {
        ClearingInstruction payLeg = load(payInstructionId);
        ClearingInstruction recLeg = load(receiveInstructionId);
        String pairId = "PVP" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        PvpSettlementPair pair = PvpSettlementPair.create(pairId, payInstructionId, payLeg.getInstructionNo(),
                receiveInstructionId, recLeg.getInstructionNo(), payLeg.getPayCurrency(), payLeg.getPayAmount(),
                recLeg.getReceiveCurrency(), recLeg.getReceiveAmount(), payLeg.getValueDate());
        return pairRepository.save(pair);
    }

    public void executePvpSettlement(String pairId) {
        PvpSettlementPair pair = pairRepository.findByPairId(pairId)
                .orElseThrow(() -> new IllegalArgumentException("PVP pair not found"));
        pair.attempt();
        try {
            ClearingInstruction payLeg = load(pair.getPayInstructionId());
            ClearingInstruction recLeg = load(pair.getReceiveInstructionId());
            payLeg.settle(getNostroBalance(payLeg));
            recLeg.settle(getNostroBalance(recLeg));
            instructionRepository.save(payLeg);
            instructionRepository.save(recLeg);
            pair.settle();
            log.info("PVP settlement completed: pairId={}", pairId);
        } catch (Exception e) {
            pair.fail(e.getMessage());
            log.error("PVP settlement failed: pairId={}, error={}", pairId, e.getMessage());
        }
        pairRepository.save(pair);
    }

    private ClearingInstruction load(Long id) {
        return instructionRepository.findById(id).orElseThrow();
    }

    private BigDecimal getNostroBalance(ClearingInstruction i) {
        return i.getNostroBalanceBefore();
    }
}
