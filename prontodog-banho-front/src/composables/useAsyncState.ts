// 🔄 Composable para gerenciar estado assíncrono
// Centraliza a lógica de loading, error e success states

import { ref, computed, type Ref } from 'vue'

export interface AsyncState<T = any> {
  data: Ref<T | null>
  loading: Ref<boolean>
  error: Ref<string | null>
  execute: (asyncFn: () => Promise<T>) => Promise<T | null>
  reset: () => void
}

/**
 * Hook para gerenciar estados de operações assíncronas
 * Evita duplicação de lógica de loading/error/success
 */
export function useAsyncState<T = any>(initialData: T | null = null): AsyncState<T> {
  const data = ref(initialData) as Ref<T | null>
  const loading = ref(false)
  const error = ref<string | null>(null)

  const execute = async (asyncFn: () => Promise<T>): Promise<T | null> => {
    try {
      loading.value = true
      error.value = null

      const result = await asyncFn()
      data.value = result

      return result
    } catch (err) {
      console.error('❌ Erro na operação assíncrona:', err)
      error.value = err instanceof Error ? err.message : 'Erro inesperado'
      return null
    } finally {
      loading.value = false
    }
  }

  const reset = () => {
    data.value = initialData
    loading.value = false
    error.value = null
  }

  return {
    data,
    loading,
    error,
    execute,
    reset
  }
}

/**
 * Hook específico para listas
 * Inclui funcionalidades extras para arrays
 */
export function useAsyncList<T = any>() {
  const asyncState = useAsyncState<T[]>([])

  const isEmpty = computed(() => !asyncState.loading.value && (!asyncState.data.value || asyncState.data.value.length === 0))
  const hasData = computed(() => !asyncState.loading.value && asyncState.data.value && asyncState.data.value.length > 0)

  return {
    items: asyncState.data,
    loading: asyncState.loading,
    error: asyncState.error,
    isEmpty,
    hasData,
    execute: asyncState.execute,
    reset: asyncState.reset
  }
}
