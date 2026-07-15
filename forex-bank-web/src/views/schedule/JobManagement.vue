<template>
  <div class="job-management">
    <div class="page-header">
      <h2>定时任务 Schedule Jobs</h2>
      <a-space>
        <a-button @click="loadJobs">刷新 Refresh</a-button>
        <a-button type="primary" @click="showCreateModal">添加任务 Add Job</a-button>
      </a-space>
    </div>

    <a-card>
      <a-form layout="inline" class="query-form">
        <a-form-item label="任务名称 Job">
          <a-input v-model:value="queryForm.jobName" allow-clear />
        </a-form-item>
        <a-form-item label="任务组 Group">
          <a-input v-model:value="queryForm.jobGroup" allow-clear />
        </a-form-item>
        <a-form-item label="状态 Status">
          <a-select v-model:value="queryForm.status" allow-clear style="width: 140px">
            <a-select-option value="ENABLED">启用 Enabled</a-select-option>
            <a-select-option value="DISABLED">停用 Disabled</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询 Search</a-button>
        </a-form-item>
      </a-form>

      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        :scroll="{ x: 1100 }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'ENABLED' ? '#52c41a' : '#8c8c8c'">
              {{ record.status === 'ENABLED' ? '启用 Enabled' : '停用 Disabled' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'lastExecuteTime'">
            {{ formatTime(record.lastExecuteTime) }}
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-space>
              <a-switch
                :checked="record.status === 'ENABLED'"
                checked-children="启用"
                un-checked-children="停用"
                @change="() => handleToggle(record)"
              />
              <a-button size="small" @click="showEditModal(record)">编辑 Edit</a-button>
              <a-button size="small" type="primary" ghost @click="handleTrigger(record)">执行 Run</a-button>
              <a-button size="small" @click="openLogs(record)">日志 Logs</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="formVisible"
      :title="editingJobId ? '编辑任务 Edit Job' : '添加任务 Add Job'"
      :confirm-loading="submitting"
      @ok="handleSubmit"
    >
      <a-form :model="formData" layout="vertical">
        <a-form-item label="任务名称 Job Name" required>
          <a-input v-model:value="formData.jobName" />
        </a-form-item>
        <a-form-item label="任务组 Job Group" required>
          <a-input v-model:value="formData.jobGroup" />
        </a-form-item>
        <a-form-item label="处理器 Handler" required>
          <a-input v-model:value="formData.jobHandler" />
        </a-form-item>
        <a-form-item label="Cron表达式 Cron" required>
          <a-input v-model:value="formData.cronExpression" />
        </a-form-item>
        <a-form-item label="描述 Description">
          <a-textarea v-model:value="formData.jobDesc" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal v-model:open="logVisible" title="执行日志 Execution Logs" width="880px" :footer="null">
      <a-table
        :columns="logColumns"
        :data-source="execLogs"
        :pagination="false"
        :loading="logLoading"
        row-key="id"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'executeStatus'">
            <a-tag :color="record.executeStatus === 'SUCCESS' ? '#52c41a' : '#f5222d'">
              {{ record.executeStatus === 'SUCCESS' ? '成功 Success' : '失败 Failed' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'startTime' || column.key === 'endTime'">
            {{ formatTime(record[column.key]) }}
          </template>
          <template v-else-if="column.key === 'executeResult'">
            <span class="result-text">{{ record.executeResult || record.errorMsg || '-' }}</span>
          </template>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { TablePaginationConfig } from 'ant-design-vue'
import { scheduleApi } from '@/api/business'

interface JobRecord {
  id: number
  jobName: string
  jobGroup: string
  jobHandler: string
  cronExpression: string
  jobDesc?: string
  status: string
  lastResult?: string
  lastExecuteTime?: string
  nextExecuteTime?: string
}

interface ExecLogRecord {
  id: number
  jobId: number
  jobName: string
  jobHandler: string
  startTime?: string
  endTime?: string
  executeStatus: string
  executeResult?: string
  errorMsg?: string
}

const columns = [
  { title: '任务名称 Job', dataIndex: 'jobName', key: 'jobName', width: 150 },
  { title: '任务组 Group', dataIndex: 'jobGroup', key: 'jobGroup', width: 110 },
  { title: '处理器 Handler', dataIndex: 'jobHandler', key: 'jobHandler', width: 170 },
  { title: 'Cron表达式 Cron', dataIndex: 'cronExpression', key: 'cronExpression', width: 150 },
  { title: '状态 Status', dataIndex: 'status', key: 'status', width: 120 },
  { title: '上次执行 Last Run', dataIndex: 'lastExecuteTime', key: 'lastExecuteTime', width: 180 },
  { title: '描述 Description', dataIndex: 'jobDesc', key: 'jobDesc', width: 220 },
  { title: '操作 Operations', key: 'operation', width: 300, fixed: 'right' }
]

const logColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '开始时间 Start', dataIndex: 'startTime', key: 'startTime', width: 180 },
  { title: '结束时间 End', dataIndex: 'endTime', key: 'endTime', width: 180 },
  { title: '状态 Status', dataIndex: 'executeStatus', key: 'executeStatus', width: 130 },
  { title: '结果 Result', dataIndex: 'executeResult', key: 'executeResult' }
]

const tableData = ref<JobRecord[]>([])
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })

const queryForm = reactive({
  jobName: '',
  jobGroup: '',
  status: undefined as string | undefined
})

const formVisible = ref(false)
const submitting = ref(false)
const editingJobId = ref<number | null>(null)
const formData = reactive({
  jobName: '',
  jobGroup: '',
  jobHandler: '',
  cronExpression: '',
  jobDesc: ''
})

const logVisible = ref(false)
const logLoading = ref(false)
const execLogs = ref<ExecLogRecord[]>([])

async function loadJobs() {
  loading.value = true
  try {
    console.info('[Schedule] loading jobs / 正在加载定时任务', {
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      ...queryForm
    })
    const res = await scheduleApi.pageJobs({
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      jobName: queryForm.jobName,
      jobGroup: queryForm.jobGroup,
      status: queryForm.status
    })
    tableData.value = res.data.data?.records || []
    pagination.total = res.data.data?.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.current = 1
  loadJobs()
}

function handleTableChange(pg: TablePaginationConfig) {
  pagination.current = pg.current || 1
  pagination.pageSize = pg.pageSize || 10
  loadJobs()
}

function resetForm() {
  editingJobId.value = null
  Object.assign(formData, {
    jobName: '',
    jobGroup: '',
    jobHandler: '',
    cronExpression: '',
    jobDesc: ''
  })
}

function showCreateModal() {
  resetForm()
  formVisible.value = true
}

function showEditModal(record: JobRecord) {
  editingJobId.value = record.id
  Object.assign(formData, {
    jobName: record.jobName,
    jobGroup: record.jobGroup,
    jobHandler: record.jobHandler,
    cronExpression: record.cronExpression,
    jobDesc: record.jobDesc || ''
  })
  formVisible.value = true
}

function toPayload() {
  // Normalize the form into the backend JobCmd contract.
  // 将表单规整为后端 JobCmd 接口契约。
  return {
    jobName: formData.jobName,
    jobGroup: formData.jobGroup,
    jobHandler: formData.jobHandler,
    cronExpression: formData.cronExpression,
    jobDesc: formData.jobDesc
  }
}

async function handleSubmit() {
  if (!formData.jobName || !formData.jobGroup || !formData.jobHandler || !formData.cronExpression) {
    message.warning('请填写必填项 Fill required fields')
    return
  }
  submitting.value = true
  try {
    console.info('[Schedule] saving job / 正在保存定时任务', toPayload())
    if (editingJobId.value) {
      await scheduleApi.updateJob(editingJobId.value, toPayload())
      message.success('任务已更新 Job updated')
    } else {
      await scheduleApi.addJob(toPayload())
      message.success('任务已添加 Job added')
    }
    formVisible.value = false
    await loadJobs()
  } finally {
    submitting.value = false
  }
}

async function handleToggle(record: JobRecord) {
  console.info('[Schedule] toggling job / 正在切换定时任务状态', record.id, record.jobHandler)
  await scheduleApi.toggleJob(record.id)
  message.success(`任务 ${record.jobName} 状态已切换 Job status changed`)
  await loadJobs()
}

async function handleTrigger(record: JobRecord) {
  console.info('[Schedule] triggering job / 正在触发定时任务', record.id, record.jobHandler)
  await scheduleApi.triggerJob(record.id)
  message.success(`任务 ${record.jobName} 已触发 Job triggered`)
  await Promise.all([loadJobs(), openLogs(record)])
}

async function openLogs(record: JobRecord) {
  logVisible.value = true
  logLoading.value = true
  try {
    console.info('[Schedule] loading job logs / 正在加载任务日志', record.id)
    const res = await scheduleApi.getJobLogs(record.id)
    execLogs.value = res.data.data || []
  } finally {
    logLoading.value = false
  }
}

function formatTime(value?: string) {
  if (!value) return '-'
  return value.replace('T', ' ')
}

onMounted(loadJobs)
</script>

<style scoped>
.job-management {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-header h2 {
  margin: 0;
}

.query-form {
  margin-bottom: 16px;
}

.result-text {
  display: inline-block;
  max-width: 360px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: middle;
}
</style>
