// 📁 services/api.ts
// Este arquivo contém toda a configuração e métodos para comunicação com a API
// É aqui que centralizamos todas as chamadas HTTP

import axios from 'axios'
import type { ClientesResponse, Cliente, NovoCliente } from '@/types/api'

// 🔧 CONFIGURAÇÃO DO AXIOS
// Criamos uma instância do axios com configurações padrão
const api = axios.create({
  baseURL: 'http://localhost:8080', // URL base da nossa API Spring Boot
  timeout: 10000, // Timeout de 10 segundos para as requisições
  headers: {
    'Content-Type': 'application/json', // Sempre enviar/receber JSON
  }
})

// 🔍 INTERCEPTADORES (opcional, mas muito útil!)
// Intercepta TODAS as requisições antes de serem enviadas
api.interceptors.request.use(
  (config) => {
    console.log('🚀 Fazendo requisição para:', `${config.baseURL || ''}${config.url || ''}`)
    return config
  },
  (error) => {
    console.error('❌ Erro na requisição:', error)
    return Promise.reject(error)
  }
)

// Intercepta TODAS as respostas antes de serem processadas
api.interceptors.response.use(
  (response) => {
    console.log('✅ Resposta recebida:', response.status, response.data)
    return response
  },
  (error) => {
    console.error('❌ Erro na resposta:', error.response?.status, error.message)

    // Aqui podemos tratar erros globais
    if (error.response?.status === 401) {
      console.log('🔐 Usuário não autorizado')
      // Futuramente: redirecionar para login
    }

    return Promise.reject(error)
  }
)

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
  }

  // ✏️ EDITAR CLIENTE (para futuramente)
  // PUT /cliente/{id}
  // async editar(id: number, clienteEditado: Partial<Cliente>): Promise<Cliente> { ... }

  // 🗑️ DELETAR CLIENTE (para futuramente)
  // DELETE /cliente/{id}
  // async deletar(id: number): Promise<void> { ... }
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
