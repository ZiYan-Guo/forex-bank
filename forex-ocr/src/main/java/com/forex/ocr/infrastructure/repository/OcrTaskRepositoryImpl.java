package com.forex.ocr.infrastructure.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.forex.common.base.dto.PageReq;
import com.forex.common.base.dto.PageResp;
import com.forex.ocr.application.query.OcrQuery;
import com.forex.ocr.domain.model.aggregate.OcrTask;
import com.forex.ocr.domain.repository.OcrTaskRepository;
import com.forex.ocr.infrastructure.mapper.OcrTaskMapper;
import com.forex.ocr.infrastructure.persistence.OcrTaskPO;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OcrTaskRepositoryImpl implements OcrTaskRepository {

    private final OcrTaskMapper ocrTaskMapper;

    @Override
    public OcrTask save(OcrTask task) {
        OcrTaskPO po = toPO(task);
        if (task.getId() == null) {
            ocrTaskMapper.insert(po);
        } else {
            ocrTaskMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<OcrTask> findById(Long id) {
        OcrTaskPO po = ocrTaskMapper.selectById(id);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<OcrTask> findByTaskId(String taskId) {
        OcrTaskPO po = ocrTaskMapper.selectByTaskId(taskId);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public PageResp<OcrTask> pageQuery(PageReq pageReq) {
        Page<OcrTaskPO> page = new Page<>(pageReq.getPageNum(), pageReq.getPageSize());
        OcrQuery query = new OcrQuery();
        query.setPageNum(pageReq.getPageNum());
        query.setPageSize(pageReq.getPageSize());
        Page<OcrTaskPO> result = ocrTaskMapper.pageQuery(page, query);
        List<OcrTask> records = result.getRecords().stream()
                .map(this::toDomain)
                .toList();
        return PageResp.of(result.getTotal(), records, pageReq.getPageNum(), pageReq.getPageSize());
    }

    private OcrTask toDomain(OcrTaskPO po) {
        return OcrTask.reconstitute(
                po.getId(),
                po.getTaskId(),
                po.getDocType(),
                po.getFileName(),
                po.getFilePath(),
                po.getOcrResult(),
                po.getRecognizedFields(),
                po.getStatus(),
                po.getErrorMsg(),
                po.getUploadTime(),
                po.getCompleteTime()
        );
    }

    private OcrTaskPO toPO(OcrTask task) {
        OcrTaskPO po = new OcrTaskPO();
        po.setId(task.getId());
        po.setTaskId(task.getTaskId());
        po.setDocType(task.getDocType());
        po.setFileName(task.getFileName());
        po.setFilePath(task.getFilePath());
        po.setOcrResult(task.getOcrResult());
        po.setRecognizedFields(task.getRecognizedFields());
        po.setStatus(task.getStatus());
        po.setErrorMsg(task.getErrorMsg());
        po.setUploadTime(task.getUploadTime());
        po.setCompleteTime(task.getCompleteTime());
        return po;
    }
}
