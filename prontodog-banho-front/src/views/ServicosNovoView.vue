<!-- 💼 Página de Cadastro de Serviços -->
<!-- Visual profissional e corporativo com cores amarelo/verde -->
<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 🏢 Header Profissional e Corporativo -->
    <div class="relative overflow-hidden bg-gradient-to-r from-amber-700 via-yellow-600 to-amber-800 text-white pt-16">
      <!-- ✨ Efeito de brilho corporativo -->
      <div class="absolute inset-0 opacity-10">
        <div class="absolute inset-0 bg-gradient-to-r from-transparent via-white/20 to-transparent transform -skew-x-12 animate-pulse"></div>
      </div>

      <div class="relative z-20 max-w-4xl mx-auto px-4 py-12">
        <div class="text-center space-y-4">
          <div class="flex justify-center mb-6">
            <div class="relative">
              <div class="w-20 h-20 bg-gradient-to-br from-yellow-400 to-amber-500 rounded-3xl flex items-center justify-center shadow-2xl">
                <FontAwesomeIcon
                  :icon="['fas', 'cog']"
                  class="text-3xl text-white drop-shadow-lg transform rotate-12 animate-bounce-gentle"
                />
              </div>
              <!-- Indicador de "novo" -->
              <div class="absolute -top-2 -right-2 w-8 h-8 bg-gradient-to-r from-green-500 to-emerald-600 rounded-full flex items-center justify-center shadow-lg">
                <FontAwesomeIcon :icon="['fas', 'plus']" class="text-sm text-white animate-pulse" />
              </div>
            </div>
          </div>

          <div class="space-y-2">
            <h1 class="text-4xl font-bold">
              <FontAwesomeIcon :icon="['fas', 'cog']" class="text-yellow-300 mr-2 animate-twinkle" />
              Cadastro de Serviços
            </h1>
            <p class="text-xl text-white opacity-90 font-medium">
              Registre novos serviços profissionais no sistema
            </p>
          </div>
        </div>
      </div>

      <!-- 🌊 Wave Decoration (Verde para contraste) -->
      <div class="absolute bottom-0 left-0 right-0 z-10">
        <svg viewBox="0 0 1440 120" class="w-full h-12">
          <defs>
            <linearGradient id="waveGradient" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" style="stop-color:#10b981;stop-opacity:1" />
              <stop offset="50%" style="stop-color:#059669;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#047857;stop-opacity:1" />
            </linearGradient>
          </defs>
          <path d="M0 60h1440V0c-120 40-240 60-360 60S840 40 720 60s-240 0-360-60S120 40 0 60z" fill="url(#waveGradient)"></path>
        </svg>
      </div>
    </div>

    <!-- 💼 Formulário Principal -->
    <div class="max-w-4xl mx-auto px-4 py-8">
      <form @submit.prevent="submeterFormulario" class="space-y-8">

        <!-- 📋 Seção: Informações Básicas -->
        <div class="bg-gradient-to-r from-white via-amber-50/30 to-yellow-50/20 rounded-2xl shadow-lg border border-amber-200/50 overflow-hidden animate-fade-in-up">
          <div class="bg-gradient-to-r from-amber-600 to-yellow-700 px-6 py-4">
            <h2 class="text-xl font-bold text-white flex items-center gap-3">
              <div class="w-8 h-8 bg-white/20 rounded-lg flex items-center justify-center">
                <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-white" />
              </div>
              Informações do Serviço
            </h2>
            <p class="text-amber-100 text-sm mt-1">Dados principais do serviço oferecido</p>
          </div>

          <div class="p-6 space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- Nome do Serviço -->
              <div class="animate-slide-up">
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  <FontAwesomeIcon :icon="['fas', 'cog']" class="text-amber-600 mr-2" />
                  Nome do Serviço *
                </label>
                <input
                  v-model="formulario.nome"
                  type="text"
                  placeholder="Ex: Banho e Tosa Completa"
                  required
                  class="w-full px-4 py-3 border-2 border-amber-200 rounded-xl focus:border-amber-500 focus:ring-4 focus:ring-amber-500/20 transition-all duration-300 bg-white"
                  :class="{ 'border-red-300 focus:border-red-500': erros.nome }"
                >
                <div class="flex justify-between items-center mt-1">
                  <span v-if="erros.nome" class="text-sm text-red-600">{{ erros.nome }}</span>
                  <span class="text-xs text-gray-500 ml-auto">Nome claro e descritivo do serviço</span>
                </div>
              </div>

              <!-- Tipo de Serviço (Select) -->
              <div class="animate-slide-up">
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  <FontAwesomeIcon :icon="['fas', 'tags']" class="text-amber-600 mr-2" />
                  Tipo de Serviço
                </label>
                <select
                  v-model="formulario.nome"
                  class="w-full px-4 py-3 border-2 border-amber-200 rounded-xl focus:border-amber-500 focus:ring-4 focus:ring-amber-500/20 transition-all duration-300 bg-white"
                  @change="preencherDescricaoAutomatica"
                >
                  <option value="">Selecione ou digite um tipo</option>
                  <option v-for="tipo in tiposServicos" :key="tipo" :value="tipo">
                    {{ tipo }}
                  </option>
                </select>
                <p class="text-xs text-gray-500 mt-1">Selecione um tipo pré-definido ou digite um personalizado</p>
              </div>
            </div>

            <!-- Descrição -->
            <div class="animate-slide-up">
              <label class="block text-sm font-medium text-gray-700 mb-2">
                <FontAwesomeIcon :icon="['fas', 'align-left']" class="text-amber-600 mr-2" />
                Descrição Detalhada
              </label>
              <textarea
                v-model="formulario.descricao"
                rows="4"
                class="w-full px-4 py-3 border-2 border-amber-200 rounded-xl focus:border-amber-500 focus:ring-4 focus:ring-amber-500/20 transition-all duration-300 bg-white resize-none"
                placeholder="Descreva detalhadamente o serviço oferecido, incluindo o que está incluso..."
                :class="{ 'border-red-300 focus:border-red-500': erros.descricao }"
              />
              <div class="flex justify-between items-center mt-1">
                <span v-if="erros.descricao" class="text-sm text-red-600">{{ erros.descricao }}</span>
                <span class="text-xs text-gray-500 ml-auto">{{ formulario.descricao.length }}/500 caracteres</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 💰 Seção: Valores e Quantidade -->
        <div class="bg-gradient-to-r from-white via-green-50/30 to-emerald-50/20 rounded-2xl shadow-lg border border-green-200/50 overflow-hidden animate-fade-in-up">
          <div class="bg-gradient-to-r from-green-600 to-emerald-700 px-6 py-4">
            <h2 class="text-xl font-bold text-white flex items-center gap-3">
              <div class="w-8 h-8 bg-white/20 rounded-lg flex items-center justify-center">
                <FontAwesomeIcon :icon="['fas', 'dollar-sign']" class="text-white" />
              </div>
              Precificação e Disponibilidade
            </h2>
            <p class="text-green-100 text-sm mt-1">Configure valores e quantidades disponíveis</p>
          </div>

          <div class="p-6 space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- Valor Unitário -->
              <div class="animate-slide-up">
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  <FontAwesomeIcon :icon="['fas', 'dollar-sign']" class="text-green-600 mr-2" />
                  Valor por Unidade
                </label>
                <div class="relative">
                  <span class="absolute left-4 top-1/2 transform -translate-y-1/2 text-green-600 font-medium">R$</span>
                  <input
                    v-model="formulario.valor"
                    type="number"
                    step="0.01"
                    min="0"
                    placeholder="0,00"
                    class="w-full pl-12 pr-4 py-3 border-2 border-green-200 rounded-xl focus:border-green-500 focus:ring-4 focus:ring-green-500/20 transition-all duration-300 bg-white"
                    :class="{ 'border-red-300 focus:border-red-500': erros.valor }"
                    required
                  >
                </div>
                <div class="flex justify-between items-center mt-1">
                  <span v-if="erros.valor" class="text-sm text-red-600">{{ erros.valor }}</span>
                  <span class="text-xs text-green-600 ml-auto" v-if="formulario.valor">
                    Valor formatado: R$ {{ formatarValor(formulario.valor) }}
                  </span>
                </div>
              </div>

              <!-- Quantidade Disponível -->
              <div class="animate-slide-up">
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  <FontAwesomeIcon :icon="['fas', 'box']" class="text-green-600 mr-2" />
                  Quantidade Disponível *
                </label>
                <input
                  v-model="formulario.quantidade"
                  type="number"
                  min="1"
                  placeholder="Ex: 100"
                  required
                  class="w-full px-4 py-3 border-2 border-green-200 rounded-xl focus:border-green-500 focus:ring-4 focus:ring-green-500/20 transition-all duration-300 bg-white"
                  :class="{ 'border-red-300 focus:border-red-500': erros.quantidade }"
                >
                <div class="flex justify-between items-center mt-1">
                  <span v-if="erros.quantidade" class="text-sm text-red-600">{{ erros.quantidade }}</span>
                  <span class="text-xs text-gray-500 ml-auto">Quantidade total de serviços disponíveis</span>
                </div>
              </div>
            </div>

            <!-- Cálculo Automático -->
            <div v-if="formulario.valor && formulario.quantidade" class="bg-gradient-to-r from-yellow-50 to-amber-50 border border-yellow-200 rounded-xl p-4 animate-slide-up">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 bg-gradient-to-br from-yellow-400 to-amber-500 rounded-xl flex items-center justify-center">
                    <FontAwesomeIcon :icon="['fas', 'calculator']" class="text-white" />
                  </div>
                  <div>
                    <h4 class="font-semibold text-gray-800">Valor Total do Estoque</h4>
                    <p class="text-sm text-gray-600">{{ formulario.quantidade }}x de R$ {{ formatarValor(formulario.valor) }}</p>
                  </div>
                </div>
                <div class="text-right">
                  <div class="text-2xl font-bold text-green-600">
                    R$ {{ formatarValor(formulario.valor * formulario.quantidade) }}
                  </div>
                  <div class="text-sm text-gray-500">valor total</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 📋 Informações Importantes -->
        <BaseCard class="bg-gradient-to-r from-blue-50 to-indigo-50 border-blue-200 animate-fade-in-up">
          <div class="flex gap-4">
            <div class="flex-shrink-0">
              <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center">
                <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-white text-lg" />
              </div>
            </div>
            <div>
              <h3 class="font-semibold text-blue-800 mb-2">Informações Importantes</h3>
              <ul class="space-y-2 text-sm text-blue-700">
                <li class="flex items-center gap-2">
                  <div class="w-1.5 h-1.5 bg-blue-500 rounded-full"></div>
                  Todos os campos marcados com * são obrigatórios
                </li>
                <li class="flex items-center gap-2">
                  <div class="w-1.5 h-1.5 bg-green-500 rounded-full"></div>
                  O valor deve ser informado em reais (R$)
                </li>
                <li class="flex items-center gap-2">
                  <div class="w-1.5 h-1.5 bg-yellow-500 rounded-full"></div>
                  A quantidade representa unidades disponíveis para venda
                </li>
                <li class="flex items-center gap-2">
                  <div class="w-1.5 h-1.5 bg-purple-500 rounded-full"></div>
                  Você pode editar essas informações após o cadastro
                </li>
              </ul>
            </div>
          </div>
        </BaseCard>

        <!-- 🎯 Botões de Ação -->
        <div class="flex flex-col sm:flex-row gap-4 justify-end animate-fade-in-up">
          <RouterLink to="/servicos">
            <BaseButton
              type="button"
              variant="secondary"
              class="w-full sm:w-auto px-8 py-3 text-lg font-medium border-2 border-gray-300 hover:border-gray-400 bg-white hover:bg-gray-50"
            >
              <FontAwesomeIcon :icon="['fas', 'arrow-left']" class="mr-2" />
              Cancelar
            </BaseButton>
          </RouterLink>

          <BaseButton
            type="submit"
            variant="primary"
            :disabled="loading"
            class="w-full sm:w-auto bg-gradient-to-r from-green-600 to-emerald-700 hover:from-green-700 hover:to-emerald-800 px-8 py-3 text-lg font-medium shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 border-0"
          >
            <FontAwesomeIcon
              v-if="loading"
              :icon="['fas', 'spinner']"
              class="mr-2 animate-spin"
            />
            <FontAwesomeIcon
              v-else
              :icon="['fas', 'save']"
              class="mr-2"
            />
            {{ loading ? 'Cadastrando...' : 'Cadastrar Serviço' }}
          </BaseButton>
        </div>

        <!-- ⚠️ Mensagem de Status -->
        <div v-if="formStatus && formStatus.icon !== 'check-circle'" class="animate-slide-up">
          <BaseCard
            :class="{
              'bg-gradient-to-r from-red-50 to-pink-50 border-red-200': formStatus.icon === 'exclamation-triangle',
              'bg-gradient-to-r from-blue-50 to-indigo-50 border-blue-200': formStatus.icon === 'info-circle'
            }"
          >
            <div class="flex items-center gap-4">
              <div
                :class="{
                  'w-12 h-12 bg-gradient-to-br from-red-500 to-pink-600 rounded-xl flex items-center justify-center': formStatus.icon === 'exclamation-triangle',
                  'w-12 h-12 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-xl flex items-center justify-center': formStatus.icon === 'info-circle'
                }"
              >
                <FontAwesomeIcon :icon="['fas', formStatus.icon]" class="text-white text-lg" />
              </div>
              <div>
                <h4
                  :class="{
                    'font-semibold text-red-700': formStatus.icon === 'exclamation-triangle',
                    'font-semibold text-blue-700': formStatus.icon === 'info-circle'
                  }"
                >
                  {{ formStatus.title }}
                </h4>
                <p
                  :class="{
                    'text-red-600': formStatus.icon === 'exclamation-triangle',
                    'text-blue-600': formStatus.icon === 'info-circle'
                  }"
                >
                  {{ formStatus.message }}
                </p>
              </div>
            </div>
          </BaseCard>
        </div>
      </form>
    </div>

    <!-- 🎉 Modal de Sucesso -->
    <BaseModal
      v-model="mostrarSucesso"
      title="Serviço Cadastrado!"
    >
      <div class="text-center space-y-6 p-6">
        <!-- Ícone de sucesso animado -->
        <div class="relative mx-auto w-24 h-24">
          <div class="absolute inset-0 bg-gradient-to-r from-green-400 to-emerald-500 rounded-full animate-pulse"></div>
          <div class="relative w-24 h-24 bg-gradient-to-br from-green-500 to-emerald-600 rounded-full flex items-center justify-center shadow-2xl">
            <FontAwesomeIcon :icon="['fas', 'check']" class="text-3xl text-white animate-bounce" />
          </div>
          <!-- Confetti effect -->
          <div class="absolute -inset-4 opacity-75">
            <div class="absolute top-2 left-2 w-2 h-2 bg-yellow-400 rounded-full animate-bounce" style="animation-delay: 0.1s;"></div>
            <div class="absolute top-4 right-3 w-1.5 h-1.5 bg-blue-400 rounded-full animate-bounce" style="animation-delay: 0.3s;"></div>
            <div class="absolute bottom-3 left-4 w-2 h-2 bg-pink-400 rounded-full animate-bounce" style="animation-delay: 0.2s;"></div>
            <div class="absolute bottom-2 right-2 w-1.5 h-1.5 bg-purple-400 rounded-full animate-bounce" style="animation-delay: 0.4s;"></div>
          </div>
        </div>

        <!-- Mensagem de sucesso -->
        <div>
          <h2 class="text-2xl font-bold text-gray-800 mb-2">Serviço Cadastrado com Sucesso!</h2>
          <p class="text-gray-600 mb-6">O serviço foi registrado no sistema e já está disponível.</p>
        </div>

        <!-- Detalhes do serviço criado -->
        <div v-if="servicoCriado" class="bg-gradient-to-r from-gray-50 to-green-50 rounded-xl p-4 border border-green-200">
          <div class="space-y-3">
            <div class="flex items-center justify-between">
              <span class="text-sm font-medium text-gray-600">Nome:</span>
              <span class="font-semibold text-gray-800">{{ servicoCriado.nome }}</span>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm font-medium text-gray-600">Valor:</span>
              <span class="font-semibold text-green-600">R$ {{ formatarValor(servicoCriado.valor) }}</span>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm font-medium text-gray-600">Quantidade:</span>
              <span class="font-semibold text-blue-600">{{ servicoCriado.quantidade }} unidades</span>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm font-medium text-gray-600">ID do Serviço:</span>
              <span class="font-mono text-sm bg-gray-100 px-2 py-1 rounded">#{{ servicoCriado.id }}</span>
            </div>
          </div>
        </div>

        <!-- Botões do modal -->
        <div class="flex flex-col sm:flex-row gap-3 justify-center">
          <BaseButton
            @click="adicionarOutroServico"
            variant="secondary"
            class="bg-gradient-to-r from-blue-600 to-indigo-700 hover:from-blue-700 hover:to-indigo-800 text-white border-0"
          >
            <FontAwesomeIcon :icon="['fas', 'plus']" class="mr-2" />
            Cadastrar Outro
          </BaseButton>

          <BaseButton
            @click="voltarParaLista"
            variant="primary"
            class="bg-gradient-to-r from-green-600 to-emerald-700 hover:from-green-700 hover:to-emerald-800 border-0"
          >
            <FontAwesomeIcon :icon="['fas', 'list']" class="mr-2" />
            Ver Lista de Serviços
          </BaseButton>
        </div>
      </div>
    </BaseModal>

    <!-- 🔄 Loading Overlay -->
    <div v-if="loading" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl p-8 shadow-2xl max-w-sm w-full mx-4">
        <div class="text-center space-y-4">
          <div class="relative mx-auto w-16 h-16">
            <div class="absolute inset-0 bg-gradient-to-r from-amber-400 to-yellow-500 rounded-2xl animate-pulse"></div>
            <div class="relative w-16 h-16 bg-gradient-to-br from-amber-500 to-yellow-600 rounded-2xl flex items-center justify-center shadow-xl">
              <FontAwesomeIcon :icon="['fas', 'cog']" class="text-2xl text-white animate-spin" />
            </div>
          </div>
          <div>
            <h3 class="text-lg font-semibold text-gray-800">Cadastrando Serviço</h3>
            <p class="text-gray-600">Processando informações...</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { BaseCard, BaseButton, BaseInput, BaseModal } from '@/components/UI'
import { servicosService } from '@/services/api'
import { TIPOS_SERVICOS } from '@/types/api'
import type { NovoServico, ServicoCompleto } from '@/types/api'

// 🎯 Configuração inicial
const router = useRouter()

// 📋 Estados do formulário
const loading = ref(false)
const mostrarSucesso = ref(false)
const servicoCriado = ref<ServicoCompleto | null>(null)

// 📝 Dados do formulário
const formulario = reactive({
  nome: '',
  descricao: '',
  quantidade: 1,
  valor: 0
})

// ⚠️ Controle de erros
const erros = reactive({
  nome: '',
  descricao: '',
  quantidade: '',
  valor: ''
})

// 📊 Status do formulário
const formStatus = ref<{
  icon: string
  title: string
  message: string
} | null>(null)

// 💡 Computed properties
const tiposServicos = computed(() => TIPOS_SERVICOS)

// 🔧 Funções utilitárias
const formatarValor = (valor: number): string => {
  return valor.toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const preencherDescricaoAutomatica = () => {
  if (!formulario.descricao && formulario.nome) {
    const descricoesPadrao: Record<string, string> = {
      'Banho Simples': 'Banho com shampoo neutro, secagem e escovação básica.',
      'Banho e Tosa': 'Banho completo, tosa higiênica, corte de unhas e limpeza de ouvidos.',
      'Tosa Higiênica': 'Tosa das áreas íntimas e patas para maior higiene.',
      'Tosa Completa': 'Tosa artística completa conforme padrão da raça ou preferência do cliente.',
      'Escovação de Pelos': 'Escovação profissional para remoção de pelos mortos e desembaraço.',
      'Corte de Unhas': 'Corte seguro das unhas com equipamento profissional.',
      'Limpeza de Ouvidos': 'Limpeza e higienização dos ouvidos com produtos específicos.',
      'Escovação Dental': 'Escovação dental para prevenir tártaro e mau hálito.',
      'Hidratação': 'Tratamento hidratante para pelos ressecados e pele sensível.',
      'Perfume': 'Aplicação de perfume específico para pets de longa duração.',
      'Transporte': 'Serviço de busca e entrega do pet no domicílio do cliente.'
    }

    formulario.descricao = descricoesPadrao[formulario.nome] || ''
  }
}

const validarFormulario = (): boolean => {
  // Reset erros
  Object.keys(erros).forEach(key => {
    erros[key as keyof typeof erros] = ''
  })

  let valido = true

  // Validar nome
  if (!formulario.nome.trim()) {
    erros.nome = 'Nome do serviço é obrigatório'
    valido = false
  } else if (formulario.nome.length < 3) {
    erros.nome = 'Nome deve ter pelo menos 3 caracteres'
    valido = false
  }

  // Validar descrição
  if (formulario.descricao.length > 500) {
    erros.descricao = 'Descrição não pode exceder 500 caracteres'
    valido = false
  }

  // Validar quantidade
  if (!formulario.quantidade || formulario.quantidade < 1) {
    erros.quantidade = 'Quantidade deve ser pelo menos 1'
    valido = false
  }

  // Validar valor
  if (!formulario.valor || formulario.valor <= 0) {
    erros.valor = 'Valor deve ser maior que zero'
    valido = false
  }

  return valido
}

const limparFormulario = () => {
  formulario.nome = ''
  formulario.descricao = ''
  formulario.quantidade = 1
  formulario.valor = 0

  Object.keys(erros).forEach(key => {
    erros[key as keyof typeof erros] = ''
  })

  formStatus.value = null
}

// 🎯 Ações principais
const submeterFormulario = async () => {
  if (!validarFormulario()) {
    formStatus.value = {
      icon: 'exclamation-triangle',
      title: 'Erro de Validação',
      message: 'Por favor, corrija os erros no formulário antes de continuar.'
    }
    return
  }

  loading.value = true
  formStatus.value = null

  try {
    const dadosServico: NovoServico = {
      nome: formulario.nome.trim(),
      descricao: formulario.descricao.trim(),
      quantidade: Number(formulario.quantidade),
      valor: Number(formulario.valor)
    }

    const resultado = await servicosService.criar(dadosServico)

    servicoCriado.value = resultado
    mostrarSucesso.value = true
    limparFormulario()

  } catch (error) {
    console.error('❌ Erro ao criar serviço:', error)
    formStatus.value = {
      icon: 'exclamation-triangle',
      title: 'Erro ao Cadastrar',
      message: error instanceof Error ? error.message : 'Erro inesperado ao cadastrar serviço.'
    }
  } finally {
    loading.value = false
  }
}

const adicionarOutroServico = () => {
  mostrarSucesso.value = false
  servicoCriado.value = null
  limparFormulario()
}

const voltarParaLista = () => {
  router.push('/servicos')
}
</script>

<style scoped>
/* Garante que as animações funcionem suavemente */
input[type="number"]::-webkit-outer-spin-button,
input[type="number"]::-webkit-inner-spin-button {
  -webkit-appearance: none;
  appearance: none;
  margin: 0;
}

input[type="number"] {
  -moz-appearance: textfield;
  appearance: textfield;
}
</style>
