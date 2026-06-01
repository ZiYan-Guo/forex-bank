package com.forex.saccr.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_saccr_result")
public class SaccrResultPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String calcNo;
    private Long tradeId;
    private String tradeNo;
    private String counterPartyId;
    private LocalDate calcDate;
    private BigDecimal rc;
    private BigDecimal pfe;
    private BigDecimal exposure;
    private BigDecimal alpha;
    private String calcMethod;
    private String resultJson;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
