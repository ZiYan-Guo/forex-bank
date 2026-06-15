<template>
  <!-- 境外放款管理 Overseas Lending Management -->
  <div class="overseas-lending">
    <h3 class="page-title">境外放款管理</h3>

    <!-- Loan contract table card 放款合同卡片 -->
    <a-card title="放款合同 Loan Contracts" size="small">
      <template #extra>
        <a-button type="primary" size="small" @click="showCreateModal = true">
          <plus-outlined /> 新建合同
        </a-button>
      </template>
      <a-table
        :columns="contractColumns"
        :data-source="contractList"
        :pagination="false"
        row-key="contractNo"
        size="small"
        :expandable="{
          expandedRowRender: (record: LoanContract) => renderRepaymentTable(record),
          rowExpandable: () => true
        }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'repaymentMethod'">
            <a-tag :color="record.repaymentMethod === 'BULLET' ? 'blue' : 'purple'">
              {{ record.repaymentMethod === 'BULLET' ? '到期一次还本' : '分期还本付息' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'loanStatus'">
            <a-tag :color="record.loanStatus === 'ACTIVE' ? 'green' : 'red'">
              {{ record.loanStatus === 'ACTIVE' ? '存续中' : '逾期' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'actions'">
            <a-space>
              <a-button type="link" size="small" @click="handleApprove(record)">审批</a-button>
              <a-button type="link" size="small" @click="handleRepay(record)">还款</a-button>
              <a-popconfirm title="确认取消该合同？" ok-text="确认" cancel-text="取消" @confirm="handleCancel(record)">
                <a-button type="link" size="small" danger>取消</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- Create contract modal 新建合同弹窗 -->
    <a-modal v-model:open="showCreateModal" title="新建合同 New Contract" @ok="handleCreate" @cancel="showCreateModal = false" width="640px">
      <a-form :model="newContract" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="客户ID Customer Id">
              <a-input v-model:value="newContract.customerId" placeholder="请输入客户ID" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="币种 Currency">
              <a-select v-model:value="newContract.currency">
                <a-select-option value="USD">USD 美元</a-select-option>
                <a-select-option value="EUR">EUR 欧元</a-select-option>
                <a-select-option value="JPY">JPY 日元</a-select-option>
                <a-select-option value="HKD">HKD 港币</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="放款金额 Loan Amount">
              <a-input-number v-model:value="newContract.loanAmount" style="width:100%" :min="0" placeholder="请输入放款金额" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="利率 Interest Rate(%)">
              <a-input-number v-model:value="newContract.interestRate" style="width:100%" :min="0" :max="100" :step="0.01" placeholder="请输入年利率" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="起始日期 Start Date">
              <a-date-picker v-model:value="newContract.startDate" style="width:100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="到期日期 End Date">
              <a-date-picker v-model:value="newContract.endDate" style="width:100%" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="还款方式 Repayment Method">
          <a-select v-model:value="newContract.repaymentMethod">
            <a-select-option value="BULLET">到期一次还本 BULLET</a-select-option>
            <a-select-option value="INSTALLMENT">分期还本付息 INSTALLMENT</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, h } from 'vue'
import { message } from 'ant-design-vue'
import dayjs, { type Dayjs } from 'dayjs'

interface LoanContract {
  contractNo: string
  customerId: string
  loanAmount: number
  currency: string
  interestRate: number
  startDate: string
  endDate: string
  repaymentMethod: string
  loanStatus: string
  outstandingPrincipal: number
}

interface RepaymentRecord {
  repayNo: string
  repayDate: string
  principalAmount: number
  interestAmount: number
  repayStatus: string
}

// Contract table columns 合同表格列定义
const contractColumns = [
  { title: '合同编号 Contract No', dataIndex: 'contractNo', key: 'contractNo' },
  { title: '客户ID Customer Id', dataIndex: 'customerId', key: 'customerId' },
  { title: '放款金额 Loan(¥)', dataIndex: 'loanAmount', key: 'loanAmount' },
  { title: '币种 Currency', dataIndex: 'currency', key: 'currency' },
  { title: '利率 Interest%', dataIndex: 'interestRate', key: 'interestRate' },
  { title: '起始日 Start', dataIndex: 'startDate', key: 'startDate' },
  { title: '到期日 End', dataIndex: 'endDate', key: 'endDate' },
  { title: '还款方式 Repayment', dataIndex: 'repaymentMethod', key: 'repaymentMethod' },
  { title: '状态 Status', dataIndex: 'loanStatus', key: 'loanStatus' },
  { title: '未偿本金 Outstanding', dataIndex: 'outstandingPrincipal', key: 'outstandingPrincipal' },
  { title: '操作 Actions', key: 'actions' }
]

// Mock contract data 模拟合同数据
const contractList = ref<LoanContract[]>([
  { contractNo: 'OL-20260601-001', customerId: 'C1001', loanAmount: 500000000, currency: 'USD', interestRate: 3.85, startDate: '2026-01-15', endDate: '2027-01-15', repaymentMethod: 'BULLET', loanStatus: 'ACTIVE', outstandingPrincipal: 500000000 },
  { contractNo: 'OL-20260601-002', customerId: 'C1002', loanAmount: 200000000, currency: 'EUR', interestRate: 3.45, startDate: '2026-03-01', endDate: '2026-12-01', repaymentMethod: 'INSTALLMENT', loanStatus: 'ACTIVE', outstandingPrincipal: 150000000 },
  { contractNo: 'OL-20260601-003', customerId: 'C1003', loanAmount: 800000000, currency: 'USD', interestRate: 4.10, startDate: '2025-06-01', endDate: '2026-06-01', repaymentMethod: 'BULLET', loanStatus: 'OVERDUE', outstandingPrincipal: 800000000 },
  { contractNo: 'OL-20260601-004', customerId: 'C1004', loanAmount: 350000000, currency: 'JPY', interestRate: 1.25, startDate: '2026-02-15', endDate: '2027-02-15', repaymentMethod: 'INSTALLMENT', loanStatus: 'ACTIVE', outstandingPrincipal: 300000000 }
])

// Mock repayment records per contract 每笔合同的模拟还款记录
const repaymentData: Record<string, RepaymentRecord[]> = {
  'OL-20260601-001': [
    { repayNo: 'RP-001', repayDate: '2026-07-15', principalAmount: 0, interestAmount: 19250000, repayStatus: '已付' },
    { repayNo: 'RP-002', repayDate: '2027-01-15', principalAmount: 500000000, interestAmount: 19250000, repayStatus: '待付' }
  ],
  'OL-20260601-002': [
    { repayNo: 'RP-003', repayDate: '2026-06-01', principalAmount: 50000000, interestAmount: 1725000, repayStatus: '已付' },
    { repayNo: 'RP-004', repayDate: '2026-09-01', principalAmount: 50000000, interestAmount: 1293750, repayStatus: '待付' },
    { repayNo: 'RP-005', repayDate: '2026-12-01', principalAmount: 50000000, interestAmount: 1293750, repayStatus: '待付' }
  ],
  'OL-20260601-003': [
    { repayNo: 'RP-006', repayDate: '2025-12-01', principalAmount: 0, interestAmount: 16400000, repayStatus: '已付' },
    { repayNo: 'RP-007', repayDate: '2026-06-01', principalAmount: 800000000, interestAmount: 16400000, repayStatus: '逾期' }
  ],
  'OL-20260601-004': [
    { repayNo: 'RP-008', repayDate: '2026-08-15', principalAmount: 50000000, interestAmount: 218750, repayStatus: '待付' },
    { repayNo: 'RP-009', repayDate: '2027-02-15', principalAmount: 300000000, interestAmount: 1312500, repayStatus: '待付' }
  ]
}

function renderRepaymentTable(record: LoanContract) {
  const records = repaymentData[record.contractNo] || []
  const columns = [
    { title: '还款编号 Repay No', dataIndex: 'repayNo', key: 'repayNo' },
    { title: '还款日期 Repay Date', dataIndex: 'repayDate', key: 'repayDate' },
    { title: '本金 Principal(¥)', dataIndex: 'principalAmount', key: 'principalAmount' },
    { title: '利息 Interest(¥)', dataIndex: 'interestAmount', key: 'interestAmount' },
    { title: '状态 Status', dataIndex: 'repayStatus', key: 'repayStatus' }
  ]
  return h(
    'div',
    { style: { padding: '16px 48px' } },
    [h('h4', { style: { marginBottom: '8px' } }, `还款记录 - ${record.contractNo}`),
      h(
        // @ts-ignore
        'a-table',
        { columns, 'data-source': records, pagination: false, size: 'small', rowKey: 'repayNo' }
      )]
  )
}

// Create contract modal 新建合同
const showCreateModal = ref(false)
const newContract = ref({
  customerId: '',
  currency: 'USD',
  loanAmount: 0,
  interestRate: 0,
  startDate: null as Dayjs | null,
  endDate: null as Dayjs | null,
  repaymentMethod: 'BULLET'
})

function handleCreate() {
  const nc = newContract.value
  contractList.value.push({
    contractNo: `OL-${dayjs().format('YYYYMMDD')}-${String(contractList.value.length + 1).padStart(3, '0')}`,
    customerId: nc.customerId || 'C-NEW',
    loanAmount: nc.loanAmount,
    currency: nc.currency,
    interestRate: nc.interestRate,
    startDate: nc.startDate ? nc.startDate.format('YYYY-MM-DD') : dayjs().format('YYYY-MM-DD'),
    endDate: nc.endDate ? nc.endDate.format('YYYY-MM-DD') : dayjs().add(1, 'year').format('YYYY-MM-DD'),
    repaymentMethod: nc.repaymentMethod,
    loanStatus: 'ACTIVE',
    outstandingPrincipal: nc.loanAmount
  })
  showCreateModal.value = false
  message.success('合同创建成功')
}

function handleApprove(record: LoanContract) {
  message.info(`审批合同: ${record.contractNo}`)
}

function handleRepay(record: LoanContract) {
  message.info(`还款操作: ${record.contractNo}`)
}

function handleCancel(record: LoanContract) {
  record.loanStatus = 'OVERDUE'
  message.warn(`合同已取消: ${record.contractNo}`)
}
</script>

<style scoped>
.overseas-lending { background: #fff; border-radius: 8px; padding: 24px; }
.page-title { margin: 0 0 20px; font-size: 18px; color: #333; font-weight: 600; }
</style>
