package com.forex.rate.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Repository;

import com.forex.common.base.dto.PageResp;
import com.forex.rate.application.query.RateQuery;
import com.forex.rate.domain.model.aggregate.ExchangeRate;
import com.forex.rate.domain.repository.ExchangeRateRepository;
import com.forex.rate.infrastructure.mapper.ExchangeRateMapper;
import com.forex.rate.infrastructure.persistence.ExchangeRatePO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateRepositoryImpl implements ExchangeRateRepository {

    private final ExchangeRateMapper exchangeRateMapper;

    @Override
    public ExchangeRate save(ExchangeRate rate) {
        ExchangeRatePO po = toPO(rate);
        if (rate.getId() == null) {
            exchangeRateMapper.insert(po);
        } else {
            exchangeRateMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<ExchangeRate> findById(Long id) {
        ExchangeRatePO po = exchangeRateMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<ExchangeRate> findLatestByCurrencyPair(String currencyPair) {
        ExchangeRatePO po = exchangeRateMapper.selectLatestByCurrencyPair(currencyPair);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<ExchangeRate> findLatestRates() {
        List<ExchangeRatePO> poList = exchangeRateMapper.selectLatestRates();
        return poList.stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public PageResp<ExchangeRate> pageQuery(RateQuery query) {
        Page<ExchangeRatePO> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<ExchangeRatePO> result = exchangeRateMapper.pageQuery(page, query);
        List<ExchangeRate> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();

        log.debug("Exchange rate page query completed, currencyPair={}, pageNum={}, pageSize={}, total={}",
                query.getCurrencyPair(), query.getPageNum(), query.getPageSize(), result.getTotal());
        return PageResp.of(result.getTotal(), records, query.getPageNum(), query.getPageSize());
    }

    private ExchangeRate toDomain(ExchangeRatePO po) {
        if (po == null) {
            return null;
        }
        return ExchangeRate.reconstitute(
                po.getId(),
                po.getCurrencyPair(),
                po.getBaseCurrency(),
                po.getQuoteCurrency(),
                po.getBidRate(),
                po.getAskRate(),
                po.getMidRate(),
                po.getSpread(),
                po.getRateSource(),
                po.getRateDate(),
                po.getRateTime(),
                po.getStatus()
        );
    }

    private ExchangeRatePO toPO(ExchangeRate rate) {
        ExchangeRatePO po = new ExchangeRatePO();
        po.setId(rate.getId());
        po.setCurrencyPair(rate.getCurrencyPair());
        po.setBaseCurrency(rate.getBaseCurrency());
        po.setQuoteCurrency(rate.getQuoteCurrency());
        po.setBidRate(rate.getBidRate());
        po.setAskRate(rate.getAskRate());
        po.setMidRate(rate.getMidRate());
        po.setSpread(rate.getSpread());
        po.setRateSource(rate.getRateSource());
        po.setRateDate(rate.getRateDate());
        po.setRateTime(rate.getRateTime());
        po.setStatus(rate.getStatus());
        return po;
    }
}
