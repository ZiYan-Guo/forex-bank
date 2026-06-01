package com.forex.risk.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RiskParamConfig extends BaseEntity {

    private Long id;
    private String paramKey;
    private String paramValue;
    private String paramType;
    private String currency;
    private Integer isEnabled;
}
