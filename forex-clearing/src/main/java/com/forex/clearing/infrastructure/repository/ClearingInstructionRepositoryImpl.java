package com.forex.clearing.infrastructure.repository;

import com.forex.clearing.domain.model.aggregate.ClearingInstruction;
import com.forex.clearing.domain.model.query.ClearingQuery;
import com.forex.clearing.domain.repository.ClearingInstructionRepository;
import com.forex.clearing.infrastructure.mapper.ClearingInstructionMapper;
import com.forex.clearing.infrastructure.persistence.ClearingInstructionPO;
import com.forex.common.base.dto.PageResp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClearingInstructionRepositoryImpl implements ClearingInstructionRepository {

    private final ClearingInstructionMapper clearingInstructionMapper;

    @Override
    public ClearingInstruction save(ClearingInstruction instruction) {
        ClearingInstructionPO po = toPO(instruction);
        if (instruction.getId() == null) {
            clearingInstructionMapper.insert(po);
        } else {
            clearingInstructionMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<ClearingInstruction> findById(Long id) {
        ClearingInstructionPO po = clearingInstructionMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<ClearingInstruction> findByInstructionNo(String instructionNo) {
        ClearingInstructionPO po = clearingInstructionMapper.selectByInstructionNo(instructionNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<ClearingInstruction> findByBizNo(String bizNo) {
        ClearingInstructionPO po = clearingInstructionMapper.selectByBizNo(bizNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public PageResp<ClearingInstruction> pageQuery(ClearingQuery query) {
        Page<ClearingInstructionPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        List<ClearingInstructionPO> poList = clearingInstructionMapper.pageQuery(page, query);
        page.setRecords(poList);

        List<ClearingInstruction> records = page.getRecords().stream().map(this::toDomain).toList();
        return PageResp.of(page.getTotal(), records, (int) page.getCurrent(), (int) page.getSize());
    }

    private ClearingInstruction toDomain(ClearingInstructionPO po) {
        return ClearingInstruction.reconstitute(
                po.getId(),
                po.getInstructionNo(),
                po.getBizType(),
                po.getBizNo(),
                po.getClearingChannel(),
                po.getNostroAccount(),
                po.getCounterPartyAccount(),
                po.getPayCurrency(),
                po.getPayAmount(),
                po.getReceiveCurrency(),
                po.getReceiveAmount(),
                po.getValueDate(),
                po.getSettlementDate(),
                po.getSettlementType(),
                po.getInstructionStatus(),
                po.getSwiftRef(),
                po.getCipsRef(),
                po.getNostroBalanceBefore(),
                po.getNostroBalanceAfter(),
                po.getSendTime(),
                po.getAckTime(),
                po.getSettleTime(),
                po.getOperatorId(),
                po.getRemark()
        );
    }

    private ClearingInstructionPO toPO(ClearingInstruction instruction) {
        ClearingInstructionPO po = new ClearingInstructionPO();
        po.setId(instruction.getId());
        po.setInstructionNo(instruction.getInstructionNo());
        po.setBizType(instruction.getBizType());
        po.setBizNo(instruction.getBizNo());
        po.setClearingChannel(instruction.getClearingChannel());
        po.setNostroAccount(instruction.getNostroAccount());
        po.setCounterPartyAccount(instruction.getCounterPartyAccount());
        po.setPayCurrency(instruction.getPayCurrency());
        po.setPayAmount(instruction.getPayAmount());
        po.setReceiveCurrency(instruction.getReceiveCurrency());
        po.setReceiveAmount(instruction.getReceiveAmount());
        po.setValueDate(instruction.getValueDate());
        po.setSettlementDate(instruction.getSettlementDate());
        po.setSettlementType(instruction.getSettlementType());
        po.setInstructionStatus(instruction.getInstructionStatus());
        po.setSwiftRef(instruction.getSwiftRef());
        po.setCipsRef(instruction.getCipsRef());
        po.setNostroBalanceBefore(instruction.getNostroBalanceBefore());
        po.setNostroBalanceAfter(instruction.getNostroBalanceAfter());
        po.setSendTime(instruction.getSendTime());
        po.setAckTime(instruction.getAckTime());
        po.setSettleTime(instruction.getSettleTime());
        po.setOperatorId(instruction.getOperatorId());
        po.setRemark(instruction.getRemark());
        po.setVersion(instruction.getVersion());
        return po;
    }
}
