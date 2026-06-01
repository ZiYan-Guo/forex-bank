<template>
  <div class="journal-entry">
    <h2>簿记核算</h2>

    <a-card class="search-card">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="会计期间">
          <a-range-picker v-model:value="queryParams.fiscalPeriod" @change="handleSearch" />
        </a-form-item>
        <a-form-item label="业务类型">
          <a-select v-model:value="queryParams.bizType" allow-clear style="width: 120px" @change="handleSearch">
            <a-select-option value="TRADING">外汇买卖</a-select-option>
            <a-select-option value="PAYMENT">跨境支付</a-select-option>
            <a-select-option value="EXCHANGE">结售汇</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>
      <div style="margin-top: 12px">
        <a-button type="primary" @click="handleDayEndSettlement">日终结算</a-button>
      </div>
    </a-card>

    <a-card style="margin-top: 16px">
      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        row-key="voucherNo"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'entryDirection'">
            <a-tag :color="record.entryDirection === 'DEBIT' ? '#f5222d' : '#52c41a'">
              {{ record.entryDirection === 'DEBIT' ? '借' : '贷' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'entryStatus'">
            <a-tag :color="entryStatusMap[record.entryStatus]?.color">{{ entryStatusMap[record.entryStatus]?.label }}</a-tag>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-space>
              <a @click="handlePost(record)">过账</a>
              <a @click="handleReverse(record)">冲销</a>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-drawer title="凭证详情" :open="detailVisible" :width="600" @close="detailVisible = false">
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="凭证号">{{ detailData.voucherNo }}</a-descriptions-item>
        <a-descriptions-item label="凭证日期">{{ detailData.voucherDate }}</a-descriptions-item>
        <a-descriptions-item label="会计期间">{{ detailData.fiscalPeriod }}</a-descriptions-item>
        <a-descriptions-item label="业务类型">{{ detailData.bizType }}</a-descriptions-item>
        <a-descriptions-item label="科目代码">{{ detailData.accountCode }}</a-descriptions-item>
        <a-descriptions-item label="金额">{{ detailData.amount }}</a-descriptions-item>
        <a-descriptions-item label="借贷方向">
          <a-tag :color="detailData.entryDirection === 'DEBIT' ? '#f5222d' : '#52c41a'">
            {{ detailData.entryDirection === 'DEBIT' ? '借方' : '贷方' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="entryStatusMap[detailData.entryStatus]?.color">{{ entryStatusMap[detailData.entryStatus]?.label }}</a-tag>
        </a-descriptions-item>
      </a-descriptions>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { TablePaginationConfig } from 'ant-design-vue'

const entryStatusMap: Record<string, { label: string; color: string }> = {
  DRAFT: { label: '草稿', color: '#8c8c8c' },
  POSTED: { label: '已过账', color: '#52c41a' },
  REVERSED: { label: '已冲销', color: '#f5222d' }
}

interface JournalRecord {
  voucherNo: string
  voucherDate: string
  fiscalPeriod: string
  bizType: string
  accountCode: string
  amount: number
  entryDirection: string
  entryStatus: string
}

const columns = [
  { title: '凭证号', dataIndex: 'voucherNo', key: 'voucherNo' },
  { title: '凭证日期', dataIndex: 'voucherDate', key: 'voucherDate' },
  { title: '会计期间', dataIndex: 'fiscalPeriod', key: 'fiscalPeriod' },
  { title: '业务类型', dataIndex: 'bizType', key: 'bizType' },
  { title: '科目代码', dataIndex: 'accountCode', key: 'accountCode' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '借贷', dataIndex: 'entryDirection', key: 'entryDirection' },
  { title: '状态', dataIndex: 'entryStatus', key: 'entryStatus' },
  { title: '操作', key: 'operation', width: 120 }
]

const mockData: JournalRecord[] = [
  { voucherNo: 'JV20240001', voucherDate: '2024-01-15', fiscalPeriod: '2024-01', bizType: 'TRADING', accountCode: '100201', amount: 500000, entryDirection: 'DEBIT', entryStatus: 'POSTED' },
  { voucherNo: 'JV20240002', voucherDate: '2024-01-15', fiscalPeriod: '2024-01', bizType: 'TRADING', accountCode: '100202', amount: 500000, entryDirection: 'CREDIT', entryStatus: 'POSTED' },
  { voucherNo: 'JV20240003', voucherDate: '2024-01-16', fiscalPeriod: '2024-01', bizType: 'PAYMENT', accountCode: '200101', amount: 300000, entryDirection: 'DEBIT', entryStatus: 'DRAFT' }
]

const tableData = ref<JournalRecord[]>(mockData)
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: mockData.length })

const queryParams = reactive({ fiscalPeriod: undefined as any, bizType: undefined as string | undefined })

const detailVisible = ref(false)
const detailData = reactive<JournalRecord>({} as JournalRecord)

function fetchData() { loading.value = false }

function handleSearch() { fetchData() }
function handleReset() { queryParams.fiscalPeriod = undefined; queryParams.bizType = undefined; handleSearch() }
function handleTableChange(pg: TablePaginationConfig) { pagination.current = pg.current!; pagination.pageSize = pg.pageSize!; fetchData() }

function showDetail(record: JournalRecord) { Object.assign(detailData, record); detailVisible.value = true }

function handlePost(record: JournalRecord) { message.success(`凭证 ${record.voucherNo} 已过账`) }
function handleReverse(record: JournalRecord) { message.success(`凭证 ${record.voucherNo} 已冲销`) }
function handleDayEndSettlement() { message.success('日终结算完成') }

onMounted(() => fetchData())
</script>

<style scoped>
.journal-entry h2 { margin-bottom: 16px; }
.search-card { margin-bottom: 16px; }
</style>
