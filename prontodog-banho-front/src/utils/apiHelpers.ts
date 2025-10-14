// 🛠️ Helpers para API
// Centraliza tratamento de erros e logging

import type { AxiosError } from 'axios'

/**
 * Interface para resposta de erro da API
 */
export interface ApiError {
  status: number
  message: string
}

/**
 * Mapeia códigos de status HTTP para mensagens de erro amigáveis
 */
const ERROR_MESSAGES: Record<number, string> = {
  400: 'Dados inválidos. Verifique as informações e tente novamente.',
  401: 'Você não tem permissão para realizar esta ação.',
  403: 'Acesso negado.',
  404: 'Recurso não encontrado.',
  409: 'Este item já existe no sistema.',
  422: 'Dados inválidos. Verifique as informações.',
  500: 'Erro interno do servidor. Tente novamente mais tarde.',
  503: 'Serviço temporariamente indisponível.'
}

/**
 * Trata erros HTTP de forma padronizada
 * @param error Erro do axios
 * @param fallbackMessage Mensagem padrão se não houver mapeamento
 * @returns Erro formatado para exibição
 */
export const handleApiError = (error: unknown, fallbackMessage = 'Erro inesperado'): never => {
  const axiosError = error as AxiosError

  // Log apenas em desenvolvimento
  if (import.meta.env.DEV) {
    console.error('❌ API Error:', axiosError.response?.status, axiosError.message)
  }

  const status = axiosError.response?.status || 0
  const message = ERROR_MESSAGES[status] || fallbackMessage

  throw new Error(message)
}

/**
 * Log apenas em desenvolvimento
 * @param message Mensagem para logar
 * @param data Dados adicionais (opcional)
 */
export const devLog = (message: string, data?: any): void => {
  if (import.meta.env.DEV) {
    if (data) {
      console.log(message, data)
    } else {
      console.log(message)
    }
  }
}

/**
 * Wrapper para operações assíncronas com tratamento de erro padronizado
 * @param operation Função assíncrona para executar
 * @param errorMessage Mensagem de erro customizada
 * @returns Resultado da operação
 */
export const withErrorHandling = async <T>(
  operation: () => Promise<T>,
  errorMessage?: string
): Promise<T> => {
  try {
    return await operation()
  } catch (error) {
    return handleApiError(error, errorMessage)
  }
}

/**
 * Valida se um número é um ID válido
 * @param id ID para validar
 * @throws Error se ID inválido
 */
export const validateId = (id: number): void => {
  if (!id || id <= 0 || !Number.isInteger(id)) {
    throw new Error('ID inválido')
  }
  return
}

/**
 * Mensagens de sucesso padronizadas
 */
export const SUCCESS_MESSAGES = {
  CREATED: 'Item criado com sucesso!',
  UPDATED: 'Item atualizado com sucesso!',
  DELETED: 'Item excluído com sucesso!',
  LOADED: 'Dados carregados com sucesso!'
} as const
