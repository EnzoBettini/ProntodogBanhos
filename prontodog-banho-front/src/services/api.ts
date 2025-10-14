// 📁 services/api.ts
// Este arquivo contém toda a configuração e métodos para comunicação com a API
// É aqui que centralizamos todas as chamadas HTTP

import axios from 'axios'
import type { ClientesResponse, Cliente, NovoCliente, Animal, NovoAnimal } from '@/types/api'
import { handleApiError, devLog, withErrorHandling, validateId } from '@/utils/apiHelpers'

// 🔧 CONFIGURAÇÃO DO AXIOS
// Criamos uma instância do axios com configurações padrão
const api = axios.create({
  baseURL: 'http://localhost:8080', // URL base da nossa API Spring Boot
  timeout: 10000, // Timeout de 10 segundos para as requisições
  headers: {
    'Content-Type': 'application/json', // Sempre enviar/receber JSON
  }
})

// 🔍 INTERCEPTADORES
// Intercepta requisições para logging em desenvolvimento
api.interceptors.request.use(
  (config) => {
    devLog('🚀 Fazendo requisição para:', `${config.baseURL || ''}${config.url || ''}`)
    return config
  },
  (error) => Promise.reject(error)
)

// Intercepta respostas para tratamento global
api.interceptors.response.use(
  (response) => {
    devLog('✅ Resposta recebida:', response.status)
    return response
  },
  (error) => {
    // Tratamento global de erros de autenticação
    if (error.response?.status === 401) {
      devLog('🔐 Usuário não autorizado')
      // TODO: Redirecionar para login quando implementado
    }
    return Promise.reject(error)
  }
)

// 🐕 SERVIÇOS DE ANIMAIS
// Aqui ficam todos os métodos relacionados aos animais
export const animaisService = {

  // 📖 BUSCAR TODOS OS ANIMAIS
  // GET /animal
  async buscarTodos(): Promise<Animal[]> {
    return withErrorHandling(async () => {
      devLog('🔍 Buscando todos os animais...')
      const response = await api.get<Animal[]>('/animal')
      devLog(`✅ ${response.data.length} animais encontrados!`)
      return response.data
    }, 'Não foi possível carregar a lista de animais.')
  },

  // 📖 BUSCAR ANIMAL POR ID
  // GET /animal/{id}
  async buscarPorId(id: number): Promise<Animal> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🔍 Buscando animal com ID ${id}...`)
      const response = await api.get<Animal>(`/animal/${id}`)
      devLog('✅ Animal encontrado!')
      return response.data
    }, 'Não foi possível buscar os dados do animal.')
  },

  // 💾 CRIAR NOVO ANIMAL
  // POST /animal
  async criar(novoAnimal: NovoAnimal & { clienteId: number }): Promise<Animal> {
    validateId(novoAnimal.clienteId)
    return withErrorHandling(async () => {
      devLog('💾 Criando novo animal...', novoAnimal.nome)

      // Prepara os dados para enviar para API
      const dadosParaAPI = {
        nome: novoAnimal.nome,
        tipo: novoAnimal.tipo,
        codigoSimplesVet: novoAnimal.codigoSimplesVet,
        cliente: {
          id: novoAnimal.clienteId
        }
      }

      const response = await api.post<Animal>('/animal', dadosParaAPI)
      devLog('✅ Animal criado com sucesso! ID:', response.data.id)
      return response.data
    }, 'Não foi possível cadastrar o animal. Tente novamente.')
  }
}

// 📋 SERVIÇOS DE CLIENTES
// Aqui ficam todos os métodos relacionados aos clientes
export const clientesService = {

  // 📖 BUSCAR TODOS OS CLIENTES
  // GET /cliente
  async buscarTodos(): Promise<ClientesResponse> {
    try {
      console.log('📊 Buscando lista de clientes...')

      // Faz a requisição GET para /cliente
      const response = await api.get<ClientesResponse>('/cliente')

      console.log(`✅ ${response.data.length} clientes encontrados`)
      return response.data

    } catch (error) {
      console.error('❌ Erro ao buscar clientes:', error)

      // Re-lança o erro para quem chamou tratar
      throw new Error('Não foi possível carregar a lista de clientes')
    }
  },

  // 🔍 BUSCAR UM CLIENTE POR ID (para futuramente)
  // GET /cliente/{id}
  async buscarPorId(id: number): Promise<Cliente> {
    try {
      console.log(`🔍 Buscando cliente ID: ${id}`)

      const response = await api.get<Cliente>(`/cliente/${id}`)

      console.log('✅ Cliente encontrado:', response.data.nomeCompleto)
      return response.data

    } catch (error) {
      console.error('❌ Erro ao buscar cliente:', error)
      throw new Error(`Não foi possível encontrar o cliente ID: ${id}`)
    }
  },

  // 💾 CRIAR NOVO CLIENTE
  // POST /cliente
  async criar(novoCliente: NovoCliente): Promise<Cliente> {
    try {
      console.log('💾 Criando novo cliente...', novoCliente.nomeCompleto)

      // Faz a requisição POST para /cliente
      const response = await api.post<Cliente>('/cliente', novoCliente)

      console.log('✅ Cliente criado com sucesso! ID:', response.data.id)
      return response.data

    } catch (error) {
      console.error('❌ Erro ao criar cliente:', error)

      // Verifica se é um erro de validação do backend
      if ((error as any).response?.status === 400) {
        throw new Error('Dados inválidos. Verifique as informações e tente novamente.')
      }

      // Verifica se o CPF já existe
      if ((error as any).response?.status === 409) {
        throw new Error('Este CPF já está cadastrado no sistema.')
      }

      // Erro genérico
      throw new Error('Não foi possível cadastrar o cliente. Tente novamente.')
    }
  },

  // 🗑️ EXCLUIR CLIENTE POR ID
  // POST /cliente/{id} (Backend usa POST para exclusão)
  async excluir(id: number): Promise<void> {
    try {
      console.log(`🗑️ Excluindo cliente com ID ${id}...`)
      await api.post(`/cliente/${id}`)
      console.log('✅ Cliente excluído com sucesso!')
    } catch (error) {
      console.error('❌ Erro ao excluir cliente:', error)
      if ((error as any).response?.status === 404) {
        throw new Error('Cliente não encontrado.')
      }
      if ((error as any).response?.status === 409) {
        throw new Error('Não é possível excluir este cliente pois ele possui animais cadastrados.')
      }
      throw new Error('Não foi possível excluir o cliente. Tente novamente.')
    }
  },

  // ✏️ ATUALIZAR CLIENTE POR ID
  // PUT /cliente/{id}
  async atualizar(id: number, dadosAtualizados: Partial<NovoCliente>): Promise<Cliente> {
    try {
      console.log(`✏️ Atualizando cliente com ID ${id}...`, dadosAtualizados)
      const response = await api.put<Cliente>(`/cliente/${id}`, dadosAtualizados)
      console.log('✅ Cliente atualizado com sucesso!')
      return response.data
    } catch (error) {
      console.error('❌ Erro ao atualizar cliente:', error)
      console.error('❌ Resposta do servidor:', (error as any).response?.data)

      if ((error as any).response?.status === 404) {
        throw new Error('Cliente não encontrado.')
      }
      if ((error as any).response?.status === 400) {
        throw new Error('Dados inválidos. Verifique as informações e tente novamente.')
      }
      throw new Error('Não foi possível atualizar o cliente. Tente novamente.')
    }
  }
}

// 🔄 Exporta a instância do axios caso precise usar diretamente
export { api }

// 📝 EXEMPLO DE USO:
/*
import { clientesService } from '@/services/api'

// Em um componente Vue:
const clientes = await clientesService.buscarTodos()
console.log('Lista de clientes:', clientes)
*/
