package com.forex.reporting.infrastructure.repository;

import com.forex.reporting.domain.model.entity.ForexSettlementReport;
import com.forex.reporting.domain.repository.SettlementReportRepository;
import com.forex.reporting.infrastructure.mapper.SettlementReportMapper;
import com.forex.reporting.infrastructure.persistence.SettlementReportPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SettlementReportRepositoryImpl implements SettlementReportRepository {

    private final SettlementReportMapper settlementReportMapper;

    @Override
    public ForexSettlementReport save(ForexSettlementReport report) {
        SettlementReportPO po = toPO(report);
        if (report.getId() == null) {
            settlementReportMapper.insert(po);
        } else {
            settlementReportMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<ForexSettlementReport> findById(Long id) {
        SettlementReportPO po = settlementReportMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<ForexSettlementReport> findByReportNo(String reportNo) {
        SettlementReportPO po = settlementReportMapper.selectByReportNo(reportNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<ForexSettlementReport> findByExchangeOrderNo(String exchangeOrderNo) {
        SettlementReportPO po = settlementReportMapper.selectByExchangeOrderNo(exchangeOrderNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    private ForexSettlementReport toDomain(SettlementReportPO po) {
        return new ForexSettlementReport(
                po.getId(), po.getReportNo(), po.getCustomerId(),
                po.getExchangeOrderNo(), po.getExchangeType(),
                po.getDealType(), po.getTransactionAmount(),
                po.getTransactionCurrency(), po.getCnyAmount(),
                po.getExchangeRate(), po.getTransactionDate(),
                po.getSettleDate(), po.getSettlementCode(),
                po.getReportStatus(), po.getSubmitTime(),
                po.getRegulatoryRef());
    }

    private SettlementReportPO toPO(ForexSettlementReport report) {
        SettlementReportPO po = new SettlementReportPO();
        po.setId(report.getId());
        po.setReportNo(report.getReportNo());
        po.setCustomerId(report.getCustomerId());
        po.setExchangeOrderNo(report.getExchangeOrderNo());
        po.setExchangeType(report.getExchangeType());
        po.setDealType(report.getDealType());
        po.setTransactionAmount(report.getTransactionAmount());
        po.setTransactionCurrency(report.getTransactionCurrency());
        po.setCnyAmount(report.getCnyAmount());
        po.setExchangeRate(report.getExchangeRate());
        po.setTransactionDate(report.getTransactionDate());
        po.setSettleDate(report.getSettleDate());
        po.setSettlementCode(report.getSettlementCode());
        po.setReportStatus(report.getReportStatus());
        po.setSubmitTime(report.getSubmitTime());
        po.setRegulatoryRef(report.getRegulatoryRef());
        return po;
    }
}
