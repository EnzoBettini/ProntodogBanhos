<template>
  <div class="relative min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50 p-4 pb-32">
    <!-- 🎨 Background decorativo -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none z-0">
      <div class="absolute top-20 left-10 w-72 h-72 bg-blue-200/20 rounded-full blur-3xl"></div>
      <div class="absolute bottom-20 right-10 w-96 h-96 bg-green-200/20 rounded-full blur-3xl"></div>
      <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-gradient-to-r from-blue-100/10 to-green-100/10 rounded-full blur-3xl"></div>
    </div>

    <div class="relative z-20 max-w-7xl mx-auto pt-8">

      <!-- 🔄 Estado de Loading -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-r from-blue-400 to-green-500 rounded-full mb-4 animate-spin">
          <FontAwesomeIcon :icon="['fas', 'spinner']" class="text-white text-2xl" />
        </div>
        <p class="text-gray-600 text-lg">Carregando dashboard...</p>
          </div>

      <!-- ❌ Estado de Erro -->
      <div v-else-if="error" class="text-center py-12">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-red-500 rounded-full mb-4">
          <FontAwesomeIcon :icon="['fas', 'exclamation-triangle']" class="text-white text-2xl" />
        </div>
        <h3 class="text-xl font-semibold text-gray-800 mb-2">Erro ao carregar dashboard</h3>
        <p class="text-gray-600 mb-4">{{ error }}</p>
        <button
          @click="carregarDados"
          class="flex items-center gap-2 px-6 py-3 bg-blue-500 text-white rounded-xl hover:bg-blue-600 transition-colors mx-auto"
        >
          <FontAwesomeIcon :icon="['fas', 'refresh']" />
          Tentar Novamente
        </button>
      </div>

      <!-- 📊 Dashboard Principal -->
      <div v-else>

        <!-- 🎯 Header Dashboard -->
        <div class="relative overflow-hidden rounded-3xl bg-gradient-to-r from-blue-600 via-blue-700 to-green-600 text-white p-8 mb-8 shadow-2xl">
          <div class="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full blur-3xl transform translate-x-32 -translate-y-32"></div>
          <div class="absolute bottom-0 left-0 w-48 h-48 bg-white/5 rounded-full blur-2xl transform -translate-x-24 translate-y-24"></div>

          <div class="relative z-10">
            <div class="flex items-center justify-between">
              <div>
                <h1 class="text-4xl font-bold mb-2 bg-clip-text text-transparent bg-gradient-to-r from-white to-blue-100">
                  🐕 ProntoDog Dashboard
                </h1>
                <p class="text-xl opacity-90">{{ saudacao }}, administrador!</p>
                <p class="text-lg opacity-75 mt-1">{{ dataAtual }}</p>
              </div>

              <!-- Botão Refresh -->
              <button
                @click="carregarDados"
                :disabled="loading"
                class="group flex items-center gap-2 px-6 py-3 bg-white/20 backdrop-blur-sm rounded-xl hover:bg-white/30 transition-all duration-300 border border-white/20 hover:border-white/40 transform hover:-translate-y-1 hover:shadow-lg disabled:opacity-50"
              >
                <FontAwesomeIcon
                  :icon="['fas', 'refresh']"
                  :class="{ 'animate-spin': loading, 'group-hover:rotate-180': !loading }"
                  class="transition-transform duration-300"
                />
                <span class="font-medium">{{ loading ? 'Carregando...' : 'Atualizar' }}</span>
              </button>
            </div>
          </div>
        </div>

        <!-- 💰 Métricas Financeiras -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">

          <!-- Total de Receita -->
          <BaseCard class="shadow-xl border-0 bg-gradient-to-br from-green-50 to-emerald-100">
            <div class="p-6">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 bg-gradient-to-br from-green-500 to-emerald-600 rounded-xl flex items-center justify-center">
                  <FontAwesomeIcon :icon="['fas', 'dollar-sign']" class="text-white text-xl" />
                </div>
                <div class="flex-1">
                  <p class="text-sm font-medium text-gray-600 mb-1">Receita do Mês</p>
                  <p class="text-2xl font-bold text-gray-900">{{ formatarValor(metricas.receitaTotal) }}</p>
                  <p class="text-xs text-green-600 font-medium">💰 Pagamentos de {{ new Date().toLocaleDateString('pt-BR', { month: 'long' }) }}</p>
                </div>
              </div>
            </div>
          </BaseCard>

          <!-- Serviços em Aberto -->
          <BaseCard class="shadow-xl border-0 bg-gradient-to-br from-orange-50 to-amber-100">
            <div class="p-6">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 bg-gradient-to-br from-orange-500 to-amber-600 rounded-xl flex items-center justify-center animate-pulse">
                  <FontAwesomeIcon :icon="['fas', 'clock']" class="text-white text-xl" />
                </div>
                <div class="flex-1">
                  <p class="text-sm font-medium text-gray-600 mb-1">Em Aberto (Mês)</p>
                  <p class="text-2xl font-bold text-gray-900">{{ formatarValor(metricas.valorEmAberto) }}</p>
                  <p class="text-xs text-orange-600 font-medium">📅 Cadastrados em {{ new Date().toLocaleDateString('pt-BR', { month: 'long' }) }}</p>
                </div>
              </div>
            </div>
          </BaseCard>

          <!-- Pacotes Vencidos -->
          <BaseCard class="shadow-xl border-0 bg-gradient-to-br from-red-50 to-pink-100">
            <div class="p-6">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 bg-gradient-to-br from-red-500 to-pink-600 rounded-xl flex items-center justify-center">
                  <FontAwesomeIcon :icon="['fas', 'exclamation-triangle']" class="text-white text-xl" />
                </div>
                <div class="flex-1">
                  <p class="text-sm font-medium text-gray-600 mb-1">Pacotes Vencidos</p>
                  <p class="text-2xl font-bold text-gray-900">{{ metricas.pacotesVencidos }}</p>
                  <p class="text-xs text-red-600 font-medium">🚨 Atenção necessária</p>
                </div>
              </div>
        </div>
      </BaseCard>

          <!-- Banhos Realizados -->
          <BaseCard class="shadow-xl border-0 bg-gradient-to-br from-blue-50 to-cyan-100">
            <div class="p-6">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-cyan-600 rounded-xl flex items-center justify-center">
                  <FontAwesomeIcon :icon="['fas', 'bath']" class="text-white text-xl" />
                </div>
                <div class="flex-1">
                  <p class="text-sm font-medium text-gray-600 mb-1">Banhos do Mês</p>
                  <p class="text-2xl font-bold text-gray-900">{{ metricas.banhosRealizados }}</p>
                  <p class="text-xs text-blue-600 font-medium">🛁 {{ new Date().toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' }) }}</p>
                </div>
              </div>
          </div>
          </BaseCard>
        </div>

        <!-- 📊 Informações Gerais -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">

          <!-- Resumo de Clientes/Animais -->
          <BaseCard class="shadow-xl border-0 lg:col-span-1">
            <div class="p-6">
              <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
                <FontAwesomeIcon :icon="['fas', 'users']" class="text-blue-600" />
                Base de Clientes
              </h3>

              <div class="space-y-4">
                <div class="flex items-center justify-between p-3 bg-gradient-to-r from-blue-50 to-purple-50 rounded-lg">
                  <div class="flex items-center gap-3">
                    <div class="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                      <FontAwesomeIcon :icon="['fas', 'user']" class="text-white text-sm" />
                    </div>
                    <span class="font-medium text-gray-700">Clientes</span>
                  </div>
                  <span class="text-xl font-bold text-gray-900">{{ metricas.totalClientes }}</span>
                </div>

                <div class="flex items-center justify-between p-3 bg-gradient-to-r from-green-50 to-emerald-50 rounded-lg">
                  <div class="flex items-center gap-3">
                    <div class="w-8 h-8 bg-green-500 rounded-full flex items-center justify-center">
                      <FontAwesomeIcon :icon="['fas', 'paw']" class="text-white text-sm" />
                    </div>
                    <span class="font-medium text-gray-700">Animais</span>
                  </div>
                  <span class="text-xl font-bold text-gray-900">{{ metricas.totalAnimais }}</span>
                </div>

                <div class="flex items-center justify-between p-3 bg-gradient-to-r from-purple-50 to-pink-50 rounded-lg">
                  <div class="flex items-center gap-3">
                    <div class="w-8 h-8 bg-purple-500 rounded-full flex items-center justify-center">
                      <FontAwesomeIcon :icon="['fas', 'clipboard-list']" class="text-white text-sm" />
                    </div>
                    <span class="font-medium text-gray-700">Serviços</span>
                  </div>
                  <span class="text-xl font-bold text-gray-900">{{ metricas.totalServicos }}</span>
                </div>
              </div>
            </div>
          </BaseCard>

          <!-- Status de Pacotes -->
          <BaseCard class="shadow-xl border-0 lg:col-span-2">
            <div class="p-6">
              <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
                <FontAwesomeIcon :icon="['fas', 'box']" class="text-violet-600" />
                Status dos Pacotes
              </h3>

              <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <!-- Pacotes Válidos -->
                <div class="p-4 bg-gradient-to-br from-green-50 to-emerald-50 rounded-xl border border-green-200">
                  <div class="text-center">
                    <div class="w-10 h-10 bg-green-500 rounded-full flex items-center justify-center mx-auto mb-2">
                      <FontAwesomeIcon :icon="['fas', 'check-circle']" class="text-white" />
                    </div>
                    <p class="text-2xl font-bold text-green-800">{{ metricas.pacotesValidos }}</p>
                    <p class="text-sm text-green-600 font-medium">Válidos</p>
                  </div>
                </div>

                <!-- Pacotes Vencendo -->
                <div class="p-4 bg-gradient-to-br from-yellow-50 to-amber-50 rounded-xl border border-yellow-200">
                  <div class="text-center">
                    <div class="w-10 h-10 bg-yellow-500 rounded-full flex items-center justify-center mx-auto mb-2 animate-pulse">
                      <FontAwesomeIcon :icon="['fas', 'clock']" class="text-white" />
                    </div>
                    <p class="text-2xl font-bold text-yellow-800">{{ metricas.pacotesVencendo }}</p>
                    <p class="text-sm text-yellow-600 font-medium">Vencendo</p>
                  </div>
                </div>

                <!-- Pacotes Vencidos -->
                <div class="p-4 bg-gradient-to-br from-red-50 to-pink-50 rounded-xl border border-red-200">
                  <div class="text-center">
                    <div class="w-10 h-10 bg-red-500 rounded-full flex items-center justify-center mx-auto mb-2">
                      <FontAwesomeIcon :icon="['fas', 'exclamation-triangle']" class="text-white" />
                    </div>
                    <p class="text-2xl font-bold text-red-800">{{ metricas.pacotesVencidos }}</p>
                    <p class="text-sm text-red-600 font-medium">Vencidos</p>
                  </div>
                </div>
              </div>
        </div>
      </BaseCard>
    </div>

        <!-- 🚀 Ações Rápidas -->
        <BaseCard class="shadow-xl border-0 mb-8">
          <div class="p-6">
            <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
              <FontAwesomeIcon :icon="['fas', 'bolt']" class="text-amber-600" />
              Ações Rápidas
            </h3>

            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              <button
                @click="$router.push('/animal-servico/novo')"
                class="group p-4 bg-gradient-to-br from-blue-50 to-blue-100 rounded-xl hover:from-blue-100 hover:to-blue-200 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg"
              >
                <div class="text-center">
                  <div class="w-12 h-12 bg-blue-500 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:scale-110 transition-transform">
                    <FontAwesomeIcon :icon="['fas', 'plus']" class="text-white text-xl" />
                  </div>
                  <p class="font-medium text-gray-800">Novo Serviço</p>
                  <p class="text-xs text-gray-600">Registrar atendimento</p>
                </div>
              </button>

              <button
                @click="$router.push('/clientes/novo')"
                class="group p-4 bg-gradient-to-br from-green-50 to-green-100 rounded-xl hover:from-green-100 hover:to-green-200 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg"
              >
                <div class="text-center">
                  <div class="w-12 h-12 bg-green-500 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:scale-110 transition-transform">
                    <FontAwesomeIcon :icon="['fas', 'user-plus']" class="text-white text-xl" />
                  </div>
                  <p class="font-medium text-gray-800">Novo Cliente</p>
                  <p class="text-xs text-gray-600">Cadastrar pessoa</p>
                </div>
              </button>

              <button
          @click="$router.push('/animais/novo')"
                class="group p-4 bg-gradient-to-br from-purple-50 to-purple-100 rounded-xl hover:from-purple-100 hover:to-purple-200 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg"
              >
                <div class="text-center">
                  <div class="w-12 h-12 bg-purple-500 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:scale-110 transition-transform">
                    <FontAwesomeIcon :icon="['fas', 'paw']" class="text-white text-xl" />
                  </div>
                  <p class="font-medium text-gray-800">Novo Animal</p>
                  <p class="text-xs text-gray-600">Cadastrar pet</p>
                </div>
              </button>

              <button
                @click="$router.push('/animal-servico')"
                class="group p-4 bg-gradient-to-br from-orange-50 to-orange-100 rounded-xl hover:from-orange-100 hover:to-orange-200 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg"
              >
                <div class="text-center">
                  <div class="w-12 h-12 bg-orange-500 rounded-xl flex items-center justify-center mx-auto mb-3 group-hover:scale-110 transition-transform">
                    <FontAwesomeIcon :icon="['fas', 'list']" class="text-white text-xl" />
                  </div>
                  <p class="font-medium text-gray-800">Ver Serviços</p>
                  <p class="text-xs text-gray-600">Gerenciar lista</p>
                </div>
              </button>
            </div>
      </div>
    </BaseCard>

        <!-- 📋 Atividade Recente (se houver dados) -->
        <BaseCard v-if="atividadeRecente.length > 0" class="shadow-xl border-0">
          <div class="p-6">
            <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
              <FontAwesomeIcon :icon="['fas', 'history']" class="text-cyan-600" />
              Atividade Recente
            </h3>

            <div class="space-y-3">
              <div
                v-for="(atividade, index) in atividadeRecente.slice(0, 5)"
                :key="index"
                class="flex items-center gap-4 p-3 bg-gradient-to-r from-gray-50 to-slate-50 rounded-lg hover:from-gray-100 hover:to-slate-100 transition-colors"
              >
                <div class="w-8 h-8 bg-gradient-to-r from-cyan-500 to-blue-500 rounded-full flex items-center justify-center">
                  <FontAwesomeIcon :icon="['fas', 'bath']" class="text-white text-sm" />
                </div>
                <div class="flex-1">
                  <p class="font-medium text-gray-800">{{ atividade.descricao }}</p>
                  <p class="text-xs text-gray-500">{{ atividade.tempo }}</p>
        </div>
        </div>
        </div>
      </div>
    </BaseCard>

      </div>
    </div>

    <!-- Botão de Configurações (canto inferior direito) -->
    <div class="fixed bottom-6 right-6 z-50">
      <button
        @click="$router.push('/configuracoes')"
        class="group w-14 h-14 bg-gradient-to-r from-blue-500 to-purple-500 rounded-full flex items-center justify-center shadow-lg hover:shadow-xl transform hover:scale-110 transition-all duration-300"
        title="Configurações"
      >
        <FontAwesomeIcon :icon="['fas', 'cog']" class="text-white text-xl group-hover:rotate-180 transition-transform duration-300" />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import BaseCard from '@/components/UI/BaseCard.vue'
import { animaisService, clientesService, animalServicoService, servicosService } from '@/services/api'
import type { Animal, Cliente, AnimalServico, ServicoCompleto } from '@/types/api'

// 📊 Estados Reativos
const loading = ref(true)
const error = ref('')

// 📈 Dados da API
const animais = ref<Animal[]>([])
const clientes = ref<Cliente[]>([])
const animalServicos = ref<AnimalServico[]>([])
const servicos = ref<ServicoCompleto[]>([])

// 🕐 Data e Saudação
const saudacao = computed(() => {
  const hora = new Date().getHours()
  if (hora < 12) return 'Bom dia'
  if (hora < 18) return 'Boa tarde'
  return 'Boa noite'
})

const dataAtual = computed(() => {
  return new Date().toLocaleDateString('pt-BR', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
})

// 📊 Métricas Calculadas
const metricas = computed(() => {
  // 📅 Filtro do mês atual
  const hoje = new Date()
  const anoAtual = hoje.getFullYear()
  const mesAtual = hoje.getMonth() + 1 // getMonth() retorna 0-11, precisamos 1-12

  console.log(`📅 Calculando métricas para: ${mesAtual}/${anoAtual}`)

  // 🐛 Debug: Log dos dados para entender o problema
  console.log('🔍 Debug - Calculando métricas:', {
    animalServicos: animalServicos.value.length,
    servicos: servicos.value.length
  })

  // Log da estrutura dos objetos
  if (animalServicos.value[0]) {
    console.log('📋 Primeiro AnimalServico:', animalServicos.value[0])
    console.log('🔗 Chaves do AnimalServico:', Object.keys(animalServicos.value[0]))
  }

  if (servicos.value[0]) {
    console.log('💼 Primeiro Servico:', servicos.value[0])
    console.log('🔗 Chaves do Servico:', Object.keys(servicos.value[0]))
  }

  // Log de todos os IDs de serviços disponíveis
  const idsServicosDisponiveis = servicos.value.map(s => s.id)
  const servicoIdsReferenciados = animalServicos.value.map(as => as.servicoId || 'UNDEFINED')

  console.log('🆔 IDs dos serviços disponíveis:', idsServicosDisponiveis)
  console.log('🆔 servicoIds dos animalServicos:', servicoIdsReferenciados)
  console.log('🔧 Detalhes dos serviços disponíveis:', servicos.value.map(s => ({
    id: s.id,
    nome: s.nome,
    valor: s.valor
  })))

  // Encontrar IDs órfãos
  const idsOrfaos = servicoIdsReferenciados.filter(id =>
    id !== 'UNDEFINED' && !idsServicosDisponiveis.includes(id)
  )
  console.log('💀 IDs órfãos (não existem na tabela servico):', idsOrfaos)

  // Contar quantos animalServicos têm relacionamento válido
  const animalServicosValidos = animalServicos.value.filter(as =>
    as.servicoId && idsServicosDisponiveis.includes(as.servicoId)
  )
  console.log('✅ AnimalServicos com relacionamento válido:', animalServicosValidos.length)

  // Mostrar um exemplo de animalServico com problema
  const animalServicoComProblema = animalServicos.value.find(as =>
    as.servicoId && !idsServicosDisponiveis.includes(as.servicoId)
  )
  if (animalServicoComProblema) {
    console.log('🚨 Exemplo de AnimalServico com problema:', animalServicoComProblema)
  }

  // Função para verificar se é do mês atual (para cadastro de serviços)
  const isServicoDoMesAtual = (dataServico: string) => {
    const data = new Date(dataServico + 'T00:00:00')
    const isDoMes = data.getFullYear() === anoAtual && (data.getMonth() + 1) === mesAtual

    if (isDoMes) {
      console.log(`📅 Serviço cadastrado no mês: ${dataServico}`)
    }

    return isDoMes
  }

  // Função para verificar se foi PAGO no mês atual (para receita)
  const isPagamentoDoMesAtual = (dataPagamento: string | null) => {
    if (!dataPagamento) return false

    const data = new Date(dataPagamento + 'T00:00:00')
    const isDoMes = data.getFullYear() === anoAtual && (data.getMonth() + 1) === mesAtual

    if (isDoMes) {
      console.log(`💰 Pagamento realizado no mês: ${dataPagamento}`)
    }

    return isDoMes
  }

  // Filtrar apenas animalServicos com relacionamento válido
  const animalServicosComRelacao = animalServicos.value.filter(as => {
    // Tenta acessar servicoId ou servico.id
    const servicoId = as.servicoId || as.servico?.id
    console.log(`🔍 AnimalServico ${as.id}: servicoId=${as.servicoId}, servico.id=${as.servico?.id}, servicoValido=${!!servicoId}`)
    return servicoId && idsServicosDisponiveis.includes(servicoId)
  })

  // Log das datas de todos os serviços válidos para ver o padrão
  console.log('📅 Datas dos serviços válidos:', animalServicosComRelacao.map(as => ({
    id: as.id,
    dataServico: as.dataServico,
    statusPagamento: as.statusPagamento
  })))

  // Separar lógicas diferentes:
  // 1. Serviços cadastrados no mês (para "em aberto" e "banhos realizados")
  const animalServicosCadastradosNoMes = animalServicosComRelacao.filter(as =>
    isServicoDoMesAtual(as.dataServico)
  )

  // 2. Serviços PAGOS no mês (para "receita total") - baseado na dataPagamento
  const animalServicosPagosNoMes = animalServicosComRelacao.filter(as =>
    as.statusPagamento === 'pago' && isPagamentoDoMesAtual(as.dataPagamento || null)
  )

  console.log(`📊 Trabalhando com ${animalServicosComRelacao.length}/${animalServicos.value.length} AnimalServicos válidos`)
  console.log(`📅 Cadastrados no mês: ${animalServicosCadastradosNoMes.length}`)
  console.log(`💰 Pagos no mês: ${animalServicosPagosNoMes.length}`)

  // Cálculos corretos:
  // - Receita: Serviços pagos no mês (por dataPagamento)
  // - Em Aberto: Serviços cadastrados no mês que estão em aberto
  const servicosPagos = animalServicosPagosNoMes // Já filtrados por pagamento do mês
  const servicosEmAberto = animalServicosCadastradosNoMes.filter(s => s.statusPagamento === 'em_aberto')

  // Para pacotes vencidos, usar todos os válidos (não apenas do mês)
  const todosServicosValidos = animalServicosComRelacao.filter(s => s.statusPagamento !== 'cancelado')

  console.log('💰 Status dos pagamentos (mês atual):', {
    cadastradosNoMes: animalServicosCadastradosNoMes.length,
    pagosNoMes: servicosPagos.length,
    emAbertoNoMes: servicosEmAberto.length,
    canceladosNoMes: animalServicosCadastradosNoMes.filter(s => s.statusPagamento === 'cancelado').length
  })

  // Log detalhado dos serviços pagos
  console.log('💳 Serviços PAGOS do mês (por dataPagamento):', servicosPagos.map(s => ({
    id: s.id,
    dataServico: s.dataServico,
    dataPagamento: s.dataPagamento,
    servicoId: s.servicoId,
    statusPagamento: s.statusPagamento
  })))

  // Log detalhado dos serviços em aberto
  console.log('⏳ Serviços EM ABERTO do mês (por dataServico):', servicosEmAberto.map(s => ({
    id: s.id,
    dataServico: s.dataServico,
    dataPagamento: s.dataPagamento,
    servicoId: s.servicoId,
    statusPagamento: s.statusPagamento
  })))

  // Função helper para encontrar valor do serviço
  const obterValorServico = (animalServico: any) => {
    const servicoId = animalServico.servicoId || animalServico.servico?.id

    console.log(`🔍 Buscando valor para AnimalServico ${animalServico.id}:`, {
      servicoId: animalServico.servicoId,
      servicoObjectId: animalServico.servico?.id,
      servicoIdFinal: servicoId,
      hasServicoEmbed: !!animalServico.servico,
      servicoEmbedValor: animalServico.servico?.valor
    })

    // Primeiro tenta pelo servicoId (ou servico.id)
    if (servicoId) {
      const servicoInfo = servicos.value.find(s => s.id === servicoId)
      if (servicoInfo) {
        console.log(`✅ Valor encontrado via servicoId/servico.id: R$ ${servicoInfo.valor}`)
        return servicoInfo.valor
      } else {
        console.log(`⚠️ ServiceId ${servicoId} não encontrado na lista de serviços`)
        console.log('🔍 Serviços disponíveis:', servicos.value.map(s => s.id))
      }
    }

    // Se não encontrou, tenta pelo objeto servico embed valor direto
    if (animalServico.servico?.valor) {
      console.log(`✅ Valor encontrado via embed direto: R$ ${animalServico.servico.valor}`)
      return animalServico.servico.valor
    }

    console.log(`❌ FALHA: Valor não encontrado para AnimalServico ID ${animalServico.id}`)
    return 0
  }

  const receitaTotal = servicosPagos.reduce((total, servico) => {
    const valor = obterValorServico(servico)
    if (valor > 0) {
      console.log(`✅ Receita: R$ ${valor} do serviço ID ${servico.id}`)
    }
    return total + valor
  }, 0)

  const valorEmAberto = servicosEmAberto.reduce((total, servico) => {
    const valor = obterValorServico(servico)
    if (valor > 0) {
      console.log(`📋 Em aberto: R$ ${valor} do serviço ID ${servico.id}`)
    }
    return total + valor
  }, 0)

  console.log('💰 RESULTADO - Cálculos financeiros do mês:', {
    receitaTotal: `R$ ${receitaTotal}`,
    valorEmAberto: `R$ ${valorEmAberto}`,
    servicosPagosCount: servicosPagos.length,
    servicosEmAbertoCount: servicosEmAberto.length,
    mesAtual: `${mesAtual}/${anoAtual}`
  })

  if (receitaTotal === 0 && servicosPagos.length > 0) {
    console.log('🚨 PROBLEMA DETECTADO: Há serviços pagos mas receita = 0!')
    console.log('🔍 Primeiro serviço pago:', servicosPagos[0])
  }

  if (valorEmAberto === 0 && servicosEmAberto.length > 0) {
    console.log('🚨 PROBLEMA DETECTADO: Há serviços em aberto mas valor = 0!')
    console.log('🔍 Primeiro serviço em aberto:', servicosEmAberto[0])
  }

  // Cálculos de banhos (serviços cadastrados no mês atual)
  const totalBanhosRealizados = animalServicosCadastradosNoMes.reduce((total, servico) => {
    return total + (servico.banhosUsados || 0)
  }, 0)

  // Status de pacotes - função helper para obter quantidade do serviço
  const obterQuantidadeServico = (animalServico: any) => {
    const servicoId = animalServico.servicoId || animalServico.servico?.id

    // Primeiro tenta pelo servicoId (ou servico.id)
    if (servicoId) {
      const servicoInfo = servicos.value.find(s => s.id === servicoId)
      if (servicoInfo) return servicoInfo.quantidade
    }

    // Se não encontrou, tenta pelo objeto servico embed
    if (animalServico.servico?.quantidade) {
      return animalServico.servico.quantidade
    }

    return 1 // Default para banho único
  }

  const servicosComExpiracao = todosServicosValidos.filter(s => {
    const quantidade = obterQuantidadeServico(s)
    return s.dataExpiracao && quantidade > 1
  })

  console.log('📦 Serviços com expiração:', {
    total: servicosComExpiracao.length,
    servicos: servicosComExpiracao.map(s => ({
      id: s.id,
      dataExpiracao: s.dataExpiracao,
      quantidade: obterQuantidadeServico(s)
    }))
  })

  const pacotesVencidos = servicosComExpiracao.filter(servico => {
    if (!servico.dataExpiracao) return false
    const dataExpiracao = new Date(servico.dataExpiracao + 'T23:59:59')
    return dataExpiracao < hoje
  }).length

  const pacotesVencendo = servicosComExpiracao.filter(servico => {
    if (!servico.dataExpiracao) return false
    const dataExpiracao = new Date(servico.dataExpiracao + 'T23:59:59')
    const diasRestantes = Math.ceil((dataExpiracao.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24))
    return diasRestantes >= 0 && diasRestantes <= 7
  }).length

  const pacotesValidos = servicosComExpiracao.filter(servico => {
    if (!servico.dataExpiracao) return false
    const dataExpiracao = new Date(servico.dataExpiracao + 'T23:59:59')
    const diasRestantes = Math.ceil((dataExpiracao.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24))
    return diasRestantes > 7
  }).length

  console.log('📦 Status dos pacotes:', {
    vencidos: pacotesVencidos,
    vencendo: pacotesVencendo,
    validos: pacotesValidos
  })

  const resultado = {
    // Financeiro
    receitaTotal,
    valorEmAberto,
    servicosEmAberto: servicosEmAberto.length,

    // Totais
    totalClientes: clientes.value.length,
    totalAnimais: animais.value.length,
    totalServicos: animalServicosCadastradosNoMes.length,
    banhosRealizados: totalBanhosRealizados,

    // Pacotes
    pacotesVencidos,
    pacotesVencendo,
    pacotesValidos
  }

  console.log('📊 Métricas finais:', resultado)
  return resultado
})

// 📋 Atividade Recente
const atividadeRecente = computed(() => {
  const atividades: Array<{ descricao: string; tempo: string }> = []

  // Pegar os últimos 10 serviços ordenados por data
  const servicosRecentes = animalServicos.value
    .sort((a, b) => new Date(b.dataServico).getTime() - new Date(a.dataServico).getTime())
    .slice(0, 10)

  servicosRecentes.forEach(servico => {
    const animal = animais.value.find(a => a.id === servico.animalId)
    const servicoInfo = servicos.value.find(s => s.id === servico.servicoId)
    const cliente = clientes.value.find(c => c.id === animal?.cliente?.id)

    if (animal && servicoInfo && cliente) {
      const dataServico = new Date(servico.dataServico)
      const tempo = formatarTempoRelativo(dataServico)

      atividades.push({
        descricao: `${servicoInfo.nome} para ${animal.nome} (${cliente.nomeCompleto})`,
        tempo
      })
    }
  })

  return atividades
})

// 🛠️ Funções Auxiliares
const formatarValor = (valor: number): string => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(valor)
}

const formatarTempoRelativo = (data: Date): string => {
  const agora = new Date()
  const diffMs = agora.getTime() - data.getTime()
  const diffDias = Math.floor(diffMs / (1000 * 60 * 60 * 24))

  if (diffDias === 0) return 'Hoje'
  if (diffDias === 1) return 'Ontem'
  if (diffDias < 7) return `${diffDias} dias atrás`
  if (diffDias < 30) return `${Math.floor(diffDias / 7)} semanas atrás`
  return `${Math.floor(diffDias / 30)} meses atrás`
}

// 🔄 Carregamento de Dados
const carregarDados = async () => {
  try {
    loading.value = true
    error.value = ''

    console.log('🚀 Carregando dados da dashboard...')

    // Buscar dados em paralelo
    const [
      animaisData,
      clientesData,
      animalServicosData,
      servicosData
    ] = await Promise.all([
      animaisService.buscarTodos(),
      clientesService.buscarTodos(),
      animalServicoService.buscarTodos(),
      servicosService.buscarTodos()
    ])

    // Atualizar dados reativos
    animais.value = animaisData
    clientes.value = clientesData
    animalServicos.value = animalServicosData
    servicos.value = servicosData

    console.log('✅ Dados carregados com sucesso!')
    console.log({
      animais: animaisData.length,
      clientes: clientesData.length,
      animalServicos: animalServicosData.length,
      servicos: servicosData.length
    })

  } catch (err) {
    console.error('❌ Erro ao carregar dados da dashboard:', err)
    error.value = 'Erro ao carregar dados. Tente novamente.'
  } finally {
    loading.value = false
  }
}

// 🎯 Lifecycle
onMounted(() => {
  carregarDados()
})
</script>
