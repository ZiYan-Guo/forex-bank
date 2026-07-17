import { defineStore } from 'pinia'
import { ref } from 'vue'
import { rateApi } from '@/api/exchange'

interface RateItem {
  currencyPair: string
  bidRate: number
  askRate: number
  midRate: number
  rateTime: string
}

export const useRateStore = defineStore('rate', () => {
  const rates = ref<RateItem[]>([])
  const loading = ref(false)
  let timer: ReturnType<typeof setInterval> | null = null

  const fetchRates = async () => {
    loading.value = true
    try {
      const res = await rateApi.getAll()
      rates.value = res.data.data || []
    } catch (error) {
      console.error('[rate] fetch latest rates failed', error)
    } finally {
      loading.value = false
    }
  }

  const startPolling = (intervalMs = 10000) => {
    fetchRates()
    timer = setInterval(fetchRates, intervalMs)
  }

  const stopPolling = () => {
    if (timer) { clearInterval(timer); timer = null }
  }

  return { rates, loading, fetchRates, startPolling, stopPolling }
})
