package com.forex.clearing.application.service;

import com.forex.clearing.domain.model.aggregate.TradeConfirmation;
import com.forex.clearing.domain.repository.TradeConfirmationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ConfirmationWorkflowService {

    private final TradeConfirmationRepository confirmationRepository;

    /**
     * Manual intervention for confirmation failures.
     * ACTIONS: ACCEPT_EXTERNAL, REJECT_EXTERNAL, AMEND_INTERNAL, FORCE_MATCH
     * 人工干预确认失败。
     */
    public void resolveIntervention(String confirmId, String action, String comment, Long operatorId) {
        TradeConfirmation cfm = confirmationRepository.findByConfirmId(confirmId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Confirmation not found"));
        cfm.resolve(action, comment);
        confirmationRepository.save(cfm);
        log.info("Confirmation resolved: confirmId={}, action={}, operator={}", confirmId, action, operatorId);
    }
}
