package com.forex.saccr.application.service;

import com.forex.common.base.dto.PageResp;
import com.forex.saccr.adapter.dto.CalculateCmd;
import com.forex.saccr.adapter.dto.SimmCalculateCmd;
import com.forex.saccr.domain.model.aggregate.SaccrResult;
import com.forex.saccr.domain.model.aggregate.SimmResult;
import com.forex.saccr.domain.model.query.SaccrQuery;
import com.forex.saccr.domain.model.query.SimmQuery;
import com.forex.saccr.domain.repository.SaccrResultRepository;
import com.forex.saccr.domain.repository.SimmResultRepository;
import com.forex.saccr.domain.service.SaccrCalculationService;
import com.forex.saccr.domain.service.SimmCalculationService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.UUID;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SaccrAppService {

    private final SaccrCalculationService saccrCalculationService;
    private final SimmCalculationService simmCalculationService;
    private final SaccrResultRepository saccrResultRepository;
    private final SimmResultRepository simmResultRepository;

    public SaccrResult calculateSaccr(CalculateCmd cmd) {
        String calcNo = "SACCR" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        SaccrResult input = SaccrResult.create(calcNo, cmd.getTradeId(), cmd.getTradeNo(),
                cmd.getCounterPartyId(), cmd.getCalcDate());
        input.updateResult(cmd.getRc(), cmd.getPfe());
        SaccrResult result = saccrCalculationService.calculate(input);
        return saccrResultRepository.save(result);
    }

    public SimmResult calculateSimm(SimmCalculateCmd cmd) {
        String calcNo = "SIMM" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
        SimmResult input = SimmResult.create(calcNo, cmd.getTradeId(), cmd.getTradeNo(),
                cmd.getCalcDate(), cmd.getNotionalAmount());
        SimmResult result = simmCalculationService.calculate(input);
        return simmResultRepository.save(result);
    }

    public SaccrResult getSaccrResult(String calcNo) {
        return saccrResultRepository.findByCalcNo(calcNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "SA-CCR计算结果不存在"));
    }

    public SimmResult getSimmResult(String calcNo) {
        return simmResultRepository.findByCalcNo(calcNo)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "SIMM计算结果不存在"));
    }

    public PageResp<SaccrResult> pageQuerySaccr(SaccrQuery query) {
        return saccrResultRepository.pageQuery(query);
    }

    public PageResp<SimmResult> pageQuerySimm(SimmQuery query) {
        return simmResultRepository.pageQuery(query);
    }
}
