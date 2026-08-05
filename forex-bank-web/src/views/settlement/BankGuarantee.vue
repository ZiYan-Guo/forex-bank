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
        <a-form-item label="客户ID" required>
          <a-input-number v-model:value="form.customerId" :min="1" style="width:100%" placeholder="请输入客户ID" />
        </a-form-item>
        <a-form-item label="金额" required>
          <a-input-number v-model:value="form.guaranteeAmount" :min="0" :precision="2" style="width:100%" placeholder="请输入金额" />
        </a-form-item>
        <a-form-item label="币种" required>
          <a-select v-model:value="form.guaranteeCurrency" placeholder="请选择币种">
            <a-select-option value="USD">USD 美元</a-select-option>
            <a-select-option value="EUR">EUR 欧元</a-select-option>
            <a-select-option value="GBP">GBP 英镑</a-select-option>
            <a-select-option value="JPY">JPY 日元</a-select-option>
            <a-select-option value="CNY">CNY 人民币</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="受益人" required>
          <a-input v-model:value="form.beneficiaryName" placeholder="请输入受益人" />
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
        :pagination="{ current: pagination.pageNum, pageSize: pagination.pageSize, total: pagination.total, showSizeChanger: true, onChange: onPageChange }"
        row-key="guaranteeNo"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'guaranteeType'">
            <a-tag :color="typeColor(record.guaranteeType)">{{ typeLabel(record.guaranteeType) }}</a-tag>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="OrderStatusMap[record.guaranteeStatus]?.color || '#8c8c8c'">
              {{ OrderStatusMap[record.guaranteeStatus]?.label || record.guaranteeStatus }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a @click="viewDetail(record)">查看</a>
              <a v-if="record.guaranteeStatus === 'DRAFT'" @click="handleIssue(record)">签发</a>
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
        <a-descriptions-item label="金额">{{ detailRecord.guaranteeAmount }}</a-descriptions-item>
        <a-descriptions-item label="币种">{{ detailRecord.guaranteeCurrency }}</a-descriptions-item>
        <a-descriptions-item label="受益人">{{ detailRecord.beneficiaryInfo }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="OrderStatusMap[detailRecord.guaranteeStatus]?.color || '#8c8c8c'">
            {{ OrderStatusMap[detailRecord.guaranteeStatus]?.label || detailRecord.guaranteeStatus }}
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
  customerId: null as number | null,
  guaranteeType: undefined as string | undefined,
  guaranteeAmount: null as number | null,
  guaranteeCurrency: undefined as string | undefined,
  beneficiaryName: '',
  effectiveDate: undefined as any,
  expiryDate: undefined as any,
  guaranteeFormat: 'DIRECT'
})

const list = ref<any[]>([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const columns = [
  { title: '保函编号', dataIndex: 'guaranteeNo', key: 'guaranteeNo' },
  { title: '保函类型', dataIndex: 'guaranteeType', key: 'guaranteeType' },
  { title: '金额', dataIndex: 'guaranteeAmount', key: 'guaranteeAmount' },
  { title: '币种', dataIndex: 'guaranteeCurrency', key: 'guaranteeCurrency' },
  { title: '受益人', dataIndex: 'beneficiaryInfo', key: 'beneficiaryInfo' },
  { title: '生效日', dataIndex: 'effectiveDate', key: 'effectiveDate' },
  { title: '到期日', dataIndex: 'expiryDate', key: 'expiryDate' },
  { title: '状态', dataIndex: 'guaranteeStatus', key: 'status' },
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

async function fetchGuarantees() {
  loading.value = true
  try {
    const res = await settlementApi.guaranteePageQuery({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    if (res.data?.code === 200) {
      list.value = res.data.data?.records || []
      pagination.total = res.data.data?.total || 0
    }
  } finally {
    loading.value = false
  }
}

function onPageChange(page: number, size: number) {
  pagination.pageNum = page
  pagination.pageSize = size
  fetchGuarantees()
}

async function handleCreate() {
  submitting.value = true
  try {
    const res = await settlementApi.createGuarantee({
      ...form,
      effectiveDate: formatDate(form.effectiveDate),
      expiryDate: formatDate(form.expiryDate)
    })
    if (res.data?.code === 200) {
      message.success('保函创建成功')
      fetchGuarantees()
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
    fetchGuarantees()
  }
}

function formatDate(value: any) {
  if (!value) {
    return undefined
  }
  return typeof value?.format === 'function' ? value.format('YYYY-MM-DD') : value
}

onMounted(() => {
  fetchGuarantees()
})
</script>

<style scoped>
.guarantee-page { background: #fff; padding: 24px; border-radius: 8px; }
</style>
