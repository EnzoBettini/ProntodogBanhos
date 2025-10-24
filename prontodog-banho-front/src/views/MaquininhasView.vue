<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 🏢 Header Profissional -->
    <div class="relative overflow-hidden bg-gradient-to-r from-blue-700 via-indigo-600 to-blue-800 text-white pt-16">
      <div class="absolute inset-0 opacity-20">
        <div class="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent transform -skew-x-12 animate-pulse"></div>
      </div>

      <div class="relative z-20 max-w-7xl mx-auto px-4 py-12">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="relative">
              <div class="w-16 h-16 bg-gradient-to-br from-blue-400 to-indigo-500 rounded-2xl flex items-center justify-center shadow-2xl">
                <FontAwesomeIcon icon="credit-card" class="text-2xl text-white drop-shadow-lg animate-bounce-gentle" />
              </div>
              <div class="absolute -top-2 -right-2 bg-gradient-to-r from-green-500 to-emerald-600 text-white text-xs font-bold px-2 py-1 rounded-full shadow-lg animate-pulse">
                {{ maquininhasCount }}
              </div>
            </div>

            <div>
              <h1 class="text-4xl font-bold flex items-center gap-3">
                <FontAwesomeIcon icon="credit-card" class="text-blue-300 animate-twinkle mr-1" />
                Maquininhas de Cartão
              </h1>
              <p class="text-xl opacity-90 font-medium">Gestão de maquininhas e taxas</p>
            </div>
          </div>

          <div class="flex gap-3">
            <RouterLink
              to="/maquininhas/nova"
              class="group bg-gradient-to-r from-green-600 to-emerald-700 hover:from-green-700 hover:to-emerald-800 px-6 py-3 rounded-xl font-medium shadow-lg hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-1 flex items-center gap-2"
            >
              <FontAwesomeIcon icon="plus" class="group-hover:rotate-90 transition-transform duration-300" />
              Nova Maquininha
            </RouterLink>
          </div>
        </div>
      </div>

      <!-- Wave decoration -->
      <div class="absolute bottom-0 left-0 right-0 z-10 overflow-hidden">
        <svg viewBox="0 0 1440 100" class="w-full h-20" preserveAspectRatio="none">
          <path
            d="M0,50 C240,30 480,70 720,50 C960,30 1200,70 1440,50 L1440,100 L0,100 Z"
            fill="#3b82f6"
          />
        </svg>
      </div>
    </div>

    <div class="max-w-7xl mx-auto px-4 py-8">
      <!-- Filtros e Estatísticas -->
      <div class="mb-8 bg-white rounded-2xl shadow-lg p-6">
        <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
          <!-- Campo de busca -->
          <div class="flex-1 max-w-md relative">
            <div class="relative group">
              <input
                type="text"
                v-model="filtroBusca"
                placeholder="Buscar por nome, adquirente ou conta..."
                class="w-full pl-12 pr-4 py-3 border-2 border-blue-200 rounded-xl focus:border-blue-500 focus:ring-4 focus:ring-blue-500/20 transition-all duration-300 bg-white/80 backdrop-blur-sm"
              >
              <FontAwesomeIcon
                icon="search"
                class="absolute left-4 top-1/2 transform -translate-y-1/2 text-blue-500 group-focus-within:text-blue-600 transition-colors"
              />
            </div>

            <div v-if="filtroBusca" class="mt-2">
              <span class="inline-flex items-center gap-1 px-3 py-1 bg-gradient-to-r from-blue-100 to-indigo-100 text-blue-700 rounded-full text-sm font-medium">
                <FontAwesomeIcon icon="filter" class="text-xs" />
                Buscando: "{{ filtroBusca }}"
              </span>
            </div>
          </div>

          <!-- Estatísticas -->
          <div class="flex flex-wrap gap-4">
            <div class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-blue-100 to-indigo-100 rounded-xl">
              <FontAwesomeIcon icon="credit-card" class="text-blue-600" />
              <span class="text-sm font-medium text-blue-800">
                {{ maquininhasFiltradas.length }} de {{ maquininhasCount }} encontradas
              </span>
            </div>

            <div class="flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-green-100 to-emerald-100 rounded-xl">
              <FontAwesomeIcon icon="check-circle" class="text-green-600" />
              <span class="text-sm font-medium text-green-800">
                {{ maquininhasAtivas }} ativas
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="flex items-center justify-center py-20">
        <div class="text-center space-y-4">
          <div class="relative">
            <div class="w-16 h-16 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl flex items-center justify-center shadow-xl animate-bounce">
              <FontAwesomeIcon icon="spinner" class="text-2xl text-white animate-spin" />
            </div>
            <div class="absolute inset-0 bg-gradient-to-r from-blue-400/30 to-indigo-500/30 rounded-2xl blur-xl animate-pulse"></div>
          </div>
          <p class="text-gray-600 font-medium">Carregando maquininhas...</p>
        </div>
      </div>

      <!-- Lista vazia -->
      <div v-else-if="maquininhasFiltradas.length === 0" class="py-20">
        <div class="max-w-md mx-auto text-center space-y-6">
          <div class="relative inline-block">
            <div class="w-24 h-24 bg-gradient-to-br from-blue-100 to-indigo-100 rounded-3xl flex items-center justify-center">
              <FontAwesomeIcon icon="credit-card" class="text-4xl text-blue-400" />
            </div>
          </div>
          <div>
            <h3 class="text-2xl font-bold text-gray-800 mb-2">
              {{ filtroBusca ? 'Nenhuma maquininha encontrada' : 'Nenhuma maquininha cadastrada' }}
            </h3>
            <p class="text-gray-600">
              {{ filtroBusca ? 'Tente ajustar os filtros de busca.' : 'Cadastre sua primeira maquininha para começar.' }}
            </p>
          </div>
          <RouterLink
            to="/maquininhas/nova"
            class="inline-flex items-center gap-2 bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-6 py-3 rounded-xl font-medium hover:from-blue-700 hover:to-indigo-700 transition-all duration-300 transform hover:scale-105 shadow-lg"
          >
            <FontAwesomeIcon icon="plus" />
            Cadastrar Primeira Maquininha
          </RouterLink>
        </div>
      </div>

      <!-- Lista de Maquininhas -->
      <div v-else class="grid gap-6">
        <div
          v-for="maquininha in maquininhasFiltradas"
          :key="maquininha.id"
          class="bg-white rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 overflow-hidden border border-gray-100"
        >
          <div class="p-6">
            <div class="flex items-start justify-between mb-4">
              <div class="flex items-center gap-4">
                <div class="w-14 h-14 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg">
                  <FontAwesomeIcon icon="credit-card" class="text-2xl text-white" />
                </div>
                <div>
                  <h3 class="text-xl font-bold text-gray-800 flex items-center gap-2">
                    {{ maquininha.nome }}
                    <span
                      v-if="maquininha.ativo"
                      class="px-2 py-1 bg-green-100 text-green-700 text-xs font-semibold rounded-lg"
                    >
                      Ativa
                    </span>
                    <span
                      v-else
                      class="px-2 py-1 bg-gray-100 text-gray-700 text-xs font-semibold rounded-lg"
                    >
                      Inativa
                    </span>
                  </h3>
                  <p class="text-gray-600 text-sm">{{ maquininha.adquirenteNome }}</p>
                </div>
              </div>

              <div class="flex gap-2">
                <button
                  @click="verDetalhes(maquininha.id)"
                  class="p-2 bg-blue-100 text-blue-600 rounded-lg hover:bg-blue-200 transition-colors"
                  title="Ver detalhes"
                >
                  <FontAwesomeIcon icon="eye" />
                </button>
                <button
                  @click="editar(maquininha.id)"
                  class="p-2 bg-yellow-100 text-yellow-600 rounded-lg hover:bg-yellow-200 transition-colors"
                  title="Editar"
                >
                  <FontAwesomeIcon icon="edit" />
                </button>
                <button
                  @click="confirmarExclusao(maquininha)"
                  class="p-2 bg-red-100 text-red-600 rounded-lg hover:bg-red-200 transition-colors"
                  title="Excluir"
                >
                  <FontAwesomeIcon icon="trash" />
                </button>
              </div>
            </div>

            <!-- Informações -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
              <div class="flex items-center gap-2 text-sm">
                <FontAwesomeIcon icon="building" class="text-indigo-500" />
                <span class="text-gray-700">{{ maquininha.contaBancariaNome }}</span>
              </div>
              <div class="flex items-center gap-2 text-sm">
                <FontAwesomeIcon icon="calendar" class="text-green-500" />
                <span class="text-gray-700">Débito: {{ maquininha.prazoRecebimentoDebito }}d | Crédito: {{ maquininha.prazoRecebimentoCredito }}d</span>
              </div>
              <div class="flex items-center gap-2 text-sm">
                <FontAwesomeIcon icon="bolt" class="text-cyan-500" />
                <span class="text-gray-700">PIX: {{ maquininha.aceitaPix ? 'Sim' : 'Não' }}</span>
              </div>
            </div>

            <!-- Taxas configuradas -->
            <div v-if="maquininha.totalTaxasConfiguradas > 0" class="flex items-center gap-2 p-3 bg-gradient-to-r from-purple-50 to-pink-50 rounded-xl border border-purple-100">
              <FontAwesomeIcon icon="percent" class="text-purple-600" />
              <span class="text-sm text-purple-800 font-medium">
                {{ maquininha.totalTaxasConfiguradas }} taxa(s) configurada(s) para {{ maquininha.totalBandeirasConfiguradas }} bandeira(s)
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { maquininhasService } from '@/services/api'

const router = useRouter()
const loading = ref(false)
const maquininhas = ref<any[]>([])
const filtroBusca = ref('')

onMounted(async () => {
  await carregarMaquininhas()
})

const carregarMaquininhas = async () => {
  loading.value = true
  try {
    maquininhas.value = await maquininhasService.listarTodas()
  } catch (error) {
    console.error('Erro ao carregar maquininhas:', error)
    alert('Erro ao carregar a lista de maquininhas.')
  } finally {
    loading.value = false
  }
}

const maquininhasFiltradas = computed(() => {
  if (!filtroBusca.value) return maquininhas.value

  const busca = filtroBusca.value.toLowerCase()
  return maquininhas.value.filter(m =>
    m.nome?.toLowerCase().includes(busca) ||
    m.adquirenteNome?.toLowerCase().includes(busca) ||
    m.contaBancariaNome?.toLowerCase().includes(busca)
  )
})

const maquininhasCount = computed(() => maquininhas.value.length)
const maquininhasAtivas = computed(() => maquininhas.value.filter(m => m.ativo).length)

const verDetalhes = (id: number) => {
  // Futuramente: abrir modal com detalhes
  alert(`Ver detalhes da maquininha ${id}`)
}

const editar = (id: number) => {
  router.push(`/maquininhas/${id}/editar`)
}

const confirmarExclusao = async (maquininha: any) => {
  if (confirm(`Deseja realmente excluir a maquininha "${maquininha.nome}"? Esta ação não pode ser desfeita.`)) {
    try {
      await maquininhasService.excluir(maquininha.id)
      alert('Maquininha excluída com sucesso!')
      await carregarMaquininhas()
    } catch (error) {
      console.error('Erro ao excluir:', error)
      alert('Erro ao excluir a maquininha. Tente novamente.')
    }
  }
}
</script>

<style scoped>
@keyframes bounce-gentle {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

@keyframes twinkle {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.animate-bounce-gentle {
  animation: bounce-gentle 2s ease-in-out infinite;
}

.animate-twinkle {
  animation: twinkle 2s ease-in-out infinite;
}
</style>

