package com.forex.position.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_position_limit_config")
public class PositionLimitConfigPO extends BasePO {

    private String currency;
    private String limitType;
    private BigDecimal limitAmount;
    private BigDecimal warningPct;
    private Integer isEnabled;
}
