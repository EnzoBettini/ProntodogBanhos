<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-indigo-100">
    <!-- 🌟 Header com gradiente elegante -->
    <div class="relative overflow-hidden bg-gradient-to-r from-blue-600 via-indigo-600 to-blue-700 text-white pt-16">
      <!-- Background pattern -->
      <div class="absolute inset-0 opacity-10 bg-pattern"></div>

      <div class="relative px-6 py-8 z-20">
        <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
          <div class="flex items-center gap-4 animate-fade-in-up">
            <!-- Ícone animado -->
            <div class="relative">
              <div class="w-16 h-16 bg-white bg-opacity-20 backdrop-blur-sm rounded-2xl flex items-center justify-center transform hover:scale-110 transition-all duration-300 shadow-lg">
                <FontAwesomeIcon icon="credit-card" class="text-2xl text-white animate-bounce-gentle" />
              </div>
              <div class="absolute -top-1 -right-1 w-6 h-6 bg-yellow-400 rounded-full flex items-center justify-center animate-pulse">
                <FontAwesomeIcon icon="plus" class="text-xs text-yellow-800" />
              </div>
            </div>

            <div class="space-y-1">
              <h1 class="text-4xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-blue-100">
                Nova Maquininha
              </h1>
              <p class="text-white text-lg flex items-center gap-2 font-medium opacity-90">
                <FontAwesomeIcon icon="wand-magic-sparkles" class="text-yellow-400 animate-twinkle mr-1" />
                Configure uma nova maquininha de cartão ({{ etapaAtual }}/4)
              </p>
            </div>
          </div>

          <!-- Botão voltar elegante -->
          <button
            @click="voltar"
            class="group flex items-center gap-2 px-6 py-3 bg-white bg-opacity-20 backdrop-blur-sm text-white rounded-xl hover:bg-opacity-30 transition-all duration-300 border border-white border-opacity-20 hover:border-opacity-40 transform hover:-translate-y-1 hover:shadow-lg"
          >
            <FontAwesomeIcon icon="arrow-left" class="group-hover:-translate-x-1 transition-transform duration-300" />
            <span class="font-medium">Voltar</span>
          </button>
        </div>

        <!-- Progresso do Wizard -->
        <div class="mt-8">
          <div class="flex items-center justify-center gap-2">
            <div
              v-for="step in 4"
              :key="step"
              class="flex items-center"
            >
              <div
                :class="[
                  'w-12 h-12 rounded-full flex items-center justify-center font-bold transition-all duration-300',
                  step === etapaAtual ? 'bg-yellow-400 text-yellow-900 scale-110 shadow-lg' :
                  step < etapaAtual ? 'bg-white text-blue-600' :
                  'bg-white bg-opacity-20 text-white border-2 border-white border-opacity-30'
                ]"
              >
                <FontAwesomeIcon v-if="step < etapaAtual" icon="check" />
                <span v-else>{{ step }}</span>
              </div>
              <div
                v-if="step < 4"
                :class="[
                  'w-16 h-1 mx-2 transition-all duration-300',
                  step < etapaAtual ? 'bg-white' : 'bg-white bg-opacity-20'
                ]"
              />
            </div>
          </div>
          <div class="text-center mt-4">
            <p class="text-white text-sm font-medium">
              {{ titulosEtapas[etapaAtual - 1] }}
            </p>
          </div>
        </div>
      </div>

      <!-- Wave decoration -->
      <div class="absolute bottom-0 left-0 right-0 z-10">
        <svg viewBox="0 0 1440 60" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full h-15">
          <path d="M0 60h1440V0c-120 40-240 60-360 60S840 40 720 60s-240 0-360-60S120 40 0 60z" fill="#3b82f6"/>
        </svg>
      </div>
    </div>

    <!-- 📝 Container do formulário -->
    <div class="px-6 pb-8 -mt-8 relative z-10">
      <div class="max-w-5xl mx-auto">
        <!-- Card principal com glassmorphism -->
        <div class="relative bg-white bg-opacity-80 backdrop-blur-xl rounded-3xl shadow-2xl border border-white border-opacity-20 overflow-hidden animate-slide-up">

          <!-- Loading overlay -->
          <div v-if="salvando" class="absolute inset-0 bg-gradient-to-br from-blue-500 to-indigo-600 bg-opacity-95 backdrop-blur-sm flex items-center justify-center z-50">
            <div class="text-center text-white">
              <div class="relative mb-6">
                <div class="w-20 h-20 border-4 border-white border-opacity-30 rounded-full animate-spin"></div>
                <div class="absolute top-0 left-0 w-20 h-20 border-4 border-transparent border-t-white rounded-full animate-spin"></div>
                <FontAwesomeIcon icon="credit-card" class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 text-2xl animate-bounce" />
              </div>
              <h3 class="text-xl font-semibold mb-2">Cadastrando maquininha...</h3>
              <p class="text-white text-opacity-80">Configurando taxas e prazos! 💳</p>
            </div>
          </div>

          <form @submit.prevent="salvar" class="p-8 space-y-8">

            <!-- ========== ETAPA 1: DADOS BÁSICOS ========== -->
            <div v-show="etapaAtual === 1" class="space-y-6 animate-fade-in-up">
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
                    <p class="text-xs text-gray-500 mt-1">Normalmente 1 dia útil</p>
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
                    <p class="text-xs text-gray-500 mt-1">Normalmente 30 dias</p>
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
                      <p class="text-xs text-gray-500 mt-1">Exemplo: 2.99% ao mês</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- ========== ETAPA 2: CONFIGURAÇÃO PIX ========== -->
            <div v-show="etapaAtual === 2" class="space-y-6 animate-fade-in-up">
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
                        <p class="text-xs text-gray-500 mt-1">Exemplo: 0.99%</p>
                      </div>
                      <div>
                        <label class="block text-sm font-medium text-gray-700 mb-2">Prazo Recebimento PIX (dias)</label>
                        <input
                          v-model.number="formData.prazoRecebimentoPix"
                          type="number"
                          min="0"
                          class="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:border-cyan-500 focus:ring-2 focus:ring-cyan-200 transition-all duration-200"
                        />
                        <p class="text-xs text-gray-500 mt-1">Normalmente 0 (imediato)</p>
                      </div>
                    </div>
                  </div>

                  <div v-else class="pl-8">
                    <div class="bg-gray-100 rounded-xl p-4 flex items-start gap-3">
                      <FontAwesomeIcon icon="info-circle" class="text-gray-400 mt-1" />
                      <div>
                        <p class="text-sm text-gray-600">
                          Desabilitando PIX nesta maquininha, você não conseguirá registrar pagamentos PIX por ela.
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- ========== ETAPA 3: SELEÇÃO DE BANDEIRAS ========== -->
            <div v-show="etapaAtual === 3" class="space-y-6 animate-fade-in-up">
              <div class="bg-gradient-to-r from-purple-50 to-pink-50 rounded-2xl p-6 border border-purple-100">
                <div class="flex items-center gap-3 mb-6">
                  <div class="w-10 h-10 bg-gradient-to-r from-purple-500 to-pink-500 rounded-xl flex items-center justify-center">
                    <FontAwesomeIcon icon="credit-card" class="text-white" />
                  </div>
                  <h2 class="text-xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">
                    Bandeiras Aceitas
                  </h2>
                </div>

                <p class="text-sm text-gray-600 mb-4">
                  Selecione quais bandeiras de cartão são aceitas nesta maquininha:
                </p>

                <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                  <div
                    v-for="bandeira in bandeiras"
                    :key="bandeira.id"
                    @click="toggleBandeira(bandeira.id)"
                    :class="[
                      'cursor-pointer p-4 rounded-xl border-2 transition-all duration-200 hover:shadow-md',
                      bandeirasSelecionadas.includes(bandeira.id) ?
                        'border-purple-500 bg-purple-50' :
                        'border-gray-200 hover:border-purple-300'
                    ]"
                  >
                    <div class="flex items-center gap-2">
                      <div
                        :class="[
                          'w-5 h-5 rounded flex items-center justify-center border-2 transition-all duration-200',
                          bandeirasSelecionadas.includes(bandeira.id) ?
                            'bg-purple-500 border-purple-500' :
                            'border-gray-300'
                        ]"
                      >
                        <FontAwesomeIcon v-if="bandeirasSelecionadas.includes(bandeira.id)" icon="check" class="text-white text-xs" />
                      </div>
                      <span class="font-medium text-gray-700">{{ bandeira.nome }}</span>
                    </div>
                  </div>
                </div>

                <div v-if="bandeirasSelecionadas.length === 0" class="mt-4 bg-yellow-50 border border-yellow-200 rounded-xl p-4 flex items-start gap-3">
                  <FontAwesomeIcon icon="exclamation-triangle" class="text-yellow-600 mt-1" />
                  <div>
                    <p class="text-sm text-yellow-800 font-medium">Nenhuma bandeira selecionada</p>
                    <p class="text-xs text-yellow-700 mt-1">Selecione pelo menos uma bandeira para continuar.</p>
                  </div>
                </div>

                <div v-else class="mt-4 bg-purple-50 border border-purple-200 rounded-xl p-4 flex items-start gap-3">
                  <FontAwesomeIcon icon="check-circle" class="text-purple-600 mt-1" />
                  <div>
                    <p class="text-sm text-purple-800 font-medium">
                      {{ bandeirasSelecionadas.length }} bandeira(s) selecionada(s)
                    </p>
                    <p class="text-xs text-purple-700 mt-1">
                      Na próxima etapa você configurará as taxas para cada uma.
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <!-- ========== ETAPA 4: CONFIGURAÇÃO DE TAXAS ========== -->
            <div v-show="etapaAtual === 4" class="space-y-6 animate-fade-in-up">
              <div class="bg-gradient-to-r from-red-50 to-orange-50 rounded-2xl p-6 border border-red-100">
                <div class="flex items-center gap-3 mb-6">
                  <div class="w-10 h-10 bg-gradient-to-r from-red-500 to-orange-500 rounded-xl flex items-center justify-center">
                    <FontAwesomeIcon icon="percent" class="text-white" />
                  </div>
                  <h2 class="text-xl font-bold bg-gradient-to-r from-red-600 to-orange-600 bg-clip-text text-transparent">
                    Configuração de Taxas
                  </h2>
                </div>

                <p class="text-sm text-gray-600 mb-6">
                  Configure as taxas para cada bandeira e tipo de transação:
                </p>

                <div v-if="bandeirasSelecionadas.length === 0" class="bg-yellow-50 border border-yellow-200 rounded-xl p-4 flex items-start gap-3">
                  <FontAwesomeIcon icon="exclamation-triangle" class="text-yellow-600 mt-1" />
                  <div>
                    <p class="text-sm text-yellow-800 font-medium">Volte e selecione bandeiras</p>
                    <p class="text-xs text-yellow-700 mt-1">Você precisa selecionar pelo menos uma bandeira na etapa anterior.</p>
                  </div>
                </div>

                <div v-else class="space-y-6">
                  <div
                    v-for="bandeiraId in bandeirasSelecionadas"
                    :key="bandeiraId"
                    class="bg-white rounded-xl p-6 border-2 border-gray-200 shadow-sm"
                  >
                    <h3 class="font-bold text-lg text-gray-800 mb-4 flex items-center gap-2">
                      <FontAwesomeIcon icon="credit-card" class="text-purple-500" />
                      {{ getBandeiraNome(bandeiraId) }}
                    </h3>

                    <!-- Débito -->
                    <div class="mb-4 pb-4 border-b border-gray-200">
                      <h4 class="font-semibold text-sm text-gray-700 mb-2 flex items-center gap-2">
                        <FontAwesomeIcon icon="circle" class="text-green-500 text-xs" />
                        Débito
                      </h4>
                      <div class="grid md:grid-cols-2 gap-3">
                        <div>
                          <label class="block text-xs text-gray-600 mb-1">Taxa (%)</label>
                          <input
                            v-model.number="getTaxaBandeira(bandeiraId, 'debito').taxaPercentual"
                            type="number"
                            step="0.01"
                            min="0"
                            placeholder="1.99"
                            class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:border-green-500 focus:ring-1 focus:ring-green-200"
                          />
                        </div>
                        <div>
                          <label class="block text-xs text-gray-600 mb-1">Taxa Fixa (R$)</label>
                          <input
                            v-model.number="getTaxaBandeira(bandeiraId, 'debito').taxaFixa"
                            type="number"
                            step="0.01"
                            min="0"
                            placeholder="0.00"
                            class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:border-green-500 focus:ring-1 focus:ring-green-200"
                          />
                        </div>
                      </div>
                    </div>

                    <!-- Crédito à Vista -->
                    <div class="mb-4 pb-4 border-b border-gray-200">
                      <h4 class="font-semibold text-sm text-gray-700 mb-2 flex items-center gap-2">
                        <FontAwesomeIcon icon="circle" class="text-blue-500 text-xs" />
                        Crédito à Vista
                      </h4>
                      <div class="grid md:grid-cols-2 gap-3">
                        <div>
                          <label class="block text-xs text-gray-600 mb-1">Taxa (%)</label>
                          <input
                            v-model.number="getTaxaBandeira(bandeiraId, 'credito_avista').taxaPercentual"
                            type="number"
                            step="0.01"
                            min="0"
                            placeholder="2.99"
                            class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:border-blue-500 focus:ring-1 focus:ring-blue-200"
                          />
                        </div>
                        <div>
                          <label class="block text-xs text-gray-600 mb-1">Taxa Fixa (R$)</label>
                          <input
                            v-model.number="getTaxaBandeira(bandeiraId, 'credito_avista').taxaFixa"
                            type="number"
                            step="0.01"
                            min="0"
                            placeholder="0.00"
                            class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:border-blue-500 focus:ring-1 focus:ring-blue-200"
                          />
                        </div>
                      </div>
                    </div>

                    <!-- Crédito Parcelado -->
                    <div>
                      <h4 class="font-semibold text-sm text-gray-700 mb-3 flex items-center gap-2">
                        <FontAwesomeIcon icon="circle" class="text-orange-500 text-xs" />
                        Crédito Parcelado (2x a 12x)
                      </h4>

                      <div class="space-y-3">
                        <div v-for="parcela in [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]" :key="parcela" class="grid grid-cols-3 gap-2 items-center">
                          <div class="text-sm font-medium text-gray-600">{{ parcela }}x</div>
                          <div>
                            <input
                              v-model.number="getTaxaBandeira(bandeiraId, 'credito_parcelado', parcela).taxaPercentual"
                              type="number"
                              step="0.01"
                              min="0"
                              :placeholder="(3.5 + (parcela - 2) * 0.5).toString()"
                              class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:border-orange-500 focus:ring-1 focus:ring-orange-200"
                            />
                          </div>
                          <div>
                            <input
                              v-model.number="getTaxaBandeira(bandeiraId, 'credito_parcelado', parcela).taxaFixa"
                              type="number"
                              step="0.01"
                              min="0"
                              placeholder="0.00"
                              class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:border-orange-500 focus:ring-1 focus:ring-orange-200"
                            />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Botões de navegação -->
            <div class="flex items-center justify-between pt-6 border-t border-gray-200">
              <button
                v-if="etapaAtual > 1"
                @click="voltarEtapa"
                type="button"
                class="flex items-center gap-2 px-6 py-3 bg-gray-200 text-gray-700 rounded-xl hover:bg-gray-300 transition-all duration-200 font-medium"
              >
                <FontAwesomeIcon icon="arrow-left" />
                <span>Voltar</span>
              </button>
              <div v-else></div>

              <button
                v-if="etapaAtual < 4"
                @click="avancarEtapa"
                type="button"
                class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl hover:from-blue-700 hover:to-indigo-700 transition-all duration-200 font-medium shadow-lg hover:shadow-xl transform hover:scale-105"
              >
                <span>Próxima Etapa</span>
                <FontAwesomeIcon icon="arrow-right" />
              </button>

              <button
                v-else
                type="submit"
                :disabled="salvando"
                class="flex items-center gap-2 px-8 py-3 bg-gradient-to-r from-green-600 to-emerald-600 text-white rounded-xl hover:from-green-700 hover:to-emerald-700 transition-all duration-200 font-medium shadow-lg hover:shadow-xl transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <FontAwesomeIcon icon="check" />
                <span>Salvar Maquininha</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
  maquininhasService,
  adquirentesService,
  bandeirasService,
  contasBancariasService
} from '@/services/api'
import type { NovaMaquininha, MaquininhaTaxa } from '@/types/api'

const router = useRouter()
const etapaAtual = ref(1)
const salvando = ref(false)

// Dados de seleção
const adquirentes = ref<any[]>([])
const bandeiras = ref<any[]>([])
const contas = ref<any[]>([])
const bandeirasSelecionadas = ref<number[]>([])

// Formulário
const formData = ref<NovaMaquininha>({
  nome: '',
  adquirenteId: 0,
  contaBancariaId: 0,
  prazoRecebimentoDebito: 1,
  prazoRecebimentoCredito: 30,
  aceitaAntecipacao: false,
  antecipacaoAutomatica: false,
  taxaAntecipacaoMensal: 0,
  aceitaPix: false,
  contaPixId: null,
  taxaPix: 0,
  prazoRecebimentoPix: 0,
  taxas: []
})

// Armazena as taxas configuradas
const taxasConfiguradas = ref<Map<string, MaquininhaTaxa>>(new Map())

const titulosEtapas = [
  'Dados Básicos da Maquininha',
  'Configuração do PIX',
  'Selecionar Bandeiras',
  'Configurar Taxas'
]

// Carregar dados
onMounted(async () => {
  try {
    const [adqResponse, bandResponse, contasResponse] = await Promise.all([
      adquirentesService.listarAtivos(),
      bandeirasService.listarAtivas(),
      contasBancariasService.listarAtivas()
    ])
    adquirentes.value = adqResponse
    bandeiras.value = bandResponse
    contas.value = contasResponse
  } catch (error) {
    console.error('Erro ao carregar dados:', error)
    alert('Erro ao carregar dados iniciais. Por favor, recarregue a página.')
  }
})

// Navegação entre etapas
const avancarEtapa = () => {
  if (validarEtapa()) {
    etapaAtual.value++
  }
}

const voltarEtapa = () => {
  etapaAtual.value--
}

const validarEtapa = (): boolean => {
  switch (etapaAtual.value) {
    case 1:
      if (!formData.value.nome || !formData.value.adquirenteId || !formData.value.contaBancariaId) {
        alert('Por favor, preencha todos os campos obrigatórios.')
        return false
      }
      if (formData.value.prazoRecebimentoDebito < 0 || formData.value.prazoRecebimentoCredito < 1) {
        alert('Prazos inválidos. Débito deve ser ≥ 0 e Crédito deve ser ≥ 1.')
        return false
      }
      return true

    case 2:
      if (formData.value.aceitaPix) {
        if (formData.value.taxaPix < 0 || formData.value.prazoRecebimentoPix < 0) {
          alert('Taxas e prazos não podem ser negativos.')
          return false
        }
      }
      return true

    case 3:
      if (bandeirasSelecionadas.value.length === 0) {
        alert('Selecione pelo menos uma bandeira.')
        return false
      }
      return true

    case 4:
      return true

    default:
      return true
  }
}

// Bandeiras
const toggleBandeira = (bandeiraId: number) => {
  const index = bandeirasSelecionadas.value.indexOf(bandeiraId)
  if (index === -1) {
    bandeirasSelecionadas.value.push(bandeiraId)
  } else {
    bandeirasSelecionadas.value.splice(index, 1)
    // Remover taxas dessa bandeira
    const keysToRemove: string[] = []
    taxasConfiguradas.value.forEach((taxa, key) => {
      if (key.startsWith(`${bandeiraId}-`)) {
        keysToRemove.push(key)
      }
    })
    keysToRemove.forEach(key => taxasConfiguradas.value.delete(key))
  }
}

const getBandeiraNome = (bandeiraId: number): string => {
  const bandeira = bandeiras.value.find(b => b.id === bandeiraId)
  return bandeira ? bandeira.nome : 'Bandeira'
}

// Gerenciamento de taxas
const getTaxaBandeira = (bandeiraId: number, tipoTransacao: string, numeroParcelas?: number): MaquininhaTaxa => {
  const key = numeroParcelas ?
    `${bandeiraId}-${tipoTransacao}-${numeroParcelas}` :
    `${bandeiraId}-${tipoTransacao}`

  if (!taxasConfiguradas.value.has(key)) {
    taxasConfiguradas.value.set(key, {
      bandeiraId,
      tipoTransacao: tipoTransacao as any,
      numeroParcelas: numeroParcelas || null,
      taxaPercentual: 0,
      taxaFixa: 0
    })
  }

  return taxasConfiguradas.value.get(key)!
}

// Salvar
const salvar = async () => {
  if (!validarEtapa()) return

  salvando.value = true

  try {
    // Montar array de taxas
    const taxasArray: MaquininhaTaxa[] = []
    taxasConfiguradas.value.forEach(taxa => {
      if (taxa.taxaPercentual > 0 || (taxa.taxaFixa && taxa.taxaFixa > 0)) {
        taxasArray.push(taxa)
      }
    })

    formData.value.taxas = taxasArray

    // Se não aceita PIX, zerar campos relacionados
    if (!formData.value.aceitaPix) {
      formData.value.taxaPix = 0
      formData.value.prazoRecebimentoPix = 0
      formData.value.contaPixId = null
    }

    // Se não aceita antecipação, zerar campos relacionados
    if (!formData.value.aceitaAntecipacao) {
      formData.value.antecipacaoAutomatica = false
      formData.value.taxaAntecipacaoMensal = 0
    }

    console.log('📤 Enviando dados:', formData.value)

    await maquininhasService.criar(formData.value)

    alert('✅ Maquininha cadastrada com sucesso!')
    router.push('/maquininhas')
  } catch (error: any) {
    console.error('❌ Erro ao salvar:', error)
    alert(error.message || 'Erro ao salvar a maquininha. Tente novamente.')
  } finally {
    salvando.value = false
  }
}

const voltar = () => {
  if (confirm('Deseja realmente sair? Os dados não salvos serão perdidos.')) {
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

@keyframes twinkle {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
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

.animate-twinkle {
  animation: twinkle 2s ease-in-out infinite;
}

.bg-pattern {
  background-image: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 20px 20px;
}
</style>

