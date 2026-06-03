<template>
  <div class="confirmation-board">
    <h2>确认匹配看板</h2>

    <a-card style="margin-bottom: 16px">
      <a-form layout="inline" :model="initiateForm">
        <a-form-item label="交易编号">
          <a-input v-model:value="initiateForm.tradeNo" style="width: 160px" />
        </a-form-item>
        <a-form-item label="交易类型">
          <a-select v-model:value="initiateForm.tradeType" style="width: 120px">
            <a-select-option value="CFETS">CFETS</a-select-option>
            <a-select-option value="OTC">OTC</a-select-option>
            <a-select-option value="VOICE">VOICE</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="币种对">
          <a-input v-model:value="initiateForm.currencyPair" style="width: 100px" />
        </a-form-item>
        <a-form-item label="金额">
          <a-input-number v-model:value="initiateForm.amount" style="width: 140px" :min="0" />
        </a-form-item>
        <a-form-item label="汇率">
          <a-input-number v-model:value="initiateForm.rate" style="width: 120px" :min="0" :step="0.0001" />
        </a-form-item>
        <a-form-item label="起息日">
          <a-date-picker v-model:value="initiateForm.valueDate" style="width: 140px" />
        </a-form-item>
        <a-form-item label="对手方">
          <a-input v-model:value="initiateForm.counterparty" style="width: 140px" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleInitiate">发起确认</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
      <a-tab-pane key="CENTRALIZED" tab="集中确认 (CFETS)" />
      <a-tab-pane key="BILATERAL" tab="双边确认 (SWIFT)" />
    </a-tabs>

    <a-card style="margin-top: 16px">
      <a-table
        :columns="columns"
        :data-source="filteredData"
        :pagination="pagination"
        row-key="confirmId"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'currencyPair'">
            <strong>{{ record.currencyPair }}</strong>
          </template>
          <template v-else-if="column.key === 'amount'">
            {{ record.amount }} <span style="color: #8c8c8c; font-size: 12px">@ {{ record.rate }}</span>
          </template>
          <template v-else-if="column.key === 'matchStatus'">
            <a-tag :color="statusColor(record.matchStatus)">
              {{ statusLabel(record.matchStatus) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'confirmFlag'">
            <a-tag :color="record.confirmFlag === 'CENTRALIZED' ? '#1677ff' : '#722ed1'">
              {{ record.confirmFlag === 'CENTRALIZED' ? '集中确认' : '双边确认' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-space>
              <a-button v-if="record.matchStatus === 'DISCREPANCY'" type="link" size="small" @click="handleRetry(record)">
                重试
              </a-button>
              <a-button v-if="record.matchStatus === 'DISCREPANCY'" type="link" size="small" @click="handleManualResolve(record)">
                人工干预
              </a-button>
              <span v-if="record.matchStatus === 'MATCHED'" style="color: #52c41a">已匹配</span>
              <span v-if="record.matchStatus === 'MANUALLY_RESOLVED'" style="color: #fa8c16">已人工处理</span>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="manualResolveVisible" title="人工干预" @ok="handleConfirmResolve" @cancel="manualResolveVisible = false">
      <a-form layout="vertical">
        <a-form-item label="确认ID">
          <a-input :value="currentRecord?.confirmId" disabled />
        </a-form-item>
        <a-form-item label="差异描述">
          <a-textarea :value="currentRecord?.discrepancyDetail" disabled :rows="2" />
        </a-form-item>
        <a-form-item label="干预动作" required>
          <a-radio-group v-model:value="resolveAction">
            <a-radio value="ACCEPT_EXTERNAL">接受外部数据</a-radio>
            <a-radio value="REJECT_EXTERNAL">拒绝外部数据</a-radio>
            <a-radio value="AMEND_INTERNAL">修改内部数据</a-radio>
            <a-radio value="FORCE_MATCH">强制匹配</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="resolveComment" :rows="3" placeholder="输入干预原因..." />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { message } from 'ant-design-vue'
import type { TablePaginationConfig } from 'ant-design-vue'
import dayjs from 'dayjs'

interface ConfirmationRecord {
  confirmId: string
  tradeNo: string
  tradeType: string
  confirmFlag: string
  currencyPair: string
  amount: string
  rate: string
  valueDate: string
  counterparty: string
  matchStatus: string
  discrepancyDetail: string
}

const activeTab = ref('CENTRALIZED')

const columns = [
  { title: '交易编号', dataIndex: 'tradeNo', key: 'tradeNo' },
  { title: '币种对', dataIndex: 'currencyPair', key: 'currencyPair' },
  { title: '金额 / 汇率', dataIndex: 'amount', key: 'amount' },
  { title: '起息日', dataIndex: 'valueDate', key: 'valueDate' },
  { title: '对手方', dataIndex: 'counterparty', key: 'counterparty' },
  { title: '确认方式', dataIndex: 'confirmFlag', key: 'confirmFlag', width: 110 },
  { title: '匹配状态', dataIndex: 'matchStatus', key: 'matchStatus', width: 120 },
  { title: '操作', key: 'operation', width: 180 }
]

const mockData: ConfirmationRecord[] = [
  { confirmId: 'CFMA1B2C3D4E5F6', tradeNo: 'FX20240001', tradeType: 'CFETS', confirmFlag: 'CENTRALIZED', currencyPair: 'USD/CNY', amount: '100,000.00', rate: '7.2536', valueDate: '2024-06-01', counterparty: 'BANK_A', matchStatus: 'MATCHED', discrepancyDetail: '' },
  { confirmId: 'CFMF1E2D3C4B5A6', tradeNo: 'FX20240002', tradeType: 'CFETS', confirmFlag: 'CENTRALIZED', currencyPair: 'EUR/CNY', amount: '50,000.00', rate: '7.8532', valueDate: '2024-06-01', counterparty: 'BANK_B', matchStatus: 'MATCHED', discrepancyDetail: '' },
  { confirmId: 'CFMA3B4C5D6E7F8', tradeNo: 'FX20240003', tradeType: 'CFETS', confirmFlag: 'CENTRALIZED', currencyPair: 'GBP/CNY', amount: '75,000.00', rate: '9.1234', valueDate: '2024-06-02', counterparty: 'BANK_C', matchStatus: 'DISCREPANCY', discrepancyDetail: '金额差异: 外部 75,000 vs 内部 75,500' },
  { confirmId: 'CFMB5C6D7E8F9A0', tradeNo: 'FX20240005', tradeType: 'OTC', confirmFlag: 'BILATERAL', currencyPair: 'USD/JPY', amount: '200,000.00', rate: '156.78', valueDate: '2024-06-03', counterparty: 'BANK_D', matchStatus: 'DISCREPANCY', discrepancyDetail: '起息日不匹配: 外部 0603 vs 内部 0604' },
  { confirmId: 'CFMC7D8E9F0A1B2', tradeNo: 'FX20240006', tradeType: 'VOICE', confirmFlag: 'BILATERAL', currencyPair: 'AUD/USD', amount: '150,000.00', rate: '0.6620', valueDate: '2024-06-04', counterparty: 'BANK_E', matchStatus: 'MATCHED', discrepancyDetail: '' },
  { confirmId: 'CFMD9E0F1A2B3C4', tradeNo: 'FX20240008', tradeType: 'CFETS', confirmFlag: 'CENTRALIZED', currencyPair: 'USD/CNY', amount: '199,999.80', rate: '7.2536', valueDate: '2024-06-01', counterparty: 'BANK_F', matchStatus: 'MANUALLY_RESOLVED', discrepancyDetail: '已接受外部数据' }
]

const pagination = reactive({ current: 1, pageSize: 10, total: mockData.length })

const filteredData = computed(() => {
  const filtered = mockData.filter(r => r.confirmFlag === activeTab.value)
  pagination.total = filtered.length
  return filtered
})

const initiateForm = reactive({
  tradeNo: '',
  tradeType: 'CFETS' as string,
  currencyPair: 'USD/CNY',
  amount: 100000 as number,
  rate: 7.2536 as number,
  valueDate: null as any,
  counterparty: 'BANK_A'
})

const manualResolveVisible = ref(false)
const currentRecord = ref<ConfirmationRecord | null>(null)
const resolveAction = ref('ACCEPT_EXTERNAL')
const resolveComment = ref('')

function statusColor(status: string) {
  const map: Record<string, string> = {
    MATCHED: '#52c41a',
    MATCHING: '#1677ff',
    UNMATCHED: '#fa8c16',
    DISCREPANCY: '#f5222d',
    MANUALLY_RESOLVED: '#fa8c16'
  }
  return map[status] || '#8c8c8c'
}

function statusLabel(status: string) {
  const map: Record<string, string> = {
    MATCHED: '已匹配',
    MATCHING: '匹配中',
    UNMATCHED: '未匹配',
    DISCREPANCY: '差异',
    MANUALLY_RESOLVED: '已人工处理'
  }
  return map[status] || status
}

function handleTabChange(key: string) {
  activeTab.value = key
}

function handleTableChange(pg: TablePaginationConfig) {
  pagination.current = pg.current!
  pagination.pageSize = pg.pageSize!
}

function handleInitiate() {
  if (!initiateForm.tradeNo) {
    message.warning('请输入交易编号')
    return
  }
  const newRecord: ConfirmationRecord = {
    confirmId: 'CFM' + Math.random().toString(36).substring(2, 14).toUpperCase(),
    tradeNo: initiateForm.tradeNo,
    tradeType: initiateForm.tradeType,
    confirmFlag: initiateForm.tradeType === 'CFETS' ? 'CENTRALIZED' : 'BILATERAL',
    currencyPair: initiateForm.currencyPair,
    amount: initiateForm.amount.toFixed(2),
    rate: initiateForm.rate.toFixed(4),
    valueDate: initiateForm.valueDate ? dayjs(initiateForm.valueDate).format('YYYY-MM-DD') : '2024-06-01',
    counterparty: initiateForm.counterparty,
    matchStatus: 'MATCHED',
    discrepancyDetail: ''
  }
  mockData.unshift(newRecord)
  message.success(`确认已发起: ${newRecord.confirmId}`)
}

function handleRetry(record: ConfirmationRecord) {
  record.matchStatus = 'MATCHING'
  setTimeout(() => {
    record.matchStatus = 'MATCHED'
    message.success(`确认 ${record.confirmId} 重新匹配成功`)
  }, 1000)
}

function handleManualResolve(record: ConfirmationRecord) {
  currentRecord.value = record
  resolveAction.value = 'ACCEPT_EXTERNAL'
  resolveComment.value = ''
  manualResolveVisible.value = true
}

function handleConfirmResolve() {
  if (!currentRecord.value) return
  currentRecord.value.matchStatus = 'MANUALLY_RESOLVED'
  manualResolveVisible.value = false
  message.success(`确认 ${currentRecord.value.confirmId} 人工干预完成`)
}
</script>

<style scoped>
.confirmation-board h2 { margin-bottom: 16px; }
</style>
