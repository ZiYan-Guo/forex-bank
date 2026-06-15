package com.forex.cashpool.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 资金池成员实体 - 描述资金池中的成员账户信息
 * Pool Member Entity - Describes member account info within a cash pool
 */
@Slf4j
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PoolMember extends BaseEntity {

    private Long id;
    private String poolId;
    private Long memberAccountId;
    private String memberType;
    private String currency;
    private String settlementMode;
    private BigDecimal contributionLimit;
    private LocalDate joinDate;

    /**
     * 获取贡献额度 - 返回成员在池中的额度贡献值
     * Get contribution limit - Returns the member's quota contribution in the pool
     */
    public BigDecimal getContributionLimit() {
        return contributionLimit != null ? contributionLimit : BigDecimal.ZERO;
    }
}
