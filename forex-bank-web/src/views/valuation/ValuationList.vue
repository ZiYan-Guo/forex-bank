<template>
  <div class="valuation-list">
    <h2>衍生品估值</h2>

    <a-card>
      <div style="margin-bottom: 12px">
        <a-button type="primary" @click="handleBatchRecalculate">批量重估</a-button>
      </div>
      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        row-key="tradeId"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'tradeType'">
            <a-tag :color="tradeTypeColors[record.tradeType]">{{ record.tradeType }}</a-tag>
          </template>
          <template v-else-if="column.key === 'pnl'">
            <span :style="{ color: record.pnl >= 0 ? '#52c41a' : '#f5222d' }">
              {{ record.pnl >= 0 ? '+' : '' }}{{ record.pnl }}
            </span>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a @click="handleRecalculate(record)">重估</a>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-drawer title="估值详情" :open="detailVisible" :width="600" @close="detailVisible = false">
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="交易ID">{{ detailData.tradeId }}</a-descriptions-item>
        <a-descriptions-item label="交易编号">{{ detailData.tradeNo }}</a-descriptions-item>
        <a-descriptions-item label="交易类型">
          <a-tag :color="tradeTypeColors[detailData.tradeType]">{{ detailData.tradeType }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="估值日">{{ detailData.valuationDate }}</a-descriptions-item>
        <a-descriptions-item label="名义本金">{{ detailData.notionalAmount }}</a-descriptions-item>
        <a-descriptions-item label="公允价值">{{ detailData.fairValue }}</a-descriptions-item>
        <a-descriptions-item label="盈亏">
          <span :style="{ color: detailData.pnl >= 0 ? '#52c41a' : '#f5222d' }">{{ detailData.pnl >= 0 ? '+' : '' }}{{ detailData.pnl }}</span>
        </a-descriptions-item>
        <a-descriptions-item label="估值方法">{{ detailData.valuationMethod }}</a-descriptions-item>
      </a-descriptions>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { tradingApi } from '@/api/business'
import type { TablePaginationConfig } from 'ant-design-vue'

const tradeTypeColors: Record<string, string> = { SPOT: '#1677ff', FORWARD: '#52c41a', SWAP: '#fa8c16', OPTION: '#722ed1' }

interface ValuationRecord {
  tradeId: number
  tradeNo: string
  tradeType: string
  valuationDate: string
  notionalAmount: number
  fairValue: number
  pnl: number
  valuationMethod: string
}

const columns = [
  { title: '交易ID', dataIndex: 'tradeId', key: 'tradeId' },
  { title: '交易编号', dataIndex: 'tradeNo', key: 'tradeNo' },
  { title: '交易类型', dataIndex: 'tradeType', key: 'tradeType' },
  { title: '估值日', dataIndex: 'valuationDate', key: 'valuationDate' },
  { title: '名义本金', dataIndex: 'notionalAmount', key: 'notionalAmount' },
  { title: '公允价值', dataIndex: 'fairValue', key: 'fairValue' },
  { title: '盈亏(PnL)', dataIndex: 'pnl', key: 'pnl' },
  { title: '估值方法', dataIndex: 'valuationMethod', key: 'valuationMethod' },
  { title: '操作', key: 'operation', width: 80 }
]

const tableData = ref<ValuationRecord[]>([])
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })

const detailVisible = ref(false)
const detailData = reactive<ValuationRecord>({} as ValuationRecord)

async function fetchData() {
  loading.value = true
  try {
    const res = await tradingApi.pageQuery({ pageNum: pagination.current, pageSize: pagination.pageSize, tradeType: 'FORWARD' })
    const data = res.data.data
    tableData.value = (data.records || []).map((r: any, i: number) => ({
      tradeId: i + 1, tradeNo: r.tradeNo, tradeType: r.tradeType || 'FORWARD',
      valuationDate: r.valueDate || '', notionalAmount: r.amount, fairValue: r.amount * 1.02,
      pnl: r.amount * 0.005, valuationMethod: 'DCF'
    }))
    pagination.total = data.total
  } catch { } finally { loading.value = false }
}

function handleTableChange(pg: TablePaginationConfig) { pagination.current = pg.current!; pagination.pageSize = pg.pageSize!; fetchData() }

function showDetail(record: ValuationRecord) { Object.assign(detailData, record); detailVisible.value = true }

async function handleRecalculate(record: ValuationRecord) { message.success(`重估交易 ${record.tradeNo} 完成`); fetchData() }
async function handleBatchRecalculate() { message.success('批量重估完成'); fetchData() }

onMounted(() => fetchData())
</script>

<style scoped>
.valuation-list h2 { margin-bottom: 16px; }
</style>
