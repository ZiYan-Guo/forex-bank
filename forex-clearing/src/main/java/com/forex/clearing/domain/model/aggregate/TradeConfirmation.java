package com.forex.clearing.domain.model.aggregate;

import com.forex.clearing.domain.model.valueobject.ConfirmationFlag;
import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class TradeConfirmation extends BaseAggregate {

    public static final String STATUS_UNMATCHED = "UNMATCHED";
    public static final String STATUS_MATCHING = "MATCHING";
    public static final String STATUS_MATCHED = "MATCHED";
    public static final String STATUS_DISCREPANCY = "DISCREPANCY";
    public static final String STATUS_MANUALLY_RESOLVED = "MANUALLY_RESOLVED";

    private Long id;
    private String confirmId;
    private String tradeNo;
    private String tradeType;
    private ConfirmationFlag confirmFlag;
    private String currencyPair;
    private String direction;
    private BigDecimal amount;
    private BigDecimal rate;
    private LocalDate valueDate;
    private String counterparty;
    private String matchStatus;
    private String externalRef;
    private String discrepancyDetail;
    private Integer retryCount;
    private LocalDateTime nextRetryAt;
    private String resolutionAction;
    private String resolutionComment;

    private TradeConfirmation() {
        super();
    }

    public static TradeConfirmation create(String confirmId, String tradeNo, String tradeType,
                                            ConfirmationFlag confirmFlag, String currencyPair,
                                            String direction, BigDecimal amount, BigDecimal rate,
                                            LocalDate valueDate, String counterparty) {
        TradeConfirmation cfm = new TradeConfirmation();
        cfm.confirmId = confirmId;
        cfm.tradeNo = tradeNo;
        cfm.tradeType = tradeType;
        cfm.confirmFlag = confirmFlag;
        cfm.currencyPair = currencyPair;
        cfm.direction = direction;
        cfm.amount = amount;
        cfm.rate = rate;
        cfm.valueDate = valueDate;
        cfm.counterparty = counterparty;
        cfm.matchStatus = STATUS_UNMATCHED;
        cfm.retryCount = 0;
        cfm.validate();
        return cfm;
    }

    public static TradeConfirmation reconstitute(Long id, String confirmId, String tradeNo,
                                                  String tradeType, String confirmFlag,
                                                  String currencyPair, String direction,
                                                  BigDecimal amount, BigDecimal rate,
                                                  LocalDate valueDate, String counterparty,
                                                  String matchStatus, String externalRef,
                                                  String discrepancyDetail, Integer retryCount,
                                                  LocalDateTime nextRetryAt,
                                                  String resolutionAction, String resolutionComment) {
        TradeConfirmation cfm = new TradeConfirmation();
        cfm.id = id;
        cfm.confirmId = confirmId;
        cfm.tradeNo = tradeNo;
        cfm.tradeType = tradeType;
        cfm.confirmFlag = confirmFlag != null ? ConfirmationFlag.valueOf(confirmFlag) : null;
        cfm.currencyPair = currencyPair;
        cfm.direction = direction;
        cfm.amount = amount;
        cfm.rate = rate;
        cfm.valueDate = valueDate;
        cfm.counterparty = counterparty;
        cfm.matchStatus = matchStatus;
        cfm.externalRef = externalRef;
        cfm.discrepancyDetail = discrepancyDetail;
        cfm.retryCount = retryCount != null ? retryCount : 0;
        cfm.nextRetryAt = nextRetryAt;
        cfm.resolutionAction = resolutionAction;
        cfm.resolutionComment = resolutionComment;
        return cfm;
    }

    public void startMatching() {
        if (!STATUS_UNMATCHED.equals(this.matchStatus)) {
            throw new BusinessException("只有未匹配状态才能发起匹配");
        }
        this.matchStatus = STATUS_MATCHING;
        markUpdated();
    }

    public void markMatched() {
        if (!STATUS_MATCHING.equals(this.matchStatus)
                && !STATUS_UNMATCHED.equals(this.matchStatus)) {
            throw new BusinessException("当前状态不能标记为已匹配");
        }
        this.matchStatus = STATUS_MATCHED;
        markUpdated();
    }

    public void markDiscrepancy(String detail, String externalRef) {
        if (STATUS_MATCHED.equals(this.matchStatus)) {
            throw new BusinessException("已匹配的记录不能标记为差异");
        }
        this.matchStatus = STATUS_DISCREPANCY;
        this.discrepancyDetail = detail;
        this.externalRef = externalRef;
        markUpdated();
    }

    public void resolve(String action, String comment) {
        if (!STATUS_DISCREPANCY.equals(this.matchStatus)) {
            throw new BusinessException("只有差异状态才能进行人工干预");
        }
        this.matchStatus = STATUS_MANUALLY_RESOLVED;
        this.resolutionAction = action;
        this.resolutionComment = comment;
        markUpdated();
    }

    public void incrementRetry() {
        this.retryCount = this.retryCount + 1;
        long delayMinutes = switch (this.retryCount) {
            case 1 -> 5;
            case 2 -> 15;
            case 3 -> 60;
            default -> 60;
        };
        this.nextRetryAt = LocalDateTime.now().plusMinutes(delayMinutes);
        markUpdated();
    }

    @Override
    protected void validate() {
        if (confirmId == null || confirmId.isBlank()) {
            throw new BusinessException("确认ID不能为空");
        }
        if (tradeNo == null || tradeNo.isBlank()) {
            throw new BusinessException("交易编号不能为空");
        }
        if (confirmFlag == null) {
            throw new BusinessException("确认类型不能为空");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("金额必须大于0");
        }
    }
}
