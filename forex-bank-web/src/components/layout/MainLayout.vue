<template>
  <a-layout style="height:100vh">
    <a-layout-sider v-model:collapsed="collapsed" collapsible theme="dark" width="220">
      <div class="logo">
        <img src="/vite.svg" width="32" height="32" />
        <span v-show="!collapsed">银行外汇系统</span>
      </div>
      <a-menu theme="dark" mode="inline" v-model:selectedKeys="selectedKeys" @click="onMenuClick">
        <a-menu-item key="/dashboard"><dashboard-outlined /> 工作台</a-menu-item>
        <a-sub-menu key="trade" title="交易业务">
          <template #icon><dollar-outlined /></template>
          <a-menu-item key="/exchange">结售汇</a-menu-item>
          <a-menu-item key="/trading">外汇买卖</a-menu-item>
          <a-menu-item key="/payment">跨境支付</a-menu-item>
        </a-sub-menu>
        <a-sub-menu key="settle" title="国际结算">
          <template #icon><file-text-outlined /></template>
          <a-menu-item key="/settlement/lc">信用证</a-menu-item>
          <a-menu-item key="/settlement/collection">跟单托收</a-menu-item>
          <a-menu-item key="/settlement/guarantee">国际保函</a-menu-item>
        </a-sub-menu>
        <a-sub-menu key="risk-mgmt" title="风控管理">
          <template #icon><alert-outlined /></template>
          <a-menu-item key="/risk">风险监测</a-menu-item>
          <a-menu-item key="/position">敞口管理</a-menu-item>
          <a-menu-item key="/valuation">衍生品估值</a-menu-item>
          <a-menu-item key="/margin">保证金</a-menu-item>
        </a-sub-menu>
        <a-sub-menu key="back-office" title="后台管理">
          <template #icon><setting-outlined /></template>
          <a-menu-item key="/customer">客户管理</a-menu-item>
          <a-menu-item key="/account">账户管理</a-menu-item>
          <a-menu-item key="/bookkeeping">簿记核算</a-menu-item>
          <a-menu-item key="/clearing">清算管理</a-menu-item>
          <a-menu-item key="/reporting">监管报送</a-menu-item>
        </a-sub-menu>
        <a-sub-menu key="tools" title="系统工具">
          <template #icon><tool-outlined /></template>
          <a-menu-item key="/workflow">工作流审批</a-menu-item>
          <a-menu-item key="/notification">通知公告</a-menu-item>
          <a-menu-item key="/ocr">OCR识别</a-menu-item>
          <a-menu-item key="/schedule">定时任务</a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="top-bar">
        <div class="rate-ticker">
          <span v-for="r in rateStore.rates.slice(0,6)" :key="r.currencyPair" class="rate-item">
            {{ r.currencyPair }} <span :class="r.askRate > r.midRate ? 'up' : 'down'">{{ r.askRate?.toFixed(4) }}</span>
          </span>
        </div>
        <div class="top-actions">
          <a-badge :count="5" size="small"><bell-outlined style="font-size:18px;cursor:pointer" /></a-badge>
          <a-dropdown>
            <span style="cursor:pointer;margin-left:16px">
              <user-outlined /> {{ userStore.userInfo?.realName || '管理员' }}
            </span>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="handleLogout">退出登录</a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
      <a-layout-content class="main-content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useRateStore } from '@/store/rate'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const rateStore = useRateStore()
const collapsed = ref(false)
const selectedKeys = ref<string[]>([route.path])

onMounted(() => rateStore.startPolling())
onUnmounted(() => rateStore.stopPolling())

function onMenuClick({ key }: { key: string }) {
  selectedKeys.value = [key]
  router.push(key)
}

async function handleLogout() {
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.logo { height: 48px; display: flex; align-items: center; justify-content: center; gap: 8px; color: #fff; font-size: 16px; font-weight: bold; }
.top-bar { background: #fff; padding: 0 24px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #f0f0f0; height: 48px; line-height: 48px; }
.rate-ticker { display: flex; gap: 20px; overflow: hidden; }
.rate-item { font-size: 13px; font-family: 'Courier New', monospace; white-space: nowrap; }
.up { color: #f5222d; }
.down { color: #52c41a; }
.top-actions { display: flex; align-items: center; gap: 16px; }
.main-content { margin: 16px; padding: 16px; background: #f5f7fa; min-height: calc(100vh - 80px); overflow: auto; }
</style>
