package com.forex.position.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PositionLimitConfig extends BaseEntity {

    private Long id;
    private String currency;
    private String limitType;
    private BigDecimal limitAmount;
    private BigDecimal warningPct;
    private Integer isEnabled;
}
