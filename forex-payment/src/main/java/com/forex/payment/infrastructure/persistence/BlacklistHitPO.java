package com.forex.payment.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_payment_blacklist_hit")
public class BlacklistHitPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long paymentId;
    private String paymentNo;
    private String hitType;
    private String hitListName;
    private String hitField;
    private String hitValue;
    private BigDecimal matchScore;
    private LocalDateTime checkTime;
    private String checkResult;
    private Long reviewerId;
    private LocalDateTime reviewTime;
    private String reviewComment;
}
