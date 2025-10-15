<template>
  <div class="min-h-screen bg-gradient-to-br from-amber-50 via-orange-50 to-yellow-50 p-4 pb-20 relative overflow-hidden">
    <!-- 🎨 Background decorativo ultra moderno -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none z-0">
      <!-- Gradientes animados de fundo -->
      <div class="absolute -top-20 -left-20 w-96 h-96 bg-gradient-to-br from-amber-300/30 via-orange-200/20 to-yellow-300/25 rounded-full blur-3xl animate-float"></div>
      <div class="absolute -bottom-20 -right-20 w-[500px] h-[500px] bg-gradient-to-tl from-emerald-300/25 via-green-200/20 to-teal-300/30 rounded-full blur-3xl animate-float-delayed"></div>
      <div class="absolute top-1/3 left-1/4 w-80 h-80 bg-gradient-to-r from-orange-200/15 to-amber-200/20 rounded-full blur-2xl animate-pulse-slow"></div>

      <!-- Partículas flutuantes -->
      <div class="absolute top-20 left-1/4 w-2 h-2 bg-amber-400/60 rounded-full animate-bounce-gentle" style="animation-delay: 0.2s"></div>
      <div class="absolute top-40 right-1/3 w-3 h-3 bg-orange-400/50 rounded-full animate-float" style="animation-delay: 0.8s"></div>
      <div class="absolute bottom-40 left-1/3 w-1.5 h-1.5 bg-yellow-500/70 rounded-full animate-bounce-gentle" style="animation-delay: 1.2s"></div>
      <div class="absolute bottom-60 right-1/4 w-2.5 h-2.5 bg-green-400/40 rounded-full animate-float-delayed" style="animation-delay: 1.6s"></div>
    </div>

    <div class="relative z-20 max-w-4xl mx-auto pt-8 fade-in-up">
      <!-- 📋 Header Ultra Moderno -->
      <div class="text-center mb-12">
        <!-- Ícone principal com animação -->
        <div class="relative inline-block mb-8">
          <div class="inline-flex items-center justify-center w-24 h-24 bg-gradient-to-br from-amber-400 via-orange-500 to-yellow-500 rounded-3xl shadow-2xl transform hover:scale-110 transition-all duration-500 hover:rotate-6 animate-bounce-gentle">
            <FontAwesomeIcon :icon="['fas', 'clipboard-list']" class="text-white text-4xl animate-pulse-slow" />
          </div>

          <!-- Anel decorativo -->
          <div class="absolute -inset-4 bg-gradient-to-r from-amber-400/20 to-orange-400/20 rounded-full animate-ping"></div>
          <div class="absolute -inset-2 bg-gradient-to-r from-yellow-400/30 to-amber-400/30 rounded-full animate-pulse-slow"></div>
        </div>

        <!-- Título com gradiente -->
        <h1 class="text-5xl font-extrabold bg-gradient-to-r from-amber-600 via-orange-600 to-yellow-600 bg-clip-text text-transparent mb-4 animate-fade-in">
          Cadastrar Animal Serviço
        </h1>

        <!-- Badge de destaque -->
        <div class="inline-flex items-center gap-2 bg-gradient-to-r from-amber-100 to-orange-100 px-6 py-2 rounded-full border border-amber-200 shadow-lg mb-6 animate-slide-up">
          <div class="w-2 h-2 bg-gradient-to-r from-amber-500 to-orange-500 rounded-full animate-pulse"></div>
          <span class="text-amber-700 font-semibold text-sm">Sistema de Gestão</span>
          <div class="w-2 h-2 bg-gradient-to-r from-orange-500 to-yellow-500 rounded-full animate-pulse" style="animation-delay: 0.5s"></div>
        </div>

        <!-- Descrição aprimorada -->
        <p class="text-gray-600 max-w-2xl mx-auto text-xl leading-relaxed font-medium animate-fade-in" style="animation-delay: 0.2s">
          Registre um novo serviço para um animal específico com total controle sobre datas, utilização e responsáveis.
        </p>

        <!-- Indicadores de progresso -->
        <div class="flex justify-center items-center gap-8 mt-8 animate-fade-in" style="animation-delay: 0.4s">
          <div class="flex items-center gap-2 text-sm text-gray-500">
            <div class="w-2 h-2 bg-green-500 rounded-full"></div>
            <span>Rápido & Fácil</span>
          </div>
          <div class="flex items-center gap-2 text-sm text-gray-500">
            <div class="w-2 h-2 bg-blue-500 rounded-full"></div>
            <span>100% Seguro</span>
          </div>
          <div class="flex items-center gap-2 text-sm text-gray-500">
            <div class="w-2 h-2 bg-purple-500 rounded-full"></div>
            <span>Controle Total</span>
          </div>
        </div>
      </div>

      <!-- 📝 Formulário Ultra Moderno -->
      <BaseCard class="backdrop-blur-lg bg-white/90 border border-amber-100/50 shadow-2xl hover:shadow-3xl transition-all duration-500 animate-slide-up" style="animation-delay: 0.6s">
        <form @submit.prevent="cadastrarAnimalServico" class="space-y-10 p-2">
          <!-- 🐕 Seleção do Animal -->
          <div class="group space-y-4 p-6 rounded-2xl bg-gradient-to-r from-amber-50/50 to-orange-50/50 border border-amber-200/50 hover:border-amber-300/70 transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1">
            <div class="flex items-center justify-between">
              <label class="flex items-center text-lg font-bold text-gray-800 group-hover:text-amber-700 transition-colors">
                <div class="p-2 bg-gradient-to-r from-amber-500 to-orange-500 rounded-lg mr-3 shadow-md group-hover:shadow-lg transition-all">
                  <FontAwesomeIcon :icon="['fas', 'dog']" class="text-white text-sm" />
                </div>
                Animal *
              </label>
              <div class="flex items-center gap-1">
                <div class="w-2 h-2 bg-amber-500 rounded-full animate-pulse"></div>
                <div class="w-1.5 h-1.5 bg-orange-400 rounded-full animate-pulse" style="animation-delay: 0.2s"></div>
                <div class="w-1 h-1 bg-yellow-400 rounded-full animate-pulse" style="animation-delay: 0.4s"></div>
              </div>
            </div>
            <SearchSelect
              v-model="formulario.animalId"
              :options="animaisFiltrados"
              :loading="carregandoAnimais"
              :disabled="loading"
              required
              placeholder="🔍 Digite o nome do animal, tipo ou código..."
              value-key="id"
              label-key="nome"
              description-key="animalDescription"
              @search="buscarAnimais"
            />
            <p class="text-sm text-gray-500 flex items-center gap-2">
              <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-amber-500" />
              Selecione o animal que receberá o serviço
            </p>
          </div>

          <!-- 💼 Seleção do Serviço -->
          <div class="group space-y-4 p-6 rounded-2xl bg-gradient-to-r from-emerald-50/50 to-green-50/50 border border-emerald-200/50 hover:border-emerald-300/70 transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1">
            <div class="flex items-center justify-between">
              <label class="flex items-center text-lg font-bold text-gray-800 group-hover:text-emerald-700 transition-colors">
                <div class="p-2 bg-gradient-to-r from-emerald-500 to-green-500 rounded-lg mr-3 shadow-md group-hover:shadow-lg transition-all">
                  <FontAwesomeIcon :icon="['fas', 'wrench']" class="text-white text-sm" />
                </div>
                Serviço *
              </label>
              <div class="flex items-center gap-1">
                <div class="w-2 h-2 bg-emerald-500 rounded-full animate-pulse"></div>
                <div class="w-1.5 h-1.5 bg-green-400 rounded-full animate-pulse" style="animation-delay: 0.2s"></div>
                <div class="w-1 h-1 bg-teal-400 rounded-full animate-pulse" style="animation-delay: 0.4s"></div>
              </div>
            </div>
            <SearchSelect
              v-model="formulario.servicoId"
              :options="servicosFiltrados"
              :loading="carregandoServicos"
              :disabled="loading"
              required
              placeholder="🛠️ Digite o nome do serviço..."
              value-key="id"
              label-key="nome"
              description-key="servicoDescription"
              @search="buscarServicos"
            />
            <p class="text-sm text-gray-500 flex items-center gap-2">
              <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-emerald-500" />
              Escolha o tipo de serviço a ser oferecido
            </p>
          </div>

          <!-- 📅 Data do Serviço -->
          <div class="group space-y-4 p-6 rounded-2xl bg-gradient-to-r from-blue-50/50 to-cyan-50/50 border border-blue-200/50 hover:border-blue-300/70 transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1">
            <div class="flex items-center justify-between">
              <label class="flex items-center text-lg font-bold text-gray-800 group-hover:text-blue-700 transition-colors">
                <div class="p-2 bg-gradient-to-r from-blue-500 to-cyan-500 rounded-lg mr-3 shadow-md group-hover:shadow-lg transition-all">
                  <FontAwesomeIcon :icon="['fas', 'calendar-alt']" class="text-white text-sm" />
                </div>
                Data do Serviço *
              </label>
              <div class="flex items-center gap-1">
                <div class="w-2 h-2 bg-blue-500 rounded-full animate-pulse"></div>
                <div class="w-1.5 h-1.5 bg-cyan-400 rounded-full animate-pulse" style="animation-delay: 0.2s"></div>
                <div class="w-1 h-1 bg-sky-400 rounded-full animate-pulse" style="animation-delay: 0.4s"></div>
              </div>
            </div>
            <input
              v-model="formulario.dataServico"
              type="date"
              :disabled="loading"
              required
              class="w-full px-6 py-4 bg-white/80 border-2 border-blue-200/50 rounded-2xl focus:border-blue-500 focus:ring-4 focus:ring-blue-100 transition-all duration-300 text-lg font-medium hover:border-blue-300 disabled:bg-gray-50 disabled:cursor-not-allowed hover:shadow-lg focus:shadow-lg backdrop-blur-sm"
            />
            <p class="text-sm text-gray-500 flex items-center gap-2">
              <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-blue-500" />
              Data em que o serviço foi contratado ou realizado
            </p>
          </div>

          <!-- ⏰ Data de Expiração (apenas para pacotes) -->
          <div
            v-if="servicoSelecionado && servicoSelecionado.quantidade > 1"
            class="group space-y-4 p-6 rounded-2xl bg-gradient-to-r from-violet-50/50 to-purple-50/50 border border-violet-200/50 hover:border-violet-300/70 transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1"
          >
            <div class="flex items-center justify-between">
              <label class="flex items-center text-lg font-bold text-gray-800 group-hover:text-violet-700 transition-colors">
                <div class="p-2 bg-gradient-to-r from-violet-500 to-purple-500 rounded-lg mr-3 shadow-md group-hover:shadow-lg transition-all">
                  <FontAwesomeIcon :icon="['fas', 'clock']" class="text-white text-sm" />
                </div>
                Data de Expiração do Pacote
                <span class="ml-2 px-2 py-1 bg-violet-100 text-violet-700 rounded-full text-xs font-medium">
                  Opcional
                </span>
              </label>
              <div class="flex items-center gap-1">
                <div class="w-2 h-2 bg-violet-500 rounded-full animate-pulse"></div>
                <div class="w-1.5 h-1.5 bg-purple-400 rounded-full animate-pulse" style="animation-delay: 0.2s"></div>
                <div class="w-1 h-1 bg-indigo-400 rounded-full animate-pulse" style="animation-delay: 0.4s"></div>
              </div>
            </div>
            <input
              v-model="formulario.dataExpiracao"
              type="date"
              :disabled="loading"
              class="w-full px-6 py-4 bg-white/80 border-2 border-violet-200/50 rounded-2xl focus:border-violet-500 focus:ring-4 focus:ring-violet-100 transition-all duration-300 text-lg font-medium hover:border-violet-300 disabled:bg-gray-50 disabled:cursor-not-allowed hover:shadow-lg focus:shadow-lg backdrop-blur-sm"
            />
            <p class="text-sm text-gray-500 flex items-center gap-2">
              <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-violet-500" />
              Data limite para usar todos os banhos do pacote.
            </p>
          </div>

          <!-- 💡 Aviso para banhos únicos -->
          <div
            v-if="servicoSelecionado && servicoSelecionado.quantidade === 1"
            class="p-4 rounded-xl bg-gradient-to-r from-amber-50 to-yellow-50 border border-amber-200"
          >
            <div class="flex items-center gap-3">
              <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-amber-600 text-lg" />
              <div>
                <p class="text-amber-800 font-medium">Banho Único</p>
                <p class="text-amber-700 text-sm">Este serviço não precisa de data de expiração, pois será realizado na data informada.</p>
              </div>
            </div>
          </div>

          <!-- 💳 Seção de Pagamento -->
          <div class="group space-y-4 p-6 rounded-2xl bg-gradient-to-r from-emerald-50/50 to-green-50/50 border border-emerald-200/50 hover:border-emerald-300/70 transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1">
            <div class="flex items-center justify-between">
              <label class="flex items-center text-lg font-bold text-gray-800 group-hover:text-emerald-700 transition-colors">
                <div class="p-2 bg-gradient-to-r from-emerald-500 to-green-500 rounded-lg mr-3 shadow-md group-hover:shadow-lg transition-all">
                  <FontAwesomeIcon :icon="['fas', 'credit-card']" class="text-white text-sm" />
                </div>
                Controle de Pagamento
              </label>
              <div class="flex items-center gap-1">
                <div class="w-2 h-2 bg-emerald-500 rounded-full animate-pulse"></div>
                <div class="w-1.5 h-1.5 bg-green-400 rounded-full animate-pulse" style="animation-delay: 0.2s"></div>
                <div class="w-1 h-1 bg-teal-400 rounded-full animate-pulse" style="animation-delay: 0.4s"></div>
              </div>
            </div>

            <!-- Status e Data lado a lado -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <!-- Status de Pagamento -->
              <div>
                <label class="block text-sm font-semibold text-gray-700 mb-2">
                  Status de Pagamento *
                </label>
                <select
                  v-model="formulario.statusPagamento"
                  :disabled="loading"
                  required
                  class="w-full px-4 py-3 bg-white/80 border-2 border-emerald-200/50 rounded-xl focus:border-emerald-500 focus:ring-4 focus:ring-emerald-100 transition-all duration-300 text-base font-medium hover:border-emerald-300 disabled:bg-gray-50 disabled:cursor-not-allowed hover:shadow-lg focus:shadow-lg backdrop-blur-sm"
                >
                  <option value="em_aberto">💳 Em Aberto</option>
                  <option value="pago">✅ Pago</option>
                  <option value="cancelado">❌ Cancelado</option>
                </select>
              </div>

              <!-- Data de Pagamento -->
              <div>
                <label class="block text-sm font-semibold text-gray-700 mb-2">
                  Data de Pagamento
                  <span v-if="formulario.statusPagamento !== 'pago'" class="ml-1 px-2 py-1 bg-gray-100 text-gray-600 rounded-full text-xs font-medium">
                    Opcional
                  </span>
                  <span v-else class="ml-1 px-2 py-1 bg-emerald-100 text-emerald-700 rounded-full text-xs font-medium">
                    Recomendado
                  </span>
                </label>
                <input
                  v-model="formulario.dataPagamento"
                  type="date"
                  :disabled="loading"
                  class="w-full px-4 py-3 bg-white/80 border-2 border-emerald-200/50 rounded-xl focus:border-emerald-500 focus:ring-4 focus:ring-emerald-100 transition-all duration-300 text-base font-medium hover:border-emerald-300 disabled:bg-gray-50 disabled:cursor-not-allowed hover:shadow-lg focus:shadow-lg backdrop-blur-sm"
                />
              </div>
            </div>

            <p class="text-sm text-gray-500 flex items-center gap-2">
              <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-emerald-500" />
              <span v-if="formulario.statusPagamento === 'em_aberto'">
                Serviço aguarda pagamento. Defina a data quando receber.
              </span>
              <span v-else-if="formulario.statusPagamento === 'pago'">
                Serviço foi pago. A data ajuda no controle financeiro.
              </span>
              <span v-else>
                Serviço foi cancelado. Não será cobrado.
              </span>
            </p>
          </div>

          <!-- 🛁 Banhos Usados -->
          <div class="group space-y-4 p-6 rounded-2xl bg-gradient-to-r from-cyan-50/50 to-teal-50/50 border border-cyan-200/50 hover:border-cyan-300/70 transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1">
            <div class="flex items-center justify-between">
              <label class="flex items-center text-lg font-bold text-gray-800 group-hover:text-cyan-700 transition-colors">
                <div class="p-2 bg-gradient-to-r from-cyan-500 to-teal-500 rounded-lg mr-3 shadow-md group-hover:shadow-lg transition-all">
                  <FontAwesomeIcon :icon="['fas', 'bath']" class="text-white text-sm" />
                </div>
                Banhos Já Utilizados
              </label>
              <div class="flex items-center gap-1">
                <div class="w-2 h-2 bg-cyan-500 rounded-full animate-pulse"></div>
                <div class="w-1.5 h-1.5 bg-teal-400 rounded-full animate-pulse" style="animation-delay: 0.2s"></div>
                <div class="w-1 h-1 bg-blue-400 rounded-full animate-pulse" style="animation-delay: 0.4s"></div>
              </div>
            </div>

            <div class="relative">
              <BaseInput
                v-model.number="formulario.banhosUsados"
                type="number"
                min="0"
                :max="maxBanhosPermitidos"
                :disabled="loading || !servicoSelecionado"
                class="w-full px-6 py-4 bg-white/80 border-2 border-cyan-200/50 rounded-2xl focus:border-cyan-500 focus:ring-4 focus:ring-cyan-100 transition-all duration-300 text-lg font-medium hover:border-cyan-300 disabled:bg-gray-50 disabled:cursor-not-allowed hover:shadow-lg focus:shadow-lg backdrop-blur-sm"
                :class="{
                  'border-red-300 bg-red-50/80': formulario.banhosUsados > maxBanhosPermitidos
                }"
                placeholder="0"
                @input="validarBanhosUsados"
              />

              <!-- Progress bar visual -->
              <div v-if="servicoSelecionado" class="mt-3 bg-gray-200 rounded-full h-2 overflow-hidden">
                <div
                  class="h-full transition-all duration-500 rounded-full"
                  :class="formulario.banhosUsados > maxBanhosPermitidos ? 'bg-red-500' : 'bg-gradient-to-r from-cyan-500 to-teal-500'"
                  :style="{ width: Math.min((formulario.banhosUsados / maxBanhosPermitidos) * 100, 100) + '%' }"
                ></div>
              </div>
            </div>

            <div class="text-sm space-y-2">
              <div v-if="servicoSelecionado" class="flex items-center justify-between p-3 bg-white/60 rounded-xl border border-cyan-100">
                <span class="text-gray-600">Máximo permitido:</span>
                <div class="flex items-center gap-2">
                  <span class="font-bold text-cyan-700">{{ maxBanhosPermitidos }} banhos</span>
                  <span v-if="servicoSelecionado.quantidade === 1" class="px-2 py-1 bg-amber-100 text-amber-700 rounded-full text-xs font-medium">
                    Banho Único
                  </span>
                  <span v-else class="px-2 py-1 bg-blue-100 text-blue-700 rounded-full text-xs font-medium">
                    Pacote de {{ servicoSelecionado.quantidade }}
                  </span>
                </div>
              </div>

              <div v-if="formulario.banhosUsados > maxBanhosPermitidos" class="flex items-center gap-2 p-3 bg-red-50 border border-red-200 rounded-xl">
                <FontAwesomeIcon :icon="['fas', 'exclamation-triangle']" class="text-red-500" />
                <span class="text-red-600 font-medium">Valor será limitado automaticamente a {{ maxBanhosPermitidos }}</span>
              </div>

              <div v-else-if="formulario.banhosUsados === maxBanhosPermitidos && maxBanhosPermitidos > 0" class="flex items-center gap-2 p-3 bg-amber-50 border border-amber-200 rounded-xl">
                <FontAwesomeIcon :icon="['fas', 'check-circle']" class="text-amber-600" />
                <span class="text-amber-700 font-medium">Máximo atingido - todos os banhos do pacote já utilizados</span>
              </div>

              <p v-else class="text-gray-500 flex items-center gap-2">
                <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-cyan-500" />
                Quantidade de banhos já utilizados (geralmente inicia com 0). Máximo: {{ maxBanhosPermitidos }}
              </p>
            </div>
          </div>

          <!-- 📅 Datas dos Banhos Já Realizados (se banhosUsados > 0) -->
          <div v-if="formulario.banhosUsados > 0" class="group space-y-6 p-6 rounded-2xl bg-gradient-to-r from-indigo-50/50 to-purple-50/50 border border-indigo-200/50 hover:border-indigo-300/70 transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1">
            <div class="flex items-center justify-between">
              <label class="flex items-center text-lg font-bold text-gray-800 group-hover:text-indigo-700 transition-colors">
                <div class="p-2 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-lg mr-3 shadow-md group-hover:shadow-lg transition-all">
                  <FontAwesomeIcon :icon="['fas', 'calendar-days']" class="text-white text-sm" />
                </div>
                Datas dos Banhos Já Realizados
              </label>
              <div class="px-3 py-1 bg-gradient-to-r from-indigo-100 to-purple-100 rounded-full border border-indigo-200">
                <span class="text-indigo-700 font-bold text-sm">{{ formulario.banhosUsados }} banho{{ formulario.banhosUsados > 1 ? 's' : '' }}</span>
              </div>
            </div>

            <p class="text-sm text-gray-500 flex items-center gap-2 mb-4">
              <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-indigo-500" />
              Defina as datas em que cada banho foi realizado. A data padrão é hoje, mas você pode alterar facilmente.
            </p>

            <!-- Lista de banhos para definir datas -->
            <div class="space-y-4">
              <div
                v-for="(data, index) in datasBanhosRealizados"
                :key="index"
                class="bg-white/80 backdrop-blur-sm border border-indigo-100 rounded-2xl p-5 hover:shadow-lg transition-all duration-300 transform hover:-translate-y-1"
              >
                <div class="flex items-start gap-4">
                  <!-- Número do banho -->
                  <div class="flex-shrink-0">
                    <div class="w-12 h-12 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-xl flex items-center justify-center shadow-lg">
                      <span class="text-white font-bold text-lg">{{ index + 1 }}</span>
                    </div>
                  </div>

                  <!-- Campos de data e observação -->
                  <div class="flex-grow space-y-3">
                    <div>
                      <label class="block text-sm font-semibold text-gray-700 mb-2">
                        Data do {{ index + 1 }}º Banho *
                      </label>
                      <input
                        v-model="datasBanhosRealizados[index]"
                        type="date"
                        :disabled="loading"
                        required
                        class="w-full px-4 py-3 bg-white/80 border-2 border-indigo-200/50 rounded-xl focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100 transition-all duration-300 text-base font-medium hover:border-indigo-300 disabled:bg-gray-50 disabled:cursor-not-allowed hover:shadow-lg focus:shadow-lg backdrop-blur-sm"
                      />
                    </div>

                    <div>
                      <label class="block text-sm font-semibold text-gray-700 mb-2">
                        Observações (opcional)
                      </label>
                      <input
                        v-model="observacoesBanhos[index]"
                        type="text"
                        :disabled="loading"
                        placeholder="Ex: Banho completo, animal estava agitado, etc."
                        class="w-full px-4 py-3 bg-white/80 border-2 border-indigo-200/50 rounded-xl focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100 transition-all duration-300 text-base font-medium hover:border-indigo-300 disabled:bg-gray-50 disabled:cursor-not-allowed hover:shadow-lg focus:shadow-lg backdrop-blur-sm"
                      />
                    </div>
                  </div>

                  <!-- Indicador visual -->
                  <div class="flex-shrink-0 flex items-center gap-1 mt-2">
                    <div class="w-2 h-2 bg-indigo-500 rounded-full animate-pulse"></div>
                    <div class="w-1.5 h-1.5 bg-purple-400 rounded-full animate-pulse" style="animation-delay: 0.2s"></div>
                    <div class="w-1 h-1 bg-violet-400 rounded-full animate-pulse" style="animation-delay: 0.4s"></div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Atalhos rápidos -->
            <div class="flex flex-wrap gap-3 mt-4">
              <button
                type="button"
                @click="preencherTodosHoje"
                class="px-4 py-2 bg-gradient-to-r from-indigo-100 to-purple-100 text-indigo-700 rounded-lg border border-indigo-200 hover:from-indigo-200 hover:to-purple-200 transition-all duration-300 text-sm font-medium"
              >
                📅 Todos hoje
              </button>
              <button
                type="button"
                @click="preencherSequencial"
                class="px-4 py-2 bg-gradient-to-r from-indigo-100 to-purple-100 text-indigo-700 rounded-lg border border-indigo-200 hover:from-indigo-200 hover:to-purple-200 transition-all duration-300 text-sm font-medium"
              >
                📆 Sequencial (últimos dias)
              </button>
            </div>
          </div>

          <!-- 👤 Usuário Responsável -->
          <div class="group space-y-4 p-6 rounded-2xl bg-gradient-to-r from-purple-50/50 to-indigo-50/50 border border-purple-200/50 hover:border-purple-300/70 transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1">
            <div class="flex items-center justify-between">
              <label class="flex items-center text-lg font-bold text-gray-800 group-hover:text-purple-700 transition-colors">
                <div class="p-2 bg-gradient-to-r from-purple-500 to-indigo-500 rounded-lg mr-3 shadow-md group-hover:shadow-lg transition-all">
                  <FontAwesomeIcon :icon="['fas', 'user']" class="text-white text-sm" />
                </div>
                Usuário Responsável *
              </label>
              <div class="flex items-center gap-1">
                <div class="w-2 h-2 bg-purple-500 rounded-full animate-pulse"></div>
                <div class="w-1.5 h-1.5 bg-indigo-400 rounded-full animate-pulse" style="animation-delay: 0.2s"></div>
                <div class="w-1 h-1 bg-violet-400 rounded-full animate-pulse" style="animation-delay: 0.4s"></div>
              </div>
            </div>
            <SearchSelect
              v-model="formulario.usuarioId"
              :options="usuariosFiltrados"
              :loading="carregandoUsuarios"
              :disabled="loading"
              required
              placeholder="👤 Digite o nome do usuário..."
              value-key="id"
              label-key="nome"
              description-key="usuarioDescription"
              @search="buscarUsuarios"
            />
            <p class="text-sm text-gray-500 flex items-center gap-2">
              <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-purple-500" />
              Usuário responsável pela venda ou execução do serviço
            </p>
          </div>

          <!-- 🎯 Resumo Ultra Moderno (se serviço selecionado) -->
          <div v-if="servicoSelecionado" class="relative overflow-hidden bg-gradient-to-br from-amber-50 via-orange-50 to-yellow-50 border-2 border-amber-200/70 rounded-3xl p-8 shadow-xl animate-fade-in">
            <!-- Background decorativo -->
            <div class="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-amber-200/30 to-orange-200/30 rounded-full blur-2xl transform translate-x-16 -translate-y-16"></div>
            <div class="absolute bottom-0 left-0 w-24 h-24 bg-gradient-to-tr from-yellow-200/30 to-amber-200/30 rounded-full blur-xl transform -translate-x-12 translate-y-12"></div>

            <div class="relative z-10">
              <!-- Header do resumo -->
              <div class="flex items-center justify-between mb-6">
                <div class="flex items-center">
                  <div class="p-3 bg-gradient-to-r from-amber-500 to-orange-500 rounded-2xl mr-4 shadow-lg">
                    <FontAwesomeIcon :icon="['fas', 'clipboard-check']" class="text-white text-xl" />
                  </div>
                  <div>
                    <h3 class="text-2xl font-bold bg-gradient-to-r from-amber-700 to-orange-700 bg-clip-text text-transparent">Resumo do Serviço</h3>
                    <p class="text-amber-600 text-sm font-medium">Detalhes da transação</p>
                  </div>
                </div>
                <div class="px-4 py-2 bg-gradient-to-r from-green-100 to-emerald-100 rounded-full border border-green-200">
                  <span class="text-green-700 font-bold text-sm flex items-center gap-1">
                    <div class="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
                    Configurado
                  </span>
                </div>
              </div>

              <!-- Cards de informação -->
              <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                <!-- Serviço -->
                <div class="bg-white/80 backdrop-blur-sm border border-amber-100 rounded-2xl p-5 hover:shadow-lg transition-all duration-300 transform hover:-translate-y-1">
                  <div class="flex items-center justify-between mb-3">
                    <FontAwesomeIcon :icon="['fas', 'wrench']" class="text-amber-600 text-lg" />
                    <div class="w-3 h-3 bg-amber-400 rounded-full animate-pulse"></div>
                  </div>
                  <p class="text-gray-500 text-sm font-medium mb-1">Serviço</p>
                  <p class="font-bold text-gray-800 text-lg">{{ servicoSelecionado.nome }}</p>
                </div>

                <!-- Valor -->
                <div class="bg-white/80 backdrop-blur-sm border border-green-100 rounded-2xl p-5 hover:shadow-lg transition-all duration-300 transform hover:-translate-y-1">
                  <div class="flex items-center justify-between mb-3">
                    <FontAwesomeIcon :icon="['fas', 'dollar-sign']" class="text-green-600 text-lg" />
                    <div class="w-3 h-3 bg-green-400 rounded-full animate-pulse"></div>
                  </div>
                  <p class="text-gray-500 text-sm font-medium mb-1">Valor Total</p>
                  <p class="font-bold text-green-600 text-xl">R$ {{ formatarValor(servicoSelecionado.valor) }}</p>
                </div>

                <!-- Quantidade -->
                <div class="bg-white/80 backdrop-blur-sm border border-blue-100 rounded-2xl p-5 hover:shadow-lg transition-all duration-300 transform hover:-translate-y-1">
                  <div class="flex items-center justify-between mb-3">
                    <FontAwesomeIcon :icon="['fas', 'bath']" class="text-blue-600 text-lg" />
                    <div class="w-3 h-3 bg-blue-400 rounded-full animate-pulse"></div>
                  </div>
                  <p class="text-gray-500 text-sm font-medium mb-1">Banhos Inclusos</p>
                  <div class="flex items-center gap-2">
                    <p class="font-bold text-blue-600 text-xl">{{ servicoSelecionado.quantidade }}</p>
                    <span v-if="servicoSelecionado.quantidade === 1" class="px-2 py-1 bg-amber-100 text-amber-700 rounded-full text-xs font-bold">
                      ÚNICO
                    </span>
                    <span v-else class="px-2 py-1 bg-blue-100 text-blue-700 rounded-full text-xs font-bold">
                      PACOTE
                    </span>
                  </div>
                </div>
              </div>

              <!-- Barra de progresso dos banhos -->
              <div v-if="formulario.banhosUsados >= 0" class="mt-6 p-4 bg-white/60 rounded-2xl border border-amber-100">
                <div class="flex items-center justify-between mb-2">
                  <span class="text-gray-700 font-medium text-sm">Progresso de Utilização</span>
                  <span class="text-gray-600 font-bold text-sm">{{ formulario.banhosUsados }}/{{ servicoSelecionado.quantidade }}</span>
                </div>
                <div class="w-full bg-gray-200 rounded-full h-3 overflow-hidden">
                  <div
                    class="h-full transition-all duration-700 rounded-full"
                    :class="formulario.banhosUsados > servicoSelecionado.quantidade ? 'bg-red-500' : 'bg-gradient-to-r from-amber-500 to-orange-500'"
                    :style="{ width: Math.min((formulario.banhosUsados / servicoSelecionado.quantidade) * 100, 100) + '%' }"
                  ></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 🎭 Botões de Ação Ultra Modernos -->
          <div class="flex flex-col sm:flex-row gap-6 pt-8 mt-8 border-t-2 border-gradient-to-r from-amber-200 to-orange-200">
            <!-- Cancelar -->
            <button
              type="button"
              @click="$router.push('/servicos')"
              :disabled="loading"
              class="group relative overflow-hidden inline-flex items-center justify-center font-bold rounded-2xl transition-all duration-500 focus:outline-none focus:ring-4 disabled:opacity-50 disabled:cursor-not-allowed px-10 py-5 text-lg bg-gradient-to-r from-gray-100 to-gray-200 text-gray-700 hover:from-gray-200 hover:to-gray-300 focus:ring-gray-300 border-2 border-gray-300 hover:border-gray-400 shadow-lg hover:shadow-xl transform hover:-translate-y-1 hover:scale-105"
            >
              <div class="absolute inset-0 bg-gradient-to-r from-gray-600/10 to-gray-700/10 transform scale-x-0 group-hover:scale-x-100 transition-transform duration-500 origin-left"></div>
              <FontAwesomeIcon :icon="['fas', 'arrow-left']" class="mr-3 transition-transform duration-300 group-hover:-translate-x-1" />
              <span class="relative">Cancelar</span>
            </button>

            <!-- Cadastrar -->
            <button
              type="submit"
              :disabled="loading || !formularioValido"
              class="group relative overflow-hidden flex-1 inline-flex items-center justify-center font-bold rounded-2xl transition-all duration-500 focus:outline-none focus:ring-4 disabled:opacity-50 disabled:cursor-not-allowed px-10 py-5 text-lg text-white bg-gradient-to-r from-amber-500 via-orange-500 to-yellow-500 hover:from-amber-600 hover:via-orange-600 hover:to-yellow-600 focus:ring-amber-300 shadow-2xl hover:shadow-3xl transform hover:-translate-y-2 hover:scale-105 disabled:transform-none disabled:hover:scale-100"
            >
              <!-- Efeito de brilho -->
              <div class="absolute inset-0 bg-gradient-to-r from-white/20 to-transparent transform -skew-x-12 scale-x-0 group-hover:scale-x-100 transition-transform duration-700 origin-left"></div>

              <!-- Partículas flutuantes no hover -->
              <div class="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-500">
                <div class="absolute top-2 left-4 w-1 h-1 bg-white rounded-full animate-bounce" style="animation-delay: 0.1s"></div>
                <div class="absolute top-4 right-6 w-1.5 h-1.5 bg-white/80 rounded-full animate-bounce" style="animation-delay: 0.3s"></div>
                <div class="absolute bottom-3 left-1/3 w-1 h-1 bg-white/60 rounded-full animate-bounce" style="animation-delay: 0.5s"></div>
              </div>

              <FontAwesomeIcon
                v-if="loading"
                :icon="['fas', 'spinner']"
                class="mr-3 animate-spin text-xl"
              />
              <FontAwesomeIcon
                v-else
                :icon="['fas', 'plus-circle']"
                class="mr-3 text-xl transition-transform duration-300 group-hover:rotate-180 group-hover:scale-110"
              />
              <span class="relative font-extrabold tracking-wide">
                {{ loading ? 'Cadastrando...' : 'Cadastrar Animal Serviço' }}
              </span>
            </button>
          </div>
        </form>
      </BaseCard>
    </div>

    <!-- 🌊 Wave decoration simples -->
    <div class="absolute bottom-0 left-0 w-full overflow-hidden z-0">
      <svg
        class="relative block w-full h-16"
        viewBox="0 0 1200 120"
        preserveAspectRatio="none"
      >
        <path
          d="M0,60 C300,20 600,100 900,60 C1050,40 1150,80 1200,60 L1200,120 L0,120 Z"
          class="fill-gradient-to-r from-amber-400 to-green-500 opacity-80"
          fill="url(#gradient1)"
        ></path>
        <defs>
          <linearGradient id="gradient1" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" style="stop-color:#F59E0B"/>
            <stop offset="50%" style="stop-color:#10B981"/>
            <stop offset="100%" style="stop-color:#059669"/>
          </linearGradient>
        </defs>
      </svg>
    </div>

    <!-- 🎉 Modal de sucesso super bonitinho -->
    <BaseModal v-model="mostrarSucesso" size="lg">
      <div class="text-center p-8 bg-gradient-to-br from-amber-50 to-green-50">
        <!-- Celebração animada -->
        <div class="relative mb-8">
          <div class="w-24 h-24 bg-gradient-to-r from-amber-400 to-green-500 rounded-full flex items-center justify-center mx-auto shadow-2xl animate-bounce-gentle">
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
          <h3 class="text-3xl font-bold bg-gradient-to-r from-amber-600 to-green-600 bg-clip-text text-transparent">
            🎉 Serviço Registrado! 🎉
          </h3>
          <p class="text-xl text-gray-700 font-semibold">
            O serviço foi cadastrado com sucesso para <strong>{{ animalServicoRegistrado?.animalNome }}</strong>!
          </p>

          <!-- Informações do registro -->
          <div class="bg-white bg-opacity-60 rounded-2xl p-6 inline-block backdrop-blur-sm border border-amber-200 max-w-md">
            <div class="space-y-3 text-left">
              <div class="flex items-center gap-3">
                <FontAwesomeIcon icon="dog" class="text-amber-600 w-5" />
                <div>
                  <span class="font-medium text-gray-600">Animal:</span>
                  <span class="font-bold text-gray-800 ml-2">{{ animalServicoRegistrado?.animalNome }}</span>
                </div>
              </div>

              <div class="flex items-center gap-3">
                <FontAwesomeIcon icon="wrench" class="text-green-600 w-5" />
                <div>
                  <span class="font-medium text-gray-600">Serviço:</span>
                  <span class="font-bold text-gray-800 ml-2">{{ animalServicoRegistrado?.servicoNome }}</span>
                </div>
              </div>

              <div class="flex items-center gap-3">
                <FontAwesomeIcon icon="user" class="text-purple-600 w-5" />
                <div>
                  <span class="font-medium text-gray-600">Vendedor:</span>
                  <span class="font-bold text-gray-800 ml-2">{{ animalServicoRegistrado?.usuarioNome }}</span>
                </div>
              </div>

              <div class="flex items-center gap-3">
                <FontAwesomeIcon icon="calendar-alt" class="text-blue-600 w-5" />
                <div>
                  <span class="font-medium text-gray-600">Data:</span>
                  <span class="font-bold text-gray-800 ml-2">{{ formatarDataSegura(animalServicoRegistrado?.dataServico || '') }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Botões estilosos -->
        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <button
            @click="cadastrarOutroServico"
            class="px-8 py-3 bg-gradient-to-r from-amber-500 to-yellow-500 hover:from-amber-600 hover:to-yellow-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 flex items-center justify-center gap-2"
          >
            <FontAwesomeIcon icon="plus-circle" />
            Cadastrar Outro Serviço
          </button>

          <button
            @click="verListaServicos"
            class="px-8 py-3 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 flex items-center justify-center gap-2"
          >
            <FontAwesomeIcon icon="list" />
            Ver Lista de Serviços
          </button>
        </div>
      </div>
    </BaseModal>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseInput from '@/components/UI/BaseInput.vue'
import BaseButton from '@/components/UI/BaseButton.vue'
import BaseModal from '@/components/UI/BaseModal.vue'
import SearchSelect from '@/components/UI/SearchSelect.vue'
import { animalServicoService, animaisService, servicosService, usuariosService } from '@/services/api'
import type { Animal, ServicoCompleto, NovoAnimalServico, Usuario, CriarAnimalServicoCompleto } from '@/types/api'
import { formatarDataSegura } from '@/utils/formatters'

const router = useRouter()

// 📊 Estados reativas
const loading = ref(false)
const carregandoAnimais = ref(false)
const carregandoServicos = ref(false)
const carregandoUsuarios = ref(false)
const mostrarSucesso = ref(false)
const animalServicoRegistrado = ref<any>(null)

// 📋 Dados para os selects
const animais = ref<Animal[]>([])
const servicos = ref<ServicoCompleto[]>([])
const usuarios = ref<Usuario[]>([])

// 🔍 Estados de busca
const animaisFiltrados = ref<Animal[]>([])
const servicosFiltrados = ref<ServicoCompleto[]>([])
const usuariosFiltrados = ref<Usuario[]>([])

// 📝 Formulário
const formulario = ref({
  animalId: '',
  servicoId: '',
  dataServico: '',
  dataExpiracao: '',
  banhosUsados: 0,
  usuarioId: '',
  // Campos de pagamento
  statusPagamento: 'em_aberto',
  dataPagamento: ''
})

// 📅 Datas dos banhos já realizados
const datasBanhosRealizados = ref<string[]>([])
const observacoesBanhos = ref<string[]>([])

// 🧮 Computadas
const formularioValido = computed(() => {
  return formulario.value.animalId &&
         formulario.value.servicoId &&
         formulario.value.dataServico &&
         formulario.value.usuarioId &&
         formulario.value.banhosUsados >= 0 &&
         formulario.value.banhosUsados <= maxBanhosPermitidos.value
})

const servicoSelecionado = computed(() => {
  return servicos.value.find(s => s.id === Number(formulario.value.servicoId))
})

const maxBanhosPermitidos = computed(() => {
  return servicoSelecionado.value?.quantidade || 1
})

// 📝 Adicionar descrições dinâmicas aos dados
const prepararDadosAnimais = (): void => {
  animais.value.forEach(animal => {
    (animal as any).animalDescription = `${animal.tipo} - Código: ${animal.codigoSimplesVet}`
  })
  animaisFiltrados.value = animais.value.slice(0, 10)
}

const prepararDadosServicos = (): void => {
  servicos.value.forEach(servico => {
    const quantidadeText = servico.quantidade > 1 ? ` (${servico.quantidade} banhos)` : ''
    ;(servico as any).servicoDescription = `R$ ${formatarValor(servico.valor)}${quantidadeText}`
  })
  servicosFiltrados.value = servicos.value.slice(0, 10)
}

const prepararDadosUsuarios = (): void => {
  usuarios.value.forEach(usuario => {
    (usuario as any).usuarioDescription = `${usuario.email} - ${usuario.role}`
  })
  usuariosFiltrados.value = usuarios.value.slice(0, 10)
}

// 💰 Formatação de valor
const formatarValor = (valor: number): string => {
  return valor.toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 📅 Gerenciar datas dos banhos já realizados
const gerenciarDatasBanhos = (quantidadeBanhos: number): void => {
  // 🚨 VALIDAÇÃO: Limitar número máximo para evitar travamento
  const maxBanhosPermitidosReal = maxBanhosPermitidos.value || 10

  if (quantidadeBanhos > maxBanhosPermitidosReal) {
    console.warn(`⚠️ Quantidade de banhos (${quantidadeBanhos}) maior que o permitido (${maxBanhosPermitidosReal}). Limitando...`)
    quantidadeBanhos = maxBanhosPermitidosReal
    // Atualizar o formulário para o valor correto
    formulario.value.banhosUsados = quantidadeBanhos
  }

  // 🕒 CORREÇÃO DE FUSO HORÁRIO: Usar data local sem conversão UTC
  const obterDataLocal = (): string => {
    const agora = new Date()
    const ano = agora.getFullYear()
    const mes = String(agora.getMonth() + 1).padStart(2, '0')
    const dia = String(agora.getDate()).padStart(2, '0')
    return `${ano}-${mes}-${dia}`
  }

  const hoje = obterDataLocal()

  // Ajustar o array de datas
  if (quantidadeBanhos === 0) {
    datasBanhosRealizados.value = []
    observacoesBanhos.value = []
  } else {
    // Adicionar datas faltantes (com data de hoje como padrão)
    while (datasBanhosRealizados.value.length < quantidadeBanhos) {
      datasBanhosRealizados.value.push(hoje)
      observacoesBanhos.value.push('')
    }

    // Remover datas extras
    if (datasBanhosRealizados.value.length > quantidadeBanhos) {
      datasBanhosRealizados.value.splice(quantidadeBanhos)
      observacoesBanhos.value.splice(quantidadeBanhos)
    }
  }
}

// 🚀 Atalhos para preenchimento rápido de datas
const obterDataLocal = (): string => {
  const agora = new Date()
  const ano = agora.getFullYear()
  const mes = String(agora.getMonth() + 1).padStart(2, '0')
  const dia = String(agora.getDate()).padStart(2, '0')
  return `${ano}-${mes}-${dia}`
}

const obterDataLocalComOffset = (diasOffset: number): string => {
  const data = new Date()
  data.setDate(data.getDate() - diasOffset)
  const ano = data.getFullYear()
  const mes = String(data.getMonth() + 1).padStart(2, '0')
  const dia = String(data.getDate()).padStart(2, '0')
  return `${ano}-${mes}-${dia}`
}

const preencherTodosHoje = (): void => {
  const hoje = obterDataLocal()
  for (let i = 0; i < datasBanhosRealizados.value.length; i++) {
    datasBanhosRealizados.value[i] = hoje
  }
  console.log('📅 Todas as datas preenchidas com hoje:', hoje)
}

const preencherSequencial = (): void => {
  for (let i = 0; i < datasBanhosRealizados.value.length; i++) {
    datasBanhosRealizados.value[i] = obterDataLocalComOffset(i)
  }
  console.log('📆 Datas preenchidas sequencialmente (últimos dias)')
}

// ✅ Validação em tempo real do campo banhos usados
const validarBanhosUsados = (): void => {
  const maxPermitido = maxBanhosPermitidos.value || 10

  // Se o valor for muito alto, limitar automaticamente
  if (formulario.value.banhosUsados > maxPermitido) {
    console.warn(`⚠️ Limitando banhos de ${formulario.value.banhosUsados} para ${maxPermitido}`)
    formulario.value.banhosUsados = maxPermitido
  }

  // Se o valor for negativo, zerar
  if (formulario.value.banhosUsados < 0) {
    formulario.value.banhosUsados = 0
  }
}

// 🔍 Funções de busca
const buscarAnimais = async (termo: string): Promise<void> => {
  if (termo.length < 2) {
    animaisFiltrados.value = animais.value.slice(0, 10)
    return
  }

  try {
    carregandoAnimais.value = true
    console.log('🔍 Buscando animais:', termo)

    const termoLower = termo.toLowerCase()
    const resultados = animais.value.filter(animal => {
      const animalComDescricao = animal as any
      return animal.nome.toLowerCase().includes(termoLower) ||
             animal.tipo.toLowerCase().includes(termoLower) ||
             animal.codigoSimplesVet.toString().includes(termo) ||
             (animalComDescricao.animalDescription && animalComDescricao.animalDescription.toLowerCase().includes(termoLower))
    }).slice(0, 20)

    // Adicionar descrições aos resultados
    resultados.forEach(animal => {
      (animal as any).animalDescription = `${animal.tipo} - Código: ${animal.codigoSimplesVet}`
    })

    animaisFiltrados.value = resultados
    console.log(`🔍 ${resultados.length} animais encontrados!`)
  } catch (error) {
    console.error('❌ Erro ao buscar animais:', error)
  } finally {
    carregandoAnimais.value = false
  }
}

const buscarServicos = async (termo: string): Promise<void> => {
  if (termo.length < 2) {
    servicosFiltrados.value = servicos.value.slice(0, 10)
    return
  }

  try {
    carregandoServicos.value = true
    console.log('🔍 Buscando serviços:', termo)

    const termoLower = termo.toLowerCase()
    const resultados = servicos.value.filter(servico => {
      const servicoComDescricao = servico as any
      return servico.nome.toLowerCase().includes(termoLower) ||
             servico.descricao.toLowerCase().includes(termoLower) ||
             (servicoComDescricao.servicoDescription && servicoComDescricao.servicoDescription.toLowerCase().includes(termoLower))
    }).slice(0, 20)

    // Adicionar descrições aos resultados
    resultados.forEach(servico => {
      const quantidadeText = servico.quantidade > 1 ? ` (${servico.quantidade} banhos)` : ''
      ;(servico as any).servicoDescription = `R$ ${formatarValor(servico.valor)}${quantidadeText}`
    })

    servicosFiltrados.value = resultados
    console.log(`🔍 ${resultados.length} serviços encontrados!`)
  } catch (error) {
    console.error('❌ Erro ao buscar serviços:', error)
  } finally {
    carregandoServicos.value = false
  }
}

const buscarUsuarios = async (termo: string): Promise<void> => {
  if (termo.length < 2) {
    usuariosFiltrados.value = usuarios.value.slice(0, 10)
    return
  }

  try {
    carregandoUsuarios.value = true
    console.log('🔍 Buscando usuários:', termo)

    const termoLower = termo.toLowerCase()
    const resultados = usuarios.value.filter(usuario => {
      const usuarioComDescricao = usuario as any
      return usuario.nome.toLowerCase().includes(termoLower) ||
             usuario.email.toLowerCase().includes(termoLower) ||
             usuario.role.toLowerCase().includes(termoLower) ||
             (usuarioComDescricao.usuarioDescription && usuarioComDescricao.usuarioDescription.toLowerCase().includes(termoLower))
    }).slice(0, 20)

    // Adicionar descrições aos resultados
    resultados.forEach(usuario => {
      (usuario as any).usuarioDescription = `${usuario.email} - ${usuario.role}`
    })

    usuariosFiltrados.value = resultados
    console.log(`🔍 ${resultados.length} usuários encontrados!`)
  } catch (error) {
    console.error('❌ Erro ao buscar usuários:', error)
  } finally {
    carregandoUsuarios.value = false
  }
}

// 📊 Carregar dados iniciais
const carregarAnimais = async (): Promise<void> => {
  try {
    carregandoAnimais.value = true
    console.log('🐕 Carregando lista de animais...')
    animais.value = await animaisService.buscarTodos()
    prepararDadosAnimais()
    console.log(`✅ ${animais.value.length} animais carregados!`)
  } catch (error) {
    console.error('❌ Erro ao carregar animais:', error)
  } finally {
    carregandoAnimais.value = false
  }
}

const carregarServicos = async (): Promise<void> => {
  try {
    carregandoServicos.value = true
    console.log('💼 Carregando lista de serviços...')
    servicos.value = await servicosService.buscarTodos()
    prepararDadosServicos()
    console.log(`✅ ${servicos.value.length} serviços carregados!`)
  } catch (error) {
    console.error('❌ Erro ao carregar serviços:', error)
  } finally {
    carregandoServicos.value = false
  }
}

const carregarUsuarios = async (): Promise<void> => {
  try {
    carregandoUsuarios.value = true
    console.log('👤 Carregando lista de usuários do backend...')
    usuarios.value = await usuariosService.buscarTodos()
    prepararDadosUsuarios()
    console.log(`✅ ${usuarios.value.length} usuários carregados!`)
  } catch (error) {
    console.error('❌ Erro ao carregar usuários:', error)
  } finally {
    carregandoUsuarios.value = false
  }
}

// 💾 Cadastrar animal serviço
const cadastrarAnimalServico = async (): Promise<void> => {
  if (!formularioValido.value) {
    console.warn('⚠️ Formulário inválido')
    return
  }

  try {
    loading.value = true
    console.log('💾 Cadastrando animal serviço...', formulario.value)

    let resultado

    // Usar API completa se há banhos já realizados
    if (formulario.value.banhosUsados > 0) {
      console.log('🔄 Tentando usar API completa para banhos já realizados...')

      const dadosCompletos: CriarAnimalServicoCompleto = {
        dataServico: formulario.value.dataServico,
        dataExpiracao: formulario.value.dataExpiracao || undefined,
        banhosUsados: formulario.value.banhosUsados,
        animalId: Number(formulario.value.animalId),
        servicoId: Number(formulario.value.servicoId),
        usuarioId: Number(formulario.value.usuarioId),
        statusPagamento: formulario.value.statusPagamento,
        dataPagamento: formulario.value.dataPagamento || undefined,
        datasBanhosRealizados: datasBanhosRealizados.value,
        observacoesBanhos: observacoesBanhos.value.filter(obs => obs.trim() !== '')
      }

      console.log('📤 DADOS ENVIADOS PARA BACKEND:')
      console.log('  - Data do Serviço (frontend):', formulario.value.dataServico)
      console.log('  - Datas dos Banhos (frontend):', datasBanhosRealizados.value)
      console.log('  - Objeto completo:', dadosCompletos)
      console.log('  - Formato JSON:', JSON.stringify(dadosCompletos, null, 2))

      try {
        resultado = await animalServicoService.criarCompleto(dadosCompletos)
        console.log('✅ Animal serviço completo cadastrado com sucesso!', resultado)
      } catch (error: any) {
        console.warn('⚠️ API completa falhou, usando fallback para API simples...', error)

        // Fallback: usar API simples e avisar o usuário
        const novoAnimalServico: NovoAnimalServico = {
          dataServico: formulario.value.dataServico,
          dataExpiracao: formulario.value.dataExpiracao || undefined,
          banhosUsados: formulario.value.banhosUsados,
          animal: { id: Number(formulario.value.animalId) },
          servico: { id: Number(formulario.value.servicoId) },
          usuario: { id: Number(formulario.value.usuarioId) }
        }

        resultado = await animalServicoService.criar(novoAnimalServico)

        alert(`⚠️ ATENÇÃO: O serviço foi cadastrado, mas as datas individuais dos ${formulario.value.banhosUsados} banhos não foram registradas automaticamente. Você precisará registrá-las manualmente na tela de detalhes do serviço.\n\nMotivo: Nova funcionalidade ainda não disponível no servidor (${error.message})`)

        console.log('✅ Animal serviço cadastrado com API simples (fallback)', resultado)
      }
    } else {
      // Usar API simples se não há banhos realizados
      const novoAnimalServico: NovoAnimalServico = {
        dataServico: formulario.value.dataServico,
        dataExpiracao: formulario.value.dataExpiracao || undefined,
        banhosUsados: formulario.value.banhosUsados,
        statusPagamento: formulario.value.statusPagamento,
        dataPagamento: formulario.value.dataPagamento || undefined,
        animal: { id: Number(formulario.value.animalId) },
        servico: { id: Number(formulario.value.servicoId) },
        usuario: { id: Number(formulario.value.usuarioId) }
      }

      resultado = await animalServicoService.criar(novoAnimalServico)
      console.log('✅ Animal serviço cadastrado com sucesso!', resultado)
    }

    // 🎉 Preparar dados do sucesso
    animalServicoRegistrado.value = {
      ...resultado,
      animalNome: animais.value.find(a => a.id === Number(formulario.value.animalId))?.nome,
      servicoNome: servicos.value.find(s => s.id === Number(formulario.value.servicoId))?.nome,
      usuarioNome: usuarios.value.find(u => u.id === Number(formulario.value.usuarioId))?.nome
    }

    mostrarSucesso.value = true
  } catch (error) {
    console.error('❌ Erro ao cadastrar animal serviço:', error)
    alert('Erro ao cadastrar animal serviço. Tente novamente.')
  } finally {
    loading.value = false
  }
}

// 🎉 Funções do modal de sucesso
const verListaServicos = (): void => {
  mostrarSucesso.value = false
  router.push('/servicos')
}

const cadastrarOutroServico = (): void => {
  mostrarSucesso.value = false
  animalServicoRegistrado.value = null

  // Limpar formulário (usando data local para evitar problema de timezone)
  formulario.value = {
    animalId: '',
    servicoId: '',
    dataServico: obterDataLocal(),
    dataExpiracao: '',
    banhosUsados: 0,
    usuarioId: '',
    // Campos de pagamento
    statusPagamento: 'em_aberto',
    dataPagamento: ''
  }

  // Limpar datas dos banhos
  datasBanhosRealizados.value = []
  observacoesBanhos.value = []
}

// 👀 Watchers
// Ajustar banhos automaticamente quando serviço mudar
watch(servicoSelecionado, (novoServico) => {
  if (novoServico && formulario.value.banhosUsados > novoServico.quantidade) {
    console.log(`⚠️ Ajustando banhos de ${formulario.value.banhosUsados} para ${novoServico.quantidade} (limite do serviço)`)
    formulario.value.banhosUsados = 0 // Resetar para 0 para evitar confusão
    gerenciarDatasBanhos(0)
  }

  // 🚀 NOVA LÓGICA: Limpar data de expiração se for banho único
  if (novoServico && novoServico.quantidade === 1) {
    console.log('🔄 Banho único detectado - limpando data de expiração')
    formulario.value.dataExpiracao = ''
  }
})

// Gerenciar datas quando banhos usados mudar
watch(() => formulario.value.banhosUsados, (novosLimite) => {
  gerenciarDatasBanhos(novosLimite)
}, { immediate: false })

// 🔄 Lifecycle
onMounted(async () => {
  console.log('🚀 Inicializando cadastro de animal serviço...')
  await Promise.all([
    carregarAnimais(),
    carregarServicos(),
    carregarUsuarios()
  ])

  // Definir data padrão como hoje (usando data local para evitar problema de timezone)
  formulario.value.dataServico = obterDataLocal()
  // dataExpiracao fica vazia por padrão (opcional)
})
</script>

<style scoped>
/* 🎨 Animações Ultra Modernas */

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

@keyframes fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slide-up {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes float-delayed {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-15px);
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

@keyframes pulse-slow {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

/* Aplicação das animações */
.fade-in-up {
  animation: fade-in-up 0.8s ease-out;
}

.animate-fade-in {
  animation: fade-in 0.6s ease-out forwards;
}

.animate-slide-up {
  animation: slide-up 0.7s ease-out forwards;
}

.animate-float {
  animation: float 3s ease-in-out infinite;
}

.animate-float-delayed {
  animation: float-delayed 4s ease-in-out infinite;
  animation-delay: 1s;
}

.animate-bounce-gentle {
  animation: bounce-gentle 2s ease-in-out infinite;
}

.animate-pulse-slow {
  animation: pulse-slow 3s ease-in-out infinite;
}

/* Efeitos de hover ultra suaves */
.group:hover .group-hover\:shadow-glow {
  box-shadow: 0 0 30px rgba(245, 158, 11, 0.4);
}

/* Gradientes personalizados */
.bg-gradient-primary {
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 50%, #eab308 100%);
}

.shadow-3xl {
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

/* Efeitos de glassmorphism */
.backdrop-blur-ultra {
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

/* Animação de loading skeleton */
.loading-skeleton {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

/* Responsividade aprimorada */
@media (max-width: 640px) {
  .grid-cols-1 {
    grid-template-columns: 1fr;
  }

  /* Ajustes para mobile */
  .fade-in-up {
    animation-duration: 0.6s;
  }

  .animate-slide-up {
    animation-duration: 0.5s;
  }
}

/* Efeitos de partículas */
@keyframes particle-float {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100px) rotate(180deg);
    opacity: 0;
  }
}

.animate-particle {
  animation: particle-float 4s ease-out infinite;
}

/* Efeito de typing para textos */
@keyframes typing {
  from {
    width: 0;
  }
  to {
    width: 100%;
  }
}

.animate-typing {
  overflow: hidden;
  white-space: nowrap;
  animation: typing 2s steps(30) 1s forwards;
}

/* Melhorias nos inputs */
.input-glow:focus {
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1), 0 0 20px rgba(245, 158, 11, 0.2);
}
</style>
