package com.forex.exchange.infrastructure.repository;

import com.forex.exchange.domain.model.entity.ExchangeQuote;
import com.forex.exchange.domain.repository.ExchangeQuoteRepository;
import com.forex.exchange.infrastructure.mapper.ExchangeQuoteMapper;
import com.forex.exchange.infrastructure.persistence.ExchangeQuotePO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExchangeQuoteRepositoryImpl implements ExchangeQuoteRepository {

    private final ExchangeQuoteMapper exchangeQuoteMapper;

    @Override
    public ExchangeQuote save(ExchangeQuote quote) {
        ExchangeQuotePO po = toPO(quote);
        if (quote.getId() == null) {
            exchangeQuoteMapper.insert(po);
        } else {
            exchangeQuoteMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<ExchangeQuote> findLatestQuote(Long customerId, String baseCcy, String quoteCcy) {
        ExchangeQuotePO po = exchangeQuoteMapper.selectLatestQuote(customerId, baseCcy, quoteCcy);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    private ExchangeQuote toDomain(ExchangeQuotePO po) {
        return new ExchangeQuote(
                po.getId(),
                po.getCustomerId(),
                po.getBaseCurrency(),
                po.getQuoteCurrency(),
                po.getBidRate(),
                po.getAskRate(),
                po.getMidRate(),
                po.getQuoteTime(),
                po.getExpireTime(),
                po.getQuoteStatus()
        );
    }

    private ExchangeQuotePO toPO(ExchangeQuote quote) {
        ExchangeQuotePO po = new ExchangeQuotePO();
        po.setId(quote.getId());
        po.setCustomerId(quote.getCustomerId());
        po.setBaseCurrency(quote.getBaseCurrency());
        po.setQuoteCurrency(quote.getQuoteCurrency());
        po.setBidRate(quote.getBidRate());
        po.setAskRate(quote.getAskRate());
        po.setMidRate(quote.getMidRate());
        po.setQuoteTime(quote.getQuoteTime());
        po.setExpireTime(quote.getExpireTime());
        po.setQuoteStatus(quote.getQuoteStatus());
        return po;
    }
}
