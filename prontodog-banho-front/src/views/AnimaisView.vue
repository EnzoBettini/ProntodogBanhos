<template>
  <div class="p-6 space-y-6">
    <!-- 🎯 Header da página -->
    <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
      <div class="flex items-center gap-3">
        <div class="w-12 h-12 bg-primary-100 rounded-full flex items-center justify-center">
          <FontAwesomeIcon icon="dog" class="text-xl text-primary-600" />
        </div>
        <div>
          <h1 class="text-3xl font-bold text-primary-700">Animais</h1>
          <p class="text-gray-600">
            {{ loading ? 'Carregando...' : `${animaisFiltrados.length} ${animaisFiltrados.length === 1 ? 'animal encontrado' : 'animais encontrados'}` }}
          </p>
        </div>
      </div>

      <div class="flex items-center gap-3">
        <BaseButton
          variant="outline"
          @click="carregarAnimais"
          :disabled="loading"
          title="Atualizar lista"
        >
          <FontAwesomeIcon
            :icon="loading ? 'spinner' : 'refresh'"
            :class="{ 'animate-spin': loading }"
            class="mr-2"
          />
          Atualizar
        </BaseButton>
        <BaseButton variant="primary" @click="$router.push('/animais/novo')">
          <FontAwesomeIcon icon="plus" class="mr-2" />
          Novo Animal
        </BaseButton>
      </div>
    </div>

    <!-- 🔍 Barra de pesquisa -->
    <BaseCard class="p-4">
      <div class="flex flex-col md:flex-row gap-4">
        <div class="flex-1">
          <div class="relative">
            <FontAwesomeIcon
              icon="search"
              class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
            />
            <input
              v-model="filtroNome"
              type="text"
              placeholder="Buscar por nome do animal..."
              class="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors"
            />
          </div>
        </div>
        <div class="md:w-48">
          <select
            v-model="filtroCliente"
            class="w-full px-3 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors"
          >
            <option value="">Todos os clientes</option>
            <option v-for="cliente in clientesUnicos" :key="cliente" :value="cliente">
              {{ cliente }}
            </option>
          </select>
        </div>
        <div class="md:w-40">
          <select
            v-model="filtroTipo"
            class="w-full px-3 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors"
          >
            <option value="">Todos os tipos</option>
            <option v-for="tipo in tiposUnicos" :key="tipo" :value="tipo">
              {{ tipo }}
            </option>
          </select>
        </div>
      </div>
    </BaseCard>

    <!-- 💾 Estado de carregamento -->
    <div v-if="loading" class="flex justify-center py-12">
      <div class="text-center">
        <FontAwesomeIcon icon="spinner" class="text-4xl text-primary-500 animate-spin mb-4" />
        <p class="text-gray-600">Carregando animais...</p>
      </div>
    </div>

    <!-- ❌ Estado de erro -->
    <BaseCard v-else-if="error" class="p-8 text-center border-red-200 bg-red-50">
      <FontAwesomeIcon icon="exclamation-triangle" class="text-4xl text-red-500 mb-4" />
      <h3 class="text-lg font-semibold text-red-700 mb-2">Erro ao carregar animais</h3>
      <p class="text-red-600 mb-4">{{ error }}</p>
      <BaseButton variant="outline" @click="carregarAnimais">
        <FontAwesomeIcon icon="refresh" class="mr-2" />
        Tentar novamente
      </BaseButton>
    </BaseCard>

    <!-- 📋 Tabela de animais -->
    <BaseCard v-else-if="animais.length > 0" class="overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50 border-b border-gray-200">
            <tr>
              <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Animal
              </th>
              <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Tipo
              </th>
              <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Código SimplesVet
              </th>
              <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Serviços
              </th>
              <th class="px-6 py-4 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                Ações
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr
              v-for="animal in animaisFiltrados"
              :key="animal.id"
              class="hover:bg-gray-50 transition-colors"
            >
              <!-- Nome do Animal -->
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="w-10 h-10 bg-primary-100 rounded-full flex items-center justify-center mr-3">
                    <FontAwesomeIcon icon="dog" class="text-primary-600" />
                  </div>
                  <div>
                    <div class="text-sm font-medium text-gray-900">{{ animal.nome }}</div>
                    <div class="text-sm text-gray-500">ID: {{ animal.codigoAnimalSistema }}</div>
                  </div>
                </div>
              </td>

              <!-- Tipo -->
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                  :class="getCorTipo(animal.tipo)"
                >
                  {{ animal.tipo }}
                </span>
              </td>

              <!-- Código SimplesVet -->
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ animal.codigoSimplesVet || 'N/A' }}</div>
              </td>

              <!-- Serviços -->
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <FontAwesomeIcon icon="calendar-days" class="text-gray-400 mr-2" />
                  <span class="text-sm text-gray-900">
                    {{ animal.servicos?.length || 0 }} serviço{{ (animal.servicos?.length || 0) !== 1 ? 's' : '' }}
                  </span>
                </div>
              </td>

              <!-- Ações -->
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <div class="flex justify-end space-x-2">
                  <button
                    @click="visualizarAnimal(animal)"
                    class="text-primary-600 hover:text-primary-900 p-2 rounded-lg hover:bg-primary-50 transition-colors"
                    title="Ver detalhes"
                  >
                    <FontAwesomeIcon icon="eye" />
                  </button>
                  <button
                    @click="editarAnimal(animal)"
                    class="text-blue-600 hover:text-blue-900 p-2 rounded-lg hover:bg-blue-50 transition-colors"
                    title="Editar"
                  >
                    <FontAwesomeIcon icon="edit" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 📊 Informações da tabela -->
      <div class="px-6 py-4 bg-gray-50 border-t border-gray-200">
        <div class="flex items-center justify-between text-sm text-gray-500">
          <div>
            Mostrando {{ animaisFiltrados.length }} de {{ animais.length }} animais
          </div>
          <div v-if="ultimaAtualizacao" class="flex items-center">
            <FontAwesomeIcon icon="refresh" class="mr-1" />
            Atualizado em {{ ultimaAtualizacao }}
          </div>
        </div>
      </div>
    </BaseCard>

    <!-- 📭 Estado vazio -->
    <BaseCard v-else class="p-12 text-center">
      <FontAwesomeIcon icon="dog" class="text-6xl text-gray-300 mb-6" />
      <h3 class="text-xl font-semibold text-gray-700 mb-2">Nenhum animal encontrado</h3>
      <p class="text-gray-500 mb-6">
        Não há animais cadastrados no sistema ainda.
      </p>
      <BaseButton variant="primary" @click="$router.push('/animais/novo')">
        <FontAwesomeIcon icon="plus" class="mr-2" />
        Adicionar primeiro animal
      </BaseButton>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
// 📚 Imports
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseButton from '@/components/UI/BaseButton.vue'
import { animaisService } from '@/services/api'
import type { Animal } from '@/types/api'

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

// 🎨 Computed Properties
const animaisFiltrados = computed(() => {
  return animais.value.filter(animal => {
    const nomeMatch = !filtroNome.value ||
      animal.nome.toLowerCase().includes(filtroNome.value.toLowerCase())

    const clienteMatch = !filtroCliente.value ||
      animal.cliente?.nomeCompleto === filtroCliente.value

    const tipoMatch = !filtroTipo.value ||
      animal.tipo === filtroTipo.value

    return nomeMatch && clienteMatch && tipoMatch
  })
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

// 🛠️ Métodos
const carregarAnimais = async (): Promise<void> => {
  try {
    console.log('🔄 Iniciando carregamento de animais...')
    loading.value = true
    error.value = null
    const response = await animaisService.buscarTodos()
    animais.value = response
    ultimaAtualizacao.value = formatarHorario()
    console.log(`✅ ${response.length} animais carregados com sucesso!`)
  } catch (err) {
    console.error('❌ Erro ao carregar animais:', err)
    error.value = err instanceof Error ? err.message : 'Ocorreu um erro inesperado ao carregar os animais'
  } finally {
    loading.value = false
  }
}

const formatarHorario = (): string => {
  return new Date().toLocaleTimeString('pt-BR', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getCorTipo = (tipo: string): string => {
  const cores: Record<string, string> = {
    'Cachorro': 'bg-blue-100 text-blue-800',
    'Gato': 'bg-purple-100 text-purple-800',
    'Pássaro': 'bg-yellow-100 text-yellow-800',
    'Peixe': 'bg-cyan-100 text-cyan-800',
    'Hamster': 'bg-pink-100 text-pink-800',
    'Coelho': 'bg-green-100 text-green-800'
  }
  return cores[tipo] || 'bg-gray-100 text-gray-800'
}

const visualizarAnimal = (animal: Animal): void => {
  console.log('👁️ Visualizar animal:', animal.nome)
  // TODO: Implementar modal ou página de detalhes
  alert(`Detalhes do ${animal.nome} serão implementados em breve!`)
}

const editarAnimal = (animal: Animal): void => {
  console.log('✏️ Editar animal:', animal.nome)
  // TODO: Navegar para página de edição
  alert(`Edição do ${animal.nome} será implementada em breve!`)
}

// 🎬 Lifecycle
onMounted(() => {
  console.log('🎬 Página de animais carregada!')
  carregarAnimais()
})
</script>
