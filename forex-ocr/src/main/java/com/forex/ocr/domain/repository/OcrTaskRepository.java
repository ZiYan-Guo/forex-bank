package com.forex.ocr.domain.repository;

import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;
import com.forex.ocr.domain.model.aggregate.OcrTask;

import java.util.Optional;

public interface OcrTaskRepository {

    OcrTask save(OcrTask task);

    Optional<OcrTask> findById(Long id);

    Optional<OcrTask> findByTaskId(String taskId);

    PageResp<OcrTask> pageQuery(PageReq pageReq);
}
