package com.forex.risk.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.risk.domain.model.entity.RiskReport;
import com.forex.risk.domain.model.query.RiskQuery;
import com.forex.risk.domain.repository.RiskReportRepository;
import com.forex.risk.infrastructure.mapper.RiskReportMapper;
import com.forex.risk.infrastructure.persistence.RiskReportPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RiskReportRepositoryImpl implements RiskReportRepository {

    private final RiskReportMapper riskReportMapper;

    @Override
    public RiskReport save(RiskReport report) {
        RiskReportPO po = toPO(report);
        if (report.getId() == null) {
            riskReportMapper.insert(po);
        } else {
            riskReportMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<RiskReport> findById(Long id) {
        RiskReportPO po = riskReportMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<RiskReport> findByReportNo(String reportNo) {
        RiskReportPO po = riskReportMapper.selectByReportNo(reportNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public PageResp<RiskReport> pageQuery(RiskQuery query) {
        Page<RiskReportPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<RiskReportPO> result = riskReportMapper.selectPage(page, null);
        List<RiskReport> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    private RiskReport toDomain(RiskReportPO po) {
        return new RiskReport(
                po.getId(), po.getReportNo(), po.getReportType(),
                po.getReportPeriod(), po.getCustomerId(),
                po.getTotalTransactions(), po.getTotalAmount(),
                po.getReportContent(), po.getReportStatus(),
                po.getSubmitTime(), po.getSubmitterId(),
                po.getRegulatoryRef());
    }

    private RiskReportPO toPO(RiskReport report) {
        RiskReportPO po = new RiskReportPO();
        po.setId(report.getId());
        po.setReportNo(report.getReportNo());
        po.setReportType(report.getReportType());
        po.setReportPeriod(report.getReportPeriod());
        po.setCustomerId(report.getCustomerId());
        po.setTotalTransactions(report.getTotalTransactions());
        po.setTotalAmount(report.getTotalAmount());
        po.setReportContent(report.getReportContent());
        po.setReportStatus(report.getReportStatus());
        po.setSubmitTime(report.getSubmitTime());
        po.setSubmitterId(report.getSubmitterId());
        po.setRegulatoryRef(report.getRegulatoryRef());
        return po;
    }
}
