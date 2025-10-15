<template>
  <div class="relative min-h-screen bg-gradient-to-br from-amber-50 via-white to-green-50 p-4 pb-32">
    <!-- 🎨 Background decorativo -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none z-0">
      <div class="absolute top-20 left-10 w-72 h-72 bg-amber-200/20 rounded-full blur-3xl"></div>
      <div class="absolute bottom-20 right-10 w-96 h-96 bg-green-200/20 rounded-full blur-3xl"></div>
      <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-gradient-to-r from-amber-100/10 to-green-100/10 rounded-full blur-3xl"></div>
    </div>

    <div class="relative z-20 max-w-7xl mx-auto pt-8">
      <!-- 🔄 Estado de Loading -->
      <div v-if="loading" class="text-center py-12">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-r from-amber-400 to-green-500 rounded-full mb-4 animate-spin">
          <FontAwesomeIcon :icon="['fas', 'spinner']" class="text-white text-2xl" />
        </div>
        <p class="text-gray-600 text-lg">Carregando detalhes...</p>
      </div>

      <!-- ❌ Estado de Erro -->
      <div v-else-if="error" class="text-center py-12">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-red-500 rounded-full mb-4">
          <FontAwesomeIcon :icon="['fas', 'exclamation-triangle']" class="text-white text-2xl" />
        </div>
        <h3 class="text-xl font-semibold text-gray-800 mb-2">Erro ao carregar</h3>
        <p class="text-gray-600 mb-4">{{ error }}</p>
        <BaseButton @click="carregarDados" variant="primary">
          <FontAwesomeIcon :icon="['fas', 'refresh']" class="mr-2" />
          Tentar Novamente
        </BaseButton>
      </div>

      <!-- 📋 Conteúdo Principal -->
      <div v-else-if="animalServico">
        <!-- Header com navegação -->
        <div class="flex items-center gap-4 mb-8">
          <BaseButton @click="voltarParaLista" variant="ghost" class="flex items-center gap-2">
            <FontAwesomeIcon :icon="['fas', 'arrow-left']" />
            <span class="hidden sm:inline">Voltar para Lista</span>
          </BaseButton>

          <div class="flex-1">
            <h1 class="text-3xl font-bold text-gray-800">Detalhes do Serviço</h1>
            <p class="text-gray-600">Animal: {{ animal?.nome || 'N/A' }} • Serviço: {{ servico?.nome || 'N/A' }}</p>
          </div>

          <div class="flex items-center gap-4">
            <BaseBadge
              :variant="isServicoCompleto ? 'success' : 'warning'"
              size="lg"
            >
              {{ isServicoCompleto ? 'Completo' : 'Em Andamento' }}
            </BaseBadge>

            <!-- Botão Excluir -->
            <BaseButton
              @click="confirmarExclusaoAnimalServico"
              variant="danger"
              class="flex items-center gap-2"
              title="Excluir este animal serviço"
            >
              <FontAwesomeIcon :icon="['fas', 'trash']" />
              <span class="hidden sm:inline">Excluir</span>
            </BaseButton>
          </div>
        </div>

        <!-- Grid principal -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <!-- 📊 Informações do Serviço -->
          <BaseCard class="shadow-xl border-0">
            <div class="flex items-center gap-3 mb-6">
              <div class="w-12 h-12 bg-gradient-to-br from-amber-400 to-green-500 rounded-xl flex items-center justify-center">
                <FontAwesomeIcon :icon="['fas', 'clipboard-list']" class="text-white text-xl" />
              </div>
              <div>
                <h2 class="text-xl font-bold text-gray-800">Informações do Serviço</h2>
                <p class="text-gray-600">Dados e progresso do atendimento</p>
              </div>
            </div>

            <div class="space-y-6">
              <!-- Alerta de Expiração (se vencido) -->
              <div v-if="isPackageExpired" class="relative overflow-hidden p-4 bg-gradient-to-r from-red-50 to-orange-50 rounded-xl border-2 border-red-200 animate-pulse">
                <div class="absolute top-0 right-0 w-16 h-16 bg-red-200/30 rounded-full blur-2xl"></div>
                <div class="relative flex items-center gap-3">
                  <div class="w-8 h-8 bg-red-500 rounded-full flex items-center justify-center animate-bounce">
                    <FontAwesomeIcon :icon="['fas', 'exclamation-triangle']" class="text-white text-sm" />
                  </div>
                  <div>
                    <p class="text-red-800 font-bold text-sm">⚠️ PACOTE VENCIDO</p>
                    <p class="text-red-600 text-sm">
                      Expirou em {{ formatarData(animalServico.dataExpiracao!) }}
                      <span class="ml-2 text-xs bg-red-100 text-red-700 px-2 py-1 rounded-full">
                        {{ diasVencido }} dia{{ diasVencido !== 1 ? 's' : '' }} atrás
                      </span>
                    </p>
                  </div>
                </div>
              </div>

              <!-- ID do Animal Serviço -->
              <div class="p-4 bg-gradient-to-r from-gray-50 to-slate-50 rounded-xl border border-gray-200">
                <div class="flex items-center gap-3">
                  <FontAwesomeIcon :icon="['fas', 'hashtag']" class="text-gray-600" />
                  <div>
                    <p class="text-sm font-medium text-gray-600">ID do Registro</p>
                    <p class="text-lg font-mono font-bold text-gray-800 tracking-wider">#{{ animalServico.id }}</p>
                  </div>
                </div>
              </div>

              <!-- Data do serviço -->
              <div class="flex items-center justify-between p-4 bg-gradient-to-r from-amber-50 to-green-50 rounded-xl border border-amber-200">
                <div class="flex items-center gap-3">
                  <FontAwesomeIcon :icon="['fas', 'calendar-alt']" class="text-amber-600" />
                  <div>
                    <p class="text-sm font-medium text-gray-600">Data do Serviço</p>
                    <p class="text-lg font-semibold text-gray-800">{{ formatarData(animalServico.dataServico) }}</p>
                  </div>
                </div>
                <BaseButton @click="editarData = true" variant="ghost" size="sm">
                  <FontAwesomeIcon :icon="['fas', 'edit']" class="mr-1" />
                  Editar
                </BaseButton>
              </div>

              <!-- Data de Expiração (se existir) -->
              <div v-if="animalServico.dataExpiracao" class="p-4 bg-gradient-to-r from-violet-50 to-purple-50 rounded-xl border border-violet-200"
                   :class="{
                     'border-red-300 bg-gradient-to-r from-red-50 to-orange-50': isPackageExpired,
                     'border-yellow-300 bg-gradient-to-r from-yellow-50 to-amber-50': isPackageExpiringSoon && !isPackageExpired,
                     'border-green-300 bg-gradient-to-r from-green-50 to-emerald-50': !isPackageExpired && !isPackageExpiringSoon
                   }">
                <div class="flex items-center gap-3">
                  <FontAwesomeIcon
                    :icon="['fas', 'clock']"
                    :class="{
                      'text-red-600': isPackageExpired,
                      'text-yellow-600': isPackageExpiringSoon && !isPackageExpired,
                      'text-violet-600': !isPackageExpired && !isPackageExpiringSoon
                    }"
                  />
                  <div class="flex-1">
                    <p class="text-sm font-medium"
                       :class="{
                         'text-red-700': isPackageExpired,
                         'text-yellow-700': isPackageExpiringSoon && !isPackageExpired,
                         'text-gray-600': !isPackageExpired && !isPackageExpiringSoon
                       }">
                      Data de Expiração
                    </p>
                    <p class="text-lg font-semibold"
                       :class="{
                         'text-red-800': isPackageExpired,
                         'text-yellow-800': isPackageExpiringSoon && !isPackageExpired,
                         'text-gray-800': !isPackageExpired && !isPackageExpiringSoon
                       }">
                      {{ formatarData(animalServico.dataExpiracao) }}
                    </p>
                  </div>
                  <div v-if="!isPackageExpired" class="text-right">
                    <p class="text-xs font-medium"
                       :class="{
                         'text-yellow-700': isPackageExpiringSoon,
                         'text-green-700': !isPackageExpiringSoon
                       }">
                      {{ isPackageExpiringSoon ? 'Expira em' : 'Válido por mais' }}
                    </p>
                    <p class="text-sm font-bold"
                       :class="{
                         'text-yellow-800': isPackageExpiringSoon,
                         'text-green-800': !isPackageExpiringSoon
                       }">
                      {{ diasParaExpirar }} dia{{ diasParaExpirar !== 1 ? 's' : '' }}
                    </p>
                  </div>
                </div>
              </div>

              <!-- Progresso dos banhos -->
              <div class="p-4 bg-gradient-to-r from-blue-50 to-cyan-50 rounded-xl border border-blue-200">
                <div class="flex items-center gap-3 mb-3">
                  <FontAwesomeIcon :icon="['fas', 'bath']" class="text-blue-600" />
                  <div>
                    <p class="text-sm font-medium text-gray-600">Progresso dos Banhos</p>
                    <p class="text-lg font-semibold text-gray-800">
                      {{ animalServico.banhosUsados }} de {{ totalBanhos }} banhos
                    </p>
                  </div>
                </div>

                <!-- Barra de progresso -->
                <div class="w-full bg-gray-200 rounded-full h-3 mb-3">
                  <div
                    class="h-3 rounded-full transition-all duration-500"
                    :class="{
                      'bg-green-500': progressoBanhos === 100,
                      'bg-yellow-400': progressoBanhos > 0 && progressoBanhos < 100,
                      'bg-red-400': progressoBanhos === 0
                    }"
                    :style="{ width: `${progressoBanhos}%` }"
                  ></div>
                </div>

                <!-- Botão adicionar banho -->
                <BaseButton
                  v-if="podeAdicionarBanho"
                  @click="mostrarModalBanho = true"
                  variant="primary"
                  size="sm"
                  class="w-full"
                >
                  <FontAwesomeIcon :icon="['fas', 'plus-circle']" class="mr-2" />
                  Registrar Próximo Banho
                </BaseButton>
                <p v-else class="text-center text-gray-500 text-sm font-medium">
                  ✅ Todos os banhos foram realizados
                </p>
              </div>

              <!-- Detalhes do serviço -->
              <div class="p-4 bg-gradient-to-r from-purple-50 to-pink-50 rounded-xl border border-purple-200">
                <div class="flex items-center gap-3 mb-3">
                  <FontAwesomeIcon :icon="['fas', 'wrench']" class="text-purple-600" />
                  <div>
                    <p class="text-sm font-medium text-gray-600">Serviço Contratado</p>
                    <p class="text-lg font-semibold text-gray-800">{{ servico?.nome || 'N/A' }}</p>
                  </div>
                </div>

                <div class="grid grid-cols-2 gap-4 text-sm mb-3">
                  <div>
                    <p class="text-gray-600">Quantidade:</p>
                    <p class="font-medium">{{ servico?.quantidade || 'N/A' }} banhos</p>
                  </div>
                  <div>
                    <p class="text-gray-600">Valor:</p>
                    <p class="font-medium text-green-600">R$ {{ (servico?.valor || 0).toFixed(2).replace('.', ',') }}</p>
                  </div>
                </div>

                <!-- Informação de quem lançou -->
                <div class="pt-3 border-t border-purple-200">
                  <p class="text-gray-600 text-sm">Lançado por:</p>
                  <p class="font-medium text-purple-800">{{ usuario?.nome || 'N/A' }}</p>
                </div>
              </div>
            </div>
          </BaseCard>

          <!-- 🐕 Informações do Animal -->
          <BaseCard class="shadow-xl border-0">
            <div class="flex items-center gap-3 mb-6">
              <div class="w-12 h-12 bg-gradient-to-br from-blue-400 to-purple-500 rounded-xl flex items-center justify-center">
                <FontAwesomeIcon :icon="['fas', 'paw']" class="text-white text-xl" />
              </div>
              <div>
                <h2 class="text-xl font-bold text-gray-800">Informações do Animal</h2>
                <p class="text-gray-600">Dados do pet atendido</p>
              </div>
            </div>

            <div v-if="animal" class="space-y-6">
              <!-- Info principal do animal -->
              <div class="flex items-center gap-4 p-4 bg-gradient-to-r from-blue-50 to-purple-50 rounded-xl border border-blue-200">
                <div class="w-16 h-16 bg-gradient-to-br from-blue-400 to-purple-500 rounded-xl flex items-center justify-center text-white text-2xl">
                  <FontAwesomeIcon :icon="getAnimalFontAwesomeIcon(animal.tipo)" />
                </div>
                <div class="flex-1">
                  <h3 class="text-xl font-bold text-gray-800">{{ animal.nome }}</h3>
                  <p class="text-gray-600">{{ animal.tipo }}</p>
                  <p class="text-sm text-gray-500">Código: {{ animal.codigoSimplesVet }}</p>
                </div>
                <BaseButton @click="verDetalhesAnimal" variant="ghost" size="sm">
                  <FontAwesomeIcon :icon="['fas', 'external-link-alt']" class="mr-1" />
                  Ver Detalhes
                </BaseButton>
              </div>

              <!-- Dados do cliente -->
              <div class="p-4 bg-gradient-to-r from-emerald-50 to-teal-50 rounded-xl border border-emerald-200">
                <div class="flex items-center gap-3 mb-3">
                  <FontAwesomeIcon :icon="['fas', 'user']" class="text-emerald-600" />
                  <div class="flex-1">
                    <p class="text-sm font-medium text-gray-600">Cliente Responsável</p>
                    <p class="text-lg font-semibold text-gray-800">{{ cliente?.nomeCompleto || 'N/A' }}</p>
                  </div>
                  <BaseButton v-if="cliente" @click="verDetalhesCliente" variant="ghost" size="sm">
                    <FontAwesomeIcon :icon="['fas', 'external-link-alt']" class="mr-1" />
                    Ver Cliente
                  </BaseButton>
                </div>

                <div v-if="cliente" class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
                  <div>
                    <p class="text-gray-600">CPF:</p>
                    <p class="font-medium">{{ formatarCpf(cliente.cpf) }}</p>
                  </div>
                  <div v-if="cliente.telefones && cliente.telefones.length > 0">
                    <p class="text-gray-600">Telefone:</p>
                    <p class="font-medium">{{ cliente.telefones?.[0]?.telefone ? formatarTelefone(cliente.telefones[0].telefone) : 'N/A' }}</p>
                  </div>
                  <div v-if="cliente.emailClientes && cliente.emailClientes.length > 0" class="sm:col-span-2">
                    <p class="text-gray-600">Email:</p>
                    <p class="font-medium">{{ cliente.emailClientes?.[0]?.email || 'N/A' }}</p>
                  </div>
                </div>
                <p v-else class="text-gray-500 text-sm">Cliente não encontrado</p>
              </div>
            </div>
            <div v-else class="text-center py-8">
              <FontAwesomeIcon :icon="['fas', 'exclamation-circle']" class="text-gray-400 text-3xl mb-2" />
              <p class="text-gray-500">Animal não encontrado</p>
            </div>
          </BaseCard>
        </div>

        <!-- 🛁 Histórico de Banhos (se for pacote) -->
        <div v-if="servico && servico.quantidade > 1" class="mt-8">
          <BaseCard class="shadow-xl border-0">
            <div class="flex items-center gap-3 mb-6">
              <div class="w-12 h-12 bg-gradient-to-br from-cyan-400 to-blue-500 rounded-xl flex items-center justify-center">
                <FontAwesomeIcon :icon="['fas', 'history']" class="text-white text-xl" />
              </div>
              <div>
                <h2 class="text-xl font-bold text-gray-800">Histórico de Banhos</h2>
                <p class="text-gray-600">Registro de banhos individuais do pacote</p>
              </div>
            </div>

            <div v-if="banhosIndividuais.length" class="space-y-3">
              <div
                v-for="banho in banhosIndividuais"
                :key="banho.id"
                class="flex items-center justify-between p-4 bg-gradient-to-r from-cyan-50 to-blue-50 rounded-xl border border-cyan-200"
              >
                <div class="flex items-center gap-3">
                  <div class="w-8 h-8 bg-gradient-to-br from-cyan-400 to-blue-500 rounded-full flex items-center justify-center text-white text-sm font-bold">
                    {{ banho.numeroBanho }}
                  </div>
                  <div>
                    <p class="font-semibold text-gray-800">{{ formatarData(banho.dataBanho) }}</p>
                    <p v-if="banho.observacoes" class="text-sm text-gray-600">{{ banho.observacoes }}</p>
                    <p v-else class="text-sm text-gray-500 italic">Sem observações</p>
                  </div>
                </div>
                <BaseButton @click="excluirBanho(banho)" variant="ghost" size="sm" class="text-red-600 hover:text-red-700">
                  <FontAwesomeIcon :icon="['fas', 'trash']" />
                </BaseButton>
              </div>
            </div>
            <div v-else class="text-center py-8">
              <FontAwesomeIcon :icon="['fas', 'bath']" class="text-gray-400 text-3xl mb-2" />
              <p class="text-gray-500">Nenhum banho individual registrado ainda</p>
            </div>
          </BaseCard>
        </div>
      </div>

      <!-- Estado não encontrado -->
      <div v-else class="text-center py-12">
        <div class="inline-flex items-center justify-center w-16 h-16 bg-gray-500 rounded-full mb-4">
          <FontAwesomeIcon :icon="['fas', 'search']" class="text-white text-2xl" />
        </div>
        <h3 class="text-xl font-semibold text-gray-800 mb-2">Serviço não encontrado</h3>
        <p class="text-gray-600 mb-4">O registro solicitado não existe ou foi removido.</p>
        <BaseButton @click="voltarParaLista" variant="primary">
          <FontAwesomeIcon :icon="['fas', 'arrow-left']" class="mr-2" />
          Voltar para Lista
        </BaseButton>
      </div>
    </div>

    <!-- 🗓️ Modal de Edição de Data -->
    <BaseModal v-model="editarData" title="Editar Data do Serviço" size="md">
      <div class="space-y-6">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Nova Data</label>
          <input
            type="date"
            v-model="novaData"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500"
          />
        </div>

        <div class="flex gap-3 pt-4 border-t">
          <BaseButton @click="editarData = false" variant="ghost" class="flex-1">
            Cancelar
          </BaseButton>
          <BaseButton @click="salvarNovaData" variant="primary" class="flex-1" :disabled="salvandoData">
            <FontAwesomeIcon :icon="['fas', 'spinner']" v-if="salvandoData" class="mr-2 animate-spin" />
            <FontAwesomeIcon :icon="['fas', 'save']" v-else class="mr-2" />
            {{ salvandoData ? 'Salvando...' : 'Salvar' }}
          </BaseButton>
        </div>
      </div>
    </BaseModal>

    <!-- 🛁 Modal de Banho Individual -->
    <BaseModal v-model="mostrarModalBanho" title="Registrar Banho Individual" size="md">
      <div class="space-y-6">
        <p class="text-gray-700 text-lg font-semibold">
          Registrando banho para: <span class="text-green-600">{{ animal?.nome }}</span>
        </p>
        <p class="text-gray-600 text-md">
          Próximo banho: <span class="font-bold text-lg text-blue-600">{{ (animalServico?.banhosUsados || 0) + 1 }}</span>
        </p>

        <div class="space-y-4">
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <FontAwesomeIcon :icon="['fas', 'calendar-alt']" class="mr-2 text-blue-600" />
              Data do Banho *
            </label>
            <input
              type="date"
              v-model="formularioBanho.dataBanho"
              required
              class="w-full px-4 py-3 bg-white border-2 border-gray-200 rounded-xl focus:border-blue-500 focus:ring-4 focus:ring-blue-100 transition-all duration-300 hover:border-gray-300"
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

        <div class="flex flex-col sm:flex-row gap-3 pt-4 border-t">
          <BaseButton @click="mostrarModalBanho = false" variant="ghost" class="w-full sm:w-auto">
            Cancelar
          </BaseButton>
          <BaseButton
            @click="salvarBanhoIndividual"
            variant="primary"
            :disabled="!formularioBanho.dataBanho || salvandoBanho"
            class="w-full sm:w-auto"
          >
            <FontAwesomeIcon v-if="salvandoBanho" :icon="['fas', 'spinner']" class="mr-2 animate-spin" />
            <FontAwesomeIcon v-else :icon="['fas', 'bath']" class="mr-2" />
            Registrar Banho
          </BaseButton>
        </div>
      </div>
    </BaseModal>

    <!-- 🌊 Wave Footer -->
    <div class="fixed bottom-0 left-0 right-0 w-full overflow-hidden z-10 pointer-events-none">
      <svg viewBox="0 0 1440 120" class="w-full h-20 md:h-24" preserveAspectRatio="none">
        <defs>
          <linearGradient id="detalhesWave" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" style="stop-color:#F59E0B;stop-opacity:0.9" />
            <stop offset="50%" style="stop-color:#10B981;stop-opacity:0.8" />
            <stop offset="100%" style="stop-color:#047857;stop-opacity:1" />
          </linearGradient>
        </defs>
        <path
          d="M0,60 C120,45 240,30 360,45 C480,60 600,75 720,60 C840,45 960,30 1080,45 C1200,60 1320,75 1440,60 L1440,120 L0,120 Z"
          fill="url(#detalhesWave)"
        />
      </svg>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseButton from '@/components/UI/BaseButton.vue'
import BaseModal from '@/components/UI/BaseModal.vue'
import BaseBadge from '@/components/UI/BaseBadge.vue'
import { animalServicoService, animaisService, servicosService, clientesService, usuariosService, banhosIndividuaisService, type NovoBanhoIndividual, type BanhoIndividual } from '@/services/api'
import type { AnimalServico, Animal, ServicoCompleto, Cliente, Usuario } from '@/types/api'
import { formatarCpf, formatarTelefone, getIconeTipoAnimal } from '@/utils/formatters'

// Props
interface Props {
  id: string
}
const props = defineProps<Props>()

// Router
const router = useRouter()

// Estados
const loading = ref(true)
const error = ref('')
const animalServico = ref<AnimalServico | null>(null)
const animal = ref<Animal | null>(null)
const servico = ref<ServicoCompleto | null>(null)
const cliente = ref<Cliente | null>(null)
const usuario = ref<Usuario | null>(null)
const banhosIndividuais = ref<BanhoIndividual[]>([])

// Estados de edição
const editarData = ref(false)
const novaData = ref('')
const salvandoData = ref(false)

// Modal de banho
const mostrarModalBanho = ref(false)
const salvandoBanho = ref(false)
const formularioBanho = ref({
  dataBanho: '',
  observacoes: ''
})

// Computadas
const totalBanhos = computed(() => servico.value?.quantidade || 1)

const progressoBanhos = computed(() => {
  if (!animalServico.value) return 0
  return Math.min((animalServico.value.banhosUsados / totalBanhos.value) * 100, 100)
})

const isServicoCompleto = computed(() => {
  if (!animalServico.value) return false
  return animalServico.value.banhosUsados >= totalBanhos.value
})

const podeAdicionarBanho = computed(() => {
  return !isServicoCompleto.value && servico.value && servico.value.quantidade > 1
})

// Computadas para controle de expiração
const isPackageExpired = computed(() => {
  if (!animalServico.value?.dataExpiracao) return false

  const hoje = new Date()
  const [ano, mes, dia] = animalServico.value.dataExpiracao.split('-')
  const dataExpiracao = new Date(Number(ano), Number(mes) - 1, Number(dia))

  return hoje > dataExpiracao
})

const isPackageExpiringSoon = computed(() => {
  if (!animalServico.value?.dataExpiracao || isPackageExpired.value) return false

  const hoje = new Date()
  const [ano, mes, dia] = animalServico.value.dataExpiracao.split('-')
  const dataExpiracao = new Date(Number(ano), Number(mes) - 1, Number(dia))

  const diasParaExpirar = Math.ceil((dataExpiracao.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24))
  return diasParaExpirar <= 7 // Considera "expirando em breve" se for em 7 dias ou menos
})

const diasParaExpirar = computed(() => {
  if (!animalServico.value?.dataExpiracao) return 0

  const hoje = new Date()
  const [ano, mes, dia] = animalServico.value.dataExpiracao.split('-')
  const dataExpiracao = new Date(Number(ano), Number(mes) - 1, Number(dia))

  return Math.ceil((dataExpiracao.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24))
})

const diasVencido = computed(() => {
  if (!animalServico.value?.dataExpiracao || !isPackageExpired.value) return 0

  const hoje = new Date()
  const [ano, mes, dia] = animalServico.value.dataExpiracao.split('-')
  const dataExpiracao = new Date(Number(ano), Number(mes) - 1, Number(dia))

  return Math.abs(Math.ceil((hoje.getTime() - dataExpiracao.getTime()) / (1000 * 60 * 60 * 24)))
})

// Funções
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

const getAnimalIcon = (tipo: string) => {
  return getIconeTipoAnimal(tipo)
}

const getAnimalFontAwesomeIcon = (tipo: string): string[] => {
  const icones: Record<string, string[]> = {
    'Cachorro': ['fas', 'dog'],
    'Gato': ['fas', 'paw'],
    'Pássaro': ['fas', 'paw'],
    'Coelho': ['fas', 'paw'],
    'Hamster': ['fas', 'paw'],
    'Peixe': ['fas', 'paw'],
    'Tartaruga': ['fas', 'paw']
  }
  return icones[tipo] || ['fas', 'paw']
}

const carregarDados = async (): Promise<void> => {
  try {
    loading.value = true
    error.value = ''

    console.log('🔍 Carregando dados do Animal Serviço ID:', props.id)

    // Carregar animal serviço
    animalServico.value = await animalServicoService.buscarPorId(Number(props.id))

    if (!animalServico.value) {
      error.value = 'Serviço não encontrado'
      return
    }

    // Carregar dados relacionados
    const [animaisData, servicosData, clientesData, usuariosData] = await Promise.all([
      animaisService.buscarTodos(),
      servicosService.buscarTodos(),
      clientesService.buscarTodos(),
      usuariosService.buscarTodos()
    ])

    // Buscar dados específicos - busca reversa devido ao @JsonBackReference
    animal.value = animaisData.find(a =>
      a.servicos?.some(s => s.id === animalServico.value?.id)
    ) || null

    // Tentar múltiplas abordagens para encontrar o serviço
    console.log('🔍 AnimalServico servicoId:', animalServico.value?.servicoId)

    // Primeiro: tentar usar servicoId se disponível
    if (animalServico.value?.servicoId) {
      servico.value = servicosData.find(s => s.id === animalServico.value?.servicoId) || null
    }

    // Segundo: busca reversa através dos servicosAnimais
    if (!servico.value) {
      servico.value = servicosData.find(s =>
        s.servicosAnimais?.some(sa => sa.id === animalServico.value?.id)
      ) || null
    }

    // Terceiro: busca reversa através do animal (se o animal tem serviços)
    if (!servico.value && animal.value) {
      const animalServicoNoAnimal = animal.value.servicos?.find(s => s.id === animalServico.value?.id)
      if (animalServicoNoAnimal?.servico) {
        servico.value = animalServicoNoAnimal.servico
      }
    }

    if (animal.value) {
      cliente.value = clientesData.find(c =>
        c.animais?.some(a => a.id === animal.value?.id)
      ) || null
    }

    // 🔥 CORREÇÃO: usar busca reversa igual à lista (mesma abordagem do AnimalServicoView)
    usuario.value = usuariosData.find(u =>
      u.animalServicos?.some(as => as.id === animalServico.value?.id)
    ) || null

    // Se for pacote, carregar banhos individuais
    if (servico.value && servico.value.quantidade > 1) {
      banhosIndividuais.value = await banhosIndividuaisService.buscarPorAnimalServico(animalServico.value.id)
    }

    // Definir data atual para edição
    novaData.value = animalServico.value.dataServico

    console.log('✅ Dados carregados:', {
      animalServico: animalServico.value,
      animal: animal.value,
      servico: servico.value,
      cliente: cliente.value,
      banhosIndividuais: banhosIndividuais.value.length
    })

    // Debug: mostrar estrutura dos dados para diagnosticar problemas
    console.log('🔍 Debug AnimalServico:', animalServico.value)
    console.log('🔍 Debug Animal encontrado:', animal.value)
    console.log('🔍 Debug Serviço encontrado:', servico.value)
    console.log('🔍 Debug Todos os serviços:', servicosData.map(s => ({ id: s.id, nome: s.nome, servicosAnimais: s.servicosAnimais?.length })))

  } catch (err) {
    console.error('❌ Erro ao carregar dados:', err)
    error.value = 'Erro ao carregar os dados. Tente novamente.'
  } finally {
    loading.value = false
  }
}

const voltarParaLista = (): void => {
  router.push('/animal-servico')
}

const verDetalhesAnimal = (): void => {
  if (animal.value) {
    router.push(`/animais/${animal.value.id}/editar`)
  }
}

const verDetalhesCliente = (): void => {
  if (cliente.value) {
    router.push(`/clientes/${cliente.value.id}/editar`)
  }
}

const salvarNovaData = async (): Promise<void> => {
  if (!animalServico.value || !novaData.value) return

  try {
    salvandoData.value = true

    const dadosAtualizacao = {
      ...animalServico.value,
      dataServico: novaData.value
    }

    await animalServicoService.atualizar(animalServico.value.id, dadosAtualizacao)

    animalServico.value.dataServico = novaData.value
    editarData.value = false

    console.log('✅ Data atualizada com sucesso!')

  } catch (err) {
    console.error('❌ Erro ao atualizar data:', err)
    alert('Erro ao atualizar a data. Tente novamente.')
  } finally {
    salvandoData.value = false
  }
}

// 🗑️ Funções de exclusão do animal serviço
const confirmarExclusaoAnimalServico = (): void => {
  if (!animalServico.value || !animal.value || !servico.value) return

  const confirmacao = window.confirm(
    `🗑️ Tem certeza que deseja excluir este animal serviço?\n\n` +
    `Animal: ${animal.value.nome}\n` +
    `Serviço: ${servico.value.nome}\n` +
    `Data: ${formatarData(animalServico.value.dataServico)}\n` +
    `Banhos utilizados: ${animalServico.value.banhosUsados}/${servico.value.quantidade}\n\n` +
    `⚠️ ATENÇÃO: Esta ação não poderá ser desfeita!\n` +
    `Todos os banhos individuais relacionados também serão excluídos.`
  )

  if (confirmacao) {
    excluirAnimalServico()
  }
}

const excluirAnimalServico = async (): Promise<void> => {
  if (!animalServico.value) return

  try {
    loading.value = true
    console.log(`🗑️ Excluindo animal serviço ID ${animalServico.value.id}...`)

    await animalServicoService.excluir(animalServico.value.id)

    console.log('✅ Animal serviço excluído com sucesso!')

    // Mostrar feedback de sucesso
    alert(`✅ Animal serviço de "${animal.value?.nome}" foi excluído com sucesso!\n\nVocê será redirecionado para a lista.`)

    // Redirecionar para lista
    voltarParaLista()

  } catch (err) {
    console.error('❌ Erro ao excluir animal serviço:', err)
    alert(`❌ Erro ao excluir animal serviço: ${err instanceof Error ? err.message : 'Erro desconhecido'}\n\nTente novamente.`)
  } finally {
    loading.value = false
  }
}

const salvarBanhoIndividual = async (): Promise<void> => {
  if (!animalServico.value || !formularioBanho.value.dataBanho) return

  try {
    salvandoBanho.value = true

    const dadosBanho = {
      animalServicoId: animalServico.value.id,
      dataBanho: formularioBanho.value.dataBanho,
      observacoes: formularioBanho.value.observacoes || null,
      usuarioId: null
    }

    console.log('🛁 Registrando banho:', { animalServicoId: animalServico.value.id, data: dadosBanho.dataBanho })

    await banhosIndividuaisService.criar(dadosBanho)

    // Atualizar contador local
    animalServico.value.banhosUsados = animalServico.value.banhosUsados + 1

    // Recarregar banhos individuais
    if (servico.value && servico.value.quantidade > 1) {
      banhosIndividuais.value = await banhosIndividuaisService.buscarPorAnimalServico(animalServico.value.id)
    }

    mostrarModalBanho.value = false
    formularioBanho.value = { dataBanho: '', observacoes: '' }

    console.log('✅ Banho registrado com sucesso!')

  } catch (err) {
    console.error('❌ Erro ao registrar banho:', err)
    alert('Erro ao registrar banho. Tente novamente.')
  } finally {
    salvandoBanho.value = false
  }
}

const excluirBanho = async (banho: BanhoIndividual): Promise<void> => {
  if (!animalServico.value) return

  if (!confirm(`Deseja realmente excluir o banho ${banho.numeroBanho}?`)) return

  try {
    await banhosIndividuaisService.excluir(banho.id)

    // Atualizar contador local
    animalServico.value.banhosUsados = Math.max(0, animalServico.value.banhosUsados - 1)

    // Recarregar banhos individuais
    banhosIndividuais.value = await banhosIndividuaisService.buscarPorAnimalServico(animalServico.value.id)

    console.log('✅ Banho excluído com sucesso!')

  } catch (err) {
    console.error('❌ Erro ao excluir banho:', err)
    alert('Erro ao excluir banho. Tente novamente.')
  }
}

// Lifecycle
onMounted(() => {
  carregarDados()

  // Definir data padrão para novo banho
  formularioBanho.value.dataBanho = new Date().toISOString().split('T')[0] || ''
})
</script>

<style scoped>
/* Animações personalizadas */
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

@keyframes bounce-gentle {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

.animate-bounce-gentle {
  animation: bounce-gentle 2s infinite;
}
</style>
