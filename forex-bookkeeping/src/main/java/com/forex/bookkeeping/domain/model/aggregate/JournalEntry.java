package com.forex.bookkeeping.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;
import com.forex.common.base.exception.BusinessException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Journal entry aggregate root. A double-entry bookkeeping record. Each entry has a direction
 * (DEBIT or CREDIT) and moves through states: DRAFT → POSTED → REVERSED.
 * 会计分录聚合根，表示复式记账记录。每笔分录有借贷方向，状态流转：草稿→已过账→已冲正。
 */
@Getter
public class JournalEntry extends BaseAggregate {

    public static final String DIRECTION_DEBIT = "DEBIT";
    public static final String DIRECTION_CREDIT = "CREDIT";

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_POSTED = "POSTED";
    public static final String STATUS_REVERSED = "REVERSED";

    private Long id;
    /** Unique voucher number. 凭证唯一编号。 */
    private String voucherNo;
    private LocalDate voucherDate;
    private String fiscalPeriod;
    private String bizType;
    private String bizNo;
    private String currency;
    /** Entry amount. 分录金额。 */
    private BigDecimal amount;
    /** Entry direction: DEBIT or CREDIT. 分录方向：借方/贷方。 */
    private String entryDirection;
    /** Account code for the debit/credit side. 科目代码。 */
    private String accountCode;
    private String accountName;
    private String oppositeAccountCode;
    private String summary;
    /** Current entry status. 分录当前状态。 */
    private String entryStatus;
    private String reversedVoucherNo;
    private LocalDateTime postedTime;
    private Long operatorId;

    private JournalEntry() {
        super();
    }

    /**
     * Create a new journal entry. 创建会计分录。
     */
    public static JournalEntry create(String voucherNo, LocalDate voucherDate, String fiscalPeriod,
                                       String bizType, String bizNo, String currency,
                                       BigDecimal amount, String entryDirection, String accountCode,
                                       String accountName, String oppositeAccountCode,
                                       String summary, Long operatorId) {
        JournalEntry entry = new JournalEntry();
        entry.voucherNo = voucherNo;
        entry.voucherDate = voucherDate;
        entry.fiscalPeriod = fiscalPeriod;
        entry.bizType = bizType;
        entry.bizNo = bizNo;
        entry.currency = currency;
        entry.amount = amount;
        entry.entryDirection = entryDirection;
        entry.accountCode = accountCode;
        entry.accountName = accountName;
        entry.oppositeAccountCode = oppositeAccountCode;
        entry.summary = summary;
        entry.entryStatus = STATUS_DRAFT;
        entry.operatorId = operatorId;
        entry.validate();
        return entry;
    }

    /**
     * Rebuild aggregate from persistence. 从持久化重建聚合。
     */
    public static JournalEntry reconstitute(Long id, String voucherNo, LocalDate voucherDate,
                                             String fiscalPeriod, String bizType, String bizNo,
                                             String currency, BigDecimal amount,
                                             String entryDirection, String accountCode,
                                             String accountName, String oppositeAccountCode,
                                             String summary, String entryStatus,
                                             String reversedVoucherNo, LocalDateTime postedTime,
                                             Long operatorId) {
        JournalEntry entry = new JournalEntry();
        entry.id = id;
        entry.voucherNo = voucherNo;
        entry.voucherDate = voucherDate;
        entry.fiscalPeriod = fiscalPeriod;
        entry.bizType = bizType;
        entry.bizNo = bizNo;
        entry.currency = currency;
        entry.amount = amount;
        entry.entryDirection = entryDirection;
        entry.accountCode = accountCode;
        entry.accountName = accountName;
        entry.oppositeAccountCode = oppositeAccountCode;
        entry.summary = summary;
        entry.entryStatus = entryStatus;
        entry.reversedVoucherNo = reversedVoucherNo;
        entry.postedTime = postedTime;
        entry.operatorId = operatorId;
        return entry;
    }

    /**
     * Post the journal entry to the ledger. 过账。
     */
    public void post() {
        if (!STATUS_DRAFT.equals(this.entryStatus)) {
            throw new BusinessException("只有草稿状态的记账分录才能过账");
        }
        this.entryStatus = STATUS_POSTED;
        this.postedTime = LocalDateTime.now();
        markUpdated();
    }

    /**
     * Reverse a posted journal entry using a reversal voucher. 冲正。
     */
    public void reverse(String reverseVoucherNo) {
        if (!STATUS_POSTED.equals(this.entryStatus)) {
            throw new BusinessException("只有已过账状态的记账分录才能冲正");
        }
        this.entryStatus = STATUS_REVERSED;
        this.reversedVoucherNo = reverseVoucherNo;
        markUpdated();
    }

    @Override
    protected void validate() {
        if (voucherNo == null || voucherNo.isBlank()) {
            throw new BusinessException("凭证号不能为空");
        }
        if (voucherDate == null) {
            throw new BusinessException("凭证日期不能为空");
        }
        if (bizType == null || bizType.isBlank()) {
            throw new BusinessException("业务类型不能为空");
        }
        if (currency == null || currency.isBlank()) {
            throw new BusinessException("币种不能为空");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("金额必须大于0");
        }
        if (entryDirection == null || entryDirection.isBlank()) {
            throw new BusinessException("分录方向不能为空");
        }
        if (!DIRECTION_DEBIT.equals(entryDirection) && !DIRECTION_CREDIT.equals(entryDirection)) {
            throw new BusinessException("分录方向必须为DEBIT或CREDIT");
        }
        if (accountCode == null || accountCode.isBlank()) {
            throw new BusinessException("科目代码不能为空");
        }
    }
}
