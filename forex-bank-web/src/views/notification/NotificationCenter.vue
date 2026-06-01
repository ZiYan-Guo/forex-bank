<template>
  <div class="notification-center">
    <h2>通知公告</h2>

    <a-card>
      <a-tabs v-model:activeKey="activeTab">
        <a-tab-pane key="notification" tab="消息通知">
          <a-table
            :columns="notifyColumns"
            :data-source="notifyData"
            :pagination="notifyPagination"
            :loading="loading"
            row-key="id"
            @change="handleNotifyTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'notifyType'">
                <a-tag :color="notifyTypeMap[record.notifyType]?.color">{{ notifyTypeMap[record.notifyType]?.label }}</a-tag>
              </template>
              <template v-else-if="column.key === 'status'">
                <a-tag :color="record.status === 'READ' ? '#52c41a' : '#fa8c16'">{{ record.status === 'READ' ? '已读' : '未读' }}</a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="notice" tab="系统公告">
          <div style="margin-bottom: 12px">
            <a-button type="primary" @click="showCreateNotice">新建公告</a-button>
          </div>
          <a-table
            :columns="noticeColumns"
            :data-source="noticeData"
            :pagination="noticePagination"
            :loading="loading"
            row-key="id"
            @change="handleNoticeTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'noticeType'">
                <a-tag>{{ record.noticeType }}</a-tag>
              </template>
              <template v-else-if="column.key === 'publishStatus'">
                <a-tag :color="publishStatusMap[record.publishStatus]?.color">{{ publishStatusMap[record.publishStatus]?.label }}</a-tag>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <a-modal v-model:open="noticeVisible" title="新建公告" :confirm-loading="submitting" @ok="handleNoticeSubmit">
      <a-form :model="noticeForm" layout="vertical">
        <a-form-item label="标题" required>
          <a-input v-model:value="noticeForm.title" />
        </a-form-item>
        <a-form-item label="公告类型" required>
          <a-select v-model:value="noticeForm.noticeType">
            <a-select-option value="SYSTEM">系统公告</a-select-option>
            <a-select-option value="MAINTENANCE">维护公告</a-select-option>
            <a-select-option value="REGULATORY">监管公告</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="内容" required>
          <a-textarea v-model:value="noticeForm.content" :rows="4" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { TablePaginationConfig } from 'ant-design-vue'

const notifyTypeMap: Record<string, { label: string; color: string }> = {
  TRADE_ALERT: { label: '交易提醒', color: '#1677ff' },
  RISK_ALERT: { label: '风险预警', color: '#f5222d' },
  APPROVAL: { label: '审批通知', color: '#fa8c16' },
  SYSTEM: { label: '系统通知', color: '#52c41a' }
}

const publishStatusMap: Record<string, { label: string; color: string }> = {
  DRAFT: { label: '草稿', color: '#8c8c8c' },
  PUBLISHED: { label: '已发布', color: '#52c41a' },
  RECALLED: { label: '已撤回', color: '#fa8c16' }
}

interface NotifyRecord {
  id: number
  title: string
  notifyType: string
  bizNo: string
  status: string
  sendTime: string
}

interface NoticeRecord {
  id: number
  title: string
  noticeType: string
  publishStatus: string
  publishTime: string
}

const notifyColumns = [
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '通知类型', dataIndex: 'notifyType', key: 'notifyType' },
  { title: '业务编号', dataIndex: 'bizNo', key: 'bizNo' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '发送时间', dataIndex: 'sendTime', key: 'sendTime' }
]

const noticeColumns = [
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '公告类型', dataIndex: 'noticeType', key: 'noticeType' },
  { title: '发布状态', dataIndex: 'publishStatus', key: 'publishStatus' },
  { title: '发布时间', dataIndex: 'publishTime', key: 'publishTime' }
]

const notifyData = ref<NotifyRecord[]>([
  { id: 1, title: '交易TR20240001已成交', notifyType: 'TRADE_ALERT', bizNo: 'TR20240001', status: 'UNREAD', sendTime: '2024-01-15 10:30:00' },
  { id: 2, title: '风险预警：客户1001大额交易', notifyType: 'RISK_ALERT', bizNo: 'RL20240001', status: 'READ', sendTime: '2024-01-15 09:00:00' },
  { id: 3, title: '请审批支付申请', notifyType: 'APPROVAL', bizNo: 'WF20240001', status: 'UNREAD', sendTime: '2024-01-15 08:00:00' }
])

const noticeData = ref<NoticeRecord[]>([
  { id: 1, title: '系统升级通知', noticeType: 'MAINTENANCE', publishStatus: 'PUBLISHED', publishTime: '2024-01-10' },
  { id: 2, title: '外汇管理新政提醒', noticeType: 'REGULATORY', publishStatus: 'PUBLISHED', publishTime: '2024-01-08' }
])

const loading = ref(false)
const notifyPagination = reactive({ current: 1, pageSize: 10, total: 3 })
const noticePagination = reactive({ current: 1, pageSize: 10, total: 2 })

const activeTab = ref('notification')

const noticeVisible = ref(false)
const submitting = ref(false)
const noticeForm = reactive({ title: '', noticeType: 'SYSTEM', content: '' })

function handleNotifyTableChange(pg: TablePaginationConfig) { notifyPagination.current = pg.current!; notifyPagination.pageSize = pg.pageSize! }
function handleNoticeTableChange(pg: TablePaginationConfig) { noticePagination.current = pg.current!; noticePagination.pageSize = pg.pageSize! }

function showCreateNotice() { noticeForm.title = ''; noticeForm.noticeType = 'SYSTEM'; noticeForm.content = ''; noticeVisible.value = true }

function handleNoticeSubmit() {
  submitting.value = true
  message.success('公告已创建')
  noticeVisible.value = false
  submitting.value = false
}

onMounted(() => {})
</script>

<style scoped>
.notification-center h2 { margin-bottom: 16px; }
</style>
