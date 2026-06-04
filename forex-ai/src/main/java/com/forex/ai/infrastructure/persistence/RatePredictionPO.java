package com.forex.ai.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;
import com.forex.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_rate_prediction")
public class RatePredictionPO extends BasePO {

    private String predNo;
    private String currencyPair;
    private String predType;
    private LocalDateTime predTime;
    private LocalDateTime targetTime;
    private BigDecimal predictedRate;
    private BigDecimal lowerBound;
    private BigDecimal upperBound;
    private BigDecimal confidence;
    private String modelName;
}
