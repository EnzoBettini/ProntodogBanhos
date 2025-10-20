<template>
  <div class="min-h-screen bg-gradient-to-br from-violet-50 via-white to-purple-100">
    <!-- 🌟 Header com gradiente elegante -->
    <div class="relative overflow-hidden bg-gradient-to-r from-violet-600 via-purple-600 to-indigo-700 text-white pt-16">
      <!-- Background pattern -->
      <div class="absolute inset-0 opacity-10 bg-pattern"></div>

      <div class="relative px-6 py-8 z-20">
        <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
          <div class="flex items-center gap-4 animate-fade-in-up">
            <!-- Ícone animado -->
            <div class="relative">
              <div class="w-16 h-16 bg-white bg-opacity-20 backdrop-blur-sm rounded-2xl flex items-center justify-center transform hover:scale-110 transition-all duration-300 shadow-lg">
                <FontAwesome icon="shopping-cart" class="text-2xl text-white animate-bounce-gentle" />
              </div>
              <div v-if="!loading && vendas.length > 0" class="absolute -top-1 -right-1 w-8 h-8 bg-yellow-400 rounded-full flex items-center justify-center animate-pulse">
                <span class="text-xs font-bold text-yellow-800">{{ vendas.length }}</span>
              </div>
            </div>

            <div class="space-y-1">
              <h1 class="text-4xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-violet-100">
                Vendas
              </h1>
              <p class="text-white text-lg flex items-center gap-2 font-medium opacity-90">
                <FontAwesome icon="star" class="text-yellow-400 animate-twinkle mr-1" />
                {{ loading ? 'Carregando...' : `${vendasFiltradas.length} ${vendasFiltradas.length === 1 ? 'venda encontrada' : 'vendas encontradas'}` }}
              </p>
            </div>
          </div>

          <!-- Botões de ação elegantes -->
          <div class="flex items-center gap-3">
            <button
              @click="carregarVendas"
              :disabled="loading"
              class="group flex items-center gap-2 px-4 py-2 bg-white bg-opacity-20 backdrop-blur-sm text-white rounded-xl hover:bg-opacity-30 transition-all duration-300 border border-white border-opacity-20 hover:border-opacity-40 transform hover:-translate-y-1 hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <FontAwesome
                :icon="loading ? 'spinner' : 'refresh'"
                :class="{ 'animate-spin': loading, 'group-hover:rotate-180': !loading }"
                class="transition-transform duration-300"
              />
              <span class="font-medium">{{ loading ? 'Carregando...' : 'Atualizar' }}</span>
            </button>

            <button
              @click="$router.push('/vendas/novo')"
              class="group flex items-center gap-2 px-6 py-2 bg-gradient-to-r from-yellow-400 to-orange-500 text-white rounded-xl hover:from-yellow-500 hover:to-orange-600 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-xl font-medium shadow-lg"
            >
              <FontAwesome icon="plus" class="group-hover:rotate-90 transition-transform duration-300" />
              <span>Nova Venda</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Wave decoration -->
      <div class="absolute bottom-0 left-0 right-0 z-10">
        <svg viewBox="0 0 1440 60" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full h-15">
          <path d="M0 60h1440V0c-120 40-240 60-360 60S840 40 720 60s-240 0-360-60S120 40 0 60z" fill="white"/>
        </svg>
      </div>
    </div>

    <!-- Container principal -->
    <div class="relative -mt-8 px-6 pb-8 z-30">

      <!-- 🔍 Filtros elegantes -->
      <BaseCard v-if="!loading || vendas.length > 0" class="mb-6 shadow-lg border-0 bg-white bg-opacity-90 backdrop-blur-sm animate-slide-up">
        <div class="p-4">
          <div class="flex flex-col gap-4">
            <!-- Campo de busca principal -->
            <div class="flex-1 relative group">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <FontAwesome icon="search" class="h-5 w-5 text-violet-400" />
              </div>
              <input
                v-model="filtroBusca"
                type="text"
                placeholder="Digite nome do cliente ou use: #100037 (Código), @12345678900 (CPF)..."
                class="w-full pl-10 pr-12 py-3 bg-gradient-to-r from-white to-violet-50 border border-violet-200 rounded-xl focus:ring-2 focus:ring-violet-500 focus:border-violet-500 transition-all duration-300 placeholder-gray-400 text-gray-700 shadow-sm"
              />

              <!-- Tooltip de ajuda -->
              <div class="absolute inset-y-0 right-0 pr-3 flex items-center">
                <div class="relative group/tooltip">
                  <FontAwesome icon="info-circle" class="h-4 w-4 text-gray-400 hover:text-violet-500 cursor-help transition-colors" />

                  <!-- Conteúdo do tooltip -->
                  <div class="absolute bottom-full right-0 mb-2 w-72 p-3 bg-gray-900 text-white text-sm rounded-lg shadow-xl opacity-0 invisible group-hover/tooltip:opacity-100 group-hover/tooltip:visible transition-all duration-200 z-50">
                    <div class="space-y-2">
                      <p class="font-semibold text-violet-300">💡 Filtros Inteligentes:</p>
                      <div class="space-y-1 text-xs">
                        <p>📝 <span class="text-violet-200">Nome:</span> Maria Silva</p>
                        <p>🧾 <span class="text-violet-200">Código:</span> <code class="bg-gray-800 px-1 rounded">#100037</code></p>
                        <p>📱 <span class="text-violet-200">CPF:</span> <code class="bg-gray-800 px-1 rounded">@12345678900</code></p>
                      </div>
                      <p class="text-xs text-gray-400 mt-2 pt-2 border-t border-gray-700">
                        Use os símbolos para busca específica!
                      </p>
                    </div>

                    <!-- Seta do tooltip -->
                    <div class="absolute top-full right-4 w-0 h-0 border-l-4 border-r-4 border-t-4 border-transparent border-t-gray-900"></div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 💡 Dicas dos filtros inteligentes -->
            <div v-if="filtroBusca && !loading" class="w-full">
              <div class="flex flex-wrap items-center gap-2 px-3 py-2 bg-gradient-to-r from-violet-50 to-purple-50 rounded-lg border border-violet-100">
                <FontAwesome icon="lightbulb" class="text-violet-500 text-sm" />
                <span class="text-xs text-violet-700 font-medium">Filtros inteligentes:</span>
                <div class="flex flex-wrap gap-2 text-xs">
                  <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-violet-700">
                    <FontAwesome icon="hashtag" class="mr-1" /><code>#100037</code> = Código
                  </span>
                  <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-violet-700">
                    <FontAwesome icon="at" class="mr-1" /><code>@CPF</code> = CPF Cliente
                  </span>
                  <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-violet-700">
                    <FontAwesome icon="user" class="mr-1" /><code>Nome</code> = Cliente
                  </span>
                </div>
              </div>
            </div>

            <!-- Filtros secundários e stats -->
            <div class="flex flex-col lg:flex-row gap-3 items-stretch lg:items-center">
              <!-- Status filter -->
              <div class="relative flex-shrink-0">
                <select
                  v-model="filtroStatus"
                  class="appearance-none bg-gradient-to-r from-white to-violet-50 border border-violet-200 text-gray-700 py-3 px-4 pr-10 rounded-xl focus:ring-2 focus:ring-violet-500 focus:border-violet-500 transition-all duration-300 shadow-sm min-w-[180px]"
                >
                  <option value="">Todos os status</option>
                  <option value="em_aberto">Em Aberto</option>
                  <option value="parcial">Parcial</option>
                  <option value="pago">Pago</option>
                  <option value="cancelado">Cancelado</option>
                </select>
                <FontAwesome icon="chevron-down" class="absolute right-3 top-1/2 transform -translate-y-1/2 text-violet-400 pointer-events-none" />
              </div>

              <!-- Tipo de venda filter -->
              <div class="relative flex-shrink-0">
                <select
                  v-model="filtroTipoVenda"
                  class="appearance-none bg-gradient-to-r from-white to-purple-50 border border-purple-200 text-gray-700 py-3 px-4 pr-10 rounded-xl focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all duration-300 shadow-sm min-w-[180px]"
                >
                  <option value="">Todos os tipos</option>
                  <option value="presencial">Presencial</option>
                  <option value="agendamento">Agendamento</option>
                  <option value="busca_entrega">Busca e Entrega</option>
                </select>
                <FontAwesome icon="chevron-down" class="absolute right-3 top-1/2 transform -translate-y-1/2 text-purple-400 pointer-events-none" />
              </div>

              <!-- Stats rápidas -->
              <div class="flex items-center gap-3 flex-wrap flex-1">
                <!-- Filtro Ativo (aparece quando há busca específica) -->
                <div v-if="infoFiltroAtivo" class="flex items-center gap-2 px-4 py-2 rounded-full shadow-sm border-2 animate-pulse bg-violet-50 border-violet-200 text-violet-700">
                  <FontAwesome :icon="infoFiltroAtivo.icone" class="text-sm" />
                  <span class="text-sm font-bold">{{ infoFiltroAtivo.tipo }}</span>
                  <code class="text-xs bg-white bg-opacity-60 px-1.5 py-0.5 rounded">{{ infoFiltroAtivo.valor }}</code>
                </div>

                <!-- Stats normais -->
                <div class="flex items-center gap-2 px-3 py-1.5 bg-gradient-to-r from-violet-100 to-purple-100 rounded-full">
                  <FontAwesome icon="shopping-cart" class="text-violet-600 text-sm" />
                  <span class="text-sm font-medium text-violet-700">{{ vendasFiltradas.length }} encontradas</span>
                </div>

                <div v-if="totalVendas > 0" class="flex items-center gap-2 px-3 py-1.5 bg-gradient-to-r from-green-100 to-emerald-100 rounded-full">
                  <FontAwesome icon="dollar-sign" class="text-green-600 text-sm" />
                  <span class="text-sm font-medium text-green-700">{{ formatarMoeda(totalVendas) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </BaseCard>

      <!-- 🎯 Estados da aplicação -->

      <!-- ⏳ Estado de Loading -->
      <div v-if="loading && vendas.length === 0" class="text-center py-12">
        <div class="relative">
          <div class="w-16 h-16 bg-gradient-to-br from-violet-400 to-purple-600 rounded-2xl flex items-center justify-center mx-auto mb-4 animate-bounce-gentle">
            <FontAwesome icon="shopping-cart" class="text-2xl text-white" />
          </div>
          <div class="absolute top-0 left-1/2 transform -translate-x-1/2 w-16 h-16 bg-violet-200 rounded-2xl animate-ping opacity-20"></div>
        </div>
        <h3 class="text-lg font-medium text-gray-700 mb-2">Carregando vendas...</h3>
        <p class="text-gray-500">Buscando dados na API ✨</p>
      </div>

      <!-- ❌ Estado de Erro -->
      <BaseCard v-else-if="error" class="p-8 text-center bg-gradient-to-br from-red-50 to-pink-50 border-0 shadow-lg">
        <div class="relative">
          <div class="w-16 h-16 bg-gradient-to-br from-red-400 to-pink-600 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <FontAwesome icon="exclamation-triangle" class="text-2xl text-white" />
          </div>
        </div>
        <h3 class="text-lg font-semibold text-red-700 mb-2">Erro ao carregar vendas</h3>
        <p class="text-red-600 mb-4">{{ error }}</p>
        <button
          @click="carregarVendas"
          class="flex items-center gap-2 px-6 py-2 bg-gradient-to-r from-red-500 to-pink-600 text-white rounded-xl hover:from-red-600 hover:to-pink-700 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg mx-auto"
        >
          <FontAwesome icon="refresh" />
          <span>Tentar novamente</span>
        </button>
      </BaseCard>

      <!-- 📭 Estado Vazio -->
      <BaseCard v-else-if="!loading && vendas.length === 0" class="p-12 text-center bg-gradient-to-br from-violet-50 to-purple-50 border-0 shadow-lg">
        <div class="relative">
          <div class="w-20 h-20 bg-gradient-to-br from-violet-400 to-purple-600 rounded-2xl flex items-center justify-center mx-auto mb-6 animate-bounce-gentle">
            <FontAwesome icon="shopping-cart" class="text-3xl text-white" />
          </div>
          <div class="absolute top-0 left-1/2 transform -translate-x-1/2 w-20 h-20 bg-violet-200 rounded-2xl animate-ping opacity-20"></div>
        </div>
        <h3 class="text-xl font-semibold text-gray-700 mb-2">Nenhuma venda encontrada</h3>
        <p class="text-gray-600 mb-6">Comece criando sua primeira venda!</p>
        <button
          @click="$router.push('/vendas/novo')"
          class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-violet-500 to-purple-600 text-white rounded-xl hover:from-violet-600 hover:to-purple-700 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg mx-auto font-medium"
        >
          <FontAwesome icon="plus" />
          <span>Criar Primeira Venda</span>
        </button>
      </BaseCard>

      <!-- 🔍 Estado de busca sem resultados -->
      <BaseCard v-else-if="!loading && vendas.length > 0 && vendasFiltradas.length === 0" class="p-12 text-center bg-gradient-to-br from-amber-50 to-orange-50 border-0 shadow-lg">
        <div class="relative">
          <div class="w-20 h-20 bg-gradient-to-br from-amber-400 to-orange-500 rounded-2xl flex items-center justify-center mx-auto mb-6 animate-bounce-gentle">
            <FontAwesome icon="search" class="text-3xl text-white" />
          </div>
          <div class="absolute top-0 left-1/2 transform -translate-x-1/2 w-20 h-20 bg-amber-200 rounded-2xl animate-ping opacity-20"></div>
        </div>
        <h3 class="text-xl font-semibold text-gray-700 mb-2">Nenhum resultado encontrado</h3>
        <p class="text-gray-600 mb-4">Não encontramos vendas com esses filtros</p>
        <div v-if="filtroBusca" class="flex items-center justify-center gap-2 px-4 py-2 bg-amber-100 rounded-lg mb-6 max-w-md mx-auto">
          <FontAwesome icon="search" class="text-amber-600" />
          <code class="text-amber-800 font-medium">{{ filtroBusca }}</code>
        </div>
        <button
          @click="limparFiltros"
          class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-amber-500 to-orange-500 text-white rounded-xl hover:from-amber-600 hover:to-orange-600 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg mx-auto font-medium"
        >
          <FontAwesome icon="times" />
          <span>Limpar filtros</span>
        </button>
      </BaseCard>

      <!-- ✅ Lista de vendas -->
      <div v-else class="grid grid-cols-1 gap-4 animate-fade-in">
        <div
          v-for="venda in vendasFiltradas"
          :key="venda.id"
          @click="$router.push(`/vendas/${venda.id}`)"
          class="bg-white rounded-xl shadow-sm hover:shadow-xl transition-all duration-300 p-6 cursor-pointer hover:scale-[1.01] border-2 border-transparent hover:border-violet-200"
        >
          <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
            <!-- Info principal -->
            <div class="flex-1">
              <div class="flex items-center gap-3 mb-2 flex-wrap">
                <span class="text-2xl font-bold text-violet-600">#{{ venda.codigoVenda }}</span>
                <span
                  :class="[
                    'px-3 py-1 rounded-full text-xs font-semibold',
                    getStatusBadgeClass(venda.statusVenda)
                  ]"
                >
                  {{ getStatusLabel(venda.statusVenda) }}
                </span>
                <span class="px-3 py-1 rounded-full text-xs font-semibold bg-blue-100 text-blue-700">
                  {{ getTipoVendaLabel(venda.tipoVenda) }}
                </span>
              </div>

              <div class="space-y-1 text-gray-600">
                <p class="flex items-center gap-2">
                  <FontAwesome icon="user" class="text-violet-500" />
                  <span class="font-medium">{{ venda.clienteNome }}</span>
                </p>
                <p class="flex items-center gap-2 text-sm">
                  <FontAwesome icon="calendar" class="text-gray-400" />
                  {{ formatarData(venda.dataVenda) }}
                </p>
                <p class="flex items-center gap-2 text-sm">
                  <FontAwesome icon="box" class="text-gray-400" />
                  {{ venda.quantidadeItens }} {{ venda.quantidadeItens === 1 ? 'item' : 'itens' }}
                  <span class="mx-1">•</span>
                  <FontAwesome icon="credit-card" class="text-gray-400" />
                  {{ venda.quantidadeBaixas }} {{ venda.quantidadeBaixas === 1 ? 'pagamento' : 'pagamentos' }}
                </p>
              </div>
            </div>

            <!-- Valores -->
            <div class="flex flex-col items-end gap-2">
              <div class="text-right">
                <p class="text-sm text-gray-500">Valor Total</p>
                <p class="text-2xl font-bold text-gray-800">{{ formatarMoeda(venda.valorTotal) }}</p>
              </div>

              <div v-if="venda.statusVenda !== 'pago'" class="text-right">
                <p class="text-xs text-gray-500">Pendente</p>
                <p class="text-lg font-semibold text-red-600">{{ formatarMoeda(venda.valorPendente) }}</p>
              </div>

              <!-- Barra de progresso do pagamento -->
              <div v-if="venda.statusVenda === 'parcial'" class="w-40">
                <div class="flex justify-between text-xs text-gray-600 mb-1">
                  <span>Pago</span>
                  <span>{{ venda.percentualPago.toFixed(0) }}%</span>
                </div>
                <div class="h-2 bg-gray-200 rounded-full overflow-hidden">
                  <div
                    class="h-full bg-gradient-to-r from-green-400 to-emerald-500 transition-all duration-500"
                    :style="{ width: `${venda.percentualPago}%` }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { vendasService } from '@/services/api'
import { FontAwesomeIcon as FontAwesome } from '@fortawesome/vue-fontawesome'
import { formatarValor } from '@/utils/formatters'
import BaseCard from '@/components/UI/BaseCard.vue'

const vendas = ref<any[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const filtroStatus = ref<string>('')
const filtroTipoVenda = ref<string>('')
const filtroBusca = ref('')

const normalizarTexto = (texto: string | null | undefined): string => {
  if (!texto) return ''
  return texto
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '') // Remove acentos
    .replace(/[^\w\s]/g, '') // Remove caracteres especiais
    .trim()
}

// Info do filtro ativo (para busca especial)
const infoFiltroAtivo = computed(() => {
  if (!filtroBusca.value.trim()) return null

  const busca = filtroBusca.value.trim()

  // Código da venda (#123)
  if (busca.startsWith('#')) {
    return {
      tipo: 'Código',
      valor: busca.substring(1),
      icone: 'hashtag',
      cor: 'violet'
    }
  }

  // CPF do cliente (@12345678900)
  if (busca.startsWith('@')) {
    return {
      tipo: 'CPF',
      valor: busca.substring(1),
      icone: 'at',
      cor: 'purple'
    }
  }

  return null
})

const vendasFiltradas = computed(() => {
  let resultado = vendas.value

  // Filtro por status
  if (filtroStatus.value) {
    resultado = resultado.filter(v => v.statusVenda === filtroStatus.value)
  }

  // Filtro por tipo de venda
  if (filtroTipoVenda.value) {
    resultado = resultado.filter(v => v.tipoVenda === filtroTipoVenda.value)
  }

  // Filtro por busca
  if (filtroBusca.value.trim()) {
    const busca = filtroBusca.value.trim()

    // Busca por código (#100037)
    if (busca.startsWith('#')) {
      const codigo = busca.substring(1)
      resultado = resultado.filter(v =>
        v.codigoVenda?.toString().includes(codigo)
      )
    }
    // Busca por CPF (@12345678900)
    else if (busca.startsWith('@')) {
      const cpf = busca.substring(1).replace(/\D/g, '')
      resultado = resultado.filter(v => {
        const cpfCliente = v.clienteCpf ? v.clienteCpf.replace(/\D/g, '') : ''
        return cpfCliente.includes(cpf)
      })
    }
    // Busca por nome do cliente
    else {
      const termoBusca = normalizarTexto(busca)
      resultado = resultado.filter(v => {
        const nomeCliente = normalizarTexto(v.clienteNome)
        return nomeCliente.includes(termoBusca)
      })
    }
  }

  return resultado
})

const totalVendas = computed(() => {
  return vendasFiltradas.value
    .filter(v => v.statusVenda !== 'cancelado')
    .reduce((acc, v) => acc + (v.valorTotal || 0), 0)
})

const limparFiltros = () => {
  filtroBusca.value = ''
  filtroStatus.value = ''
  filtroTipoVenda.value = ''
}

const carregarVendas = async () => {
  try {
    loading.value = true
    error.value = null
    vendas.value = await vendasService.buscarTodas()
  } catch (err: any) {
    error.value = err.message || 'Erro ao carregar vendas'
    console.error('Erro ao carregar vendas:', err)
  } finally {
    loading.value = false
  }
}

const formatarData = (data: string) => {
  const d = new Date(data)
  return d.toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatarMoeda = (valor: number) => {
  return formatarValor(valor)
}

const getStatusBadgeClass = (status: string) => {
  const classes: Record<string, string> = {
    em_aberto: 'bg-orange-100 text-orange-700',
    parcial: 'bg-yellow-100 text-yellow-700',
    pago: 'bg-green-100 text-green-700',
    cancelado: 'bg-red-100 text-red-700'
  }
  return classes[status] || 'bg-gray-100 text-gray-700'
}

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    em_aberto: 'Em Aberto',
    parcial: 'Parcial',
    pago: 'Pago',
    cancelado: 'Cancelado'
  }
  return labels[status] || status
}

const getTipoVendaLabel = (tipo: string) => {
  const labels: Record<string, string> = {
    presencial: 'Presencial',
    agendamento: 'Agendamento',
    busca_entrega: 'Busca e Entrega'
  }
  return labels[tipo] || tipo
}

onMounted(() => {
  carregarVendas()
})
</script>

<style scoped>
.bg-pattern {
  background-image: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 20px 20px;
}

.animate-fade-in-up {
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.animate-slide-up {
  animation: slideUp 0.4s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-bounce-gentle {
  animation: bounceGentle 2s ease-in-out infinite;
}

@keyframes bounceGentle {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.animate-twinkle {
  animation: twinkle 1.5s ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(0.9);
  }
}
</style>
