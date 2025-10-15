<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-indigo-100">
    <!-- 🌟 Header com gradiente elegante -->
    <div class="relative overflow-hidden bg-gradient-to-r from-blue-600 via-indigo-600 to-blue-700 text-white pt-16">
      <!-- Background pattern -->
      <div class="absolute inset-0 opacity-10 bg-pattern"></div>

      <div class="relative px-6 py-8 z-20">
        <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
          <div class="flex items-center gap-4 animate-fade-in-up">
            <!-- Ícone animado -->
            <div class="relative">
              <div class="w-16 h-16 bg-white bg-opacity-20 backdrop-blur-sm rounded-2xl flex items-center justify-center transform hover:scale-110 transition-all duration-300 shadow-lg">
                <FontAwesomeIcon icon="dog" class="text-2xl text-white animate-bounce-gentle" />
              </div>
              <div v-if="!loading && animais.length > 0" class="absolute -top-1 -right-1 w-8 h-8 bg-yellow-400 rounded-full flex items-center justify-center animate-pulse">
                <span class="text-xs font-bold text-yellow-800">{{ animais.length }}</span>
              </div>
            </div>

            <div class="space-y-1">
              <h1 class="text-4xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-blue-100">
                Lista de Animais
              </h1>
              <p class="text-white text-lg flex items-center gap-2 font-medium opacity-90">
                <FontAwesomeIcon icon="star" class="text-yellow-400 animate-twinkle mr-1" />
                {{ loading ? 'Carregando...' : `${animaisFiltrados.length} ${animaisFiltrados.length === 1 ? 'amiguinho encontrado' : 'amiguinhos encontrados'}` }}
              </p>
            </div>
          </div>

          <!-- Botões de ação elegantes -->
          <div class="flex items-center gap-3">
            <button
              @click="carregarAnimais"
              :disabled="loading"
              class="group flex items-center gap-2 px-4 py-2 bg-white bg-opacity-20 backdrop-blur-sm text-white rounded-xl hover:bg-opacity-30 transition-all duration-300 border border-white border-opacity-20 hover:border-opacity-40 transform hover:-translate-y-1 hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <FontAwesomeIcon
                :icon="loading ? 'spinner' : 'refresh'"
                :class="{ 'animate-spin': loading, 'group-hover:rotate-180': !loading }"
                class="transition-transform duration-300"
              />
              <span class="font-medium">{{ loading ? 'Carregando...' : 'Atualizar' }}</span>
            </button>

            <button
              @click="$router.push('/animais/novo')"
              class="group flex items-center gap-2 px-6 py-2 bg-gradient-to-r from-yellow-400 to-amber-500 text-white rounded-xl hover:from-yellow-500 hover:to-amber-600 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-xl font-medium shadow-lg"
            >
              <FontAwesomeIcon icon="plus" class="group-hover:rotate-90 transition-transform duration-300" />
              <span>Novo Animal</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Wave decoration -->
      <div class="absolute bottom-0 left-0 right-0 z-10">
        <svg viewBox="0 0 1440 60" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full h-15">
          <defs>
            <linearGradient id="animalWaveGradient" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" style="stop-color:#10b981;stop-opacity:1" />
              <stop offset="50%" style="stop-color:#059669;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#047857;stop-opacity:1" />
            </linearGradient>
          </defs>
          <path d="M0 60h1440V0c-120 40-240 60-360 60S840 40 720 60s-240 0-360-60S120 40 0 60z" fill="url(#animalWaveGradient)"/>
        </svg>
      </div>
    </div>

    <!-- Container principal -->
    <div class="relative -mt-8 px-6 pb-8 z-30">

      <!-- 🔍 Filtros elegantes -->
      <BaseCard class="mb-6 shadow-lg border-0 bg-white bg-opacity-90 backdrop-blur-sm animate-slide-up">
        <div class="p-4">
          <div class="flex flex-col lg:flex-row gap-4 items-center">
            <!-- Campo de busca principal -->
            <div class="flex-1 relative">
              <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <FontAwesomeIcon icon="search" class="h-5 w-5 text-blue-400" />
              </div>
              <input
                v-model="filtroNome"
                type="text"
                placeholder="Digite nome ou use: #123 (ID) ou $456 (SimplesVet)..."
                class="w-full pl-10 pr-4 py-3 bg-gradient-to-r from-white to-blue-50 border border-blue-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-300 placeholder-gray-400 text-gray-700 shadow-sm"
              />
            </div>

            <!-- 💡 Dicas dos filtros inteligentes -->
            <div v-if="filtroNome && !loading" class="w-full">
              <div class="flex flex-wrap items-center gap-2 px-3 py-2 bg-gradient-to-r from-blue-50 to-indigo-50 rounded-lg border border-blue-100">
                <FontAwesomeIcon icon="lightbulb" class="text-blue-500 text-sm" />
                <span class="text-xs text-blue-700 font-medium">Dicas de busca:</span>
                <div class="flex flex-wrap gap-2 text-xs">
                  <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-blue-700">
                    <FontAwesomeIcon icon="hashtag" class="mr-1" /><code>#123</code> = ID Sistema
                  </span>
                  <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-blue-700">
                    <FontAwesomeIcon icon="dollar-sign" class="mr-1" /><code>$456</code> = SimplesVet
                  </span>
                  <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-blue-700">
                    <FontAwesomeIcon icon="dog" class="mr-1" /><code>Rex</code> = Nome/Dono
                  </span>
                </div>
              </div>
            </div>

            <!-- Filtros secundários -->
            <div class="flex flex-col sm:flex-row gap-3">
              <div class="relative">
                <select
                  v-model="filtroCliente"
                  class="appearance-none bg-gradient-to-r from-white to-indigo-50 border border-indigo-200 text-gray-700 py-3 px-4 pr-8 rounded-xl focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-all duration-300 shadow-sm min-w-[180px]"
                >
                  <option value="">Todos os donos</option>
                  <option v-for="cliente in clientesUnicos" :key="cliente" :value="cliente">
                    {{ cliente }}
                  </option>
                </select>
                <FontAwesomeIcon icon="chevron-down" class="absolute right-3 top-1/2 transform -translate-y-1/2 text-indigo-400 pointer-events-none" />
              </div>

              <div class="relative">
                <select
                  v-model="filtroTipo"
                  class="appearance-none bg-gradient-to-r from-white to-purple-50 border border-purple-200 text-gray-700 py-3 px-4 pr-8 rounded-xl focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all duration-300 shadow-sm min-w-[150px]"
                >
                  <option value="">Todos os tipos</option>
                  <option v-for="tipo in tiposUnicos" :key="tipo" :value="tipo">
                    {{ tipo }}
                  </option>
                </select>
                <FontAwesomeIcon icon="chevron-down" class="absolute right-3 top-1/2 transform -translate-y-1/2 text-purple-400 pointer-events-none" />
              </div>
            </div>

            <!-- Stats rápidas -->
            <div class="flex items-center gap-3 flex-wrap">
              <!-- Filtro Ativo (aparece quando há busca) -->
              <div v-if="infoFiltroAtivo" class="flex items-center gap-2 px-4 py-2 rounded-full shadow-sm border-2 animate-pulse"
                :class="{
                  'bg-blue-50 border-blue-200 text-blue-700': infoFiltroAtivo.cor === 'blue',
                  'bg-green-50 border-green-200 text-green-700': infoFiltroAtivo.cor === 'green',
                  'bg-purple-50 border-purple-200 text-purple-700': infoFiltroAtivo.cor === 'purple'
                }"
              >
                <FontAwesomeIcon :icon="infoFiltroAtivo.icone" class="text-sm" />
                <span class="text-sm font-bold">{{ infoFiltroAtivo.tipo }}</span>
                <code class="text-xs bg-white bg-opacity-60 px-1.5 py-0.5 rounded">{{ infoFiltroAtivo.valor }}</code>
              </div>

              <!-- Stats normais -->
              <div class="flex items-center gap-2 px-3 py-1.5 bg-gradient-to-r from-blue-100 to-indigo-100 rounded-full">
                <FontAwesomeIcon icon="dog" class="text-blue-600 text-sm" />
                <span class="text-sm font-medium text-blue-700">{{ animaisFiltrados.length }} encontrados</span>
              </div>

              <div v-if="!infoFiltroAtivo" class="flex items-center gap-2 px-3 py-1.5 bg-gradient-to-r from-green-100 to-emerald-100 rounded-full">
                <FontAwesomeIcon icon="users" class="text-green-600 text-sm" />
                <span class="text-sm font-medium text-green-700">{{ clientesUnicos.length }} donos</span>
              </div>
            </div>
          </div>
        </div>
      </BaseCard>

      <!-- ⏳ Estado de Loading -->
      <div v-if="loading && animais.length === 0" class="text-center py-12">
        <div class="relative">
          <div class="w-16 h-16 bg-gradient-to-br from-blue-400 to-indigo-600 rounded-2xl flex items-center justify-center mx-auto mb-4 animate-bounce-gentle">
            <FontAwesomeIcon icon="dog" class="text-2xl text-white" />
          </div>
          <div class="absolute top-0 left-1/2 transform -translate-x-1/2 w-16 h-16 bg-blue-200 rounded-2xl animate-ping opacity-20"></div>
        </div>
        <h3 class="text-lg font-medium text-gray-700 mb-2">Carregando animais...</h3>
        <p class="text-gray-500">Buscando seus amiguinhos na API ✨</p>
      </div>

      <!-- ❌ Estado de Erro -->
      <BaseCard v-else-if="error" class="p-8 text-center bg-gradient-to-br from-red-50 to-pink-50 border-0 shadow-lg">
        <div class="relative">
          <div class="w-16 h-16 bg-gradient-to-br from-red-400 to-pink-600 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <FontAwesomeIcon icon="exclamation-triangle" class="text-2xl text-white" />
          </div>
        </div>
        <h3 class="text-lg font-semibold text-red-700 mb-2">Erro ao carregar animais</h3>
        <p class="text-red-600 mb-4">{{ error }}</p>
        <button
          @click="carregarAnimais"
          class="flex items-center gap-2 px-6 py-2 bg-gradient-to-r from-red-500 to-pink-600 text-white rounded-xl hover:from-red-600 hover:to-pink-700 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg mx-auto"
        >
          <FontAwesomeIcon icon="refresh" />
          <span>Tentar novamente</span>
        </button>
      </BaseCard>

      <!-- 🐕 Lista elegante de animais -->
      <div v-else-if="animais.length > 0" ref="listaAnimaisRef" class="space-y-4">
        <div
          v-for="(animal, index) in animaisExibidos"
          :key="animal.id"
          class="group relative bg-gradient-to-r from-white via-white to-blue-50 rounded-xl shadow-lg hover:shadow-2xl cursor-pointer transform transition-all duration-300 hover:-translate-y-1 animate-fade-in-up overflow-hidden"
          :style="{ animationDelay: `${index * 100}ms` }"
          @click="visualizarAnimal(animal)"
        >
          <div class="p-6">
            <div class="flex items-center justify-between">
              <!-- 🐕 Avatar e informações principais -->
              <div class="flex items-center gap-4 flex-1">
                <!-- Avatar circular com gradiente -->
                <div class="relative">
                  <div class="w-16 h-16 bg-gradient-to-br from-blue-400 to-indigo-600 rounded-2xl flex items-center justify-center shadow-lg transform group-hover:scale-110 transition-all duration-300">
                    <FontAwesomeIcon icon="dog" class="text-2xl text-white" />
                  </div>
                  <!-- Badge de tipo -->
                  <div class="absolute -top-1 -right-1 w-6 h-6 bg-gradient-to-r from-purple-500 to-pink-600 rounded-full flex items-center justify-center shadow-md">
                    <span class="text-xs font-bold text-white">{{ animal.tipo.charAt(0).toUpperCase() }}</span>
                  </div>
                </div>

                <!-- Informações do animal -->
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-3 mb-1">
                    <h3 class="text-xl font-bold text-gray-900 truncate group-hover:text-blue-700 transition-colors duration-300">
                      {{ animal.nome }}
                    </h3>
                    <span
                      class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium flex-shrink-0"
                      :class="getCorTipoAnimal(animal.tipo)"
                    >
                      {{ animal.tipo }}
                    </span>
                  </div>

                  <div class="flex items-center gap-4 text-sm text-gray-600 mb-2">
                    <span class="flex items-center gap-1">
                      <FontAwesomeIcon icon="hashtag" class="text-gray-400" />
                      ID: {{ animal.codigoAnimalSistema }}
                    </span>
                    <span class="flex items-center gap-1">
                      <FontAwesomeIcon icon="code" class="text-gray-400" />
                      SimplesVet: {{ animal.codigoSimplesVet || 'N/A' }}
                    </span>
                  </div>

                  <!-- Dono do animal -->
                  <div class="flex items-center gap-2 text-sm">
                    <div class="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
                      <FontAwesomeIcon icon="user" class="text-green-600 text-xs" />
                    </div>
                    <span class="text-gray-700 font-medium">
                      {{ animal.cliente?.nomeCompleto || 'Dono não informado' }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- 📅 Seção de serviços -->
              <div class="flex-shrink-0 ml-4">
                <div class="text-right">
                  <p class="text-xs text-gray-500 mb-2 font-medium">Serviços:</p>
                  <div class="flex items-center gap-2 justify-end">
                    <div class="w-8 h-8 bg-amber-100 rounded-full flex items-center justify-center">
                      <FontAwesomeIcon icon="calendar-days" class="text-amber-600 text-xs" />
                    </div>
                    <span class="text-lg font-bold text-gray-800">
                      {{ animal.servicos?.length || 0 }}
                    </span>
                    <span class="text-sm text-gray-600">
                      {{ (animal.servicos?.length || 0) === 1 ? 'serviço' : 'serviços' }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- 🔧 Ações -->
              <div class="flex-shrink-0 ml-4">
                <div class="flex items-center gap-2">
                  <button
                    @click.stop="visualizarAnimal(animal)"
                    class="w-10 h-10 bg-blue-100 hover:bg-blue-200 text-blue-600 rounded-full flex items-center justify-center transition-all duration-300 transform hover:scale-110"
                    title="Ver detalhes"
                  >
                    <FontAwesomeIcon icon="eye" class="text-sm" />
                  </button>
                  <button
                    @click.stop="editarAnimal(animal)"
                    class="w-10 h-10 bg-indigo-100 hover:bg-indigo-200 text-indigo-600 rounded-full flex items-center justify-center transition-all duration-300 transform hover:scale-110"
                    title="Editar animal"
                  >
                    <FontAwesomeIcon icon="edit" class="text-sm" />
                  </button>
                </div>
              </div>

              <!-- 🔗 Indicador de ação -->
              <div class="flex-shrink-0 ml-4">
                <div class="w-10 h-10 bg-gray-100 group-hover:bg-blue-100 rounded-full flex items-center justify-center transition-all duration-300">
                  <FontAwesomeIcon
                    icon="chevron-right"
                    class="text-gray-400 group-hover:text-blue-600 group-hover:translate-x-1 transition-all duration-300"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- Borda gradiente no hover -->
          <div class="absolute inset-0 border-2 border-transparent group-hover:border-blue-200 rounded-xl transition-all duration-300 pointer-events-none"></div>
        </div>

        <!-- 📄 Botão Carregar Mais -->
        <div v-if="temMaisItens" class="mt-8 flex justify-center">
          <BaseButton
            @click="carregarMais"
            class="group relative px-8 py-4 bg-gradient-to-r from-blue-500 to-indigo-600 hover:from-blue-600 hover:to-indigo-700 text-white font-semibold rounded-xl shadow-lg transform hover:-translate-y-1 hover:shadow-2xl transition-all duration-300 flex items-center gap-3"
          >
            <FontAwesomeIcon icon="chevron-down" class="group-hover:translate-y-1 transition-transform duration-300" />
            <span>Carregar mais {{ Math.min(itensPorPagina, totalItensDisponiveis - itensExibidos) }} animais</span>
            <div class="absolute inset-0 bg-white opacity-0 group-hover:opacity-20 rounded-xl transition-opacity duration-300"></div>
          </BaseButton>
        </div>

        <!-- 📊 Rodapé elegante com estatísticas -->
        <BaseCard class="mt-8 bg-gradient-to-r from-gray-50 to-blue-50 border-0 shadow-sm">
          <div class="p-4">
            <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 text-sm">
              <div class="flex flex-wrap items-center gap-4">
                <div class="flex items-center gap-2">
                  <div class="w-2 h-2 bg-blue-500 rounded-full animate-pulse"></div>
                  <span class="text-gray-600 font-medium">
                    <span class="text-blue-600 font-bold">{{ animaisExibidos.length }}</span> de <span class="text-blue-600 font-bold">{{ totalItensDisponiveis }}</span> encontrados
                  </span>
                </div>
                <div class="flex items-center gap-2">
                  <div class="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
                  <span class="text-gray-600 font-medium">
                    <span class="text-green-600 font-bold">{{ clientesUnicos.length }}</span> donos únicos
                  </span>
                </div>
              </div>

              <div v-if="ultimaAtualizacao" class="flex items-center gap-2 text-gray-500">
                <FontAwesomeIcon icon="clock" class="text-xs" />
                <span>Atualizado às {{ ultimaAtualizacao }}</span>
              </div>
            </div>
          </div>
        </BaseCard>
      </div>

      <!-- 📭 Estado vazio -->
      <BaseCard v-else class="p-12 text-center bg-gradient-to-br from-blue-50 to-indigo-50 border-0 shadow-lg">
        <div class="relative">
          <div class="w-20 h-20 rounded-2xl flex items-center justify-center mx-auto mb-6 animate-bounce-gentle"
            :class="infoFiltroAtivo ? 'bg-gradient-to-br from-amber-400 to-orange-500' : 'bg-gradient-to-br from-blue-400 to-indigo-600'"
          >
            <FontAwesomeIcon
              :icon="infoFiltroAtivo ? 'search' : 'dog'"
              class="text-3xl text-white"
            />
          </div>
          <div class="absolute top-0 left-1/2 transform -translate-x-1/2 w-20 h-20 rounded-2xl animate-ping opacity-20"
            :class="infoFiltroAtivo ? 'bg-amber-200' : 'bg-blue-200'"
          ></div>
        </div>

        <!-- Mensagem personalizada baseada no filtro -->
        <template v-if="infoFiltroAtivo">
          <h3 class="text-xl font-semibold text-gray-700 mb-2">
            Nenhum resultado para {{ infoFiltroAtivo.tipo.toLowerCase() }}
          </h3>
          <p class="text-gray-600 mb-4">{{ infoFiltroAtivo.descricao }}</p>
          <div class="flex items-center justify-center gap-2 px-4 py-2 bg-amber-100 rounded-lg mb-6 max-w-md mx-auto">
            <FontAwesomeIcon :icon="infoFiltroAtivo.icone" class="text-amber-600" />
            <code class="text-amber-800 font-medium">{{ filtroNome }}</code>
          </div>
          <button
            @click="filtroNome = ''"
            class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-amber-500 to-orange-500 text-white rounded-xl hover:from-amber-600 hover:to-orange-600 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg mx-auto font-medium"
          >
            <FontAwesomeIcon icon="times" />
            <span>Limpar filtro</span>
          </button>
        </template>

        <!-- Mensagem padrão (sem filtros) -->
        <template v-else>
          <h3 class="text-xl font-semibold text-gray-700 mb-2">Nenhum amiguinho encontrado</h3>
          <p class="text-gray-500 mb-6">
            Que tal cadastrar o primeiro pet no sistema? 🐾
          </p>
          <button
            @click="$router.push('/animais/novo')"
            class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-xl hover:from-blue-600 hover:to-indigo-700 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg mx-auto font-medium"
          >
            <FontAwesomeIcon icon="plus" />
            <span>Adicionar primeiro animal</span>
          </button>
        </template>
      </BaseCard>
    </div>
  </div>
</template>

<script setup lang="ts">
// 📚 Imports
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseButton from '@/components/UI/BaseButton.vue'
import { clientesService } from '@/services/api'
import type { Animal, Cliente } from '@/types/api'
import { formatarHorario, getCorTipoAnimal } from '@/utils/formatters'
import { useAutoAnimateList } from '@/composables/useAutoAnimate'

// 🎯 Configurações
const router = useRouter()

// 📊 Estado do componente
const animais = ref<Animal[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const ultimaAtualizacao = ref<string>('')

// 🔍 Filtros
const filtroNome = ref('')
const filtroCliente = ref('')
const filtroTipo = ref('')

// 📄 Estados da paginação
const itensPorPagina = ref(10)                // Quantos itens mostrar por vez
const itensExibidos = ref(10)                 // Quantos itens estão sendo exibidos atualmente

// 🎬 Auto-animate para listas
const listaAnimaisRef = ref<HTMLElement>()
useAutoAnimateList(listaAnimaisRef)

// 🎨 Computed Properties
const animaisFiltrados = computed(() => {
  return animais.value.filter(animal => {
    // 🧠 FILTROS INTELIGENTES com prefixos especiais
    let nomeMatch = true
    if (filtroNome.value) {
      const termoBusca = filtroNome.value.trim()

      // 🚀 FILTRO INTELIGENTE: Detecta prefixos especiais
      if (termoBusca.startsWith('#')) {
        // 🆔 Busca por ID do Sistema: #123
        const idBusca = termoBusca.substring(1)
        if (idBusca) {
          nomeMatch = animal.codigoAnimalSistema.toString().includes(idBusca) ||
                      animal.id?.toString().includes(idBusca)
        } else {
          nomeMatch = true // Se apenas # foi digitado, mostra todos
        }
      } else if (termoBusca.startsWith('$')) {
        // 💰 Busca por SimplesVet: $456
        const simplesVetBusca = termoBusca.substring(1)
        if (simplesVetBusca) {
          nomeMatch = animal.codigoSimplesVet?.toString().includes(simplesVetBusca) || false
        } else {
          nomeMatch = true // Se apenas $ foi digitado, mostra todos
        }
      } else {
        // 🔍 BUSCA NORMAL: Por nome do animal ou dono
        const termoBuscaLower = termoBusca.toLowerCase()

        // 📝 Busca por nome do animal
        const nomeAnimalMatch = animal.nome.toLowerCase().includes(termoBuscaLower)

        // 👤 Busca por nome do dono
        const donoMatch = animal.cliente?.nomeCompleto?.toLowerCase().includes(termoBuscaLower) || false

        // 🐕 Busca por tipo do animal
        const tipoAnimalMatch = animal.tipo.toLowerCase().includes(termoBuscaLower)

        nomeMatch = nomeAnimalMatch || donoMatch || tipoAnimalMatch
      }
    }

    // 👤 Filtro específico por cliente (dropdown)
    const clienteMatch = !filtroCliente.value ||
      animal.cliente?.nomeCompleto === filtroCliente.value

    // 🐕 Filtro específico por tipo (dropdown)
    const tipoMatch = !filtroTipo.value ||
      animal.tipo === filtroTipo.value

    return nomeMatch && clienteMatch && tipoMatch
  })
})

// 📄 Animais que devem aparecer na tela (paginados)
const animaisExibidos = computed(() => {
  return animaisFiltrados.value.slice(0, itensExibidos.value)
})

// 📊 Controles da paginação
const temMaisItens = computed(() => {
  return animaisFiltrados.value.length > itensExibidos.value
})

const totalItensDisponiveis = computed(() => {
  return animaisFiltrados.value.length
})

const clientesUnicos = computed(() => {
  const clientes = animais.value
    .map(animal => animal.cliente?.nomeCompleto)
    .filter(Boolean)
  return [...new Set(clientes)].sort()
})

const tiposUnicos = computed(() => {
  const tipos = animais.value
    .map(animal => animal.tipo)
    .filter(Boolean)
  return [...new Set(tipos)].sort()
})

// 🎯 Informações sobre o filtro ativo
const infoFiltroAtivo = computed(() => {
  const termo = filtroNome.value?.trim()
  if (!termo) return null

  if (termo.startsWith('#')) {
    const id = termo.substring(1)
    return {
      tipo: 'ID Sistema',
      icone: 'hashtag',
      cor: 'blue',
      valor: id || '...',
      descricao: id ? `Buscando ID do sistema: ${id}` : 'Digite o ID do sistema após #'
    }
  } else if (termo.startsWith('$')) {
    const simplesVet = termo.substring(1)
    return {
      tipo: 'SimplesVet',
      icone: 'dollar-sign',
      cor: 'green',
      valor: simplesVet || '...',
      descricao: simplesVet ? `Buscando SimplesVet: ${simplesVet}` : 'Digite o código SimplesVet após $'
    }
  } else {
    return {
      tipo: 'Busca Normal',
      icone: 'search',
      cor: 'purple',
      valor: termo,
      descricao: `Buscando por nome/dono/tipo: "${termo}"`
    }
  }
})

// 🛠️ Métodos
const carregarAnimais = async (): Promise<void> => {
  try {
    console.log('🔄 Iniciando carregamento de animais...')
    loading.value = true
    error.value = null

    // 🔄 Busca clientes com animais (dados completos)
    const clientes = await clientesService.buscarTodos()

    // 🐕 Extrai todos os animais dos clientes e adiciona referência ao cliente
    const todosAnimais: Animal[] = []
    clientes.forEach((cliente: Cliente) => {
      cliente.animais.forEach((animal: Animal) => {
        todosAnimais.push({
          ...animal,
          cliente: {
            id: cliente.id,
            nomeCompleto: cliente.nomeCompleto,
            cpf: cliente.cpf,
            codigoSimplesVet: cliente.codigoSimplesVet,
            codigoClienteSistema: cliente.codigoClienteSistema,
            telefones: cliente.telefones,
            emailClientes: cliente.emailClientes,
            animais: [] // Evita referência circular
          }
        })
      })
    })

    animais.value = todosAnimais
    ultimaAtualizacao.value = formatarHorario()
    console.log(`✅ ${todosAnimais.length} animais carregados com sucesso (com dados dos donos)!`)
  } catch (err) {
    console.error('❌ Erro ao carregar animais:', err)
    error.value = err instanceof Error ? err.message : 'Ocorreu um erro inesperado ao carregar os animais'
  } finally {
    loading.value = false
  }
}

// Funções movidas para @/utils/formatters

const visualizarAnimal = (animal: Animal): void => {
  console.log('👁️ Visualizar animal:', animal.nome)
  router.push(`/animais/${animal.id}/editar`)
}

// 📄 Funções de Paginação
const carregarMais = (): void => {
  console.log('📄 Carregando mais animais...')
  const proximosItens = Math.min(itensPorPagina.value, totalItensDisponiveis.value - itensExibidos.value)
  itensExibidos.value += proximosItens
  console.log(`✅ Mostrando agora ${itensExibidos.value} de ${totalItensDisponiveis.value} animais`)
}

const resetarPaginacao = (): void => {
  console.log('🔄 Resetando paginação...')
  itensExibidos.value = itensPorPagina.value
}

// 👀 Watchers para resetar paginação quando filtros mudam
watch([filtroNome, filtroCliente, filtroTipo], () => {
  resetarPaginacao()
})

const editarAnimal = (animal: Animal): void => {
  console.log('✏️ Editar animal:', animal.nome)
  router.push(`/animais/${animal.id}/editar`)
}

// 🎬 Lifecycle
onMounted(() => {
  console.log('🎬 Página de animais carregada!')
  carregarAnimais()
})
</script>

<!-- Estilos movidos para @/assets/styles/animations.css -->
