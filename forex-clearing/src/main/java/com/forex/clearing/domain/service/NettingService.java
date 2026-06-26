package com.forex.clearing.domain.service;

import com.forex.clearing.domain.model.aggregate.ClearingInstruction;
import com.forex.clearing.domain.model.valueobject.NettingPosition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NettingService {

    public List<NettingPosition> calculateBilateralNetting(List<ClearingInstruction> instructions) {
        Map<String, NettingPosition> positionMap = new LinkedHashMap<>();
        for (ClearingInstruction instruction : instructions) {
            String payCurrency = instruction.getPayCurrency();
            String receiveCurrency = instruction.getReceiveCurrency();

            positionMap.compute(payCurrency, (k, v) -> {
                if (v == null) {
                    return NettingPosition.of(payCurrency, instruction.getPayAmount(), BigDecimal.ZERO);
                }
                return v.add(instruction.getPayAmount(), BigDecimal.ZERO);
            });

            positionMap.compute(receiveCurrency, (k, v) -> {
                if (v == null) {
                    return NettingPosition.of(receiveCurrency, BigDecimal.ZERO, instruction.getReceiveAmount());
                }
                return v.add(BigDecimal.ZERO, instruction.getReceiveAmount());
            });
        }
        return new ArrayList<>(positionMap.values());
    }
}
