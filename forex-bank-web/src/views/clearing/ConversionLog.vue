<template>
  <div class="conversion-log">
    <h2>报文转换日志</h2>

    <a-card class="search-card">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="源类型">
          <a-select v-model:value="queryParams.sourceType" allow-clear style="width: 130px" @change="handleSearch">
            <a-select-option value="MT103">MT103</a-select-option>
            <a-select-option value="MT202">MT202</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="queryParams.status" allow-clear style="width: 100px" @change="handleSearch">
            <a-select-option value="SUCCESS">成功</a-select-option>
            <a-select-option value="FAILED">失败</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="时间范围">
          <a-range-picker v-model:value="queryParams.dateRange" @change="handleSearch" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>
      <div style="margin-top: 12px">
        <a-button type="primary" @click="convertVisible = true">手动转换</a-button>
      </div>
    </a-card>

    <a-card style="margin-top: 16px">
      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'sourceType'">
            <a-tag color="blue">{{ record.sourceType }}</a-tag>
          </template>
          <template v-else-if="column.key === 'targetType'">
            <a-tag color="purple">{{ record.targetType }}</a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 'SUCCESS' ? '#52c41a' : '#f5222d'">
              {{ record.status === 'SUCCESS' ? 'SUCCESS' : 'FAILED' }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="convertVisible" title="手动报文转换" :confirm-loading="converting" @ok="handleConvert">
      <a-form layout="vertical">
        <a-form-item label="源报文类型">
          <a-select v-model:value="convertForm.sourceType" style="width: 100%">
            <a-select-option value="MT103">MT103</a-select-option>
            <a-select-option value="MT202">MT202</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="源报文">
          <a-textarea v-model:value="convertForm.sourceMessage" :rows="8" placeholder="请输入MT报文内容..." />
        </a-form-item>
      </a-form>
      <template v-if="convertResult">
        <a-divider>转换结果</a-divider>
        <a-descriptions :column="1" bordered size="small">
          <a-descriptions-item label="状态">
            <a-tag :color="convertResult.success ? '#52c41a' : '#f5222d'">
              {{ convertResult.success ? 'SUCCESS' : 'FAILED' }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="目标类型">{{ convertResult.targetType }}</a-descriptions-item>
          <a-descriptions-item v-if="convertResult.errorReason" label="错误原因">{{ convertResult.errorReason }}</a-descriptions-item>
        </a-descriptions>
        <a-textarea v-if="convertResult.targetMessage" :value="convertResult.targetMessage" :rows="6" readonly style="margin-top: 8px" />
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'

interface ConversionRecord {
  id: number
  sourceType: string
  targetType: string
  status: string
  errorReason: string
  operator: string
  createTime: string
}

const columns = [
  { title: '日志编号', dataIndex: 'id', key: 'id' },
  { title: '源类型', dataIndex: 'sourceType', key: 'sourceType' },
  { title: '目标类型', dataIndex: 'targetType', key: 'targetType' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '错误原因', dataIndex: 'errorReason', key: 'errorReason' },
  { title: '操作员', dataIndex: 'operator', key: 'operator' },
  { title: '时间', dataIndex: 'createTime', key: 'createTime' },
]

const tableData = ref<ConversionRecord[]>([
  { id: 1, sourceType: 'MT103', targetType: 'pain.001', status: 'SUCCESS', errorReason: '', operator: 'admin', createTime: '2026-06-10 09:30:00' },
  { id: 2, sourceType: 'MT202', targetType: 'pacs.009', status: 'SUCCESS', errorReason: '', operator: 'admin', createTime: '2026-06-10 09:25:00' },
  { id: 3, sourceType: 'MT103', targetType: 'pain.001', status: 'FAILED', errorReason: '字段映射失败: 32A 金额格式异常', operator: 'operator1', createTime: '2026-06-10 08:15:00' },
  { id: 4, sourceType: 'MT202', targetType: 'pacs.009', status: 'FAILED', errorReason: '收款行BIC无法解析', operator: 'operator1', createTime: '2026-06-09 16:40:00' },
  { id: 5, sourceType: 'MT103', targetType: 'pain.001', status: 'SUCCESS', errorReason: '', operator: 'admin', createTime: '2026-06-09 14:20:00' },
])
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 5 })
const queryParams = reactive({ sourceType: undefined as string | undefined, status: undefined as string | undefined, dateRange: undefined })

const convertVisible = ref(false)
const converting = ref(false)
const convertForm = reactive({ sourceType: 'MT103', sourceMessage: '' })
const convertResult = ref<any>(null)

function handleSearch() { pagination.current = 1 }
function handleReset() { queryParams.sourceType = undefined; queryParams.status = undefined; queryParams.dateRange = undefined; handleSearch() }
function handleTableChange(pg: any) { pagination.current = pg.current; pagination.pageSize = pg.pageSize }

async function handleConvert() {
  converting.value = true
  try {
    const targetType = convertForm.sourceType === 'MT103' ? 'pain.001' : 'pacs.009'
    const targetMessage = convertForm.sourceType === 'MT103'
      ? '<?xml version="1.0"?><Document><CstmrCdtTrfInitn>...</CstmrCdtTrfInitn></Document>'
      : '<?xml version="1.0"?><Document><FIToFICstmrCdtTrf>...</FIToFICstmrCdtTrf></Document>'

    convertResult.value = {
      success: true,
      sourceType: convertForm.sourceType,
      targetType,
      targetMessage,
    }
    message.success('转换成功')
  } catch {
    convertResult.value = { success: false, errorReason: '转换失败' }
    message.error('转换失败')
  } finally {
    converting.value = false
  }
}
</script>

<style scoped>
.conversion-log { background: #fff; padding: 24px; border-radius: 8px; }
.conversion-log h2 { margin-bottom: 16px; }
.search-card { margin-bottom: 16px; }
</style>
