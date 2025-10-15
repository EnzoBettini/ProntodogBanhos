<template>
  <div class="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-green-100">
    <!-- 🌟 Header com gradiente elegante -->
    <div class="relative overflow-hidden bg-gradient-to-r from-emerald-600 via-green-600 to-emerald-700 text-white pt-16">
      <!-- Background pattern -->
      <div class="absolute inset-0 opacity-10 bg-pattern"></div>

      <div class="relative px-6 py-8 z-20">
        <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
          <div class="flex items-center gap-4 animate-fade-in-up">
            <!-- Ícone animado -->
            <div class="relative">
              <div class="w-16 h-16 bg-white bg-opacity-20 backdrop-blur-sm rounded-2xl flex items-center justify-center transform hover:scale-110 transition-all duration-300 shadow-lg">
                <FontAwesomeIcon icon="users" class="text-2xl text-white animate-bounce-gentle" />
              </div>
              <div v-if="!loading && clientes.length > 0" class="absolute -top-1 -right-1 w-8 h-8 bg-yellow-400 rounded-full flex items-center justify-center animate-pulse">
                <span class="text-xs font-bold text-yellow-800">{{ clientes.length }}</span>
              </div>
            </div>

            <div class="space-y-1">
              <h1 class="text-4xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-emerald-100">
                Lista de Clientes
              </h1>
              <p class="text-white text-lg flex items-center gap-2 font-medium opacity-90">
                <FontAwesomeIcon icon="star" class="text-yellow-400 animate-twinkle mr-1" />
                Gerencie todos os clientes do ProntoDog Banhos
              </p>
            </div>
          </div>

          <!-- Botões de ação elegantes -->
          <div class="flex items-center gap-3">
            <button
              @click="carregarClientes"
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
              @click="$router.push('/clientes/novo')"
              class="group flex items-center gap-2 px-6 py-2 bg-gradient-to-r from-yellow-400 to-orange-500 text-white rounded-xl hover:from-yellow-500 hover:to-orange-600 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-xl font-medium shadow-lg"
            >
              <FontAwesomeIcon icon="plus" class="group-hover:rotate-90 transition-transform duration-300" />
              <span>Novo Cliente</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Wave decoration -->
      <div class="absolute bottom-0 left-0 right-0 z-10">
        <svg viewBox="0 0 1440 60" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full h-15">
          <defs>
            <linearGradient id="waveGradient" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" style="stop-color:#3b82f6;stop-opacity:1" />
              <stop offset="50%" style="stop-color:#1d4ed8;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#1e40af;stop-opacity:1" />
            </linearGradient>
          </defs>
          <path d="M0 60h1440V0c-120 40-240 60-360 60S840 40 720 60s-240 0-360-60S120 40 0 60z" fill="url(#waveGradient)"/>
        </svg>
      </div>
    </div>

    <!-- Container principal -->
    <div class="relative -mt-8 px-6 pb-8 z-30">

      <!-- 🎯 Estados da aplicação -->

      <!-- ⏳ Estado de Loading -->
      <div v-if="loading && clientes.length === 0" class="text-center py-12">
        <FontAwesomeIcon icon="spinner" class="text-4xl text-primary-500 animate-spin mb-4" />
        <h3 class="text-lg font-medium text-gray-700 mb-2">Carregando clientes...</h3>
        <p class="text-gray-500">Buscando dados na API</p>
      </div>

      <!-- ❌ Estado de Erro -->
      <div v-else-if="error" class="text-center py-12">
        <FontAwesomeIcon icon="exclamation-triangle" class="text-4xl text-red-500 mb-4" />
        <h3 class="text-lg font-medium text-red-700 mb-2">Erro ao carregar clientes</h3>
        <p class="text-red-600 mb-4">{{ error }}</p>
        <BaseButton variant="outline" @click="carregarClientes">
          <FontAwesomeIcon icon="refresh" class="mr-2" />
          Tentar Novamente
        </BaseButton>
      </div>

      <!-- 📭 Estado Vazio -->
      <div v-else-if="!loading && clientes.length === 0" class="text-center py-12">
        <FontAwesomeIcon icon="users" class="text-4xl text-gray-400 mb-4" />
        <h3 class="text-lg font-medium text-gray-700 mb-2">Nenhum cliente encontrado</h3>
        <p class="text-gray-500 mb-4">Comece adicionando seu primeiro cliente!</p>
        <BaseButton variant="primary" @click="$router.push('/clientes/novo')">
          <FontAwesomeIcon icon="plus" class="mr-2" />
          Adicionar Primeiro Cliente
        </BaseButton>
      </div>

      <!-- 🔍 Estado de busca sem resultados -->
      <div v-else-if="!loading && clientes.length > 0 && clientesFiltrados.length === 0" class="p-12 text-center">
        <BaseCard class="bg-gradient-to-br from-amber-50 to-orange-50 border-0 shadow-lg">
          <div class="p-8">
            <div class="relative">
              <div class="w-20 h-20 rounded-2xl flex items-center justify-center mx-auto mb-6 animate-bounce-gentle"
                :class="infoFiltroAtivo ? 'bg-gradient-to-br from-amber-400 to-orange-500' : 'bg-gradient-to-br from-emerald-400 to-green-500'"
              >
                <FontAwesomeIcon
                  :icon="infoFiltroAtivo ? 'search' : 'users'"
                  class="text-3xl text-white"
                />
              </div>
              <div class="absolute top-0 left-1/2 transform -translate-x-1/2 w-20 h-20 rounded-2xl animate-ping opacity-20"
                :class="infoFiltroAtivo ? 'bg-amber-200' : 'bg-emerald-200'"
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
                <code class="text-amber-800 font-medium">{{ filtroBusca }}</code>
              </div>
              <button
                @click="filtroBusca = ''"
                class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-amber-500 to-orange-500 text-white rounded-xl hover:from-amber-600 hover:to-orange-600 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg mx-auto font-medium"
              >
                <FontAwesomeIcon icon="times" />
                <span>Limpar filtro</span>
              </button>
            </template>
          </div>
        </BaseCard>
      </div>

      <!-- ✅ Lista de Clientes (Estado de Sucesso) -->
      <div v-else>
        <!-- 🔍 Filtros elegantes -->
        <BaseCard class="mb-6 shadow-lg border-0 bg-white bg-opacity-90 backdrop-blur-sm animate-slide-up">
          <div class="p-4">
            <div class="flex flex-col md:flex-row gap-4 items-center">
              <!-- Campo de busca principal -->
              <div class="flex-1 relative group">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <FontAwesomeIcon icon="search" class="h-5 w-5 text-emerald-400" />
                </div>
                <input
                  v-model="filtroBusca"
                  type="text"
                  placeholder="Digite nome ou use: #123 (ID), $456 (SimplesVet), @12345678900 (CPF)..."
                  class="w-full pl-10 pr-12 py-3 bg-gradient-to-r from-white to-emerald-50 border border-emerald-200 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-emerald-500 transition-all duration-300 placeholder-gray-400 text-gray-700 shadow-sm"
                />

                <!-- Tooltip de ajuda -->
                <div class="absolute inset-y-0 right-0 pr-3 flex items-center">
                  <div class="relative group/tooltip">
                    <FontAwesomeIcon icon="info-circle" class="h-4 w-4 text-gray-400 hover:text-emerald-500 cursor-help transition-colors" />

                    <!-- Conteúdo do tooltip -->
                    <div class="absolute bottom-full right-0 mb-2 w-80 p-3 bg-gray-900 text-white text-sm rounded-lg shadow-xl opacity-0 invisible group-hover/tooltip:opacity-100 group-hover/tooltip:visible transition-all duration-200 z-50">
                      <div class="space-y-2">
                        <p class="font-semibold text-emerald-300">💡 Filtros Inteligentes:</p>
                        <div class="space-y-1 text-xs">
                          <p>📝 <span class="text-emerald-200">Nome:</span> Maria Silva</p>
                          <p>🆔 <span class="text-emerald-200">ID Sistema:</span> <code class="bg-gray-800 px-1 rounded">#123</code></p>
                          <p>🏥 <span class="text-emerald-200">SimplesVet:</span> <code class="bg-gray-800 px-1 rounded">$456</code></p>
                          <p>📱 <span class="text-emerald-200">CPF:</span> <code class="bg-gray-800 px-1 rounded">@12345678900</code></p>
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
                <div class="flex flex-wrap items-center gap-2 px-3 py-2 bg-gradient-to-r from-emerald-50 to-green-50 rounded-lg border border-emerald-100">
                  <FontAwesomeIcon icon="lightbulb" class="text-emerald-500 text-sm" />
                  <span class="text-xs text-emerald-700 font-medium">Filtros inteligentes:</span>
                  <div class="flex flex-wrap gap-2 text-xs">
                    <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-emerald-700">
                      <FontAwesomeIcon icon="hashtag" class="mr-1" /><code>#123</code> = ID Sistema
                    </span>
                    <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-emerald-700">
                      <FontAwesomeIcon icon="dollar-sign" class="mr-1" /><code>$456</code> = SimplesVet
                    </span>
                    <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-emerald-700">
                      <FontAwesomeIcon icon="at" class="mr-1" /><code>@123456</code> = CPF
                    </span>
                    <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-emerald-700">
                      <FontAwesomeIcon icon="user" class="mr-1" /><code>Maria</code> = Nome
                    </span>
                  </div>
                </div>
              </div>

              <!-- Stats rápidas -->
              <div class="flex items-center gap-3 flex-wrap">
                <!-- Filtro Ativo (aparece quando há busca) -->
                <div v-if="infoFiltroAtivo" class="flex items-center gap-2 px-4 py-2 rounded-full shadow-sm border-2 animate-pulse"
                  :class="{
                    'bg-blue-50 border-blue-200 text-blue-700': infoFiltroAtivo.cor === 'blue',
                    'bg-green-50 border-green-200 text-green-700': infoFiltroAtivo.cor === 'green',
                    'bg-purple-50 border-purple-200 text-purple-700': infoFiltroAtivo.cor === 'purple',
                    'bg-emerald-50 border-emerald-200 text-emerald-700': infoFiltroAtivo.cor === 'emerald'
                  }"
                >
                  <FontAwesomeIcon :icon="infoFiltroAtivo.icone" class="text-sm" />
                  <span class="text-sm font-bold">{{ infoFiltroAtivo.tipo }}</span>
                  <code class="text-xs bg-white bg-opacity-60 px-1.5 py-0.5 rounded">{{ infoFiltroAtivo.valor }}</code>
                </div>

                <!-- Stats normais -->
                <div class="flex items-center gap-2 px-3 py-1.5 bg-gradient-to-r from-emerald-100 to-green-100 rounded-full">
                  <FontAwesomeIcon icon="users" class="text-emerald-600 text-sm" />
                  <span class="text-sm font-medium text-emerald-700">{{ clientesExibidos.length }} de {{ totalItensDisponiveis }} encontrados</span>
                </div>

                <div v-if="!infoFiltroAtivo" class="flex items-center gap-2 px-3 py-1.5 bg-gradient-to-r from-blue-100 to-indigo-100 rounded-full">
                  <FontAwesomeIcon icon="dog" class="text-blue-600 text-sm" />
                  <span class="text-sm font-medium text-blue-700">{{ totalAnimais }} pets</span>
                </div>
              </div>
            </div>
          </div>
        </BaseCard>

        <!-- 🎨 Lista elegante de clientes -->
        <div ref="listaClientesRef" class="space-y-4">
          <div
            v-for="(cliente, index) in clientesExibidos"
            :key="cliente.id"
            class="group relative bg-gradient-to-r from-white via-white to-emerald-50 rounded-xl shadow-lg hover:shadow-2xl cursor-pointer transform transition-all duration-200 hover:-translate-y-1 animate-fade-in overflow-hidden"
            @click="verDetalhes(cliente)"
          >
              <div class="p-6">
                <div class="flex items-center justify-between">
                  <!-- 👤 Avatar e informações principais -->
                  <div class="flex items-center gap-4 flex-1">
                    <!-- Avatar circular com gradiente -->
                    <div class="relative">
                      <div class="w-16 h-16 bg-gradient-to-br from-emerald-400 to-green-600 rounded-2xl flex items-center justify-center shadow-lg transform group-hover:scale-105 transition-transform duration-200">
                        <FontAwesomeIcon icon="user" class="text-2xl text-white" />
                      </div>
                      <!-- Badge de pets -->
                      <div class="absolute -top-1 -right-1 w-6 h-6 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-full flex items-center justify-center shadow-md">
                        <span class="text-xs font-bold text-white">{{ cliente.animais.length }}</span>
                      </div>
                    </div>

                    <!-- Informações do cliente -->
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center gap-3 mb-1">
                        <h3 class="text-xl font-bold text-gray-900 truncate group-hover:text-emerald-700 transition-colors duration-300">
                          {{ cliente.nomeCompleto }}
                        </h3>
                        <BaseBadge
                          :variant="cliente.animais.length > 0 ? 'success' : 'gray'"
                          size="sm"
                          class="flex-shrink-0"
                        >
                          {{ cliente.animais.length }} pets
                        </BaseBadge>
                      </div>

                      <div class="flex items-center gap-4 text-sm text-gray-600 mb-2 flex-wrap">
                        <span class="flex items-center gap-1">
                          <FontAwesomeIcon icon="hashtag" class="text-gray-400" />
                          ID: {{ cliente.codigoClienteSistema }}
                        </span>
                        <span class="flex items-center gap-1">
                          <FontAwesomeIcon icon="id-card" class="text-gray-400" />
                          {{ formatarCpf(cliente.cpf) }}
                        </span>
                        <span class="flex items-center gap-1">
                          <FontAwesomeIcon icon="code" class="text-gray-400" />
                          SimplesVet: {{ cliente.codigoSimplesVet }}
                        </span>
                      </div>

                      <!-- Contatos em linha -->
                      <div class="flex items-center gap-6 text-sm">
                        <div v-if="cliente.telefones.length > 0" class="flex items-center gap-2">
                          <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                            <FontAwesomeIcon icon="phone" class="text-blue-600 text-xs" />
                          </div>
                          <span class="text-gray-700">
                            {{ formatarTelefone(cliente.telefones[0]?.telefone || '') }}
                            <span v-if="cliente.telefones.length > 1" class="text-gray-400">
                              (+{{ cliente.telefones.length - 1 }})
                            </span>
                          </span>
                        </div>

                        <div v-if="cliente.emailClientes.length > 0" class="flex items-center gap-2">
                          <div class="w-8 h-8 bg-purple-100 rounded-full flex items-center justify-center">
                            <FontAwesomeIcon icon="envelope" class="text-purple-600 text-xs" />
                          </div>
                          <span class="text-gray-700 truncate max-w-[200px]">
                            {{ cliente.emailClientes[0]?.email || '' }}
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 🐕 Seção de pets -->
                  <div class="flex-shrink-0 ml-4">
                    <div v-if="cliente.animais.length > 0" class="text-right">
                      <p class="text-xs text-gray-500 mb-2 font-medium">Pets cadastrados:</p>
                      <div class="flex flex-wrap gap-1 justify-end max-w-[280px]">
                        <BaseBadge
                          v-for="animal in cliente.animais.slice(0, 4)"
                          :key="animal.id"
                          variant="info"
                          size="sm"
                          class="transform hover:scale-105 transition-transform duration-200"
                        >
                          {{ animal.nome }}
                        </BaseBadge>
                        <BaseBadge
                          v-if="cliente.animais.length > 4"
                          variant="gray"
                          size="sm"
                        >
                          +{{ cliente.animais.length - 4 }}
                        </BaseBadge>
                      </div>
                    </div>
                    <div v-else class="text-center">
                      <p class="text-sm text-gray-400 italic">Nenhum pet cadastrado</p>
                    </div>
                  </div>

                  <!-- 🔗 Indicador de ação -->
                  <div class="flex-shrink-0 ml-4">
                    <div class="w-10 h-10 bg-gray-100 group-hover:bg-emerald-100 rounded-full flex items-center justify-center transition-all duration-300">
                      <FontAwesomeIcon
                        icon="chevron-right"
                        class="text-gray-400 group-hover:text-emerald-600 group-hover:translate-x-1 transition-all duration-300"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <!-- Borda gradiente no hover -->
              <div class="absolute inset-0 border-2 border-transparent group-hover:border-emerald-200 rounded-xl transition-all duration-300 pointer-events-none"></div>
          </div>
        </div>

        <!-- 📄 Botão Carregar Mais -->
        <div v-if="temMaisItens" class="mt-8 flex justify-center">
          <BaseButton
            @click="carregarMais"
            class="group relative px-8 py-4 bg-gradient-to-r from-emerald-500 to-green-600 hover:from-emerald-600 hover:to-green-700 text-white font-semibold rounded-xl shadow-lg transform hover:-translate-y-1 hover:shadow-2xl transition-all duration-300 flex items-center gap-3"
          >
            <FontAwesomeIcon icon="chevron-down" class="group-hover:translate-y-1 transition-transform duration-300" />
            <span>Carregar mais {{ Math.min(itensPorPagina, totalItensDisponiveis - itensExibidos) }} clientes</span>
            <div class="absolute inset-0 bg-white opacity-0 group-hover:opacity-20 rounded-xl transition-opacity duration-300"></div>
          </BaseButton>
        </div>

        <!-- 📊 Rodapé elegante com estatísticas -->
        <BaseCard class="mt-8 bg-gradient-to-r from-gray-50 to-emerald-50 border-0 shadow-sm">
          <div class="p-4">
            <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4 text-sm">
              <div class="flex flex-wrap items-center gap-4">
                <div class="flex items-center gap-2">
                  <div class="w-2 h-2 bg-emerald-500 rounded-full animate-pulse"></div>
                  <span class="text-gray-600 font-medium">
                    <span class="text-emerald-600 font-bold">{{ clientes.length }}</span> clientes total
                  </span>
                </div>
                <div class="flex items-center gap-2">
                  <div class="w-2 h-2 bg-blue-500 rounded-full animate-pulse"></div>
                  <span class="text-gray-600 font-medium">
                    <span class="text-blue-600 font-bold">{{ totalAnimais }}</span> pets cadastrados
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
    </div>

  </div>
</template>

<script setup lang="ts">
// 📚 Imports necessários
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseButton from '@/components/UI/BaseButton.vue'
import BaseBadge from '@/components/UI/BaseBadge.vue'
import { clientesService } from '@/services/api'
import type { Cliente } from '@/types/api'
import { formatarCpf, formatarTelefone, formatarHorario } from '@/utils/formatters'
import { useAutoAnimateList } from '@/composables/useAutoAnimate'

// 🎯 Configuração do router
const router = useRouter()

// 📊 Estados reativos da aplicação
const clientes = ref<Cliente[]>([])           // Lista completa de clientes
const loading = ref(false)                    // Estado de carregamento
const error = ref<string | null>(null)       // Mensagem de erro
const filtroBusca = ref('')                  // Filtro de busca geral
const ultimaAtualizacao = ref('')            // Timestamp da última atualização

// 📄 Estados da paginação
const itensPorPagina = ref(10)                // Quantos itens mostrar por vez
const itensExibidos = ref(10)                 // Quantos itens estão sendo exibidos atualmente


// 🎬 Auto-animate para listas
const listaClientesRef = ref<HTMLElement>()
useAutoAnimateList(listaClientesRef)

// 💻 Computadas (dados derivados)
const clientesFiltrados = computed(() => {
  if (!filtroBusca.value) return clientes.value

  return clientes.value.filter(cliente => {
    // 🧠 FILTROS INTELIGENTES com prefixos especiais
    const termoBusca = filtroBusca.value.trim()

    // 🚀 FILTRO INTELIGENTE: Detecta prefixos especiais
    if (termoBusca.startsWith('#')) {
      // 🆔 Busca por ID do Sistema: #123
      const idBusca = termoBusca.substring(1)
      if (idBusca) {
        return cliente.codigoClienteSistema.toString().includes(idBusca) ||
               cliente.id.toString().includes(idBusca)
      } else {
        return true // Se apenas # foi digitado, mostra todos
      }
    } else if (termoBusca.startsWith('$')) {
      // 🏥 Busca por SimplesVet: $456
      const simplesVetBusca = termoBusca.substring(1)
      if (simplesVetBusca) {
        return cliente.codigoSimplesVet.toString().includes(simplesVetBusca)
      } else {
        return true // Se apenas $ foi digitado, mostra todos
      }
    } else if (termoBusca.startsWith('@')) {
      // 📱 Busca por CPF: @12345678900
      const cpfBusca = termoBusca.substring(1)
      if (cpfBusca) {
        const cpfLimpo = cliente.cpf.replace(/\D/g, '') // Remove formatação
        const cpfFormatado = formatarCpf(cliente.cpf)
        return cpfLimpo.includes(cpfBusca.replace(/\D/g, '')) ||
               cpfFormatado.includes(cpfBusca)
      } else {
        return true // Se apenas @ foi digitado, mostra todos
      }
    } else {
      // 🔍 BUSCA NORMAL: Por nome do cliente
      const termoBuscaLower = termoBusca.toLowerCase()

      // 📝 Busca por nome do cliente
      const nomeMatch = cliente.nomeCompleto.toLowerCase().includes(termoBuscaLower)

      return nomeMatch
    }
  })
})

// 📄 Clientes que devem aparecer na tela (paginados)
const clientesExibidos = computed(() => {
  return clientesFiltrados.value.slice(0, itensExibidos.value)
})

// 📊 Controles da paginação
const temMaisItens = computed(() => {
  return clientesFiltrados.value.length > itensExibidos.value
})

const totalItensDisponiveis = computed(() => {
  return clientesFiltrados.value.length
})

const totalAnimais = computed(() => {
  return clientes.value.reduce((total, cliente) => total + cliente.animais.length, 0)
})

// 🎯 Informações sobre o filtro ativo
const infoFiltroAtivo = computed(() => {
  const termo = filtroBusca.value?.trim()
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
  } else if (termo.startsWith('@')) {
    const cpf = termo.substring(1)
    return {
      tipo: 'CPF',
      icone: 'at',
      cor: 'purple',
      valor: cpf || '...',
      descricao: cpf ? `Buscando CPF: ${cpf}` : 'Digite o CPF após @'
    }
  } else {
    return {
      tipo: 'Nome',
      icone: 'user',
      cor: 'emerald',
      valor: termo,
      descricao: `Buscando por nome: "${termo}"`
    }
  }
})

// 🔧 Funções utilitárias movidas para @/utils/formatters

// 🚀 Função principal para carregar clientes
const carregarClientes = async (): Promise<void> => {
  try {
    console.log('🔄 Iniciando carregamento de clientes...')

    // Ativa o estado de loading
    loading.value = true
    error.value = null

    // 🌐 Chama a API
    const response = await clientesService.buscarTodos()

    // ✅ Sucesso - atualiza os dados
    clientes.value = response
    ultimaAtualizacao.value = formatarHorario()

    console.log(`✅ ${response.length} clientes carregados com sucesso!`)

  } catch (err) {
    // ❌ Erro - mostra mensagem para o usuário
    console.error('❌ Erro ao carregar clientes:', err)

    error.value = err instanceof Error
      ? err.message
      : 'Ocorreu um erro inesperado ao carregar os clientes'

  } finally {
    // 🏁 Sempre desativa o loading
    loading.value = false
  }
}

// 👀 Função para ver detalhes do cliente
const verDetalhes = (cliente: Cliente): void => {
  console.log('👁️ Ver detalhes:', cliente.nomeCompleto)
  router.push(`/clientes/${cliente.id}/editar`)
}

const onClienteExcluido = (clienteId: number): void => {
  // Remove o cliente da lista local
  clientes.value = clientes.value.filter(c => c.id !== clienteId)
  console.log(`✅ Cliente ID ${clienteId} removido da lista`)
}

const onClienteAtualizado = (clienteAtualizado: Cliente): void => {
  // Atualiza o cliente na lista local
  const index = clientes.value.findIndex(c => c.id === clienteAtualizado.id)
  if (index !== -1) {
    clientes.value[index] = clienteAtualizado
    console.log(`✅ Cliente ID ${clienteAtualizado.id} atualizado na lista`)
  }
}

// 📄 Funções de Paginação
const carregarMais = (): void => {
  console.log('📄 Carregando mais clientes...')
  const proximosItens = Math.min(itensPorPagina.value, totalItensDisponiveis.value - itensExibidos.value)
  itensExibidos.value += proximosItens
  console.log(`✅ Mostrando agora ${itensExibidos.value} de ${totalItensDisponiveis.value} clientes`)
}

const resetarPaginacao = (): void => {
  console.log('🔄 Resetando paginação...')
  itensExibidos.value = itensPorPagina.value
}

// 👀 Watcher para resetar paginação quando filtro muda
watch(filtroBusca, () => {
  resetarPaginacao()
})

// 🎬 Lifecycle - carrega dados quando o componente é montado
onMounted(() => {
  console.log('🎬 Componente ClientesView montado!')
  carregarClientes()
})
</script>

<!-- Estilos movidos para @/assets/styles/animations.css -->
