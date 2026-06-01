package com.forex.ocr.domain.model.aggregate;

import com.forex.common.base.domain.BaseAggregate;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class OcrTask extends BaseAggregate {

    private Long id;
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

    private OcrTask() {
        super();
    }

    public static OcrTask create(String docType, String fileName, String filePath) {
        OcrTask task = new OcrTask();
        task.taskId = UUID.randomUUID().toString().replace("-", "");
        task.docType = docType;
        task.fileName = fileName;
        task.filePath = filePath;
        task.status = "UPLOADED";
        task.uploadTime = LocalDateTime.now();
        task.validate();
        return task;
    }

    public static OcrTask reconstitute(Long id, String taskId, String docType, String fileName,
                                        String filePath, String ocrResult, String recognizedFields,
                                        String status, String errorMsg, LocalDateTime uploadTime,
                                        LocalDateTime completeTime) {
        OcrTask task = new OcrTask();
        task.id = id;
        task.taskId = taskId;
        task.docType = docType;
        task.fileName = fileName;
        task.filePath = filePath;
        task.ocrResult = ocrResult;
        task.recognizedFields = recognizedFields;
        task.status = status;
        task.errorMsg = errorMsg;
        task.uploadTime = uploadTime;
        task.completeTime = completeTime;
        return task;
    }

    public void startProcessing() {
        this.status = "PROCESSING";
        markUpdated();
    }

    public void complete(String result) {
        this.status = "COMPLETED";
        this.ocrResult = result;
        this.completeTime = LocalDateTime.now();
        markUpdated();
    }

    public void fail(String error) {
        this.status = "FAILED";
        this.errorMsg = error;
        this.completeTime = LocalDateTime.now();
        markUpdated();
    }

    @Override
    protected void validate() {
        if (docType == null || docType.isBlank()) {
            throw new IllegalArgumentException("单据类型不能为空");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
    }
}
