package com.forex.risk.infrastructure.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forex.risk.domain.model.entity.SamplingTask;
import com.forex.risk.domain.repository.SamplingTaskRepository;
import com.forex.risk.infrastructure.mapper.SamplingTaskMapper;
import com.forex.risk.infrastructure.persistence.SamplingTaskPO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Sampling task repository implementation.
 * 抽查任务仓储实现。
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SamplingTaskRepositoryImpl implements SamplingTaskRepository {

    private static final TypeReference<List<String>> RULE_LIST_TYPE = new TypeReference<>() {
    };

    private final SamplingTaskMapper samplingTaskMapper;
    private final ObjectMapper objectMapper;

    @Override
    public SamplingTask save(SamplingTask task) {
        SamplingTaskPO po = toPO(task);
        SamplingTaskPO existing = samplingTaskMapper.selectByTaskId(task.getTaskId());
        if (existing == null) {
            samplingTaskMapper.insert(po);
            log.info("Sampling task persisted: taskId={}, bizNo={} / 抽查任务已入库：任务ID={}, 业务编号={}",
                    po.getTaskId(), po.getBizNo(), po.getTaskId(), po.getBizNo());
        } else {
            po.setId(existing.getId());
            samplingTaskMapper.updateById(po);
            log.info("Sampling task updated: taskId={}, status={} / 抽查任务已更新：任务ID={}, 状态={}",
                    po.getTaskId(), po.getStatus(), po.getTaskId(), po.getStatus());
        }
        return findByTaskId(task.getTaskId()).orElseGet(() -> toDomain(po));
    }

    @Override
    public List<SamplingTask> saveAll(List<SamplingTask> tasks) {
        return tasks.stream().map(this::save).toList();
    }

    @Override
    public Optional<SamplingTask> findByTaskId(String taskId) {
        return Optional.ofNullable(samplingTaskMapper.selectByTaskId(taskId)).map(this::toDomain);
    }

    @Override
    public List<SamplingTask> findAll() {
        return samplingTaskMapper.selectAllTasks().stream().map(this::toDomain).toList();
    }

    private SamplingTask toDomain(SamplingTaskPO po) {
        return new SamplingTask(
                po.getId(),
                po.getTaskId(),
                po.getBizNo(),
                po.getBizType(),
                po.getCustomerId(),
                po.getAmount(),
                po.getCurrency(),
                po.getCountryCode(),
                po.getAccountAgeDays(),
                po.getSamplingRate(),
                po.getReason(),
                parseMatchedRules(po.getMatchedRulesJson()),
                po.getStatus(),
                po.getBusinessDate(),
                po.getCreateTime(),
                po.getCompletedAt(),
                po.getReviewResult(),
                po.getReviewComment()
        );
    }

    private SamplingTaskPO toPO(SamplingTask task) {
        SamplingTaskPO po = new SamplingTaskPO();
        po.setId(task.getId());
        po.setTaskId(task.getTaskId());
        po.setBizNo(task.getBizNo());
        po.setBizType(task.getBizType());
        po.setCustomerId(task.getCustomerId());
        po.setAmount(task.getAmount());
        po.setCurrency(task.getCurrency());
        po.setCountryCode(task.getCountryCode());
        po.setAccountAgeDays(task.getAccountAgeDays());
        po.setSamplingRate(task.getSamplingRate());
        po.setReason(task.getReason());
        po.setMatchedRulesJson(writeMatchedRules(task.getMatchedRules()));
        po.setStatus(task.getStatus());
        po.setBusinessDate(task.getBusinessDate());
        po.setCompletedAt(task.getCompletedAt());
        po.setReviewResult(task.getReviewResult());
        po.setReviewComment(task.getReviewComment());
        return po;
    }

    private List<String> parseMatchedRules(String json) {
        try {
            return json == null || json.isBlank() ? List.of() : objectMapper.readValue(json, RULE_LIST_TYPE);
        } catch (Exception e) {
            log.warn("Failed to parse matched sampling rules: json={} / 命中抽查规则解析失败：JSON={}", json, json, e);
            return List.of();
        }
    }

    private String writeMatchedRules(List<String> rules) {
        try {
            return objectMapper.writeValueAsString(rules == null ? List.of() : rules);
        } catch (Exception e) {
            log.warn("Failed to serialize matched sampling rules: rules={} / 命中抽查规则序列化失败：规则={}", rules, rules, e);
            return "[]";
        }
    }
}
