<template>
  <div class="sampling-rule-config">
    <div class="page-header">
      <h2>便利化抽查规则</h2>
      <a-space>
        <a-button @click="reloadAll">刷新 Refresh</a-button>
        <a-button type="primary" @click="openCreateModal">新增规则 Add Rule</a-button>
        <a-button type="primary" ghost @click="generateTasks">生成抽查任务 Generate Tasks</a-button>
      </a-space>
    </div>

    <a-card>
      <a-table
        :columns="ruleColumns"
        :data-source="rules"
        :loading="ruleLoading"
        row-key="id"
        :pagination="{ pageSize: 8 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'targetModule'">
            <a-tag :color="moduleColorMap[record.targetModule]">
              {{ moduleNameMap[record.targetModule] || record.targetModule }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'samplingRate'">
            <a-progress
              :percent="Number(record.samplingRate || 0)"
              :size="16"
              :stroke-color="rateColor(Number(record.samplingRate || 0))"
            />
          </template>
          <template v-else-if="column.key === 'status'">
            <a-switch
              :checked="record.status === 'ACTIVE'"
              checked-children="启用"
              un-checked-children="停用"
              @change="(checked: boolean) => toggleRuleStatus(record, checked)"
            />
          </template>
          <template v-else-if="column.key === 'isAutoExtract'">
            <a-tag :color="record.isAutoExtract ? 'green' : 'default'">
              {{ record.isAutoExtract ? '自动 Auto' : '手动 Manual' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-space>
              <a @click="openEditModal(record)">编辑 Edit</a>
              <a-popconfirm title="确认删除该规则？" @confirm="deleteRule(record)">
                <a style="color: #ff4d4f">删除 Delete</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-card title="抽查任务 Sampling Tasks" class="section">
      <a-table
        :columns="taskColumns"
        :data-source="tasks"
        :loading="taskLoading"
        row-key="taskId"
        :pagination="{ pageSize: 8 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'amount'">
            {{ formatAmount(record.amount, record.currency) }}
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 'PENDING' ? 'orange' : 'green'">
              {{ record.status === 'PENDING' ? '待处理 Pending' : '已完成 Completed' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'reviewResult'">
            <span>{{ record.reviewResult || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'matchedRules'">
            <a-space wrap>
              <a-tag v-for="rule in record.matchedRules || []" :key="rule" color="blue">{{ rule }}</a-tag>
            </a-space>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-button
              v-if="record.status === 'PENDING'"
              size="small"
              type="link"
              @click="openCompleteModal(record)"
            >
              完成 Complete
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-card title="抽查统计 Sampling Statistics" class="section" :loading="statisticsLoading">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-statistic title="近30天覆盖率 Last 30d" :value="statistics.last30dCoverageRate" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="交易总数 Total Tx" :value="statistics.totalTransactions" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="抽查笔数 Sampled" :value="statistics.sampledTransactions" />
        </a-col>
        <a-col :span="6">
          <a-statistic title="抽查金额 Total Amount" :value="formatAmount(statistics.totalAmount)" />
        </a-col>
      </a-row>
      <a-divider />
      <a-table
        :columns="moduleColumns"
        :data-source="statistics.moduleBreakdown"
        row-key="module"
        :pagination="false"
        size="small"
      />
    </a-card>

    <a-modal
      v-model:open="showRuleModal"
      :title="editingRuleId ? '编辑抽查规则 Edit Sampling Rule' : '新增抽查规则 Add Sampling Rule'"
      @ok="saveRule"
      width="720px"
    >
      <a-form layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="规则编码 Rule Code">
              <a-input v-model:value="ruleForm.ruleCode" placeholder="SMP_HIGH_AMT" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="规则名称 Rule Name">
              <a-input v-model:value="ruleForm.ruleName" placeholder="大额交易 High Amount" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="适用模块 Target Module">
              <a-select v-model:value="ruleForm.targetModule">
                <a-select-option value="FX_EXCHANGE">结售汇 FX Exchange</a-select-option>
                <a-select-option value="FX_PAYMENT">跨境支付 FX Payment</a-select-option>
                <a-select-option value="FX_TRADING">外汇买卖 FX Trading</a-select-option>
                <a-select-option value="FX_SETTLEMENT">国际结算 FX Settlement</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="抽查比例 Sampling Rate">
              <a-input-number
                v-model:value="ruleForm.samplingRate"
                :min="0"
                :max="100"
                style="width: 100%"
                addon-after="%"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="优先级 Priority">
              <a-input-number v-model:value="ruleForm.priority" :min="0" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="生效日期 Effective Date">
              <a-date-picker v-model:value="ruleForm.effectiveDate" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="失效日期 Expiry Date">
              <a-date-picker v-model:value="ruleForm.expireDate" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="规则条件 Condition JSON">
          <a-textarea
            v-model:value="ruleForm.conditionJson"
            :rows="4"
            placeholder='{"minAmount": 500000, "currency": "USD", "countries": ["IR", "KP"]}'
          />
        </a-form-item>
        <a-space>
          <a-switch v-model:checked="ruleForm.isAutoExtract" checked-children="自动" un-checked-children="手动" />
          <span>自动提取样本 Auto Extract</span>
        </a-space>
      </a-form>
    </a-modal>

    <a-modal v-model:open="showCompleteModal" title="完成抽查任务 Complete Task" @ok="completeTask">
      <a-form layout="vertical">
        <a-form-item label="检查结果 Review Result">
          <a-select v-model:value="completeForm.result">
            <a-select-option value="PASS">通过 Pass</a-select-option>
            <a-select-option value="WARNING">关注 Warning</a-select-option>
            <a-select-option value="FAIL">不通过 Fail</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="检查意见 Review Comment">
          <a-textarea v-model:value="completeForm.comment" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import dayjs, { Dayjs } from 'dayjs'
import { riskApi } from '@/api/business'

interface SamplingRule {
  id: number
  ruleCode: string
  ruleName: string
  conditionJson: string
  samplingRate: number
  targetModule: string
  effectiveDate?: string
  expireDate?: string
  priority?: number
  status: string
  isAutoExtract: boolean
}

interface SamplingTask {
  taskId: string
  bizNo: string
  bizType: string
  customerId: number
  amount: number | string
  currency?: string
  countryCode?: string
  accountAgeDays?: number
  samplingRate: number
  reason: string
  matchedRules?: string[]
  status: string
  reviewResult?: string
  reviewComment?: string
  completedAt?: string
}

interface RuleForm {
  ruleCode: string
  ruleName: string
  targetModule: string
  samplingRate: number
  priority: number
  conditionJson: string
  effectiveDate: Dayjs | null
  expireDate: Dayjs | null
  isAutoExtract: boolean
}

const moduleColorMap: Record<string, string> = {
  FX_EXCHANGE: 'blue',
  FX_PAYMENT: 'green',
  FX_TRADING: 'orange',
  FX_SETTLEMENT: 'purple'
}

const moduleNameMap: Record<string, string> = {
  FX_EXCHANGE: '结售汇 Exchange',
  FX_PAYMENT: '跨境支付 Payment',
  FX_TRADING: '外汇买卖 Trading',
  FX_SETTLEMENT: '国际结算 Settlement'
}

const rules = ref<SamplingRule[]>([])
const tasks = ref<SamplingTask[]>([])
const ruleLoading = ref(false)
const taskLoading = ref(false)
const statisticsLoading = ref(false)
const showRuleModal = ref(false)
const showCompleteModal = ref(false)
const editingRuleId = ref<number | null>(null)
const currentTaskId = ref('')

const ruleForm = reactive<RuleForm>({
  ruleCode: '',
  ruleName: '',
  targetModule: 'FX_PAYMENT',
  samplingRate: 20,
  priority: 0,
  conditionJson: '{}',
  effectiveDate: dayjs(),
  expireDate: null,
  isAutoExtract: true
})

const completeForm = reactive({
  result: 'PASS',
  comment: ''
})

const statistics = reactive({
  last30dCoverageRate: '0%',
  totalTransactions: 0,
  sampledTransactions: 0,
  totalAmount: 0,
  moduleBreakdown: [] as any[]
})

const ruleColumns = [
  { title: '规则编码 Code', dataIndex: 'ruleCode', key: 'ruleCode', width: 140 },
  { title: '规则名称 Name', dataIndex: 'ruleName', key: 'ruleName' },
  { title: '适用模块 Module', key: 'targetModule', width: 130 },
  { title: '抽查比例 Rate', key: 'samplingRate', width: 160 },
  { title: '生效日期 Effective', dataIndex: 'effectiveDate', key: 'effectiveDate', width: 120 },
  { title: '自动 Auto', key: 'isAutoExtract', width: 100 },
  { title: '状态 Status', key: 'status', width: 90 },
  { title: '操作 Operations', key: 'operation', width: 150 }
]

const taskColumns = [
  { title: '任务ID Task ID', dataIndex: 'taskId', key: 'taskId', width: 220 },
  { title: '业务编号 Biz No', dataIndex: 'bizNo', key: 'bizNo', width: 160 },
  { title: '业务类型 Biz Type', dataIndex: 'bizType', key: 'bizType', width: 120 },
  { title: '客户ID Customer', dataIndex: 'customerId', key: 'customerId', width: 110 },
  { title: '金额 Amount', key: 'amount', width: 130 },
  { title: '开户天数 Age', dataIndex: 'accountAgeDays', key: 'accountAgeDays', width: 110 },
  { title: '抽查比例 Rate', dataIndex: 'samplingRate', key: 'samplingRate', width: 110 },
  { title: '命中规则 Rules', key: 'matchedRules', width: 180 },
  { title: '原因 Reason', dataIndex: 'reason', key: 'reason' },
  { title: '状态 Status', key: 'status', width: 120 },
  { title: '检查结果 Result', key: 'reviewResult', width: 120 },
  { title: '操作', key: 'operation', width: 100 }
]

const moduleColumns = [
  { title: '模块 Module', dataIndex: 'moduleName', key: 'moduleName' },
  { title: '抽查笔数 Sampled', dataIndex: 'sampledCount', key: 'sampledCount' },
  { title: '总笔数 Total', dataIndex: 'totalCount', key: 'totalCount' },
  { title: '覆盖率 Rate', dataIndex: 'rate', key: 'rate' }
]

function rateColor(rate: number): string {
  if (rate >= 80) return '#f5222d'
  if (rate >= 50) return '#fa8c16'
  return '#52c41a'
}

function formatAmount(amount: number | string, currency = 'CNY'): string {
  const value = Number(amount || 0).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
  return `${currency} ${value}`
}

function resetRuleForm() {
  editingRuleId.value = null
  Object.assign(ruleForm, {
    ruleCode: '',
    ruleName: '',
    targetModule: 'FX_PAYMENT',
    samplingRate: 20,
    priority: 0,
    conditionJson: '{}',
    effectiveDate: dayjs(),
    expireDate: null,
    isAutoExtract: true
  })
}

function toPayload() {
  // Normalize form state into the backend SamplingRule command payload.
  // 将页面表单状态规整为后端 SamplingRule 命令参数。
  return {
    ruleCode: ruleForm.ruleCode,
    ruleName: ruleForm.ruleName,
    targetModule: ruleForm.targetModule,
    samplingRate: ruleForm.samplingRate,
    priority: ruleForm.priority,
    conditionJson: ruleForm.conditionJson || '{}',
    effectiveDate: ruleForm.effectiveDate?.format('YYYY-MM-DD'),
    expireDate: ruleForm.expireDate?.format('YYYY-MM-DD') || '',
    isAutoExtract: ruleForm.isAutoExtract
  }
}

async function loadRules() {
  ruleLoading.value = true
  try {
    console.info('[SamplingRule] loading rules / 正在加载抽查规则')
    const res = await riskApi.listSamplingRules()
    rules.value = res.data.data || []
  } finally {
    ruleLoading.value = false
  }
}

async function loadTasks() {
  taskLoading.value = true
  try {
    console.info('[SamplingRule] loading persisted tasks / 正在加载已持久化抽查任务')
    const res = await riskApi.listSamplingTasks()
    tasks.value = res.data.data?.tasks || []
  } finally {
    taskLoading.value = false
  }
}

async function loadStatistics() {
  statisticsLoading.value = true
  try {
    console.info('[SamplingRule] loading statistics / 正在加载抽查统计')
    const res = await riskApi.getSamplingStatistics()
    Object.assign(statistics, res.data.data || {})
  } finally {
    statisticsLoading.value = false
  }
}

async function reloadAll() {
  // Refresh rules, generated tasks, and statistics together to keep the dashboard consistent.
  // 同步刷新规则、抽查任务和统计数据，保证页面看板口径一致。
  await Promise.all([loadRules(), loadTasks(), loadStatistics()])
}

function openCreateModal() {
  resetRuleForm()
  showRuleModal.value = true
}

function openEditModal(record: SamplingRule) {
  editingRuleId.value = record.id
  Object.assign(ruleForm, {
    ruleCode: record.ruleCode,
    ruleName: record.ruleName,
    targetModule: record.targetModule,
    samplingRate: Number(record.samplingRate || 0),
    priority: record.priority || 0,
    conditionJson: record.conditionJson || '{}',
    effectiveDate: record.effectiveDate ? dayjs(record.effectiveDate) : null,
    expireDate: record.expireDate ? dayjs(record.expireDate) : null,
    isAutoExtract: record.isAutoExtract
  })
  showRuleModal.value = true
}

async function saveRule() {
  if (!ruleForm.ruleCode || !ruleForm.ruleName) {
    message.warning('请填写规则编码和名称 Fill rule code and name')
    return
  }
  try {
    JSON.parse(ruleForm.conditionJson || '{}')
  } catch {
    message.error('规则条件不是合法 JSON / Condition JSON is invalid')
    return
  }
  const payload = toPayload()
  if (editingRuleId.value) {
    await riskApi.updateSamplingRule(editingRuleId.value, payload)
    message.success('规则已更新 Rule updated')
  } else {
    await riskApi.createSamplingRule(payload)
    message.success('规则已添加 Rule added')
  }
  showRuleModal.value = false
  await loadRules()
}

async function toggleRuleStatus(record: SamplingRule, checked: boolean) {
  const status = checked ? 'ACTIVE' : 'INACTIVE'
  await riskApi.updateSamplingRuleStatus(record.id, status)
  message.success(`规则 ${record.ruleCode} 已${checked ? '启用' : '停用'}`)
  await loadRules()
}

async function deleteRule(record: SamplingRule) {
  await riskApi.deleteSamplingRule(record.id)
  message.success('规则已删除 Rule deleted')
  await loadRules()
}

async function generateTasks() {
  const today = dayjs().format('YYYY-MM-DD')
  console.info('[SamplingRule] generating and persisting tasks / 正在生成并持久化抽查任务', today)
  const res = await riskApi.generateSamplingTasks({ date: today })
  tasks.value = res.data.data?.tasks || []
  message.success(`已生成 ${res.data.data?.count || 0} 条抽查任务`)
  await loadStatistics()
}

function openCompleteModal(record: SamplingTask) {
  currentTaskId.value = record.taskId
  completeForm.result = 'PASS'
  completeForm.comment = ''
  showCompleteModal.value = true
}

async function completeTask() {
  await riskApi.completeSamplingTask(currentTaskId.value, completeForm)
  message.success(`任务 ${currentTaskId.value} 已完成 Task completed`)
  showCompleteModal.value = false
  await Promise.all([loadTasks(), loadStatistics()])
}

onMounted(reloadAll)
</script>

<style scoped>
.sampling-rule-config {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-header h2 {
  margin: 0;
}

.section {
  margin-top: 16px;
}
</style>
