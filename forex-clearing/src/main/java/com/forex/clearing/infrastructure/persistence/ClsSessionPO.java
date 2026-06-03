package com.forex.clearing.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_cls_session")
public class ClsSessionPO extends BasePO {

    private String sessionId;
    private LocalDate settlementDate;
    private LocalDateTime payInWindowStart;
    private LocalDateTime payInWindowEnd;
    private String sessionStatus;
    private BigDecimal totalPayInSum;
    private BigDecimal totalPayOutSum;
    private BigDecimal netPosition;
    private String positionJson;
}
