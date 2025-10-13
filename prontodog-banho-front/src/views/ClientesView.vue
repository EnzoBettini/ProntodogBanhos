<template>
  <div class="p-8">
    <!-- 📋 Cabeçalho da página -->
    <BaseCard>
      <template #header>
        <div class="flex items-center justify-between">
          <h1 class="text-2xl font-bold text-primary-700 flex items-center gap-2">
            <FontAwesomeIcon icon="users" />
            Lista de Clientes
            <!-- 📊 Contador de clientes -->
            <BaseBadge v-if="!loading && clientes.length > 0" variant="primary">
              {{ clientes.length }}
            </BaseBadge>
          </h1>

          <!-- 🔄 Botão de atualizar -->
          <BaseButton
            variant="ghost"
            size="sm"
            @click="carregarClientes"
            :disabled="loading"
          >
            <FontAwesomeIcon
              :icon="loading ? 'spinner' : 'refresh'"
              :class="{ 'animate-spin': loading }"
              class="mr-2"
            />
            {{ loading ? 'Carregando...' : 'Atualizar' }}
          </BaseButton>
        </div>
      </template>

      <p class="text-gray-600 mb-6">Gerencie todos os clientes do ProntoDog Banhos.</p>

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

      <!-- ✅ Lista de Clientes (Estado de Sucesso) -->
      <div v-else>
        <!-- 🔝 Ações do topo -->
        <div class="flex items-center justify-between mb-6">
          <div class="flex items-center gap-4">
            <!-- 🔍 Busca (placeholder para futuro) -->
            <div class="relative">
              <FontAwesomeIcon icon="search" class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" />
              <input
                v-model="filtroNome"
                type="text"
                placeholder="Buscar cliente..."
                class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
              >
            </div>
          </div>

          <BaseButton variant="primary" @click="$router.push('/clientes/novo')">
            <FontAwesomeIcon icon="plus" class="mr-2" />
            Novo Cliente
          </BaseButton>
        </div>

        <!-- 📊 Grid de Clientes -->
        <div class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          <BaseCard
            v-for="cliente in clientesFiltrados"
            :key="cliente.id"
            variant="outlined"
            class="hover:shadow-lg transition-all duration-200 cursor-pointer"
            @click="verDetalhes(cliente)"
          >
            <div class="p-4">
              <!-- 👤 Cabeçalho do cliente -->
              <div class="flex items-start justify-between mb-3">
                <div class="flex-1">
                  <h3 class="font-semibold text-gray-900 mb-1">{{ cliente.nomeCompleto }}</h3>
                  <p class="text-sm text-gray-500">CPF: {{ formatarCpf(cliente.cpf) }}</p>
                  <p class="text-sm text-gray-500">SimplesVet: {{ cliente.codigoSimplesVet }}</p>
                </div>
                <BaseBadge
                  :variant="cliente.animais.length > 0 ? 'success' : 'gray'"
                  size="sm"
                >
                  {{ cliente.animais.length }} pets
                </BaseBadge>
              </div>

              <!-- 📞 Contatos -->
              <div class="space-y-2 mb-4">
                <!-- Telefones -->
                <div v-if="cliente.telefones.length > 0" class="flex items-center text-sm text-gray-600">
                  <FontAwesomeIcon icon="phone" class="w-4 h-4 mr-2 text-gray-400" />
                  {{ formatarTelefone(cliente.telefones[0]?.telefone || '') }}
                  <span v-if="cliente.telefones.length > 1" class="ml-1 text-gray-400">
                    (+{{ cliente.telefones.length - 1 }})
                  </span>
                </div>

                <!-- Emails -->
                <div v-if="cliente.emailClientes.length > 0" class="flex items-center text-sm text-gray-600">
                  <FontAwesomeIcon icon="envelope" class="w-4 h-4 mr-2 text-gray-400" />
                  {{ cliente.emailClientes[0]?.email || '' }}
                </div>
              </div>

              <!-- 🐕 Pets -->
              <div v-if="cliente.animais.length > 0" class="border-t pt-3">
                <p class="text-xs text-gray-500 mb-2">Pets cadastrados:</p>
                <div class="flex flex-wrap gap-1">
                  <BaseBadge
                    v-for="animal in cliente.animais.slice(0, 3)"
                    :key="animal.id"
                    variant="info"
                    size="sm"
                  >
                    {{ animal.nome }}
                  </BaseBadge>
                  <BaseBadge
                    v-if="cliente.animais.length > 3"
                    variant="gray"
                    size="sm"
                  >
                    +{{ cliente.animais.length - 3 }}
                  </BaseBadge>
                </div>
              </div>
            </div>
          </BaseCard>
        </div>

        <!-- 📊 Rodapé com informações -->
        <div class="mt-8 pt-6 border-t border-gray-200">
          <div class="flex items-center justify-between text-sm text-gray-500">
            <span>Total: {{ clientes.length }} clientes</span>
            <span>Última atualização: {{ ultimaAtualizacao }}</span>
          </div>
        </div>
      </div>
    </BaseCard>

    <!-- 🎭 Modal de Perfil do Cliente -->
    <ClienteProfileModal
      :is-open="modalPerfilAberto"
      :cliente-id="clienteSelecionadoId"
      @close="fecharModalPerfil"
      @cliente-excluido="onClienteExcluido"
      @cliente-atualizado="onClienteAtualizado"
    />
  </div>
</template>

<script setup lang="ts">
// 📚 Imports necessários
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseButton from '@/components/UI/BaseButton.vue'
import BaseBadge from '@/components/UI/BaseBadge.vue'
import { clientesService } from '@/services/api'
import type { Cliente } from '@/types/api'
import ClienteProfileModal from '@/components/UI/ClienteProfileModal.vue'

// 🎯 Configuração do router
const router = useRouter()

// 📊 Estados reativos da aplicação
const clientes = ref<Cliente[]>([])           // Lista de clientes
const loading = ref(false)                    // Estado de carregamento
const error = ref<string | null>(null)       // Mensagem de erro
const filtroNome = ref('')                   // Filtro de busca
const ultimaAtualizacao = ref('')            // Timestamp da última atualização

// 🎭 Modal de perfil
const modalPerfilAberto = ref(false)
const clienteSelecionadoId = ref<number | null>(null)

// 💻 Computadas (dados derivados)
const clientesFiltrados = computed(() => {
  if (!filtroNome.value) return clientes.value

  return clientes.value.filter(cliente =>
    cliente.nomeCompleto.toLowerCase().includes(filtroNome.value.toLowerCase()) ||
    cliente.cpf.includes(filtroNome.value)
  )
})

// 🔧 Funções utilitárias
const formatarCpf = (cpf: string): string => {
  return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
}

const formatarTelefone = (telefone: string): string => {
  // Remove caracteres não numéricos
  const numeros = telefone.replace(/\D/g, '')

  // Formata conforme o tamanho
  if (numeros.length === 11) {
    return numeros.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3')
  } else if (numeros.length === 10) {
    return numeros.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3')
  }

  return telefone
}

const formatarHorario = (): string => {
  return new Date().toLocaleString('pt-BR')
}

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
  clienteSelecionadoId.value = cliente.id
  modalPerfilAberto.value = true
}

const fecharModalPerfil = (): void => {
  modalPerfilAberto.value = false
  clienteSelecionadoId.value = null
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

// 🎬 Lifecycle - carrega dados quando o componente é montado
onMounted(() => {
  console.log('🎬 Componente ClientesView montado!')
  carregarClientes()
})
</script>
