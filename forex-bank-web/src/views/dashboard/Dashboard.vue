<template>
  <div class="dashboard">
    <h3 class="page-title">工作台</h3>

    <a-row :gutter="16" class="stat-row">
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic title="今日交易笔数" :value="todayTradeCount">
            <template #prefix><swap-outlined style="color:#1677ff" /></template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic title="今日交易金额" :value="todayTradeAmount" :precision="2" prefix="¥">
            <template #prefix><dollar-outlined style="color:#52c41a" /></template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic title="待审批任务" :value="pendingApprovals">
            <template #suffix><a-badge :count="5" style="margin-left:8px" /></template>
            <template #prefix><clock-circle-outlined style="color:#fa8c16" /></template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable>
          <a-statistic title="风险预警" :value="riskAlerts" value-style="color:#f5222d">
            <template #suffix><a-badge :count="2" color="#f5222d" style="margin-left:8px" /></template>
            <template #prefix><alert-outlined style="color:#f5222d" /></template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" class="content-row">
      <a-col :span="16">
        <a-card title="最近交易" size="small">
          <a-table :columns="tradeColumns" :data-source="recentTrades" :pagination="false" row-key="id" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'type'">
                <a-tag :color="record.type === '结汇' ? 'blue' : 'green'">{{ record.type }}</a-tag>
              </template>
              <template v-if="column.key === 'status'">
                <a-tag :color="statusMap[record.status]?.color">{{ statusMap[record.status]?.label }}</a-tag>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card title="实时牌价" size="small">
          <div v-for="r in rateStore.rates.slice(0, 6)" :key="r.currencyPair" class="rate-row">
            <span class="rate-pair">{{ r.currencyPair }}</span>
            <span class="rate-ask" :class="r.askRate > r.midRate ? 'up' : 'down'">
              {{ r.askRate?.toFixed(4) }}
            </span>
            <span class="rate-bid">{{ r.bidRate?.toFixed(4) }}</span>
          </div>
          <a-empty v-if="rateStore.rates.length === 0" :image="aEmpty.PRESENTED_IMAGE_SIMPLE" description="暂无牌价数据" />
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" class="quick-row">
      <a-col :span="6">
        <a-card hoverable class="quick-card" @click="router.push('/exchange')">
          <dollar-outlined style="font-size:28px;color:#1677ff" />
          <span class="quick-label">办理结售汇</span>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable class="quick-card" @click="router.push('/payment')">
          <send-outlined style="font-size:28px;color:#52c41a" />
          <span class="quick-label">跨境汇款</span>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable class="quick-card" @click="router.push('/settlement/lc')">
          <file-text-outlined style="font-size:28px;color:#fa8c16" />
          <span class="quick-label">开立信用证</span>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card hoverable class="quick-card" @click="router.push('/customer')">
          <team-outlined style="font-size:28px;color:#722ed1" />
          <span class="quick-label">客户查询</span>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useRateStore } from '@/store/rate'
import { Empty } from 'ant-design-vue'

const router = useRouter()
const rateStore = useRateStore()
const aEmpty = Empty

const todayTradeCount = ref(Math.floor(Math.random() * 129) + 128)
const todayTradeAmount = ref(Math.floor(Math.random() * 5000000) + 3000000)
const pendingApprovals = ref(5)
const riskAlerts = ref(2)

const statusMap: Record<string, { label: string; color: string }> = {
  SUCCESS: { label: '成功', color: '#52c41a' },
  PROCESSING: { label: '处理中', color: '#1677ff' },
  PENDING: { label: '待确认', color: '#fa8c16' },
  CANCELLED: { label: '已取消', color: '#8c8c8c' }
}

const tradeColumns = [
  { title: '交易编号', dataIndex: 'id', key: 'id' },
  { title: '类型', dataIndex: 'type', key: 'type' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '时间', dataIndex: 'time', key: 'time' }
]

const recentTrades = ref([
  { id: 'FX20260501-001', type: '结汇', amount: '¥1,250,000.00', status: 'SUCCESS', time: '2026-06-01 09:30:12' },
  { id: 'FX20260501-002', type: '售汇', amount: '¥890,000.00', status: 'PROCESSING', time: '2026-06-01 09:45:33' },
  { id: 'FX20260501-003', type: '结汇', amount: '¥520,000.00', status: 'SUCCESS', time: '2026-06-01 10:12:08' },
  { id: 'FX20260501-004', type: '售汇', amount: '¥1,780,000.00', status: 'PENDING', time: '2026-06-01 10:28:45' },
  { id: 'FX20260501-005', type: '结汇', amount: '¥340,000.00', status: 'CANCELLED', time: '2026-06-01 10:55:21' }
])

onMounted(() => rateStore.startPolling())
onUnmounted(() => rateStore.stopPolling())
</script>

<style scoped>
.dashboard {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.page-title {
  margin: 0 0 20px;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.stat-row {
  margin-bottom: 16px;
}

.content-row {
  margin-bottom: 16px;
}

.quick-row {
  margin-top: 16px;
}

.rate-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px 0;
  border-bottom: 1px solid #f5f5f5;
  font-family: 'Courier New', monospace;
  font-size: 13px;
}

.rate-row:last-child {
  border-bottom: none;
}

.rate-pair {
  font-weight: bold;
  min-width: 60px;
  color: #333;
}

.rate-ask {
  margin-left: auto;
}

.rate-ask.up { color: #f5222d; }
.rate-ask.down { color: #52c41a; }

.rate-bid {
  color: #888;
  min-width: 70px;
  text-align: right;
}

.quick-card {
  cursor: pointer;
  text-align: center;
  padding: 8px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.quick-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.quick-label {
  font-size: 14px;
  color: #555;
}
</style>
