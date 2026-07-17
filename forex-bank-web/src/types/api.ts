export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  traceId: string
  timestamp: number
}

export interface PageResp<T> {
  total: number
  records: T[]
  pageNum: number
  pageSize: number
  totalPages: number
}

export interface PageReq {
  pageNum?: number
  pageSize?: number
}

export interface TokenInfo {
  accessToken: string
  refreshToken: string
  expiresIn: number
  userId: number
  username: string
  realName: string
}

export interface UserInfo {
  id: number
  username: string
  realName: string
  roles: string[]
  permissions: string[]
}

export interface ExchangeRate {
  id?: number
  currencyPair: string
  baseCurrency?: string
  quoteCurrency?: string
  bidRate: number
  askRate: number
  midRate: number
  spread?: number
  rateDate?: string
  rateTime: string
  rateSource: string
  status?: number
}

export interface RiskLevel { LOW: 1; MEDIUM: 2; HIGH: 3; PROHIBITED: 9 }
export const RiskLevelMap: Record<number, { label: string; color: string }> = {
  1: { label: '低风险', color: '#52c41a' },
  2: { label: '中风险', color: '#fa8c16' },
  3: { label: '高风险', color: '#f5222d' },
  9: { label: '禁止类', color: '#8c8c8c' }
}

export const OrderStatusMap: Record<string, { label: string; color: string }> = {
  PENDING: { label: '待确认', color: '#fa8c16' },
  CONFIRMED: { label: '已确认', color: '#1677ff' },
  PROCESSING: { label: '处理中', color: '#1677ff' },
  SUCCESS: { label: '成功', color: '#52c41a' },
  FAILED: { label: '失败', color: '#f5222d' },
  CANCELLED: { label: '已取消', color: '#8c8c8c' },
  REVERSED: { label: '已冲正', color: '#8c8c8c' }
}

export interface MenuItem {
  key: string
  label: string
  icon?: string
  children?: MenuItem[]
  permission?: string
}
