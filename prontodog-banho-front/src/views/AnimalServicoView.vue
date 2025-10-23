<template>
  <div class="min-h-screen bg-gradient-to-br from-amber-50 via-white to-orange-100">
    <!-- 🌟 Header com gradiente elegante -->
    <div class="relative overflow-hidden bg-gradient-to-r from-amber-600 via-orange-600 to-amber-700 text-white pt-16">
      <!-- Background pattern -->
      <div class="absolute inset-0 opacity-10 bg-pattern"></div>

      <div class="relative px-6 py-8 z-20">
        <div class="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-6">
          <div class="flex items-center gap-4 animate-fade-in-up">
            <!-- Ícone animado -->
            <div class="relative">
              <div class="w-16 h-16 bg-white bg-opacity-20 backdrop-blur-sm rounded-2xl flex items-center justify-center transform hover:scale-110 transition-all duration-300 shadow-lg">
                <FontAwesomeIcon icon="clipboard-list" class="text-2xl text-white animate-bounce-gentle" />
              </div>
              <div v-if="!loading && animalServicos.length > 0" class="absolute -top-1 -right-1 w-8 h-8 bg-yellow-400 rounded-full flex items-center justify-center animate-pulse">
                <span class="text-xs font-bold text-yellow-800">{{ animalServicos.length }}</span>
              </div>
            </div>

            <div class="space-y-1">
              <h1 class="text-4xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-amber-100">
                Animal Serviços
              </h1>
              <p class="text-white text-lg flex items-center gap-2 font-medium opacity-90">
                <FontAwesomeIcon icon="star" class="text-yellow-400 animate-twinkle mr-1" />
                {{ loading ? 'Carregando...' : `${animalServicosFiltrados.length} ${animalServicosFiltrados.length === 1 ? 'serviço encontrado' : 'serviços encontrados'}` }}
              </p>
            </div>
          </div>

          <!-- Botões de ação elegantes -->
          <div class="flex items-center gap-3">
            <button
              @click="carregarAnimalServicos"
              :disabled="loading"
              class="group flex items-center gap-2 px-4 py-2 bg-white bg-opacity-20 backdrop-blur-sm text-white rounded-xl hover:bg-opacity-30 transition-all duration-300 border border-white border-opacity-20 hover:border-opacity-40 transform hover:-translate-y-1 hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <FontAwesomeIcon
                :icon="loading ? 'spinner' : 'refresh'"
                :class="{ 'animate-spin': loading, 'group-hover:rotate-180': !loading }"
                class="transition-transform duration-300"
              />
              <span class="font-medium">{{ loading ? 'Carregando...' : 'Atualizar' }}</span>
            </button>

            <button
              @click="$router.push('/animal-servico/novo')"
              class="group flex items-center gap-2 px-6 py-2 bg-gradient-to-r from-yellow-400 to-orange-500 text-white rounded-xl hover:from-yellow-500 hover:to-orange-600 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-xl font-medium shadow-lg"
            >
              <FontAwesomeIcon icon="plus" class="group-hover:rotate-90 transition-transform duration-300" />
              <span>Novo Serviço</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Wave decoration -->
      <div class="absolute bottom-0 left-0 right-0 z-10">
        <svg viewBox="0 0 1440 60" fill="none" xmlns="http://www.w3.org/2000/svg" class="w-full h-15">
          <defs>
            <linearGradient id="animalServicoWaveGradient" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" style="stop-color:#3b82f6;stop-opacity:1" />
              <stop offset="50%" style="stop-color:#1d4ed8;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#1e40af;stop-opacity:1" />
            </linearGradient>
          </defs>
          <path d="M0 60h1440V0c-120 40-240 60-360 60S840 40 720 60s-240 0-360-60S120 40 0 60z" fill="url(#animalServicoWaveGradient)"/>
        </svg>
      </div>
    </div>

    <!-- Container principal -->
    <div class="relative -mt-8 px-6 pb-8 z-30">

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
      <div v-else-if="!animalServicos.length && !loading" class="p-12 text-center">
        <BaseCard class="bg-gradient-to-br from-amber-50 to-orange-50 border-0 shadow-lg">
          <div class="p-8">
            <div class="relative">
              <div class="w-20 h-20 bg-gradient-to-br from-amber-400 to-orange-500 rounded-2xl flex items-center justify-center mx-auto mb-6 animate-bounce-gentle">
                <FontAwesomeIcon icon="clipboard-list" class="text-3xl text-white" />
              </div>
              <div class="absolute top-0 left-1/2 transform -translate-x-1/2 w-20 h-20 bg-amber-200 rounded-2xl animate-ping opacity-20"></div>
            </div>
            <h3 class="text-xl font-semibold text-gray-700 mb-2">Nenhum serviço encontrado</h3>
            <p class="text-gray-500 mb-6">
              Que tal cadastrar o primeiro animal serviço no sistema? 🐾
            </p>
            <button
              @click="$router.push('/animal-servico/novo')"
              class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-amber-500 to-orange-500 text-white rounded-xl hover:from-amber-600 hover:to-orange-600 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg mx-auto font-medium"
            >
              <FontAwesomeIcon icon="plus" />
              <span>Cadastrar primeiro serviço</span>
            </button>
          </div>
        </BaseCard>
      </div>

      <!-- 🔍 Estado de busca sem resultados -->
      <div v-else-if="!loading && animalServicos.length > 0 && animalServicosFiltrados.length === 0" class="p-12 text-center">
        <BaseCard class="bg-gradient-to-br from-red-50 to-pink-50 border-0 shadow-lg">
          <div class="p-8">
            <div class="relative">
              <div class="w-20 h-20 rounded-2xl flex items-center justify-center mx-auto mb-6 animate-bounce-gentle"
                :class="infoFiltroAtivo ? 'bg-gradient-to-br from-red-400 to-pink-500' : 'bg-gradient-to-br from-amber-400 to-orange-500'"
              >
                <FontAwesomeIcon
                  :icon="infoFiltroAtivo ? 'search' : 'clipboard-list'"
                  class="text-3xl text-white"
                />
              </div>
              <div class="absolute top-0 left-1/2 transform -translate-x-1/2 w-20 h-20 rounded-2xl animate-ping opacity-20"
                :class="infoFiltroAtivo ? 'bg-red-200' : 'bg-amber-200'"
              ></div>
            </div>

            <!-- Mensagem personalizada baseada no filtro -->
            <template v-if="infoFiltroAtivo">
              <h3 class="text-xl font-semibold text-gray-700 mb-2">
                Nenhum resultado para {{ infoFiltroAtivo.tipo.toLowerCase() }}
              </h3>
              <p class="text-gray-600 mb-4">{{ infoFiltroAtivo.descricao }}</p>
              <div class="flex items-center justify-center gap-2 px-4 py-2 bg-red-100 rounded-lg mb-6 max-w-md mx-auto">
                <FontAwesomeIcon :icon="infoFiltroAtivo.icone" class="text-red-600" />
                <code class="text-red-800 font-medium">{{ filtroTexto }}</code>
              </div>
              <button
                @click="filtroTexto = ''"
                class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-red-500 to-pink-500 text-white rounded-xl hover:from-red-600 hover:to-pink-600 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg mx-auto font-medium"
              >
                <FontAwesomeIcon icon="times" />
                <span>Limpar filtro de busca</span>
              </button>
            </template>

            <!-- Mensagem para filtro de expiração sem resultados -->
            <template v-else-if="filtroExpiracao !== 'todos'">
              <h3 class="text-xl font-semibold text-gray-700 mb-2">
                Nenhum pacote {{ getFiltroExpiracaoTexto() }}
              </h3>
              <p class="text-gray-600 mb-4">
                Não há pacotes que correspondam ao filtro selecionado no momento.
              </p>
              <div class="flex items-center justify-center gap-2 px-4 py-2 bg-violet-100 rounded-lg mb-6 max-w-md mx-auto">
                <FontAwesomeIcon :icon="['fas', 'filter']" class="text-violet-600" />
                <span class="text-violet-800 font-medium">Filtro: {{ getFiltroExpiracaoTexto() }}</span>
              </div>
              <div class="flex flex-col sm:flex-row gap-3 justify-center">
                <button
                  @click="filtroExpiracao = 'todos'"
                  class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-violet-500 to-purple-500 text-white rounded-xl hover:from-violet-600 hover:to-purple-600 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg font-medium"
                >
                  <FontAwesomeIcon icon="times" />
                  <span>Remover filtro</span>
                </button>
                <button
                  @click="$router.push('/animal-servico/novo')"
                  class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-amber-500 to-orange-500 text-white rounded-xl hover:from-amber-600 hover:to-orange-600 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg font-medium"
                >
                  <FontAwesomeIcon icon="plus" />
                  <span>Cadastrar novo</span>
                </button>
              </div>
            </template>

            <!-- Mensagem genérica para outros casos -->
            <template v-else>
              <h3 class="text-xl font-semibold text-gray-700 mb-2">
                Nenhum resultado encontrado
              </h3>
              <p class="text-gray-600 mb-4">
                Nenhum animal serviço corresponde aos filtros aplicados.
              </p>
              <button
                @click="limparTodosFiltros"
                class="flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-gray-500 to-slate-500 text-white rounded-xl hover:from-gray-600 hover:to-slate-600 transition-all duration-300 transform hover:-translate-y-1 hover:shadow-lg mx-auto font-medium"
              >
                <FontAwesomeIcon icon="refresh" />
                <span>Limpar todos os filtros</span>
              </button>
            </template>
          </div>
        </BaseCard>
      </div>

      <!-- ✅ Lista de Animal Serviços -->
      <div v-else>
        <!-- 🔍 Filtros elegantes -->
        <BaseCard class="mb-6 shadow-lg border-0 bg-white bg-opacity-90 backdrop-blur-sm animate-slide-up">
          <div class="p-4">
            <div class="flex flex-col lg:flex-row gap-4 items-center">
              <!-- Campo de busca principal -->
              <div class="flex-1 relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <FontAwesomeIcon icon="search" class="h-5 w-5 text-amber-400" />
                </div>
                <input
                  v-model="filtroTexto"
                  type="text"
                  placeholder="Digite nome ou use: #123 (ID), @animal (Animal), %pacote (Serviço)..."
                  class="w-full pl-10 pr-4 py-3 bg-gradient-to-r from-white to-amber-50 border border-amber-200 rounded-xl focus:ring-2 focus:ring-amber-500 focus:border-amber-500 transition-all duration-300 placeholder-gray-400 text-gray-700 shadow-sm"
                />
              </div>

              <!-- 💡 Dicas dos filtros inteligentes -->
              <div v-if="filtroTexto && !loading" class="w-full lg:w-auto">
                <div class="flex flex-wrap items-center gap-2 px-3 py-2 bg-gradient-to-r from-amber-50 to-orange-50 rounded-lg border border-amber-100">
                  <FontAwesomeIcon icon="lightbulb" class="text-amber-500 text-sm" />
                  <span class="text-xs text-amber-700 font-medium">Filtros:</span>
                  <div class="flex flex-wrap gap-2 text-xs">
                    <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-amber-700">
                      <FontAwesomeIcon icon="hashtag" class="mr-1" /><code>#123</code> = ID
                    </span>
                    <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-amber-700">
                      <FontAwesomeIcon icon="at" class="mr-1" /><code>@Rex</code> = Animal
                    </span>
                    <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-amber-700">
                      <FontAwesomeIcon icon="percent" class="mr-1" /><code>%Pacote</code> = Serviço
                    </span>
                    <span class="px-2 py-1 bg-white bg-opacity-60 rounded-md text-amber-700">
                      <FontAwesomeIcon icon="user" class="mr-1" /><code>Maria</code> = Dono
                    </span>
                  </div>
                </div>
              </div>

              <!-- 🗓️ Filtro de Expiração -->
              <div class="relative">
                <select
                  v-model="filtroExpiracao"
                  class="px-4 py-3 bg-white border-2 border-violet-200 rounded-xl focus:ring-2 focus:ring-violet-500 focus:border-violet-500 transition-all duration-300 text-gray-700 font-medium shadow-sm appearance-none pr-10"
                  :class="{
                    'border-red-300 bg-red-50 text-red-700': filtroExpiracao === 'vencidos',
                    'border-yellow-300 bg-yellow-50 text-yellow-700': filtroExpiracao === 'vencendo',
                    'border-green-300 bg-green-50 text-green-700': filtroExpiracao === 'validos',
                    'border-gray-300 bg-gray-50 text-gray-700': filtroExpiracao === 'sem-expiracao'
                  }"
                >
                  <option value="todos">📋 Todos ({{ contadoresExpiracao.total }})</option>
                  <option value="vencidos" :class="{ 'text-red-700 font-bold': contadoresExpiracao.vencidos > 0 }">
                    🚨 Pacotes Vencidos ({{ contadoresExpiracao.vencidos }})
                  </option>
                  <option value="vencendo" :class="{ 'text-yellow-700 font-bold': contadoresExpiracao.vencendo > 0 }">
                    ⏰ Pacotes Vencendo ({{ contadoresExpiracao.vencendo }})
                  </option>
                  <option value="validos">🟢 Pacotes Válidos ({{ contadoresExpiracao.validos }})</option>
                  <option value="sem-expiracao">📅 Banhos Únicos + Sem Data ({{ contadoresExpiracao.semExpiracao }})</option>
                </select>
                <!-- Ícone dropdown customizado -->
                <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                  <FontAwesomeIcon :icon="['fas', 'chevron-down']" class="text-gray-400 text-sm" />
                </div>
              </div>

              <!-- Stats rápidas -->
              <div class="flex items-center gap-3 flex-wrap">
                <!-- Filtro Ativo (aparece quando há busca) -->
                <div v-if="infoFiltroAtivo" class="flex items-center gap-2 px-4 py-2 rounded-full shadow-sm border-2 animate-pulse"
                  :class="{
                    'bg-blue-50 border-blue-200 text-blue-700': infoFiltroAtivo.cor === 'blue',
                    'bg-green-50 border-green-200 text-green-700': infoFiltroAtivo.cor === 'green',
                    'bg-purple-50 border-purple-200 text-purple-700': infoFiltroAtivo.cor === 'purple',
                    'bg-amber-50 border-amber-200 text-amber-700': infoFiltroAtivo.cor === 'amber'
                  }"
                >
                  <FontAwesomeIcon :icon="infoFiltroAtivo.icone" class="text-sm" />
                  <span class="text-sm font-bold">{{ infoFiltroAtivo.tipo }}</span>
                  <code class="text-xs bg-white bg-opacity-60 px-1.5 py-0.5 rounded">{{ infoFiltroAtivo.valor }}</code>
                </div>

                <!-- Stats normais -->
                <div class="flex items-center gap-2 px-3 py-1.5 bg-gradient-to-r from-amber-100 to-orange-100 rounded-full">
                  <FontAwesomeIcon icon="clipboard-list" class="text-amber-600 text-sm" />
                  <span class="text-sm font-medium text-amber-700">{{ animalServicosFiltrados.length }} encontrados</span>
                </div>

                <div v-if="!infoFiltroAtivo" class="flex items-center gap-2 px-3 py-1.5 bg-gradient-to-r from-blue-100 to-indigo-100 rounded-full">
                  <FontAwesomeIcon icon="calendar-days" class="text-blue-600 text-sm" />
                  <span class="text-sm font-medium text-blue-700">{{ totalServicosAtivos }} ativos</span>
                </div>
              </div>
            </div>
          </div>
        </BaseCard>

        <!-- 📊 Lista elegante de animal serviços -->
        <div ref="listaAnimalServicosRef" class="space-y-4 mb-4">
          <div
            v-for="(animalServico, index) in animalServicosPaginados"
            :key="animalServico.id"
            class="group relative rounded-xl shadow-lg hover:shadow-2xl transform transition-all duration-200 hover:-translate-y-1 animate-fade-in overflow-hidden border-2"
            :class="{
              // 🎯 SERVIÇOS ÚNICOS - Estilos específicos
              // ✅ Serviço único realizado
              'bg-gradient-to-r from-green-50 via-green-50 to-emerald-100 border-green-200':
                isServicoUnico(animalServico) && getStatusServicoUnico(animalServico) === 'realizado',

              // ⏳ Serviço único pendente
              'bg-gradient-to-r from-gray-50 via-gray-50 to-slate-100 border-gray-200':
                isServicoUnico(animalServico) && getStatusServicoUnico(animalServico) === 'pendente',

              // 📦 PACOTES - Estilos existentes (apenas para não-únicos)
              // 🚨 Card urgente - vencido (apenas para pacotes)
              'bg-gradient-to-r from-red-50 via-red-50 to-red-100 border-red-200 animate-pulse animate-urgent-glow':
                !isServicoUnico(animalServico) && getTotalBanhos(animalServico) > 1 && getExpirationStatus(animalServico) === 'vencido',

              // ⚠️ Card atenção - vencendo (apenas pacotes) ou poucos banhos
              'bg-gradient-to-r from-yellow-50 via-yellow-50 to-yellow-100 border-yellow-200':
                !isServicoUnico(animalServico) && ((getTotalBanhos(animalServico) > 1 && getExpirationStatus(animalServico) === 'vencendo') ||
                (!isServicoCompleto(animalServico) && getBanhosRestantes(animalServico) <= 1)),

              // ✅ Card completo (apenas pacotes)
              'bg-gradient-to-r from-green-50 via-green-50 to-green-100 border-green-200':
                !isServicoUnico(animalServico) && isServicoCompleto(animalServico),

              // 🔵 Card normal (apenas pacotes)
              'bg-gradient-to-r from-white via-white to-amber-50 border-gray-200':
                !isServicoUnico(animalServico) && !isServicoCompleto(animalServico) &&
                (getTotalBanhos(animalServico) === 1 ||
                 (getTotalBanhos(animalServico) > 1 && getExpirationStatus(animalServico) === 'valido' && getBanhosRestantes(animalServico) > 1)),

              // 📅 Card sem expiração (apenas para pacotes)
              'bg-gradient-to-r from-slate-50 via-slate-50 to-gray-100 border-slate-200':
                !isServicoUnico(animalServico) && getTotalBanhos(animalServico) > 1 && getExpirationStatus(animalServico) === 'sem-expiracao'
            }"
          >
            <div class="p-6">
              <div class="flex items-center justify-between">
                <!-- 🐕 Informações principais -->
                <div class="flex items-center gap-4 flex-1">
                  <!-- Avatar com gradiente -->
                  <div class="relative">
                    <!-- 🎯 Avatar para SERVIÇOS ÚNICOS -->
                    <div v-if="isServicoUnico(animalServico)" class="w-16 h-16 bg-gradient-to-br from-purple-400 to-pink-500 rounded-2xl flex items-center justify-center shadow-lg transform group-hover:scale-105 transition-transform duration-200">
                      <FontAwesomeIcon :icon="['fas', 'star']" class="text-2xl text-white" />
                    </div>
                    <!-- 📦 Avatar para PACOTES (mantém original) -->
                    <div v-else class="w-16 h-16 bg-gradient-to-br from-amber-400 to-green-600 rounded-2xl flex items-center justify-center shadow-lg transform group-hover:scale-105 transition-transform duration-200">
                      <FontAwesomeIcon :icon="['fas', 'clipboard-list']" class="text-2xl text-white" />
                    </div>

                    <!-- Badge condicional -->
                    <!-- 🎯 Badge para SERVIÇOS ÚNICOS (status) -->
                    <div v-if="isServicoUnico(animalServico)" class="absolute -top-1 -right-1 w-8 h-8 rounded-full flex items-center justify-center shadow-md"
                      :class="{
                        'bg-gradient-to-r from-green-400 to-emerald-500': getStatusServicoUnico(animalServico) === 'realizado',
                        'bg-gradient-to-r from-gray-400 to-slate-500': getStatusServicoUnico(animalServico) === 'pendente'
                      }"
                    >
                      <FontAwesomeIcon
                        :icon="getStatusServicoUnico(animalServico) === 'realizado' ? 'check' : 'clock'"
                        class="text-white text-xs"
                      />
                    </div>
                    <!-- 📦 Badge para PACOTES (banhos usados - mantém original) -->
                    <div v-else class="absolute -top-1 -right-1 w-8 h-8 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-full flex items-center justify-center shadow-md">
                      <span class="text-xs font-bold text-white">{{ animalServico.banhosUsados }}</span>
                    </div>
                  </div>

                  <!-- Informações do serviço -->
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-3 mb-1">
                      <!-- 🎯 Título diferente para SERVIÇOS ÚNICOS -->
                      <h3 v-if="isServicoUnico(animalServico)" class="text-xl font-bold text-gray-900 truncate group-hover:text-purple-700 transition-colors duration-300">
                        {{ getAnimalNome(animalServico) }} - {{ getServicoNome(animalServico) }}
                      </h3>
                      <!-- 📦 Título original para PACOTES -->
                      <h3 v-else class="text-xl font-bold text-gray-900 truncate group-hover:text-amber-700 transition-colors duration-300">
                        {{ getAnimalNome(animalServico) }}
                      </h3>

                      <div class="flex items-center gap-2 flex-shrink-0 flex-wrap">
                        <!-- 🎯 Badge de Status para SERVIÇOS ÚNICOS -->
                        <div v-if="isServicoUnico(animalServico)"
                          class="px-2 py-1 rounded-lg text-sm font-medium shadow-sm border-2 transition-all duration-300"
                          :class="{
                            'bg-green-50 border-green-200 text-green-700': getStatusServicoUnico(animalServico) === 'realizado',
                            'bg-gray-50 border-gray-200 text-gray-700': getStatusServicoUnico(animalServico) === 'pendente'
                          }"
                        >
                          <FontAwesomeIcon
                            :icon="getStatusServicoUnico(animalServico) === 'realizado' ? 'check-circle' : 'clock'"
                            class="opacity-70"
                          />
                          {{ getStatusServicoUnico(animalServico) === 'realizado' ? 'Realizado' : '⏳ Pendente' }}
                        </div>

                        <!-- 📦 Badge de Status para PACOTES (mantém original) -->
                        <div v-else
                          class="px-2 py-1 rounded-lg text-xs font-medium shadow-sm border transition-all duration-300"
                          :class="{
                            'bg-green-50 border-green-200 text-green-700': isServicoCompleto(animalServico),
                            'bg-blue-50 border-blue-200 text-blue-700': !isServicoCompleto(animalServico)
                          }"
                        >
                          <FontAwesomeIcon
                            :icon="isServicoCompleto(animalServico) ? 'check-circle' : 'clock'"
                            class="mr-1 opacity-60"
                          />
                          {{ isServicoCompleto(animalServico) ? 'Completo' : 'Em Andamento' }}
                        </div>

                        <!-- 🛁 Badge de Banhos Restantes (APENAS para PACOTES) -->
                        <div
                          v-if="!isServicoUnico(animalServico) && !isServicoCompleto(animalServico)"
                          class="px-2 py-1 rounded-lg text-xs font-medium shadow-sm border transition-all duration-300"
                          :class="{
                            'bg-red-50 border-red-200 text-red-600 animate-pulse': getBanhosRestantes(animalServico) <= 1,
                            'bg-yellow-50 border-yellow-200 text-yellow-700': getBanhosRestantes(animalServico) === 2,
                            'bg-blue-50 border-blue-200 text-blue-600': getBanhosRestantes(animalServico) > 2
                          }"
                        >
                          <FontAwesomeIcon icon="bath" class="mr-1 opacity-60" />
                          {{ getBanhosRestantes(animalServico) }} restante{{ getBanhosRestantes(animalServico) !== 1 ? 's' : '' }}
                        </div>

                        <!-- 📦 Badge de Tipo de Serviço -->
                        <div class="px-2 py-1 rounded-lg text-xs font-medium bg-gray-50 border border-gray-200 text-gray-600 shadow-sm">
                          <FontAwesomeIcon
                            :icon="getTotalBanhos(animalServico) > 1 ? 'box' : 'droplet'"
                            class="mr-1 opacity-60"
                          />
                          {{ getServicoDescricao(animalServico) }}
                        </div>

                        <!-- 🗓️ Badge de Status de Expiração (APENAS para PACOTES) -->
                        <div
                          v-if="!isServicoUnico(animalServico) && animalServico.dataExpiracao && getTotalBanhos(animalServico) > 1"
                          class="px-2 py-1 rounded-lg text-xs font-medium shadow-sm border transition-all duration-300"
                          :class="{
                            'bg-red-50 border-red-200 text-red-600 animate-bounce': getExpirationStatus(animalServico) === 'vencido',
                            'bg-yellow-50 border-yellow-200 text-yellow-600 animate-pulse': getExpirationStatus(animalServico) === 'vencendo',
                            'bg-green-50 border-green-200 text-green-600': getExpirationStatus(animalServico) === 'valido'
                          }"
                        >
                          <span v-if="getExpirationStatus(animalServico) === 'vencido'">
                            🚨 Vencido há {{ getDaysExpired(animalServico) }}d
                          </span>
                          <span v-else-if="getExpirationStatus(animalServico) === 'vencendo'">
                            ⏰ {{ getDaysUntilExpiration(animalServico) }}d restantes
                          </span>
                          <span v-else>
                            {{ getDaysUntilExpiration(animalServico) }}d restantes
                          </span>
                        </div>

                        <!-- 💳 Badge de Status de Pagamento -->
                        <div
                          class="px-2 py-1 rounded-lg text-xs font-medium shadow-sm border transition-all duration-300"
                          :class="{
                            'bg-emerald-50 border-emerald-200 text-emerald-700': animalServico.statusPagamento === 'pago',
                            'bg-orange-50 border-orange-200 text-orange-700 animate-pulse': animalServico.statusPagamento === 'em_aberto',
                            'bg-red-50 border-red-200 text-red-700': animalServico.statusPagamento === 'cancelado'
                          }"
                        >
                          <FontAwesomeIcon
                            :icon="getStatusPagamentoIcon(animalServico.statusPagamento)"
                            class="mr-1 opacity-60"
                          />
                          {{ getStatusPagamentoTexto(animalServico.statusPagamento) }}
                        </div>

                        <!-- 🧾 Badge de Venda Relacionada -->
                        <router-link
                          v-if="animalServico.vendaId"
                          :to="`/vendas/${animalServico.vendaId}`"
                          class="px-3 py-1.5 rounded-lg text-xs font-semibold shadow-sm border transition-all duration-300 bg-gradient-to-r from-indigo-50 to-purple-50 border-indigo-300 text-indigo-700 hover:from-indigo-100 hover:to-purple-100 hover:border-indigo-400 hover:shadow-lg hover:scale-105 flex items-center gap-1.5 whitespace-nowrap"
                          title="Clique para ver a venda completa"
                        >
                          <FontAwesomeIcon
                            :icon="['fas', 'receipt']"
                            class="text-sm"
                          />
                          <span>Venda #{{ animalServico.vendaId }}</span>
                          <FontAwesomeIcon
                            :icon="['fas', 'external-link-alt']"
                            class="text-[10px] opacity-60"
                          />
                        </router-link>
                      </div>
                    </div>

                    <div class="flex items-center gap-4 text-sm text-gray-600 mb-2 flex-wrap">
                      <span class="flex items-center gap-1">
                        <FontAwesomeIcon icon="hashtag" class="text-gray-400" />
                        ID: {{ animalServico.id }}
                      </span>
                      <span class="flex items-center gap-1">
                        <FontAwesomeIcon icon="calendar-alt" class="text-gray-400" />
                        {{ formatarData(animalServico.dataServico) }}
                      </span>
                      <span class="flex items-center gap-1">
                        <FontAwesomeIcon icon="user" class="text-emerald-500" />
                        <span class="text-emerald-700 font-medium">{{ getClienteNome(animalServico) }}</span>
                      </span>
                      <span class="flex items-center gap-1">
                        <FontAwesomeIcon icon="dollar-sign" class="text-blue-500" />
                        <span class="text-blue-700 font-bold">R$ {{ formatarValor(getServicoValor(animalServico)) }}</span>
                      </span>
                    </div>

                    <!-- Progress bar de banhos (APENAS para PACOTES) -->
                    <div v-if="!isServicoUnico(animalServico)" class="flex items-center gap-3">
                      <div class="flex-1 bg-gray-200 rounded-full h-3 shadow-inner">
                        <div
                          class="h-3 rounded-full transition-all duration-300 ease-out"
                          :class="{
                            'bg-gradient-to-r from-green-400 to-emerald-500': isServicoCompleto(animalServico),
                            'bg-gradient-to-r from-red-400 to-red-500 animate-pulse': !isServicoCompleto(animalServico) && getBanhosRestantes(animalServico) <= 1,
                            'bg-gradient-to-r from-yellow-400 to-orange-500': !isServicoCompleto(animalServico) && getBanhosRestantes(animalServico) === 2,
                            'bg-gradient-to-r from-indigo-400 to-purple-500': !isServicoCompleto(animalServico) && getBanhosRestantes(animalServico) > 2
                          }"
                          :style="{ width: `${getProgressoBanhos(animalServico)}%` }"
                        >
                          <!-- Efeito de brilho na barra -->
                          <div class="h-full rounded-full bg-gradient-to-r from-transparent via-white/20 to-transparent animate-shimmer"></div>
                        </div>
                      </div>
                      <div class="flex items-center gap-2">
                        <span class="text-sm font-bold text-gray-700">
                          {{ animalServico.banhosUsados }}/{{ getTotalBanhos(animalServico) }}
                        </span>
                        <span class="text-xs text-gray-500">•</span>
                        <span
                          class="text-xs font-medium"
                          :class="{
                            'text-red-600': !isServicoCompleto(animalServico) && getBanhosRestantes(animalServico) <= 1,
                            'text-yellow-600': !isServicoCompleto(animalServico) && getBanhosRestantes(animalServico) === 2,
                            'text-purple-600': !isServicoCompleto(animalServico) && getBanhosRestantes(animalServico) > 2,
                            'text-green-600': isServicoCompleto(animalServico)
                          }"
                        >
                          {{ Math.round(getProgressoBanhos(animalServico)) }}%
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 🎯 Status e Ícone Clicável -->
                <div class="flex items-center gap-3 ml-4">
                  <!-- Status badge -->
                  <!-- 🎯 Status para SERVIÇOS ÚNICOS -->
                  <BaseBadge
                    v-if="isServicoUnico(animalServico)"
                    :variant="getStatusServicoUnico(animalServico) === 'realizado' ? 'success' : 'secondary'"
                    size="md"
                  >
                    {{ getStatusServicoUnico(animalServico) === 'realizado' ? 'Realizado' : 'Pendente' }}
                  </BaseBadge>

                  <!-- 📦 Status para PACOTES (mantém original) -->
                  <BaseBadge
                    v-else
                    :variant="isServicoCompleto(animalServico) ? 'success' : 'warning'"
                    size="md"
                  >
                    {{ isServicoCompleto(animalServico) ? 'Completo' : 'Em Andamento' }}
                  </BaseBadge>

                  <!-- 🚀 Ações Rápidas -->
                  <div class="flex items-center gap-2">
                    <!-- 🎯 Botão para SERVIÇOS ÚNICOS - Marcar como Realizado (quando PENDENTE) -->
                    <button
                      v-if="isServicoUnico(animalServico) && getStatusServicoUnico(animalServico) === 'pendente'"
                      @click.stop="abrirModalBanhoRapido(animalServico)"
                      class="group/btn flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-green-500 to-emerald-500 text-white rounded-lg hover:from-green-600 hover:to-emerald-600 transition-all duration-200 transform hover:scale-105 shadow-md hover:shadow-lg"
                      title="Marcar como realizado"
                    >
                      <FontAwesomeIcon :icon="['fas', 'check']" class="text-sm" />
                      <span class="text-xs font-bold hidden sm:inline">Realizado</span>
                    </button>

                    <!-- 🔄 Botão para SERVIÇOS ÚNICOS - Marcar como Pendente (quando REALIZADO) -->
                    <button
                      v-if="isServicoUnico(animalServico) && getStatusServicoUnico(animalServico) === 'realizado'"
                      @click.stop="marcarServicoUnicoComoPendente(animalServico)"
                      class="group/btn flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-gray-500 to-gray-600 text-white rounded-lg hover:from-gray-600 hover:to-gray-700 transition-all duration-200 transform hover:scale-105 shadow-md hover:shadow-lg"
                      title="Marcar como pendente"
                    >
                      <FontAwesomeIcon :icon="['fas', 'undo']" class="text-sm" />
                      <span class="text-xs font-bold hidden sm:inline">Pendente</span>
                    </button>

                    <!-- 📦 Botão para PACOTES - Registrar Banho (mantém original) -->
                    <button
                      v-if="!isServicoUnico(animalServico) && !isServicoCompleto(animalServico)"
                      @click.stop="abrirModalBanhoRapido(animalServico)"
                      class="group/btn flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-violet-500 to-purple-500 text-white rounded-lg hover:from-violet-600 hover:to-purple-600 transition-all duration-200 transform hover:scale-105 shadow-md hover:shadow-lg"
                      title="Registrar banho do pacote"
                    >
                      <FontAwesomeIcon :icon="['fas', 'bath']" class="text-sm" />
                      <span class="text-xs font-bold hidden sm:inline">Banho</span>
                    </button>

                    <!-- Ver detalhes -->
                    <button
                      @click.stop="verDetalhesAnimalServico(animalServico)"
                      class="group/btn flex items-center gap-1 px-3 py-2 text-blue-600 hover:text-blue-700 hover:bg-blue-50 rounded-lg transition-all duration-200 transform hover:scale-105"
                      title="Ver detalhes"
                    >
                      <FontAwesomeIcon :icon="['fas', 'eye']" class="text-sm" />
                    </button>

                    <!-- Menu de ações (⋮) -->
                    <div class="relative">
                      <button
                        @click.stop="toggleMenuAcoes(animalServico.id)"
                        class="group/btn flex items-center justify-center w-8 h-8 text-gray-600 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-all duration-200 transform hover:scale-105"
                        title="Mais ações"
                      >
                        <FontAwesomeIcon :icon="['fas', 'ellipsis-vertical']" class="text-sm" />
                      </button>

                      <!-- Dropdown Menu -->
                      <div
                        v-if="menuAcoesAberto === animalServico.id"
                        class="absolute right-0 top-full mt-1 w-48 bg-white rounded-lg shadow-xl border border-gray-200 z-50 py-1"
                        @click.stop
                      >
                        <!-- Editar Expiração (só para pacotes) -->
                        <button
                          v-if="getTotalBanhos(animalServico) > 1"
                          @click="editarExpiracao(animalServico)"
                          class="w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-violet-50 hover:text-violet-700 transition-colors flex items-center gap-3"
                        >
                          <FontAwesomeIcon :icon="['fas', 'clock']" class="text-violet-500" />
                          Editar Expiração
                        </button>

                          <!-- Duplicar Serviço -->
                          <button
                            @click="duplicarServico(animalServico)"
                            class="w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-700 transition-colors flex items-center gap-3"
                          >
                            <FontAwesomeIcon :icon="['fas', 'copy']" class="text-blue-500" />
                            Duplicar Serviço
                          </button>

                          <hr class="my-1 border-gray-100">

                        <!-- Excluir -->
                        <button
                          @click="confirmarExclusao(animalServico)"
                          class="w-full px-4 py-2 text-left text-sm text-red-600 hover:bg-red-50 hover:text-red-700 transition-colors flex items-center gap-3"
                        >
                          <FontAwesomeIcon :icon="['fas', 'trash']" class="text-red-500" />
                          Excluir Serviço
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Paginação -->
        <BaseCard v-if="totalPaginas > 1" class="shadow-lg border-0 bg-white">
          <div class="p-4 flex flex-col sm:flex-row items-center justify-between gap-4">
            <!-- Info da paginação -->
            <div class="flex items-center gap-2 text-sm text-gray-600">
              <FontAwesomeIcon icon="list" class="text-orange-500" />
              <span>
                Mostrando
                <strong class="text-orange-600">{{ indicePrimeiroItem }}</strong> -
                <strong class="text-orange-600">{{ indiceUltimoItem }}</strong>
                de
                <strong class="text-orange-600">{{ animalServicosFiltrados.length }}</strong>
                serviços
              </span>
            </div>

            <!-- Controles de paginação -->
            <div class="flex items-center gap-2">
              <button @click="paginaAtual = 1" :disabled="paginaAtual === 1" class="px-3 py-2 rounded-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-orange-50" :class="paginaAtual === 1 ? 'text-gray-400' : 'text-orange-600 hover:text-orange-700'">
                <FontAwesomeIcon icon="angle-double-left" />
              </button>
              <button @click="paginaAtual--" :disabled="paginaAtual === 1" class="px-3 py-2 rounded-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-orange-50" :class="paginaAtual === 1 ? 'text-gray-400' : 'text-orange-600 hover:text-orange-700'">
                <FontAwesomeIcon icon="chevron-left" class="mr-1" />
                <span class="hidden sm:inline">Anterior</span>
              </button>
              <div class="flex items-center gap-1">
                <button v-for="pagina in paginasVisiveis" :key="pagina" @click="paginaAtual = pagina" class="w-10 h-10 rounded-lg font-medium transition-all duration-200" :class="paginaAtual === pagina ? 'bg-gradient-to-r from-orange-500 to-amber-600 text-white shadow-md' : 'bg-orange-50 text-orange-600 hover:bg-orange-100'">
                  {{ pagina }}
                </button>
              </div>
              <button @click="paginaAtual++" :disabled="paginaAtual === totalPaginas" class="px-3 py-2 rounded-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-orange-50" :class="paginaAtual === totalPaginas ? 'text-gray-400' : 'text-orange-600 hover:text-orange-700'">
                <span class="hidden sm:inline">Próxima</span>
                <FontAwesomeIcon icon="chevron-right" class="ml-1" />
              </button>
              <button @click="paginaAtual = totalPaginas" :disabled="paginaAtual === totalPaginas" class="px-3 py-2 rounded-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-orange-50" :class="paginaAtual === totalPaginas ? 'text-gray-400' : 'text-orange-600 hover:text-orange-700'">
                <FontAwesomeIcon icon="angle-double-right" />
              </button>
            </div>
            <div class="flex items-center gap-2 text-sm">
              <span class="text-gray-600">Itens por página:</span>
              <select v-model="itensPorPagina" @change="paginaAtual = 1" class="px-3 py-2 border border-orange-200 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-orange-500 text-gray-700 bg-white">
                <option :value="10">10</option>
                <option :value="25">25</option>
                <option :value="50">50</option>
                <option :value="100">100</option>
              </select>
            </div>
          </div>
        </BaseCard>
      </div>
    </div>

    <!-- 🛁 Modal para adicionar banho individual -->
    <BaseModal
      v-model="mostrarModalBanho"
      size="md"
      :title="animalServicoSelecionado && getTotalBanhos(animalServicoSelecionado) === 1
        ? 'Registrar Banho Único'
        : 'Registrar Banho do Pacote'"
    >
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
              Data do Serviço*
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
    <div class="fixed bottom-0 left-0 right-0 overflow-hidden z-10 pointer-events-none">
      <div :class="footerMarginClass" class="transition-all duration-300">
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
  </div>
</template>

<style scoped>
/* 🎨 Animações otimizadas para performance */
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
    transform: translateY(15px);
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
    transform: translateY(-3px);
  }
}

@keyframes twinkle {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

@keyframes urgent-glow {
  0%, 100% {
    box-shadow: 0 0 5px rgba(239, 68, 68, 0.2);
  }
  50% {
    box-shadow: 0 0 15px rgba(239, 68, 68, 0.4);
  }
}

/* Classes de animação otimizadas */
.animate-fade-in {
  animation: fade-in 0.3s ease-out;
}

.animate-slide-up {
  animation: slide-up 0.4s ease-out;
}

.animate-bounce-gentle {
  animation: bounce-gentle 2s ease-in-out infinite;
}

.animate-twinkle {
  animation: twinkle 1.2s ease-in-out infinite;
}

.animate-shimmer {
  animation: shimmer 1.5s ease-in-out infinite;
}

.animate-urgent-glow {
  animation: urgent-glow 1.8s ease-in-out infinite;
}

/* 🌊 Wave background pattern */
.bg-pattern {
  background-image: radial-gradient(circle at 25% 25%, rgba(255, 255, 255, 0.1) 1px, transparent 1px),
                    radial-gradient(circle at 75% 75%, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 30px 30px;
}
</style>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseButton from '@/components/UI/BaseButton.vue'
import BaseInput from '@/components/UI/BaseInput.vue'
import BaseModal from '@/components/UI/BaseModal.vue'
import BaseBadge from '@/components/UI/BaseBadge.vue'
import { animalServicoService, animaisService, servicosService, usuariosService, banhosIndividuaisService, clientesService, servicosAdicionaisService, type NovoBanhoIndividual } from '@/services/api'
import type { AnimalServico, Animal, ServicoCompleto, Usuario } from '@/types/api'
import { useSidebarStore } from '@/stores/sidebar'

const router = useRouter()
const sidebarStore = useSidebarStore()

// 📊 Estados reativas
const loading = ref(false)
const error = ref('')
const animalServicos = ref<AnimalServico[]>([])
const animais = ref<Animal[]>([])
const servicos = ref<ServicoCompleto[]>([])
const usuarios = ref<Usuario[]>([])
const valoresAdicionais = ref<Map<number, number>>(new Map())

// 📋 Estados do modal de banho
const mostrarModalBanho = ref(false)
const salvandoBanho = ref(false)
const animalServicoSelecionado = ref<AnimalServico | null>(null)
const formularioBanho = ref({
  dataBanho: '',
  observacoes: ''
})

// 🚀 Estados das ações rápidas
const menuAcoesAberto = ref<number | null>(null)

// 🔍 Filtros
const filtroTexto = ref('')
const filtroExpiracao = ref('todos') // 'todos', 'vencidos', 'vencendo', 'validos', 'sem-expiracao'
const itensPorPagina = ref(10)
const paginaAtual = ref(1)

// 🗓️ Funções para determinar status de expiração (apenas para pacotes)
const getExpirationStatus = (animalServico: AnimalServico): 'vencido' | 'vencendo' | 'valido' | 'sem-expiracao' => {
  // Banhos únicos não têm controle de expiração
  if (getTotalBanhos(animalServico) === 1) return 'sem-expiracao'

  if (!animalServico.dataExpiracao) return 'sem-expiracao'

  const hoje = new Date()
  const [ano, mes, dia] = animalServico.dataExpiracao.split('-')
  const dataExpiracao = new Date(Number(ano), Number(mes) - 1, Number(dia))

  if (hoje > dataExpiracao) {
    return 'vencido'
  }

  const diasParaExpirar = Math.ceil((dataExpiracao.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24))
  if (diasParaExpirar <= 7) {
    return 'vencendo'
  }

  return 'valido'
}

const getDaysUntilExpiration = (animalServico: AnimalServico): number => {
  if (!animalServico.dataExpiracao) return 0

  const hoje = new Date()
  const [ano, mes, dia] = animalServico.dataExpiracao.split('-')
  const dataExpiracao = new Date(Number(ano), Number(mes) - 1, Number(dia))

  return Math.ceil((dataExpiracao.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24))
}

const getDaysExpired = (animalServico: AnimalServico): number => {
  if (!animalServico.dataExpiracao) return 0

  const hoje = new Date()
  const [ano, mes, dia] = animalServico.dataExpiracao.split('-')
  const dataExpiracao = new Date(Number(ano), Number(mes) - 1, Number(dia))

  return Math.abs(Math.ceil((hoje.getTime() - dataExpiracao.getTime()) / (1000 * 60 * 60 * 24)))
}

// 💳 Funções para status de pagamento
const getStatusPagamentoIcon = (status: string): string => {
  switch (status) {
    case 'pago':
      return 'check-circle'
    case 'em_aberto':
      return 'clock'
    case 'cancelado':
      return 'times-circle'
    default:
      return 'question-circle'
  }
}

const getStatusPagamentoTexto = (status: string): string => {
  switch (status) {
    case 'pago':
      return 'Pago'
    case 'em_aberto':
      return 'Em Aberto'
    case 'cancelado':
      return 'Cancelado'
    default:
      return 'Indefinido'
  }
}

// 🎨 Computed property para margem do footer (respeitar sidebar)
const footerMarginClass = computed(() => {
  // Margem esquerda baseada no estado da sidebar
  return sidebarStore.isOpen ? 'ml-64' : 'ml-20'
})

// 📄 Lista com filtros inteligentes aplicados
const animalServicosFiltrados = computed(() => {
  return animalServicos.value.filter(animalServico => {
    // 🗓️ FILTRO DE EXPIRAÇÃO
    if (filtroExpiracao.value !== 'todos') {
      const status = getExpirationStatus(animalServico)
      if (filtroExpiracao.value === 'vencidos' && status !== 'vencido') return false
      if (filtroExpiracao.value === 'vencendo' && status !== 'vencendo') return false
      if (filtroExpiracao.value === 'validos' && status !== 'valido') return false
      if (filtroExpiracao.value === 'sem-expiracao' && status !== 'sem-expiracao') return false
    }

    // 🔍 FILTRO DE TEXTO
    if (!filtroTexto.value) return true

    // 🧠 FILTROS INTELIGENTES com prefixos especiais
    const termoBusca = filtroTexto.value.trim()

    // 🚀 FILTRO INTELIGENTE: Detecta prefixos especiais
    if (termoBusca.startsWith('#')) {
      // 🆔 Busca por ID do Serviço: #123
      const idBusca = termoBusca.substring(1)
      if (idBusca) {
        return animalServico.id.toString().includes(idBusca)
      } else {
        return true // Se apenas # foi digitado, mostra todos
      }
    } else if (termoBusca.startsWith('@')) {
      // 🐕 Busca por Nome do Animal: @Rex
      const animalBusca = termoBusca.substring(1)
      if (animalBusca) {
        const animalNome = getAnimalNome(animalServico).toLowerCase()
        return animalNome.includes(animalBusca.toLowerCase())
      } else {
        return true // Se apenas @ foi digitado, mostra todos
      }
    } else if (termoBusca.startsWith('%')) {
      // 🎯 Busca por Tipo de Serviço: %Pacote
      const servicoBusca = termoBusca.substring(1)
      if (servicoBusca) {
        const servicoNome = getServicoNome(animalServico).toLowerCase()
        const servicoDescricao = getServicoDescricao(animalServico).toLowerCase()
        return servicoNome.includes(servicoBusca.toLowerCase()) ||
               servicoDescricao.includes(servicoBusca.toLowerCase())
      } else {
        return true // Se apenas % foi digitado, mostra todos
      }
    } else {
      // 🔍 BUSCA NORMAL: Por nome do dono
      const termoBuscaLower = termoBusca.toLowerCase()

      // 👤 Busca por nome do cliente (dono)
      const clienteNome = getClienteNome(animalServico).toLowerCase()

      return clienteNome.includes(termoBuscaLower)
    }
  })
})

// Computed properties para paginação
const totalPaginas = computed(() => {
  return Math.ceil(animalServicosFiltrados.value.length / itensPorPagina.value)
})

const indicePrimeiroItem = computed(() => {
  return (paginaAtual.value - 1) * itensPorPagina.value + 1
})

const indiceUltimoItem = computed(() => {
  const ultimo = paginaAtual.value * itensPorPagina.value
  return ultimo > animalServicosFiltrados.value.length ? animalServicosFiltrados.value.length : ultimo
})

const animalServicosPaginados = computed(() => {
  const inicio = (paginaAtual.value - 1) * itensPorPagina.value
  const fim = inicio + itensPorPagina.value
  return animalServicosFiltrados.value.slice(inicio, fim)
})

const paginasVisiveis = computed(() => {
  const total = totalPaginas.value
  const atual = paginaAtual.value
  const paginas: number[] = []

  if (total <= 7) {
    for (let i = 1; i <= total; i++) {
      paginas.push(i)
    }
  } else {
    if (atual <= 4) {
      for (let i = 1; i <= 5; i++) {
        paginas.push(i)
      }
      paginas.push(total)
    } else if (atual >= total - 3) {
      paginas.push(1)
      for (let i = total - 4; i <= total; i++) {
        paginas.push(i)
      }
    } else {
      paginas.push(1)
      for (let i = atual - 1; i <= atual + 1; i++) {
        paginas.push(i)
      }
      paginas.push(total)
    }
  }

  return paginas
})

// 📊 Contadores por status de expiração
const contadoresExpiracao = computed(() => {
  const contadores = {
    total: animalServicos.value.length,
    vencidos: 0,
    vencendo: 0,
    validos: 0,
    semExpiracao: 0
  }

  animalServicos.value.forEach(animalServico => {
    const status = getExpirationStatus(animalServico)
    switch (status) {
      case 'vencido':
        contadores.vencidos++
        break
      case 'vencendo':
        contadores.vencendo++
        break
      case 'valido':
        contadores.validos++
        break
      case 'sem-expiracao':
        contadores.semExpiracao++
        break
    }
  })

  return contadores
})

// 🎯 Informações sobre o filtro ativo
const infoFiltroAtivo = computed(() => {
  const termo = filtroTexto.value?.trim()
  if (!termo) return null

  if (termo.startsWith('#')) {
    const id = termo.substring(1)
    return {
      tipo: 'ID Serviço',
      icone: 'hashtag',
      cor: 'blue',
      valor: id || '...',
      descricao: id ? `Buscando ID do serviço: ${id}` : 'Digite o ID do serviço após #'
    }
  } else if (termo.startsWith('@')) {
    const animal = termo.substring(1)
    return {
      tipo: 'Nome Animal',
      icone: 'at',
      cor: 'green',
      valor: animal || '...',
      descricao: animal ? `Buscando animal: ${animal}` : 'Digite o nome do animal após @'
    }
  } else if (termo.startsWith('%')) {
    const servico = termo.substring(1)
    return {
      tipo: 'Tipo Serviço',
      icone: 'percent',
      cor: 'purple',
      valor: servico || '...',
      descricao: servico ? `Buscando serviço: ${servico}` : 'Digite o tipo de serviço após %'
    }
  } else {
    return {
      tipo: 'Nome Dono',
      icone: 'user',
      cor: 'amber',
      valor: termo,
      descricao: `Buscando por dono: "${termo}"`
    }
  }
})

// 📊 Total de serviços ativos (não completos)
const totalServicosAtivos = computed(() => {
  return animalServicos.value.filter(animalServico => !isServicoCompleto(animalServico)).length
})

// 🗓️ Função para obter texto descritivo do filtro de expiração
const getFiltroExpiracaoTexto = (): string => {
  switch (filtroExpiracao.value) {
    case 'vencidos':
      return 'pacotes vencidos'
    case 'vencendo':
      return 'pacotes vencendo em breve'
    case 'validos':
      return 'pacotes válidos'
    case 'sem-expiracao':
      return 'banhos únicos e serviços sem data de expiração'
    default:
      return ''
  }
}

// 🧹 Função para limpar todos os filtros
const limparTodosFiltros = (): void => {
  filtroTexto.value = ''
  filtroExpiracao.value = 'todos'
}

// 🎯 Funções utilitárias para detectar tipo de serviço
const isServicoUnico = (animalServico: AnimalServico): boolean => {
  const servico = getServicoCompleto(animalServico)
  return servico?.podeSerAdicional === true
}

const getStatusServicoUnico = (animalServico: AnimalServico): 'realizado' | 'pendente' => {
    return animalServico.statusServico as 'realizado' | 'pendente'
}

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

const getClienteNome = (animalServico: AnimalServico): string => {
  // Busca reversa: procura o animal que tem este animalServico, depois pega o cliente
  const animal = getAnimalCompleto(animalServico)
  return animal?.cliente?.nomeCompleto || `Dono não encontrado (ID: ${animalServico.id})`
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

const getServicoValor = (animalServico: AnimalServico): number => {
  const servico = getServicoCompleto(animalServico)
  const valorPrincipal = servico?.valor || 0
  const valorAdicionais = valoresAdicionais.value.get(animalServico.id) || 0
  return valorPrincipal + valorAdicionais
}

const formatarValor = (valor: number): string => {
  return valor.toFixed(2).replace('.', ',')
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

const getBanhosRestantes = (animalServico: AnimalServico): number => {
  const total = getTotalBanhos(animalServico)
  return Math.max(total - animalServico.banhosUsados, 0)
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

  } catch (error: any) {
    console.error('❌ Erro ao excluir animal serviço:', error)

    // apiHelpers já extraiu a mensagem do backend
    const mensagem = error?.message || 'Erro desconhecido ao excluir serviço'

    alert(`❌ Não foi possível excluir este serviço\n\n${mensagem}`)
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
    const [animalServicosData, clientesData, servicosData, usuariosData] = await Promise.all([
      animalServicoService.buscarTodos(),
      clientesService.buscarTodos(), // 🔥 CORREÇÃO: usar clientesService para obter dados completos
      servicosService.buscarTodos(),
      usuariosService.buscarTodos()
    ])

    // 🐕 Extrai todos os animais dos clientes e adiciona referência ao cliente
    const todosAnimais: any[] = []
    clientesData.forEach((cliente: any) => {
      cliente.animais.forEach((animal: any) => {
        todosAnimais.push({
          ...animal,
          cliente: {
            id: cliente.id,
            nomeCompleto: cliente.nomeCompleto,
            cpf: cliente.cpf,
            codigoSimplesVet: cliente.codigoSimplesVet,
            codigoClienteSistema: cliente.codigoClienteSistema,
            telefones: cliente.telefones,
            emailClientes: cliente.emailClientes,
            animais: [] // Evita referência circular
          }
        })
      })
    })

    animalServicos.value = animalServicosData
    animais.value = todosAnimais // 🔥 CORREÇÃO: usar animais com dados do cliente
    servicos.value = servicosData
    usuarios.value = usuariosData

    console.log(`✅ Dados carregados:`)
    console.log(`  - ${animalServicos.value.length} animal serviços`)
    console.log(`  - ${animais.value.length} animais (com dados dos donos)`)
    console.log(`  - ${servicos.value.length} serviços`)
    console.log(`  - ${usuarios.value.length} usuários`)

    // Debug: mostrar estrutura dos dados
    if (animalServicos.value.length > 0) {
      console.log('🔍 Estrutura do primeiro Animal Serviço:', animalServicos.value[0])
    }
    if (animais.value.length > 0) {
      console.log('🔍 Estrutura do primeiro Animal (com cliente):', animais.value[0])
    }

    // 💰 Carregar valores dos serviços adicionais para cada serviço
    await carregarValoresAdicionais()

  } catch (err) {
    console.error('❌ Erro ao carregar animal serviços:', err)
    error.value = 'Erro ao carregar os dados. Tente novamente.'
  } finally {
    loading.value = false
  }
}

// 💰 Função para carregar valores dos serviços adicionais
const carregarValoresAdicionais = async (): Promise<void> => {
  console.log('💰 Carregando valores dos serviços adicionais...')
  const novoMap = new Map<number, number>()
  
  await Promise.all(
    animalServicos.value.map(async (animalServico) => {
      try {
        const valorAdicional = await servicosAdicionaisService.calcularValorTotal(animalServico.id)
        novoMap.set(animalServico.id, valorAdicional)
      } catch (err) {
        console.error(`Erro ao carregar valor adicional do serviço ${animalServico.id}:`, err)
        novoMap.set(animalServico.id, 0)
      }
    })
  )
  
  valoresAdicionais.value = novoMap
  console.log(`✅ Valores adicionais carregados para ${novoMap.size} serviços`)
}

const carregarMaisItens = (): void => {
  itensExibidos.value += itensPorPagina.value
}
// 👀 Watchers para resetar paginação quando filtros mudam
watch([filtroTexto, filtroExpiracao], () => {
  paginaAtual.value = 1
})

// 👁️ Navegação para detalhes
const verDetalhesAnimalServico = (animalServico: AnimalServico): void => {
  console.log('🔍 Navegando para detalhes do Animal Serviço:', animalServico.id)
  router.push(`/animal-servico/${animalServico.id}`)
}

// 🚀 Funções das ações rápidas
const toggleMenuAcoes = (animalServicoId: number): void => {
  if (menuAcoesAberto.value === animalServicoId) {
    menuAcoesAberto.value = null
  } else {
    menuAcoesAberto.value = animalServicoId
  }
}

const abrirModalBanhoRapido = (animalServico: AnimalServico): void => {
  console.log('🚀 Abrindo modal rápido para registrar banho:', animalServico)
  
  // 🎯 Para serviços únicos, apenas atualizar o status (sem registrar banho individual)
  if (isServicoUnico(animalServico)) {
    marcarServicoUnicoComoRealizado(animalServico)
  } else {
    // 📦 Para pacotes, abrir modal de registro de banho individual
    abrirModalBanho(animalServico)
  }
  
  menuAcoesAberto.value = null
}

// 🎯 Marcar serviço único como realizado (sem criar banho individual)
const marcarServicoUnicoComoRealizado = async (animalServico: AnimalServico): Promise<void> => {
  try {
    loading.value = true

    const dataAtual = new Date().toISOString().split('T')[0]

    console.log(`✅ Marcando serviço único ${animalServico.id} como realizado...`)
    
    // Atualizar o serviço com statusServico = 'realizado' e dataRealizacao = hoje
    await animalServicoService.atualizar(animalServico.id, {
      statusServico: 'realizado',
      dataRealizacao: dataAtual
    })

    // Atualizar o item na lista localmente
    const index = animalServicos.value.findIndex(as => as.id === animalServico.id)
    if (index !== -1 && animalServicos.value[index]) {
      animalServicos.value[index].statusServico = 'realizado'
      animalServicos.value[index].dataRealizacao = dataAtual
    }

    console.log('✅ Serviço único marcado como REALIZADO com sucesso!')
  } catch (error) {
    console.error('❌ Erro ao marcar serviço único como realizado:', error)
    alert('Erro ao marcar como realizado. Tente novamente.')
  } finally {
    loading.value = false
  }
}

// 🔄 Marcar serviço único como pendente
const marcarServicoUnicoComoPendente = async (animalServico: AnimalServico): Promise<void> => {
  try {
    loading.value = true

    console.log(`⏳ Marcando serviço único ${animalServico.id} como pendente...`)
    
    // Atualizar o serviço com statusServico = 'pendente' e limpar dataRealizacao
    await animalServicoService.atualizar(animalServico.id, {
      statusServico: 'pendente',
      dataRealizacao: null
    })

    // Atualizar o item na lista localmente
    const index = animalServicos.value.findIndex(as => as.id === animalServico.id)
    if (index !== -1 && animalServicos.value[index]) {
      animalServicos.value[index].statusServico = 'pendente'
      animalServicos.value[index].dataRealizacao = undefined
    }

    console.log('⏳ Serviço único marcado como PENDENTE com sucesso!')
  } catch (error) {
    console.error('❌ Erro ao marcar serviço único como pendente:', error)
    alert('Erro ao marcar como pendente. Tente novamente.')
  } finally {
    loading.value = false
  }
}

const editarExpiracao = (animalServico: AnimalServico): void => {
  console.log('📅 Editando expiração:', animalServico.id)
  // TODO: Implementar modal de edição de expiração
  alert(`🚧 Funcionalidade "Editar Expiração" será implementada em breve!\n\nPacote: ${getAnimalNome(animalServico)}\nID: ${animalServico.id}`)
  menuAcoesAberto.value = null
}

const duplicarServico = (animalServico: AnimalServico): void => {
  console.log('📋 Duplicando serviço:', animalServico.id)
  // TODO: Implementar duplicação de serviço
  const confirmacao = confirm(`📋 Duplicar este serviço?\n\nAnimal: ${getAnimalNome(animalServico)}\nServiço: ${getServicoDescricao(animalServico)}\nValor: R$ ${formatarValor(getServicoValor(animalServico))}\n\n✨ Será criado um novo serviço idêntico para o mesmo animal.`)

  if (confirmacao) {
    alert(`🚧 Funcionalidade "Duplicar Serviço" será implementada em breve!\n\nPor enquanto, use a tela de cadastro manual. 😊`)
  }
  menuAcoesAberto.value = null
}

// 💳 Funções para alterar status de pagamento
const marcarComoPago = async (animalServico: AnimalServico): Promise<void> => {
  try {
    loading.value = true
    menuAcoesAberto.value = null

    const dataAtual = new Date().toISOString().split('T')[0]

    console.log(`💳 Marcando serviço ${animalServico.id} como pago...`)
    await animalServicoService.marcarComoPago(animalServico.id, dataAtual as string)

    // Atualizar o item na lista
    const index = animalServicos.value.findIndex(as => as.id === animalServico.id)
    if (index !== -1 && animalServicos.value[index]) {
      animalServicos.value[index].statusPagamento = 'pago'
      animalServicos.value[index].dataPagamento = dataAtual
    }

    console.log('✅ Status alterado para PAGO com sucesso!')
  } catch (error) {
    console.error('❌ Erro ao marcar como pago:', error)
    alert('Erro ao marcar como pago. Tente novamente.')
  } finally {
    loading.value = false
  }
}

const marcarComoEmAberto = async (animalServico: AnimalServico): Promise<void> => {
  try {
    loading.value = true
    menuAcoesAberto.value = null

    console.log(`🔄 Marcando serviço ${animalServico.id} como em aberto...`)
    await animalServicoService.reativarServico(animalServico.id)

    // Atualizar o item na lista
    const index = animalServicos.value.findIndex(as => as.id === animalServico.id)
    if (index !== -1 && animalServicos.value[index]) {
      animalServicos.value[index].statusPagamento = 'em_aberto'
      animalServicos.value[index].dataPagamento = undefined
    }

    console.log('✅ Status alterado para EM ABERTO com sucesso!')
  } catch (error) {
    console.error('❌ Erro ao marcar como em aberto:', error)
    alert('Erro ao marcar como em aberto. Tente novamente.')
  } finally {
    loading.value = false
  }
}

const marcarComoCancelado = async (animalServico: AnimalServico): Promise<void> => {
  if (!confirm(`Tem certeza de que deseja CANCELAR o serviço de ${getAnimalNome(animalServico)}?`)) {
    menuAcoesAberto.value = null
    return
  }

  try {
    loading.value = true
    menuAcoesAberto.value = null

    console.log(`❌ Marcando serviço ${animalServico.id} como cancelado...`)
    await animalServicoService.marcarComoCancelado(animalServico.id)

    // Atualizar o item na lista
    const index = animalServicos.value.findIndex(as => as.id === animalServico.id)
    if (index !== -1 && animalServicos.value[index]) {
      animalServicos.value[index].statusPagamento = 'cancelado'
      animalServicos.value[index].dataPagamento = undefined
    }

    console.log('✅ Status alterado para CANCELADO com sucesso!')
  } catch (error) {
    console.error('❌ Erro ao marcar como cancelado:', error)
    alert('Erro ao marcar como cancelado. Tente novamente.')
  } finally {
    loading.value = false
  }
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
// Fechar menus quando clicar fora
const fecharMenus = (): void => {
  menuAcoesAberto.value = null
}

// 🔄 Lifecycle
onMounted(() => {
  carregarAnimalServicos()

  // Event listener para fechar menus ao clicar fora
  document.addEventListener('click', fecharMenus)
})

// Cleanup no unmount
onUnmounted(() => {
  document.removeEventListener('click', fecharMenus)
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
