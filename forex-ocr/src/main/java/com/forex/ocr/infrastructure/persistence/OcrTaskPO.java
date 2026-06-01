package com.forex.ocr.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableName;
import com.forex.common.mybatis.base.BasePO;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_ocr_task")
public class OcrTaskPO extends BasePO {

    private String taskId;
    private String docType;
    private String fileName;
    private String filePath;
    private String ocrResult;
    private String recognizedFields;
    private String status;
    private String errorMsg;
    private LocalDateTime uploadTime;
    private LocalDateTime completeTime;
}
