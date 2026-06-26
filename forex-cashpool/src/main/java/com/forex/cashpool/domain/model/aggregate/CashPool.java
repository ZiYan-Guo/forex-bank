package com.forex.cashpool.domain.model.aggregate;

import com.forex.cashpool.domain.model.entity.PoolMember;
import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.forex.common.base.exception.BusinessException;
import com.forex.common.base.result.ResultCode;

/**
 * 资金池聚合根 - 管理资金池生命周期、成员关系与额度状态
 * CashPool Aggregate Root - Manages cash pool lifecycle, member relationships and quota status
 */
@Slf4j
@Getter
public class CashPool extends BaseAggregate {

    private Long id;
    private String poolId;
    private Long mainAccountId;
    private String poolName;
    private String poolCurrency;
    private BigDecimal totalLimit;
    private BigDecimal usedLimit;
    private BigDecimal availableLimit;
    private String poolStatus;
    private LocalDate effectiveDate;
    private List<PoolMember> members = new ArrayList<>();

    private CashPool() {
        super();
    }

    /**
     * 创建资金池 - 初始化聚合根状态
     * Create CashPool - Initialize aggregate root state
     */
    public static CashPool create(String poolId, Long mainAccountId, String poolName,
                                   String poolCurrency, BigDecimal totalLimit, LocalDate effectiveDate) {
        CashPool pool = new CashPool();
        pool.poolId = poolId;
        pool.mainAccountId = mainAccountId;
        pool.poolName = poolName;
        pool.poolCurrency = poolCurrency;
        pool.totalLimit = totalLimit;
        pool.usedLimit = BigDecimal.ZERO;
        pool.availableLimit = totalLimit;
        pool.poolStatus = "ACTIVE";
        pool.effectiveDate = effectiveDate;
        pool.members = new ArrayList<>();
        pool.validate();
        log.info("资金池创建成功, poolId: {}, poolName: {}, poolCurrency: {}, totalLimit: {}",
                poolId, poolName, poolCurrency, totalLimit);
        return pool;
    }

    /**
     * 从持久化数据重建资金池聚合根
     * Reconstitute CashPool aggregate root from persisted data
     */
    public static CashPool reconstitute(Long id, String poolId, Long mainAccountId,
                                         String poolName, String poolCurrency,
                                         BigDecimal totalLimit, BigDecimal usedLimit,
                                         BigDecimal availableLimit, String poolStatus,
                                         LocalDate effectiveDate, List<PoolMember> members) {
        CashPool pool = new CashPool();
        pool.id = id;
        pool.poolId = poolId;
        pool.mainAccountId = mainAccountId;
        pool.poolName = poolName;
        pool.poolCurrency = poolCurrency;
        pool.totalLimit = totalLimit;
        pool.usedLimit = usedLimit;
        pool.availableLimit = availableLimit;
        pool.poolStatus = poolStatus;
        pool.effectiveDate = effectiveDate;
        pool.members = members != null ? members : new ArrayList<>();
        return pool;
    }

    /**
     * 添加成员到资金池 - 校验成员信息并加入池成员列表
     * Add member to cash pool - Validate member info and add to member list
     */
    public void addMember(PoolMember member) {
        if (member == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "资金池成员不能为空");
        }
        if (!this.poolId.equals(member.getPoolId())) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "成员所属资金池编号不匹配");
        }
        this.members.add(member);
        recalculateLimits();
        markUpdated();
        log.info("资金池成员添加成功, poolId: {}, memberAccountId: {}, memberType: {}",
                poolId, member.getMemberAccountId(), member.getMemberType());
    }

    /**
     * 重新计算可用额度 - 基于总额度与已使用额度
     * Recalculate available limit based on total and used limits
     */
    public BigDecimal calculateAvailable() {
        if (totalLimit == null || usedLimit == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal available = totalLimit.subtract(usedLimit);
        this.availableLimit = available;
        return available;
    }

    /**
     * 暂停资金池 - 将状态置为SUSPENDED，暂停所有操作
     * Suspend cash pool - Set status to SUSPENDED, pause all operations
     */
    public void suspend() {
        if ("CLOSED".equals(this.poolStatus)) {
            throw new IllegalStateException("已关闭的资金池无法暂停");
        }
        this.poolStatus = "SUSPENDED";
        markUpdated();
        log.info("资金池已暂停, poolId: {}", poolId);
    }

    /**
     * 关闭资金池 - 将状态置为CLOSED，终止所有操作
     * Close cash pool - Set status to CLOSED, terminate all operations
     */
    public void close() {
        if ("CLOSED".equals(this.poolStatus)) {
            throw new IllegalStateException("资金池已关闭，无需重复操作");
        }
        if (usedLimit.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("资金池仍有未清偿额度，无法关闭，已使用额度: " + usedLimit);
        }
        this.poolStatus = "CLOSED";
        this.availableLimit = BigDecimal.ZERO;
        markUpdated();
        log.info("资金池已关闭, poolId: {}", poolId);
    }

    private void recalculateLimits() {
        if (members == null || members.isEmpty()) {
            return;
        }
        BigDecimal totalContribution = members.stream()
                .map(PoolMember::getContributionLimit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalLimit = totalContribution;
        calculateAvailable();
    }

    /**
     * 验证资金池关键字段 - 确保必填字段不为空、限额为正数
     * Validate cash pool mandatory fields - Ensure required fields are not null, limits positive
     */
    @Override
    protected void validate() {
        if (poolId == null || poolId.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "资金池编号不能为空");
        }
        if (mainAccountId == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "主账户ID不能为空");
        }
        if (poolCurrency == null || poolCurrency.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "资金池币种不能为空");
        }
        if (totalLimit == null || totalLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "总额度不能为负数");
        }
        if (effectiveDate == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAIL, "生效日期不能为空");
        }
    }
}
