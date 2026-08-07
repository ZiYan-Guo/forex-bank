<template>
  <div class="margin-list">
    <h2>押品与保证金管理</h2>

    <a-row :gutter="16" class="summary-row">
      <a-col :span="6">
        <a-statistic title="押品总价值" :value="ledgerSummary.totalCollateralValue" :precision="2" />
      </a-col>
      <a-col :span="6">
        <a-statistic title="现金押品余额" :value="ledgerSummary.cashCollateralBalance" :precision="2" />
      </a-col>
      <a-col :span="6">
        <a-statistic title="债券押品余额" :value="ledgerSummary.bondCollateralBalance" :precision="2" />
      </a-col>
      <a-col :span="6">
        <a-statistic title="缺口/在途金额" :value="ledgerSummary.totalShortfallAmount" :precision="2" />
      </a-col>
    </a-row>

    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="ledger" tab="保证金台账">
        <a-card class="search-card">
          <a-form layout="inline" :model="queryParams">
            <a-form-item label="保证金类型">
              <a-select v-model:value="queryParams.marginType" allow-clear style="width: 140px" @change="handleSearch">
                <a-select-option value="INITIAL">初始保证金</a-select-option>
                <a-select-option value="VARIATION">变动保证金</a-select-option>
                <a-select-option value="ADDITIONAL">追加保证金</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="状态">
              <a-select v-model:value="queryParams.status" allow-clear style="width: 140px" @change="handleSearch">
                <a-select-option value="PENDING">待缴</a-select-option>
                <a-select-option value="PAID">已缴</a-select-option>
                <a-select-option value="PARTIAL">部分缴</a-select-option>
                <a-select-option value="CALLED">已追缴</a-select-option>
                <a-select-option value="SUFFICIENT">足额</a-select-option>
                <a-select-option value="RELEASED">已释放</a-select-option>
                <a-select-option value="CANCELLED">已取消</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item>
              <a-space>
                <a-button type="primary" @click="handleSearch">查询</a-button>
                <a-button @click="handleReset">重置</a-button>
              </a-space>
            </a-form-item>
          </a-form>
        </a-card>

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
              <a-tag :color="marginTypeMap[record.marginType]?.color">{{ marginTypeMap[record.marginType]?.label || record.marginType }}</a-tag>
            </template>
            <template v-else-if="column.key === 'status'">
              <a-tag :color="marginStatusMap[record.status]?.color">{{ marginStatusMap[record.status]?.label || record.status }}</a-tag>
            </template>
            <template v-else-if="column.key === 'shortfallAmount'">
              <span :class="{ danger: Number(record.shortfallAmount) > 0 }">{{ formatAmount(record.shortfallAmount) }}</span>
            </template>
            <template v-else-if="column.key === 'collateralValue'">
              {{ formatAmount(record.collateralValue || record.depositedAmount) }}
            </template>
            <template v-else-if="column.key === 'operation'">
              <a-space>
                <a @click="showDetail(record)">详情</a>
                <a @click="showOpModal(record, 'deposit')">缴纳</a>
                <a @click="showOpModal(record, 'call')">追缴</a>
                <a @click="showOpModal(record, 'release')">释放</a>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-tab-pane>

      <a-tab-pane key="calculate" tab="计算引擎">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-card title="变动保证金 VM">
              <a-form :model="vmForm" layout="vertical">
                <a-form-item label="敞口金额">
                  <a-input-number v-model:value="vmForm.exposureAmount" class="full" :precision="2" />
                </a-form-item>
                <a-form-item label="我方门槛值 / 对手方门槛值">
                  <a-space compact class="full">
                    <a-input-number v-model:value="vmForm.ourThresholdAmount" class="half" :min="0" :precision="2" />
                    <a-input-number v-model:value="vmForm.counterpartyThresholdAmount" class="half" :min="0" :precision="2" />
                  </a-space>
                </a-form-item>
                <a-form-item label="最小转让金额">
                  <a-input-number v-model:value="vmForm.minimumTransferAmount" class="full" :min="0" :precision="2" />
                </a-form-item>
                <a-form-item label="账户余额 / 在途金额">
                  <a-space compact class="full">
                    <a-input-number v-model:value="vmForm.accountBalance" class="half" :precision="2" />
                    <a-input-number v-model:value="vmForm.inTransitAmount" class="half" :precision="2" />
                  </a-space>
                </a-form-item>
                <a-button type="primary" :loading="calculatingVm" @click="handleVmCalculate">计算 VM</a-button>
              </a-form>
              <a-descriptions v-if="vmResult" class="result-box" :column="1" bordered size="small">
                <a-descriptions-item label="交收净额">{{ formatAmount(vmResult.netSettlementAmount) }}</a-descriptions-item>
                <a-descriptions-item label="Delivery">{{ formatAmount(vmResult.deliveryAmount) }}</a-descriptions-item>
                <a-descriptions-item label="Return">{{ formatAmount(vmResult.returnAmount) }}</a-descriptions-item>
                <a-descriptions-item label="动作">{{ vmResult.action }}</a-descriptions-item>
                <a-descriptions-item label="规则">{{ vmResult.ruleRemark }}</a-descriptions-item>
              </a-descriptions>
            </a-card>
          </a-col>

          <a-col :span="12">
            <a-card title="初始保证金 IM 标准法">
              <a-space direction="vertical" class="full">
                <a-row v-for="(trade, index) in imForm.trades" :key="index" :gutter="8">
                  <a-col :span="7">
                    <a-select v-model:value="trade.assetClass" class="full">
                      <a-select-option value="FX">外汇</a-select-option>
                      <a-select-option value="INTEREST_RATE">利率</a-select-option>
                      <a-select-option value="CREDIT">信用</a-select-option>
                      <a-select-option value="COMMODITY">商品/贵金属</a-select-option>
                      <a-select-option value="EQUITY">股票</a-select-option>
                      <a-select-option value="OTHER">其他</a-select-option>
                    </a-select>
                  </a-col>
                  <a-col :span="5">
                    <a-input-number v-model:value="trade.tenorYears" class="full" :min="0" :precision="2" placeholder="期限" />
                  </a-col>
                  <a-col :span="6">
                    <a-input-number v-model:value="trade.notionalAmount" class="full" :min="0" :precision="2" placeholder="名义本金" />
                  </a-col>
                  <a-col :span="5">
                    <a-input-number v-model:value="trade.marketValue" class="full" :precision="2" placeholder="MTM" />
                  </a-col>
                  <a-col :span="1">
                    <a-button danger type="link" @click="removeImTrade(index)">删</a-button>
                  </a-col>
                </a-row>
                <a-space>
                  <a-button @click="addImTrade">新增交易</a-button>
                  <a-button type="primary" :loading="calculatingIm" @click="handleImCalculate">计算 IM</a-button>
                </a-space>
              </a-space>
              <a-descriptions v-if="imResult" class="result-box" :column="1" bordered size="small">
                <a-descriptions-item label="初始保证金总额">{{ formatAmount(imResult.grossInitialMargin) }}</a-descriptions-item>
                <a-descriptions-item label="NGR">{{ imResult.ngr }}</a-descriptions-item>
                <a-descriptions-item label="标准化 IM 净额">{{ formatAmount(imResult.standardizedInitialMargin) }}</a-descriptions-item>
              </a-descriptions>
            </a-card>
          </a-col>
        </a-row>
      </a-tab-pane>

      <a-tab-pane key="collateral" tab="押品估值">
        <a-card title="现金/债券押品估值">
          <a-form :model="collateralForm" layout="inline">
            <a-form-item label="类型">
              <a-select v-model:value="collateralForm.collateralType" style="width: 120px">
                <a-select-option value="CASH">现金</a-select-option>
                <a-select-option value="BOND">债券</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="币种">
              <a-input v-model:value="collateralForm.currency" style="width: 100px" />
            </a-form-item>
            <a-form-item label="估值金额">
              <a-input-number v-model:value="collateralForm.marketValue" :min="0" :precision="2" />
            </a-form-item>
            <a-form-item label="汇率">
              <a-input-number v-model:value="collateralForm.fxRate" :min="0" :precision="6" />
            </a-form-item>
            <a-form-item label="haircut%">
              <a-input-number v-model:value="collateralForm.haircutPct" :min="0" :max="99.99" :precision="2" />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" :loading="valuingCollateral" @click="handleCollateralValue">估值</a-button>
            </a-form-item>
          </a-form>
          <a-descriptions v-if="collateralResult" class="result-box" :column="2" bordered size="small">
            <a-descriptions-item label="折算基础价值">{{ formatAmount(collateralResult.convertedValue) }}</a-descriptions-item>
            <a-descriptions-item label="押品价值">{{ formatAmount(collateralResult.collateralValue) }}</a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-tab-pane>
    </a-tabs>

    <a-drawer title="保证金详情" :open="detailVisible" :width="640" @close="detailVisible = false">
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="保证金编号">{{ detailData.marginNo }}</a-descriptions-item>
        <a-descriptions-item label="客户ID">{{ detailData.customerId }}</a-descriptions-item>
        <a-descriptions-item label="交易ID">{{ detailData.tradeId }}</a-descriptions-item>
        <a-descriptions-item label="保证金类型">{{ marginTypeMap[detailData.marginType]?.label || detailData.marginType }}</a-descriptions-item>
        <a-descriptions-item label="应缴金额">{{ formatAmount(detailData.requiredAmount) }}</a-descriptions-item>
        <a-descriptions-item label="已缴金额">{{ formatAmount(detailData.depositedAmount) }}</a-descriptions-item>
        <a-descriptions-item label="缺口金额">{{ formatAmount(detailData.shortfallAmount) }}</a-descriptions-item>
        <a-descriptions-item label="押品类型">{{ detailData.collateralType }}</a-descriptions-item>
        <a-descriptions-item label="押品价值">{{ formatAmount(detailData.collateralValue || detailData.depositedAmount) }}</a-descriptions-item>
        <a-descriptions-item label="水位线">{{ detailData.waterLevel || '-' }}</a-descriptions-item>
        <a-descriptions-item label="状态">{{ marginStatusMap[detailData.status]?.label || detailData.status }}</a-descriptions-item>
      </a-descriptions>
    </a-drawer>

    <a-modal v-model:open="opVisible" :title="opTitle" :confirm-loading="submitting" @ok="handleOpSubmit">
      <a-form :model="opForm" layout="vertical">
        <a-form-item label="金额" required>
          <a-input-number v-model:value="opForm.amount" class="full" :min="0" :precision="2" />
        </a-form-item>
        <a-form-item label="原因/备注">
          <a-textarea v-model:value="opForm.remark" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { marginApi } from '@/api/business'

const activeTab = ref('ledger')

const marginTypeMap: Record<string, { label: string; color: string }> = {
  INITIAL: { label: '初始保证金', color: '#1677ff' },
  VARIATION: { label: '变动保证金', color: '#fa8c16' },
  ADDITIONAL: { label: '追加保证金', color: '#722ed1' }
}

const marginStatusMap: Record<string, { label: string; color: string }> = {
  PENDING: { label: '待缴', color: '#fa8c16' },
  PAID: { label: '已缴', color: '#52c41a' },
  PARTIAL: { label: '部分缴', color: '#faad14' },
  CALLED: { label: '已追缴', color: '#f5222d' },
  SUFFICIENT: { label: '足额', color: '#52c41a' },
  RELEASED: { label: '已释放', color: '#8c8c8c' },
  CANCELLED: { label: '已取消', color: '#8c8c8c' }
}

interface MarginRecord {
  id?: number
  marginNo: string
  customerId: number
  tradeId?: number
  marginType: string
  marginCurrency?: string
  requiredAmount: number
  depositedAmount: number
  shortfallAmount: number
  collateralType?: string
  collateralValue?: number
  waterLevel?: string
  status: string
}

const columns = [
  { title: '保证金编号', dataIndex: 'marginNo', key: 'marginNo' },
  { title: '客户ID', dataIndex: 'customerId', key: 'customerId' },
  { title: '保证金类型', dataIndex: 'marginType', key: 'marginType' },
  { title: '应缴金额', dataIndex: 'requiredAmount', key: 'requiredAmount' },
  { title: '已缴金额', dataIndex: 'depositedAmount', key: 'depositedAmount' },
  { title: '押品价值', dataIndex: 'collateralValue', key: 'collateralValue' },
  { title: '缺口金额', dataIndex: 'shortfallAmount', key: 'shortfallAmount' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'operation', width: 210 }
]

const tableData = ref<MarginRecord[]>([])
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })
const queryParams = reactive({ marginType: undefined as string | undefined, status: undefined as string | undefined })
const ledgerSummary = reactive({
  vmReceivedBalance: 0,
  vmPostedBalance: 0,
  imPledgeeBalance: 0,
  imPledgorBalance: 0,
  cashCollateralBalance: 0,
  bondCollateralBalance: 0,
  inTransitAmount: 0,
  totalCollateralValue: 0,
  totalShortfallAmount: 0
})

const detailVisible = ref(false)
const detailData = reactive<MarginRecord>({} as MarginRecord)
const opVisible = ref(false)
const opType = ref<'deposit' | 'call' | 'release'>('deposit')
const opTarget = ref<MarginRecord | null>(null)
const submitting = ref(false)
const opForm = reactive({ amount: 0, remark: '' })
const opTitle = computed(() => ({ deposit: '缴纳保证金', call: '发起追缴', release: '释放保证金' }[opType.value]))

const vmForm = reactive({
  exposureAmount: 1200000,
  ourThresholdAmount: 200000,
  counterpartyThresholdAmount: 200000,
  minimumTransferAmount: 50000,
  accountBalance: 300000,
  inTransitAmount: 0
})
const vmResult = ref<any>(null)
const calculatingVm = ref(false)

const imForm = reactive({
  trades: [
    { assetClass: 'FX', tenorYears: 1, notionalAmount: 10000000, marketValue: 120000 },
    { assetClass: 'INTEREST_RATE', tenorYears: 4, notionalAmount: 5000000, marketValue: -30000 }
  ]
})
const imResult = ref<any>(null)
const calculatingIm = ref(false)

const collateralForm = reactive({ collateralType: 'BOND', currency: 'CNY', marketValue: 1000000, fxRate: 1, haircutPct: 2 })
const collateralResult = ref<any>(null)
const valuingCollateral = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const res = await marginApi.pageQuery({
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
      marginType: queryParams.marginType,
      status: queryParams.status
    })
    const page = res.data.data
    tableData.value = page.records || []
    pagination.total = page.total || 0
  } finally {
    loading.value = false
  }
}

async function fetchLedgerSummary() {
  const res = await marginApi.ledgerSummary()
  Object.assign(ledgerSummary, res.data.data || {})
}

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

async function handleOpSubmit() {
  if (!opTarget.value || !opForm.amount) return
  submitting.value = true
  try {
    if (opType.value === 'deposit') await marginApi.deposit(opTarget.value.marginNo, opForm.amount)
    if (opType.value === 'call') await marginApi.call(opTarget.value.marginNo, opForm.amount)
    if (opType.value === 'release') await marginApi.release(opTarget.value.marginNo, opForm.amount, opForm.remark || '手动释放')
    message.success(`${opTitle.value}成功`)
    opVisible.value = false
    await Promise.all([fetchData(), fetchLedgerSummary()])
  } finally {
    submitting.value = false
  }
}

async function handleVmCalculate() {
  calculatingVm.value = true
  try {
    const res = await marginApi.calculateVm(vmForm)
    vmResult.value = res.data.data
  } finally {
    calculatingVm.value = false
  }
}

function addImTrade() {
  imForm.trades.push({ assetClass: 'FX', tenorYears: 1, notionalAmount: 0, marketValue: 0 })
}

function removeImTrade(index: number) {
  if (imForm.trades.length > 1) imForm.trades.splice(index, 1)
}

async function handleImCalculate() {
  calculatingIm.value = true
  try {
    const res = await marginApi.calculateImStandard(imForm)
    imResult.value = res.data.data
  } finally {
    calculatingIm.value = false
  }
}

async function handleCollateralValue() {
  valuingCollateral.value = true
  try {
    const res = await marginApi.valueCollateral(collateralForm)
    collateralResult.value = res.data.data
  } finally {
    valuingCollateral.value = false
  }
}

function formatAmount(value?: number | string) {
  return Number(value || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

onMounted(() => {
  fetchData()
  fetchLedgerSummary()
  handleVmCalculate()
  handleImCalculate()
  handleCollateralValue()
})
</script>

<style scoped>
.margin-list h2 { margin-bottom: 16px; }
.summary-row { margin-bottom: 16px; }
.search-card { margin-bottom: 16px; }
.full { width: 100%; }
.half { width: 50%; }
.result-box { margin-top: 16px; }
.danger { color: #f5222d; font-weight: 600; }
</style>
