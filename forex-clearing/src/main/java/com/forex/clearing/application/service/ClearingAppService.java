package com.forex.clearing.application.service;

import com.forex.clearing.application.command.GenerateClearingCmd;
import com.forex.clearing.domain.model.aggregate.ClearingInstruction;
import com.forex.clearing.domain.model.query.ClearingQuery;
import com.forex.clearing.domain.repository.ClearingInstructionRepository;
import com.forex.clearing.domain.service.ClearingDomainService;
import com.forex.common.base.annotation.RedisLock;
import com.forex.common.base.dto.PageResp;
import com.forex.common.base.exception.BusinessException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClearingAppService {

    private final ClearingDomainService clearingDomainService;
    private final ClearingInstructionRepository clearingInstructionRepository;

    public ClearingInstruction generateInstruction(GenerateClearingCmd cmd) {
        ClearingInstruction instruction = ClearingInstruction.create(
                cmd.getBizType(), cmd.getBizNo(), cmd.getClearingChannel(),
                cmd.getNostroAccount(), null,
                cmd.getPayCurrency(), cmd.getPayAmount(),
                cmd.getReceiveCurrency(), cmd.getReceiveAmount(),
                cmd.getValueDate(), null, null, null);

        ClearingInstruction saved = clearingDomainService.createInstruction(instruction);
        clearingDomainService.generateInstruction(saved);
        return saved;
    }

    @RedisLock(key = "#instructionNo")
    public void sendInstruction(String instructionNo) {
        ClearingInstruction instruction = clearingInstructionRepository
                .findByInstructionNo(instructionNo)
                .orElseThrow(() -> new BusinessException("清算指令不存在"));
        clearingDomainService.sendInstruction(instruction);
    }

    @RedisLock(key = "#instructionNo")
    public void acknowledgeInstruction(String instructionNo, String swiftRef) {
        ClearingInstruction instruction = clearingInstructionRepository
                .findByInstructionNo(instructionNo)
                .orElseThrow(() -> new BusinessException("清算指令不存在"));
        clearingDomainService.acknowledgeInstruction(instruction, swiftRef);
    }

    @RedisLock(key = "#instructionNo")
    public void settleInstruction(String instructionNo) {
        ClearingInstruction instruction = clearingInstructionRepository
                .findByInstructionNo(instructionNo)
                .orElseThrow(() -> new BusinessException("清算指令不存在"));
        clearingDomainService.settleInstruction(instruction);
    }

    @RedisLock(key = "#instructionNo")
    public void cancelInstruction(String instructionNo, String reason) {
        ClearingInstruction instruction = clearingInstructionRepository
                .findByInstructionNo(instructionNo)
                .orElseThrow(() -> new BusinessException("清算指令不存在"));
        instruction.cancel(reason);
        clearingInstructionRepository.save(instruction);
    }

    public ClearingInstruction getInstructionDetail(String instructionNo) {
        return clearingInstructionRepository.findByInstructionNo(instructionNo)
                .orElseThrow(() -> new BusinessException("清算指令不存在"));
    }

    public PageResp<ClearingInstruction> pageQuery(ClearingQuery query) {
        return clearingInstructionRepository.pageQuery(query);
    }
}
