<template>
  <div class="min-h-screen bg-gradient-to-br from-green-50 via-white to-emerald-100">
    <!-- Header -->
    <div class="relative overflow-hidden bg-gradient-to-r from-green-600 via-emerald-600 to-teal-700 text-white pt-16">
      <div class="absolute inset-0 opacity-10 bg-pattern"></div>

      <div class="relative px-6 py-8 z-20">
        <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
          <div class="flex items-center gap-4 animate-fade-in-up">
            <div class="w-16 h-16 bg-white bg-opacity-20 backdrop-blur-sm rounded-2xl flex items-center justify-center shadow-lg">
              <FontAwesome icon="credit-card" class="text-2xl text-white" />
            </div>

            <div>
              <h1 class="text-4xl font-bold">Formas de Pagamento</h1>
              <p class="text-green-100 mt-1">Gerencie formas de pagamento e taxas</p>
            </div>
          </div>

          <div class="flex items-center gap-3">
            <button
              @click="carregarFormas"
              :disabled="loading"
              class="group flex items-center gap-2 px-4 py-2 bg-white bg-opacity-20 backdrop-blur-sm text-white rounded-xl hover:bg-opacity-30 transition-all duration-300"
            >
              <FontAwesome
                :icon="loading ? 'spinner' : 'refresh'"
                :class="{ 'animate-spin': loading }"
              />
              <span>{{ loading ? 'Carregando...' : 'Atualizar' }}</span>
            </button>

            <button
              @click="abrirModalNovo"
              class="group flex items-center gap-2 px-6 py-2 bg-gradient-to-r from-yellow-400 to-orange-500 text-white rounded-xl hover:from-yellow-500 hover:to-orange-600 transition-all duration-300 shadow-lg"
            >
              <FontAwesome icon="plus" />
              <span>Nova Forma</span>
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

    <!-- Conteúdo Principal -->
    <div class="relative -mt-8 px-6 pb-8 z-30">

      <!-- Loading -->
      <div v-if="loading && formas.length === 0" class="text-center py-12">
        <FontAwesome icon="spinner" class="text-4xl text-green-500 animate-spin mb-4" />
        <p class="text-gray-600">Carregando formas de pagamento...</p>
      </div>

      <!-- Lista de formas -->
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="forma in formas"
          :key="forma.id"
          class="bg-white rounded-xl shadow-sm hover:shadow-md transition p-6"
        >
          <div class="flex items-start justify-between mb-4">
            <div>
              <h3 class="font-bold text-lg text-gray-800">{{ forma.nome }}</h3>
              <span :class="[
                'inline-block px-2 py-1 rounded text-xs font-semibold mt-1',
                getTipoClass(forma.tipo)
              ]">
                {{ getTipoLabel(forma.tipo) }}
              </span>
            </div>

            <div class="flex items-center gap-2">
              <button
                @click="toggleAtivo(forma)"
                :class="[
                  'px-3 py-1 rounded-lg text-xs font-semibold transition',
                  forma.ativo
                    ? 'bg-green-100 text-green-700 hover:bg-green-200'
                    : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                ]"
              >
                {{ forma.ativo ? 'Ativo' : 'Inativo' }}
              </button>
            </div>
          </div>

          <div class="space-y-2 text-sm">
            <div class="flex justify-between">
              <span class="text-gray-600">Taxa Percentual:</span>
              <span class="font-semibold">{{ forma.taxaPercentual }}%</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">Taxa Fixa:</span>
              <span class="font-semibold">{{ formatarMoeda(forma.taxaFixa) }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">Parcelas Máx:</span>
              <span class="font-semibold">{{ forma.parcelasMax }}x</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">Prazo Recebimento:</span>
              <span class="font-semibold">{{ forma.diasRecebimento }} dias</span>
            </div>
          </div>

          <div class="flex gap-2 mt-4 pt-4 border-t">
            <button
              @click="abrirModalEditar(forma)"
              class="flex-1 px-3 py-2 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 transition text-sm font-medium"
            >
              <FontAwesome icon="edit" class="mr-1" />
              Editar
            </button>
            <button
              @click="excluirForma(forma)"
              class="px-3 py-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition text-sm"
            >
              <FontAwesome icon="trash" />
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de Criar/Editar -->
    <BaseModal v-model="mostrarModal" :title="formaEditando ? 'Editar Forma de Pagamento' : 'Nova Forma de Pagamento'" size="lg">
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Nome</label>
          <input
            v-model="formForma.nome"
            type="text"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
            placeholder="Ex: Crédito 3x"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Tipo</label>
          <select
            v-model="formForma.tipo"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
          >
            <option value="dinheiro">Dinheiro</option>
            <option value="debito">Débito</option>
            <option value="credito">Crédito</option>
            <option value="pix">PIX</option>
            <option value="boleto">Boleto</option>
            <option value="outro">Outro</option>
          </select>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Taxa Percentual (%)</label>
            <input
              v-model.number="formForma.taxaPercentual"
              type="number"
              step="0.01"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
              placeholder="0.00"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Taxa Fixa (R$)</label>
            <input
              v-model.number="formForma.taxaFixa"
              type="number"
              step="0.01"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
              placeholder="0.00"
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Parcelas Máximas</label>
            <input
              v-model.number="formForma.parcelasMax"
              type="number"
              min="1"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Dias para Receber</label>
            <input
              v-model.number="formForma.diasRecebimento"
              type="number"
              min="0"
              class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500"
            />
          </div>
        </div>

        <div class="flex gap-3 pt-4 border-t">
          <button
            @click="mostrarModal = false"
            class="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition"
          >
            Cancelar
          </button>
          <button
            @click="salvarForma"
            class="flex-1 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
          >
            <FontAwesome icon="check" class="mr-2" />
            Salvar
          </button>
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { formasPagamentoService } from '@/services/api'
import { FontAwesomeIcon as FontAwesome } from '@fortawesome/vue-fontawesome'
import { formatarValor } from '@/utils/formatters'
import BaseModal from '@/components/UI/BaseModal.vue'

const formas = ref<any[]>([])
const loading = ref(false)
const mostrarModal = ref(false)
const formaEditando = ref<any>(null)

const formForma = ref({
  nome: '',
  tipo: 'dinheiro',
  taxaPercentual: 0,
  taxaFixa: 0,
  parcelasMax: 1,
  diasRecebimento: 0,
  ativo: true
})

const carregarFormas = async () => {
  try {
    loading.value = true
    formas.value = await formasPagamentoService.buscarTodas()
  } catch (err) {
    console.error('Erro ao carregar formas:', err)
    alert('Erro ao carregar formas de pagamento')
  } finally {
    loading.value = false
  }
}

const abrirModalNovo = () => {
  formaEditando.value = null
  formForma.value = {
    nome: '',
    tipo: 'dinheiro',
    taxaPercentual: 0,
    taxaFixa: 0,
    parcelasMax: 1,
    diasRecebimento: 0,
    ativo: true
  }
  mostrarModal.value = true
}

const abrirModalEditar = (forma: any) => {
  formaEditando.value = forma
  formForma.value = { ...forma }
  mostrarModal.value = true
}

const salvarForma = async () => {
  try {
    if (formaEditando.value) {
      await formasPagamentoService.atualizar(formaEditando.value.id, formForma.value)
    } else {
      await formasPagamentoService.criar(formForma.value)
    }
    mostrarModal.value = false
    carregarFormas()
  } catch (err: any) {
    alert('Erro ao salvar: ' + (err.message || 'Erro desconhecido'))
  }
}

const toggleAtivo = async (forma: any) => {
  try {
    if (forma.ativo) {
      await formasPagamentoService.desativar(forma.id)
    } else {
      await formasPagamentoService.ativar(forma.id)
    }
    carregarFormas()
  } catch (err: any) {
    alert('Erro ao alterar status: ' + (err.message || 'Erro desconhecido'))
  }
}

const excluirForma = async (forma: any) => {
  if (!confirm(`Deseja realmente excluir "${forma.nome}"?`)) return

  try {
    await formasPagamentoService.excluir(forma.id)
    carregarFormas()
  } catch (err: any) {
    alert('Erro ao excluir: ' + (err.message || 'Erro desconhecido'))
  }
}

const getTipoClass = (tipo: string) => {
  const classes: Record<string, string> = {
    dinheiro: 'bg-green-100 text-green-700',
    debito: 'bg-blue-100 text-blue-700',
    credito: 'bg-purple-100 text-purple-700',
    pix: 'bg-cyan-100 text-cyan-700',
    boleto: 'bg-orange-100 text-orange-700',
    outro: 'bg-gray-100 text-gray-700'
  }
  return classes[tipo] || 'bg-gray-100 text-gray-700'
}

const getTipoLabel = (tipo: string) => {
  const labels: Record<string, string> = {
    dinheiro: 'Dinheiro',
    debito: 'Débito',
    credito: 'Crédito',
    pix: 'PIX',
    boleto: 'Boleto',
    outro: 'Outro'
  }
  return labels[tipo] || tipo
}

const formatarMoeda = (valor: number) => formatarValor(valor)

onMounted(() => {
  carregarFormas()
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
</style>

