// 📁 services/api.ts
// Este arquivo contém toda a configuração e métodos para comunicação com a API
// É aqui que centralizamos todas as chamadas HTTP

import axios from 'axios'
import type {
  ClientesResponse, Cliente, NovoCliente,
  Animal, NovoAnimal,
  ServicosResponse, ServicoCompleto, NovoServico,
  AnimalServico, NovoAnimalServico, CriarAnimalServicoCompleto,
  UsuariosResponse, Usuario
} from '@/types/api'

// Interface para BanhoIndividual
export interface BanhoIndividual {
  id: number
  animalServico: { id: number }
  dataBanho: string // formato: "YYYY-MM-DD"
  numeroBanho: number
  observacoes?: string
  usuario?: { id: number }
  createdAt: string
}

export interface NovoBanhoIndividual {
  animalServicoId: number
  dataBanho: string
  observacoes?: string | null
  usuarioId?: number | null
}
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
    const fullUrl = `${config.baseURL || ''}${config.url || ''}`
    devLog('🚀 Fazendo requisição para:', fullUrl)
    devLog('📋 Method:', config.method?.toUpperCase())
    devLog('📦 Data:', config.data)
    devLog('🔧 Headers:', config.headers)
    return config
  },
  (error) => {
    console.error('❌ Erro no interceptor REQUEST:', error)
    return Promise.reject(error)
  }
)

// Intercepta respostas para logging
api.interceptors.response.use(
  (response) => {
    devLog('✅ Resposta recebida:', `${response.status} - ${JSON.stringify(response.data).substring(0, 100)}...`)
    return response
  },
  (error) => {
    const fullUrl = `${error.config?.baseURL || ''}${error.config?.url || ''}`
    console.error('❌ Erro na resposta da API:', {
      url: fullUrl,
      method: error.config?.method?.toUpperCase(),
      status: error.response?.status,
      data: error.response?.data,
      message: error.message
    })
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
        raca: novoAnimal.raca || null,
        peso: novoAnimal.peso || null,
        codigoSimplesVet: novoAnimal.codigoSimplesVet,
        cliente: {
          id: novoAnimal.clienteId
        }
      }

      devLog('📤 Dados enviados para API:', dadosParaAPI)
      const response = await api.post<Animal>('/animal', dadosParaAPI)
      devLog('✅ Animal criado com sucesso! ID:', response.data.id)
      return response.data
    }, 'Não foi possível cadastrar o animal. Tente novamente.')
  },

  // ✏️ ATUALIZAR ANIMAL POR ID
  // PUT /animal/atualizarcompleto/{id}
  async atualizar(id: number, dadosAtualizados: Partial<Animal>): Promise<Animal> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`✏️ Atualizando animal com ID ${id}...`, dadosAtualizados)

      // Prepara os dados para enviar para API
      const dadosParaAPI = {
        nome: dadosAtualizados.nome,
        tipo: dadosAtualizados.tipo,
        raca: dadosAtualizados.raca || null,
        peso: dadosAtualizados.peso || null,
        codigoSimplesVet: dadosAtualizados.codigoSimplesVet
      }

      devLog('📤 Dados de atualização enviados para API:', dadosParaAPI)
      const response = await api.put<Animal>(`/animal/atualizarcompleto/${id}`, dadosParaAPI)
      devLog('✅ Animal atualizado com sucesso!')
      return response.data
    }, 'Não foi possível atualizar o animal. Tente novamente.')
  },

  // 🗑️ EXCLUIR ANIMAL POR ID
  // DELETE /animal/{id}
  async excluir(id: number): Promise<void> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🗑️ Excluindo animal com ID ${id}...`)
      await api.delete(`/animal/${id}`)
      devLog('✅ Animal excluído com sucesso!')
    }, 'Não foi possível excluir o animal. Tente novamente.')
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

// 💼 SERVIÇOS DE SERVIÇOS
// Aqui ficam todos os métodos relacionados aos serviços do petshop
export const servicosService = {

  // 📖 BUSCAR TODOS OS SERVIÇOS
  // GET /servico
  async buscarTodos(): Promise<ServicosResponse> {
    return withErrorHandling(async () => {
      devLog('🔍 Buscando todos os serviços...')
      const response = await api.get<ServicosResponse>('/servico')
      devLog(`✅ ${response.data.length} serviços encontrados!`)
      return response.data
    }, 'Não foi possível carregar a lista de serviços.')
  },

  // 📖 BUSCAR SERVIÇOS SIMPLES (sem relacionamentos problemáticos para modais)
  // GET /servico/simples
  async buscarTodosSimples(): Promise<ServicosResponse> {
    return withErrorHandling(async () => {
      devLog('🔍 Buscando todos os serviços (versão simples para modais)...')
      const response = await api.get<ServicosResponse>('/servico/simples')
      devLog(`✅ ${response.data.length} serviços encontrados (simples)!`)
      return response.data
    }, 'Não foi possível carregar a lista de serviços para edição.')
  },

  // 📖 BUSCAR SERVIÇO POR ID
  // GET /servico/{id}
  async buscarPorId(id: number): Promise<ServicoCompleto> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🔍 Buscando serviço com ID ${id}...`)
      const response = await api.get<ServicoCompleto>(`/servico/${id}`)
      devLog('✅ Serviço encontrado!')
      return response.data
    }, 'Não foi possível buscar os dados do serviço.')
  },

  // 💾 CRIAR NOVO SERVIÇO
  // POST /servico
  async criar(novoServico: NovoServico): Promise<ServicoCompleto> {
    return withErrorHandling(async () => {
      devLog('💾 Criando novo serviço...', novoServico.nome)
      const response = await api.post<ServicoCompleto>('/servico', novoServico)
      devLog('✅ Serviço criado com sucesso! ID:', response.data.id)
      return response.data
    }, 'Não foi possível cadastrar o serviço. Tente novamente.')
  },

  // 🗑️ EXCLUIR SERVIÇO POR ID
  // POST /servico/{id} (Backend usa POST para exclusão)
  async excluir(id: number): Promise<void> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🗑️ Excluindo serviço com ID ${id}...`)
      await api.post(`/servico/${id}`)
      devLog('✅ Serviço excluído com sucesso!')
    }, 'Não foi possível excluir o serviço. Tente novamente.')
  },

  // ✏️ ATUALIZAR SERVIÇO POR ID
  // PUT /servico/{id} (assumindo que existe, caso contrário usar POST)
  async atualizar(id: number, dadosAtualizados: Partial<NovoServico>): Promise<ServicoCompleto> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`✏️ Atualizando serviço com ID ${id}...`, dadosAtualizados)
      const response = await api.put<ServicoCompleto>(`/servico/${id}`, dadosAtualizados)
      devLog('✅ Serviço atualizado com sucesso!')
      return response.data
    }, 'Não foi possível atualizar o serviço. Tente novamente.')
  }
}

// 🛁 SERVIÇOS DE ANIMAL-SERVIÇO
// Aqui ficam todos os métodos relacionados ao registro de serviços para animais
export const animalServicoService = {

  // 📖 BUSCAR TODOS OS REGISTROS DE ANIMAL-SERVIÇO
  async buscarTodos(): Promise<AnimalServico[]> {
    return withErrorHandling(async () => {
      devLog('📋 Buscando todos os registros de animal-serviço...')
      const response = await api.get('/animalservico')
      devLog(`✅ ${response.data.length} registros encontrados!`)
      return response.data
    }, 'Não foi possível carregar os registros de animal-serviço.')
  },

  // 🔍 BUSCAR REGISTRO POR ID
  async buscarPorId(id: number): Promise<AnimalServico> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🔍 Buscando registro de animal-serviço com ID ${id}...`)
      const response = await api.get(`/animalservico/${id}`)
      devLog('✅ Registro encontrado!')
      return response.data
    }, 'Registro de animal-serviço não encontrado.')
  },

  // ➕ CRIAR NOVO REGISTRO
  async criar(animalServico: NovoAnimalServico): Promise<AnimalServico> {
    return withErrorHandling(async () => {
      devLog('➕ Criando novo registro de animal-serviço...', animalServico)
      const response = await api.post('/animalservico', animalServico)
      devLog('✅ Registro criado com sucesso!')
      return response.data
    }, 'Não foi possível criar o registro de animal-serviço. Tente novamente.')
  },

  // ➕ CRIAR REGISTRO COMPLETO (com banhos individuais)
  async criarCompleto(dadosCompletos: CriarAnimalServicoCompleto): Promise<AnimalServico> {
    return withErrorHandling(async () => {
      devLog('➕ Criando registro completo de animal-serviço com banhos individuais...', dadosCompletos)

      try {
        const response = await api.post('/animalservico/criar-completo', dadosCompletos)
        devLog('✅ Registro completo criado com sucesso!')
        return response.data
      } catch (error: any) {
        // Log detalhado do erro para debug
        console.error('❌ Erro detalhado na API completa:', {
          status: error.response?.status,
          statusText: error.response?.statusText,
          url: error.config?.url,
          method: error.config?.method,
          data: error.response?.data,
          headers: error.response?.headers,
          message: error.message
        })

        // Re-throw o erro para o withErrorHandling processar
        throw error
      }
    }, `Não foi possível criar o registro completo de animal-serviço.

Possíveis causas:
- O backend não foi atualizado com a nova funcionalidade
- Erro de validação nos dados enviados
- Problemas de conexão com o servidor

Por favor, verifique se o backend Spring Boot está rodando com as últimas alterações.`)
  },

  // 🗑️ EXCLUIR REGISTRO
  async excluir(id: number): Promise<void> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🗑️ Excluindo registro de animal-serviço com ID ${id}...`)
      await api.delete(`/animalservico/${id}`)
      devLog('✅ Registro excluído com sucesso!')
    }, 'Não foi possível excluir o registro de animal-serviço. Tente novamente.')
  },

  // ✏️ ATUALIZAR REGISTRO
  async atualizar(id: number, animalServico: Partial<AnimalServico>): Promise<AnimalServico> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`✏️ Atualizando registro de animal-serviço com ID ${id}...`, animalServico)
      const response = await api.put(`/animalservico/${id}`, animalServico)
      devLog('✅ Registro atualizado com sucesso!')
      return response.data
    }, 'Não foi possível atualizar o registro de animal-serviço. Tente novamente.')
  },

  // 💳 MARCAR COMO PAGO
  async marcarComoPago(id: number, dataPagamento: string): Promise<AnimalServico> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`💳 Marcando serviço ${id} como pago na data ${dataPagamento}...`)
      const response = await api.put(`/animalservico/${id}/marcar-pago?dataPagamento=${dataPagamento}`)
      devLog('✅ Serviço marcado como pago com sucesso!')
      return response.data
    }, 'Não foi possível marcar o serviço como pago. Tente novamente.')
  },

  // 🔄 REATIVAR SERVIÇO (marcar como em aberto)
  async reativarServico(id: number): Promise<AnimalServico> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🔄 Reativando serviço ${id} (em aberto)...`)
      const response = await api.put(`/animalservico/${id}/reativar`)
      devLog('✅ Serviço reativado (em aberto) com sucesso!')
      return response.data
    }, 'Não foi possível reativar o serviço. Tente novamente.')
  },

  // ❌ MARCAR COMO CANCELADO
  async marcarComoCancelado(id: number): Promise<AnimalServico> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`❌ Marcando serviço ${id} como cancelado...`)
      const response = await api.put(`/animalservico/${id}/marcar-cancelado`)
      devLog('✅ Serviço marcado como cancelado com sucesso!')
      return response.data
    }, 'Não foi possível marcar o serviço como cancelado. Tente novamente.')
  }
}

// 👤 SERVIÇOS DE USUÁRIOS
// Aqui ficam todos os métodos relacionados aos usuários do sistema
export const usuariosService = {

  // 📖 BUSCAR TODOS OS USUÁRIOS
  async buscarTodos(): Promise<Usuario[]> {
    return withErrorHandling(async () => {
      devLog('👤 Buscando todos os usuários...')
      const response = await api.get<UsuariosResponse>('/usuario')
      devLog(`✅ ${response.data.length} usuários encontrados!`)
      return response.data
    }, 'Não foi possível carregar os usuários.')
  },

  // 🔍 BUSCAR USUÁRIO POR ID
  async buscarPorId(id: number): Promise<Usuario> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🔍 Buscando usuário com ID ${id}...`)
      const response = await api.get<Usuario>(`/usuario/${id}`)
      devLog('✅ Usuário encontrado!')
      return response.data
    }, 'Usuário não encontrado.')
  },

  // ➕ CRIAR NOVO USUÁRIO
  async criar(usuario: Omit<Usuario, 'id'>): Promise<Usuario> {
    return withErrorHandling(async () => {
      devLog('➕ Criando novo usuário...', usuario)
      const response = await api.post<Usuario>('/usuario', usuario)
      devLog('✅ Usuário criado com sucesso!')
      return response.data
    }, 'Não foi possível criar o usuário. Tente novamente.')
  },

  // 🗑️ EXCLUIR USUÁRIO
  async excluir(id: number): Promise<void> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🗑️ Excluindo usuário com ID ${id}...`)
      await api.post(`/usuario/${id}`) // Endpoint usa POST para deletar
      devLog('✅ Usuário excluído com sucesso!')
    }, 'Não foi possível excluir o usuário. Tente novamente.')
  }
}

// 🛁 SERVIÇOS DE BANHO INDIVIDUAL
// Aqui ficam todos os métodos relacionados aos banhos individuais
export const banhosIndividuaisService = {

  // 📖 BUSCAR TODOS OS BANHOS INDIVIDUAIS
  async buscarTodos(): Promise<BanhoIndividual[]> {
    return withErrorHandling(async () => {
      devLog('🛁 Buscando todos os banhos individuais...')
      const response = await api.get<BanhoIndividual[]>('/banho-individual')
      devLog(`✅ ${response.data.length} banhos individuais encontrados!`)
      return response.data
    }, 'Não foi possível carregar os banhos individuais.')
  },

  // 🔍 BUSCAR BANHOS POR ANIMAL SERVIÇO
  async buscarPorAnimalServico(animalServicoId: number): Promise<BanhoIndividual[]> {
    validateId(animalServicoId)
    return withErrorHandling(async () => {
      devLog(`🔍 Buscando banhos para animal serviço ID ${animalServicoId}...`)
      const response = await api.get<BanhoIndividual[]>(`/banho-individual/animal-servico/${animalServicoId}`)
      devLog(`✅ ${response.data.length} banhos encontrados!`)
      return response.data
    }, 'Não foi possível carregar os banhos deste serviço.')
  },

  // ➕ CRIAR NOVO BANHO INDIVIDUAL
  async criar(banho: NovoBanhoIndividual): Promise<BanhoIndividual> {
    return withErrorHandling(async () => {
      devLog('➕ Criando novo banho individual...', banho)
      const response = await api.post<BanhoIndividual>('/banho-individual', banho)
      devLog('✅ Banho individual criado com sucesso!')
      return response.data
    }, 'Não foi possível registrar o banho. Tente novamente.')
  },

  // 🗑️ EXCLUIR BANHO INDIVIDUAL
  async excluir(id: number): Promise<void> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🗑️ Excluindo banho individual com ID ${id}...`)
      await api.delete(`/banho-individual/${id}`)
      devLog('✅ Banho individual excluído com sucesso!')
    }, 'Não foi possível excluir o banho. Tente novamente.')
  }
}

// 🔧 SERVIÇOS ADICIONAIS
// Aqui ficam todos os métodos relacionados aos serviços adicionais
export const servicosAdicionaisService = {

  // 📖 BUSCAR TODOS OS SERVIÇOS ADICIONAIS DE UM ANIMAL SERVIÇO
  async buscarPorAnimalServico(animalServicoId: number): Promise<any[]> {
    validateId(animalServicoId)
    return withErrorHandling(async () => {
      devLog('📋 Buscando serviços adicionais para animal serviço:', animalServicoId)
      const response = await api.get(`/api/servicos-adicionais/animal-servico/${animalServicoId}`)
      devLog(`✅ ${response.data.length} serviços adicionais encontrados!`)
      return response.data
    }, 'Não foi possível carregar os serviços adicionais.')
  },

  // 🔍 BUSCAR SERVIÇO ADICIONAL POR ID
  async buscarPorId(id: number): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog('🔍 Buscando serviço adicional por ID:', id)
      const response = await api.get(`/api/servicos-adicionais/${id}`)
      devLog('✅ Serviço adicional encontrado!')
      return response.data
    }, 'Não foi possível buscar o serviço adicional.')
  },

  // 💾 CRIAR NOVO SERVIÇO ADICIONAL
  async criar(novoServicoAdicional: any): Promise<any> {
    return withErrorHandling(async () => {
      devLog('💾 Criando novo serviço adicional...', novoServicoAdicional)
      const response = await api.post('/api/servicos-adicionais', novoServicoAdicional)
      devLog('✅ Serviço adicional criado com sucesso!')
      return response.data
    }, 'Não foi possível criar o serviço adicional.')
  },

  // 🗑️ REMOVER SERVIÇO ADICIONAL
  async remover(id: number): Promise<void> {
    validateId(id)
    return withErrorHandling(async () => {
      await api.delete(`/api/servicos-adicionais/${id}`)
      devLog('🗑️ Serviço adicional removido com sucesso!')
    }, 'Não foi possível remover o serviço adicional.')
  },

  // 🔄 ATUALIZAR STATUS DE PAGAMENTO
  async atualizarStatusPagamento(id: number, status: string, dataPagamento?: string): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      const params = new URLSearchParams({ status })
      if (dataPagamento) {
        params.append('dataPagamento', dataPagamento)
      }

      const response = await api.put(`/api/servicos-adicionais/${id}/status-pagamento?${params}`)
      devLog('✅ Status de pagamento atualizado!')
      return response.data
    }, 'Não foi possível atualizar o status de pagamento.')
  },

  // 📊 CALCULAR VALOR TOTAL DOS SERVIÇOS ADICIONAIS
  async calcularValorTotal(animalServicoId: number): Promise<number> {
    validateId(animalServicoId)
    return withErrorHandling(async () => {
      const response = await api.get(`/api/servicos-adicionais/animal-servico/${animalServicoId}/valor-total`)
      return response.data || 0
    }, 'Não foi possível calcular o valor total dos serviços adicionais.')
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
