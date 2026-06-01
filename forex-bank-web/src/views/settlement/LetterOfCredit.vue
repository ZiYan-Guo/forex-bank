<template>
  <div class="lc-page">
    <h2>国际信用证</h2>

    <a-card title="开立信用证" :collapsible="true" :default-collapsed="true" style="margin-bottom:24px">
      <a-form :model="lcForm" layout="vertical" style="max-width:900px">
        <a-row :gutter="24">
          <a-col :span="8">
            <a-form-item label="信用证类型" required>
              <a-select v-model:value="lcForm.lcType" placeholder="请选择类型">
                <a-select-option value="IMPORT">进口</a-select-option>
                <a-select-option value="EXPORT">出口</a-select-option>
                <a-select-option value="STANDBY">备用</a-select-option>
                <a-select-option value="DOMESTIC">国内</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="信用证编号" required>
              <a-input v-model:value="lcForm.lcNo" placeholder="请输入信用证编号" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="客户ID">
              <a-input-number v-model:value="lcForm.customerId" :min="1" style="width:100%" placeholder="请输入客户ID" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="币种" required>
              <a-select v-model:value="lcForm.currency" placeholder="请选择币种">
                <a-select-option value="USD">USD 美元</a-select-option>
                <a-select-option value="EUR">EUR 欧元</a-select-option>
                <a-select-option value="GBP">GBP 英镑</a-select-option>
                <a-select-option value="JPY">JPY 日元</a-select-option>
                <a-select-option value="CNY">CNY 人民币</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="金额" required>
              <a-input-number v-model:value="lcForm.amount" :min="0" :precision="2" style="width:100%" placeholder="请输入金额" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="有效期" required>
              <a-date-picker v-model:value="lcForm.expiryDate" style="width:100%" placeholder="请选择有效期" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="兑用方式">
              <a-select v-model:value="lcForm.availabilityType" placeholder="请选择兑用方式">
                <a-select-option value="SIGHT">即期</a-select-option>
                <a-select-option value="ACCEPTANCE">承兑</a-select-option>
                <a-select-option value="NEGOTIATION">议付</a-select-option>
                <a-select-option value="DEFERRED">延期</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="申请人信息">
              <a-textarea v-model:value="lcForm.applicantInfo" :rows="3" placeholder="请输入申请人信息" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="受益人信息">
              <a-textarea v-model:value="lcForm.beneficiaryInfo" :rows="3" placeholder="请输入受益人信息" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="货物描述">
              <a-textarea v-model:value="lcForm.goodsDescription" :rows="3" placeholder="请输入货物描述" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="单据要求">
              <a-textarea v-model:value="lcForm.docRequirements" :rows="3" placeholder="请输入单据要求" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item>
          <a-button type="primary" :loading="submitting" @click="handleCreateLc">创建信用证</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card title="信用证列表">
      <a-table
        :columns="lcColumns"
        :data-source="lcList"
        :loading="loading"
        :pagination="{ current: pagination.pageNum, pageSize: pagination.pageSize, total: pagination.total, showSizeChanger: true, onChange: onPageChange }"
        row-key="lcNo"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'lcType'">
            <a-tag :color="lcTypeColor(record.lcType)">{{ lcTypeLabel(record.lcType) }}</a-tag>
          </template>
          <template v-if="column.key === 'lcStatus'">
            <a-tag :color="OrderStatusMap[record.lcStatus]?.color || '#8c8c8c'">
              {{ OrderStatusMap[record.lcStatus]?.label || record.lcStatus }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a @click="viewDetail(record)">查看</a>
              <a v-if="record.lcStatus === 'PENDING'" @click="handleIssue(record)">开立</a>
              <a v-if="record.lcStatus === 'PENDING' || record.lcStatus === 'CONFIRMED'" @click="handleAmend(record)">修改</a>
              <a v-if="record.lcStatus === 'CONFIRMED'" @click="handlePresent(record)">交单</a>
              <a v-if="record.lcStatus === 'PROCESSING'" @click="handleCheckDocs(record)">审单</a>
              <a v-if="record.lcStatus === 'PROCESSING'" @click="handleAccept(record)">承兑</a>
              <a v-if="record.lcStatus === 'PROCESSING' || record.lcStatus === 'CONFIRMED'" @click="handlePay(record)">付款</a>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="detailVisible" title="信用证详情" :footer="null" width="700px">
      <a-descriptions v-if="detailRecord" :column="2" bordered size="small">
        <a-descriptions-item label="信用证号">{{ detailRecord.lcNo }}</a-descriptions-item>
        <a-descriptions-item label="类型">
          <a-tag :color="lcTypeColor(detailRecord.lcType)">{{ lcTypeLabel(detailRecord.lcType) }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="金额">{{ detailRecord.amount }}</a-descriptions-item>
        <a-descriptions-item label="币种">{{ detailRecord.currency }}</a-descriptions-item>
        <a-descriptions-item label="有效期">{{ detailRecord.expiryDate }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="OrderStatusMap[detailRecord.lcStatus]?.color || '#8c8c8c'">
            {{ OrderStatusMap[detailRecord.lcStatus]?.label || detailRecord.lcStatus }}
          </a-tag>
        </a-descriptions-item>
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

const lcForm = reactive({
  lcType: undefined as string | undefined,
  lcNo: '',
  customerId: null as number | null,
  currency: undefined as string | undefined,
  amount: null as number | null,
  expiryDate: undefined as string | undefined,
  availabilityType: undefined as string | undefined,
  applicantInfo: '',
  beneficiaryInfo: '',
  goodsDescription: '',
  docRequirements: ''
})

const lcList = ref<any[]>([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const lcColumns = [
  { title: '信用证号', dataIndex: 'lcNo', key: 'lcNo' },
  { title: '类型', dataIndex: 'lcType', key: 'lcType' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '有效期', dataIndex: 'expiryDate', key: 'expiryDate' },
  { title: '状态', dataIndex: 'lcStatus', key: 'lcStatus' },
  { title: '操作', key: 'action', width: 320 }
]

function lcTypeLabel(type: string) {
  const map: Record<string, string> = { IMPORT: '进口', EXPORT: '出口', STANDBY: '备用', DOMESTIC: '国内' }
  return map[type] || type
}

function lcTypeColor(type: string) {
  const map: Record<string, string> = { IMPORT: '#1677ff', EXPORT: '#52c41a', STANDBY: '#fa8c16', DOMESTIC: '#722ed1' }
  return map[type] || '#8c8c8c'
}

async function fetchLcList() {
  loading.value = true
  try {
    const res = await settlementApi.lcPageQuery({ pageNum: pagination.pageNum, pageSize: pagination.pageSize })
    if (res.data?.code === 200) {
      lcList.value = res.data.data?.records || []
      pagination.total = res.data.data?.total || 0
    }
  } finally {
    loading.value = false
  }
}

function onPageChange(page: number, size: number) {
  pagination.pageNum = page
  pagination.pageSize = size
  fetchLcList()
}

async function handleCreateLc() {
  submitting.value = true
  try {
    const res = await settlementApi.createLc(lcForm)
    if (res.data?.code === 200) {
      message.success('信用证创建成功')
      fetchLcList()
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
  const res = await settlementApi.issueLc(record.lcNo)
  if (res.data?.code === 200) {
    message.success('信用证已开立')
    fetchLcList()
  }
}

async function handleAmend(record: any) {
  const res = await settlementApi.amendLc({ lcNo: record.lcNo })
  if (res.data?.code === 200) {
    message.success('信用证已修改')
    fetchLcList()
  }
}

async function handlePresent(record: any) {
  const res = await settlementApi.presentLcDocs(record.lcNo)
  if (res.data?.code === 200) {
    message.success('单据已提交')
    fetchLcList()
  }
}

async function handleCheckDocs(record: any) {
  const res = await settlementApi.checkLcDocs(record.lcNo, false)
  if (res.data?.code === 200) {
    message.success('审单完成')
    fetchLcList()
  }
}

async function handleAccept(record: any) {
  const res = await settlementApi.acceptLc(record.lcNo)
  if (res.data?.code === 200) {
    message.success('已承兑')
    fetchLcList()
  }
}

async function handlePay(record: any) {
  const res = await settlementApi.payLc(record.lcNo)
  if (res.data?.code === 200) {
    message.success('付款已处理')
    fetchLcList()
  }
}

onMounted(() => {
  fetchLcList()
})
</script>

<style scoped>
.lc-page { background: #fff; padding: 24px; border-radius: 8px; }
</style>
