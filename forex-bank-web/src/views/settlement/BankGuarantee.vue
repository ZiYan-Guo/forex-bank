<template>
  <div class="guarantee-page">
    <h2>国际保函</h2>

    <a-card title="新建保函" style="margin-bottom:24px">
      <a-form :model="form" layout="vertical" style="max-width:600px">
        <a-form-item label="保函类型" required>
          <a-select v-model:value="form.guaranteeType" placeholder="请选择保函类型">
            <a-select-option value="BID">投标保函</a-select-option>
            <a-select-option value="PERFORMANCE">履约保函</a-select-option>
            <a-select-option value="ADVANCE_PAYMENT">预付款保函</a-select-option>
            <a-select-option value="RETENTION">质量保函</a-select-option>
            <a-select-option value="PAYMENT">付款保函</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="金额" required>
          <a-input-number v-model:value="form.amount" :min="0" :precision="2" style="width:100%" placeholder="请输入金额" />
        </a-form-item>
        <a-form-item label="币种" required>
          <a-select v-model:value="form.currency" placeholder="请选择币种">
            <a-select-option value="USD">USD 美元</a-select-option>
            <a-select-option value="EUR">EUR 欧元</a-select-option>
            <a-select-option value="GBP">GBP 英镑</a-select-option>
            <a-select-option value="JPY">JPY 日元</a-select-option>
            <a-select-option value="CNY">CNY 人民币</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="受益人" required>
          <a-input v-model:value="form.beneficiary" placeholder="请输入受益人" />
        </a-form-item>
        <a-form-item label="生效日" required>
          <a-date-picker v-model:value="form.effectiveDate" style="width:100%" placeholder="请选择生效日" />
        </a-form-item>
        <a-form-item label="到期日" required>
          <a-date-picker v-model:value="form.expiryDate" style="width:100%" placeholder="请选择到期日" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" :loading="submitting" @click="handleCreate">创建保函</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card title="保函列表">
      <a-table
        :columns="columns"
        :data-source="list"
        :loading="loading"
        row-key="guaranteeNo"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'guaranteeType'">
            <a-tag :color="typeColor(record.guaranteeType)">{{ typeLabel(record.guaranteeType) }}</a-tag>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="OrderStatusMap[record.status]?.color || '#8c8c8c'">
              {{ OrderStatusMap[record.status]?.label || record.status }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a @click="viewDetail(record)">查看</a>
              <a v-if="record.status === 'PENDING'" @click="handleIssue(record)">签发</a>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="detailVisible" title="保函详情" :footer="null" width="600px">
      <a-descriptions v-if="detailRecord" :column="2" bordered size="small">
        <a-descriptions-item label="保函编号">{{ detailRecord.guaranteeNo }}</a-descriptions-item>
        <a-descriptions-item label="保函类型">
          <a-tag :color="typeColor(detailRecord.guaranteeType)">{{ typeLabel(detailRecord.guaranteeType) }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="金额">{{ detailRecord.amount }}</a-descriptions-item>
        <a-descriptions-item label="币种">{{ detailRecord.currency }}</a-descriptions-item>
        <a-descriptions-item label="受益人">{{ detailRecord.beneficiary }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="OrderStatusMap[detailRecord.status]?.color || '#8c8c8c'">
            {{ OrderStatusMap[detailRecord.status]?.label || detailRecord.status }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="生效日">{{ detailRecord.effectiveDate }}</a-descriptions-item>
        <a-descriptions-item label="到期日">{{ detailRecord.expiryDate }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { settlementApi } from '@/api/business'
import { OrderStatusMap } from '@/types/api'

const submitting = ref(false)
const loading = ref(false)
const detailVisible = ref(false)
const detailRecord = ref<any>(null)

const form = reactive({
  guaranteeType: undefined as string | undefined,
  amount: null as number | null,
  currency: undefined as string | undefined,
  beneficiary: '',
  effectiveDate: undefined as string | undefined,
  expiryDate: undefined as string | undefined
})

const list = ref<any[]>([])

const columns = [
  { title: '保函编号', dataIndex: 'guaranteeNo', key: 'guaranteeNo' },
  { title: '保函类型', dataIndex: 'guaranteeType', key: 'guaranteeType' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '币种', dataIndex: 'currency', key: 'currency' },
  { title: '受益人', dataIndex: 'beneficiary', key: 'beneficiary' },
  { title: '生效日', dataIndex: 'effectiveDate', key: 'effectiveDate' },
  { title: '到期日', dataIndex: 'expiryDate', key: 'expiryDate' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action', width: 150 }
]

function typeLabel(type: string) {
  const map: Record<string, string> = {
    BID: '投标保函', PERFORMANCE: '履约保函', ADVANCE_PAYMENT: '预付款保函',
    RETENTION: '质量保函', PAYMENT: '付款保函'
  }
  return map[type] || type
}

function typeColor(type: string) {
  const map: Record<string, string> = {
    BID: '#1677ff', PERFORMANCE: '#52c41a', ADVANCE_PAYMENT: '#fa8c16',
    RETENTION: '#722ed1', PAYMENT: '#eb2f96'
  }
  return map[type] || '#8c8c8c'
}

function mockFetch() {
  list.value = [
    { guaranteeNo: 'BG20260601001', guaranteeType: 'PERFORMANCE', amount: 200000, currency: 'USD', beneficiary: 'XYZ Construction', effectiveDate: '2026-06-01', expiryDate: '2027-06-01', status: 'CONFIRMED' },
    { guaranteeNo: 'BG20260528002', guaranteeType: 'BID', amount: 50000, currency: 'EUR', beneficiary: 'ABC Engineering', effectiveDate: '2026-05-28', expiryDate: '2026-11-28', status: 'PENDING' },
    { guaranteeNo: 'BG20260520003', guaranteeType: 'PAYMENT', amount: 120000, currency: 'GBP', beneficiary: 'Global Supplies', effectiveDate: '2026-05-20', expiryDate: '2026-12-31', status: 'SUCCESS' }
  ]
}

async function handleCreate() {
  submitting.value = true
  try {
    const res = await settlementApi.createGuarantee(form)
    if (res.data?.code === 200) {
      message.success('保函创建成功')
      mockFetch()
    } else {
      message.error(res.data?.message || '创建失败')
    }
  } finally {
    submitting.value = false
  }
}

function viewDetail(record: any) {
  detailRecord.value = record
  detailVisible.value = true
}

async function handleIssue(record: any) {
  const res = await settlementApi.issueGuarantee(record.guaranteeNo)
  if (res.data?.code === 200) {
    message.success('保函已签发')
    mockFetch()
  }
}

onMounted(() => {
  mockFetch()
})
</script>

<style scoped>
.guarantee-page { background: #fff; padding: 24px; border-radius: 8px; }
</style>
