package com.forex.clearing.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Clearing instruction aggregate root. Manages settlement instruction lifecycle:
 * DRAFT → GENERATED → SENT → ACKNOWLEDGED → SETTLED. Supports channels: SWIFT, CIPS, CFXPS, LOCAL.
 * 清算指令聚合根，管理清算指令生命周期。支持渠道：SWIFT、CIPS、CFXPS、LOCAL。
 */
@Getter
public class ClearingInstruction extends BaseAggregate {

    public static final String CHANNEL_SWIFT = "SWIFT";
    public static final String CHANNEL_CIPS = "CIPS";
    public static final String CHANNEL_CFXPS = "CFXPS";
    public static final String CHANNEL_LOCAL = "LOCAL";

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_GENERATED = "GENERATED";
    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_ACKNOWLEDGED = "ACKNOWLEDGED";
    public static final String STATUS_SETTLED = "SETTLED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private Long id;
    /** Unique instruction number. 指令唯一编号。 */
    private String instructionNo;
    private String bizType;
    private String bizNo;
    /** Clearing channel: SWIFT, CIPS, CFXPS, LOCAL. 清算渠道。 */
    private String clearingChannel;
    /** Nostro account at the correspondent bank. 往来账户（我行在代理行的账户）。 */
    private String nostroAccount;
    private String counterPartyAccount;
    private String payCurrency;
    /** Payment amount. 支付金额。 */
    private BigDecimal payAmount;
    private String receiveCurrency;
    private BigDecimal receiveAmount;
    private LocalDate valueDate;
    private LocalDate settlementDate;
    private String settlementType;
    /** Current instruction status. 指令当前状态。 */
    private String instructionStatus;
    private String swiftRef;
    private String cipsRef;
    private BigDecimal nostroBalanceBefore;
    private BigDecimal nostroBalanceAfter;
    private LocalDateTime sendTime;
    private LocalDateTime ackTime;
    private LocalDateTime settleTime;
    private Long operatorId;
    private String remark;

    private ClearingInstruction() {
        super();
    }

    /**
     * Create a new clearing instruction draft. 创建清算指令草稿。
     */
    public static ClearingInstruction create(String bizType, String bizNo, String clearingChannel,
                                              String nostroAccount, String counterPartyAccount,
                                              String payCurrency, BigDecimal payAmount,
                                              String receiveCurrency, BigDecimal receiveAmount,
                                              LocalDate valueDate, String settlementType,
                                              Long operatorId, String remark) {
        ClearingInstruction instruction = new ClearingInstruction();
        instruction.bizType = bizType;
        instruction.bizNo = bizNo;
        instruction.clearingChannel = clearingChannel;
        instruction.nostroAccount = nostroAccount;
        instruction.counterPartyAccount = counterPartyAccount;
        instruction.payCurrency = payCurrency;
        instruction.payAmount = payAmount;
        instruction.receiveCurrency = receiveCurrency;
        instruction.receiveAmount = receiveAmount;
        instruction.valueDate = valueDate;
        instruction.settlementType = settlementType;
        instruction.instructionStatus = STATUS_DRAFT;
        instruction.operatorId = operatorId;
        instruction.remark = remark;
        instruction.validate();
        return instruction;
    }

    /**
     * Rebuild aggregate from persistence. 从持久化重建聚合。
     */
    public static ClearingInstruction reconstitute(Long id, String instructionNo, String bizType,
                                                    String bizNo, String clearingChannel,
                                                    String nostroAccount, String counterPartyAccount,
                                                    String payCurrency, BigDecimal payAmount,
                                                    String receiveCurrency, BigDecimal receiveAmount,
                                                    LocalDate valueDate, LocalDate settlementDate,
                                                    String settlementType, String instructionStatus,
                                                    String swiftRef, String cipsRef,
                                                    BigDecimal nostroBalanceBefore,
                                                    BigDecimal nostroBalanceAfter,
                                                    LocalDateTime sendTime, LocalDateTime ackTime,
                                                    LocalDateTime settleTime, Long operatorId,
                                                    String remark) {
        ClearingInstruction instruction = new ClearingInstruction();
        instruction.id = id;
        instruction.instructionNo = instructionNo;
        instruction.bizType = bizType;
        instruction.bizNo = bizNo;
        instruction.clearingChannel = clearingChannel;
        instruction.nostroAccount = nostroAccount;
        instruction.counterPartyAccount = counterPartyAccount;
        instruction.payCurrency = payCurrency;
        instruction.payAmount = payAmount;
        instruction.receiveCurrency = receiveCurrency;
        instruction.receiveAmount = receiveAmount;
        instruction.valueDate = valueDate;
        instruction.settlementDate = settlementDate;
        instruction.settlementType = settlementType;
        instruction.instructionStatus = instructionStatus;
        instruction.swiftRef = swiftRef;
        instruction.cipsRef = cipsRef;
        instruction.nostroBalanceBefore = nostroBalanceBefore;
        instruction.nostroBalanceAfter = nostroBalanceAfter;
        instruction.sendTime = sendTime;
        instruction.ackTime = ackTime;
        instruction.settleTime = settleTime;
        instruction.operatorId = operatorId;
        instruction.remark = remark;
        return instruction;
    }

    /**
     * Generate the clearing instruction with an instruction number and nostro balance. 生成清算指令。
     */
    public void generate(String instructionNo, BigDecimal nostroBalanceBefore) {
        if (!STATUS_DRAFT.equals(this.instructionStatus)) {
            throw new BusinessException("只有草稿状态的清算指令才能生成");
        }
        this.instructionNo = instructionNo;
        this.nostroBalanceBefore = nostroBalanceBefore;
        this.instructionStatus = STATUS_GENERATED;
        markUpdated();
    }

    /**
     * Send the instruction via the clearing channel. 发送清算指令。
     */
    public void send() {
        if (!STATUS_GENERATED.equals(this.instructionStatus)) {
            throw new BusinessException("只有已生成状态的清算指令才能发送");
        }
        this.instructionStatus = STATUS_SENT;
        this.sendTime = LocalDateTime.now();
        markUpdated();
    }

    /**
     * Acknowledge receipt with a SWIFT reference. 确认回执。
     */
    public void acknowledge(String swiftRef) {
        if (!STATUS_SENT.equals(this.instructionStatus)) {
            throw new BusinessException("只有已发送状态的清算指令才能确认回执");
        }
        this.swiftRef = swiftRef;
        this.instructionStatus = STATUS_ACKNOWLEDGED;
        this.ackTime = LocalDateTime.now();
        markUpdated();
    }

    /**
     * Settle the instruction and record the post-settlement nostro balance. 结算清算指令。
     */
    public void settle(BigDecimal nostroBalanceAfter) {
        if (!STATUS_ACKNOWLEDGED.equals(this.instructionStatus)) {
            throw new BusinessException("只有已确认回执的清算指令才能结算");
        }
        this.nostroBalanceAfter = nostroBalanceAfter;
        this.instructionStatus = STATUS_SETTLED;
        this.settlementDate = LocalDate.now();
        this.settleTime = LocalDateTime.now();
        markUpdated();
    }

    /**
     * Cancel the clearing instruction with the given reason. 取消清算指令。
     */
    public void cancel(String reason) {
        if (STATUS_SETTLED.equals(this.instructionStatus)) {
            throw new BusinessException("已结算的清算指令不能取消");
        }
        if (STATUS_CANCELLED.equals(this.instructionStatus)) {
            throw new BusinessException("清算指令已取消");
        }
        this.instructionStatus = STATUS_CANCELLED;
        this.remark = reason;
        markUpdated();
    }

    @Override
    protected void validate() {
        if (bizType == null || bizType.isBlank()) {
            throw new BusinessException("业务类型不能为空");
        }
        if (clearingChannel == null || clearingChannel.isBlank()) {
            throw new BusinessException("清算渠道不能为空");
        }
        if (!CHANNEL_SWIFT.equals(clearingChannel) && !CHANNEL_CIPS.equals(clearingChannel)
                && !CHANNEL_CFXPS.equals(clearingChannel) && !CHANNEL_LOCAL.equals(clearingChannel)) {
            throw new BusinessException("清算渠道必须为SWIFT/CIPS/CFXPS/LOCAL");
        }
        if (nostroAccount == null || nostroAccount.isBlank()) {
            throw new BusinessException("我行账户不能为空");
        }
        if (payCurrency == null || payCurrency.isBlank()) {
            throw new BusinessException("支付币种不能为空");
        }
        if (payAmount == null || payAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("支付金额必须大于0");
        }
        if (valueDate == null) {
            throw new BusinessException("起息日不能为空");
        }
    }
}
