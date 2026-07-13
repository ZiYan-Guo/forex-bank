package com.forex.risk.domain.repository;

import com.forex.risk.domain.model.entity.SamplingTask;

import java.util.List;
import java.util.Optional;

/**
 * Sampling task repository interface.
 * 抽查任务仓储接口。
 */
public interface SamplingTaskRepository {

    /** Save or update one task by stable taskId. 按稳定任务编号保存或更新任务。 */
    SamplingTask save(SamplingTask task);

    /** Save or update a batch of tasks. 批量保存或更新任务。 */
    List<SamplingTask> saveAll(List<SamplingTask> tasks);

    /** Find one task by taskId. 根据任务编号查询任务。 */
    Optional<SamplingTask> findByTaskId(String taskId);

    /** List all tasks, newest first. 查询全部任务，按创建时间倒序。 */
    List<SamplingTask> findAll();
}
