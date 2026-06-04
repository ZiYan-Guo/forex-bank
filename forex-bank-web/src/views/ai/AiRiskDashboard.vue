<template>
  <div class="ai-risk-dashboard">
    <h2>AI智能风控仪表板</h2>

    <a-row :gutter="16" class="stat-row">
      <a-col :span="6">
        <a-card><a-statistic title="今日评估数" :value="258" /></a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="高风险" :value="12" :value-style="{ color: '#f5222d' }" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="待审核" :value="8" :value-style="{ color: '#fa8c16' }" />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic title="AI准确率" :value="94" suffix="%" :value-style="{ color: '#52c41a' }" />
        </a-card>
      </a-col>
    </a-row>

    <a-card style="margin-top:16px">
      <a-tabs v-model:activeKey="activeTab">
        <a-tab-pane key="ranking" tab="风险排序">
          <a-table
            :columns="rankingColumns"
            :data-source="rankingData"
            :pagination="{ pageSize: 10 }"
            row-key="id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'riskType'">
                <a-tag :color="record.riskTypeColor">{{ record.riskType }}</a-tag>
              </template>
              <template v-else-if="column.key === 'aiScore'">
                <a-progress
                  :percent="record.aiScore"
                  :stroke-color="record.aiScore >= 80 ? '#f5222d' : record.aiScore >= 50 ? '#fa8c16' : '#52c41a'"
                  size="small"
                />
              </template>
              <template v-else-if="column.key === 'operation'">
                <a-space>
                  <a @click="viewDetail(record)">查看详情</a>
                  <a-popconfirm title="确认已处理该风险？" @confirm="markProcessed(record)">
                    <a>标记已处理</a>
                  </a-popconfirm>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-tab-pane>

        <a-tab-pane key="network" tab="关联网络">
          <v-chart :option="networkOption" style="height:500px" />
        </a-tab-pane>

        <a-tab-pane key="blacklist" tab="黑名单匹配">
          <a-input-search
            v-model:value="searchName"
            placeholder="按名称模糊搜索..."
            style="width:400px;margin-bottom:16px"
            @search="handleSearch"
          />
          <a-table
            :columns="blacklistColumns"
            :data-source="blacklistData"
            :pagination="false"
            row-key="id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'matchScore'">
                <a-progress :percent="record.matchScore" size="small" />
              </template>
              <template v-else-if="column.key === 'operation'">
                <a-button size="small" type="primary" @click="handleMatch(record)">匹配</a-button>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <a-modal v-model:open="detailVisible" title="风险详情" :footer="null" width="700">
      <a-descriptions :column="2" bordered size="small" v-if="detailRecord">
        <a-descriptions-item label="客户名称">{{ detailRecord.customer }}</a-descriptions-item>
        <a-descriptions-item label="交易编号">{{ detailRecord.txnNo }}</a-descriptions-item>
        <a-descriptions-item label="风险类型">
          <a-tag :color="detailRecord.riskTypeColor">{{ detailRecord.riskType }}</a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="AI评分">{{ detailRecord.aiScore }}%</a-descriptions-item>
        <a-descriptions-item label="AI分析摘要" :span="2">{{ detailRecord.summary }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { GraphChart } from 'echarts/charts'
import { TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([GraphChart, TooltipComponent, CanvasRenderer])

const activeTab = ref('ranking')
const searchName = ref('')
const detailVisible = ref(false)
const detailRecord = ref<any>(null)

const rankingColumns = [
  { title: '排名', dataIndex: 'rank', key: 'rank', width: 60 },
  { title: '客户', dataIndex: 'customer', key: 'customer' },
  { title: '交易编号', dataIndex: 'txnNo', key: 'txnNo' },
  { title: '风险类型', dataIndex: 'riskType', key: 'riskType' },
  { title: 'AI评分', dataIndex: 'aiScore', key: 'aiScore', width: 180 },
  { title: 'AI分析摘要', dataIndex: 'summary', key: 'summary', ellipsis: true },
  { title: '操作', key: 'operation', width: 180 }
]

const rankingData = ref([
  { id: 1, rank: 1, customer: '张三贸易公司', txnNo: 'TXN20240601001', riskType: '洗钱嫌疑', riskTypeColor: 'red', aiScore: 92, summary: '交易金额异常，与历史模式不匹配，涉及高风险地区' },
  { id: 2, rank: 2, customer: '李四进出口公司', txnNo: 'TXN20240601002', riskType: '逃汇风险', riskTypeColor: 'orange', aiScore: 78, summary: '频繁大额购汇后立即跨境转出' },
  { id: 3, rank: 3, customer: '王五制造集团', txnNo: 'TXN20240601003', riskType: '虚假贸易', riskTypeColor: 'red', aiScore: 85, summary: '提单日期与报关单不一致，货物描述模糊' },
  { id: 4, rank: 4, customer: '赵六科技公司', txnNo: 'TXN20240601004', riskType: '合规风险', riskTypeColor: 'orange', aiScore: 65, summary: '客户尽调资料过期，经营范围与实际不符' },
  { id: 5, rank: 5, customer: '钱七供应链', txnNo: 'TXN20240601005', riskType: '汇率操纵', riskTypeColor: 'red', aiScore: 88, summary: '临近定价窗口期异常交易，价差偏离正常范围' }
])

const blacklistColumns = [
  { title: '匹配名称', dataIndex: 'matchedName', key: 'matchedName' },
  { title: '匹配度', dataIndex: 'matchScore', key: 'matchScore', width: 180 },
  { title: '来源列表', dataIndex: 'sourceList', key: 'sourceList' },
  { title: '匹配类型', dataIndex: 'matchType', key: 'matchType' },
  { title: '操作', key: 'operation', width: 100 }
]

const blacklistData = ref([
  { id: 1, matchedName: 'ZHANG SAN TRADING', matchScore: 95, sourceList: '联合国制裁名单, OFAC SDN', matchType: '精确匹配' },
  { id: 2, matchedName: 'LI SI IMPORT EXPORT', matchScore: 88, sourceList: 'FBI通缉名单', matchType: '模糊匹配' },
  { id: 3, matchedName: 'WANG WU GROUP LTD', matchScore: 72, sourceList: '中国公安部', matchType: '关联匹配' }
])

const networkOption = {
  tooltip: { show: true },
  series: [
    {
      type: 'graph',
      layout: 'force',
      roam: true,
      draggable: true,
      label: { show: true, fontSize: 12 },
      force: { repulsion: 300, edgeLength: 150 },
      data: [
        { name: '张三贸易', symbolSize: 40, itemStyle: { color: '#f5222d' } },
        { name: '李四进出口', symbolSize: 40, itemStyle: { color: '#f5222d' } },
        { name: '王五集团', symbolSize: 30, itemStyle: { color: '#1677ff' } },
        { name: '赵六科技', symbolSize: 30, itemStyle: { color: '#1677ff' } },
        { name: '钱七供应链', symbolSize: 30, itemStyle: { color: '#1677ff' } },
        { name: '海外A公司', symbolSize: 25, itemStyle: { color: '#d9d9d9' } },
        { name: '海外B公司', symbolSize: 25, itemStyle: { color: '#d9d9d9' } },
        { name: '香港账户', symbolSize: 35, itemStyle: { color: '#fa8c16' } }
      ],
      links: [
        { source: '张三贸易', target: '海外A公司', lineStyle: { color: '#f5222d', width: 2 } },
        { source: '李四进出口', target: '海外B公司', lineStyle: { color: '#f5222d', width: 2 } },
        { source: '张三贸易', target: '香港账户', lineStyle: { color: '#f5222d', width: 3 } },
        { source: '王五集团', target: '张三贸易', lineStyle: { color: '#fa8c16' } },
        { source: '赵六科技', target: '海外A公司', lineStyle: { color: '#d9d9d9' } },
        { source: '钱七供应链', target: '海外B公司', lineStyle: { color: '#d9d9d9' } },
        { source: '香港账户', target: '海外A公司', lineStyle: { color: '#f5222d' } }
      ]
    }
  ]
}

function viewDetail(record: any) {
  detailRecord.value = record
  detailVisible.value = true
}

function markProcessed(record: any) {
  message.success(`交易 ${record.txnNo} 已标记为已处理`)
}

function handleSearch() {
  message.info(`搜索: ${searchName.value}`)
}

function handleMatch(record: any) {
  message.success(`已匹配: ${record.matchedName}`)
}
</script>

<style scoped>
.ai-risk-dashboard h2 { margin-bottom: 16px; }
.stat-row { margin-bottom: 0; }
</style>
