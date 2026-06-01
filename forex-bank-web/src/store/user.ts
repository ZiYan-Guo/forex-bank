import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { TokenInfo } from '@/types/api'
import { authApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<TokenInfo | null>(null)

  const isLoggedIn = () => !!token.value

  const login = async (username: string, password: string) => {
    const res = await authApi.login(username, password)
    const info = res.data.data
    token.value = info.accessToken
    userInfo.value = info
    localStorage.setItem('token', info.accessToken)
    localStorage.setItem('refreshToken', info.refreshToken)
    return info
  }

  const logout = async () => {
    try { await authApi.logout() } catch {}
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
  }

  return { token, userInfo, isLoggedIn, login, logout }
})
