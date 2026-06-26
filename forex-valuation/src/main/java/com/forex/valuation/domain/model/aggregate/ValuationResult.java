package com.forex.valuation.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

@Getter
public class ValuationResult extends BaseAggregate {

    private Long id;
    private Long tradeId;
    private String tradeNo;
    private String tradeType;
    private LocalDate valuationDate;
    private String currencyPair;
    private BigDecimal notionalAmount;
    private BigDecimal fairValue;
    private BigDecimal pnl;
    private BigDecimal cumulativePnl;
    private String valuationMethod;
    private String modelParams;
    private String marketDataSnapshot;

    private ValuationResult() {
        super();
    }

    public static ValuationResult create(Long tradeId, String tradeNo, String tradeType,
                                          LocalDate valuationDate, String currencyPair,
                                          BigDecimal notionalAmount, String valuationMethod) {
        ValuationResult result = new ValuationResult();
        result.tradeId = tradeId;
        result.tradeNo = tradeNo;
        result.tradeType = tradeType;
        result.valuationDate = valuationDate;
        result.currencyPair = currencyPair;
        result.notionalAmount = notionalAmount;
        result.valuationMethod = valuationMethod;
        result.fairValue = BigDecimal.ZERO;
        result.pnl = BigDecimal.ZERO;
        result.cumulativePnl = BigDecimal.ZERO;
        result.validate();
        return result;
    }

    public static ValuationResult reconstitute(Long id, Long tradeId, String tradeNo,
                                                String tradeType, LocalDate valuationDate,
                                                String currencyPair, BigDecimal notionalAmount,
                                                BigDecimal fairValue, BigDecimal pnl,
                                                BigDecimal cumulativePnl, String valuationMethod,
                                                String modelParams, String marketDataSnapshot) {
        ValuationResult result = new ValuationResult();
        result.id = id;
        result.tradeId = tradeId;
        result.tradeNo = tradeNo;
        result.tradeType = tradeType;
        result.valuationDate = valuationDate;
        result.currencyPair = currencyPair;
        result.notionalAmount = notionalAmount;
        result.fairValue = fairValue;
        result.pnl = pnl;
        result.cumulativePnl = cumulativePnl;
        result.valuationMethod = valuationMethod;
        result.modelParams = modelParams;
        result.marketDataSnapshot = marketDataSnapshot;
        return result;
    }

    public void recalculate(BigDecimal newFairValue, BigDecimal newPnL) {
        this.fairValue = newFairValue;
        this.pnl = newPnL;
        this.cumulativePnl = this.cumulativePnl.add(newPnL);
        markUpdated();
    }

    @Override
    protected void validate() {
        if (tradeId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "交易ID不能为空");
        }
        if (currencyPair == null || currencyPair.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "货币对不能为空");
        }
        if (valuationMethod == null || valuationMethod.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "估值方法不能为空");
        }
    }
}
