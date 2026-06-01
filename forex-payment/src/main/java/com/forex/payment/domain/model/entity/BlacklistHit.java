package com.forex.payment.domain.model.entity;

import com.forex.common.base.domain.BaseEntity;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class BlacklistHit extends BaseEntity {

    private static final long serialVersionUID = 1L;

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

    public BlacklistHit(Long id, Long paymentId, String paymentNo, String hitType,
                         String hitListName, String hitField, String hitValue,
                         BigDecimal matchScore, LocalDateTime checkTime, String checkResult,
                         Long reviewerId, LocalDateTime reviewTime, String reviewComment) {
        this.id = id;
        this.paymentId = paymentId;
        this.paymentNo = paymentNo;
        this.hitType = hitType;
        this.hitListName = hitListName;
        this.hitField = hitField;
        this.hitValue = hitValue;
        this.matchScore = matchScore;
        this.checkTime = checkTime;
        this.checkResult = checkResult;
        this.reviewerId = reviewerId;
        this.reviewTime = reviewTime;
        this.reviewComment = reviewComment;
    }
}
