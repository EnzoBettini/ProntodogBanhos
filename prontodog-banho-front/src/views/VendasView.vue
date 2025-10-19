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
                Gerencie todas as vendas e pagamentos
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

      <!-- 🎯 Estados da aplicação -->

      <!-- ⏳ Estado de Loading -->
      <div v-if="loading && vendas.length === 0" class="text-center py-12">
        <FontAwesome icon="spinner" class="text-4xl text-violet-500 animate-spin mb-4" />
        <h3 class="text-lg font-medium text-gray-700 mb-2">Carregando vendas...</h3>
        <p class="text-gray-500">Buscando dados na API</p>
      </div>

      <!-- ❌ Estado de Erro -->
      <div v-else-if="error" class="text-center py-12">
        <FontAwesome icon="exclamation-triangle" class="text-4xl text-red-500 mb-4" />
        <h3 class="text-lg font-medium text-red-700 mb-2">Erro ao carregar vendas</h3>
        <p class="text-red-600 mb-4">{{ error }}</p>
        <button
          @click="carregarVendas"
          class="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition"
        >
          <FontAwesome icon="refresh" class="mr-2" />
          Tentar Novamente
        </button>
      </div>

      <!-- 📭 Estado Vazio -->
      <div v-else-if="!loading && vendas.length === 0" class="text-center py-12">
        <FontAwesome icon="shopping-cart" class="text-4xl text-gray-400 mb-4" />
        <h3 class="text-lg font-medium text-gray-700 mb-2">Nenhuma venda encontrada</h3>
        <p class="text-gray-500 mb-4">Comece criando sua primeira venda!</p>
        <button
          @click="$router.push('/vendas/novo')"
          class="px-6 py-3 bg-gradient-to-r from-violet-500 to-purple-600 text-white rounded-lg hover:from-violet-600 hover:to-purple-700 transition shadow-lg"
        >
          <FontAwesome icon="plus" class="mr-2" />
          Criar Primeira Venda
        </button>
      </div>

      <!-- ✅ Lista de vendas -->
      <div v-else class="space-y-4">
        <!-- 🔍 Busca avançada por cliente -->
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center gap-3 mb-4">
            <FontAwesome icon="search" class="text-violet-500 text-xl" />
            <h3 class="text-lg font-bold text-gray-800">Buscar por Cliente</h3>
            <button
              v-if="filtroBusca"
              @click="limparBusca"
              class="ml-auto text-sm px-3 py-1 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition"
            >
              <FontAwesome icon="times" class="mr-1" />
              Limpar
            </button>
          </div>

          <div class="relative">
            <input
              v-model="filtroBusca"
              type="text"
              placeholder="Digite o nome, CPF ou código do sistema do cliente..."
              class="w-full px-4 py-3 pl-12 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500 focus:border-transparent transition"
              @input="buscarCliente"
            />
            <FontAwesome
              icon="search"
              class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400"
            />
            <div v-if="filtroBusca" class="absolute right-4 top-1/2 -translate-y-1/2">
              <FontAwesome
                icon="spinner"
                v-if="buscandoCliente"
                class="text-violet-500 animate-spin"
              />
              <span v-else class="text-xs text-gray-500">
                {{ vendasFiltradas.length }} resultado(s)
              </span>
            </div>
          </div>

          <div v-if="filtroBusca" class="mt-3 text-sm text-gray-600">
            <FontAwesome icon="info-circle" class="mr-1 text-blue-500" />
            Buscando por: <strong>{{ filtroBusca }}</strong>
          </div>
        </div>

        <!-- Filtros rápidos de status -->
        <div class="bg-white rounded-xl shadow-sm p-4 flex gap-2 overflow-x-auto">
          <button
            v-for="status in statusOptions"
            :key="status.value"
            @click="filtroStatus = filtroStatus === status.value ? null : status.value"
            :class="[
              'px-4 py-2 rounded-lg font-medium transition whitespace-nowrap',
              filtroStatus === status.value
                ? `bg-${status.color}-500 text-white`
                : `bg-${status.color}-100 text-${status.color}-700 hover:bg-${status.color}-200`
            ]"
          >
            <FontAwesome :icon="status.icon" class="mr-2" />
            {{ status.label }}
          </button>
          <button
            v-if="filtroStatus"
            @click="filtroStatus = null"
            class="px-4 py-2 rounded-lg font-medium bg-gray-200 text-gray-700 hover:bg-gray-300 transition whitespace-nowrap"
          >
            <FontAwesome icon="times" class="mr-2" />
            Limpar filtro
          </button>
        </div>

        <!-- Cards de vendas -->
        <div class="grid grid-cols-1 gap-4">
          <div
            v-for="venda in vendasFiltradas"
            :key="venda.id"
            @click="$router.push(`/vendas/${venda.id}`)"
            class="bg-white rounded-xl shadow-sm hover:shadow-md transition-all duration-300 p-6 cursor-pointer hover:scale-[1.01]"
          >
            <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
              <!-- Info principal -->
              <div class="flex-1">
                <div class="flex items-center gap-3 mb-2">
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { vendasService } from '@/services/api'
import { FontAwesomeIcon as FontAwesome } from '@fortawesome/vue-fontawesome'
import { formatarValor } from '@/utils/formatters'

const vendas = ref<any[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const filtroStatus = ref<string | null>(null)
const filtroBusca = ref('')
const buscandoCliente = ref(false)

const statusOptions = [
  { value: 'em_aberto', label: 'Em Aberto', color: 'orange', icon: 'clock' },
  { value: 'parcial', label: 'Parcial', color: 'yellow', icon: 'hourglass-half' },
  { value: 'pago', label: 'Pago', color: 'green', icon: 'check-circle' },
  { value: 'cancelado', label: 'Cancelado', color: 'red', icon: 'times-circle' }
]

const normalizarTexto = (texto: string | null | undefined): string => {
  if (!texto) return ''
  return texto
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '') // Remove acentos
    .replace(/[^\w\s]/g, '') // Remove caracteres especiais
    .trim()
}

const vendasFiltradas = computed(() => {
  let resultado = vendas.value

  // Filtro por status
  if (filtroStatus.value) {
    resultado = resultado.filter(v => v.statusVenda === filtroStatus.value)
  }

  // Filtro por busca de cliente (nome, CPF ou código)
  if (filtroBusca.value.trim()) {
    const termoBusca = normalizarTexto(filtroBusca.value)

    resultado = resultado.filter(venda => {
      // Buscar por nome do cliente
      const nomeCliente = normalizarTexto(venda.clienteNome)
      if (nomeCliente.includes(termoBusca)) return true

      // Buscar por CPF (remove formatação)
      const cpfCliente = venda.clienteCpf ?
        venda.clienteCpf.replace(/\D/g, '') : ''
      const cpfBusca = filtroBusca.value.replace(/\D/g, '')
      if (cpfCliente.includes(cpfBusca)) return true

      // Buscar por código da venda
      const codigoVenda = venda.codigoVenda?.toString() || ''
      if (codigoVenda.includes(filtroBusca.value.trim())) return true

      return false
    })
  }

  return resultado
})

const limparBusca = () => {
  filtroBusca.value = ''
  buscandoCliente.value = false
}

const buscarCliente = () => {
  // A busca é reativa via computed, apenas atualizar o estado de loading
  buscandoCliente.value = true
  setTimeout(() => {
    buscandoCliente.value = false
  }, 300)
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

