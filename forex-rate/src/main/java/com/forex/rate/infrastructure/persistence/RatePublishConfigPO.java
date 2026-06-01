package com.forex.rate.infrastructure.persistence;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.forex.common.mybatis.base.BasePO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_rate_publish_config")
public class RatePublishConfigPO extends BasePO {

    private String channelCode;
    private String channelName;
    private BigDecimal spreadAdjust;
    private Integer isEnabled;
    private Integer pushInterval;
}
