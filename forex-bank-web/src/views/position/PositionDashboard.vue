<template>
  <div class="position-dashboard">
    <h2>敞口管理</h2>

    <a-row :gutter="16">
      <a-col :span="8">
        <a-card title="币种敞口分布">
          <v-chart :option="pieOption" style="height: 300px" autoresize />
        </a-card>
      </a-col>
      <a-col :span="16">
        <a-card>
          <div style="margin-bottom: 12px">
            <a-button type="primary" @click="handleCheckBreach">检查超限</a-button>
          </div>
          <a-table
            :columns="columns"
            :data-source="tableData"
            :pagination="pagination"
            :loading="loading"
            row-key="positionNo"
            @change="handleTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'limitUsagePct'">
                <a-progress :percent="record.limitUsagePct" :stroke-color="record.limitUsagePct > 80 ? '#f5222d' : '#52c41a'" size="small" />
              </template>
              <template v-else-if="column.key === 'riskLevel'">
                <a-tag :color="RiskLevelMap[record.riskLevel]?.color">{{ RiskLevelMap[record.riskLevel]?.label }}</a-tag>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import VChart from 'vue-echarts'
import * as echarts from 'echarts/core'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { positionApi } from '@/api/business'
import { RiskLevelMap } from '@/types/api'
import type { TablePaginationConfig } from 'ant-design-vue'

echarts.use([PieChart, TitleComponent, TooltipComponent, CanvasRenderer])

interface PositionRecord {
  positionNo: string
  currencyPair: string
  longAmount: number
  shortAmount: number
  netPosition: number
  positionLimit: number
  limitUsagePct: number
  riskLevel: number
}

const columns = [
  { title: '敞口编号', dataIndex: 'positionNo', key: 'positionNo' },
  { title: '币种对', dataIndex: 'currencyPair', key: 'currencyPair' },
  { title: '多头金额', dataIndex: 'longAmount', key: 'longAmount' },
  { title: '空头金额', dataIndex: 'shortAmount', key: 'shortAmount' },
  { title: '净敞口', dataIndex: 'netPosition', key: 'netPosition' },
  { title: '限额', dataIndex: 'positionLimit', key: 'positionLimit' },
  { title: '限额使用率', dataIndex: 'limitUsagePct', key: 'limitUsagePct' },
  { title: '风险等级', dataIndex: 'riskLevel', key: 'riskLevel' }
]

const tableData = ref<PositionRecord[]>([])
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 0, pageSizeOptions: ['10'] as string[] })

const pieOption = computed(() => ({
  title: { text: '币种敞口分布', left: 'center', textStyle: { fontSize: 14 } },
  tooltip: { trigger: 'item' },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    data: tableData.value.map(r => ({ name: r.currencyPair, value: Math.abs(r.netPosition) }))
  }]
}))

async function fetchData() {
  loading.value = true
  try {
    const res = await positionApi.pageQuery({ pageNum: pagination.current, pageSize: pagination.pageSize })
    const data = res.data.data
    tableData.value = data.records
    pagination.total = data.total
  } catch { } finally { loading.value = false }
}

function handleTableChange(pg: TablePaginationConfig) { pagination.current = pg.current!; pagination.pageSize = pg.pageSize!; fetchData() }

async function handleCheckBreach() {
  try {
    const res = await positionApi.checkBreach()
    message.success('超限检查完成')
  } catch { }
}

onMounted(() => fetchData())
</script>

<style scoped>
.position-dashboard h2 { margin-bottom: 16px; }
</style>
