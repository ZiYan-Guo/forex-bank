package com.forex.margin.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_margin_call")
public class MarginCallPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long marginId;
    private String marginNo;
    private String callType;
    private BigDecimal callAmount;
    private LocalDateTime callDate;
    private LocalDateTime responseDate;
    private String responseStatus;
    private LocalDateTime createTime;
}
