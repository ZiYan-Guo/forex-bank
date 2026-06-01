<template>
  <div class="margin-list">
    <h2>保证金管理</h2>

    <a-card class="search-card">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="保证金类型">
          <a-select v-model:value="queryParams.marginType" allow-clear style="width: 120px" @change="handleSearch">
            <a-select-option value="INITIAL">初始保证金</a-select-option>
            <a-select-option value="VARIATION">变动保证金</a-select-option>
            <a-select-option value="MAINTENANCE">维持保证金</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model:value="queryParams.status" allow-clear style="width: 120px" @change="handleSearch">
            <a-select-option value="PENDING">待缴纳</a-select-option>
            <a-select-option value="DEPOSITED">已缴纳</a-select-option>
            <a-select-option value="SHORTFALL">不足</a-select-option>
            <a-select-option value="RELEASED">已释放</a-select-option>
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
        row-key="marginNo"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'marginType'">
            <a-tag :color="marginTypeMap[record.marginType]?.color">{{ marginTypeMap[record.marginType]?.label }}</a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="marginStatusMap[record.status]?.color">{{ marginStatusMap[record.status]?.label }}</a-tag>
          </template>
          <template v-else-if="column.key === 'shortfallAmount'">
            <span :style="{ color: record.shortfallAmount > 0 ? '#f5222d' : '#52c41a' }">{{ record.shortfallAmount }}</span>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-space>
              <a @click="showOpModal(record, 'deposit')">缴纳</a>
              <a @click="showOpModal(record, 'call')">追缴</a>
              <a @click="showOpModal(record, 'release')">释放</a>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-drawer title="保证金详情" :open="detailVisible" :width="600" @close="detailVisible = false">
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="保证金编号">{{ detailData.marginNo }}</a-descriptions-item>
        <a-descriptions-item label="客户ID">{{ detailData.customerId }}</a-descriptions-item>
        <a-descriptions-item label="保证金类型">
          <a-tag :color="marginTypeMap[detailData.marginType]?.color">{{ marginTypeMap[detailData.marginType]?.label }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="应缴金额">{{ detailData.requiredAmount }}</a-descriptions-item>
        <a-descriptions-item label="已缴金额">{{ detailData.depositedAmount }}</a-descriptions-item>
        <a-descriptions-item label="缺口金额">{{ detailData.shortfallAmount }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="marginStatusMap[detailData.status]?.color">{{ marginStatusMap[detailData.status]?.label }}</a-tag>
        </a-descriptions-item>
      </a-descriptions>
    </a-drawer>

    <a-modal v-model:open="opVisible" :title="opTitle" :confirm-loading="submitting" @ok="handleOpSubmit">
      <a-form :model="opForm" layout="vertical">
        <a-form-item label="金额" required>
          <a-input-number v-model:value="opForm.amount" style="width: 100%" :min="0" :precision="2" />
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="opForm.remark" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'

const marginTypeMap: Record<string, { label: string; color: string }> = {
  INITIAL: { label: '初始保证金', color: '#1677ff' },
  VARIATION: { label: '变动保证金', color: '#fa8c16' },
  MAINTENANCE: { label: '维持保证金', color: '#52c41a' }
}

const marginStatusMap: Record<string, { label: string; color: string }> = {
  PENDING: { label: '待缴纳', color: '#fa8c16' },
  DEPOSITED: { label: '已缴纳', color: '#52c41a' },
  SHORTFALL: { label: '不足', color: '#f5222d' },
  RELEASED: { label: '已释放', color: '#8c8c8c' }
}

interface MarginRecord {
  marginNo: string
  customerId: number
  marginType: string
  requiredAmount: number
  depositedAmount: number
  shortfallAmount: number
  status: string
}

const columns = [
  { title: '保证金编号', dataIndex: 'marginNo', key: 'marginNo' },
  { title: '客户ID', dataIndex: 'customerId', key: 'customerId' },
  { title: '保证金类型', dataIndex: 'marginType', key: 'marginType' },
  { title: '应缴金额', dataIndex: 'requiredAmount', key: 'requiredAmount' },
  { title: '已缴金额', dataIndex: 'depositedAmount', key: 'depositedAmount' },
  { title: '缺口金额', dataIndex: 'shortfallAmount', key: 'shortfallAmount' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'operation', width: 160 }
]

const mockData: MarginRecord[] = [
  { marginNo: 'MG20240001', customerId: 1001, marginType: 'INITIAL', requiredAmount: 500000, depositedAmount: 500000, shortfallAmount: 0, status: 'DEPOSITED' },
  { marginNo: 'MG20240002', customerId: 1002, marginType: 'VARIATION', requiredAmount: 300000, depositedAmount: 200000, shortfallAmount: 100000, status: 'SHORTFALL' },
  { marginNo: 'MG20240003', customerId: 1003, marginType: 'MAINTENANCE', requiredAmount: 800000, depositedAmount: 0, shortfallAmount: 800000, status: 'PENDING' }
]

const tableData = ref<MarginRecord[]>(mockData)
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: mockData.length })

const queryParams = reactive({ marginType: undefined as string | undefined, status: undefined as string | undefined })

const detailVisible = ref(false)
const detailData = reactive<MarginRecord>({} as MarginRecord)

const opVisible = ref(false)
const opType = ref<'deposit' | 'call' | 'release'>('deposit')
const opTarget = ref<MarginRecord | null>(null)
const submitting = ref(false)
const opForm = reactive({ amount: 0, remark: '' })

const opTitle = computed(() => ({ deposit: '缴纳', call: '追缴', release: '释放' }[opType.value] || '操作'))

function fetchData() { loading.value = false }

function handleSearch() { pagination.current = 1; fetchData() }
function handleReset() { queryParams.marginType = undefined; queryParams.status = undefined; handleSearch() }
function handleTableChange(pg: any) { pagination.current = pg.current; pagination.pageSize = pg.pageSize; fetchData() }

function showDetail(record: MarginRecord) { Object.assign(detailData, record); detailVisible.value = true }

function showOpModal(record: MarginRecord, type: 'deposit' | 'call' | 'release') {
  opType.value = type
  opTarget.value = record
  opForm.amount = 0
  opForm.remark = ''
  opVisible.value = true
}

function handleOpSubmit() {
  message.success(`${opTitle.value}操作成功`)
  opVisible.value = false
}

onMounted(() => fetchData())
</script>

<style scoped>
.margin-list h2 { margin-bottom: 16px; }
.search-card { margin-bottom: 16px; }
</style>
