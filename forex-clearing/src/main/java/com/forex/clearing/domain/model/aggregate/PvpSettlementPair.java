package com.forex.clearing.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class PvpSettlementPair extends BaseAggregate {

    public static final String STATUS_PAIRED = "PAIRED";
    public static final String STATUS_ATTEMPTED = "ATTEMPTED";
    public static final String STATUS_SETTLED = "SETTLED";
    public static final String STATUS_FAILED = "FAILED";

    private Long id;
    private String pairId;
    private Long payInstructionId;
    private String payInstructionNo;
    private Long receiveInstructionId;
    private String receiveInstructionNo;
    private String payCurrency;
    private BigDecimal payAmount;
    private String receiveCurrency;
    private BigDecimal receiveAmount;
    private LocalDate settlementDate;
    private String status;
    private String failureReason;
    private LocalDateTime settledAt;

    private PvpSettlementPair() {
        super();
    }

    public static PvpSettlementPair create(String pairId, Long payInstructionId, String payInstructionNo,
                                            Long receiveInstructionId, String receiveInstructionNo,
                                            String payCurrency, BigDecimal payAmount,
                                            String receiveCurrency, BigDecimal receiveAmount,
                                            LocalDate settlementDate) {
        PvpSettlementPair pair = new PvpSettlementPair();
        pair.pairId = pairId;
        pair.payInstructionId = payInstructionId;
        pair.payInstructionNo = payInstructionNo;
        pair.receiveInstructionId = receiveInstructionId;
        pair.receiveInstructionNo = receiveInstructionNo;
        pair.payCurrency = payCurrency;
        pair.payAmount = payAmount;
        pair.receiveCurrency = receiveCurrency;
        pair.receiveAmount = receiveAmount;
        pair.settlementDate = settlementDate;
        pair.status = STATUS_PAIRED;
        pair.validate();
        return pair;
    }

    public static PvpSettlementPair reconstitute(Long id, String pairId, Long payInstructionId,
                                                  String payInstructionNo, Long receiveInstructionId,
                                                  String receiveInstructionNo, String payCurrency,
                                                  BigDecimal payAmount, String receiveCurrency,
                                                  BigDecimal receiveAmount, LocalDate settlementDate,
                                                  String status, String failureReason,
                                                  LocalDateTime settledAt) {
        PvpSettlementPair pair = new PvpSettlementPair();
        pair.id = id;
        pair.pairId = pairId;
        pair.payInstructionId = payInstructionId;
        pair.payInstructionNo = payInstructionNo;
        pair.receiveInstructionId = receiveInstructionId;
        pair.receiveInstructionNo = receiveInstructionNo;
        pair.payCurrency = payCurrency;
        pair.payAmount = payAmount;
        pair.receiveCurrency = receiveCurrency;
        pair.receiveAmount = receiveAmount;
        pair.settlementDate = settlementDate;
        pair.status = status;
        pair.failureReason = failureReason;
        pair.settledAt = settledAt;
        return pair;
    }

    public void attempt() {
        if (!STATUS_PAIRED.equals(this.status)) {
            throw new BusinessException("只有已配对状态才能尝试结算");
        }
        this.status = STATUS_ATTEMPTED;
        markUpdated();
    }

    public void settle() {
        if (!STATUS_ATTEMPTED.equals(this.status)) {
            throw new BusinessException("只有已尝试状态才能确认结算");
        }
        this.status = STATUS_SETTLED;
        this.settledAt = LocalDateTime.now();
        markUpdated();
    }

    public void fail(String reason) {
        if (STATUS_SETTLED.equals(this.status)) {
            throw new BusinessException("已结算的PVP对不能标记失败");
        }
        this.status = STATUS_FAILED;
        this.failureReason = reason;
        markUpdated();
    }

    @Override
    protected void validate() {
        if (payInstructionId == null) {
            throw new BusinessException("支付指令ID不能为空");
        }
        if (receiveInstructionId == null) {
            throw new BusinessException("收款指令ID不能为空");
        }
        if (payCurrency == null || receiveCurrency == null) {
            throw new BusinessException("币种不能为空");
        }
        if (payCurrency.equals(receiveCurrency)) {
            throw new BusinessException("PVP结算对必须使用不同币种");
        }
    }
}
