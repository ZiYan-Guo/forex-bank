<template>
  <div class="clearing-list">
    <h2>清算管理</h2>

    <a-card class="search-card">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="清算渠道">
          <a-select v-model:value="queryParams.clearingChannel" allow-clear style="width: 140px" @change="handleSearch">
            <a-select-option value="SWIFT">SWIFT</a-select-option>
            <a-select-option value="CIPS">CIPS</a-select-option>
            <a-select-option value="CHIPS">CHIPS</a-select-option>
            <a-select-option value="CLS">CLS</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="queryParams.instructionStatus" allow-clear style="width: 120px" @change="handleSearch">
            <a-select-option v-for="(v, k) in instructionStatusMap" :key="k" :value="k">{{ v.label }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card style="margin-top: 16px">
      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        row-key="instructionNo"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'clearingChannel'">
            <a-tag :color="channelColors[record.clearingChannel]">{{ record.clearingChannel }}</a-tag>
          </template>
          <template v-else-if="column.key === 'instructionStatus'">
            <a-tag :color="instructionStatusMap[record.instructionStatus]?.color">{{ instructionStatusMap[record.instructionStatus]?.label }}</a-tag>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-space>
              <a @click="handleAction(record, 'generate')">生成</a>
              <a @click="handleAction(record, 'send')">发送</a>
              <a @click="handleAction(record, 'ack')">确认</a>
              <a @click="handleAction(record, 'settle')">结算</a>
              <a-popconfirm title="确定要取消吗？" @confirm="handleAction(record, 'cancel')">
                <a style="color: #f5222d">取消</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-drawer title="清算指令详情" :open="detailVisible" :width="600" @close="detailVisible = false">
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="指令编号">{{ detailData.instructionNo }}</a-descriptions-item>
        <a-descriptions-item label="业务类型">{{ detailData.bizType }}</a-descriptions-item>
        <a-descriptions-item label="业务编号">{{ detailData.bizNo }}</a-descriptions-item>
        <a-descriptions-item label="清算渠道">
          <a-tag :color="channelColors[detailData.clearingChannel]">{{ detailData.clearingChannel }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="支付金额">{{ detailData.payAmount }} {{ detailData.payCurrency }}</a-descriptions-item>
        <a-descriptions-item label="起息日">{{ detailData.valueDate }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="instructionStatusMap[detailData.instructionStatus]?.color">{{ instructionStatusMap[detailData.instructionStatus]?.label }}</a-tag>
        </a-descriptions-item>
      </a-descriptions>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { paymentApi } from '@/api/business'
import type { TablePaginationConfig } from 'ant-design-vue'

const channelColors: Record<string, string> = { SWIFT: '#1677ff', CIPS: '#52c41a', CHIPS: '#fa8c16', CLS: '#722ed1' }

const instructionStatusMap: Record<string, { label: string; color: string }> = {
  PENDING: { label: '待处理', color: '#fa8c16' },
  GENERATED: { label: '已生成', color: '#1677ff' },
  SENT: { label: '已发送', color: '#1677ff' },
  ACKNOWLEDGED: { label: '已确认', color: '#52c41a' },
  SETTLED: { label: '已结算', color: '#52c41a' },
  CANCELLED: { label: '已取消', color: '#8c8c8c' }
}

interface ClearingRecord {
  instructionNo: string
  bizType: string
  bizNo: string
  clearingChannel: string
  payAmount: number
  payCurrency: string
  valueDate: string
  instructionStatus: string
}

const columns = [
  { title: '指令编号', dataIndex: 'instructionNo', key: 'instructionNo' },
  { title: '业务类型', dataIndex: 'bizType', key: 'bizType' },
  { title: '业务编号', dataIndex: 'bizNo', key: 'bizNo' },
  { title: '清算渠道', dataIndex: 'clearingChannel', key: 'clearingChannel' },
  { title: '支付金额', dataIndex: 'payAmount', key: 'payAmount' },
  { title: '币种', dataIndex: 'payCurrency', key: 'payCurrency' },
  { title: '起息日', dataIndex: 'valueDate', key: 'valueDate' },
  { title: '状态', dataIndex: 'instructionStatus', key: 'instructionStatus' },
  { title: '操作', key: 'operation', width: 220 }
]

const mockData: ClearingRecord[] = [
  { instructionNo: 'CL20240001', bizType: 'PAYMENT', bizNo: 'OUT20240001', clearingChannel: 'SWIFT', payAmount: 1000000, payCurrency: 'USD', valueDate: '2024-01-20', instructionStatus: 'SETTLED' },
  { instructionNo: 'CL20240002', bizType: 'TRADING', bizNo: 'TR20240001', clearingChannel: 'CLS', payAmount: 500000, payCurrency: 'EUR', valueDate: '2024-01-22', instructionStatus: 'GENERATED' }
]

const tableData = ref<ClearingRecord[]>(mockData)
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: mockData.length })

const queryParams = reactive({ clearingChannel: undefined as string | undefined, instructionStatus: undefined as string | undefined })

const detailVisible = ref(false)
const detailData = reactive<ClearingRecord>({} as ClearingRecord)

function fetchData() { loading.value = false }

function handleSearch() { fetchData() }
function handleReset() { queryParams.clearingChannel = undefined; queryParams.instructionStatus = undefined; handleSearch() }
function handleTableChange(pg: TablePaginationConfig) { pagination.current = pg.current!; pagination.pageSize = pg.pageSize!; fetchData() }

function showDetail(record: ClearingRecord) { Object.assign(detailData, record); detailVisible.value = true }

function handleAction(record: ClearingRecord, action: string) {
  const labels: Record<string, string> = { generate: '生成', send: '发送', ack: '确认', settle: '结算', cancel: '取消' }
  message.success(`指令 ${record.instructionNo} ${labels[action]}成功`)
}

onMounted(() => fetchData())
</script>

<style scoped>
.clearing-list h2 { margin-bottom: 16px; }
.search-card { margin-bottom: 16px; }
</style>
