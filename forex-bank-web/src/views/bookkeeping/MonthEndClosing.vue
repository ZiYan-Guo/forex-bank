<template>
  <div class="month-end-closing">
    <h2>月末结账</h2>

    <a-steps :current="currentStep" style="margin-bottom: 24px">
      <a-step title="未过账分录检查" />
      <a-step title="外币重估" />
      <a-step title="科目余额核对" />
      <a-step title="确认结账" />
    </a-steps>

    <a-card v-if="currentStep === 0">
      <template #title>未过账分录检查</template>
      <a-alert :message="`当前未过账分录：${draftCount} 笔`" :type="draftCount > 0 ? 'warning' : 'success'" show-icon style="margin-bottom: 16px" />
      <a-table :columns="draftColumns" :data-source="draftData" :pagination="false" row-key="voucherNo" size="small" />
      <div style="margin-top: 16px">
        <a-button type="primary" :disabled="draftCount === 0" @click="handleBatchPost">批量过账</a-button>
        <a-button style="margin-left: 12px" @click="nextStep">跳过</a-button>
      </div>
    </a-card>

    <a-card v-if="currentStep === 1">
      <template #title>外币重估</template>
      <a-table
        :columns="revalColumns"
        :data-source="revalData"
        :pagination="false"
        row-key="currency"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'newRate'">
            <a-input-number v-model:value="record.newRate" :step="0.0001" style="width: 120px" size="small" />
          </template>
          <template v-else-if="column.key === 'revalDiff'">
            <span :style="{ color: record.revalDiff >= 0 ? '#52c41a' : '#f5222d' }">
              {{ record.revalDiff >= 0 ? '+' : '' }}{{ record.revalDiff }}
            </span>
          </template>
        </template>
      </a-table>
      <div style="margin-top: 16px">
        <a-button type="primary" @click="handleGenerateRevalEntry">生成重估分录</a-button>
        <a-button style="margin-left: 12px" @click="nextStep">下一步</a-button>
      </div>
    </a-card>

    <a-card v-if="currentStep === 2">
      <template #title>科目余额核对</template>
      <a-table :columns="trialColumns" :data-source="trialData" :pagination="false" row-key="accountCode" size="small" />
      <a-divider />
      <a-row :gutter="16">
        <a-col :span="8">
          <a-statistic title="借方合计" :value="trialTotal.debit" :value-style="{ color: '#f5222d' }" prefix="￥" />
        </a-col>
        <a-col :span="8">
          <a-statistic title="贷方合计" :value="trialTotal.credit" :value-style="{ color: '#52c41a' }" prefix="￥" />
        </a-col>
        <a-col :span="8">
          <a-statistic
            title="借贷差"
            :value="trialTotal.debit - trialTotal.credit"
            :value-style="{ color: trialTotal.debit === trialTotal.credit ? '#52c41a' : '#f5222d' }"
            prefix="￥"
          />
        </a-col>
      </a-row>
      <div style="margin-top: 16px">
        <a-button type="primary" :disabled="trialTotal.debit !== trialTotal.credit" @click="nextStep">下一步</a-button>
      </div>
    </a-card>

    <a-card v-if="currentStep === 3">
      <template #title>确认结账</template>
      <a-descriptions :column="2" bordered size="small">
        <a-descriptions-item label="会计期间">{{ closingInfo.period }}</a-descriptions-item>
        <a-descriptions-item label="结账状态">待确认</a-descriptions-item>
        <a-descriptions-item label="已过账分录">{{ closingInfo.postedCount }} 笔</a-descriptions-item>
        <a-descriptions-item label="重估分录">{{ closingInfo.revalCount }} 笔</a-descriptions-item>
        <a-descriptions-item label="借方合计">￥{{ trialTotal.debit.toLocaleString() }}</a-descriptions-item>
        <a-descriptions-item label="贷方合计">￥{{ trialTotal.credit.toLocaleString() }}</a-descriptions-item>
        <a-descriptions-item label="操作人">Admin</a-descriptions-item>
        <a-descriptions-item label="结账时间">{{ new Date().toLocaleString() }}</a-descriptions-item>
      </a-descriptions>
      <div style="margin-top: 16px">
        <a-button type="primary" danger @click="showConfirm = true">执行月末结账</a-button>
        <a-button style="margin-left: 12px" @click="currentStep = 0">返回第一步</a-button>
      </div>
    </a-card>

    <a-modal v-model:open="showConfirm" title="确认结账" @ok="handleClosing" @cancel="showConfirm = false">
      <p>确认执行 {{ closingInfo.period }} 的月末结账操作？</p>
      <p style="color: #fa8c16">结账后该期间将锁定，无法修改凭证。</p>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'

interface DraftEntry { voucherNo: string; voucherDate: string; accountCode: string; amount: number; direction: string }

interface RevalRecord {
  currency: string
  balance: number
  oldRate: number
  newRate: number
  revalDiff: number
}

interface TrialRecord { accountCode: string; accountName: string; debitAmount: number; creditAmount: number }

const currentStep = ref(0)
const showConfirm = ref(false)

const draftData = ref<DraftEntry[]>([
  { voucherNo: 'D025', voucherDate: '2024-01-28', accountCode: '100201', amount: 25000, direction: 'DEBIT' },
  { voucherNo: 'D026', voucherDate: '2024-01-29', accountCode: '100202', amount: 18000, direction: 'CREDIT' },
  { voucherNo: 'D027', voucherDate: '2024-01-30', accountCode: '200101', amount: 32000, direction: 'DEBIT' }
])
const draftCount = ref(draftData.value.length)

const draftColumns = [
  { title: '凭证号', dataIndex: 'voucherNo', key: 'voucherNo' },
  { title: '凭证日期', dataIndex: 'voucherDate', key: 'voucherDate' },
  { title: '科目代码', dataIndex: 'accountCode', key: 'accountCode' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '借贷', dataIndex: 'direction', key: 'direction' }
]

const revalData = ref<RevalRecord[]>([
  { currency: 'USD', balance: 500000, oldRate: 7.2536, newRate: 7.2850, revalDiff: 15700 },
  { currency: 'EUR', balance: 320000, oldRate: 7.8532, newRate: 7.8910, revalDiff: 12096 },
  { currency: 'JPY', balance: 80000000, oldRate: 0.0485, newRate: 0.0489, revalDiff: 32000 },
  { currency: 'GBP', balance: 150000, oldRate: 9.1520, newRate: 9.2030, revalDiff: 7650 }
])

const revalColumns = [
  { title: '币种', dataIndex: 'currency', key: 'currency' },
  { title: '原币余额', dataIndex: 'balance', key: 'balance' },
  { title: '原汇率', dataIndex: 'oldRate', key: 'oldRate' },
  { title: '新汇率', dataIndex: 'newRate', key: 'newRate' },
  { title: '重估差额', dataIndex: 'revalDiff', key: 'revalDiff' }
]

const trialData = ref<TrialRecord[]>([
  { accountCode: '1001', accountName: '库存现金', debitAmount: 50000, creditAmount: 45000 },
  { accountCode: '1002', accountName: '银行存款', debitAmount: 2500000, creditAmount: 1800000 },
  { accountCode: '2201', accountName: '应付账款', debitAmount: 300000, creditAmount: 500000 },
  { accountCode: '4001', accountName: '实收资本', debitAmount: 0, creditAmount: 1000000 },
  { accountCode: '6001', accountName: '营业收入', debitAmount: 0, creditAmount: 355000 },
  { accountCode: '6401', accountName: '营业成本', debitAmount: 200000, creditAmount: 0 }
])

const trialTotal = reactive({
  debit: trialData.value.reduce((s, r) => s + r.debitAmount, 0),
  credit: trialData.value.reduce((s, r) => s + r.creditAmount, 0)
})

const closingInfo = reactive({
  period: '2024-01',
  postedCount: 156,
  revalCount: 0
})

const trialColumns = [
  { title: '科目代码', dataIndex: 'accountCode', key: 'accountCode' },
  { title: '科目名称', dataIndex: 'accountName', key: 'accountName' },
  { title: '借方余额', dataIndex: 'debitAmount', key: 'debitAmount' },
  { title: '贷方余额', dataIndex: 'creditAmount', key: 'creditAmount' }
]

function nextStep() { currentStep.value = Math.min(3, currentStep.value + 1) }

function handleBatchPost() {
  draftData.value = []
  draftCount.value = 0
  message.success('所有草稿分录已批量过账')
  nextStep()
}

function handleGenerateRevalEntry() {
  closingInfo.revalCount = revalData.value.length
  message.success(`已生成 ${revalData.value.length} 笔重估分录`)
  nextStep()
}

function handleClosing() {
  showConfirm.value = false
  message.success(`${closingInfo.period} 月末结账完成`)
  currentStep.value = 0
}
</script>

<style scoped>
.month-end-closing h2 { margin-bottom: 16px; }
</style>
