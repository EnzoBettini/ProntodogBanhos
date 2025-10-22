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
  },

  // 📊 BUSCAR HISTÓRICO COMPLETO DO ANIMAL
  // GET /animal/{id}/historico
  async buscarHistorico(id: number): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`📊 Buscando histórico completo do animal ID ${id}...`)
      const response = await api.get(`/animal/${id}/historico`)
      devLog('✅ Histórico carregado com sucesso!')
      return response.data
    }, 'Não foi possível carregar o histórico do animal.')
  },

  // 🔍 BUSCAR ANIMAIS COM FILTROS E PAGINAÇÃO
  // GET /animal/buscar
  async buscarComFiltros(filtros: {
    nome?: string
    tipo?: string
    raca?: string
    codigoSimplesVet?: string
    clienteNome?: string
    page?: number
    size?: number
    sortBy?: string
    sortDir?: string
  } = {}): Promise<any> {
    return withErrorHandling(async () => {
      devLog('🔍 Buscando animais com filtros:', filtros)

      // Construir parâmetros de query
      const params = new URLSearchParams()

      if (filtros.nome) params.append('nome', filtros.nome)
      if (filtros.tipo) params.append('tipo', filtros.tipo)
      if (filtros.raca) params.append('raca', filtros.raca)
      if (filtros.codigoSimplesVet) params.append('codigoSimplesVet', filtros.codigoSimplesVet)
      if (filtros.clienteNome) params.append('clienteNome', filtros.clienteNome)

      params.append('page', (filtros.page || 0).toString())
      params.append('size', (filtros.size || 20).toString())
      params.append('sortBy', filtros.sortBy || 'nome')
      params.append('sortDir', filtros.sortDir || 'asc')

      const response = await api.get(`/animal/buscar?${params}`)

      devLog(`✅ Encontrados ${response.data.totalElements} animais (página ${response.data.number + 1} de ${response.data.totalPages})`)

      return response.data
    }, 'Não foi possível buscar os animais.')
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

  // ✏️ ATUALIZAR SERVIÇO ADICIONAL COMPLETO
  async atualizar(id: number, dadosServico: any): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`✏️ Atualizando serviço adicional ID ${id}...`, dadosServico)
      const response = await api.put(`/api/servicos-adicionais/${id}`, dadosServico)
      devLog('✅ Serviço adicional atualizado com sucesso!')
      return response.data
    }, 'Não foi possível atualizar o serviço adicional.')
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
        // Converter data para formato ISO DateTime se necessário
        let dataFormatada = dataPagamento
        if (dataPagamento && !dataPagamento.includes('T')) {
          dataFormatada = `${dataPagamento}T00:00:00`
        }
        params.append('dataPagamento', dataFormatada)
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

// 💰 SERVIÇOS DE VENDAS
// Aqui ficam todos os métodos relacionados ao sistema de vendas
export const vendasService = {

  // 📖 BUSCAR TODAS AS VENDAS
  async buscarTodas(): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('🛒 Buscando todas as vendas...')
      const response = await api.get('/api/vendas')
      devLog(`✅ ${response.data.length} vendas encontradas!`)
      return response.data
    }, 'Não foi possível carregar a lista de vendas.')
  },

  // 🔍 BUSCAR VENDA POR ID
  async buscarPorId(id: number): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🔍 Buscando venda com ID ${id}...`)
      const response = await api.get(`/api/vendas/${id}`)
      devLog('✅ Venda encontrada!')
      return response.data
    }, 'Não foi possível buscar os dados da venda.')
  },

  // 🔍 BUSCAR VENDA POR CÓDIGO
  async buscarPorCodigo(codigoVenda: number): Promise<any> {
    return withErrorHandling(async () => {
      devLog(`🔍 Buscando venda com código ${codigoVenda}...`)
      const response = await api.get(`/api/vendas/codigo/${codigoVenda}`)
      devLog('✅ Venda encontrada!')
      return response.data
    }, 'Não foi possível buscar a venda pelo código.')
  },

  // 📋 BUSCAR VENDAS DE UM CLIENTE
  async buscarPorCliente(clienteId: number): Promise<any[]> {
    validateId(clienteId)
    return withErrorHandling(async () => {
      devLog(`📋 Buscando vendas do cliente ${clienteId}...`)
      const response = await api.get(`/api/vendas/cliente/${clienteId}`)
      devLog(`✅ ${response.data.length} vendas encontradas!`)
      return response.data
    }, 'Não foi possível buscar as vendas do cliente.')
  },

  // 📊 BUSCAR VENDAS POR STATUS
  async buscarPorStatus(status: string): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog(`📊 Buscando vendas com status ${status}...`)
      const response = await api.get(`/api/vendas/status/${status}`)
      devLog(`✅ ${response.data.length} vendas encontradas!`)
      return response.data
    }, 'Não foi possível buscar as vendas por status.')
  },

  // 💾 CRIAR NOVA VENDA
  async criar(venda: any): Promise<any> {
    return withErrorHandling(async () => {
      devLog('💾 Criando nova venda...', venda)
      const response = await api.post('/api/vendas', venda)
      devLog('✅ Venda criada com sucesso! Código:', response.data.codigoVenda)
      return response.data
    }, 'Não foi possível criar a venda. Tente novamente.')
  },

  // ✏️ ATUALIZAR VENDA
  async atualizar(id: number, dados: any): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`✏️ Atualizando venda ${id}...`, dados)
      const response = await api.put(`/api/vendas/${id}`, dados)
      devLog('✅ Venda atualizada com sucesso!')
      return response.data
    }, 'Não foi possível atualizar a venda.')
  },

  // ❌ CANCELAR VENDA
  async cancelar(id: number, motivo: string): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`❌ Cancelando venda ${id}...`)
      const response = await api.put(`/api/vendas/${id}/cancelar`, { motivoCancelamento: motivo })
      devLog('✅ Venda cancelada!')
      return response.data
    }, 'Não foi possível cancelar a venda.')
  },

  // DELETE /api/vendas/{id} - Excluir venda permanentemente
  async excluir(id: number): Promise<void> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🗑️ Excluindo venda ${id}...`)
      await api.delete(`/api/vendas/${id}`)
      devLog('✅ Venda excluída!')
    }, 'Não foi possível excluir a venda.')
  },

  // ➕ ADICIONAR ITEM NA VENDA (AnimalServico existente)
  async adicionarItem(vendaId: number, animalServicoId: number): Promise<any> {
    validateId(vendaId)
    validateId(animalServicoId)
    return withErrorHandling(async () => {
      devLog(`➕ Adicionando item à venda ${vendaId}...`)
      const response = await api.post(`/api/vendas/${vendaId}/itens/${animalServicoId}`)
      devLog('✅ Item adicionado!')
      return response.data
    }, 'Não foi possível adicionar o item à venda.')
  },

  // ➕ ADICIONAR NOVO ITEM NA VENDA (criar AnimalServico na hora)
  async adicionarItemNovo(vendaId: number, novoItem: { animalId: number, servicoId: number, valorItem?: number, descontoItem?: number }): Promise<any> {
    validateId(vendaId)
    return withErrorHandling(async () => {
      devLog(`➕ Adicionando novo item à venda ${vendaId}...`, novoItem)
      const response = await api.post(`/api/vendas/${vendaId}/itens`, novoItem)
      devLog('✅ Novo item adicionado!')
      return response.data
    }, 'Não foi possível adicionar o novo item à venda.')
  },

  // 🗑️ REMOVER ITEM DA VENDA
  async removerItem(vendaId: number, itemId: number): Promise<any> {
    validateId(vendaId)
    validateId(itemId)
    return withErrorHandling(async () => {
      devLog(`🗑️ Removendo item ${itemId} da venda ${vendaId}...`)
      const response = await api.delete(`/api/vendas/${vendaId}/itens/${itemId}`)
      devLog('✅ Item removido!')
      return response.data
    }, 'Não foi possível remover o item da venda.')
  },

  // 💰 REGISTRAR PAGAMENTO (BAIXA)
  async registrarBaixa(baixa: any): Promise<any> {
    return withErrorHandling(async () => {
      devLog('💰 Registrando pagamento...', baixa)
      const response = await api.post('/api/vendas/baixas', baixa)
      devLog('✅ Pagamento registrado com sucesso!')
      return response.data
    }, 'Não foi possível registrar o pagamento.')
  },

  // 🗑️ REMOVER BAIXA (PAGAMENTO)
  async removerBaixa(vendaId: number, baixaId: number): Promise<any> {
    validateId(vendaId)
    validateId(baixaId)
    return withErrorHandling(async () => {
      devLog(`🗑️ Removendo baixa ${baixaId} da venda ${vendaId}...`)
      const response = await api.delete(`/api/vendas/${vendaId}/baixas/${baixaId}`)
      devLog('✅ Baixa removida!')
      return response.data
    }, 'Não foi possível remover a baixa.')
  }
}

// 💳 SERVIÇOS DE FORMAS DE PAGAMENTO
// Aqui ficam todos os métodos relacionados às formas de pagamento
export const formasPagamentoService = {

  // 📖 BUSCAR TODAS AS FORMAS DE PAGAMENTO
  async buscarTodas(): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('💳 Buscando todas as formas de pagamento...')
      const response = await api.get('/api/formas-pagamento')
      devLog(`✅ ${response.data.length} formas de pagamento encontradas!`)
      return response.data
    }, 'Não foi possível carregar as formas de pagamento.')
  },

  // 📖 BUSCAR APENAS FORMAS ATIVAS
  async buscarAtivas(): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('💳 Buscando formas de pagamento ativas...')
      const response = await api.get('/api/formas-pagamento/ativas')
      devLog(`✅ ${response.data.length} formas ativas encontradas!`)
      return response.data
    }, 'Não foi possível carregar as formas de pagamento ativas.')
  },

  // 🔍 BUSCAR FORMA POR ID
  async buscarPorId(id: number): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🔍 Buscando forma de pagamento com ID ${id}...`)
      const response = await api.get(`/api/formas-pagamento/${id}`)
      devLog('✅ Forma de pagamento encontrada!')
      return response.data
    }, 'Não foi possível buscar a forma de pagamento.')
  },

  // 📊 BUSCAR POR TIPO
  async buscarPorTipo(tipo: string): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog(`📊 Buscando formas de pagamento do tipo ${tipo}...`)
      const response = await api.get(`/api/formas-pagamento/tipo/${tipo}`)
      devLog(`✅ ${response.data.length} formas encontradas!`)
      return response.data
    }, 'Não foi possível buscar formas por tipo.')
  },

  // 📊 BUSCAR FORMAS COM PARCELAMENTO
  async buscarComParcelamento(): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('📊 Buscando formas com parcelamento...')
      const response = await api.get('/api/formas-pagamento/parcelamento')
      devLog(`✅ ${response.data.length} formas com parcelamento encontradas!`)
      return response.data
    }, 'Não foi possível buscar formas com parcelamento.')
  },

  // 💾 CRIAR NOVA FORMA DE PAGAMENTO
  async criar(forma: any): Promise<any> {
    return withErrorHandling(async () => {
      devLog('💾 Criando nova forma de pagamento...', forma)
      const response = await api.post('/api/formas-pagamento', forma)
      devLog('✅ Forma de pagamento criada!')
      return response.data
    }, 'Não foi possível criar a forma de pagamento.')
  },

  // ✏️ ATUALIZAR FORMA DE PAGAMENTO
  async atualizar(id: number, forma: any): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`✏️ Atualizando forma de pagamento ${id}...`, forma)
      const response = await api.put(`/api/formas-pagamento/${id}`, forma)
      devLog('✅ Forma de pagamento atualizada!')
      return response.data
    }, 'Não foi possível atualizar a forma de pagamento.')
  },

  // ✅ ATIVAR FORMA DE PAGAMENTO
  async ativar(id: number): Promise<void> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`✅ Ativando forma de pagamento ${id}...`)
      await api.put(`/api/formas-pagamento/${id}/ativar`)
      devLog('✅ Forma de pagamento ativada!')
    }, 'Não foi possível ativar a forma de pagamento.')
  },

  // ❌ DESATIVAR FORMA DE PAGAMENTO
  async desativar(id: number): Promise<void> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`❌ Desativando forma de pagamento ${id}...`)
      await api.put(`/api/formas-pagamento/${id}/desativar`)
      devLog('✅ Forma de pagamento desativada!')
    }, 'Não foi possível desativar a forma de pagamento.')
  },

  // 🗑️ EXCLUIR FORMA DE PAGAMENTO
  async excluir(id: number): Promise<void> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🗑️ Excluindo forma de pagamento ${id}...`)
      await api.delete(`/api/formas-pagamento/${id}`)
      devLog('✅ Forma de pagamento excluída!')
    }, 'Não foi possível excluir a forma de pagamento.')
  }
}

// 🏦 SERVIÇOS DE MAQUININHAS
// Aqui ficam todos os métodos relacionados ao sistema de maquininhas de cartão
export const maquininhasService = {

  // 📖 LISTAR TODAS AS MAQUININHAS
  async listarTodas(): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('🏦 Buscando todas as maquininhas...')
      const response = await api.get('/api/maquininhas')
      devLog(`✅ ${response.data.length} maquininhas encontradas!`)
      return response.data
    }, 'Não foi possível carregar as maquininhas.')
  },

  // 📖 LISTAR APENAS MAQUININHAS ATIVAS
  async listarAtivas(): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('🏦 Buscando maquininhas ativas...')
      const response = await api.get('/api/maquininhas/ativas')
      devLog(`✅ ${response.data.length} maquininhas ativas encontradas!`)
      return response.data
    }, 'Não foi possível carregar as maquininhas ativas.')
  },

  // 📖 LISTAR MAQUININHAS ATIVAS (RESUMO)
  async listarAtivasResumo(): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('🏦 Buscando resumo das maquininhas ativas...')
      const response = await api.get('/api/maquininhas/ativas/resumo')
      devLog(`✅ ${response.data.length} resumos encontrados!`)
      return response.data
    }, 'Não foi possível carregar o resumo das maquininhas.')
  },

  // 🔍 BUSCAR MAQUININHA POR ID
  async buscarPorId(id: number): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🔍 Buscando maquininha com ID ${id}...`)
      const response = await api.get(`/api/maquininhas/${id}`)
      devLog('✅ Maquininha encontrada!')
      return response.data
    }, 'Não foi possível buscar a maquininha.')
  },

  // 💾 CRIAR NOVA MAQUININHA
  async criar(dados: any): Promise<any> {
    return withErrorHandling(async () => {
      devLog('💾 Criando nova maquininha...', dados)
      const response = await api.post('/api/maquininhas', dados)
      devLog('✅ Maquininha criada com sucesso! ID:', response.data.id)
      return response.data
    }, 'Não foi possível criar a maquininha. Verifique os dados e tente novamente.')
  },

  // ✏️ ATUALIZAR MAQUININHA
  async atualizar(id: number, dados: any): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`✏️ Atualizando maquininha ${id}...`, dados)
      const response = await api.put(`/api/maquininhas/${id}`, dados)
      devLog('✅ Maquininha atualizada com sucesso!')
      return response.data
    }, 'Não foi possível atualizar a maquininha.')
  },

  // 🗑️ EXCLUIR MAQUININHA
  async excluir(id: number): Promise<void> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🗑️ Excluindo maquininha ${id}...`)
      await api.delete(`/api/maquininhas/${id}`)
      devLog('✅ Maquininha excluída!')
    }, 'Não foi possível excluir a maquininha.')
  },

  // ✅ ATIVAR MAQUININHA
  async ativar(id: number): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`✅ Ativando maquininha ${id}...`)
      const response = await api.put(`/api/maquininhas/${id}/ativar`)
      devLog('✅ Maquininha ativada!')
      return response.data
    }, 'Não foi possível ativar a maquininha.')
  },

  // 📊 LISTAR TAXAS DE UMA MAQUININHA
  async listarTaxas(id: number): Promise<any[]> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`📊 Buscando taxas da maquininha ${id}...`)
      const response = await api.get(`/api/maquininhas/${id}/taxas`)
      devLog(`✅ ${response.data.length} taxas encontradas!`)
      return response.data
    }, 'Não foi possível buscar as taxas da maquininha.')
  },

  // ➕ ADICIONAR TAXA À MAQUININHA
  async adicionarTaxa(id: number, taxa: any): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`➕ Adicionando taxa à maquininha ${id}...`, taxa)
      const response = await api.post(`/api/maquininhas/${id}/taxas`, taxa)
      devLog('✅ Taxa adicionada!')
      return response.data
    }, 'Não foi possível adicionar a taxa.')
  },

  // 🧮 CALCULAR TAXA PARA UMA TRANSAÇÃO
  async calcularTaxa(maquininhaId: number, bandeiraId: number, tipoTransacao: string, numeroParcelas: number | null, valor: number): Promise<any> {
    validateId(maquininhaId)
    validateId(bandeiraId)
    return withErrorHandling(async () => {
      devLog(`🧮 Calculando taxa...`)
      const params = new URLSearchParams({
        bandeiraId: bandeiraId.toString(),
        tipoTransacao,
        valor: valor.toString()
      })
      if (numeroParcelas !== null) {
        params.append('numeroParcelas', numeroParcelas.toString())
      }
      const response = await api.get(`/api/maquininhas/${maquininhaId}/calcular-taxa?${params}`)
      devLog('✅ Taxa calculada!')
      return response.data
    }, 'Não foi possível calcular a taxa.')
  },

  // 💰 CALCULAR VALOR FINAL COM TAXA (repasse ao cliente)
  async calcularValorFinal(maquininhaId: number, bandeiraId: number, tipoTransacao: string, numeroParcelas: number | null, valorOriginal: number): Promise<any> {
    validateId(maquininhaId)
    validateId(bandeiraId)
    return withErrorHandling(async () => {
      devLog(`💰 Calculando valor final com taxa...`)
      const params = new URLSearchParams({
        bandeiraId: bandeiraId.toString(),
        tipoTransacao,
        valorOriginal: valorOriginal.toString()
      })
      if (numeroParcelas !== null && numeroParcelas > 1) {
        params.append('numeroParcelas', numeroParcelas.toString())
      }
      const response = await api.get(`/api/maquininhas/${maquininhaId}/calcular-valor-final?${params}`)
      devLog('✅ Valor final calculado!', response.data)
      return response.data
    }, 'Não foi possível calcular o valor final.')
  }
}

// 🏦 SERVIÇOS DE CONTAS BANCÁRIAS
// Aqui ficam todos os métodos relacionados às contas bancárias
export const contasBancariasService = {

  // 📖 LISTAR TODAS AS CONTAS
  async listarTodas(): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('🏦 Buscando todas as contas bancárias...')
      const response = await api.get('/api/contas-bancarias')
      devLog(`✅ ${response.data.length} contas encontradas!`)
      return response.data
    }, 'Não foi possível carregar as contas bancárias.')
  },

  // 📖 LISTAR APENAS CONTAS ATIVAS
  async listarAtivas(): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('🏦 Buscando contas bancárias ativas...')
      const response = await api.get('/api/contas-bancarias/ativas')
      devLog(`✅ ${response.data.length} contas ativas encontradas!`)
      return response.data
    }, 'Não foi possível carregar as contas ativas.')
  },

  // 🔍 BUSCAR CONTA POR ID
  async buscarPorId(id: number): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🔍 Buscando conta bancária com ID ${id}...`)
      const response = await api.get(`/api/contas-bancarias/${id}`)
      devLog('✅ Conta encontrada!')
      return response.data
    }, 'Não foi possível buscar a conta bancária.')
  },

  // 💾 CRIAR NOVA CONTA
  async criar(dados: any): Promise<any> {
    return withErrorHandling(async () => {
      devLog('💾 Criando nova conta bancária...', dados)
      const response = await api.post('/api/contas-bancarias', dados)
      devLog('✅ Conta criada com sucesso!')
      return response.data
    }, 'Não foi possível criar a conta bancária.')
  },

  // ✏️ ATUALIZAR CONTA
  async atualizar(id: number, dados: any): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`✏️ Atualizando conta bancária ${id}...`, dados)
      const response = await api.put(`/api/contas-bancarias/${id}`, dados)
      devLog('✅ Conta atualizada!')
      return response.data
    }, 'Não foi possível atualizar a conta bancária.')
  },

  // 🗑️ EXCLUIR CONTA
  async excluir(id: number): Promise<void> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`🗑️ Excluindo conta bancária ${id}...`)
      await api.delete(`/api/contas-bancarias/${id}`)
      devLog('✅ Conta excluída!')
    }, 'Não foi possível excluir a conta bancária.')
  },

  // ✅ ATIVAR CONTA
  async ativar(id: number): Promise<any> {
    validateId(id)
    return withErrorHandling(async () => {
      devLog(`✅ Ativando conta bancária ${id}...`)
      const response = await api.put(`/api/contas-bancarias/${id}/ativar`)
      devLog('✅ Conta ativada!')
      return response.data
    }, 'Não foi possível ativar a conta bancária.')
  }
}

// 🏢 SERVIÇOS DE ADQUIRENTES
// Aqui ficam todos os métodos relacionados aos adquirentes (empresas de maquininha)
export const adquirentesService = {

  // 📖 LISTAR TODOS OS ADQUIRENTES
  async listarTodos(): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('🏢 Buscando todos os adquirentes...')
      const response = await api.get('/api/adquirentes')
      devLog(`✅ ${response.data.length} adquirentes encontrados!`)
      return response.data
    }, 'Não foi possível carregar os adquirentes.')
  },

  // 📖 LISTAR APENAS ADQUIRENTES ATIVOS
  async listarAtivos(): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('🏢 Buscando adquirentes ativos...')
      const response = await api.get('/api/adquirentes/ativos')
      devLog(`✅ ${response.data.length} adquirentes ativos encontrados!`)
      return response.data
    }, 'Não foi possível carregar os adquirentes ativos.')
  }
}

// 💳 SERVIÇOS DE BANDEIRAS
// Aqui ficam todos os métodos relacionados às bandeiras de cartão
export const bandeirasService = {

  // 📖 LISTAR TODAS AS BANDEIRAS
  async listarTodas(): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('💳 Buscando todas as bandeiras...')
      const response = await api.get('/api/bandeiras')
      devLog(`✅ ${response.data.length} bandeiras encontradas!`)
      return response.data
    }, 'Não foi possível carregar as bandeiras.')
  },

  // 📖 LISTAR APENAS BANDEIRAS ATIVAS
  async listarAtivas(): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('💳 Buscando bandeiras ativas...')
      const response = await api.get('/api/bandeiras/ativas')
      devLog(`✅ ${response.data.length} bandeiras ativas encontradas!`)
      return response.data
    }, 'Não foi possível carregar as bandeiras ativas.')
  }
}

// 📊 SERVIÇOS DE RECEBIMENTOS
// Aqui ficam todos os métodos relacionados ao acompanhamento de recebimentos
export const recebimentosService = {

  // 📋 LISTAR RECEBIMENTOS PENDENTES
  async listarPendentes(maquininhaId?: number, dataInicio?: string, dataFim?: string): Promise<any[]> {
    return withErrorHandling(async () => {
      devLog('📋 Buscando recebimentos pendentes...')
      const params = new URLSearchParams()
      if (maquininhaId) params.append('maquininhaId', maquininhaId.toString())
      if (dataInicio) params.append('dataInicio', dataInicio)
      if (dataFim) params.append('dataFim', dataFim)
      const response = await api.get(`/api/recebimentos/pendentes?${params}`)
      devLog(`✅ ${response.data.length} recebimentos pendentes encontrados!`)
      return response.data
    }, 'Não foi possível buscar os recebimentos pendentes.')
  },

  // ✅ MARCAR COMO RECEBIDO
  async marcarComoRecebido(baixaId: number, dataRecebimento: string): Promise<any> {
    validateId(baixaId)
    return withErrorHandling(async () => {
      devLog(`✅ Marcando baixa ${baixaId} como recebida...`)
      const params = new URLSearchParams({ dataRecebimento })
      const response = await api.put(`/api/recebimentos/${baixaId}/recebido?${params}`)
      devLog('✅ Recebimento confirmado!')
      return response.data
    }, 'Não foi possível confirmar o recebimento.')
  },

  // 🔄 MARCAR COMO ANTECIPADO
  async marcarComoAntecipado(baixaId: number, dataRecebimento: string, taxaAntecipacao: number): Promise<any> {
    validateId(baixaId)
    return withErrorHandling(async () => {
      devLog(`🔄 Marcando baixa ${baixaId} como antecipada...`)
      const params = new URLSearchParams({
        dataRecebimento,
        taxaAntecipacao: taxaAntecipacao.toString()
      })
      const response = await api.put(`/api/recebimentos/${baixaId}/antecipado?${params}`)
      devLog('✅ Antecipação registrada!')
      return response.data
    }, 'Não foi possível registrar a antecipação.')
  },

  // ❌ MARCAR COMO ESTORNADO
  async marcarComoEstornado(baixaId: number): Promise<any> {
    validateId(baixaId)
    return withErrorHandling(async () => {
      devLog(`❌ Marcando baixa ${baixaId} como estornada...`)
      const response = await api.put(`/api/recebimentos/${baixaId}/estornado`)
      devLog('✅ Estorno registrado!')
      return response.data
    }, 'Não foi possível registrar o estorno.')
  },

  // 📊 BUSCAR PROJEÇÃO DE FLUXO DE CAIXA
  async projecaoFluxoCaixa(diasFuturos: number = 30, maquininhaId?: number): Promise<any> {
    return withErrorHandling(async () => {
      devLog(`📊 Buscando projeção de fluxo de caixa para ${diasFuturos} dias...`)
      const params = new URLSearchParams({ diasFuturos: diasFuturos.toString() })
      if (maquininhaId) params.append('maquininhaId', maquininhaId.toString())
      const response = await api.get(`/api/recebimentos/projecao-fluxo-caixa?${params}`)
      devLog('✅ Projeção calculada!')
      return response.data
    }, 'Não foi possível calcular a projeção de fluxo de caixa.')
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
