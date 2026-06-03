<template>
  <div class="settlement-tracker">
    <h2>结算追踪</h2>

    <a-card class="search-card">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="渠道">
          <a-select v-model:value="queryParams.channel" allow-clear style="width: 140px" @change="handleSearch">
            <a-select-option value="SWIFT">SWIFT</a-select-option>
            <a-select-option value="CIPS">CIPS</a-select-option>
            <a-select-option value="CHIPS">CHIPS</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="queryParams.status" allow-clear style="width: 160px" @change="handleSearch">
            <a-select-option v-for="(v, k) in statusMap" :key="k" :value="k">{{ v.label }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-checkbox v-model:checked="queryParams.overdueOnly" @change="handleSearch">
            仅显示逾期
          </a-checkbox>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleCreateTracker">新建追踪</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card style="margin-top: 16px">
      <a-table
        :columns="columns"
        :data-source="filteredData"
        :pagination="pagination"
        row-key="trackingId"
        @change="handleTableChange"
        :row-class-name="getRowClass"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'channel'">
            <a-tag :color="channelColors[record.channel] || '#1677ff'">
              {{ record.channel }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'currentStatus'">
            <div class="status-steps">
              <a-steps :current="statusStep(record.currentStatus)" size="small" :status="record.currentStatus === 'EXCEPTION' ? 'error' : 'process'">
                <a-step title="待发" />
                <a-step title="已发" />
                <a-step title="清算中" />
                <a-step title="已确认" />
                <a-step title="已结算" />
                <a-step title="资金入账" />
              </a-steps>
              <a-tag v-if="record.currentStatus === 'EXCEPTION'" color="#f5222d" style="margin-top: 4px">
                异常: {{ record.exceptionReason }}
              </a-tag>
            </div>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-dropdown>
              <a-button type="link" size="small">
                状态变更 <DownOutlined />
              </a-button>
              <template #overlay>
                <a-menu @click="({ key }: any) => handleStatusChange(record, key as string)">
                  <a-menu-item key="PENDING_SEND">PENDING_SEND</a-menu-item>
                  <a-menu-item key="SENT">SENT</a-menu-item>
                  <a-menu-item key="IN_CLEARING">IN_CLEARING</a-menu-item>
                  <a-menu-item key="ACKNOWLEDGED">ACKNOWLEDGED</a-menu-item>
                  <a-menu-item key="SETTLED">SETTLED</a-menu-item>
                  <a-menu-item key="FUNDS_CREDITED">FUNDS_CREDITED</a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="EXCEPTION" danger>标记异常</a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="createVisible" title="新建结算追踪" @ok="handleConfirmCreate" @cancel="createVisible = false">
      <a-form layout="vertical">
        <a-form-item label="支付编号" required>
          <a-input v-model:value="createForm.paymentNo" placeholder="支付编号" />
        </a-form-item>
        <a-form-item label="指令编号">
          <a-input v-model:value="createForm.instructionNo" placeholder="清算指令编号" />
        </a-form-item>
        <a-form-item label="渠道" required>
          <a-select v-model:value="createForm.channel" style="width: 100%">
            <a-select-option value="SWIFT">SWIFT</a-select-option>
            <a-select-option value="CIPS">CIPS</a-select-option>
            <a-select-option value="CHIPS">CHIPS</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { message } from 'ant-design-vue'
import { DownOutlined } from '@ant-design/icons-vue'
import type { TablePaginationConfig } from 'ant-design-vue'

interface TrackerRecord {
  trackingId: string
  paymentNo: string
  instructionNo: string
  currentStatus: string
  channel: string
  gpiStatus: string
  exceptionReason: string
  valueDate: string
}

const channelColors: Record<string, string> = {
  SWIFT: '#1677ff',
  CIPS: '#52c41a',
  CHIPS: '#fa8c16'
}

const statusMap: Record<string, { label: string; step: number }> = {
  PENDING_SEND: { label: '待发', step: 0 },
  SENT: { label: '已发', step: 1 },
  IN_CLEARING: { label: '清算中', step: 2 },
  ACKNOWLEDGED: { label: '已确认', step: 3 },
  SETTLED: { label: '已结算', step: 4 },
  FUNDS_CREDITED: { label: '资金入账', step: 5 },
  EXCEPTION: { label: '异常', step: 0 }
}

const columns = [
  { title: '追踪ID', dataIndex: 'trackingId', key: 'trackingId' },
  { title: '支付编号', dataIndex: 'paymentNo', key: 'paymentNo' },
  { title: '指令编号', dataIndex: 'instructionNo', key: 'instructionNo' },
  { title: '渠道', dataIndex: 'channel', key: 'channel', width: 100 },
  { title: '结算状态', dataIndex: 'currentStatus', key: 'currentStatus', width: 360 },
  { title: '操作', key: 'operation', width: 120 }
]

const mockData: TrackerRecord[] = [
  { trackingId: 'TRKA1B2C3D4E5F6', paymentNo: 'OUT20240001', instructionNo: 'CL20240001', currentStatus: 'FUNDS_CREDITED', channel: 'SWIFT', gpiStatus: 'CREDITED', exceptionReason: '', valueDate: '2024-05-28' },
  { trackingId: 'TRKF6E5D4C3B2A1', paymentNo: 'OUT20240002', instructionNo: 'CL20240002', currentStatus: 'SETTLED', channel: 'CIPS', gpiStatus: '', exceptionReason: '', valueDate: '2024-05-29' },
  { trackingId: 'TRKB3C4D5E6F7A8', paymentNo: 'OUT20240003', instructionNo: 'CL20240003', currentStatus: 'IN_CLEARING', channel: 'SWIFT', gpiStatus: 'PROCESSING', exceptionReason: '', valueDate: '2024-06-01' },
  { trackingId: 'TRKC5D6E7F8A9B0', paymentNo: 'OUT20240004', instructionNo: 'CL20240004', currentStatus: 'SENT', channel: 'CHIPS', gpiStatus: '', exceptionReason: '', valueDate: '2024-05-30' },
  { trackingId: 'TRKD7E8F9A0B1C2', paymentNo: 'OUT20240005', instructionNo: 'CL20240005', currentStatus: 'EXCEPTION', channel: 'SWIFT', gpiStatus: '', exceptionReason: 'SWIFT ACK超时', valueDate: '2024-05-27' },
  { trackingId: 'TRKE9F0A1B2C3D4', paymentNo: 'OUT20240006', instructionNo: 'CL20240006', currentStatus: 'PENDING_SEND', channel: 'CIPS', gpiStatus: '', exceptionReason: '', valueDate: '2024-06-02' },
  { trackingId: 'TRKF1A2B3C4D5E6', paymentNo: 'OUT20240007', instructionNo: 'CL20240007', currentStatus: 'ACKNOWLEDGED', channel: 'SWIFT', gpiStatus: '', exceptionReason: '', valueDate: '2024-05-31' }
]

const pagination = reactive({ current: 1, pageSize: 10, total: mockData.length })

const queryParams = reactive({
  channel: undefined as string | undefined,
  status: undefined as string | undefined,
  overdueOnly: false
})

const createVisible = ref(false)
const createForm = reactive({
  paymentNo: '',
  instructionNo: '',
  channel: 'SWIFT' as string
})

const filteredData = computed(() => {
  let result = mockData
  if (queryParams.channel) {
    result = result.filter(r => r.channel === queryParams.channel)
  }
  if (queryParams.status) {
    result = result.filter(r => r.currentStatus === queryParams.status)
  }
  if (queryParams.overdueOnly) {
    const today = new Date().toISOString().slice(0, 10)
    result = result.filter(r => r.valueDate < today && r.currentStatus !== 'SETTLED' && r.currentStatus !== 'FUNDS_CREDITED')
  }
  pagination.total = result.length
  return result
})

function statusStep(status: string) {
  return statusMap[status]?.step ?? 0
}

function getRowClass(record: TrackerRecord) {
  const today = new Date().toISOString().slice(0, 10)
  if (record.valueDate < today && record.currentStatus !== 'SETTLED' && record.currentStatus !== 'FUNDS_CREDITED') {
    return 'overdue-row'
  }
  return ''
}

function handleSearch() {}
function handleTableChange(pg: TablePaginationConfig) {
  pagination.current = pg.current!
  pagination.pageSize = pg.pageSize!
}

function handleCreateTracker() {
  createVisible.value = true
}

function handleConfirmCreate() {
  if (!createForm.paymentNo) {
    message.warning('请输入支付编号')
    return
  }
  const newRecord: TrackerRecord = {
    trackingId: 'TRK' + Math.random().toString(36).substring(2, 14).toUpperCase(),
    paymentNo: createForm.paymentNo,
    instructionNo: createForm.instructionNo || '-',
    currentStatus: 'PENDING_SEND',
    channel: createForm.channel,
    gpiStatus: '',
    exceptionReason: '',
    valueDate: new Date().toISOString().slice(0, 10)
  }
  mockData.unshift(newRecord)
  createVisible.value = false
  message.success(`追踪已创建: ${newRecord.trackingId}`)
}

function handleStatusChange(record: TrackerRecord, newStatus: string) {
  if (newStatus === 'EXCEPTION') {
    record.exceptionReason = '手动标记异常'
  } else {
    record.exceptionReason = ''
  }
  record.currentStatus = newStatus
  message.success(`状态已更新为 ${statusMap[newStatus]?.label || newStatus}`)
}
</script>

<style scoped>
.settlement-tracker h2 { margin-bottom: 16px; }
.search-card { margin-bottom: 16px; }
.status-steps { max-width: 360px; }
:deep(.overdue-row) {
  background-color: #fff2f0 !important;
}
</style>
