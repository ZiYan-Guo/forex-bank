package com.forex.clearing.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class SettlementBatch extends BaseAggregate {

    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CLOSED = "CLOSED";
    public static final String STATUS_SETTLED = "SETTLED";

    private Long id;
    private String batchNo;
    private LocalDate batchDate;
    private String clearingChannel;
    private Integer totalCount;
    private BigDecimal totalAmount;
    private BigDecimal netAmount;
    private String batchStatus;

    private SettlementBatch() {
        super();
    }

    public static SettlementBatch create(String batchNo, LocalDate batchDate, String clearingChannel) {
        SettlementBatch batch = new SettlementBatch();
        batch.batchNo = batchNo;
        batch.batchDate = batchDate;
        batch.clearingChannel = clearingChannel;
        batch.totalCount = 0;
        batch.totalAmount = BigDecimal.ZERO;
        batch.batchStatus = STATUS_OPEN;
        batch.validate();
        return batch;
    }

    public static SettlementBatch reconstitute(Long id, String batchNo, LocalDate batchDate,
                                                String clearingChannel, Integer totalCount,
                                                BigDecimal totalAmount, BigDecimal netAmount,
                                                String batchStatus) {
        SettlementBatch batch = new SettlementBatch();
        batch.id = id;
        batch.batchNo = batchNo;
        batch.batchDate = batchDate;
        batch.clearingChannel = clearingChannel;
        batch.totalCount = totalCount;
        batch.totalAmount = totalAmount;
        batch.netAmount = netAmount;
        batch.batchStatus = batchStatus;
        return batch;
    }

    public void addInstruction(BigDecimal amount) {
        if (!STATUS_OPEN.equals(this.batchStatus)) {
            throw new BusinessException("只有开放状态的批次才能添加指令");
        }
        if (amount == null) {
            throw new BusinessException("金额不能为空");
        }
        this.totalCount++;
        this.totalAmount = this.totalAmount.add(amount);
        markUpdated();
    }

    public void close() {
        if (!STATUS_OPEN.equals(this.batchStatus)) {
            throw new BusinessException("只有开放状态的批次才能关闭");
        }
        this.batchStatus = STATUS_CLOSED;
        markUpdated();
    }

    public void settle(BigDecimal netAmount) {
        if (!STATUS_CLOSED.equals(this.batchStatus)) {
            throw new BusinessException("只有已关闭状态的批次才能结算");
        }
        this.netAmount = netAmount;
        this.batchStatus = STATUS_SETTLED;
        markUpdated();
    }

    @Override
    protected void validate() {
        if (batchNo == null || batchNo.isBlank()) {
            throw new BusinessException("批次号不能为空");
        }
    }
}
