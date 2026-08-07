package com.forex.margin.application.service;

import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;
import com.forex.margin.adapter.dto.CollateralValuationReq;
import com.forex.margin.adapter.dto.CollateralValuationResp;
import com.forex.margin.adapter.dto.InitialMarginCalcReq;
import com.forex.margin.adapter.dto.InitialMarginCalcResp;
import com.forex.margin.adapter.dto.VmMarginCalcReq;
import com.forex.margin.adapter.dto.VmMarginCalcResp;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MarginCalculationService {

    private static final int MONEY_SCALE = 2;
    private static final int RATE_SCALE = 8;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public VmMarginCalcResp calculateVariationMargin(VmMarginCalcReq req) {
        BigDecimal exposure = money(req.getExposureAmount());
        BigDecimal totalBalance = money(req.getAccountBalance()).add(money(req.getInTransitAmount()));
        BigDecimal threshold = exposure.signum() >= 0
                ? money(req.getCounterpartyThresholdAmount())
                : money(req.getOurThresholdAmount());
        BigDecimal minimumTransfer = money(req.getMinimumTransferAmount());

        BigDecimal excessExposure = exposure.abs().subtract(threshold);
        if (excessExposure.compareTo(BigDecimal.ZERO) <= 0
                || isBelowMtaAndOppositeDirection(exposure, totalBalance, excessExposure, minimumTransfer)) {
            BigDecimal returnAmount = positive(totalBalance);
            BigDecimal deliveryAmount = totalBalance.signum() < 0 ? totalBalance.abs() : BigDecimal.ZERO;
            return new VmMarginCalcResp(threshold, totalBalance, BigDecimal.ZERO,
                    round(deliveryAmount, req.getDeliveryRoundingUnit()),
                    round(returnAmount, req.getReturnRoundingUnit()),
                    actionOf(deliveryAmount, returnAmount),
                    "敞口未超过门槛值或未达到最小转让金额，按规则不新增交收净额，仅处理现有保证金余额。");
        }

        BigDecimal netSettlement = exposure.signum() >= 0
                ? exposure.subtract(threshold).subtract(totalBalance)
                : exposure.abs().subtract(threshold).negate().subtract(totalBalance);

        BigDecimal deliveryAmount = BigDecimal.ZERO;
        BigDecimal returnAmount = BigDecimal.ZERO;
        if (netSettlement.signum() > 0) {
            if (totalBalance.signum() < 0) {
                BigDecimal balanceAbs = totalBalance.abs();
                if (netSettlement.compareTo(balanceAbs) > 0) {
                    deliveryAmount = netSettlement.subtract(balanceAbs);
                    returnAmount = balanceAbs;
                } else {
                    returnAmount = netSettlement;
                }
            } else {
                deliveryAmount = netSettlement;
            }
        } else if (netSettlement.signum() < 0) {
            BigDecimal netAbs = netSettlement.abs();
            if (totalBalance.signum() > 0) {
                if (netAbs.compareTo(totalBalance) > 0) {
                    returnAmount = netAbs.subtract(totalBalance);
                    deliveryAmount = totalBalance;
                } else {
                    deliveryAmount = netAbs;
                }
            } else {
                returnAmount = netAbs;
            }
        }

        deliveryAmount = round(deliveryAmount, req.getDeliveryRoundingUnit());
        returnAmount = round(returnAmount, req.getReturnRoundingUnit());
        return new VmMarginCalcResp(threshold, totalBalance, money(netSettlement),
                deliveryAmount, returnAmount, actionOf(deliveryAmount, returnAmount),
                "已超过门槛值且达到最小转让金额，按 VM 交收净额规则计算 Delivery/Return 金额。");
    }

    public InitialMarginCalcResp calculateStandardInitialMargin(InitialMarginCalcReq req) {
        BigDecimal grossInitialMargin = BigDecimal.ZERO;
        BigDecimal netMarketValue = BigDecimal.ZERO;
        BigDecimal grossPositiveMarketValue = BigDecimal.ZERO;

        for (InitialMarginCalcReq.TradeItem trade : req.getTrades()) {
            BigDecimal rate = resolveStandardRate(trade);
            BigDecimal notional = money(trade.getNotionalAmount());
            grossInitialMargin = grossInitialMargin.add(notional.multiply(rate));

            BigDecimal marketValue = money(trade.getMarketValue());
            netMarketValue = netMarketValue.add(marketValue);
            if (marketValue.signum() > 0) {
                grossPositiveMarketValue = grossPositiveMarketValue.add(marketValue);
            }
        }

        BigDecimal ngr = grossPositiveMarketValue.signum() == 0
                ? BigDecimal.ONE
                : netMarketValue.max(BigDecimal.ZERO).divide(grossPositiveMarketValue, RATE_SCALE, RoundingMode.HALF_UP);
        BigDecimal standardized = grossInitialMargin.multiply(new BigDecimal("0.4")
                .add(new BigDecimal("0.6").multiply(ngr)));
        return new InitialMarginCalcResp(money(grossInitialMargin), ngr.setScale(4, RoundingMode.HALF_UP),
                money(standardized), req.getTrades().size());
    }

    public CollateralValuationResp valueCollateral(CollateralValuationReq req) {
        BigDecimal marketValue = money(req.getMarketValue());
        BigDecimal fxRate = req.getFxRate() == null ? BigDecimal.ONE : req.getFxRate();
        BigDecimal haircutPct = req.getHaircutPct() == null ? BigDecimal.ZERO : req.getHaircutPct();
        if (haircutPct.compareTo(BigDecimal.ZERO) < 0 || haircutPct.compareTo(ONE_HUNDRED) >= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "haircut 必须大于等于0且小于100");
        }
        BigDecimal convertedValue = marketValue.multiply(fxRate);
        BigDecimal collateralValue = convertedValue.multiply(BigDecimal.ONE.subtract(haircutPct.divide(ONE_HUNDRED, RATE_SCALE, RoundingMode.HALF_UP)));
        return new CollateralValuationResp(req.getCollateralType(), req.getCurrency(), marketValue, fxRate,
                haircutPct, money(convertedValue), money(collateralValue));
    }

    private BigDecimal resolveStandardRate(InitialMarginCalcReq.TradeItem trade) {
        if (trade.getConservativeRatePct() != null) {
            return trade.getConservativeRatePct().divide(ONE_HUNDRED, RATE_SCALE, RoundingMode.HALF_UP);
        }
        String assetClass = trade.getAssetClass() == null ? "OTHER" : trade.getAssetClass().trim().toUpperCase();
        BigDecimal tenorYears = trade.getTenorYears() == null ? BigDecimal.ZERO : trade.getTenorYears();
        BigDecimal pct = switch (assetClass) {
            case "CREDIT" -> tenorYears.compareTo(new BigDecimal("2")) <= 0
                    ? new BigDecimal("2")
                    : tenorYears.compareTo(new BigDecimal("5")) <= 0 ? new BigDecimal("5") : new BigDecimal("10");
            case "COMMODITY", "PRECIOUS_METAL", "EQUITY" -> new BigDecimal("15");
            case "FX", "FOREX" -> new BigDecimal("6");
            case "INTEREST_RATE", "IR" -> tenorYears.compareTo(new BigDecimal("2")) <= 0
                    ? new BigDecimal("1")
                    : tenorYears.compareTo(new BigDecimal("5")) <= 0 ? new BigDecimal("2") : new BigDecimal("4");
            default -> new BigDecimal("15");
        };
        return pct.divide(ONE_HUNDRED, RATE_SCALE, RoundingMode.HALF_UP);
    }

    private boolean isBelowMtaAndOppositeDirection(BigDecimal exposure, BigDecimal totalBalance,
                                                   BigDecimal excessExposure, BigDecimal minimumTransfer) {
        return excessExposure.compareTo(minimumTransfer) < 0
                && exposure.signum() != 0
                && totalBalance.signum() != 0
                && exposure.signum() != totalBalance.signum();
    }

    private BigDecimal round(BigDecimal amount, BigDecimal unit) {
        if (unit == null || unit.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(BigDecimal.ZERO) == 0) {
            return money(amount);
        }
        return amount.divide(unit, 0, RoundingMode.CEILING).multiply(unit).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private String actionOf(BigDecimal deliveryAmount, BigDecimal returnAmount) {
        if (deliveryAmount.signum() > 0 && returnAmount.signum() > 0) {
            return "MIXED";
        }
        if (deliveryAmount.signum() > 0) {
            return "DELIVERY";
        }
        if (returnAmount.signum() > 0) {
            return "RETURN";
        }
        return "NONE";
    }

    private BigDecimal positive(BigDecimal value) {
        return value.signum() > 0 ? value : BigDecimal.ZERO;
    }

    private BigDecimal money(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
