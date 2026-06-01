<template>
  <div class="trading-list">
    <h2>外汇买卖</h2>

    <a-card class="search-card">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="交易类型">
          <a-select v-model:value="queryParams.tradeType" allow-clear style="width: 140px" @change="handleSearch">
            <a-select-option value="SPOT">即期</a-select-option>
            <a-select-option value="FORWARD">远期</a-select-option>
            <a-select-option value="SWAP">掉期</a-select-option>
            <a-select-option value="OPTION">期权</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="交易状态">
          <a-select v-model:value="queryParams.tradeStatus" allow-clear style="width: 140px" @change="handleSearch">
            <a-select-option v-for="(v, k) in OrderStatusMap" :key="k" :value="k">{{ v.label }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>
      <div style="margin-top: 12px">
        <a-button type="primary" @click="showCreateModal">新建交易</a-button>
      </div>
    </a-card>

    <a-card style="margin-top: 16px">
      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        row-key="tradeNo"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'tradeType'">
            <a-tag :color="tradeTypeColors[record.tradeType]">{{ record.tradeType }}</a-tag>
          </template>
          <template v-else-if="column.key === 'tradeStatus'">
            <a-tag :color="OrderStatusMap[record.tradeStatus]?.color">{{ OrderStatusMap[record.tradeStatus]?.label }}</a-tag>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-space>
              <a @click="showDetail(record)">详情</a>
              <a-dropdown>
                <a class="ant-dropdown-link">操作 <down-outlined /></a>
                <template #overlay>
                  <a-menu @click="({ key }: any) => handleAction(key, record)">
                    <a-menu-item key="confirm">确认</a-menu-item>
                    <a-menu-item key="execute">执行</a-menu-item>
                    <a-menu-item key="settle">交割</a-menu-item>
                    <a-menu-item key="rollover">展期</a-menu-item>
                    <a-menu-item key="close">平仓</a-menu-item>
                    <a-menu-item key="cancel">取消</a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-drawer title="交易详情" :open="detailVisible" :width="600" @close="detailVisible = false">
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="交易编号">{{ detailData.tradeNo }}</a-descriptions-item>
        <a-descriptions-item label="交易类型">
          <a-tag :color="tradeTypeColors[detailData.tradeType]">{{ detailData.tradeType }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="交易方向">{{ detailData.buyCurrency }} → {{ detailData.sellCurrency }}</a-descriptions-item>
        <a-descriptions-item label="交易金额">{{ detailData.amount }}</a-descriptions-item>
        <a-descriptions-item label="汇率">{{ detailData.rate }}</a-descriptions-item>
        <a-descriptions-item label="起息日">{{ detailData.valueDate }}</a-descriptions-item>
        <a-descriptions-item label="交易状态">
          <a-tag :color="OrderStatusMap[detailData.tradeStatus]?.color">{{ OrderStatusMap[detailData.tradeStatus]?.label }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ detailData.createTime }}</a-descriptions-item>
      </a-descriptions>
    </a-drawer>

    <a-modal v-model:open="formVisible" title="新建交易" :confirm-loading="submitting" @ok="handleSubmit">
      <a-form :model="formData" layout="vertical">
        <a-form-item label="交易类型" required>
          <a-select v-model:value="formData.tradeType">
            <a-select-option value="SPOT">即期</a-select-option>
            <a-select-option value="FORWARD">远期</a-select-option>
            <a-select-option value="SWAP">掉期</a-select-option>
            <a-select-option value="OPTION">期权</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="买入币种" required>
          <a-input v-model:value="formData.buyCurrency" />
        </a-form-item>
        <a-form-item label="卖出币种" required>
          <a-input v-model:value="formData.sellCurrency" />
        </a-form-item>
        <a-form-item label="金额" required>
          <a-input-number v-model:value="formData.amount" style="width: 100%" :min="0" />
        </a-form-item>
        <a-form-item label="汇率" required>
          <a-input-number v-model:value="formData.rate" style="width: 100%" :min="0" />
        </a-form-item>
        <a-form-item label="起息日" required>
          <a-date-picker v-model:value="formData.valueDate" style="width: 100%" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { DownOutlined } from '@ant-design/icons-vue'
import { tradingApi } from '@/api/business'
import { OrderStatusMap } from '@/types/api'
import type { TablePaginationConfig } from 'ant-design-vue'

const tradeTypeColors: Record<string, string> = { SPOT: '#1677ff', FORWARD: '#52c41a', SWAP: '#fa8c16', OPTION: '#722ed1' }

interface TradeRecord {
  tradeNo: string
  tradeType: string
  buyCurrency: string
  sellCurrency: string
  amount: number
  rate: number
  valueDate: string
  tradeStatus: string
  createTime: string
}

const columns = [
  { title: '交易编号', dataIndex: 'tradeNo', key: 'tradeNo' },
  { title: '交易类型', dataIndex: 'tradeType', key: 'tradeType' },
  { title: '交易方向', key: 'direction' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '汇率', dataIndex: 'rate', key: 'rate' },
  { title: '起息日', dataIndex: 'valueDate', key: 'valueDate' },
  { title: '状态', dataIndex: 'tradeStatus', key: 'tradeStatus' },
  { title: '操作', key: 'operation', width: 160 }
]

const tableData = ref<TradeRecord[]>([])
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })
const queryParams = reactive({ tradeType: undefined as string | undefined, tradeStatus: undefined as string | undefined })

const detailVisible = ref(false)
const detailData = reactive<TradeRecord>({} as TradeRecord)

const formVisible = ref(false)
const submitting = ref(false)
const formData = reactive({ tradeType: 'SPOT', buyCurrency: '', sellCurrency: '', amount: 0, rate: 0, valueDate: undefined as any })

async function fetchData() {
  loading.value = true
  try {
    const res = await tradingApi.pageQuery({ ...queryParams, pageNum: pagination.current, pageSize: pagination.pageSize })
    const data = res.data.data
    tableData.value = data.records
    pagination.total = data.total
  } catch { /* error handled by interceptor */ } finally {
    loading.value = false
  }
}

function handleSearch() { pagination.current = 1; fetchData() }
function handleReset() { queryParams.tradeType = undefined; queryParams.tradeStatus = undefined; handleSearch() }
function handleTableChange(pg: TablePaginationConfig) { pagination.current = pg.current!; pagination.pageSize = pg.pageSize!; fetchData() }

function showDetail(record: TradeRecord) { Object.assign(detailData, record); detailVisible.value = true }

function showCreateModal() { formVisible.value = true }

async function handleSubmit() {
  submitting.value = true
  try {
    const apiMap: Record<string, any> = { SPOT: tradingApi.createSpot, FORWARD: tradingApi.createForward, SWAP: tradingApi.createSwap, OPTION: tradingApi.createOption }
    await apiMap[formData.tradeType](formData)
    message.success('创建成功')
    formVisible.value = false
    fetchData()
  } catch { /* handled */ } finally { submitting.value = false }
}

async function handleAction(key: string, record: TradeRecord) {
  try {
    if (key === 'confirm') { await tradingApi.confirm(record.tradeNo); message.success('确认成功') }
    else if (key === 'execute') { await tradingApi.execute(record.tradeNo); message.success('执行成功') }
    else if (key === 'settle') { await tradingApi.settle(record.tradeNo); message.success('交割成功') }
    else if (key === 'rollover') { await tradingApi.rollOver({ tradeNo: record.tradeNo }); message.success('展期成功') }
    else if (key === 'close') { await tradingApi.closeOut(record.tradeNo); message.success('平仓成功') }
    else if (key === 'cancel') { await tradingApi.cancel(record.tradeNo, '手动取消'); message.success('取消成功') }
    fetchData()
  } catch { /* handled */ }
}

onMounted(() => fetchData())
</script>

<style scoped>
.trading-list h2 { margin-bottom: 16px; }
.search-card { margin-bottom: 16px; }
</style>
