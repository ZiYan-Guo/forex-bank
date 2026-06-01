<template>
  <div class="reporting-console">
    <h2>监管报送</h2>

    <a-card class="search-card">
      <a-form layout="inline" :model="queryParams">
        <a-form-item label="日期范围">
          <a-range-picker v-model:value="queryParams.dateRange" @change="handleSearch" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card style="margin-top: 16px">
      <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
        <a-tab-pane key="bop" tab="国际收支申报">
          <div style="margin-bottom: 12px">
            <a-space>
              <a-button type="primary" @click="handleGenerate">生成报表</a-button>
              <a-button :disabled="selectedRows.length === 0" @click="handleBatchSubmit">批量提交</a-button>
            </a-space>
          </div>
          <a-table
            :columns="bopColumns"
            :data-source="tableData"
            :pagination="pagination"
            :loading="loading"
            row-key="reportNo"
            :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
            @change="handleTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'reportStatus'">
                <a-tag :color="reportStatusMap[record.reportStatus]?.color">{{ reportStatusMap[record.reportStatus]?.label }}</a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="settlement" tab="结售汇申报">
          <div style="margin-bottom: 12px">
            <a-space>
              <a-button type="primary" @click="handleGenerate">生成报表</a-button>
              <a-button :disabled="selectedRows.length === 0" @click="handleBatchSubmit">批量提交</a-button>
            </a-space>
          </div>
          <a-table
            :columns="settlementColumns"
            :data-source="tableData"
            :pagination="pagination"
            :loading="loading"
            row-key="reportNo"
            :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
            @change="handleTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'reportStatus'">
                <a-tag :color="reportStatusMap[record.reportStatus]?.color">{{ reportStatusMap[record.reportStatus]?.label }}</a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="capital" tab="资本项目申报">
          <div style="margin-bottom: 12px">
            <a-space>
              <a-button type="primary" @click="handleGenerate">生成报表</a-button>
              <a-button :disabled="selectedRows.length === 0" @click="handleBatchSubmit">批量提交</a-button>
            </a-space>
          </div>
          <a-table
            :columns="capitalColumns"
            :data-source="tableData"
            :pagination="pagination"
            :loading="loading"
            row-key="reportNo"
            :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
            @change="handleTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'reportStatus'">
                <a-tag :color="reportStatusMap[record.reportStatus]?.color">{{ reportStatusMap[record.reportStatus]?.label }}</a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { reportingApi } from '@/api/business'
import type { TablePaginationConfig } from 'ant-design-vue'

const reportStatusMap: Record<string, { label: string; color: string }> = {
  DRAFT: { label: '草稿', color: '#8c8c8c' },
  SUBMITTED: { label: '已提交', color: '#1677ff' },
  APPROVED: { label: '已审核', color: '#52c41a' },
  REJECTED: { label: '已退回', color: '#f5222d' }
}

interface ReportRecord {
  reportNo: string
  customerName: string
  amount: number
  reportStatus: string
  submitTime: string
}

const baseColumns = [
  { title: '报表编号', dataIndex: 'reportNo', key: 'reportNo' },
  { title: '客户名称', dataIndex: 'customerName', key: 'customerName' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '状态', dataIndex: 'reportStatus', key: 'reportStatus' },
  { title: '提交时间', dataIndex: 'submitTime', key: 'submitTime' }
]
const bopColumns = [...baseColumns]
const settlementColumns = [...baseColumns]
const capitalColumns = [...baseColumns]

const activeTab = ref('bop')
const tableData = ref<ReportRecord[]>([])
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })
const queryParams = reactive({ dateRange: undefined as any })
const selectedRowKeys = ref<string[]>([])
const selectedRows = ref<ReportRecord[]>([])

function onSelectChange(keys: string[], rows: ReportRecord[]) { selectedRowKeys.value = keys; selectedRows.value = rows }

async function fetchData() {
  loading.value = true
  try {
    const res = await reportingApi.bopPageQuery({ pageNum: pagination.current, pageSize: pagination.pageSize })
    const data = res.data.data
    tableData.value = data.records
    pagination.total = data.total
  } catch { } finally { loading.value = false }
}

function handleSearch() { pagination.current = 1; fetchData() }
function handleReset() { queryParams.dateRange = undefined; handleSearch() }
function handleTableChange(pg: TablePaginationConfig) { pagination.current = pg.current!; pagination.pageSize = pg.pageSize!; fetchData() }
function handleTabChange() { selectedRowKeys.value = []; selectedRows.value = []; fetchData() }

async function handleGenerate() {
  try {
    activeTab.value === 'bop' ? await reportingApi.createBop({}) : activeTab.value === 'settlement' ? await reportingApi.createSettlementReport({}) : await reportingApi.createCapitalReport({})
    message.success('报表生成成功')
    fetchData()
  } catch { }
}

async function handleBatchSubmit() {
  try {
    await reportingApi.submitBatch({ reportNos: selectedRows.value.map(r => r.reportNo) })
    message.success('批量提交成功')
    fetchData()
  } catch { }
}

onMounted(() => fetchData())
</script>

<style scoped>
.reporting-console h2 { margin-bottom: 16px; }
.search-card { margin-bottom: 16px; }
</style>
