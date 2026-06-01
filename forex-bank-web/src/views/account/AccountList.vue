<template>
  <div class="account-list">
    <h2>账户管理</h2>

    <a-card class="search-card">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="账户类型">
          <a-select v-model:value="queryParams.accountType" allow-clear style="width: 120px" @change="handleSearch">
            <a-select-option value="CURRENT">活期</a-select-option>
            <a-select-option value="SAVING">储蓄</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="币种">
          <a-input v-model:value="queryParams.currency" allow-clear placeholder="币种" />
        </a-form-item>
        <a-form-item label="客户ID">
          <a-input v-model:value="queryParams.customerId" allow-clear placeholder="客户ID" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card style="margin-top: 16px">
      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        row-key="accountNo"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'accountStatus'">
            <a-tag :color="statusMap[record.accountStatus]?.color">{{ statusMap[record.accountStatus]?.label }}</a-tag>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-space>
              <a @click="showDetail(record)">查看</a>
              <a @click="showOpModal(record, 'deposit')">存款</a>
              <a @click="showOpModal(record, 'withdraw')">取款</a>
              <a @click="showOpModal(record, 'freeze')">冻结</a>
              <a-popconfirm title="确定要关闭该账户吗？" @confirm="handleClose(record)">
                <a style="color: #f5222d">关闭</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-drawer title="账户详情" :open="detailVisible" :width="600" @close="detailVisible = false">
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="账户号">{{ detailData.accountNo }}</a-descriptions-item>
        <a-descriptions-item label="客户ID">{{ detailData.customerId }}</a-descriptions-item>
        <a-descriptions-item label="账户类型">{{ detailData.accountType }}</a-descriptions-item>
        <a-descriptions-item label="币种">{{ detailData.currency }}</a-descriptions-item>
        <a-descriptions-item label="余额">{{ detailData.balance }}</a-descriptions-item>
        <a-descriptions-item label="冻结金额">{{ detailData.frozenAmount }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="statusMap[detailData.accountStatus]?.color">{{ statusMap[detailData.accountStatus]?.label }}</a-tag>
        </a-descriptions-item>
      </a-descriptions>
    </a-drawer>

    <a-modal v-model:open="opVisible" :title="opTitle" :confirm-loading="submitting" @ok="handleOpSubmit">
      <a-form :model="opForm" layout="vertical">
        <a-form-item label="金额" required>
          <a-input-number v-model:value="opForm.amount" style="width: 100%" :min="0" :precision="2" />
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="opForm.remark" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { accountApi } from '@/api/business'
import type { TablePaginationConfig } from 'ant-design-vue'

const statusMap: Record<string, { label: string; color: string }> = {
  ACTIVE: { label: '正常', color: '#52c41a' },
  FROZEN: { label: '冻结', color: '#1677ff' },
  CLOSED: { label: '已关闭', color: '#8c8c8c' }
}

interface AccountRecord {
  id: number
  accountNo: string
  customerId: number
  accountType: string
  currency: string
  balance: number
  frozenAmount: number
  accountStatus: string
}

const columns = [
  { title: '账户号', dataIndex: 'accountNo', key: 'accountNo' },
  { title: '客户ID', dataIndex: 'customerId', key: 'customerId' },
  { title: '账户类型', dataIndex: 'accountType', key: 'accountType' },
  { title: '币种', dataIndex: 'currency', key: 'currency' },
  { title: '余额', dataIndex: 'balance', key: 'balance' },
  { title: '冻结金额', dataIndex: 'frozenAmount', key: 'frozenAmount' },
  { title: '状态', dataIndex: 'accountStatus', key: 'accountStatus' },
  { title: '操作', key: 'operation', width: 260 }
]

const tableData = ref<AccountRecord[]>([])
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })
const queryParams = reactive({ accountType: undefined as string | undefined, currency: '', customerId: '' })

const detailVisible = ref(false)
const detailData = reactive<AccountRecord>({} as AccountRecord)

const opVisible = ref(false)
const opType = ref<'deposit' | 'withdraw' | 'freeze'>('deposit')
const opTarget = ref<AccountRecord | null>(null)
const submitting = ref(false)
const opForm = reactive({ amount: 0, remark: '' })

const opTitle = computed(() => ({ deposit: '存款', withdraw: '取款', freeze: '冻结冻结' }[opType.value] || '操作'))

async function fetchData() {
  loading.value = true
  try {
    const res = await accountApi.getCustomerAccounts(0)
    tableData.value = (res.data.data || []).slice(0, 10)
    pagination.total = (res.data.data || []).length
  } catch { /* fallback */ } finally { loading.value = false }
}

function handleSearch() { pagination.current = 1; fetchData() }
function handleReset() { queryParams.accountType = undefined; queryParams.currency = ''; queryParams.customerId = ''; handleSearch() }
function handleTableChange(pg: TablePaginationConfig) { pagination.current = pg.current!; pagination.pageSize = pg.pageSize!; fetchData() }

function showDetail(record: AccountRecord) { Object.assign(detailData, record); detailVisible.value = true }

function showOpModal(record: AccountRecord, type: 'deposit' | 'withdraw' | 'freeze') {
  opType.value = type
  opTarget.value = record
  opForm.amount = 0
  opForm.remark = ''
  opVisible.value = true
}

async function handleOpSubmit() {
  submitting.value = true
  try {
    const data = { id: opTarget.value!.id, amount: opForm.amount, remark: opForm.remark }
    if (opType.value === 'deposit') { await accountApi.deposit(data); message.success('存款成功') }
    else if (opType.value === 'withdraw') { await accountApi.withdraw(data); message.success('取款成功') }
    else if (opType.value === 'freeze') { await accountApi.freeze(data); message.success('冻结成功') }
    opVisible.value = false
    fetchData()
  } catch { } finally { submitting.value = false }
}

async function handleClose(record: AccountRecord) {
  try { await accountApi.close(record.id); message.success('账户已关闭'); fetchData() } catch { }
}

onMounted(() => fetchData())
</script>

<style scoped>
.account-list h2 { margin-bottom: 16px; }
.search-card { margin-bottom: 16px; }
</style>
