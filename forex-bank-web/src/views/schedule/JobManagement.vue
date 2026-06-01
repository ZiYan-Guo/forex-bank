<template>
  <div class="job-management">
    <h2>定时任务</h2>

    <a-card>
      <div style="margin-bottom: 12px">
        <a-button type="primary" @click="showCreateModal">添加任务</a-button>
      </div>
      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        row-key="jobName"
        :expandable="expandedRowRender ? { expandedRowRender } : undefined"
        @change="handleTableChange"
        @expand="handleExpand"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'RUNNING' ? '#52c41a' : '#8c8c8c'">
              {{ record.status === 'RUNNING' ? '运行中' : '已停止' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-space>
              <a-switch
                :checked="record.status === 'RUNNING'"
                checked-children="运行"
                un-checked-children="停止"
                @click="handleToggle(record)"
              />
              <a-button size="small" @click="handleTrigger(record)">执行</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="formVisible" title="添加任务" :confirm-loading="submitting" @ok="handleSubmit">
      <a-form :model="formData" layout="vertical">
        <a-form-item label="任务名称" required>
          <a-input v-model:value="formData.jobName" />
        </a-form-item>
        <a-form-item label="任务组" required>
          <a-input v-model:value="formData.jobGroup" />
        </a-form-item>
        <a-form-item label="处理器" required>
          <a-input v-model:value="formData.jobHandler" />
        </a-form-item>
        <a-form-item label="Cron表达式" required>
          <a-input v-model:value="formData.cronExpression" placeholder="0 0 2 * * ?" />
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-model:value="formData.description" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal v-model:open="logVisible" title="执行日志" width="800" :footer="null">
      <a-table :columns="logColumns" :data-source="execLogs" :pagination="logPagination" size="small" @change="handleLogTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'execStatus'">
            <a-tag :color="record.execStatus === 'SUCCESS' ? '#52c41a' : '#f5222d'">
              {{ record.execStatus === 'SUCCESS' ? '成功' : '失败' }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, h, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { TablePaginationConfig } from 'ant-design-vue'

interface JobRecord {
  jobName: string
  jobGroup: string
  jobHandler: string
  cronExpression: string
  description: string
  status: string
  lastExecuteTime: string
}

interface ExecLogRecord {
  id: number
  jobName: string
  startTime: string
  endTime: string
  duration: number
  execStatus: string
  execMessage: string
}

const columns = [
  { title: '任务名称', dataIndex: 'jobName', key: 'jobName' },
  { title: '任务组', dataIndex: 'jobGroup', key: 'jobGroup' },
  { title: '处理器', dataIndex: 'jobHandler', key: 'jobHandler' },
  { title: 'Cron表达式', dataIndex: 'cronExpression', key: 'cronExpression' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '上次执行时间', dataIndex: 'lastExecuteTime', key: 'lastExecuteTime' },
  { title: '操作', key: 'operation', width: 160 }
]

const logColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '开始时间', dataIndex: 'startTime', key: 'startTime' },
  { title: '结束时间', dataIndex: 'endTime', key: 'endTime' },
  { title: '耗时(ms)', dataIndex: 'duration', key: 'duration' },
  { title: '状态', dataIndex: 'execStatus', key: 'execStatus' },
  { title: '消息', dataIndex: 'execMessage', key: 'execMessage' }
]

const mockData: JobRecord[] = [
  { jobName: 'RateSyncJob', jobGroup: 'SYSTEM', jobHandler: 'rateSyncHandler', cronExpression: '0 */5 * * * ?', description: '汇率同步任务', status: 'RUNNING', lastExecuteTime: '2024-01-15 10:30:00' },
  { jobName: 'PositionCalcJob', jobGroup: 'RISK', jobHandler: 'positionCalcHandler', cronExpression: '0 0 18 * * ?', description: '敞口计算任务', status: 'RUNNING', lastExecuteTime: '2024-01-15 18:00:00' },
  { jobName: 'ReportGenJob', jobGroup: 'REPORT', jobHandler: 'reportGenHandler', cronExpression: '0 0 2 * * ?', description: '报表生成任务', status: 'STOPPED', lastExecuteTime: '2024-01-14 02:00:00' }
]

const tableData = ref<JobRecord[]>(mockData)
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: mockData.length })

const formVisible = ref(false)
const submitting = ref(false)
const formData = reactive({ jobName: '', jobGroup: '', jobHandler: '', cronExpression: '', description: '' })

const logVisible = ref(false)
const currentJob = ref<JobRecord | null>(null)
const execLogs = ref<ExecLogRecord[]>([])
const logPagination = reactive({ current: 1, pageSize: 5, total: 0 })

function expandedRowRender(record: JobRecord) {
  return h('div', { style: { padding: '8px 24px' } }, [
    h('p', { style: { margin: 0, color: '#888' } }, `描述: ${record.description}`)
  ])
}

async function fetchData() { loading.value = false }

function handleTableChange(pg: TablePaginationConfig) { pagination.current = pg.current!; pagination.pageSize = pg.pageSize! }

function handleExpand(expanded: boolean, record: JobRecord) {
  if (expanded) {
    execLogs.value = [
      { id: 1, jobName: record.jobName, startTime: '2024-01-15 10:30:00', endTime: '2024-01-15 10:30:05', duration: 5120, execStatus: 'SUCCESS', execMessage: '执行成功' },
      { id: 2, jobName: record.jobName, startTime: '2024-01-15 10:25:00', endTime: '2024-01-15 10:25:04', duration: 3980, execStatus: 'SUCCESS', execMessage: '执行成功' }
    ]
    logPagination.total = execLogs.value.length
    currentJob.value = record
  }
}

function handleLogTableChange(pg: TablePaginationConfig) { logPagination.current = pg.current!; logPagination.pageSize = pg.pageSize! }

function showCreateModal() { Object.assign(formData, { jobName: '', jobGroup: '', jobHandler: '', cronExpression: '', description: '' }); formVisible.value = true }

function handleSubmit() {
  submitting.value = true
  message.success('任务添加成功')
  formVisible.value = false
  submitting.value = false
}

function handleToggle(record: JobRecord) {
  record.status = record.status === 'RUNNING' ? 'STOPPED' : 'RUNNING'
  message.success(`任务 ${record.jobName} ${record.status === 'RUNNING' ? '已启动' : '已停止'}`)
}

function handleTrigger(record: JobRecord) {
  message.success(`任务 ${record.jobName} 已触发执行`)
  record.lastExecuteTime = new Date().toLocaleString()
}

onMounted(() => fetchData())
</script>

<style scoped>
.job-management h2 { margin-bottom: 16px; }
</style>
