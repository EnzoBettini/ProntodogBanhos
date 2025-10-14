<!-- 📋 Página de Listagem de Serviços -->
<!-- Visual profissional e corporativo com cores amarelo/verde -->
<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 🏢 Header Profissional -->
    <div class="relative overflow-hidden bg-gradient-to-r from-amber-700 via-yellow-600 to-amber-800 text-white pt-16">
      <!-- ✨ Efeito de brilho sutil -->
      <div class="absolute inset-0 opacity-20">
        <div class="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent transform -skew-x-12 animate-pulse"></div>
      </div>

      <div class="relative z-20 max-w-7xl mx-auto px-4 py-12">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="relative">
              <div class="w-16 h-16 bg-gradient-to-br from-yellow-400 to-amber-500 rounded-2xl flex items-center justify-center shadow-2xl">
                <FontAwesomeIcon
                  :icon="['fas', 'cog']"
                  class="text-2xl text-white drop-shadow-lg transform rotate-12 animate-bounce-gentle"
                />
              </div>
              <!-- Badge com contador -->
              <div class="absolute -top-2 -right-2 bg-gradient-to-r from-green-500 to-emerald-600 text-white text-xs font-bold px-2 py-1 rounded-full shadow-lg animate-pulse">
                {{ servicosCount }}
              </div>
            </div>

            <div>
              <h1 class="text-4xl font-bold flex items-center gap-3">
                <FontAwesomeIcon :icon="['fas', 'cog']" class="text-yellow-300 animate-twinkle mr-1" />
                Gestão de Serviços
              </h1>
              <p class="text-xl opacity-90 font-medium">Administração profissional de serviços</p>
            </div>
          </div>

          <div class="flex gap-3">
            <!-- Botão Adicionar Serviço -->
            <RouterLink
              to="/servicos/novo"
              class="group bg-gradient-to-r from-green-600 to-emerald-700 hover:from-green-700 hover:to-emerald-800 px-6 py-3 rounded-xl font-medium shadow-lg hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-1 flex items-center gap-2"
            >
              <FontAwesomeIcon
                :icon="['fas', 'plus']"
                class="group-hover:rotate-90 transition-transform duration-300"
              />
              Novo Serviço
            </RouterLink>

            <!-- Botão Relatórios -->
            <button
              class="group bg-gradient-to-r from-amber-600 to-yellow-700 hover:from-amber-700 hover:to-yellow-800 px-6 py-3 rounded-xl font-medium shadow-lg hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-1 flex items-center gap-2"
            >
              <FontAwesomeIcon
                :icon="['fas', 'chart-line']"
                class="group-hover:scale-110 transition-transform duration-300"
              />
              Relatórios
            </button>
          </div>
        </div>
      </div>

      <!-- 🌊 Wave Decoration Profissional -->
      <div class="absolute bottom-0 left-0 right-0 z-10 overflow-hidden">
        <svg viewBox="0 0 1440 100" class="w-full h-20" preserveAspectRatio="none">
          <defs>
            <linearGradient id="waveGradientServices" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" style="stop-color:#10b981;stop-opacity:0.9" />
              <stop offset="30%" style="stop-color:#059669;stop-opacity:0.8" />
              <stop offset="70%" style="stop-color:#047857;stop-opacity:0.7" />
              <stop offset="100%" style="stop-color:#064e3b;stop-opacity:0.6" />
            </linearGradient>
            <!-- Sombra sutil -->
            <filter id="waveShadow">
              <feDropShadow dx="0" dy="2" stdDeviation="4" flood-opacity="0.2"/>
            </filter>
          </defs>
          <!-- Wave suave e profissional -->
          <path
            d="M0,50 C240,30 480,70 720,50 C960,30 1200,70 1440,50 L1440,100 L0,100 Z"
            fill="url(#waveGradientServices)"
            filter="url(#waveShadow)"
          />
          <!-- Wave secundária para profundidade -->
          <path
            d="M0,60 C360,35 600,75 960,55 C1200,35 1320,80 1440,60 L1440,100 L0,100 Z"
            fill="url(#waveGradientServices)"
            opacity="0.4"
          />
        </svg>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 py-8">

      <!-- 🔍 Seção de Filtros e Estatísticas -->
      <BaseCard class="mb-8 bg-gradient-to-r from-white via-yellow-50/30 to-amber-50/20">
        <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
          <!-- Campo de busca -->
          <div class="flex-1 max-w-md relative">
            <div class="relative group">
              <input
                type="text"
                v-model="filtroBusca"
                placeholder="Buscar por nome, descrição ou valor..."
                class="w-full pl-12 pr-4 py-3 border-2 border-amber-200 rounded-xl focus:border-amber-500 focus:ring-4 focus:ring-amber-500/20 transition-all duration-300 bg-white/80 backdrop-blur-sm"
                @input="resetarPaginacao"
              >
              <FontAwesomeIcon
                :icon="['fas', 'search']"
                class="absolute left-4 top-1/2 transform -translate-y-1/2 text-amber-500 group-focus-within:text-amber-600 transition-colors"
              />
            </div>

            <!-- Badge de busca ativa -->
            <div v-if="filtroBusca" class="mt-2">
              <span class="inline-flex items-center gap-1 px-3 py-1 bg-gradient-to-r from-amber-100 to-yellow-100 text-amber-700 rounded-full text-sm font-medium">
                <FontAwesomeIcon :icon="['fas', 'filter']" class="text-xs" />
                Buscando: "{{ filtroBusca }}"
              </span>
            </div>
          </div>

          <!-- Estatísticas em tempo real -->
          <div class="flex flex-wrap gap-4">
            <div class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-amber-100 to-yellow-100 rounded-xl">
              <FontAwesomeIcon :icon="['fas', 'cog']" class="text-amber-600" />
              <span class="text-sm font-medium text-amber-800">
                {{ totalItensDisponiveis }} de {{ servicosCount }} encontrados
              </span>
            </div>

            <div class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-green-100 to-emerald-100 rounded-xl">
              <FontAwesomeIcon :icon="['fas', 'dollar-sign']" class="text-green-600" />
              <span class="text-sm font-medium text-green-800">
                Catálogo: R$ {{ valorTotalServicos }}
              </span>
            </div>
          </div>
        </div>
      </BaseCard>

      <!-- ⏳ Estado de Loading -->
      <div v-if="loading" class="flex items-center justify-center py-20">
        <div class="text-center space-y-4">
          <div class="relative">
            <div class="w-16 h-16 bg-gradient-to-br from-amber-500 to-yellow-600 rounded-2xl flex items-center justify-center shadow-xl animate-bounce">
              <FontAwesomeIcon :icon="['fas', 'spinner']" class="text-2xl text-white animate-spin" />
            </div>
            <div class="absolute inset-0 bg-gradient-to-r from-amber-400/30 to-yellow-500/30 rounded-2xl blur-xl animate-pulse"></div>
          </div>
          <div class="space-y-2">
            <h3 class="text-xl font-semibold text-gray-700">Carregando serviços...</h3>
            <p class="text-gray-500">Organizando dados profissionais</p>
          </div>
        </div>
      </div>

      <!-- ❌ Estado de Erro -->
      <div v-else-if="error" class="flex items-center justify-center py-20">
        <BaseCard class="text-center max-w-md bg-gradient-to-br from-red-50 to-pink-50 border-red-200">
          <div class="space-y-4">
            <div class="w-16 h-16 bg-gradient-to-br from-red-500 to-pink-600 rounded-2xl flex items-center justify-center mx-auto shadow-xl">
              <FontAwesomeIcon :icon="['fas', 'exclamation-triangle']" class="text-2xl text-white" />
            </div>
            <div>
              <h3 class="text-xl font-semibold text-red-700 mb-2">Erro ao carregar serviços</h3>
              <p class="text-red-600 mb-4">{{ error }}</p>
              <BaseButton variant="danger" @click="recarregarDados">
                <FontAwesomeIcon :icon="['fas', 'refresh']" class="mr-2" />
                Tentar Novamente
              </BaseButton>
            </div>
          </div>
        </BaseCard>
      </div>

      <!-- 📭 Estado Vazio -->
      <div v-else-if="isEmpty" class="flex items-center justify-center py-20">
        <BaseCard class="text-center max-w-md bg-gradient-to-br from-amber-50 to-yellow-50 border-amber-200">
          <div class="space-y-6">
            <div class="relative">
              <div class="w-20 h-20 bg-gradient-to-br from-amber-500 to-yellow-600 rounded-3xl flex items-center justify-center mx-auto shadow-xl">
                <FontAwesomeIcon :icon="['fas', 'cog']" class="text-3xl text-white" />
              </div>
              <div class="absolute -top-1 -right-1 w-8 h-8 bg-gradient-to-br from-orange-400 to-red-500 rounded-full flex items-center justify-center">
                <FontAwesomeIcon :icon="['fas', 'plus']" class="text-sm text-white" />
              </div>
            </div>
            <div>
              <h3 class="text-2xl font-semibold text-amber-700 mb-2">Nenhum serviço cadastrado</h3>
              <p class="text-amber-600 mb-6">Comece criando seu primeiro serviço profissional</p>
              <RouterLink to="/servicos/novo">
                <BaseButton variant="primary" class="bg-gradient-to-r from-green-600 to-emerald-700 hover:from-green-700 hover:to-emerald-800">
                  <FontAwesomeIcon :icon="['fas', 'plus']" class="mr-2" />
                  Criar Primeiro Serviço
                </BaseButton>
              </RouterLink>
            </div>
          </div>
        </BaseCard>
      </div>

      <!-- 📋 Lista de Serviços -->
      <div v-else class="space-y-6">
        <div ref="servicosListRef" class="space-y-4">
          <div
            v-for="servico in servicosExibidos"
            :key="servico.id"
            class="group bg-gradient-to-r from-white via-white to-amber-50/30 rounded-2xl shadow-lg hover:shadow-2xl border border-amber-100/50 transition-all duration-300 transform hover:-translate-y-1 p-6 cursor-pointer"
            @click="visualizarServico(servico.id)"
          >
            <div class="flex items-start justify-between">
              <!-- Informações principais -->
              <div class="flex gap-4 flex-1">
                <!-- Avatar do serviço -->
                <div class="relative flex-shrink-0">
                  <div class="w-16 h-16 bg-gradient-to-br from-amber-500 to-yellow-600 rounded-2xl flex items-center justify-center shadow-lg">
                    <FontAwesomeIcon :icon="['fas', 'cog']" class="text-xl text-white drop-shadow-sm" />
                  </div>
                  <!-- Badge de banhos -->
                  <div class="absolute -top-2 -right-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white text-xs font-bold px-2 py-1 rounded-full shadow-lg">
                    {{ servico.quantidade }}{{ servico.quantidade === 1 ? '' : 'x' }}
                  </div>
                </div>

                <!-- Detalhes do serviço -->
                <div class="flex-1 min-w-0">
                  <div class="flex items-start justify-between mb-3">
                    <div>
                      <h3 class="text-xl font-bold text-gray-800 group-hover:text-amber-700 transition-colors">
                        {{ servico.nome }}
                      </h3>
                      <p class="text-gray-600 text-sm line-clamp-2">{{ servico.descricao || 'Sem descrição disponível' }}</p>
                    </div>
                    <div class="text-right flex-shrink-0 ml-4">
                      <div class="text-2xl font-bold text-green-600">
                        R$ {{ formatarValor(servico.valor) }}
                      </div>
                      <div class="text-sm text-gray-500">
                        {{ servico.quantidade === 1 ? 'serviço único' : 'pacote completo' }}
                      </div>
                      <div v-if="servico.quantidade > 1" class="text-xs text-blue-600 font-medium">
                        R$ {{ formatarValor(servico.valor / servico.quantidade) }} por banho
                      </div>
                    </div>
                  </div>

                  <!-- Informações detalhadas -->
                  <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
                    <div class="flex items-center gap-2 text-sm">
                      <FontAwesomeIcon :icon="['fas', 'hashtag']" class="text-amber-600" />
                      <span class="text-gray-600">ID: <span class="font-medium">{{ servico.id }}</span></span>
                    </div>

                    <div class="flex items-center gap-2 text-sm">
                      <FontAwesomeIcon :icon="['fas', 'calculator']" class="text-blue-600" />
                      <span class="text-gray-600">
                        {{ servico.quantidade === 1 ? 'Banho único' : `${servico.quantidade} banhos` }}
                      </span>
                    </div>

                    <div class="flex items-center gap-2 text-sm">
                      <FontAwesomeIcon :icon="['fas', 'calendar']" class="text-purple-600" />
                      <span class="text-gray-600">Agendamentos: <span class="font-medium">{{ servico.servicosAnimais?.length || 0 }}</span></span>
                    </div>

                    <div class="flex items-center gap-2 text-sm">
                      <FontAwesomeIcon :icon="['fas', 'dollar-sign']" class="text-green-600" />
                      <span class="text-gray-600">
                        <span v-if="servico.quantidade === 1">
                          Valor: <span class="font-medium text-green-600">R$ {{ formatarValor(servico.valor) }}</span>
                        </span>
                        <span v-else>
                          Por banho: <span class="font-medium text-green-600">R$ {{ formatarValor(servico.valor / servico.quantidade) }}</span>
                        </span>
                      </span>
                    </div>
                  </div>

                  <!-- Ações do serviço -->
                  <div class="flex items-center justify-between pt-3 border-t border-gray-100">
                    <div class="flex gap-2">
                      <button
                        @click.stop="editarServico(servico.id)"
                        class="flex items-center gap-2 px-3 py-2 bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 text-white rounded-lg text-sm font-medium transition-all duration-300 transform hover:scale-105"
                      >
                        <FontAwesomeIcon :icon="['fas', 'edit']" />
                        Editar
                      </button>

                      <button
                        @click.stop="excluirServico(servico.id)"
                        class="flex items-center gap-2 px-3 py-2 bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 text-white rounded-lg text-sm font-medium transition-all duration-300 transform hover:scale-105"
                      >
                        <FontAwesomeIcon :icon="['fas', 'trash']" />
                        Excluir
                      </button>
                    </div>

                    <!-- Indicador de ação -->
                    <FontAwesomeIcon
                      :icon="['fas', 'chevron-right']"
                      class="text-amber-500 group-hover:text-amber-600 group-hover:translate-x-1 transition-all duration-300"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 🔄 Botão "Carregar Mais" -->
        <div v-if="temMaisItens" class="text-center py-6">
          <BaseButton
            @click="carregarMais"
            variant="secondary"
            class="bg-gradient-to-r from-amber-600 to-yellow-700 hover:from-amber-700 hover:to-yellow-800 text-white border-0 px-8 py-3 text-lg font-medium shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1"
          >
            <FontAwesomeIcon :icon="['fas', 'plus-circle']" class="mr-2" />
            Carregar Mais Serviços
            <span class="ml-2 bg-white/20 px-2 py-1 rounded-full text-sm">
              +{{ Math.min(itensPorPagina, totalItensDisponiveis - itensExibidos) }}
            </span>
          </BaseButton>
        </div>
      </div>

      <!-- 📊 Footer com Estatísticas -->
      <BaseCard v-if="hasData" class="mt-8 bg-gradient-to-r from-gray-50 via-amber-50/30 to-yellow-50/20 border-amber-200/50">
        <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div class="flex items-center gap-6">
            <div class="flex items-center gap-2 text-amber-700">
              <FontAwesomeIcon :icon="['fas', 'cog']" class="text-lg" />
              <span class="font-semibold">{{ servicosCount }} serviços cadastrados</span>
            </div>

            <div class="flex items-center gap-2 text-green-700">
              <FontAwesomeIcon :icon="['fas', 'dollar-sign']" class="text-lg" />
              <span class="font-semibold">R$ {{ valorTotalServicos }} no catálogo</span>
            </div>
          </div>

          <div class="text-sm text-gray-500 flex items-center gap-2">
            <FontAwesomeIcon :icon="['fas', 'clock']" />
            Atualizado às {{ formatarHorario() }}
          </div>
        </div>
      </BaseCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { BaseCard, BaseButton } from '@/components/UI'
import { servicosService } from '@/services/api'
import { useAsyncList } from '@/composables/useAsyncState'
import { useAutoAnimateList } from '@/composables/useAutoAnimate'
import { formatarHorario } from '@/utils/formatters'
import type { ServicoCompleto } from '@/types/api'

// 🎯 Configuração inicial
const router = useRouter()

// 📊 Estados da lista
const {
  items: servicos,
  loading,
  error,
  isEmpty,
  hasData,
  execute: carregarServicos,
  reset: resetarServicos
} = useAsyncList<ServicoCompleto>()

// 🔍 Estados de busca e paginação
const filtroBusca = ref('')
const itensPorPagina = ref(10)
const itensExibidos = ref(10)

// 📋 Referência para animações
const servicosListRef = ref<HTMLElement>()
useAutoAnimateList(servicosListRef)

// 💡 Computed properties
const servicosFiltrados = computed(() => {
  if (!servicos.value) return []

  if (!filtroBusca.value.trim()) {
    return servicos.value
  }

  const termoBusca = filtroBusca.value.toLowerCase().trim()

  return servicos.value.filter(servico => {
    const nomeMatch = servico.nome?.toLowerCase().includes(termoBusca)
    const descricaoMatch = servico.descricao?.toLowerCase().includes(termoBusca)
    const valorMatch = servico.valor?.toString().includes(termoBusca)
    const idMatch = servico.id?.toString().includes(termoBusca)

    return nomeMatch || descricaoMatch || valorMatch || idMatch
  })
})

const servicosExibidos = computed(() => {
  return servicosFiltrados.value.slice(0, itensExibidos.value)
})

const temMaisItens = computed(() => {
  return itensExibidos.value < servicosFiltrados.value.length
})

const totalItensDisponiveis = computed(() => {
  return servicosFiltrados.value.length
})

const servicosCount = computed(() => {
  return servicos.value?.length || 0
})

const valorTotalServicos = computed(() => {
  if (!servicos.value) return '0,00'

  // Soma apenas os valores dos serviços (não multiplica por quantidade)
  const total = servicos.value.reduce((acc, servico) => {
    return acc + servico.valor
  }, 0)

  return formatarValor(total)
})

// 🔧 Funções utilitárias
const formatarValor = (valor: number): string => {
  return valor.toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const carregarMais = () => {
  itensExibidos.value += itensPorPagina.value
}

const resetarPaginacao = () => {
  itensExibidos.value = itensPorPagina.value
}

const visualizarServico = (id: number) => {
  console.log('🔍 Visualizando serviço:', id)
  // TODO: Implementar modal de visualização ou navegação
}

const editarServico = (id: number) => {
  console.log('✏️ Editando serviço:', id)
  // TODO: Implementar edição de serviço
  router.push(`/servicos/${id}/editar`)
}

const excluirServico = async (id: number) => {
  if (!confirm('Tem certeza que deseja excluir este serviço?')) return

  try {
    await servicosService.excluir(id)
    console.log('✅ Serviço excluído com sucesso!')
    recarregarDados()
  } catch (error) {
    console.error('❌ Erro ao excluir serviço:', error)
    alert('Erro ao excluir serviço. Tente novamente.')
  }
}

const recarregarDados = () => {
  carregarServicos(() => servicosService.buscarTodos())
}

// 👀 Watchers
watch(filtroBusca, () => {
  resetarPaginacao()
})

// 🎬 Lifecycle
onMounted(() => {
  recarregarDados()
})
</script>

<style scoped>
/* Garante que animações funcionem suavemente */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
