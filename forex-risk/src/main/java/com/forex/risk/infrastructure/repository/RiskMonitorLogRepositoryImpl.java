package com.forex.risk.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.risk.domain.model.aggregate.RiskMonitorLog;
import com.forex.risk.domain.model.query.RiskQuery;
import com.forex.risk.domain.repository.RiskMonitorLogRepository;
import com.forex.risk.infrastructure.mapper.RiskMonitorLogMapper;
import com.forex.risk.infrastructure.persistence.RiskMonitorLogPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RiskMonitorLogRepositoryImpl implements RiskMonitorLogRepository {

    private final RiskMonitorLogMapper riskMonitorLogMapper;

    @Override
    public RiskMonitorLog save(RiskMonitorLog log) {
        RiskMonitorLogPO po = toPO(log);
        if (log.getId() == null) {
            riskMonitorLogMapper.insert(po);
        } else {
            riskMonitorLogMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<RiskMonitorLog> findById(Long id) {
        RiskMonitorLogPO po = riskMonitorLogMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<RiskMonitorLog> findByLogNo(String logNo) {
        RiskMonitorLogPO po = riskMonitorLogMapper.selectByLogNo(logNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<RiskMonitorLog> findByBizNo(String bizNo) {
        return riskMonitorLogMapper.selectByBizNo(bizNo).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public PageResp<RiskMonitorLog> pageQuery(RiskQuery query) {
        Page<RiskMonitorLogPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<RiskMonitorLogPO> result = riskMonitorLogMapper.pageQuery(page, query);
        List<RiskMonitorLog> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    private RiskMonitorLog toDomain(RiskMonitorLogPO po) {
        return RiskMonitorLog.reconstitute(
                po.getId(), po.getLogNo(), po.getCustomerId(),
                po.getBizType(), po.getBizNo(),
                po.getTransactionAmount(), po.getTransactionCurrency(),
                po.getTransactionTime(), po.getMonitorRuleCode(),
                po.getMonitorRuleName(), po.getRiskCategory(),
                po.getRiskLevel(), po.getRiskScore(),
                po.getCheckResult(), po.getOperatorId(),
                po.getHandleTime(), po.getHandleRemark());
    }

    private RiskMonitorLogPO toPO(RiskMonitorLog log) {
        RiskMonitorLogPO po = new RiskMonitorLogPO();
        po.setId(log.getId());
        po.setLogNo(log.getLogNo());
        po.setCustomerId(log.getCustomerId());
        po.setBizType(log.getBizType());
        po.setBizNo(log.getBizNo());
        po.setTransactionAmount(log.getTransactionAmount());
        po.setTransactionCurrency(log.getTransactionCurrency());
        po.setTransactionTime(log.getTransactionTime());
        po.setMonitorRuleCode(log.getMonitorRuleCode());
        po.setMonitorRuleName(log.getMonitorRuleName());
        po.setRiskCategory(log.getRiskCategory());
        po.setRiskLevel(log.getRiskLevel());
        po.setRiskScore(log.getRiskScore());
        po.setCheckResult(log.getCheckResult());
        po.setOperatorId(log.getOperatorId());
        po.setHandleTime(log.getHandleTime());
        po.setHandleRemark(log.getHandleRemark());
        return po;
    }
}
