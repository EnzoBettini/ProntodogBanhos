<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-indigo-100">
    <!-- 🌟 Header -->
    <div class="relative overflow-hidden bg-gradient-to-r from-blue-600 via-indigo-600 to-blue-700 text-white pt-16">
      <div class="absolute inset-0 opacity-10 bg-pattern"></div>

      <div class="relative px-6 py-8 z-20">
        <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
          <div class="flex items-center gap-4 animate-fade-in-up">
            <div class="relative">
              <div class="w-16 h-16 bg-white bg-opacity-20 backdrop-blur-sm rounded-2xl flex items-center justify-center transform hover:scale-110 transition-all duration-300 shadow-lg">
                <FontAwesomeIcon icon="edit" class="text-2xl text-white animate-bounce-gentle" />
              </div>
            </div>

            <div class="space-y-1">
              <h1 class="text-4xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-blue-100">
                Editar Maquininha
              </h1>
              <p class="text-white text-lg flex items-center gap-2 font-medium opacity-90">
                <FontAwesomeIcon icon="pencil" class="text-yellow-400 mr-1" />
                {{ maquininha?.nome || 'Carregando...' }}
              </p>
            </div>
          </div>

          <button
            @click="voltar"
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
          <path d="M0 60h1440V0c-120 40-240 60-360 60S840 40 720 60s-240 0-360-60S120 40 0 60z" fill="#3b82f6"/>
        </svg>
      </div>
    </div>

    <!-- Container -->
    <div class="px-6 pb-8 -mt-8 relative z-10">
      <div class="max-w-5xl mx-auto">
        <!-- Card principal -->
        <div class="relative bg-white bg-opacity-80 backdrop-blur-xl rounded-3xl shadow-2xl border border-white border-opacity-20 overflow-hidden animate-slide-up">

          <!-- Loading -->
          <div v-if="carregando || salvando" class="absolute inset-0 bg-gradient-to-br from-blue-500 to-indigo-600 bg-opacity-95 backdrop-blur-sm flex items-center justify-center z-50">
            <div class="text-center text-white">
              <div class="relative mb-6">
                <div class="w-20 h-20 border-4 border-white border-opacity-30 rounded-full animate-spin"></div>
                <div class="absolute top-0 left-0 w-20 h-20 border-4 border-transparent border-t-white rounded-full animate-spin"></div>
                <FontAwesomeIcon icon="credit-card" class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-2xl animate-bounce" />
              </div>
              <h3 class="text-xl font-semibold mb-2">{{ salvando ? 'Salvando alterações...' : 'Carregando maquininha...' }}</h3>
              <p class="text-white text-opacity-80">Aguarde um momento! 💳</p>
            </div>
          </div>

          <form v-if="!carregando" @submit.prevent="salvar" class="p-8 space-y-8">

            <!-- Dados Básicos -->
            <div class="space-y-6">
              <!-- Nome da Maquininha -->
              <div class="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl p-6 border border-blue-100">
                <div class="flex items-center gap-3 mb-4">
                  <div class="w-10 h-10 bg-gradient-to-r from-blue-500 to-indigo-500 rounded-xl flex items-center justify-center">
                    <FontAwesomeIcon icon="tag" class="text-white" />
                  </div>
                  <h2 class="text-xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
                    Identificação
                  </h2>
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Nome da Maquininha *</label>
                  <input
                    v-model="formData.nome"
                    type="text"
                    required
                    placeholder="Ex: Stone Loja Principal"
                    class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all duration-200"
                  />
                </div>
              </div>

              <!-- Adquirente e Conta -->
              <div class="bg-gradient-to-r from-indigo-50 to-purple-50 rounded-2xl p-6 border border-indigo-100">
                <div class="flex items-center gap-3 mb-4">
                  <div class="w-10 h-10 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-xl flex items-center justify-center">
                    <FontAwesomeIcon icon="building" class="text-white" />
                  </div>
                  <h2 class="text-xl font-bold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent">
                    Empresa e Conta
                  </h2>
                </div>
                <div class="grid md:grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Adquirente/Operadora *</label>
                    <select
                      v-model="formData.adquirenteId"
                      required
                      class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200 transition-all duration-200"
                    >
                      <option value="">Selecione...</option>
                      <option v-for="adq in adquirentes" :key="adq.id" :value="adq.id">
                        {{ adq.nome }}
                      </option>
                    </select>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Conta Bancária *</label>
                    <select
                      v-model="formData.contaBancariaId"
                      required
                      class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200 transition-all duration-200"
                    >
                      <option value="">Selecione...</option>
                      <option v-for="conta in contas" :key="conta.id" :value="conta.id">
                        {{ conta.nome }} - {{ conta.banco }}
                      </option>
                    </select>
                  </div>
                </div>
              </div>

              <!-- Prazos -->
              <div class="bg-gradient-to-r from-green-50 to-emerald-50 rounded-2xl p-6 border border-green-100">
                <div class="flex items-center gap-3 mb-4">
                  <div class="w-10 h-10 bg-gradient-to-r from-green-500 to-emerald-500 rounded-xl flex items-center justify-center">
                    <FontAwesomeIcon icon="calendar" class="text-white" />
                  </div>
                  <h2 class="text-xl font-bold bg-gradient-to-r from-green-600 to-emerald-600 bg-clip-text text-transparent">
                    Prazos de Recebimento
                  </h2>
                </div>
                <div class="grid md:grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Prazo Débito (dias) *</label>
                    <input
                      v-model.number="formData.prazoRecebimentoDebito"
                      type="number"
                      min="0"
                      required
                      class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-green-500 focus:ring-2 focus:ring-green-200 transition-all duration-200"
                    />
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Prazo Crédito (dias) *</label>
                    <input
                      v-model.number="formData.prazoRecebimentoCredito"
                      type="number"
                      min="1"
                      required
                      class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-green-500 focus:ring-2 focus:ring-green-200 transition-all duration-200"
                    />
                  </div>
                </div>
              </div>

              <!-- Antecipação -->
              <div class="bg-gradient-to-r from-yellow-50 to-orange-50 rounded-2xl p-6 border border-yellow-100">
                <div class="flex items-center gap-3 mb-4">
                  <div class="w-10 h-10 bg-gradient-to-r from-yellow-500 to-orange-500 rounded-xl flex items-center justify-center">
                    <FontAwesomeIcon icon="bolt" class="text-white" />
                  </div>
                  <h2 class="text-xl font-bold bg-gradient-to-r from-yellow-600 to-orange-600 bg-clip-text text-transparent">
                    Antecipação
                  </h2>
                </div>
                <div class="space-y-4">
                  <div class="flex items-center gap-3">
                    <input
                      v-model="formData.aceitaAntecipacao"
                      type="checkbox"
                      id="aceita-antecipacao"
                      class="w-5 h-5 text-yellow-600 border-gray-300 rounded focus:ring-yellow-500"
                    />
                    <label for="aceita-antecipacao" class="text-sm font-medium text-gray-700">
                      Esta maquininha aceita antecipação de recebíveis?
                    </label>
                  </div>

                  <div v-if="formData.aceitaAntecipacao" class="pl-8 space-y-4">
                    <div class="flex items-center gap-3">
                      <input
                        v-model="formData.antecipacaoAutomatica"
                        type="checkbox"
                        id="antecipacao-automatica"
                        class="w-5 h-5 text-yellow-600 border-gray-300 rounded focus:ring-yellow-500"
                      />
                      <label for="antecipacao-automatica" class="text-sm font-medium text-gray-700">
                        Antecipação automática
                      </label>
                    </div>

                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-2">Taxa de Antecipação Mensal (%)</label>
                      <input
                        v-model.number="formData.taxaAntecipacaoMensal"
                        type="number"
                        min="0"
                        step="0.01"
                        class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-yellow-500 focus:ring-2 focus:ring-yellow-200 transition-all duration-200"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <!-- PIX -->
              <div class="bg-gradient-to-r from-cyan-50 to-blue-50 rounded-2xl p-6 border border-cyan-100">
                <div class="flex items-center gap-3 mb-6">
                  <div class="w-10 h-10 bg-gradient-to-r from-cyan-500 to-blue-500 rounded-xl flex items-center justify-center">
                    <FontAwesomeIcon icon="bolt" class="text-white" />
                  </div>
                  <h2 class="text-xl font-bold bg-gradient-to-r from-cyan-600 to-blue-600 bg-clip-text text-transparent">
                    PIX na Maquininha
                  </h2>
                </div>

                <div class="space-y-6">
                  <div class="flex items-center gap-3">
                    <input
                      v-model="formData.aceitaPix"
                      type="checkbox"
                      id="aceita-pix"
                      class="w-5 h-5 text-cyan-600 border-gray-300 rounded focus:ring-cyan-500"
                    />
                    <label for="aceita-pix" class="text-sm font-medium text-gray-700">
                      Esta maquininha aceita pagamentos via PIX?
                    </label>
                  </div>

                  <div v-if="formData.aceitaPix" class="pl-8 space-y-4">
                    <div>
                      <label class="block text-sm font-medium text-gray-700 mb-2">Conta para Recebimento PIX</label>
                      <select
                        v-model="formData.contaPixId"
                        class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-cyan-500 focus:ring-2 focus:ring-cyan-200 transition-all duration-200"
                      >
                        <option :value="null">Mesma conta da maquininha</option>
                        <option v-for="conta in contas" :key="conta.id" :value="conta.id">
                          {{ conta.nome }} - {{ conta.banco }}
                        </option>
                      </select>
                    </div>

                    <div class="grid md:grid-cols-2 gap-4">
                      <div>
                        <label class="block text-sm font-medium text-gray-700 mb-2">Taxa PIX (%)</label>
                        <input
                          v-model.number="formData.taxaPix"
                          type="number"
                          min="0"
                          step="0.01"
                          class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-cyan-500 focus:ring-2 focus:ring-cyan-200 transition-all duration-200"
                        />
                      </div>
                      <div>
                        <label class="block text-sm font-medium text-gray-700 mb-2">Prazo Recebimento PIX (dias)</label>
                        <input
                          v-model.number="formData.prazoRecebimentoPix"
                          type="number"
                          min="0"
                          class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-cyan-500 focus:ring-2 focus:ring-cyan-200 transition-all duration-200"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Status -->
              <div class="bg-gradient-to-r from-gray-50 to-slate-50 rounded-2xl p-6 border border-gray-100">
                <div class="flex items-center gap-3 mb-4">
                  <div class="w-10 h-10 bg-gradient-to-r from-gray-500 to-slate-500 rounded-xl flex items-center justify-center">
                    <FontAwesomeIcon icon="toggle-on" class="text-white" />
                  </div>
                  <h2 class="text-xl font-bold bg-gradient-to-r from-gray-600 to-slate-600 bg-clip-text text-transparent">
                    Status
                  </h2>
                </div>
                <div class="flex items-center gap-3">
                  <input
                    v-model="formData.ativo"
                    type="checkbox"
                    id="maquininha-ativa"
                    class="w-5 h-5 text-green-600 border-gray-300 rounded focus:ring-green-500"
                  />
                  <label for="maquininha-ativa" class="text-sm font-medium text-gray-700">
                    Maquininha ativa
                  </label>
                </div>
              </div>
            </div>

            <!-- Botões -->
            <div class="flex items-center justify-between pt-6 border-t border-gray-200">
              <button
                @click.prevent="voltar"
                type="button"
                class="flex items-center gap-2 px-6 py-3 bg-gray-200 text-gray-700 rounded-xl hover:bg-gray-300 transition-all duration-200 font-medium"
              >
                <FontAwesomeIcon icon="times" />
                <span>Cancelar</span>
              </button>

              <button
                type="submit"
                :disabled="salvando"
                class="flex items-center gap-2 px-8 py-3 bg-gradient-to-r from-green-600 to-emerald-600 text-white rounded-xl hover:from-green-700 hover:to-emerald-700 transition-all duration-200 font-medium shadow-lg hover:shadow-xl transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <FontAwesomeIcon icon="check" />
                <span>Salvar Alterações</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
  maquininhasService,
  adquirentesService,
  bandeirasService,
  contasBancariasService
} from '@/services/api'

const router = useRouter()
const route = useRoute()
const carregando = ref(true)
const salvando = ref(false)

const maquininha = ref<any>(null)
const adquirentes = ref<any[]>([])
const bandeiras = ref<any[]>([])
const contas = ref<any[]>([])

const formData = ref({
  nome: '',
  adquirenteId: 0,
  contaBancariaId: 0,
  prazoRecebimentoDebito: 1,
  prazoRecebimentoCredito: 30,
  aceitaAntecipacao: false,
  antecipacaoAutomatica: false,
  taxaAntecipacaoMensal: 0,
  aceitaPix: false,
  contaPixId: null as number | null,
  taxaPix: 0,
  prazoRecebimentoPix: 0,
  ativo: true
})

onMounted(async () => {
  try {
    carregando.value = true
    const id = Number(route.params.id)

    const [adqResponse, bandResponse, contasResponse, maqResponse] = await Promise.all([
      adquirentesService.listarAtivos(),
      bandeirasService.listarAtivas(),
      contasBancariasService.listarAtivas(),
      maquininhasService.buscarPorId(id)
    ])

    adquirentes.value = adqResponse
    bandeiras.value = bandResponse
    contas.value = contasResponse
    maquininha.value = maqResponse

    // Preencher formulário com dados existentes
    formData.value = {
      nome: maqResponse.nome,
      adquirenteId: maqResponse.adquirenteId,
      contaBancariaId: maqResponse.contaBancariaId,
      prazoRecebimentoDebito: maqResponse.prazoRecebimentoDebito,
      prazoRecebimentoCredito: maqResponse.prazoRecebimentoCredito,
      aceitaAntecipacao: maqResponse.aceitaAntecipacao,
      antecipacaoAutomatica: maqResponse.antecipacaoAutomatica,
      taxaAntecipacaoMensal: maqResponse.taxaAntecipacaoMensal,
      aceitaPix: maqResponse.aceitaPix,
      contaPixId: maqResponse.contaPixId || null,
      taxaPix: maqResponse.taxaPix,
      prazoRecebimentoPix: maqResponse.prazoRecebimentoPix,
      ativo: maqResponse.ativo
    }
  } catch (error) {
    console.error('Erro ao carregar dados:', error)
    alert('Erro ao carregar os dados da maquininha.')
    router.push('/maquininhas')
  } finally {
    carregando.value = false
  }
})

const salvar = async () => {
  salvando.value = true

  try {
    const id = Number(route.params.id)

    // Limpar campos relacionados se desabilitados
    if (!formData.value.aceitaPix) {
      formData.value.taxaPix = 0
      formData.value.prazoRecebimentoPix = 0
      formData.value.contaPixId = null
    }

    if (!formData.value.aceitaAntecipacao) {
      formData.value.antecipacaoAutomatica = false
      formData.value.taxaAntecipacaoMensal = 0
    }

    await maquininhasService.atualizar(id, formData.value)

    alert('✅ Maquininha atualizada com sucesso!')
    router.push('/maquininhas')
  } catch (error: any) {
    console.error('❌ Erro ao salvar:', error)
    alert(error.message || 'Erro ao atualizar a maquininha. Tente novamente.')
  } finally {
    salvando.value = false
  }
}

const voltar = () => {
  if (confirm('Deseja realmente sair? Alterações não salvas serão perdidas.')) {
    router.push('/maquininhas')
  }
}
</script>

<style scoped>
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
    transform: translateY(40px);
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

.animate-fade-in-up {
  animation: fade-in-up 0.6s ease-out;
}

.animate-slide-up {
  animation: slide-up 0.8s ease-out;
}

.animate-bounce-gentle {
  animation: bounce-gentle 2s ease-in-out infinite;
}

.bg-pattern {
  background-image: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 20px 20px;
}
</style>

