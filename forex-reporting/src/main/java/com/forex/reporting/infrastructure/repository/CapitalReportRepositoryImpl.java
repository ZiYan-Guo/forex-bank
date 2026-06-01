package com.forex.reporting.infrastructure.repository;

import com.forex.reporting.domain.model.entity.CapitalAccountReport;
import com.forex.reporting.domain.repository.CapitalReportRepository;
import com.forex.reporting.infrastructure.mapper.CapitalReportMapper;
import com.forex.reporting.infrastructure.persistence.CapitalReportPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CapitalReportRepositoryImpl implements CapitalReportRepository {

    private final CapitalReportMapper capitalReportMapper;

    @Override
    public CapitalAccountReport save(CapitalAccountReport report) {
        CapitalReportPO po = toPO(report);
        if (report.getId() == null) {
            capitalReportMapper.insert(po);
        } else {
            capitalReportMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<CapitalAccountReport> findById(Long id) {
        CapitalReportPO po = capitalReportMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<CapitalAccountReport> findByReportNo(String reportNo) {
        CapitalReportPO po = capitalReportMapper.selectByReportNo(reportNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    private CapitalAccountReport toDomain(CapitalReportPO po) {
        return new CapitalAccountReport(
                po.getId(), po.getReportNo(), po.getCustomerId(),
                po.getAccountNo(), po.getReportType(),
                po.getTransactionType(), po.getTransactionAmount(),
                po.getTransactionCurrency(), po.getTransactionDate(),
                po.getCapitalCode(), po.getReportStatus(),
                po.getSubmitTime(), po.getRegulatoryRef());
    }

    private CapitalReportPO toPO(CapitalAccountReport report) {
        CapitalReportPO po = new CapitalReportPO();
        po.setId(report.getId());
        po.setReportNo(report.getReportNo());
        po.setCustomerId(report.getCustomerId());
        po.setAccountNo(report.getAccountNo());
        po.setReportType(report.getReportType());
        po.setTransactionType(report.getTransactionType());
        po.setTransactionAmount(report.getTransactionAmount());
        po.setTransactionCurrency(report.getTransactionCurrency());
        po.setTransactionDate(report.getTransactionDate());
        po.setCapitalCode(report.getCapitalCode());
        po.setReportStatus(report.getReportStatus());
        po.setSubmitTime(report.getSubmitTime());
        po.setRegulatoryRef(report.getRegulatoryRef());
        return po;
    }
}
