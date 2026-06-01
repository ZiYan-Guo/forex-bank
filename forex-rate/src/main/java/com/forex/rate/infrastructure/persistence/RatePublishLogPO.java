package com.forex.rate.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.forex.common.mybatis.base.BasePO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_rate_publish_log")
public class RatePublishLogPO extends BasePO {

    private Long rateId;
    private String channelCode;
    private BigDecimal publishedRate;
    private LocalDateTime publishTime;
    private String publishStatus;
    private String errorMsg;
}
