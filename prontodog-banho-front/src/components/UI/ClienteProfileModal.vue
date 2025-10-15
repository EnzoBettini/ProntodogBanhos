<template>
  <!-- 🎭 Modal de Perfil do Cliente -->
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto">
    <!-- Overlay -->
    <div class="fixed inset-0 bg-black bg-opacity-50 transition-opacity" @click="fecharModal"></div>

    <!-- Modal Content -->
    <div class="flex items-center justify-center min-h-screen p-4">
      <div class="relative bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-hidden">

        <!-- 💾 Estado de carregamento -->
        <div v-if="loading" class="p-8 text-center">
          <FontAwesomeIcon icon="spinner" class="text-4xl text-primary-500 animate-spin mb-4" />
          <p class="text-gray-600">Carregando dados do cliente...</p>
        </div>

        <!-- ❌ Estado de erro -->
        <div v-else-if="error" class="p-8 text-center">
          <FontAwesomeIcon icon="exclamation-triangle" class="text-4xl text-red-500 mb-4" />
          <h3 class="text-lg font-semibold text-red-700 mb-2">Erro ao carregar cliente</h3>
          <p class="text-red-600 mb-4">{{ error }}</p>
          <BaseButton variant="outline" @click="carregarCliente">
            <FontAwesomeIcon icon="refresh" class="mr-2" />
            Tentar novamente
          </BaseButton>
        </div>

        <!-- 📊 Conteúdo do perfil -->
        <div v-else-if="cliente" class="flex flex-col h-full max-h-[90vh]">

          <!-- 🎯 Header do modal -->
          <div class="flex items-center justify-between p-6 border-b border-gray-200 bg-gradient-primary text-white">
            <div class="flex items-center gap-4">
              <div class="w-12 h-12 bg-white bg-opacity-20 rounded-full flex items-center justify-center">
                <FontAwesomeIcon icon="user" class="text-xl" />
              </div>
              <div>
                <h2 class="text-2xl font-bold">{{ cliente.nomeCompleto }}</h2>
                <p class="opacity-90">Cliente ID: {{ cliente.id }}</p>
              </div>
            </div>
            <button
              @click="fecharModal"
              class="text-white hover:bg-white hover:bg-opacity-20 p-2 rounded-lg transition-colors"
            >
              <FontAwesomeIcon icon="times" class="text-xl" />
            </button>
          </div>

          <!-- 📋 Conteúdo principal -->
          <div class="flex-1 overflow-y-auto p-6 space-y-6">

            <!-- 👤 Informações pessoais -->
            <BaseCard>
              <template #header>
                <div class="flex items-center justify-between">
                  <h3 class="text-xl font-semibold text-gray-800 flex items-center gap-2">
                    <FontAwesomeIcon icon="id-card" class="text-primary-500" />
                    Informações Pessoais
                  </h3>
                  <BaseButton variant="outline" size="sm" @click="editarCliente">
                    <FontAwesomeIcon icon="edit" class="mr-2" />
                    Editar
                  </BaseButton>
                </div>
              </template>

              <div class="grid md:grid-cols-2 gap-6">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">CPF</label>
                  <p class="text-gray-900">{{ formatarCpf(cliente.cpf) }}</p>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">Código SimplesVet</label>
                  <p class="text-gray-900">{{ cliente.codigoSimplesVet }}</p>
                </div>
              </div>
            </BaseCard>

            <!-- 📞 Contatos -->
            <BaseCard v-if="cliente.telefones?.length || cliente.emailClientes?.length">
              <template #header>
                <h3 class="text-xl font-semibold text-gray-800 flex items-center gap-2">
                  <FontAwesomeIcon icon="phone" class="text-primary-500" />
                  Contatos
                </h3>
              </template>

              <div class="grid md:grid-cols-2 gap-6">
                <!-- Telefones -->
                <div v-if="cliente.telefones?.length">
                  <label class="block text-sm font-medium text-gray-700 mb-2">Telefones</label>
                  <div class="space-y-2">
                    <div
                      v-for="(telefone, index) in cliente.telefones"
                      :key="telefone.id"
                      class="flex items-center gap-2 text-gray-900"
                    >
                      <FontAwesomeIcon icon="phone" class="text-gray-400 text-sm" />
                      <span>{{ formatarTelefone(telefone.telefone) }}</span>
                      <BaseBadge v-if="index === 0" variant="primary" size="sm">Principal</BaseBadge>
                    </div>
                  </div>
                </div>

                <!-- Emails -->
                <div v-if="cliente.emailClientes?.length">
                  <label class="block text-sm font-medium text-gray-700 mb-2">Emails</label>
                  <div class="space-y-2">
                    <div
                      v-for="(emailCliente, index) in cliente.emailClientes"
                      :key="emailCliente.id"
                      class="flex items-center gap-2 text-gray-900"
                    >
                      <FontAwesomeIcon icon="envelope" class="text-gray-400 text-sm" />
                      <span>{{ emailCliente.email }}</span>
                      <BaseBadge v-if="index === 0" variant="primary" size="sm">Principal</BaseBadge>
                    </div>
                  </div>
                </div>
              </div>
            </BaseCard>

            <!-- 🐕 Animais do cliente -->
            <BaseCard>
              <template #header>
                <div class="flex items-center justify-between">
                  <h3 class="text-xl font-semibold text-gray-800 flex items-center gap-2">
                    <FontAwesomeIcon icon="dog" class="text-primary-500" />
                    Animais ({{ cliente.animais?.length || 0 }})
                  </h3>
                  <BaseButton variant="outline" size="sm" @click="adicionarAnimal">
                    <FontAwesomeIcon icon="plus" class="mr-2" />
                    Adicionar Animal
                  </BaseButton>
                </div>
              </template>

              <div v-if="!cliente.animais?.length" class="text-center py-8 text-gray-500">
                <FontAwesomeIcon icon="dog" class="text-4xl mb-4 opacity-50" />
                <p class="text-lg font-medium mb-2">Nenhum animal cadastrado</p>
                <p class="text-sm">Este cliente ainda não possui animais cadastrados.</p>
              </div>

              <div v-else class="grid gap-4">
                <div
                  v-for="animal in cliente.animais"
                  :key="animal.id"
                  class="border border-gray-200 rounded-lg p-4 hover:border-primary-200 transition-colors"
                >
                  <div class="flex items-center justify-between">
                    <div class="flex items-center gap-3">
                      <div class="w-10 h-10 bg-primary-100 rounded-full flex items-center justify-center">
                        <FontAwesomeIcon icon="dog" class="text-primary-600" />
                      </div>
                      <div>
                        <h4 class="font-semibold text-gray-900">{{ animal.nome }}</h4>
                        <div class="flex items-center gap-4 text-sm text-gray-500">
                          <span>{{ animal.tipo }}</span>
                          <span>•</span>
                          <span>SimplesVet: {{ animal.codigoSimplesVet }}</span>
                        </div>
                      </div>
                    </div>
                    <div class="flex items-center gap-2">
                      <BaseBadge
                        :variant="animal.servicos?.length ? 'success' : 'gray'"
                        size="sm"
                      >
                        {{ animal.servicos?.length || 0 }} serviço{{ (animal.servicos?.length || 0) !== 1 ? 's' : '' }}
                      </BaseBadge>
                      <button
                        @click="verAnimal(animal)"
                        class="text-primary-600 hover:text-primary-900 p-1 rounded"
                        title="Ver detalhes"
                      >
                        <FontAwesomeIcon icon="eye" />
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </BaseCard>

            <!-- 📊 Estatísticas -->
            <BaseCard>
              <template #header>
                <h3 class="text-xl font-semibold text-gray-800 flex items-center gap-2">
                  <FontAwesomeIcon icon="chart-bar" class="text-primary-500" />
                  Estatísticas
                </h3>
              </template>

              <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div class="text-center p-4 bg-blue-50 rounded-lg">
                  <div class="text-2xl font-bold text-blue-600">{{ cliente.animais?.length || 0 }}</div>
                  <div class="text-sm text-blue-600">Animais</div>
                </div>
                <div class="text-center p-4 bg-green-50 rounded-lg">
                  <div class="text-2xl font-bold text-green-600">{{ totalServicos }}</div>
                  <div class="text-sm text-green-600">Serviços</div>
                </div>
                <div class="text-center p-4 bg-purple-50 rounded-lg">
                  <div class="text-2xl font-bold text-purple-600">{{ cliente.telefones?.length || 0 }}</div>
                  <div class="text-sm text-purple-600">Telefones</div>
                </div>
                <div class="text-center p-4 bg-orange-50 rounded-lg">
                  <div class="text-2xl font-bold text-orange-600">{{ cliente.emailClientes?.length || 0 }}</div>
                  <div class="text-sm text-orange-600">Emails</div>
                </div>
              </div>
            </BaseCard>
          </div>

          <!-- 🎬 Footer com ações -->
          <div class="border-t border-gray-200 p-6 bg-gray-50">
            <div class="flex items-center justify-between">
              <div class="text-sm text-gray-500">
                <FontAwesomeIcon icon="calendar-days" class="mr-1" />
                Cliente desde {{ formatarData(cliente.id) }} <!-- Simula data baseada no ID -->
              </div>
              <div class="flex gap-3">
                <BaseButton variant="outline" @click="fecharModal">
                  <FontAwesomeIcon icon="times" class="mr-2" />
                  Fechar
                </BaseButton>
                <BaseButton variant="outline" @click="editarCliente">
                  <FontAwesomeIcon icon="edit" class="mr-2" />
                  Editar Cliente
                </BaseButton>
                <BaseButton
                  variant="danger"
                  @click="confirmarExclusao"
                  :disabled="excluindo"
                >
                  <FontAwesomeIcon
                    :icon="excluindo ? 'spinner' : 'trash'"
                    :class="{ 'animate-spin': excluindo }"
                    class="mr-2"
                  />
                  {{ excluindo ? 'Excluindo...' : 'Excluir Cliente' }}
                </BaseButton>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 🎭 Modal de Edição -->
    <ClienteEditModal
      :is-open="modalEdicaoAberto"
      :cliente="cliente"
      @close="fecharModalEdicao"
      @cliente-atualizado="onClienteEditado"
    />
  </div>
</template>

<script setup lang="ts">
// 📚 Imports
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseButton from '@/components/UI/BaseButton.vue'
import BaseBadge from '@/components/UI/BaseBadge.vue'
import ClienteEditModal from '@/components/UI/ClienteEditModal.vue'
import { clientesService } from '@/services/api'
import type { Cliente, Animal } from '@/types/api'

// 🎯 Props
interface Props {
  isOpen: boolean
  clienteId: number | null
}

const props = defineProps<Props>()

// 🎭 Emits
const emit = defineEmits<{
  close: []
  clienteExcluido: [id: number]
  clienteAtualizado: [cliente: Cliente]
}>()

// 🎯 Configurações
const router = useRouter()

// 📊 Estado do componente
const cliente = ref<Cliente | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)
const excluindo = ref(false)

// 🎭 Modal de edição
const modalEdicaoAberto = ref(false)

// 🎨 Computed Properties
const totalServicos = computed(() => {
  return cliente.value?.animais?.reduce((total, animal) => {
    return total + (animal.servicos?.length || 0)
  }, 0) || 0
})

// 👀 Watchers
watch(() => props.isOpen, (isOpen) => {
  if (isOpen && props.clienteId) {
    carregarCliente()
  }
})

watch(() => props.clienteId, (clienteId) => {
  if (props.isOpen && clienteId) {
    carregarCliente()
  }
})

// 🛠️ Métodos
const carregarCliente = async (): Promise<void> => {
  if (!props.clienteId) return

  try {
    console.log('🔄 Carregando dados do cliente...')
    loading.value = true
    error.value = null
    cliente.value = await clientesService.buscarPorId(props.clienteId)
    console.log('✅ Cliente carregado:', cliente.value.nomeCompleto)
  } catch (err) {
    console.error('❌ Erro ao carregar cliente:', err)
    error.value = err instanceof Error ? err.message : 'Erro inesperado ao carregar cliente'
  } finally {
    loading.value = false
  }
}

const fecharModal = (): void => {
  emit('close')
  // Limpa dados após um delay para evitar flickering
  setTimeout(() => {
    cliente.value = null
    error.value = null
  }, 300)
}

const formatarCpf = (cpf: string): string => {
  if (!cpf) return 'N/A'
  return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
}

const formatarTelefone = (telefone: string): string => {
  if (!telefone) return 'N/A'
  return telefone.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3')
}

const formatarData = (id: number): string => {
  // Simula uma data baseada no ID (apenas para demonstração)
  const dataBase = new Date('2024-01-01')
  dataBase.setDate(dataBase.getDate() + id)

  // Usar formatação local para evitar timezone issues
  return dataBase.toLocaleDateString('pt-BR')
}

const editarCliente = (): void => {
  console.log('✏️ Editar cliente:', cliente.value?.nomeCompleto)
  modalEdicaoAberto.value = true
}

const fecharModalEdicao = (): void => {
  modalEdicaoAberto.value = false
}

const onClienteEditado = (clienteAtualizado: Cliente): void => {
  // Atualiza os dados do cliente no modal atual
  cliente.value = clienteAtualizado
  console.log('✅ Cliente atualizado no modal de perfil')

  // Emite evento para atualizar a lista principal
  emit('clienteAtualizado', clienteAtualizado)
}

const adicionarAnimal = (): void => {
  console.log('➕ Adicionar animal para:', cliente.value?.nomeCompleto)
  // TODO: Navegar para página de novo animal com cliente pré-selecionado
  router.push('/animais/novo')
  fecharModal()
}

const verAnimal = (animal: Animal): void => {
  console.log('👁️ Ver animal:', animal.nome)
  // TODO: Implementar modal ou página de detalhes do animal
  alert(`Detalhes do ${animal.nome} serão implementados em breve!`)
}

const confirmarExclusao = (): void => {
  if (!cliente.value) return

  const confirmacao = confirm(
    `⚠️ ATENÇÃO!\n\nTem certeza que deseja excluir o cliente "${cliente.value.nomeCompleto}"?\n\n` +
    `Esta ação NÃO pode ser desfeita e irá:\n` +
    `• Remover permanentemente o cliente\n` +
    `• Remover todos os ${cliente.value.animais?.length || 0} animais cadastrados\n` +
    `• Remover todo o histórico de serviços\n\n` +
    `Digite "EXCLUIR" abaixo para confirmar:`
  )

  if (confirmacao) {
    const confirmacaoTexto = prompt('Digite "EXCLUIR" para confirmar:')
    if (confirmacaoTexto === 'EXCLUIR') {
      excluirCliente()
    } else {
      alert('Exclusão cancelada. Texto de confirmação incorreto.')
    }
  }
}

const excluirCliente = async (): Promise<void> => {
  if (!cliente.value) return

  try {
    console.log('🗑️ Excluindo cliente...')
    excluindo.value = true
    await clientesService.excluir(cliente.value.id)

    console.log('✅ Cliente excluído com sucesso!')
    alert(`Cliente "${cliente.value.nomeCompleto}" foi excluído com sucesso!`)

    emit('clienteExcluido', cliente.value.id)
    fecharModal()
  } catch (err) {
    console.error('❌ Erro ao excluir cliente:', err)
    const mensagem = err instanceof Error ? err.message : 'Erro inesperado ao excluir cliente'
    alert(`Erro ao excluir cliente:\n${mensagem}`)
  } finally {
    excluindo.value = false
  }
}
</script>
