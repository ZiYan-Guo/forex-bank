package com.forex.clearing.domain.repository;

import com.forex.clearing.domain.model.aggregate.ClearingInstruction;
import com.forex.clearing.domain.model.query.ClearingQuery;
import com.forex.common.base.dto.PageResp;

import java.util.Optional;

public interface ClearingInstructionRepository {

    ClearingInstruction save(ClearingInstruction instruction);

    Optional<ClearingInstruction> findById(Long id);

    Optional<ClearingInstruction> findByInstructionNo(String instructionNo);

    Optional<ClearingInstruction> findByBizNo(String bizNo);

    PageResp<ClearingInstruction> pageQuery(ClearingQuery query);
}
