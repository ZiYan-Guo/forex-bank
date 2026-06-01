package com.forex.reporting.infrastructure.repository;

import com.forex.common.base.dto.PageResp;
import com.forex.reporting.domain.model.aggregate.BopReport;
import com.forex.reporting.domain.model.query.ReportQuery;
import com.forex.reporting.domain.repository.BopReportRepository;
import com.forex.reporting.infrastructure.mapper.BopReportMapper;
import com.forex.reporting.infrastructure.persistence.BopReportPO;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BopReportRepositoryImpl implements BopReportRepository {

    private final BopReportMapper bopReportMapper;

    @Override
    public BopReport save(BopReport report) {
        BopReportPO po = toPO(report);
        if (report.getId() == null) {
            bopReportMapper.insert(po);
        } else {
            bopReportMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<BopReport> findById(Long id) {
        BopReportPO po = bopReportMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<BopReport> findByReportNo(String reportNo) {
        BopReportPO po = bopReportMapper.selectByReportNo(reportNo);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public PageResp<BopReport> pageQuery(ReportQuery query) {
        Page<BopReportPO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<BopReportPO> result = bopReportMapper.pageQuery(page, query);
        List<BopReport> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    private BopReport toDomain(BopReportPO po) {
        return BopReport.reconstitute(
                po.getId(), po.getReportNo(), po.getReportType(),
                po.getCustomerId(), po.getCustomerName(),
                po.getTransactionNo(), po.getTransactionType(),
                po.getTransactionAmount(), po.getTransactionCurrency(),
                po.getCnyAmount(), po.getExchangeRate(),
                po.getTransactionDate(), po.getSettlementDate(),
                po.getBopCode(), po.getBopName(), po.getPurposeCode(),
                po.getPurposeRemark(), po.getCounterpartyCountry(),
                po.getCounterpartyName(), po.getReportStatus(),
                po.getSubmitTime(), po.getRegulatoryRef(),
                po.getErrorMsg());
    }

    private BopReportPO toPO(BopReport report) {
        BopReportPO po = new BopReportPO();
        po.setId(report.getId());
        po.setReportNo(report.getReportNo());
        po.setReportType(report.getReportType());
        po.setCustomerId(report.getCustomerId());
        po.setCustomerName(report.getCustomerName());
        po.setTransactionNo(report.getTransactionNo());
        po.setTransactionType(report.getTransactionType());
        po.setTransactionAmount(report.getTransactionAmount());
        po.setTransactionCurrency(report.getTransactionCurrency());
        po.setCnyAmount(report.getCnyAmount());
        po.setExchangeRate(report.getExchangeRate());
        po.setTransactionDate(report.getTransactionDate());
        po.setSettlementDate(report.getSettlementDate());
        po.setBopCode(report.getBopCode());
        po.setBopName(report.getBopName());
        po.setPurposeCode(report.getPurposeCode());
        po.setPurposeRemark(report.getPurposeRemark());
        po.setCounterpartyCountry(report.getCounterpartyCountry());
        po.setCounterpartyName(report.getCounterpartyName());
        po.setReportStatus(report.getReportStatus());
        po.setSubmitTime(report.getSubmitTime());
        po.setRegulatoryRef(report.getRegulatoryRef());
        po.setErrorMsg(report.getErrorMsg());
        return po;
    }
}
