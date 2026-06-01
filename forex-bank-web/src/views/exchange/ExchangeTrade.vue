<template>
  <div class="exchange-trade">
    <h3 class="page-title">结售汇业务</h3>

    <a-row :gutter="16">
      <a-col :span="8">
        <a-card title="实时牌价" size="small">
          <a-row :gutter="[8, 8]">
            <a-col v-for="r in rateStore.rates" :key="r.currencyPair" :span="12">
              <div
                class="rate-card"
                :class="{ selected: selectedPair === r.currencyPair }"
                @click="selectRate(r)"
              >
                <div class="rate-pair-name">{{ r.currencyPair }}</div>
                <div class="rate-value">
                  <span class="rate-ask" :class="r.askRate > r.midRate ? 'up' : 'down'">
                    {{ r.askRate?.toFixed(4) }}
                  </span>
                  <span class="rate-bid">{{ r.bidRate?.toFixed(4) }}</span>
                </div>
                <div class="rate-mid">中: {{ r.midRate?.toFixed(4) }}</div>
              </div>
            </a-col>
          </a-row>
          <a-empty v-if="rateStore.rates.length === 0" description="暂无牌价" style="margin-top:24px" />
        </a-card>
      </a-col>

      <a-col :span="16">
        <a-card title="即期结售汇" size="small">
          <a-form layout="vertical" :model="formState">
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="交易方向">
                  <a-radio-group v-model:value="formState.dealType">
                    <a-radio value="BUY">买入外汇</a-radio>
                    <a-radio value="SELL">卖出外汇</a-radio>
                  </a-radio-group>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="币种对">
                  <a-select
                    v-model:value="formState.currencyPair"
                    placeholder="选择币种对"
                    show-search
                    option-filter-prop="label"
                    @change="onPairChange"
                  >
                    <a-select-option
                      v-for="r in rateStore.rates"
                      :key="r.currencyPair"
                      :value="r.currencyPair"
                      :label="r.currencyPair"
                    >
                      {{ r.currencyPair }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="金额">
                  <a-input-number
                    v-model:value="formState.amount"
                    :min="0"
                    :precision="2"
                    style="width:100%"
                    placeholder="请输入交易金额"
                    @change="calcSettlement"
                  />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="汇率">
                  <a-input-number
                    v-model:value="formState.rate"
                    :min="0"
                    :precision="4"
                    style="width:100%"
                    placeholder="选择牌价自动填入"
                    @change="calcSettlement"
                  />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="交割日">
                  <a-date-picker v-model:value="formState.settlementDate" style="width:100%" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="账户号">
                  <a-input v-model:value="formState.accountNo" placeholder="请输入账户号" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-form-item label="摘要">
              <a-textarea v-model:value="formState.summary" :rows="2" placeholder="请输入交易摘要" />
            </a-form-item>

            <a-form-item>
              <div class="settlement-amount">
                应付金额：
                <span class="amount-value">¥{{ settlementAmount.toFixed(2) }}</span>
              </div>
            </a-form-item>

            <a-divider />

            <a-form-item>
              <a-button type="primary" size="large" :loading="submitting" @click="showConfirmModal">
                确认交易
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
    </a-row>

    <a-card title="交易记录" size="small" style="margin-top:16px">
      <a-table
        :columns="orderColumns"
        :data-source="orderData"
        :loading="tableLoading"
        :pagination="pagination"
        row-key="orderNo"
        size="small"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'orderType'">
            <a-tag :color="record.orderType === 'SPOT' ? 'blue' : 'purple'">
              {{ record.orderType === 'SPOT' ? '即期' : '远期' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'dealType'">
            <a-tag :color="record.dealType === 'BUY' ? 'green' : 'red'">
              {{ record.dealType === 'BUY' ? '买入' : '卖出' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'orderStatus'">
            <a-tag :color="OrderStatusMap[record.orderStatus]?.color">
              {{ OrderStatusMap[record.orderStatus]?.label || record.orderStatus }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="showDetail(record)">查看</a-button>
              <a-button
                v-if="record.orderStatus === 'PENDING'"
                type="link"
                size="small"
                danger
                @click="handleCancel(record)"
              >
                取消
              </a-button>
              <a-button
                v-if="record.orderStatus === 'SUCCESS'"
                type="link"
                size="small"
                @click="handleReverse(record)"
              >
                冲正
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="confirmVisible"
      title="交易确认"
      @ok="handleSubmit"
      @cancel="confirmVisible = false"
      :confirm-loading="submitting"
    >
      <a-descriptions bordered :column="1" size="small">
        <a-descriptions-item label="交易方向">
          {{ formState.dealType === 'BUY' ? '买入外汇' : '卖出外汇' }}
        </a-descriptions-item>
        <a-descriptions-item label="币种对">{{ formState.currencyPair }}</a-descriptions-item>
        <a-descriptions-item label="金额">{{ formState.amount?.toLocaleString() }}</a-descriptions-item>
        <a-descriptions-item label="汇率">{{ formState.rate?.toFixed(4) }}</a-descriptions-item>
        <a-descriptions-item label="应付金额">¥{{ settlementAmount.toFixed(2) }}</a-descriptions-item>
        <a-descriptions-item label="交割日">{{ formState.settlementDate?.format('YYYY-MM-DD') || '-' }}</a-descriptions-item>
        <a-descriptions-item label="账户号">{{ formState.accountNo || '-' }}</a-descriptions-item>
      </a-descriptions>
      <a-checkbox v-model:checked="riskConfirmed" style="margin-top:12px">
        我已确认此交易无风险，并同意提交
      </a-checkbox>
    </a-modal>

    <a-drawer
      v-model:open="detailVisible"
      title="订单详情"
      :width="480"
      @close="detailData = null"
    >
      <a-descriptions v-if="detailData" :column="1" bordered size="small">
        <a-descriptions-item label="订单号">{{ detailData.orderNo }}</a-descriptions-item>
        <a-descriptions-item label="交易类型">
          <a-tag :color="detailData.orderType === 'SPOT' ? 'blue' : 'purple'">
            {{ detailData.orderType === 'SPOT' ? '即期' : '远期' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="交易方向">
          <a-tag :color="detailData.dealType === 'BUY' ? 'green' : 'red'">
            {{ detailData.dealType === 'BUY' ? '买入' : '卖出' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="币种对">{{ detailData.currencyPair }}</a-descriptions-item>
        <a-descriptions-item label="交易金额">{{ detailData.orderAmount }}</a-descriptions-item>
        <a-descriptions-item label="成交汇率">{{ detailData.confirmedRate }}</a-descriptions-item>
        <a-descriptions-item label="订单状态">
          <a-tag :color="OrderStatusMap[detailData.orderStatus]?.color">
            {{ OrderStatusMap[detailData.orderStatus]?.label }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ detailData.createTime }}</a-descriptions-item>
      </a-descriptions>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { message } from 'ant-design-vue'
import type { Dayjs } from 'dayjs'
import { exchangeApi } from '@/api/exchange'
import { useRateStore } from '@/store/rate'
import { OrderStatusMap } from '@/types/api'

const rateStore = useRateStore()

const selectedPair = ref('')

const formState = reactive({
  dealType: 'BUY',
  currencyPair: '',
  amount: null as number | null,
  rate: null as number | null,
  settlementDate: null as Dayjs | null,
  accountNo: '',
  summary: ''
})

const settlementAmount = computed(() => {
  if (formState.amount && formState.rate) {
    return formState.amount * formState.rate
  }
  return 0
})

function calcSettlement() {}

function selectRate(r: { currencyPair: string; askRate: number; midRate: number }) {
  selectedPair.value = r.currencyPair
  formState.currencyPair = r.currencyPair
  formState.rate = r.askRate
  calcSettlement()
}

function onPairChange(pair: string) {
  const rate = rateStore.rates.find(r => r.currencyPair === pair)
  if (rate) {
    selectedPair.value = pair
    formState.rate = rate.askRate
    calcSettlement()
  }
}

const submitting = ref(false)
const confirmVisible = ref(false)
const riskConfirmed = ref(false)

function showConfirmModal() {
  if (!formState.currencyPair) {
    message.warning('请选择币种对')
    return
  }
  if (!formState.amount || formState.amount <= 0) {
    message.warning('请输入有效金额')
    return
  }
  riskConfirmed.value = false
  confirmVisible.value = true
}

async function handleSubmit() {
  if (!riskConfirmed.value) {
    message.warning('请确认风险提示')
    return
  }
  submitting.value = true
  try {
    await exchangeApi.createOrder({
      dealType: formState.dealType,
      currencyPair: formState.currencyPair,
      orderAmount: formState.amount,
      confirmedRate: formState.rate,
      settlementDate: formState.settlementDate?.format('YYYY-MM-DD'),
      accountNo: formState.accountNo,
      summary: formState.summary
    })
    message.success('交易已创建')
    confirmVisible.value = false
    formState.currencyPair = ''
    formState.amount = null
    formState.rate = null
    formState.settlementDate = null
    formState.accountNo = ''
    formState.summary = ''
    selectedPair.value = ''
    fetchOrders()
  } catch {
    message.error('交易创建失败')
  } finally {
    submitting.value = false
  }
}

const orderColumns = [
  { title: '订单号', dataIndex: 'orderNo', key: 'orderNo', width: 160 },
  { title: '类型', dataIndex: 'orderType', key: 'orderType', width: 80 },
  { title: '方向', dataIndex: 'dealType', key: 'dealType', width: 80 },
  { title: '金额', dataIndex: 'orderAmount', key: 'orderAmount', width: 120 },
  { title: '成交汇率', dataIndex: 'confirmedRate', key: 'confirmedRate', width: 100 },
  { title: '状态', dataIndex: 'orderStatus', key: 'orderStatus', width: 90 },
  { title: '时间', dataIndex: 'createTime', key: 'createTime', width: 170 },
  { title: '操作', key: 'action', width: 160, fixed: 'right' as const }
]

const orderData = ref<any[]>([])
const tableLoading = ref(false)
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total: number) => `共 ${total} 条`
})

async function fetchOrders() {
  tableLoading.value = true
  try {
    const res = await exchangeApi.pageQuery({
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    })
    const data = res.data.data
    orderData.value = data.records || []
    pagination.total = data.total || 0
  } catch {
    orderData.value = []
  } finally {
    tableLoading.value = false
  }
}

function handleTableChange(pa: any) {
  pagination.current = pa.current
  pagination.pageSize = pa.pageSize
  fetchOrders()
}

async function handleCancel(record: any) {
  try {
    await exchangeApi.cancel({ orderNo: record.orderNo })
    message.success('订单已取消')
    fetchOrders()
  } catch {
    message.error('取消失败')
  }
}

async function handleReverse(record: any) {
  try {
    await exchangeApi.reverse(record.orderNo)
    message.success('已发起冲正')
    fetchOrders()
  } catch {
    message.error('冲正失败')
  }
}

const detailVisible = ref(false)
const detailData = ref<any>(null)

function showDetail(record: any) {
  detailData.value = record
  detailVisible.value = true
}

onMounted(() => {
  rateStore.fetchRates()
  fetchOrders()
})
onUnmounted(() => {})
</script>

<style scoped>
.exchange-trade {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.page-title {
  margin: 0 0 20px;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.rate-card {
  padding: 8px;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.rate-card:hover {
  border-color: #1677ff;
}

.rate-card.selected {
  border-color: #1677ff;
  background: #f0f5ff;
}

.rate-pair-name {
  font-size: 13px;
  font-weight: bold;
  color: #333;
}

.rate-value {
  display: flex;
  justify-content: space-between;
  font-family: 'Courier New', monospace;
  margin: 4px 0;
}

.rate-ask { font-size: 14px; font-weight: 600; }
.rate-ask.up { color: #f5222d; }
.rate-ask.down { color: #52c41a; }
.rate-bid { font-size: 12px; color: #888; }
.rate-mid { font-size: 11px; color: #aaa; }

.settlement-amount {
  font-size: 16px;
  color: #333;
}

.amount-value {
  font-size: 24px;
  font-weight: bold;
  color: #1677ff;
  font-family: 'Courier New', monospace;
}
</style>
