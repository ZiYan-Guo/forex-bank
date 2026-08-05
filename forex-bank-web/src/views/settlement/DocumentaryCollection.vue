<template>
  <div class="collection-page">
    <h2>跟单托收</h2>

    <a-card title="新建托收" style="margin-bottom:24px">
      <a-form :model="form" layout="vertical" style="max-width:600px">
        <a-form-item label="托收类型" required>
          <a-select v-model:value="form.collectionType" placeholder="请选择托收类型">
            <a-select-option value="CLEAN">光票托收</a-select-option>
            <a-select-option value="DOCUMENTARY">跟单托收</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="客户ID" required>
          <a-input-number v-model:value="form.customerId" :min="1" style="width:100%" placeholder="请输入客户ID" />
        </a-form-item>
        <a-form-item label="托收方式" required>
          <a-select v-model:value="form.collectionForm" placeholder="请选择托收方式">
            <a-select-option value="DP">DP 付款交单</a-select-option>
            <a-select-option value="DA">DA 承兑交单</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="金额" required>
          <a-input-number v-model:value="form.collectionAmount" :min="0" :precision="2" style="width:100%" placeholder="请输入金额" />
        </a-form-item>
        <a-form-item label="币种" required>
          <a-select v-model:value="form.collectionCurrency" placeholder="请选择币种">
            <a-select-option value="USD">USD 美元</a-select-option>
            <a-select-option value="EUR">EUR 欧元</a-select-option>
            <a-select-option value="GBP">GBP 英镑</a-select-option>
            <a-select-option value="JPY">JPY 日元</a-select-option>
            <a-select-option value="CNY">CNY 人民币</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="付款人" required>
          <a-input v-model:value="form.draweeName" placeholder="请输入付款人" />
        </a-form-item>
        <a-form-item label="单据清单" required>
          <a-textarea v-model:value="form.documentsList" :rows="4" placeholder="请输入单据清单" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" :loading="submitting" @click="handleCreate">创建托收</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card title="托收列表">
      <a-table
        :columns="columns"
        :data-source="list"
        :loading="loading"
        :pagination="{ current: pagination.pageNum, pageSize: pagination.pageSize, total: pagination.total, showSizeChanger: true, onChange: onPageChange }"
        row-key="collectionNo"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'collectionType'">
            <a-tag :color="record.collectionType === 'CLEAN' ? '#1677ff' : '#52c41a'">
              {{ record.collectionType === 'CLEAN' ? '光票' : '跟单' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'collectionMode'">
            <a-tag :color="record.collectionForm === 'DP' ? '#fa8c16' : '#722ed1'">
              {{ record.collectionForm || '-' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="OrderStatusMap[record.collectionStatus]?.color || '#8c8c8c'">
              {{ OrderStatusMap[record.collectionStatus]?.label || record.collectionStatus }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a @click="viewDetail(record)">查看</a>
              <a v-if="canPay(record.collectionStatus)" @click="handlePay(record)">付款</a>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal v-model:open="detailVisible" title="托收详情" :footer="null" width="600px">
      <a-descriptions v-if="detailRecord" :column="2" bordered size="small">
        <a-descriptions-item label="托收编号">{{ detailRecord.collectionNo }}</a-descriptions-item>
        <a-descriptions-item label="托收类型">
          <a-tag :color="detailRecord.collectionType === 'CLEAN' ? '#1677ff' : '#52c41a'">
            {{ detailRecord.collectionType === 'CLEAN' ? '光票' : '跟单' }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="托收方式">{{ detailRecord.collectionForm }}</a-descriptions-item>
        <a-descriptions-item label="金额">{{ detailRecord.collectionAmount }}</a-descriptions-item>
        <a-descriptions-item label="币种">{{ detailRecord.collectionCurrency }}</a-descriptions-item>
        <a-descriptions-item label="付款人">{{ detailRecord.draweeInfo }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="OrderStatusMap[detailRecord.collectionStatus]?.color || '#8c8c8c'">
            {{ OrderStatusMap[detailRecord.collectionStatus]?.label || detailRecord.collectionStatus }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="单据清单" :span="2">{{ detailRecord.documentsList }}</a-descriptions-item>
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
  collectionType: undefined as string | undefined,
  collectionForm: undefined as string | undefined,
  collectionAmount: null as number | null,
  collectionCurrency: undefined as string | undefined,
  draweeName: '',
  documentsList: ''
})

const list = ref<any[]>([])
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const columns = [
  { title: '托收编号', dataIndex: 'collectionNo', key: 'collectionNo' },
  { title: '托收类型', dataIndex: 'collectionType', key: 'collectionType' },
  { title: '方式', dataIndex: 'collectionForm', key: 'collectionMode' },
  { title: '金额', dataIndex: 'collectionAmount', key: 'collectionAmount' },
  { title: '币种', dataIndex: 'collectionCurrency', key: 'collectionCurrency' },
  { title: '付款人', dataIndex: 'draweeInfo', key: 'draweeInfo' },
  { title: '状态', dataIndex: 'collectionStatus', key: 'status' },
  { title: '操作', key: 'action', width: 150 }
]

async function fetchCollections() {
  loading.value = true
  try {
    const res = await settlementApi.collectionPageQuery({
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
  fetchCollections()
}

async function handleCreate() {
  submitting.value = true
  try {
    const res = await settlementApi.createCollection(form)
    if (res.data?.code === 200) {
      message.success('托收创建成功')
      fetchCollections()
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

async function handlePay(record: any) {
  const res = await settlementApi.payCollection(record.collectionNo)
  if (res.data?.code === 200) {
    message.success('付款已处理')
    fetchCollections()
  }
}

function canPay(status: string) {
  return ['DOCS_RECEIVED', 'PRESENTED', 'ACCEPTED'].includes(status)
}

onMounted(() => {
  fetchCollections()
})
</script>

<style scoped>
.collection-page { background: #fff; padding: 24px; border-radius: 8px; }
</style>
