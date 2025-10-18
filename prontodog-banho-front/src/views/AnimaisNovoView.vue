<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-100">
    <!-- 🌟 Header com gradiente animado -->
    <div class="relative overflow-hidden bg-gradient-to-r from-blue-600 via-blue-700 to-blue-800 text-white pt-16">
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
              <div class="absolute -top-1 -right-1 w-6 h-6 bg-yellow-400 rounded-full flex items-center justify-center animate-pulse">
                <FontAwesomeIcon icon="plus" class="text-xs text-yellow-800" />
              </div>
            </div>

            <div class="space-y-1">
              <h1 class="text-4xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-blue-100">
                Novo Animal
              </h1>
              <p class="text-white text-lg flex items-center gap-2 font-medium opacity-90">
                <FontAwesomeIcon icon="star" class="text-yellow-400 animate-twinkle mr-1" />
                Cadastre um novo amiguinho no sistema
              </p>
            </div>
          </div>

          <!-- Botão voltar elegante -->
          <button
            @click="$router.push('/animais')"
            class="group flex items-center gap-2 px-6 py-3 bg-white bg-opacity-20 backdrop-blur-sm text-white rounded-xl hover:bg-opacity-30 transition-all duration-300 border border-white border-opacity-20 hover:border-opacity-40 transform hover:-translate-y-1 hover:shadow-lg"
          >
            <FontAwesomeIcon icon="arrow-left" class="group-hover:-translate-x-1 transition-transform duration-300" />
            <span class="font-medium">Voltar</span>
          </button>
        </div>
      </div>

      <!-- Wave decoration -->
      <div class="absolute bottom-0 left-0 right-0 z-10">
        <svg viewBox="0 0 1440 60" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full h-15">
          <path d="M0 60h1440V0c-120 40-240 60-360 60S840 40 720 60s-240 0-360-60S120 40 0 60z" fill="#10b981"/>
        </svg>
      </div>
    </div>

    <!-- 📝 Container do formulário -->
    <div class="px-6 pb-8 -mt-8 relative z-10">
      <div class="max-w-4xl mx-auto">
        <!-- Card principal com glassmorphism -->
        <div class="relative bg-white bg-opacity-80 backdrop-blur-xl rounded-3xl shadow-2xl border border-white border-opacity-20 animate-slide-up">

          <!-- Loading overlay melhorado -->
          <div v-if="salvando" class="absolute inset-0 bg-gradient-to-br from-blue-500 to-purple-600 bg-opacity-95 backdrop-blur-sm flex items-center justify-center z-50">
            <div class="text-center text-white">
              <!-- Spinner customizado -->
              <div class="relative mb-6">
                <div class="w-20 h-20 border-4 border-white border-opacity-30 rounded-full animate-spin"></div>
                <div class="absolute top-0 left-0 w-20 h-20 border-4 border-transparent border-t-white rounded-full animate-spin"></div>
                <FontAwesomeIcon icon="dog" class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-2xl animate-bounce" />
              </div>
              <h3 class="text-xl font-semibold mb-2">Salvando seu amiguinho...</h3>
              <p class="text-white text-opacity-80">Isso vai ser rapidinho! 🐾</p>
            </div>
          </div>

          <form @submit.prevent="salvarAnimal" class="p-8 space-y-8">
            <!-- 🐕 Seção: Dados do Animal -->
            <div class="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl p-6 border border-blue-100 transform hover:scale-[1.01] transition-all duration-300">
              <div class="flex items-center gap-3 mb-6">
                <div class="w-10 h-10 bg-gradient-to-r from-blue-500 to-indigo-500 rounded-xl flex items-center justify-center">
                  <FontAwesomeIcon icon="dog" class="text-white" />
                </div>
                <h2 class="text-xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
                  Dados do Animal
                </h2>
              </div>

              <div class="grid md:grid-cols-2 gap-6">
                <!-- Nome do animal -->
                <div class="space-y-2">
                  <label class="flex items-center gap-2 text-sm font-semibold text-gray-700">
                    <FontAwesomeIcon icon="heart" class="text-blue-500" />
                    Nome do Animal *
                  </label>
                  <div class="relative group">
                    <input
                      v-model="formulario.nome"
                      :class="[
                        'w-full pl-12 pr-4 py-3 border-2 rounded-xl focus:ring-4 focus:ring-blue-200 focus:border-blue-500 transition-all duration-300 placeholder-gray-400 bg-white group-hover:shadow-md',
                        erros.nome ? 'border-red-400 focus:border-red-500 focus:ring-red-200' : 'border-gray-200 hover:border-blue-300'
                      ]"
                      type="text"
                      placeholder="Ex: Rex, Bella, Luna..."
                      required
                    />
                    <div class="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 group-focus-within:text-blue-500 transition-colors">
                      <FontAwesomeIcon icon="heart" class="animate-pulse-gentle" />
                    </div>
                  </div>
                  <p v-if="erros.nome" class="text-red-500 text-sm flex items-center gap-1">
                    <FontAwesomeIcon icon="exclamation-circle" />
                    {{ erros.nome }}
                  </p>
                </div>

                <!-- Tipo do animal -->
                <div class="space-y-2">
                  <label class="flex items-center gap-2 text-sm font-semibold text-gray-700">
                    <FontAwesomeIcon icon="list" class="text-indigo-500" />
                    Tipo *
                  </label>
                  <div class="relative group">
                    <select
                      v-model="formulario.tipo"
                      :class="[
                        'w-full pl-12 pr-4 py-3 border-2 rounded-xl focus:ring-4 focus:ring-indigo-200 focus:border-indigo-500 transition-all duration-300 bg-white appearance-none cursor-pointer group-hover:shadow-md',
                        erros.tipo ? 'border-red-400 focus:border-red-500 focus:ring-red-200' : 'border-gray-200 hover:border-indigo-300'
                      ]"
                      required
                    >
                      <option value="">✨ Selecione o tipo</option>
                      <option v-for="tipo in TIPOS_ANIMAIS" :key="tipo" :value="tipo">
                        {{ getIconeTipoAnimal(tipo) }} {{ tipo }}
                      </option>
                    </select>
                    <div class="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 group-focus-within:text-indigo-500 transition-colors pointer-events-none">
                      <FontAwesomeIcon icon="dog" />
                    </div>
                    <div class="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-400 group-focus-within:text-indigo-500 transition-colors pointer-events-none">
                      <FontAwesomeIcon icon="chevron-down" />
                    </div>
                  </div>
                  <p v-if="erros.tipo" class="text-red-500 text-sm flex items-center gap-1">
                    <FontAwesomeIcon icon="exclamation-circle" />
                    {{ erros.tipo }}
                  </p>
                </div>

                <!-- Raça do animal -->
                <div class="space-y-2">
                  <label class="flex items-center gap-2 text-sm font-semibold text-gray-700">
                    <FontAwesomeIcon icon="paw" class="text-purple-500" />
                    Raça
                    <span class="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded-full">opcional</span>
                  </label>
                  <div class="relative group">
                    <input
                      v-model="formulario.raca"
                      :class="[
                        'w-full pl-12 pr-4 py-3 border-2 rounded-xl focus:ring-4 focus:ring-purple-200 focus:border-purple-500 transition-all duration-300 placeholder-gray-400 bg-white group-hover:shadow-md',
                        erros.raca ? 'border-red-400 focus:border-red-500 focus:ring-red-200' : 'border-gray-200 hover:border-purple-300'
                      ]"
                      type="text"
                      placeholder="Ex: Labrador, Persa, SRD..."
                    />
                    <div class="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 group-focus-within:text-purple-500 transition-colors">
                      <FontAwesomeIcon icon="paw" class="animate-pulse-gentle" />
                    </div>
                  </div>
                  <p v-if="erros.raca" class="text-red-500 text-sm flex items-center gap-1">
                    <FontAwesomeIcon icon="exclamation-circle" />
                    {{ erros.raca }}
                  </p>
                </div>

                <!-- Peso do animal -->
                <div class="space-y-2">
                  <label class="flex items-center gap-2 text-sm font-semibold text-gray-700">
                    <FontAwesomeIcon icon="calculator" class="text-orange-500" />
                    Peso (kg)
                    <span class="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded-full">opcional</span>
                  </label>
                  <div class="relative group">
                    <input
                      v-model="formulario.peso"
                      :class="[
                        'w-full pl-12 pr-4 py-3 border-2 rounded-xl focus:ring-4 focus:ring-orange-200 focus:border-orange-500 transition-all duration-300 placeholder-gray-400 bg-white group-hover:shadow-md',
                        erros.peso ? 'border-red-400 focus:border-red-500 focus:ring-red-200' : 'border-gray-200 hover:border-orange-300'
                      ]"
                      type="number"
                      step="0.01"
                      min="0"
                      max="999.99"
                      placeholder="Ex: 5.5, 12.8, 0.8..."
                    />
                    <div class="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 group-focus-within:text-orange-500 transition-colors">
                      <FontAwesomeIcon icon="calculator" class="animate-pulse-gentle" />
                    </div>
                  </div>
                  <p v-if="erros.peso" class="text-red-500 text-sm flex items-center gap-1">
                    <FontAwesomeIcon icon="exclamation-circle" />
                    {{ erros.peso }}
                  </p>
                </div>
              </div>

              <!-- Código SimplesVet -->
              <div class="mt-6">
                <div class="space-y-2">
                  <label class="flex items-center gap-2 text-sm font-semibold text-gray-700">
                    <FontAwesomeIcon icon="id-card" class="text-green-500" />
                    Código SimplesVet
                    <span class="text-xs text-gray-500 bg-gray-100 px-2 py-1 rounded-full">opcional</span>
                  </label>
                  <div class="relative group">
                    <input
                      v-model="formulario.codigoSimplesVet"
                      :class="[
                        'w-full pl-12 pr-4 py-3 border-2 rounded-xl focus:ring-4 focus:ring-green-200 focus:border-green-500 transition-all duration-300 placeholder-gray-400 bg-white group-hover:shadow-md',
                        erros.codigoSimplesVet ? 'border-red-400 focus:border-red-500 focus:ring-red-200' : 'border-gray-200 hover:border-green-300'
                      ]"
                      type="number"
                      placeholder="Ex: 12345"
                    />
                    <div class="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 group-focus-within:text-green-500 transition-colors">
                      <FontAwesomeIcon icon="hashtag" />
                    </div>
                  </div>
                  <p v-if="erros.codigoSimplesVet" class="text-red-500 text-sm flex items-center gap-1">
                    <FontAwesomeIcon icon="exclamation-circle" />
                    {{ erros.codigoSimplesVet }}
                  </p>
                </div>
              </div>
            </div>

            <!-- 👤 Seção: Cliente Responsável -->
            <div class="bg-gradient-to-r from-emerald-50 to-teal-50 rounded-2xl p-6 border border-emerald-100 transform hover:scale-[1.01] transition-all duration-300">
              <div class="flex items-center gap-3 mb-6">
                <div class="w-10 h-10 bg-gradient-to-r from-emerald-500 to-teal-500 rounded-xl flex items-center justify-center">
                  <FontAwesomeIcon icon="users" class="text-white" />
                </div>
                <h2 class="text-xl font-bold bg-gradient-to-r from-emerald-600 to-teal-600 bg-clip-text text-transparent">
                  Cliente Responsável
                </h2>
              </div>

              <!-- Estado de carregamento de clientes -->
              <div v-if="carregandoClientes" class="flex flex-col items-center justify-center py-12 space-y-4">
                <div class="relative">
                  <div class="w-16 h-16 border-4 border-emerald-200 rounded-full animate-spin"></div>
                  <div class="absolute top-0 left-0 w-16 h-16 border-4 border-transparent border-t-emerald-500 rounded-full animate-spin"></div>
                  <FontAwesomeIcon icon="users" class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-emerald-500 animate-pulse" />
                </div>
                <p class="text-emerald-700 font-medium">Carregando clientes disponíveis...</p>
                <div class="flex space-x-1">
                  <div class="w-2 h-2 bg-emerald-400 rounded-full animate-bounce" style="animation-delay: 0ms"></div>
                  <div class="w-2 h-2 bg-emerald-400 rounded-full animate-bounce" style="animation-delay: 150ms"></div>
                  <div class="w-2 h-2 bg-emerald-400 rounded-full animate-bounce" style="animation-delay: 300ms"></div>
                </div>
              </div>

              <!-- Erro ao carregar clientes -->
              <div v-else-if="erroClientes" class="bg-gradient-to-r from-red-50 to-pink-50 border-2 border-red-200 rounded-xl p-6">
                <div class="flex items-center gap-3 mb-4">
                  <div class="w-10 h-10 bg-red-100 rounded-full flex items-center justify-center">
                    <FontAwesomeIcon icon="exclamation-triangle" class="text-red-500 animate-pulse" />
                  </div>
                  <div>
                    <h3 class="font-semibold text-red-800">Ops! Algo deu errado</h3>
                    <p class="text-red-600 text-sm">{{ erroClientes }}</p>
                  </div>
                </div>
                <button
                  @click="carregarClientes"
                  class="flex items-center gap-2 px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors transform hover:scale-105"
                >
                  <FontAwesomeIcon icon="refresh" class="animate-spin" />
                  Tentar novamente
                </button>
              </div>

              <!-- Seletor de cliente -->
              <div v-else class="space-y-3">
                <label class="flex items-center gap-2 text-sm font-semibold text-gray-700">
                  <FontAwesomeIcon icon="user-check" class="text-emerald-500" />
                  Cliente Responsável *
                </label>

                <!-- SearchSelect Component -->
                <div class="search-select-emerald">
                  <SearchSelect
                    v-model="formulario.clienteId"
                    :options="clientesFormatados"
                    :loading="carregandoClientes"
                    placeholder="🔍 Digite o nome ou CPF do cliente..."
                    value-key="id"
                    label-key="nomeCompleto"
                    description-key="cpfFormatado"
                    :min-search-length="1"
                    @select="selecionarCliente"
                    :class="[
                      erros.clienteId ? 'border-red-400' : ''
                    ]"
                  />
                </div>

                <p v-if="erros.clienteId" class="text-red-500 text-sm flex items-center gap-1">
                  <FontAwesomeIcon icon="exclamation-circle" />
                  {{ erros.clienteId }}
                </p>

                <div class="flex items-center gap-2 text-sm text-emerald-600 bg-emerald-50 px-3 py-2 rounded-lg">
                  <FontAwesomeIcon icon="info-circle" />
                  <span>{{ clientes.length }} cliente{{ clientes.length !== 1 ? 's' : '' }} disponível{{ clientes.length !== 1 ? 'is' : '' }}</span>
                </div>
              </div>
            </div>

            <!-- 🚀 Botões de ação elegantes -->
            <div class="flex flex-col sm:flex-row gap-4 pt-8">
              <button
                type="submit"
                :disabled="salvando || carregandoClientes"
                class="group relative flex-1 sm:flex-none px-8 py-4 bg-gradient-to-r from-blue-500 to-blue-600 text-white font-bold rounded-2xl transform hover:-translate-y-1 hover:shadow-2xl transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none overflow-hidden"
              >
                <!-- Background animation -->
                <div class="absolute inset-0 bg-gradient-to-r from-blue-600 to-blue-700 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>

                <!-- Button content -->
                <div class="relative flex items-center justify-center gap-3">
                  <FontAwesomeIcon
                    :icon="salvando ? 'spinner' : 'heart'"
                    :class="{ 'animate-spin': salvando, 'animate-pulse': !salvando }"
                  />
                  <span>{{ salvando ? 'Salvando amiguinho...' : 'Salvar Animal' }}</span>
                  <FontAwesomeIcon v-if="!salvando" icon="star" class="animate-twinkle" />
                </div>

                <!-- Shine effect -->
                <div class="absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white to-transparent opacity-20 group-hover:translate-x-full transition-transform duration-1000 ease-in-out"></div>
              </button>

              <button
                type="button"
                @click="$router.push('/animais')"
                :disabled="salvando"
                class="group px-6 py-4 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold rounded-2xl border-2 border-gray-200 hover:border-gray-300 transform hover:-translate-y-1 hover:shadow-lg transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
              >
                <div class="flex items-center justify-center gap-2">
                  <FontAwesomeIcon icon="arrow-left" class="group-hover:-translate-x-1 transition-transform duration-300" />
                  <span>Cancelar</span>
                </div>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 🎉 Modal de sucesso super bonitinho -->
    <BaseModal v-model="mostrarSucesso" size="lg">
      <div class="text-center p-8 bg-gradient-to-br from-green-50 to-emerald-50">
        <!-- Celebração animada -->
        <div class="relative mb-8">
          <div class="w-24 h-24 bg-gradient-to-r from-green-400 to-emerald-500 rounded-full flex items-center justify-center mx-auto shadow-2xl animate-bounce-gentle">
            <FontAwesomeIcon icon="check" class="text-4xl text-white animate-pulse" />
          </div>

          <!-- Confete animado -->
          <div class="absolute -inset-4 opacity-70">
            <div class="absolute top-0 left-4 w-3 h-3 bg-yellow-400 rounded-full animate-bounce" style="animation-delay: 0.1s"></div>
            <div class="absolute top-2 right-6 w-2 h-2 bg-pink-400 rounded-full animate-bounce" style="animation-delay: 0.3s"></div>
            <div class="absolute bottom-2 left-8 w-2 h-2 bg-blue-400 rounded-full animate-bounce" style="animation-delay: 0.5s"></div>
            <div class="absolute bottom-0 right-4 w-3 h-3 bg-purple-400 rounded-full animate-bounce" style="animation-delay: 0.7s"></div>
          </div>
        </div>

        <div class="space-y-4 mb-8">
          <h3 class="text-3xl font-bold bg-gradient-to-r from-green-600 to-emerald-600 bg-clip-text text-transparent">
            🎉 Sucesso! 🎉
          </h3>
          <p class="text-xl text-gray-700 font-semibold">
            {{ animalCriado?.nome }} foi cadastrado com amor!
          </p>
          <div class="bg-white bg-opacity-60 rounded-2xl p-4 inline-block backdrop-blur-sm border border-green-200">
            <p class="text-green-700 flex items-center gap-2">
              <FontAwesomeIcon icon="id-card" class="text-green-500" />
              <span class="font-medium">ID do sistema:</span>
              <span class="font-bold text-lg">{{ animalCriado?.codigoAnimalSistema }}</span>
            </p>
          </div>
        </div>

        <!-- Botões estilosos -->
        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <button
            @click="adicionarOutroAnimal"
            class="group flex items-center justify-center gap-3 px-8 py-4 bg-gradient-to-r from-blue-500 to-blue-600 text-white font-bold rounded-2xl transform hover:-translate-y-1 hover:shadow-xl transition-all duration-300 hover:scale-105"
          >
            <FontAwesomeIcon icon="plus" class="group-hover:rotate-90 transition-transform duration-300" />
            <span>Adicionar outro amiguinho</span>
            <FontAwesomeIcon icon="star" class="animate-twinkle" />
          </button>

          <button
            @click="voltarParaLista"
            class="group flex items-center justify-center gap-2 px-6 py-4 bg-white bg-opacity-80 text-gray-700 font-semibold rounded-2xl border-2 border-gray-200 hover:border-gray-300 transform hover:-translate-y-1 hover:shadow-lg transition-all duration-300 backdrop-blur-sm"
          >
            <FontAwesomeIcon icon="list" class="group-hover:scale-110 transition-transform duration-300" />
            <span>Ver todos os animais</span>
          </button>
        </div>
      </div>
    </BaseModal>
  </div>

</template>

<script setup lang="ts">
// 📚 Imports
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseButton from '@/components/UI/BaseButton.vue'
import BaseModal from '@/components/UI/BaseModal.vue'
import SearchSelect from '@/components/UI/SearchSelect.vue'
import { animaisService, clientesService } from '@/services/api'
import { TIPOS_ANIMAIS } from '@/types/api'
import type { Cliente, Animal } from '@/types/api'
import { formatarCpf, getIconeTipoAnimal } from '@/utils/formatters'

// 🎯 Configurações
const router = useRouter()

// 📊 Estado do componente
const salvando = ref(false)
const carregandoClientes = ref(false)
const erroClientes = ref<string | null>(null)
const clientes = ref<Cliente[]>([])
const mostrarSucesso = ref(false)
const animalCriado = ref<Animal | null>(null)

// 📝 Formulário
const formulario = reactive({
  nome: '',
  tipo: '',
  raca: '',
  peso: '',
  codigoSimplesVet: '',
  clienteId: ''
})

// 🔍 Clientes com CPF formatado para o SearchSelect
const clientesFormatados = computed(() => {
  return clientes.value.map(cliente => ({
    ...cliente,
    cpfFormatado: `CPF: ${formatarCpf(cliente.cpf)} • ID: ${cliente.codigoClienteSistema}`
  }))
})

// ❌ Erros de validação
const erros = reactive({
  nome: '',
  tipo: '',
  raca: '',
  peso: '',
  codigoSimplesVet: '',
  clienteId: ''
})

// 🛠️ Métodos
const carregarClientes = async (): Promise<void> => {
  try {
    console.log('🔄 Carregando clientes...')
    carregandoClientes.value = true
    erroClientes.value = null
    const response = await clientesService.buscarTodos()
    clientes.value = response
    console.log(`✅ ${response.length} clientes carregados!`)
  } catch (err) {
    console.error('❌ Erro ao carregar clientes:', err)
    erroClientes.value = err instanceof Error ? err.message : 'Erro ao carregar clientes'
  } finally {
    carregandoClientes.value = false
  }
}

const limparErros = (): void => {
  Object.keys(erros).forEach(key => {
    erros[key as keyof typeof erros] = ''
  })
}

const validarFormulario = (): boolean => {
  limparErros()
  let valido = true

  // Nome é obrigatório
  if (!formulario.nome.trim()) {
    erros.nome = 'Nome do animal é obrigatório'
    valido = false
  } else if (formulario.nome.length < 2) {
    erros.nome = 'Nome deve ter pelo menos 2 caracteres'
    valido = false
  }

  // Tipo é obrigatório
  if (!formulario.tipo) {
    erros.tipo = 'Tipo do animal é obrigatório'
    valido = false
  }

  // Cliente é obrigatório
  if (!formulario.clienteId) {
    erros.clienteId = 'Cliente responsável é obrigatório'
    valido = false
  }

  // Raça (opcional, mas se preenchida deve ter pelo menos 2 caracteres)
  if (formulario.raca && formulario.raca.length < 2) {
    erros.raca = 'Raça deve ter pelo menos 2 caracteres'
    valido = false
  }

  // Peso (opcional, mas se preenchido deve ser válido)
  if (formulario.peso) {
    const peso = parseFloat(formulario.peso)
    if (isNaN(peso) || peso <= 0 || peso > 999.99) {
      erros.peso = 'Peso deve ser um número positivo até 999.99 kg'
      valido = false
    }
  }

  // Código SimplesVet (se preenchido, deve ser válido)
  if (formulario.codigoSimplesVet) {
    const codigo = parseInt(formulario.codigoSimplesVet)
    if (isNaN(codigo) || codigo <= 0) {
      erros.codigoSimplesVet = 'Código deve ser um número positivo'
      valido = false
    }
  }

  return valido
}

const salvarAnimal = async (): Promise<void> => {
  if (!validarFormulario()) {
    console.log('❌ Formulário inválido')
    return
  }

  try {
    console.log('💾 Salvando animal...')
    salvando.value = true

    const novoAnimal = {
      nome: formulario.nome.trim(),
      tipo: formulario.tipo,
      raca: formulario.raca.trim() || null,
      peso: formulario.peso ? parseFloat(formulario.peso) : null,
      codigoSimplesVet: formulario.codigoSimplesVet ? parseInt(formulario.codigoSimplesVet) : 0,
      clienteId: parseInt(formulario.clienteId)
    }

    const animalSalvo = await animaisService.criar(novoAnimal)

    console.log('✅ Animal salvo com sucesso!')
    animalCriado.value = animalSalvo
    mostrarSucesso.value = true

  } catch (err) {
    console.error('❌ Erro ao salvar animal:', err)
    alert(err instanceof Error ? err.message : 'Erro ao salvar animal')
  } finally {
    salvando.value = false
  }
}

const adicionarOutroAnimal = (): void => {
  // Limpa o formulário para adicionar outro animal
  Object.keys(formulario).forEach(key => {
    formulario[key as keyof typeof formulario] = ''
  })

  limparErros()
  mostrarSucesso.value = false
  animalCriado.value = null
}

const voltarParaLista = (): void => {
  router.push('/animais')
}

// 🎯 Funções do SearchSelect
const selecionarCliente = (cliente: any): void => {
  console.log('✅ Cliente selecionado:', cliente)

  // Limpa erro se havia
  if (erros.clienteId) {
    erros.clienteId = ''
  }
}

// 🎨 Utilitários visuais movidos para @/utils/formatters

// 🎬 Lifecycle
onMounted(() => {
  console.log('🎬 Página Novo Animal carregada!')
  carregarClientes()
})
</script>

<style scoped>
/* 🎨 Animações personalizadas */
@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slide-up {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes bounce-gentle {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

@keyframes pulse-gentle {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
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

.animate-fade-in-up {
  animation: fade-in-up 0.6s ease-out;
}

.animate-slide-up {
  animation: slide-up 0.8s ease-out;
}

.animate-bounce-gentle {
  animation: bounce-gentle 2s ease-in-out infinite;
}

.animate-pulse-gentle {
  animation: pulse-gentle 2s ease-in-out infinite;
}

.animate-twinkle {
  animation: twinkle 1.5s ease-in-out infinite;
}

/* 🌊 Wave background pattern */
.bg-pattern {
  background-image: radial-gradient(circle at 25% 25%, rgba(255, 255, 255, 0.1) 1px, transparent 1px),
                    radial-gradient(circle at 75% 75%, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 30px 30px;
}

/* 🎭 Glassmorphism effect */
.glass {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 💫 Hover effects */
.hover-lift {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.hover-lift:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

/* 🌈 Custom scrollbar */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(45deg, #3b82f6, #8b5cf6);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(45deg, #2563eb, #7c3aed);
}

/* 🎨 SearchSelect com tema Emerald */
.search-select-emerald input {
  border-color: #e5e7eb !important; /* border-gray-200 */
  padding-left: 1rem !important; /* Remove o padding extra para ícones */
  transition: all 0.3s ease !important;
}

.search-select-emerald input:hover {
  border-color: #a7f3d0 !important; /* border-emerald-300 */
}

.search-select-emerald input:focus {
  border-color: #10b981 !important; /* border-emerald-500 */
  box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.1) !important; /* focus:ring-emerald-100 */
  outline: none !important;
}
</style>
