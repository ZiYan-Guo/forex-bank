package com.forex.saccr.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_simm_result")
public class SimmResultPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String calcNo;
    private Long tradeId;
    private String tradeNo;
    private LocalDate calcDate;
    private BigDecimal notionalAmount;
    private BigDecimal deltaMargin;
    private BigDecimal vegaMargin;
    private BigDecimal curvatureMargin;
    private BigDecimal totalMargin;
    private String calcMethod;
    private String sensitivitiesJson;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
