package com.forex.clearing.application.service;

import com.forex.clearing.domain.model.valueobject.PaymentChannel;
import com.forex.clearing.domain.model.valueobject.SettlementRoute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteOptimizationService {

    public SettlementRoute optimizeRoute(String payCurrency, String receiveCurrency,
                                          BigDecimal amount, String counterpartyCountry) {
        List<PaymentChannel> channels = loadAvailableChannels();
        SettlementRoute bestRoute = null;
        double bestScore = 0;

        for (PaymentChannel ch : channels) {
            double score = scoreChannel(ch, amount, counterpartyCountry);
            if (score > bestScore) {
                bestScore = score;
                BigDecimal cost = ch.getFeePerTransaction();
                int hours = ch.isSameDaySettlement() ? 4 : 24;
                bestRoute = new SettlementRoute(UUID.randomUUID().toString(), ch.getChannelCode(),
                    cost, hours, BigDecimal.valueOf(score), score > 70 ? "RECOMMENDED" : "ACCEPTABLE",
                    List.of());
            }
        }
        return bestRoute;
    }

    private double scoreChannel(PaymentChannel ch, BigDecimal amount, String country) {
        double costScore = amount.compareTo(new BigDecimal("100000")) > 0 ? 30 : 20;
        double speedScore = ch.isSameDaySettlement() ? 35 : 15;
        double cutOffScore = isBeforeCutOff(ch.getCutOffTime()) ? 25 : 10;
        double balanceScore = 10;
        return costScore + speedScore + cutOffScore + balanceScore;
    }

    private boolean isBeforeCutOff(String cutOffTime) {
        return true;
    }

    private List<PaymentChannel> loadAvailableChannels() {
        return List.of(
            new PaymentChannel("SWIFT", "SWIFT", BigDecimal.valueOf(35), BigDecimal.ZERO, "17:00", false, null),
            new PaymentChannel("CIPS", "CIPS", BigDecimal.valueOf(25), BigDecimal.ZERO, "20:00", true, BigDecimal.valueOf(100000000)),
            new PaymentChannel("CFXPS", "境内外币支付", BigDecimal.valueOf(15), BigDecimal.ZERO, "16:30", true, BigDecimal.valueOf(50000000)),
            new PaymentChannel("GFIX", "GFIX通用传输", BigDecimal.valueOf(20), BigDecimal.ZERO, "18:00", true, null)
        );
    }
}
