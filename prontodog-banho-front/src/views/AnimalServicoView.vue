<template>
  <div class="min-h-screen bg-gradient-to-br from-amber-50 via-white to-green-50 p-4 pb-20">
    <!-- 🎨 Background decorativo -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none z-0">
      <!-- Círculos decorativos -->
      <div class="absolute top-20 left-10 w-72 h-72 bg-amber-200/20 rounded-full blur-3xl"></div>
      <div class="absolute bottom-20 right-10 w-96 h-96 bg-green-200/20 rounded-full blur-3xl"></div>
      <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-gradient-to-r from-amber-100/10 to-green-100/10 rounded-full blur-3xl"></div>
    </div>

    <div class="relative z-20 max-w-7xl mx-auto pt-8">
      <!-- 📋 Header -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-amber-400 to-green-500 rounded-2xl mb-6 shadow-2xl">
          <FontAwesomeIcon :icon="['fas', 'clipboard-list']" class="text-white text-3xl" />
        </div>
        <h1 class="text-4xl font-bold text-gray-800 mb-3">Animal Serviços Registrados</h1>
        <p class="text-gray-600 max-w-3xl mx-auto text-lg">
          Gerencie os serviços registrados para cada animal. Visualize pacotes, banhos utilizados e adicione novos banhos individuais.
        </p>
      </div>

      <!-- 🔄 Estado de Loading -->
      <div v-if="loading && !animalServicos.length" class="text-center py-12">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-r from-amber-400 to-green-500 rounded-full mb-4 animate-spin">
          <FontAwesomeIcon :icon="['fas', 'spinner']" class="text-white text-2xl" />
        </div>
        <p class="text-gray-600 text-lg">Carregando animal serviços...</p>
      </div>

      <!-- ❌ Estado de Erro -->
      <div v-else-if="error" class="text-center py-12">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-red-500 rounded-full mb-4">
          <FontAwesomeIcon :icon="['fas', 'exclamation-triangle']" class="text-white text-2xl" />
        </div>
        <h3 class="text-xl font-semibold text-gray-800 mb-2">Erro ao carregar</h3>
        <p class="text-gray-600 mb-4">{{ error }}</p>
        <BaseButton @click="carregarAnimalServicos" variant="primary">
          <FontAwesomeIcon :icon="['fas', 'refresh']" class="mr-2" />
          Tentar Novamente
        </BaseButton>
      </div>

      <!-- 📭 Estado Vazio -->
      <div v-else-if="!animalServicos.length && !loading" class="text-center py-12">
        <div class="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-r from-gray-400 to-gray-500 rounded-2xl mb-6 shadow-lg">
          <FontAwesomeIcon :icon="['fas', 'clipboard-list']" class="text-white text-3xl" />
        </div>
        <h3 class="text-2xl font-bold text-gray-800 mb-3">Nenhum serviço registrado</h3>
        <p class="text-gray-600 mb-6 max-w-md mx-auto">
          Ainda não há animal serviços cadastrados no sistema. Cadastre o primeiro serviço para começar!
        </p>
        <BaseButton @click="$router.push('/animal-servico/novo')" variant="primary" class="px-8 py-3">
          <FontAwesomeIcon :icon="['fas', 'plus-circle']" class="mr-2" />
          Cadastrar Primeiro Serviço
        </BaseButton>
      </div>

      <!-- ✅ Lista de Animal Serviços -->
      <div v-else>
        <!-- 🔍 Filtros -->
        <BaseCard class="mb-6 shadow-lg border-0 bg-white/80 backdrop-blur-sm">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <BaseInput
              v-model="filtroTexto"
              type="search"
              placeholder="Buscar por animal, serviço ou vendedor..."
              class="text-lg"
            >
              <template #icon>
                <FontAwesomeIcon :icon="['fas', 'search']" class="text-gray-400" />
              </template>
            </BaseInput>

            <div class="flex gap-4">
              <BaseButton @click="carregarAnimalServicos" variant="ghost" class="flex-shrink-0">
                <FontAwesomeIcon :icon="['fas', 'refresh']" class="mr-2" />
                Atualizar
              </BaseButton>

              <BaseButton @click="$router.push('/animal-servico/novo')" variant="primary" class="flex-1">
                <FontAwesomeIcon :icon="['fas', 'plus-circle']" class="mr-2" />
                Novo Registro
              </BaseButton>
            </div>
          </div>
        </BaseCard>

        <!-- 📊 Contador de itens e status da paginação -->
        <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
          <div class="flex items-center gap-4">
            <div class="bg-white px-4 py-2 rounded-lg shadow-sm border border-gray-200">
              <div class="flex items-center gap-2 text-sm text-gray-600">
                <FontAwesomeIcon :icon="['fas', 'eye']" class="text-blue-500" />
                <span>Exibindo <strong class="text-blue-600">{{ animalServicosFiltrados.length }}</strong> de <strong class="text-gray-800">{{ totalItensDisponiveis }}</strong></span>
              </div>
            </div>
            <div v-if="filtroTexto" class="bg-amber-100 px-3 py-2 rounded-lg border border-amber-200">
              <div class="flex items-center gap-2 text-sm text-amber-700">
                <FontAwesomeIcon :icon="['fas', 'filter']" />
                <span>Filtro ativo</span>
              </div>
            </div>
          </div>
          <div v-if="animalServicos.length > 10" class="bg-green-50 px-3 py-2 rounded-lg border border-green-200">
            <div class="flex items-center gap-2 text-sm text-green-700">
              <FontAwesomeIcon :icon="['fas', 'info-circle']" />
              <span>Carregamento paginado (10 por vez)</span>
            </div>
          </div>
        </div>

        <!-- 📊 Lista elegante de animal serviços -->
        <div ref="listaAnimalServicosRef" class="space-y-4">
          <div
            v-for="(animalServico, index) in animalServicosFiltrados"
            :key="animalServico.id"
            class="group relative bg-gradient-to-r from-white via-white to-amber-50 rounded-xl shadow-lg hover:shadow-2xl transform transition-all duration-300 hover:-translate-y-1 animate-fade-in-up overflow-hidden"
            :style="{ animationDelay: `${index * 100}ms` }"
          >
            <div class="p-6">
              <div class="flex items-center justify-between">
                <!-- 🐕 Informações principais -->
                <div class="flex items-center gap-4 flex-1">
                  <!-- Avatar com gradiente -->
                  <div class="relative">
                    <div class="w-16 h-16 bg-gradient-to-br from-amber-400 to-green-600 rounded-2xl flex items-center justify-center shadow-lg transform group-hover:scale-110 transition-all duration-300">
                      <FontAwesomeIcon :icon="['fas', 'clipboard-list']" class="text-2xl text-white" />
                    </div>
                    <!-- Badge de banhos usados -->
                    <div class="absolute -top-1 -right-1 w-8 h-8 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-full flex items-center justify-center shadow-md">
                      <span class="text-xs font-bold text-white">{{ animalServico.banhosUsados }}</span>
                    </div>
                  </div>

                  <!-- Informações do serviço -->
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-3 mb-2">
                      <h3 class="text-xl font-bold text-gray-900 truncate group-hover:text-amber-700 transition-colors duration-300">
                        {{ getAnimalNome(animalServico) }}
                      </h3>
                      <BaseBadge
                        :variant="animalServico.banhosUsados > 0 ? 'success' : 'warning'"
                        size="sm"
                      >
                        {{ getServicoDescricao(animalServico) }}
                      </BaseBadge>
                    </div>

                    <div class="flex items-center gap-4 text-sm text-gray-600 mb-2">
                      <span class="flex items-center gap-1">
                        <FontAwesomeIcon :icon="['fas', 'calendar-alt']" class="text-gray-400" />
                        {{ formatarData(animalServico.dataServico) }}
                      </span>
                      <span class="flex items-center gap-1">
                        <FontAwesomeIcon :icon="['fas', 'user']" class="text-gray-400" />
                        {{ getUsuarioNome(animalServico) }}
                      </span>
                    </div>

                    <!-- Progress bar de banhos -->
                    <div class="flex items-center gap-3">
                      <div class="flex-1 bg-gray-200 rounded-full h-2">
                        <div
                          class="bg-gradient-to-r from-amber-400 to-green-500 h-2 rounded-full transition-all duration-500"
                          :style="{ width: `${getProgressoBanhos(animalServico)}%` }"
                        ></div>
                      </div>
                      <span class="text-sm font-medium text-gray-600">
                        {{ animalServico.banhosUsados }}/{{ getTotalBanhos(animalServico) }}
                      </span>
                    </div>
                  </div>
                </div>

                <!-- 🎯 Status e Ícone Clicável -->
                <div class="flex items-center gap-3 ml-4">
                  <!-- Status badge -->
                  <BaseBadge
                    :variant="isServicoCompleto(animalServico) ? 'success' : 'warning'"
                    size="md"
                  >
                    {{ isServicoCompleto(animalServico) ? 'Completo' : 'Em Andamento' }}
                  </BaseBadge>

                  <!-- Ações -->
                  <div class="flex items-center gap-3">
                    <!-- Ver detalhes -->
                    <button
                      @click.stop="verDetalhesAnimalServico(animalServico)"
                      class="group/btn flex items-center gap-2 px-3 py-2 text-blue-600 hover:text-blue-700 hover:bg-blue-50 rounded-lg transition-all duration-200 transform hover:scale-105"
                      title="Ver detalhes do animal serviço"
                    >
                      <FontAwesomeIcon :icon="['fas', 'eye']" class="text-sm" />
                      <span class="text-xs font-medium hidden sm:inline">Ver</span>
                    </button>

                    <!-- Excluir -->
                    <button
                      @click.stop="confirmarExclusao(animalServico)"
                      class="group/btn flex items-center gap-2 px-3 py-2 text-red-600 hover:text-red-700 hover:bg-red-50 rounded-lg transition-all duration-200 transform hover:scale-105"
                      title="Excluir animal serviço"
                    >
                      <FontAwesomeIcon :icon="['fas', 'trash']" class="text-sm" />
                      <span class="text-xs font-medium hidden sm:inline">Excluir</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 📄 Botão "Carregar Mais" com informações detalhadas -->
        <div v-if="temMaisItens" class="text-center mt-8">
          <div class="bg-white rounded-xl p-6 shadow-lg border border-gray-100 max-w-md mx-auto">
            <div class="mb-4">
              <p class="text-gray-600 text-sm mb-2">
                Mostrando <span class="font-semibold text-blue-600">{{ animalServicosFiltrados.length }}</span>
                de <span class="font-semibold text-gray-800">{{ totalItensDisponiveis }}</span> registros
              </p>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div
                  class="bg-gradient-to-r from-amber-400 to-green-500 h-2 rounded-full transition-all duration-500"
                  :style="{ width: `${(animalServicosFiltrados.length / totalItensDisponiveis) * 100}%` }"
                ></div>
              </div>
            </div>
            <BaseButton
              @click="carregarMaisItens"
              variant="primary"
              class="px-8 py-3 text-lg w-full"
              :disabled="loading"
            >
              <FontAwesomeIcon
                v-if="loading"
                :icon="['fas', 'spinner']"
                class="mr-2 animate-spin"
              />
              <FontAwesomeIcon
                v-else
                :icon="['fas', 'plus-circle']"
                class="mr-2"
              />
              {{ loading ? 'Carregando...' : `Carregar mais ${proximosItens} ${proximosItens === 1 ? 'registro' : 'registros'}` }}
            </BaseButton>
          </div>
        </div>
      </div>
    </div>

    <!-- 🛁 Modal para adicionar banho individual -->
    <BaseModal v-model="mostrarModalBanho" size="md" title="Adicionar Banho Individual">
      <div v-if="animalServicoSelecionado" class="space-y-6">
        <!-- Informações do serviço -->
        <div class="bg-gradient-to-r from-amber-50 to-green-50 rounded-xl p-4 border border-amber-200">
          <h4 class="font-semibold text-gray-800 mb-2">Serviço Selecionado</h4>
          <div class="space-y-1 text-sm">
            <p><strong>Animal:</strong> {{ getAnimalNome(animalServicoSelecionado) }}</p>
            <p><strong>Serviço:</strong> {{ getServicoNome(animalServicoSelecionado) }}</p>
            <p><strong>Banhos usados:</strong> {{ animalServicoSelecionado.banhosUsados }}/{{ getTotalBanhos(animalServicoSelecionado) }}</p>
          </div>
        </div>

        <!-- Formulário do banho -->
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <FontAwesomeIcon :icon="['fas', 'calendar-alt']" class="mr-2 text-blue-600" />
              Data do Banho *
            </label>
            <input
              v-model="formularioBanho.dataBanho"
              type="date"
              required
              class="w-full px-4 py-3 bg-white border-2 border-gray-200 rounded-xl focus:border-blue-500 focus:ring-4 focus:ring-blue-100 transition-all duration-300 text-lg font-medium hover:border-gray-300"
            />
          </div>

          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <FontAwesomeIcon :icon="['fas', 'align-left']" class="mr-2 text-green-600" />
              Observações
            </label>
            <textarea
              v-model="formularioBanho.observacoes"
              placeholder="Observações sobre este banho (opcional)..."
              rows="3"
              class="w-full px-4 py-3 bg-white border-2 border-gray-200 rounded-xl focus:border-green-500 focus:ring-4 focus:ring-green-100 transition-all duration-300 resize-none hover:border-gray-300"
            ></textarea>
          </div>
        </div>

        <!-- Botões do modal -->
        <div class="flex flex-col sm:flex-row gap-3 pt-4 border-t">
          <BaseButton
            @click="mostrarModalBanho = false"
            variant="ghost"
            class="flex-1"
            :disabled="salvandoBanho"
          >
            Cancelar
          </BaseButton>

          <BaseButton
            @click="salvarBanhoIndividual"
            variant="primary"
            class="flex-1"
            :disabled="salvandoBanho || !formularioBanho.dataBanho"
          >
            <FontAwesomeIcon
              v-if="salvandoBanho"
              :icon="['fas', 'spinner']"
              class="mr-2 animate-spin"
            />
            <FontAwesomeIcon
              v-else
              :icon="['fas', 'plus-circle']"
              class="mr-2"
            />
            {{ salvandoBanho ? 'Salvando...' : 'Registrar Banho' }}
          </BaseButton>
        </div>
      </div>
    </BaseModal>

    <!-- 🌊 Wave Footer Decoration -->
    <div class="fixed bottom-0 left-0 right-0 w-full overflow-hidden z-10 pointer-events-none">
      <svg
        viewBox="0 0 1440 120"
        class="w-full h-20 md:h-24"
        preserveAspectRatio="none"
      >
        <defs>
          <linearGradient id="animalServicoWave" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" style="stop-color:#F59E0B;stop-opacity:0.9" />
            <stop offset="25%" style="stop-color:#FBBF24;stop-opacity:0.8" />
            <stop offset="50%" style="stop-color:#10B981;stop-opacity:0.8" />
            <stop offset="75%" style="stop-color:#059669;stop-opacity:0.9" />
            <stop offset="100%" style="stop-color:#047857;stop-opacity:1" />
          </linearGradient>
          <filter id="waveShadow" x="-20%" y="-20%" width="140%" height="140%">
            <feDropShadow dx="0" dy="-4" stdDeviation="8" flood-color="#000000" flood-opacity="0.15"/>
          </filter>
        </defs>

        <!-- Wave principal -->
        <path
          d="M0,60 C120,45 240,30 360,45 C480,60 600,75 720,60 C840,45 960,30 1080,45 C1200,60 1320,75 1440,60 L1440,120 L0,120 Z"
          fill="url(#animalServicoWave)"
          filter="url(#waveShadow)"
        />

        <!-- Wave secundária para profundidade -->
        <path
          d="M0,80 C150,65 300,50 450,65 C600,80 750,95 900,80 C1050,65 1200,50 1350,65 C1400,70 1420,75 1440,80 L1440,120 L0,120 Z"
          fill="url(#animalServicoWave)"
          opacity="0.6"
        />
      </svg>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseButton from '@/components/UI/BaseButton.vue'
import BaseInput from '@/components/UI/BaseInput.vue'
import BaseModal from '@/components/UI/BaseModal.vue'
import BaseBadge from '@/components/UI/BaseBadge.vue'
import { animalServicoService, animaisService, servicosService, usuariosService, banhosIndividuaisService, type NovoBanhoIndividual } from '@/services/api'
import type { AnimalServico, Animal, ServicoCompleto, Usuario } from '@/types/api'

const router = useRouter()

// 📊 Estados reativas
const loading = ref(false)
const error = ref('')
const animalServicos = ref<AnimalServico[]>([])
const animais = ref<Animal[]>([])
const servicos = ref<ServicoCompleto[]>([])
const usuarios = ref<Usuario[]>([])

// 📋 Estados do modal de banho
const mostrarModalBanho = ref(false)
const salvandoBanho = ref(false)
const animalServicoSelecionado = ref<AnimalServico | null>(null)
const formularioBanho = ref({
  dataBanho: '',
  observacoes: ''
})

// 🔍 Filtros
const filtroTexto = ref('')
const itensPorPagina = ref(10)
const itensExibidos = ref(10)

// 📄 Lista com filtros aplicados
const animalServicosFiltrados = computed(() => {
  return animalServicos.value.filter(animalServico => {
    if (!filtroTexto.value) return true

    const termo = filtroTexto.value.toLowerCase()
    const animalNome = getAnimalNome(animalServico).toLowerCase()
    const servicoNome = getServicoNome(animalServico).toLowerCase()
    const usuarioNome = getUsuarioNome(animalServico).toLowerCase()

    return animalNome.includes(termo) ||
           servicoNome.includes(termo) ||
           usuarioNome.includes(termo)
  }).slice(0, itensExibidos.value)
})

// 📊 Controle de paginação
const totalItensDisponiveis = computed(() => {
  return animalServicos.value.filter(animalServico => {
    if (!filtroTexto.value) return true

    const termo = filtroTexto.value.toLowerCase()
    const animalNome = getAnimalNome(animalServico).toLowerCase()
    const servicoNome = getServicoNome(animalServico).toLowerCase()
    const usuarioNome = getUsuarioNome(animalServico).toLowerCase()

    return animalNome.includes(termo) ||
           servicoNome.includes(termo) ||
           usuarioNome.includes(termo)
  }).length
})

const temMaisItens = computed(() => {
  return totalItensDisponiveis.value > itensExibidos.value
})

const proximosItens = computed(() => {
  const restantes = totalItensDisponiveis.value - itensExibidos.value
  return Math.min(restantes, itensPorPagina.value)
})

// 🔧 Funções auxiliares - Busca reversa devido ao @JsonBackReference
const getAnimalNome = (animalServico: AnimalServico): string => {
  // Busca reversa: procura o animal que tem este animalServico
  const animal = animais.value.find(a =>
    a.servicos?.some(servico => servico.id === animalServico.id)
  )
  return animal?.nome || `Animal (ID: ${animalServico.id})`
}

const getAnimalCompleto = (animalServico: AnimalServico): Animal | null => {
  // Busca reversa: procura o animal que tem este animalServico
  return animais.value.find(a =>
    a.servicos?.some(servico => servico.id === animalServico.id)
  ) || null
}

const getServicoNome = (animalServico: AnimalServico): string => {
  // Busca reversa: procura o serviço que tem este animalServico
  const servico = servicos.value.find(s =>
    s.servicosAnimais?.some(as => as.id === animalServico.id)
  )
  return servico?.nome || `Serviço (ID: ${animalServico.id})`
}

const getServicoCompleto = (animalServico: AnimalServico): ServicoCompleto | null => {
  // Busca reversa: procura o serviço que tem este animalServico
  return servicos.value.find(s =>
    s.servicosAnimais?.some(as => as.id === animalServico.id)
  ) || null
}

const getServicoDescricao = (animalServico: AnimalServico): string => {
  const servico = getServicoCompleto(animalServico)
  if (!servico) return 'N/A'

  return servico.quantidade > 1 ? `Pacote (${servico.quantidade} banhos)` : 'Banho Único'
}

const getUsuarioNome = (animalServico: AnimalServico): string => {
  // Busca reversa: procura o usuário que tem este animalServico
  const usuario = usuarios.value.find(u =>
    u.animalServicos?.some(as => as.id === animalServico.id)
  )
  return usuario?.nome || 'Vendedor'
}

const getTotalBanhos = (animalServico: AnimalServico): number => {
  const servico = getServicoCompleto(animalServico)
  return servico?.quantidade || 1
}

const getProgressoBanhos = (animalServico: AnimalServico): number => {
  const total = getTotalBanhos(animalServico)
  return Math.min((animalServico.banhosUsados / total) * 100, 100)
}

const podeAdicionarBanho = (animalServico: AnimalServico): boolean => {
  const total = getTotalBanhos(animalServico)
  return animalServico.banhosUsados < total
}

const isServicoCompleto = (animalServico: AnimalServico): boolean => {
  const total = getTotalBanhos(animalServico)
  return animalServico.banhosUsados >= total
}

const formatarData = (data: string): string => {
  if (!data) return ''

  try {
    // Parse seguro: evitar timezone UTC
    const [ano, mes, dia] = data.split('-')
    const dataLocal = new Date(Number(ano), Number(mes) - 1, Number(dia))
    return dataLocal.toLocaleDateString('pt-BR')
  } catch (error) {
    console.warn('⚠️ Erro ao formatar data:', data, error)
    return data
  }
}

// 🗑️ Funções de exclusão
const confirmarExclusao = (animalServico: AnimalServico): void => {
  const animalNome = getAnimalNome(animalServico)
  const servicoNome = getServicoDescricao(animalServico)

  const confirmacao = window.confirm(
    `🗑️ Tem certeza que deseja excluir este animal serviço?\n\n` +
    `Animal: ${animalNome}\n` +
    `Serviço: ${servicoNome}\n` +
    `Data: ${formatarData(animalServico.dataServico)}\n` +
    `Banhos utilizados: ${animalServico.banhosUsados}\n\n` +
    `⚠️ ATENÇÃO: Esta ação não poderá ser desfeita!\n` +
    `Todos os banhos individuais relacionados também serão excluídos.`
  )

  if (confirmacao) {
    excluirAnimalServico(animalServico)
  }
}

const excluirAnimalServico = async (animalServico: AnimalServico): Promise<void> => {
  try {
    loading.value = true
    console.log(`🗑️ Excluindo animal serviço ID ${animalServico.id}...`)

    await animalServicoService.excluir(animalServico.id)

    console.log('✅ Animal serviço excluído com sucesso!')

    // Remover da lista local para feedback imediato
    const index = animalServicos.value.findIndex(as => as.id === animalServico.id)
    if (index > -1) {
      animalServicos.value.splice(index, 1)
    }

    // Mostrar feedback de sucesso
    alert(`✅ Animal serviço de "${getAnimalNome(animalServico)}" foi excluído com sucesso!`)

    // Recarregar dados para garantir consistência
    await carregarAnimalServicos()

  } catch (error) {
    console.error('❌ Erro ao excluir animal serviço:', error)
    alert(`❌ Erro ao excluir animal serviço: ${error instanceof Error ? error.message : 'Erro desconhecido'}\n\nTente novamente.`)
  } finally {
    loading.value = false
  }
}

// 📊 Carregar dados
const carregarAnimalServicos = async (): Promise<void> => {
  try {
    loading.value = true
    error.value = ''

    console.log('🔄 Carregando animal serviços e dados relacionados...')
    const [animalServicosData, animaisData, servicosData, usuariosData] = await Promise.all([
      animalServicoService.buscarTodos(),
      animaisService.buscarTodos(),
      servicosService.buscarTodos(),
      usuariosService.buscarTodos()
    ])

    animalServicos.value = animalServicosData
    animais.value = animaisData
    servicos.value = servicosData
    usuarios.value = usuariosData

    console.log(`✅ Dados carregados:`)
    console.log(`  - ${animalServicos.value.length} animal serviços`)
    console.log(`  - ${animais.value.length} animais`)
    console.log(`  - ${servicos.value.length} serviços`)
    console.log(`  - ${usuarios.value.length} usuários`)

    // Debug: mostrar estrutura dos dados
    if (animalServicos.value.length > 0) {
      console.log('🔍 Estrutura do primeiro Animal Serviço:', animalServicos.value[0])
    }
    if (animais.value.length > 0) {
      console.log('🔍 Estrutura do primeiro Animal:', animais.value[0])
    }
  } catch (err) {
    console.error('❌ Erro ao carregar animal serviços:', err)
    error.value = 'Erro ao carregar os dados. Tente novamente.'
  } finally {
    loading.value = false
  }
}

const carregarMaisItens = (): void => {
  itensExibidos.value += itensPorPagina.value
}

// 👁️ Navegação para detalhes
const verDetalhesAnimalServico = (animalServico: AnimalServico): void => {
  console.log('🔍 Navegando para detalhes do Animal Serviço:', animalServico.id)
  router.push(`/animal-servico/${animalServico.id}`)
}

// 🛁 Funções do modal de banho
const abrirModalBanho = (animalServico: AnimalServico): void => {
  animalServicoSelecionado.value = animalServico
  formularioBanho.value = {
    dataBanho: new Date().toISOString().split('T')[0] || '',
    observacoes: ''
  }
  mostrarModalBanho.value = true
}

const salvarBanhoIndividual = async (): Promise<void> => {
  if (!animalServicoSelecionado.value || !formularioBanho.value.dataBanho) return

  try {
    salvandoBanho.value = true

    // 🔧 Estrutura correta que o backend espera
    const dadosBanho = {
      animalServicoId: animalServicoSelecionado.value.id,
      dataBanho: formularioBanho.value.dataBanho,
      observacoes: formularioBanho.value.observacoes || null,
      usuarioId: null // Por enquanto sem usuário específico
    }

    console.log('🛁 Enviando dados do banho:', dadosBanho)
    console.log('🔗 URL da API:', 'http://localhost:8080/banho-individual')
    console.log('🔧 Configuração completa:', JSON.stringify(dadosBanho, null, 2))

    // 🔍 Teste direto do endpoint
    console.log('🧪 Fazendo teste direto do fetch...')
    try {
      const testeResponse = await fetch('http://localhost:8080/banho-individual', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
      })
      console.log('🧪 Teste GET funcionou:', testeResponse.status)
    } catch (fetchError) {
      console.error('🧪 Teste GET falhou:', fetchError)
    }

    const resultado = await banhosIndividuaisService.criar(dadosBanho)
    console.log('✅ Resultado do backend:', resultado)

    // Atualizar o contador de banhos usados localmente
    animalServicoSelecionado.value.banhosUsados = animalServicoSelecionado.value.banhosUsados + 1

    mostrarModalBanho.value = false
    console.log('✅ Banho individual registrado com sucesso!')

  } catch (error) {
    console.error('❌ Erro ao registrar banho:', error)
    alert('Erro ao registrar banho. Tente novamente.')
  } finally {
    salvandoBanho.value = false
  }
}

// 👀 Watchers
// Resetar paginação quando filtro mudar
watch(filtroTexto, () => {
  itensExibidos.value = itensPorPagina.value
})

// 🔄 Lifecycle
onMounted(() => {
  carregarAnimalServicos()
})
</script>

<style scoped>
/* 🎨 Animações personalizadas */
@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in-up {
  animation: fade-in-up 0.6s ease-out forwards;
}

/* 📱 Responsividade */
@media (max-width: 640px) {
  .grid-cols-1 {
    grid-template-columns: 1fr;
  }
}
</style>
