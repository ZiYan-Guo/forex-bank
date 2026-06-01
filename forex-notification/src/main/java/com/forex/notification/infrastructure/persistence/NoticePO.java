package com.forex.notification.infrastructure.persistence;

import com.forex.common.mybatis.base.BasePO;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_notice")
public class NoticePO extends BasePO {

    private String title;
    private String content;
    private String noticeType;
    private String publishStatus;
    private Long publisherId;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;
}
