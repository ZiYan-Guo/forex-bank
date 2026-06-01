<template>
  <div class="workflow-list">
    <h2>工作流审批</h2>

    <a-card>
      <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
        <a-tab-pane key="all" tab="全部任务">
          <a-table
            :columns="columns"
            :data-source="tableData"
            :pagination="pagination"
            :loading="loading"
            row-key="taskId"
            @change="handleTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="taskStatusMap[record.status]?.color">{{ taskStatusMap[record.status]?.label }}</a-tag>
              </template>
              <template v-else-if="column.key === 'operation'">
                <a-space>
                  <a-button size="small" type="primary" @click="showApproveModal(record, 'approve')">通过</a-button>
                  <a-button size="small" danger @click="showApproveModal(record, 'reject')">驳回</a-button>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="my" tab="我的任务">
          <a-table
            :columns="columns"
            :data-source="myTaskData"
            :pagination="myPagination"
            :loading="loading"
            row-key="taskId"
            @change="handleMyTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="taskStatusMap[record.status]?.color">{{ taskStatusMap[record.status]?.label }}</a-tag>
              </template>
              <template v-else-if="column.key === 'operation'">
                <a-space>
                  <a-button size="small" type="primary" @click="showApproveModal(record, 'approve')">通过</a-button>
                  <a-button size="small" danger @click="showApproveModal(record, 'reject')">驳回</a-button>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <a-modal v-model:open="approveVisible" :title="approveAction === 'approve' ? '审批通过' : '审批驳回'" :confirm-loading="submitting" @ok="handleApproveSubmit">
      <a-form :model="approveForm" layout="vertical">
        <a-form-item label="审批意见" required>
          <a-textarea v-model:value="approveForm.comment" :rows="3" :placeholder="approveAction === 'approve' ? '请输入通过原因' : '请输入驳回原因'" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-drawer title="任务详情" :open="detailVisible" :width="600" @close="detailVisible = false">
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="任务ID">{{ detailData.taskId }}</a-descriptions-item>
        <a-descriptions-item label="业务类型">{{ detailData.bizType }}</a-descriptions-item>
        <a-descriptions-item label="业务编号">{{ detailData.bizNo }}</a-descriptions-item>
        <a-descriptions-item label="标题">{{ detailData.title }}</a-descriptions-item>
        <a-descriptions-item label="处理人">{{ detailData.assignee }}</a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ detailData.createTime }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="taskStatusMap[detailData.status]?.color">{{ taskStatusMap[detailData.status]?.label }}</a-tag>
        </a-descriptions-item>
      </a-descriptions>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { workflowApi } from '@/api/business'
import type { TablePaginationConfig } from 'ant-design-vue'

const taskStatusMap: Record<string, { label: string; color: string }> = {
  PENDING: { label: '待审批', color: '#fa8c16' },
  APPROVED: { label: '已通过', color: '#52c41a' },
  REJECTED: { label: '已驳回', color: '#f5222d' },
  COMPLETED: { label: '已完成', color: '#1677ff' }
}

interface TaskRecord {
  taskId: string
  bizType: string
  bizNo: string
  title: string
  assignee: string
  createTime: string
  status: string
}

const columns = [
  { title: '任务ID', dataIndex: 'taskId', key: 'taskId' },
  { title: '业务类型', dataIndex: 'bizType', key: 'bizType' },
  { title: '业务编号', dataIndex: 'bizNo', key: 'bizNo' },
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '处理人', dataIndex: 'assignee', key: 'assignee' },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'operation', width: 160 }
]

const tableData = ref<TaskRecord[]>([])
const myTaskData = ref<TaskRecord[]>([])
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })
const myPagination = reactive({ current: 1, pageSize: 10, total: 0 })

const activeTab = ref('all')

const approveVisible = ref(false)
const approveAction = ref<'approve' | 'reject'>('approve')
const approveTarget = ref<TaskRecord | null>(null)
const submitting = ref(false)
const approveForm = reactive({ comment: '' })

const detailVisible = ref(false)
const detailData = reactive<TaskRecord>({} as TaskRecord)

async function fetchData() {
  loading.value = true
  try {
    const res = await workflowApi.pageQuery({ pageNum: pagination.current, pageSize: pagination.pageSize })
    const data = res.data.data
    tableData.value = data.records
    pagination.total = data.total
  } catch { } finally { loading.value = false }
}

async function fetchMyTasks() {
  loading.value = true
  try {
    const res = await workflowApi.getMyTasks('currentUser')
    myTaskData.value = res.data.data || []
    myPagination.total = myTaskData.value.length
  } catch { } finally { loading.value = false }
}

function handleTabChange() {
  if (activeTab.value === 'all') fetchData()
  else fetchMyTasks()
}

function handleTableChange(pg: TablePaginationConfig) { pagination.current = pg.current!; pagination.pageSize = pg.pageSize!; fetchData() }
function handleMyTableChange(pg: TablePaginationConfig) { myPagination.current = pg.current!; myPagination.pageSize = pg.pageSize!; fetchMyTasks() }

function showDetail(record: TaskRecord) { Object.assign(detailData, record); detailVisible.value = true }

function showApproveModal(record: TaskRecord, action: 'approve' | 'reject') {
  approveAction.value = action
  approveTarget.value = record
  approveForm.comment = ''
  approveVisible.value = true
}

async function handleApproveSubmit() {
  submitting.value = true
  try {
    await workflowApi.complete(approveTarget.value!.taskId, {
      approved: approveAction.value === 'approve',
      comment: approveForm.comment
    })
    message.success(approveAction.value === 'approve' ? '审批通过' : '已驳回')
    approveVisible.value = false
    activeTab.value === 'all' ? fetchData() : fetchMyTasks()
  } catch { } finally { submitting.value = false }
}

onMounted(() => fetchData())
</script>

<style scoped>
.workflow-list h2 { margin-bottom: 16px; }
</style>
