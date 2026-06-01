<template>
  <div class="ocr-upload">
    <h2>OCR识别</h2>

    <a-card title="上传文件">
      <a-form layout="vertical">
        <a-form-item label="文档类型">
          <a-select v-model:value="docType" style="width: 200px">
            <a-select-option value="ID_CARD">身份证</a-select-option>
            <a-select-option value="PASSPORT">护照</a-select-option>
            <a-select-option value="BUSINESS_LICENSE">营业执照</a-select-option>
            <a-select-option value="BANK_CARD">银行卡</a-select-option>
            <a-select-option value="CONTRACT">合同</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-upload-dragger
            :before-upload="beforeUpload"
            :show-upload-list="false"
            accept="image/*"
          >
            <p class="ant-upload-drag-icon">
              <inbox-outlined />
            </p>
            <p class="ant-upload-text">点击或拖拽文件到此区域上传</p>
            <p class="ant-upload-hint">支持JPG、PNG、PDF格式</p>
          </a-upload-dragger>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card v-if="currentTaskId" title="识别任务" style="margin-top: 16px">
      <a-descriptions :column="3" bordered size="small">
        <a-descriptions-item label="任务ID">{{ currentTaskId }}</a-descriptions-item>
        <a-descriptions-item label="文档类型">{{ docTypeLabel }}</a-descriptions-item>
        <a-descriptions-item label="文件名">{{ fileName }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="taskStatusColor">{{ taskStatus }}</a-tag>
        </a-descriptions-item>
      </a-descriptions>
      <div style="margin-top: 12px">
        <a-button type="primary" :loading="processing" @click="handleProcess">开始识别</a-button>
      </div>
      <a-card v-if="ocrResult" title="识别结果" style="margin-top: 12px" size="small">
        <a-descriptions :column="2" bordered size="small">
          <a-descriptions-item v-for="(value, key) in ocrResult" :key="key" :label="String(key)">
            {{ value }}
          </a-descriptions-item>
        </a-descriptions>
      </a-card>
    </a-card>

    <a-card title="上传历史" style="margin-top: 16px">
      <a-table
        :columns="columns"
        :data-source="historyData"
        :pagination="pagination"
        :loading="historyLoading"
        row-key="taskId"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'docType'">
            <a-tag>{{ docTypeMap[record.docType] }}</a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="statusColors[record.status]">{{ record.status === 'SUCCESS' ? '已完成' : record.status === 'PROCESSING' ? '处理中' : '失败' }}</a-tag>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a @click="viewHistoryResult(record)">查看结果</a>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="resultVisible" title="识别结果详情" width="600" :footer="null">
      <a-descriptions v-if="historyResult" :column="2" bordered size="small">
        <a-descriptions-item v-for="(value, key) in historyResult" :key="key" :label="String(key)">
          {{ value }}
        </a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { message } from 'ant-design-vue'
import { InboxOutlined } from '@ant-design/icons-vue'
import type { TablePaginationConfig } from 'ant-design-vue'

const docTypeMap: Record<string, string> = {
  ID_CARD: '身份证', PASSPORT: '护照', BUSINESS_LICENSE: '营业执照', BANK_CARD: '银行卡', CONTRACT: '合同'
}

const statusColors: Record<string, string> = { SUCCESS: '#52c41a', PROCESSING: '#1677ff', FAILED: '#f5222d' }

interface HistoryRecord {
  taskId: string
  docType: string
  fileName: string
  status: string
  completeTime: string
}

const columns = [
  { title: '任务ID', dataIndex: 'taskId', key: 'taskId' },
  { title: '文档类型', dataIndex: 'docType', key: 'docType' },
  { title: '文件名', dataIndex: 'fileName', key: 'fileName' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '完成时间', dataIndex: 'completeTime', key: 'completeTime' },
  { title: '操作', key: 'operation' }
]

const docType = ref('ID_CARD')
const fileName = ref('')
const currentTaskId = ref('')
const taskStatus = ref('待上传')
const taskStatusColor = ref('#8c8c8c')
const processing = ref(false)
const ocrResult = ref<Record<string, string> | null>(null)

const historyData = ref<HistoryRecord[]>([
  { taskId: 'OCR20240001', docType: 'ID_CARD', fileName: 'idcard.jpg', status: 'SUCCESS', completeTime: '2024-01-15 10:30' },
  { taskId: 'OCR20240002', docType: 'BUSINESS_LICENSE', fileName: 'license.png', status: 'PROCESSING', completeTime: '-' }
])
const historyLoading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 2 })

const resultVisible = ref(false)
const historyResult = ref<Record<string, string> | null>(null)

const docTypeLabel = computed(() => docTypeMap[docType.value])

function beforeUpload(file: File) {
  fileName.value = file.name
  currentTaskId.value = `OCR${Date.now()}`
  taskStatus.value = '已上传'
  taskStatusColor.value = '#1677ff'
  message.success('文件上传成功')
  return false
}

async function handleProcess() {
  processing.value = true
  taskStatus.value = '处理中'
  taskStatusColor.value = '#1677ff'
  try {
    await new Promise(resolve => setTimeout(resolve, 2000))
    ocrResult.value = {
      '姓名': '张三',
      '证件号码': '110101199001011234',
      '出生日期': '1990-01-01',
      '性别': '男',
      '有效期': '2020.01.01-2030.01.01'
    }
    taskStatus.value = '已完成'
    taskStatusColor.value = '#52c41a'
    message.success('识别完成')
  } catch {
    taskStatus.value = '失败'
    taskStatusColor.value = '#f5222d'
    message.error('识别失败')
  } finally { processing.value = false }
}

function handleTableChange(pg: TablePaginationConfig) { pagination.current = pg.current!; pagination.pageSize = pg.pageSize! }

function viewHistoryResult(record: HistoryRecord) {
  historyResult.value = { '识别结果': '模拟数据', '任务ID': record.taskId, '文档类型': docTypeMap[record.docType] || record.docType }
  resultVisible.value = true
}
</script>

<style scoped>
.ocr-upload h2 { margin-bottom: 16px; }
</style>
