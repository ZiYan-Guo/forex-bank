<template>
  <div class="customer-list">
    <h2>客户管理</h2>

    <a-card class="search-card">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="客户类型">
          <a-select v-model:value="queryParams.customerType" allow-clear style="width: 120px" @change="handleSearch">
            <a-select-option value="CORP">对公</a-select-option>
            <a-select-option value="PERSONAL">对私</a-select-option>
            <a-select-option value="INTERBANK">同业</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="风险等级">
          <a-select v-model:value="queryParams.riskLevel" allow-clear style="width: 120px" @change="handleSearch">
            <a-select-option v-for="(v, k) in RiskLevelMap" :key="k" :value="k">{{ v.label }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="关键词">
          <a-input v-model:value="queryParams.keyword" allow-clear placeholder="客户名称/编号" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>
      <div style="margin-top: 12px">
        <a-button type="primary" @click="showCreateModal">新增客户</a-button>
      </div>
    </a-card>

    <a-card style="margin-top: 16px">
      <a-table
        :columns="columns"
        :data-source="tableData"
        :pagination="pagination"
        :loading="loading"
        row-key="customerNo"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'customerType'">
            <a-tag>{{ customerTypeMap[record.customerType] }}</a-tag>
          </template>
          <template v-else-if="column.key === 'riskLevel'">
            <a-tag :color="RiskLevelMap[record.riskLevel]?.color">{{ RiskLevelMap[record.riskLevel]?.label }}</a-tag>
          </template>
          <template v-else-if="column.key === 'addressStatus'">
            <a-tag :color="record.addressStatus === 'STRUCTURED' ? '#52c41a' : '#fa8c16'">
              {{ record.addressStatus === 'STRUCTURED' ? '已结构化' : '待补录' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 'ACTIVE' ? '#52c41a' : '#8c8c8c'">{{ record.status === 'ACTIVE' ? '正常' : '禁用' }}</a-tag>
          </template>
          <template v-else-if="column.key === 'operation'">
            <a-space>
              <a @click="showDetail(record)">查看</a>
              <a @click="showEditModal(record)">编辑</a>
              <a @click="showRiskModal(record)">风险评级</a>
              <a-dropdown>
                <a class="ant-dropdown-link">更多 <down-outlined /></a>
                <template #overlay>
                  <a-menu @click="({ key }: any) => handleMore(key, record)">
                    <a-menu-item key="dueDiligence">尽职调查</a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-drawer title="客户详情" :open="detailVisible" :width="600" @close="detailVisible = false">
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="客户编号">{{ detailData.customerNo }}</a-descriptions-item>
        <a-descriptions-item label="客户名称">{{ detailData.customerName }}</a-descriptions-item>
        <a-descriptions-item label="客户类型">{{ customerTypeMap[detailData.customerType] }}</a-descriptions-item>
        <a-descriptions-item label="证件号码">{{ detailData.certNo }}</a-descriptions-item>
        <a-descriptions-item label="风险等级">
          <a-tag :color="RiskLevelMap[detailData.riskLevel]?.color">{{ RiskLevelMap[detailData.riskLevel]?.label }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="联系电话">{{ detailData.phone }}</a-descriptions-item>
        <a-descriptions-item label="状态">{{ detailData.status === 'ACTIVE' ? '正常' : '禁用' }}</a-descriptions-item>
        <a-descriptions-item label="创建时间">{{ detailData.createTime }}</a-descriptions-item>
      </a-descriptions>
    </a-drawer>

    <a-modal v-model:open="formVisible" :title="editingId ? '编辑客户' : '新增客户'" :confirm-loading="submitting" @ok="handleSubmit">
      <a-form :model="formData" layout="vertical">
        <a-form-item label="客户名称" required>
          <a-input v-model:value="formData.customerName" />
        </a-form-item>
        <a-form-item label="客户类型" required>
          <a-select v-model:value="formData.customerType">
            <a-select-option value="CORP">对公</a-select-option>
            <a-select-option value="PERSONAL">对私</a-select-option>
            <a-select-option value="INTERBANK">同业</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="证件号码" required>
          <a-input v-model:value="formData.certNo" />
        </a-form-item>
        <a-form-item label="联系电话">
          <a-input v-model:value="formData.phone" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal v-model:open="riskVisible" title="风险评级" :confirm-loading="submitting" @ok="handleRiskSubmit">
      <a-form :model="riskForm" layout="vertical">
        <a-form-item label="风险等级" required>
          <a-select v-model:value="riskForm.riskLevel">
            <a-select-option v-for="(v, k) in RiskLevelMap" :key="k" :value="Number(k)">{{ v.label }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="评级原因">
          <a-textarea v-model:value="riskForm.reason" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { DownOutlined } from '@ant-design/icons-vue'
import { customerApi } from '@/api/customer'
import { RiskLevelMap } from '@/types/api'
import type { TablePaginationConfig } from 'ant-design-vue'

const customerTypeMap: Record<string, string> = { CORP: '对公', PERSONAL: '对私', INTERBANK: '同业' }

interface CustomerRecord {
  id: number
  customerNo: string
  customerName: string
  customerType: string
  certNo: string
  riskLevel: number
  phone: string
  status: string
  createTime: string
}

const columns = [
  { title: '客户编号', dataIndex: 'customerNo', key: 'customerNo' },
  { title: '客户名称', dataIndex: 'customerName', key: 'customerName' },
  { title: '客户类型', dataIndex: 'customerType', key: 'customerType' },
  { title: '证件号码', dataIndex: 'certNo', key: 'certNo' },
  { title: '风险等级', dataIndex: 'riskLevel', key: 'riskLevel' },
  { title: '地址', dataIndex: 'address', key: 'address' },
  { title: '地址状态', dataIndex: 'addressStatus', key: 'addressStatus' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'operation', width: 240 }
]

const tableData = ref<CustomerRecord[]>([])
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })
const queryParams = reactive({ customerType: undefined as string | undefined, riskLevel: undefined as number | undefined, keyword: '' })

const detailVisible = ref(false)
const detailData = reactive<CustomerRecord>({} as CustomerRecord)

const formVisible = ref(false)
const editingId = ref<number | null>(null)
const submitting = ref(false)
const formData = reactive({ customerName: '', customerType: 'CORP', certNo: '', phone: '' })

const riskVisible = ref(false)
const riskTarget = ref<CustomerRecord | null>(null)
const riskForm = reactive({ riskLevel: 1, reason: '' })

async function fetchData() {
  loading.value = true
  try {
    const res = await customerApi.pageQuery({ ...queryParams, pageNum: pagination.current, pageSize: pagination.pageSize })
    const data = res.data.data
    tableData.value = data.records
    pagination.total = data.total
  } catch { } finally { loading.value = false }
}

function handleSearch() { pagination.current = 1; fetchData() }
function handleReset() { queryParams.customerType = undefined; queryParams.riskLevel = undefined; queryParams.keyword = ''; handleSearch() }
function handleTableChange(pg: TablePaginationConfig) { pagination.current = pg.current!; pagination.pageSize = pg.pageSize!; fetchData() }

function showDetail(record: CustomerRecord) { Object.assign(detailData, record); detailVisible.value = true }

function showCreateModal() { editingId.value = null; Object.assign(formData, { customerName: '', customerType: 'CORP', certNo: '', phone: '' }); formVisible.value = true }
function showEditModal(record: CustomerRecord) { editingId.value = record.id; Object.assign(formData, { customerName: record.customerName, customerType: record.customerType, certNo: record.certNo, phone: record.phone }); formVisible.value = true }

async function handleSubmit() {
  submitting.value = true
  try {
    if (editingId.value) { await customerApi.update({ id: editingId.value, ...formData }); message.success('更新成功') }
    else { await customerApi.create(formData); message.success('创建成功') }
    formVisible.value = false
    fetchData()
  } catch { } finally { submitting.value = false }
}

function showRiskModal(record: CustomerRecord) { riskTarget.value = record; riskForm.riskLevel = record.riskLevel; riskForm.reason = ''; riskVisible.value = true }

async function handleRiskSubmit() {
  submitting.value = true
  try {
    await customerApi.updateRiskLevel({ id: riskTarget.value!.id, riskLevel: riskForm.riskLevel })
    message.success('风险评级更新成功')
    riskVisible.value = false
    fetchData()
  } catch { } finally { submitting.value = false }
}

async function handleMore(key: string, record: CustomerRecord) {
  if (key === 'dueDiligence') {
    try { await customerApi.performDueDiligence(record.id); message.success('尽职调查完成'); fetchData() } catch { }
  }
}

onMounted(() => fetchData())
</script>

<style scoped>
.customer-list h2 { margin-bottom: 16px; }
.search-card { margin-bottom: 16px; }
</style>
