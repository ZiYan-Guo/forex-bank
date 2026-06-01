package com.forex.risk.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;
import com.forex.common.mybatis.base.BasePO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_risk_param_config")
public class RiskParamConfigPO extends BasePO {

    private String paramKey;
    private String paramValue;
    private String paramType;
    private String currency;
    private Integer isEnabled;
}
