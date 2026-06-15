<template>
  <!-- 资金池仪表板 Cash Pool Dashboard -->
  <div class="cashpool-dashboard">
    <h3 class="page-title">资金池仪表板</h3>

    <!-- Stat cards: pool count, total limit, used limit, usage rate 统计卡片 -->
    <a-row :gutter="16" class="stat-row">
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic title="资金池数量 Pool Count" :value="poolCount">
            <template #prefix><wallet-outlined style="color:#1677ff" /></template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic title="总额度 Total Limit" :value="totalLimit" prefix="¥">
            <template #prefix><fund-outlined style="color:#52c41a" /></template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic title="已用额度 Used Limit" :value="usedLimit" prefix="¥">
            <template #prefix><pie-chart-outlined style="color:#fa8c16" /></template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic title="使用率 Usage Rate" :value="usageRate" suffix="%" :precision="1">
            <template #prefix><percentage-outlined style="color:#722ed1" /></template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <!-- Middle row: pool overview table + pie chart 中间：资金池总览表+饼图 -->
    <a-row :gutter="16" class="content-row">
      <a-col :span="16">
        <a-card title="资金池总览 Pool Overview" size="small">
          <a-table :columns="poolColumns" :data-source="poolList" :pagination="false" row-key="poolName" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'usagePct'">
                <a-progress :percent="record.usagePct" :size="16" :stroke-color="record.usagePct > 80 ? '#f5222d' : '#1677ff'" />
              </template>
              <template v-if="column.key === 'status'">
                <a-tag :color="record.status === 'ACTIVE' ? 'green' : 'red'">{{ record.status === 'ACTIVE' ? '生效中' : '已冻结' }}</a-tag>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card title="资金池使用分布 Pool Usage Breakdown" size="small">
          <div ref="pieChartRef" style="width:100%;height:260px"></div>
          <a-empty v-if="poolList.length === 0" :image="aEmpty.PRESENTED_IMAGE_SIMPLE" description="暂无资金池数据" />
        </a-card>
      </a-col>
    </a-row>

    <!-- Bottom: member management table 底部：成员管理表 -->
    <a-row :gutter="16" class="member-row">
      <a-col :span="24">
        <a-card title="成员管理 Member Management" size="small">
          <template #extra>
            <a-button type="primary" size="small" @click="showAddModal = true">
              <plus-outlined /> 添加成员
            </a-button>
          </template>
          <a-table :columns="memberColumns" :data-source="memberList" :pagination="false" row-key="memberAccountId" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'memberType'">
                <a-tag :color="record.memberType === 'DOMESTIC' ? 'blue' : 'orange'">
                  {{ record.memberType === 'DOMESTIC' ? '境内' : '境外' }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>

    <!-- Add member modal 添加成员弹窗 -->
    <a-modal v-model:open="showAddModal" title="添加成员 Add Member" @ok="handleAddMember" @cancel="showAddModal = false">
      <a-form :model="newMember" layout="vertical">
        <a-form-item label="成员账号 Member Account">
          <a-input v-model:value="newMember.memberAccountId" placeholder="请输入成员账号" />
        </a-form-item>
        <a-form-item label="成员类型 Member Type">
          <a-select v-model:value="newMember.memberType">
            <a-select-option value="DOMESTIC">境内 DOMESTIC</a-select-option>
            <a-select-option value="OVERSEAS">境外 OVERSEAS</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="结算模式 Settlement Mode">
          <a-select v-model:value="newMember.settlementMode">
            <a-select-option value="NET">净额结算 NET</a-select-option>
            <a-select-option value="GROSS">全额结算 GROSS</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="贡献额度 Contribution Limit">
          <a-input-number v-model:value="newMember.contributionLimit" style="width:100%" :min="0" placeholder="请输入贡献额度" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import { Empty } from 'ant-design-vue'
import * as echarts from 'echarts'

const aEmpty = Empty

// Pool statistics 资金池统计数据
const poolCount = ref(3)
const totalLimit = ref(5000000000) // ¥50亿
const usedLimit = ref(3200000000) // ¥32亿
const usageRate = ref(64) // 64%

// Pool overview table 资金池总览表
const poolColumns = [
  { title: '资金池名称 Pool Name', dataIndex: 'poolName', key: 'poolName' },
  { title: '币种 Currency', dataIndex: 'currency', key: 'currency' },
  { title: '总额度 Total Limit(¥)', dataIndex: 'totalLimit', key: 'totalLimit' },
  { title: '已用 Used(¥)', dataIndex: 'used', key: 'used' },
  { title: '使用率 Usage%', dataIndex: 'usagePct', key: 'usagePct' },
  { title: '状态 Status', dataIndex: 'status', key: 'status' }
]

const poolList = ref([
  { poolName: '跨境资金池 CN-US Cash Pool', currency: 'USD/CNY', totalLimit: '¥20亿', used: '¥13亿', usagePct: 65, status: 'ACTIVE' },
  { poolName: '亚太资金池 Asia-Pacific Pool', currency: 'JPY/CNY', totalLimit: '¥15亿', used: '¥10亿', usagePct: 67, status: 'ACTIVE' },
  { poolName: '欧洲资金池 Europe Cash Pool', currency: 'EUR/CNY', totalLimit: '¥15亿', used: '¥9亿', usagePct: 60, status: 'ACTIVE' }
])

// Member management table 成员管理表
const memberColumns = [
  { title: '成员账号 Member ID', dataIndex: 'memberAccountId', key: 'memberAccountId' },
  { title: '成员类型 Member Type', dataIndex: 'memberType', key: 'memberType' },
  { title: '结算模式 Settlement Mode', dataIndex: 'settlementMode', key: 'settlementMode' },
  { title: '贡献额度 Contribution Limit(¥)', dataIndex: 'contributionLimit', key: 'contributionLimit' }
]

const memberList = ref([
  { memberAccountId: 'ACC-001', memberType: 'DOMESTIC', settlementMode: '净额结算 NET', contributionLimit: '¥8亿' },
  { memberAccountId: 'ACC-002', memberType: 'DOMESTIC', settlementMode: '净额结算 NET', contributionLimit: '¥5亿' },
  { memberAccountId: 'ACC-003', memberType: 'OVERSEAS', settlementMode: '全额结算 GROSS', contributionLimit: '¥3亿' },
  { memberAccountId: 'ACC-004', memberType: 'OVERSEAS', settlementMode: '净额结算 NET', contributionLimit: '¥4亿' }
])

// Add member modal 添加成员弹窗
const showAddModal = ref(false)
const newMember = ref({ memberAccountId: '', memberType: 'DOMESTIC', settlementMode: 'NET', contributionLimit: 0 })

function handleAddMember() {
  memberList.value.push({
    memberAccountId: newMember.value.memberAccountId || 'ACC-NEW',
    memberType: newMember.value.memberType,
    settlementMode: newMember.value.settlementMode === 'NET' ? '净额结算 NET' : '全额结算 GROSS',
    contributionLimit: `¥${newMember.value.contributionLimit / 100000000}亿`
  })
  showAddModal.value = false
}

// ECharts pie chart: pool usage breakdown 饼图：资金池使用分布
const pieChartRef = ref<HTMLElement | null>(null)
let pieChart: echarts.ECharts | null = null

function initPieChart() {
  if (!pieChartRef.value) return
  pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: ¥{c}亿 ({d}%)' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
      label: { show: true, formatter: '{b}\n{d}%' },
      data: [
        { value: 13, name: '跨境资金池 CN-US' },
        { value: 10, name: '亚太资金池 Asia-Pacific' },
        { value: 9, name: '欧洲资金池 Europe' },
        { value: 18, name: '剩余额度 Remaining' }
      ]
    }]
  })
}

onMounted(() => {
  nextTick(() => initPieChart())
})
</script>

<style scoped>
.cashpool-dashboard { background: #fff; border-radius: 8px; padding: 24px; }
.page-title { margin: 0 0 20px; font-size: 18px; color: #333; font-weight: 600; }
.stat-row { margin-bottom: 16px; }
.content-row { margin-bottom: 16px; }
.member-row { margin-top: 16px; }
</style>
