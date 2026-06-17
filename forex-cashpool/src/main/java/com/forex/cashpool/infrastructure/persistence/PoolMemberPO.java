package com.forex.cashpool.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pool member persistent object.
 * 资金池成员持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_pool_member")
public class PoolMemberPO extends BasePO {

    private String poolId;
    private Long memberAccountId;
    private String memberType;
    private String currency;
    private String settlementMode;
    private BigDecimal contributionLimit;
    private LocalDate joinDate;
}
