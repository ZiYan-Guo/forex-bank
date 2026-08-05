import axios from 'axios'
import type { ApiResponse } from '@/types/api'
import { message } from 'ant-design-vue'
import router from '@/router'

const IDEMPOTENT_METHODS = new Set(['post', 'put', 'patch', 'delete'])

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  const requestId = crypto.randomUUID?.() ?? `${Date.now()}-${Math.random().toString(16).slice(2)}`
  // Keep request and trace identifiers aligned at the browser boundary.
  // 浏览器边界统一请求ID和追踪ID，便于串联网关与后端日志。
  config.headers['X-Request-Id'] = requestId
  config.headers['X-Trace-Id'] = requestId
  if (IDEMPOTENT_METHODS.has((config.method || 'get').toLowerCase())
    && !config.headers['Idempotency-Key']) {
    config.headers['Idempotency-Key'] = buildIdempotencyKey(config)
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data as ApiResponse
    if (res.code !== 200) {
      message.error(res.message || '操作失败')
      if (res.code === 401) {
        localStorage.removeItem('token')
        router.push('/login')
      }
      return Promise.reject(new Error(res.message))
    }
    return response
  },
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }
    message.error('网络请求失败')
    return Promise.reject(error)
  }
)

/**
 * Generates a deterministic key for the same write request.
 * 为相同写请求生成稳定的幂等键，支持重复点击和网络重试。
 */
function buildIdempotencyKey(config: { method?: string; url?: string; params?: unknown; data?: unknown }): string {
  const payload = stableStringify({
    method: (config.method || 'get').toLowerCase(),
    url: config.url || '',
    params: config.params,
    data: config.data
  })
  return `web-${hashString(payload)}`
}

/**
 * Keeps object key order stable before hashing.
 * 在哈希前固定对象键顺序，避免字段顺序导致幂等键变化。
 */
function stableStringify(value: unknown): string {
  if (value === undefined) {
    return 'undefined'
  }
  if (value === null || typeof value !== 'object') {
    return JSON.stringify(value) ?? String(value)
  }
  if (Array.isArray(value)) {
    return `[${value.map(item => stableStringify(item)).join(',')}]`
  }
  const record = value as Record<string, unknown>
  return `{${Object.keys(record).sort().map(key => `${JSON.stringify(key)}:${stableStringify(record[key])}`).join(',')}}`
}

function hashString(value: string): string {
  let hash = 2166136261
  for (let index = 0; index < value.length; index += 1) {
    hash ^= value.charCodeAt(index)
    hash = Math.imul(hash, 16777619)
  }
  return (hash >>> 0).toString(16)
}

export default request
