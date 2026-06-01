package com.forex.margin.domain.service;

import com.forex.margin.domain.event.MarginCalledEvent;
import com.forex.margin.domain.model.aggregate.MarginAccount;
import com.forex.margin.domain.model.entity.MarginCall;
import com.forex.margin.domain.model.valueobject.WaterLevel;
import com.forex.margin.domain.repository.MarginAccountRepository;
import com.forex.margin.domain.repository.MarginCallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarginDomainService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String MARGIN_NO_PREFIX = "MG";

    private final MarginAccountRepository marginAccountRepository;
    private final MarginCallRepository marginCallRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MarginAccount createMargin(Long customerId, Long tradeId, String marginType,
                                       String marginCurrency, BigDecimal requiredAmount,
                                       BigDecimal marginRate, String collateralType) {
        MarginAccount account = MarginAccount.create(customerId, tradeId, marginType,
                marginCurrency, requiredAmount, marginRate, collateralType);
        String marginNo = generateMarginNo();
        account.assignMarginNo(marginNo);
        log.info("Created margin account, marginNo: {}, customerId: {}", marginNo, customerId);
        return account;
    }

    public MarginAccount createDynamicMargin(Long customerId, Long tradeId,
                                              BigDecimal notional, int tenorMonths,
                                              BigDecimal volatilityPct, String marginCurrency,
                                              String collateralType) {
        BigDecimal requiredAmount = MarginAccount.calculateRequiredAmount(notional, tenorMonths, volatilityPct);
        BigDecimal marginRate = MarginAccount.calculateMarginRate(tenorMonths);
        MarginAccount account = MarginAccount.create(customerId, tradeId, "INITIAL",
                marginCurrency, requiredAmount, marginRate, collateralType);
        account.assignMarginNo(generateMarginNo());
        return account;
    }

    public MarginAccount depositWithWaterLevelCheck(MarginAccount account, BigDecimal amount) {
        account.deposit(amount);
        account.assignMarginNo(account.getMarginNo() != null ? account.getMarginNo() : generateMarginNo());

        WaterLevel level = account.checkWaterLevel();
        account.setWaterLevel(level.getLevel());

        if (level.needsAction()) {
            MarginCall call = new MarginCall(null, account.getId(), account.getMarginNo(),
                    "MARGIN_CALL", account.getShortfallAmount(), LocalDateTime.now(), null, "PENDING");
            marginCallRepository.save(call);
        }
        return account;
    }

    public BigDecimal calculateInitialMargin(BigDecimal notional, BigDecimal rate) {
        if (notional == null || rate == null) {
            return BigDecimal.ZERO;
        }
        return notional.multiply(rate);
    }

    public MarginAccount callMargin(MarginAccount account, BigDecimal additionalAmount) {
        account.call(additionalAmount);
        marginAccountRepository.save(account);
        eventPublisher.publishEvent(new MarginCalledEvent(account.getId(), additionalAmount));
        log.info("Called margin, marginNo: {}, additionalAmount: {}", account.getMarginNo(), additionalAmount);
        return account;
    }

    public MarginAccount releaseMargin(MarginAccount account, BigDecimal amount, String reason) {
        account.release(amount, reason);
        marginAccountRepository.save(account);
        log.info("Released margin, marginNo: {}, amount: {}, reason: {}", account.getMarginNo(), amount, reason);
        return account;
    }

    private String generateMarginNo() {
        String datePart = LocalDate.now().format(DATE_FORMAT);
        String suffix = String.format("%08d", (long) (Math.random() * 100_000_000));
        return MARGIN_NO_PREFIX + datePart + suffix;
    }
}
