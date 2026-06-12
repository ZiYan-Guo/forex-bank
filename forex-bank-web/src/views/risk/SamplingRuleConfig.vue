<template>
  <div class="sampling-rule-config">
    <h2>便利化抽查规则配置</h2>

    <a-card style="margin-top: 16px">
      <div style="margin-bottom: 16px">
        <a-space>
          <a-button type="primary" @click="showRuleModal = true">新增规则 Add Rule</a-button>
          <a-button @click="generateTasks">生成抽查任务 Generate Tasks</a-button>
        </a-space>
      </div>

      <a-table :columns="ruleColumns" :data-source="rules" row-key="id" :pagination="false">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'targetModule'">
            <a-tag :color="moduleColorMap[record.targetModule]">
              {{ moduleNameMap[record.targetModule] || record.targetModule }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'samplingRate'">
            <a-progress :percent="record.samplingRate" :size="16" :stroke-color="rateColor(record.samplingRate)" />
          </template>
          <template v-else-if="column.key === 'status'">
            <a-switch
              :checked="record.status === 'ACTIVE'"
              checked-children="启用"
              un-checked-children="停用"
              @change="(checked: boolean) => toggleRuleStatus(record, checked)"
            />
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-space>
              <a @click="editRule(record)">编辑 Edit</a>
              <a-popconfirm title="确认停用该规则？" @confirm="toggleRuleStatus(record, false)">
                <a style="color: #ff4d4f">停用 Disable</a>
              </a-popconfirm>
              <a-popconfirm title="确认删除该规则？" @confirm="deleteRule(record)">
                <a style="color: #ff4d4f">删除 Delete</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-card title="抽查任务 Sampling Tasks" style="margin-top: 16px" v-if="generatedTasks.length > 0">
      <a-table :columns="taskColumns" :data-source="generatedTasks" row-key="taskId" :pagination="false">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'PENDING' ? 'orange' : 'green'">
              {{ record.status === 'PENDING' ? '待处理 Pending' : '已完成 Completed' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-button v-if="record.status === 'PENDING'" size="small" type="link" @click="completeTask(record)">
              标记完成 Complete
            </a-button>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-card title="抽查统计 Sampling Statistics" style="margin-top: 16px">
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
          <a-statistic title="抽查金额 Total Amount" :value="statistics.totalAmount" />
        </a-col>
      </a-row>
      <a-divider />
      <h4>各模块抽查明细 Per-Module Breakdown</h4>
      <a-table :columns="moduleColumns" :data-source="statistics.moduleBreakdown" row-key="module" :pagination="false" size="small" />
    </a-card>

    <a-modal v-model:open="showRuleModal" title="新增抽查规则 Add Sampling Rule" @ok="addRule" width="640px">
      <a-form layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="规则编码 Rule Code"><a-input v-model:value="newRule.ruleCode" placeholder="SMP_XXX" /></a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="规则名称 Rule Name"><a-input v-model:value="newRule.ruleName" placeholder="规则名称" /></a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="适用模块 Target Module">
          <a-select v-model:value="newRule.targetModule" placeholder="选择模块 Select Module">
            <a-select-option value="FX_EXCHANGE">结售汇 FX Exchange</a-select-option>
            <a-select-option value="FX_PAYMENT">跨境支付 FX Payment</a-select-option>
            <a-select-option value="FX_TRADING">外汇买卖 FX Trading</a-select-option>
            <a-select-option value="FX_SETTLEMENT">国际结算 FX Settlement</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="抽查比例 (%) Sampling Rate">
          <a-input-number v-model:value="newRule.samplingRate" :min="0" :max="100" style="width: 100%" addon-after="%" />
        </a-form-item>
        <a-form-item label="规则条件 (JSON) Condition">
          <a-textarea v-model:value="newRule.conditionJson" :rows="4" placeholder='{"minAmount": 500000, "currency": "USD"}' />
        </a-form-item>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="生效日期 Effective Date">
              <a-date-picker v-model:value="newRule.effectiveDate" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="失效日期 Expiry Date">
              <a-date-picker v-model:value="newRule.expireDate" style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="自动提取样本 Auto Extract">
          <a-switch v-model:checked="newRule.isAutoExtract" checked-children="是" un-checked-children="否" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

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

function rateColor(rate: number): string {
  if (rate >= 80) return '#f5222d'
  if (rate >= 50) return '#fa8c16'
  return '#52c41a'
}

const rules = ref([
  { id: 1, ruleCode: 'SMP_HIGH_AMT', ruleName: '大额交易 High Amount', targetModule: 'FX_PAYMENT', samplingRate: 50, effectiveDate: '2026-01-01', expireDate: '2027-01-01', status: 'ACTIVE', isAutoExtract: true, conditionJson: '{"minAmount":500000,"currency":"USD"}' },
  { id: 2, ruleCode: 'SMP_HIGH_RISK', ruleName: '高风险国家 High Risk Country', targetModule: 'FX_PAYMENT', samplingRate: 100, effectiveDate: '2026-01-01', expireDate: '2027-01-01', status: 'ACTIVE', isAutoExtract: true, conditionJson: '{"countries":["IR","KP","MM"]}' },
  { id: 3, ruleCode: 'SMP_NEW_CUSTOMER', ruleName: '新客户交易 New Customer', targetModule: 'FX_EXCHANGE', samplingRate: 30, effectiveDate: '2026-01-01', expireDate: '2027-01-01', status: 'ACTIVE', isAutoExtract: false, conditionJson: '{"maxAccountAge":30}' },
  { id: 4, ruleCode: 'SMP_FREQUENT', ruleName: '频繁交易 Frequent Tx', targetModule: 'FX_TRADING', samplingRate: 60, effectiveDate: '2026-03-01', expireDate: '2027-03-01', status: 'INACTIVE', isAutoExtract: true, conditionJson: '{"maxDailyCount":5}' },
])

const ruleColumns = [
  { title: '规则编码 Code', dataIndex: 'ruleCode', key: 'ruleCode', width: 140 },
  { title: '规则名称 Name', dataIndex: 'ruleName', key: 'ruleName' },
  { title: '适用模块 Module', key: 'targetModule', width: 120 },
  { title: '抽查比例 Rate', key: 'samplingRate', width: 160 },
  { title: '生效日期 Effective', dataIndex: 'effectiveDate', key: 'effectiveDate', width: 110 },
  { title: '状态 Status', key: 'status', width: 80 },
  { title: '操作 Operations', key: 'operation', width: 180 }
]

const showRuleModal = ref(false)
const newRule = reactive({
  ruleCode: '',
  ruleName: '',
  targetModule: 'FX_PAYMENT',
  samplingRate: 20,
  conditionJson: '',
  effectiveDate: null as any,
  expireDate: null as any,
  isAutoExtract: true
})

function addRule() {
  if (!newRule.ruleCode || !newRule.ruleName) {
    message.warning('请填写规则编码和名称 Fill rule code and name')
    return
  }
  rules.value.push({
    id: rules.value.length + 1,
    ruleCode: newRule.ruleCode,
    ruleName: newRule.ruleName,
    targetModule: newRule.targetModule,
    samplingRate: newRule.samplingRate,
    effectiveDate: newRule.effectiveDate ? newRule.effectiveDate.format('YYYY-MM-DD') : dayjs().format('YYYY-MM-DD'),
    expireDate: newRule.expireDate ? newRule.expireDate.format('YYYY-MM-DD') : '',
    status: 'ACTIVE',
    isAutoExtract: newRule.isAutoExtract,
    conditionJson: newRule.conditionJson
  })
  showRuleModal.value = false
  message.success('规则已添加 Rule added')
  // Reset form 重置表单
  newRule.ruleCode = ''
  newRule.ruleName = ''
  newRule.conditionJson = ''
  newRule.samplingRate = 20
  newRule.targetModule = 'FX_PAYMENT'
  newRule.effectiveDate = null
  newRule.expireDate = null
  newRule.isAutoExtract = true
}

function editRule(record: any) {
  message.info('编辑规则: ' + record.ruleName)
}

function toggleRuleStatus(record: any, checked: boolean) {
  record.status = checked ? 'ACTIVE' : 'INACTIVE'
  message.success(`规则 ${record.ruleCode} 已${checked ? '启用' : '停用'}`)
}

function deleteRule(record: any) {
  rules.value = rules.value.filter(r => r.id !== record.id)
  message.success('规则已删除 Rule deleted')
}

const generatedTasks = ref<any[]>([])

const taskColumns = [
  { title: '任务ID Task ID', dataIndex: 'taskId', key: 'taskId', width: 180 },
  { title: '业务编号 Biz No', dataIndex: 'bizNo', key: 'bizNo' },
  { title: '业务类型 Biz Type', dataIndex: 'bizType', key: 'bizType', width: 120 },
  { title: '客户ID Customer', dataIndex: 'customerId', key: 'customerId' },
  { title: '金额 Amount', dataIndex: 'amount', key: 'amount' },
  { title: '抽查比例 Rate', dataIndex: 'samplingRate', key: 'samplingRate' },
  { title: '原因 Reason', dataIndex: 'reason', key: 'reason' },
  { title: '状态 Status', key: 'status', width: 110 },
  { title: '操作', key: 'operation', width: 100 }
]

function generateTasks() {
  generatedTasks.value = [
    { taskId: 'STK' + Date.now() + '1', bizNo: 'FX2026000001', bizType: 'FX_EXCHANGE', customerId: 1001, amount: 50000, samplingRate: 30, reason: '大额交易 High amount', status: 'PENDING' },
    { taskId: 'STK' + Date.now() + '2', bizNo: 'FX2026000002', bizType: 'FX_EXCHANGE', customerId: 1002, amount: 100000, samplingRate: 40, reason: '大额交易 High amount', status: 'PENDING' },
    { taskId: 'STK' + Date.now() + '3', bizNo: 'FX2026000003', bizType: 'FX_PAYMENT', customerId: 1003, amount: 150000, samplingRate: 50, reason: '高频交易 Frequent tx', status: 'PENDING' },
    { taskId: 'STK' + Date.now() + '4', bizNo: 'FX2026000004', bizType: 'FX_PAYMENT', customerId: 1004, amount: 200000, samplingRate: 60, reason: '高风险国家 High risk', status: 'PENDING' },
    { taskId: 'STK' + Date.now() + '5', bizNo: 'FX2026000005', bizType: 'FX_TRADING', customerId: 1005, amount: 250000, samplingRate: 70, reason: '新客户交易 New customer', status: 'PENDING' },
  ]
  message.success('抽查任务已生成 Tasks generated')
}

function completeTask(record: any) {
  record.status = 'COMPLETED'
  message.success(`任务 ${record.taskId} 已完成 Task completed`)
}

const statistics = reactive({
  last30dCoverageRate: '28.5%',
  totalTransactions: 1520,
  sampledTransactions: 433,
  totalAmount: '¥ 892,450,000.00',
  moduleBreakdown: [
    { module: 'FX_EXCHANGE', moduleName: '结售汇 Exchange', sampledCount: 180, totalCount: 600, rate: '30.0%' },
    { module: 'FX_PAYMENT', moduleName: '跨境支付 Payment', sampledCount: 200, totalCount: 750, rate: '26.7%' },
    { module: 'FX_TRADING', moduleName: '外汇买卖 Trading', sampledCount: 53, totalCount: 170, rate: '31.2%' },
  ]
})

const moduleColumns = [
  { title: '模块 Module', dataIndex: 'moduleName', key: 'moduleName' },
  { title: '抽查笔数 Sampled', dataIndex: 'sampledCount', key: 'sampledCount' },
  { title: '总笔数 Total', dataIndex: 'totalCount', key: 'totalCount' },
  { title: '覆盖率 Rate', dataIndex: 'rate', key: 'rate' },
]
</script>

<style scoped>
.sampling-rule-config h2 { margin-bottom: 16px; }
</style>
