import axios from 'axios'
import type { ApiResponse } from '@/types/api'
import { message } from 'ant-design-vue'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  config.headers['X-Request-Id'] = crypto.randomUUID?.() ?? Date.now().toString()
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

export default request
