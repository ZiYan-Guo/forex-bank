package com.forex.margin.application.service;

import com.forex.common.base.exception.BusinessException;
import com.forex.margin.adapter.dto.CollateralValuationReq;
import com.forex.margin.adapter.dto.CollateralValuationResp;
import com.forex.margin.adapter.dto.InitialMarginCalcReq;
import com.forex.margin.adapter.dto.InitialMarginCalcResp;
import com.forex.margin.adapter.dto.VmMarginCalcReq;
import com.forex.margin.adapter.dto.VmMarginCalcResp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MarginCalculationServiceTest {

    private final MarginCalculationService service = new MarginCalculationService();

    @Test
    @DisplayName("VM calculation applies threshold, MTA, balance and delivery rounding")
    void calculateVariationMarginDelivery() {
        VmMarginCalcReq req = new VmMarginCalcReq();
        req.setExposureAmount(new BigDecimal("1500"));
        req.setCounterpartyThresholdAmount(new BigDecimal("200"));
        req.setMinimumTransferAmount(new BigDecimal("100"));
        req.setAccountBalance(new BigDecimal("300"));
        req.setInTransitAmount(new BigDecimal("50"));
        req.setDeliveryRoundingUnit(new BigDecimal("10"));

        VmMarginCalcResp resp = service.calculateVariationMargin(req);

        assertEquals(new BigDecimal("200.00"), resp.getAppliedThresholdAmount());
        assertEquals(new BigDecimal("350.00"), resp.getTotalAccountBalance());
        assertEquals(new BigDecimal("950.00"), resp.getNetSettlementAmount());
        assertEquals(new BigDecimal("950.00"), resp.getDeliveryAmount());
        assertEquals(new BigDecimal("0.00"), resp.getReturnAmount());
        assertEquals("DELIVERY", resp.getAction());
    }

    @Test
    @DisplayName("Standard IM calculation follows supervisory factors and NGR")
    void calculateStandardInitialMargin() {
        InitialMarginCalcReq req = new InitialMarginCalcReq();
        req.setTrades(List.of(
                trade("CREDIT", "3", "1000", "100"),
                trade("FX", "1", "2000", "-40"),
                trade("INTEREST_RATE", "6", "3000", "20")
        ));

        InitialMarginCalcResp resp = service.calculateStandardInitialMargin(req);

        assertEquals(new BigDecimal("290.00"), resp.getGrossInitialMargin());
        assertEquals(new BigDecimal("0.6667"), resp.getNgr());
        assertEquals(new BigDecimal("232.00"), resp.getStandardizedInitialMargin());
        assertEquals(3, resp.getTradeCount());
    }

    @Test
    @DisplayName("Collateral valuation converts FX then applies haircut")
    void valueCollateral() {
        CollateralValuationReq req = new CollateralValuationReq();
        req.setCollateralType("BOND");
        req.setCurrency("USD");
        req.setMarketValue(new BigDecimal("1000"));
        req.setFxRate(new BigDecimal("7.20"));
        req.setHaircutPct(new BigDecimal("5"));

        CollateralValuationResp resp = service.valueCollateral(req);

        assertEquals(new BigDecimal("7200.00"), resp.getConvertedValue());
        assertEquals(new BigDecimal("6840.00"), resp.getCollateralValue());
    }

    @Test
    @DisplayName("Collateral haircut must be within allowed range")
    void rejectInvalidHaircut() {
        CollateralValuationReq req = new CollateralValuationReq();
        req.setCollateralType("CASH");
        req.setMarketValue(new BigDecimal("1000"));
        req.setHaircutPct(new BigDecimal("100"));

        assertThrows(BusinessException.class, () -> service.valueCollateral(req));
    }

    private InitialMarginCalcReq.TradeItem trade(String assetClass, String tenorYears,
                                                String notionalAmount, String marketValue) {
        InitialMarginCalcReq.TradeItem trade = new InitialMarginCalcReq.TradeItem();
        trade.setAssetClass(assetClass);
        trade.setTenorYears(new BigDecimal(tenorYears));
        trade.setNotionalAmount(new BigDecimal(notionalAmount));
        trade.setMarketValue(new BigDecimal(marketValue));
        return trade;
    }
}
