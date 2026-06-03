import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/components/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/Dashboard.vue'), meta: { title: '工作台', icon: 'dashboard' } },
      { path: 'customer', name: 'Customer', component: () => import('@/views/customer/CustomerList.vue'), meta: { title: '客户管理', icon: 'team' } },
      { path: 'exchange', name: 'Exchange', component: () => import('@/views/exchange/ExchangeTrade.vue'), meta: { title: '结售汇', icon: 'dollar' } },
      { path: 'trading', name: 'Trading', component: () => import('@/views/trading/TradingList.vue'), meta: { title: '外汇买卖', icon: 'swap' } },
      { path: 'payment', name: 'Payment', component: () => import('@/views/payment/PaymentForm.vue'), meta: { title: '跨境支付', icon: 'send' } },
      { path: 'payment/batch', name: 'BatchPayment', component: () => import('@/views/payment/BatchPayment.vue'), meta: { title: '批量汇款', icon: 'unordered-list' } },
      { path: 'payment/route', name: 'RouteOptimizer', component: () => import('@/views/payment/RouteOptimizer.vue'), meta: { title: '路由优选', icon: 'branches' } },
      { path: 'settlement/lc', name: 'LetterOfCredit', component: () => import('@/views/settlement/LetterOfCredit.vue'), meta: { title: '信用证', icon: 'file-text' } },
      { path: 'settlement/collection', name: 'DocumentaryCollection', component: () => import('@/views/settlement/DocumentaryCollection.vue'), meta: { title: '跟单托收', icon: 'file-done' } },
      { path: 'settlement/guarantee', name: 'BankGuarantee', component: () => import('@/views/settlement/BankGuarantee.vue'), meta: { title: '国际保函', icon: 'safety' } },
      { path: 'risk', name: 'Risk', component: () => import('@/views/risk/RiskMonitor.vue'), meta: { title: '风险监测', icon: 'alert' } },
      { path: 'reporting', name: 'Reporting', component: () => import('@/views/reporting/ReportingConsole.vue'), meta: { title: '监管报送', icon: 'file-excel' } },
      { path: 'position', name: 'Position', component: () => import('@/views/position/PositionDashboard.vue'), meta: { title: '敞口管理', icon: 'fund' } },
      { path: 'position/heatmap', name: 'ExposureHeatmap', component: () => import('@/views/position/ExposureHeatmap.vue'), meta: { title: '敞口分析', icon: 'heat-map' } },
      { path: 'account', name: 'Account', component: () => import('@/views/account/AccountList.vue'), meta: { title: '账户管理', icon: 'bank' } },
      { path: 'valuation', name: 'Valuation', component: () => import('@/views/valuation/ValuationList.vue'), meta: { title: '衍生品估值', icon: 'calculator' } },
      { path: 'valuation/pnl', name: 'PnlAttribution', component: () => import('@/views/valuation/PnlAttribution.vue'), meta: { title: '损益归因', icon: 'line-chart' } },
      { path: 'margin', name: 'Margin', component: () => import('@/views/margin/MarginList.vue'), meta: { title: '保证金', icon: 'lock' } },
      { path: 'bookkeeping', name: 'Bookkeeping', component: () => import('@/views/bookkeeping/JournalEntry.vue'), meta: { title: '簿记核算', icon: 'book' } },
      { path: 'bookkeeping/closing', name: 'MonthEndClosing', component: () => import('@/views/bookkeeping/MonthEndClosing.vue'), meta: { title: '月末结账', icon: 'check-circle' } },
      { path: 'clearing', name: 'Clearing', component: () => import('@/views/clearing/ClearingList.vue'), meta: { title: '清算', icon: 'reconciliation' } },
      { path: 'clearing/recon', name: 'ReconciliationBoard', component: () => import('@/views/clearing/ReconciliationBoard.vue'), meta: { title: '对账管理', icon: 'audit' } },
      { path: 'clearing/confirmation', name: 'ConfirmationBoard', component: () => import('@/views/clearing/ConfirmationBoard.vue'), meta: { title: '确认匹配看板', icon: 'check-square' } },
      { path: 'clearing/tracker', name: 'SettlementTracker', component: () => import('@/views/clearing/SettlementTracker.vue'), meta: { title: '结算追踪', icon: 'radar-chart' } },
      { path: 'clearing/dashboard', name: 'SettlementDashboard', component: () => import('@/views/clearing/SettlementDashboard.vue'), meta: { title: '结算仪表板', icon: 'dashboard' } },
      { path: 'workflow', name: 'Workflow', component: () => import('@/views/workflow/WorkflowList.vue'), meta: { title: '工作流', icon: 'apartment' } },
      { path: 'notification', name: 'Notification', component: () => import('@/views/notification/NotificationCenter.vue'), meta: { title: '通知公告', icon: 'bell' } },
      { path: 'ocr', name: 'Ocr', component: () => import('@/views/ocr/OcrUpload.vue'), meta: { title: 'OCR识别', icon: 'scan' } },
      { path: 'schedule', name: 'Schedule', component: () => import('@/views/schedule/JobManagement.vue'), meta: { title: '定时任务', icon: 'clock-circle' } },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
