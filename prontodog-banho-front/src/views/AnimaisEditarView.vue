<!-- 🐕 Página de Edição/Visualização de Animais -->
<!-- Visual azul/indigo temático dos animais -->
<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 🐕 Header Temático dos Animais -->
    <div class="relative overflow-hidden bg-gradient-to-r from-blue-600 via-indigo-600 to-blue-700 text-white pt-16">
      <!-- ✨ Efeito de brilho sutil -->
      <div class="absolute inset-0 opacity-10">
        <div class="absolute inset-0 bg-gradient-to-r from-transparent via-white/20 to-transparent transform -skew-x-12 animate-pulse"></div>
      </div>

      <div class="relative z-20 max-w-4xl mx-auto px-4 py-12">
        <div class="text-center space-y-4">
          <div class="flex justify-center mb-6">
            <div class="relative">
              <div class="w-20 h-20 bg-gradient-to-br from-blue-400 to-indigo-500 rounded-3xl flex items-center justify-center shadow-2xl">
                <FontAwesomeIcon
                  :icon="['fas', 'dog']"
                  class="text-3xl text-white drop-shadow-lg transform rotate-12 animate-bounce-gentle"
                />
              </div>
              <!-- Indicador de "editar" -->
              <div class="absolute -top-2 -right-2 w-8 h-8 bg-gradient-to-r from-purple-500 to-pink-600 rounded-full flex items-center justify-center shadow-lg">
                <FontAwesomeIcon :icon="['fas', 'edit']" class="text-sm text-white animate-pulse" />
              </div>
            </div>
          </div>

          <div class="space-y-2">
            <h1 class="text-4xl font-bold">
              <FontAwesomeIcon :icon="['fas', 'dog']" class="text-blue-300 mr-2 animate-twinkle" />
              Perfil do Animal
            </h1>
            <p class="text-xl text-white opacity-90 font-medium">
              Visualize e edite as informações do pet
            </p>
            <div v-if="animalCarregado" class="text-lg text-blue-100 font-medium mt-4">
              🐾 Editando: <span class="font-bold">{{ formulario.nome || 'Carregando...' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 🌊 Wave Decoration (Verde para contraste) -->
      <div class="absolute bottom-0 left-0 right-0 z-10 overflow-hidden">
        <svg viewBox="0 0 1440 100" class="w-full h-20" preserveAspectRatio="none">
          <defs>
            <linearGradient id="waveGradientEditarAnimal" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" style="stop-color:#10b981;stop-opacity:0.9" />
              <stop offset="30%" style="stop-color:#059669;stop-opacity:0.8" />
              <stop offset="70%" style="stop-color:#047857;stop-opacity:0.7" />
              <stop offset="100%" style="stop-color:#064e3b;stop-opacity:0.6" />
            </linearGradient>
            <!-- Sombra sutil -->
            <filter id="waveShadowEditarAnimal">
              <feDropShadow dx="0" dy="2" stdDeviation="4" flood-opacity="0.2"/>
            </filter>
          </defs>
          <!-- Wave suave e profissional -->
          <path
            d="M0,50 C240,30 480,70 720,50 C960,30 1200,70 1440,50 L1440,100 L0,100 Z"
            fill="url(#waveGradientEditarAnimal)"
            filter="url(#waveShadowEditarAnimal)"
          />
          <!-- Wave secundária para profundidade -->
          <path
            d="M0,60 C360,35 600,75 960,55 C1200,35 1320,80 1440,60 L1440,100 L0,100 Z"
            fill="url(#waveGradientEditarAnimal)"
            opacity="0.4"
          />
        </svg>
      </div>
    </div>

    <!-- ⏳ Estado de Loading (Carregando dados do animal) -->
    <div v-if="loadingAnimal" class="max-w-4xl mx-auto px-4 py-8">
      <div class="flex items-center justify-center py-20">
        <div class="text-center space-y-4">
          <div class="relative">
            <div class="w-16 h-16 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl flex items-center justify-center shadow-xl animate-bounce">
              <FontAwesomeIcon :icon="['fas', 'spinner']" class="text-2xl text-white animate-spin" />
            </div>
            <div class="absolute inset-0 bg-gradient-to-r from-blue-400/30 to-indigo-500/30 rounded-2xl blur-xl animate-pulse"></div>
          </div>
          <div class="space-y-2">
            <h3 class="text-xl font-semibold text-gray-700">Carregando dados do animal...</h3>
            <p class="text-gray-500">ID: {{ $route.params.id }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- ❌ Estado de Erro -->
    <div v-else-if="errorCarregando" class="max-w-4xl mx-auto px-4 py-8">
      <div class="flex items-center justify-center py-20">
        <BaseCard class="text-center max-w-md bg-gradient-to-br from-red-50 to-pink-50 border-red-200">
          <div class="space-y-4">
            <div class="w-16 h-16 bg-gradient-to-br from-red-500 to-pink-600 rounded-2xl flex items-center justify-center mx-auto shadow-xl">
              <FontAwesomeIcon :icon="['fas', 'exclamation-triangle']" class="text-2xl text-white" />
            </div>
            <div>
              <h3 class="text-xl font-semibold text-red-700 mb-2">Erro ao carregar animal</h3>
              <p class="text-red-600 mb-4">{{ errorCarregando }}</p>
              <div class="flex gap-3 justify-center">
                <BaseButton variant="secondary" @click="carregarAnimal">
                  <FontAwesomeIcon :icon="['fas', 'refresh']" class="mr-2" />
                  Tentar Novamente
                </BaseButton>
                <RouterLink to="/animais">
                  <BaseButton variant="primary">
                    <FontAwesomeIcon :icon="['fas', 'arrow-left']" class="mr-2" />
                    Voltar à Lista
                  </BaseButton>
                </RouterLink>
              </div>
            </div>
          </div>
        </BaseCard>
      </div>
    </div>

    <!-- 🐾 Formulário Principal -->
    <div v-else-if="animalCarregado" class="max-w-4xl mx-auto px-4 py-8">
      <form @submit.prevent="submeterFormulario" class="space-y-8">


        <!-- 🐕 Seção: Dados do Animal -->
        <div class="bg-gradient-to-r from-white via-blue-50/30 to-indigo-50/20 rounded-2xl shadow-lg border border-blue-200/50 overflow-hidden animate-fade-in-up">
          <div class="bg-gradient-to-r from-blue-600 to-indigo-700 px-6 py-4">
            <h2 class="text-xl font-bold text-white flex items-center gap-3">
              <div class="w-8 h-8 bg-white/20 rounded-lg flex items-center justify-center">
                <FontAwesomeIcon :icon="['fas', 'dog']" class="text-white" />
              </div>
              Dados do Animal
            </h2>
            <p class="text-blue-100 text-sm mt-1">Informações principais do pet</p>
          </div>

          <div class="p-6 space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <!-- Nome do Animal -->
              <div class="animate-slide-up">
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  <FontAwesomeIcon :icon="['fas', 'paw']" class="text-blue-600 mr-2" />
                  Nome do Animal *
                </label>
                <input
                  v-model="formulario.nome"
                  type="text"
                  placeholder="Ex: Rex, Mimi, Thor..."
                  required
                  class="w-full px-4 py-3 border-2 border-blue-200 rounded-xl focus:border-blue-500 focus:ring-4 focus:ring-blue-500/20 transition-all duration-300 bg-white"
                  :class="{ 'border-red-300 focus:border-red-500': erros.nome }"
                >
                <div class="flex justify-between items-center mt-1">
                  <span v-if="erros.nome" class="text-sm text-red-600">{{ erros.nome }}</span>
                  <span class="text-xs text-gray-500 ml-auto">Nome carinhoso do pet</span>
                </div>
              </div>

              <!-- Tipo do Animal -->
              <div class="animate-slide-up">
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  <FontAwesomeIcon :icon="['fas', 'heart']" class="text-blue-600 mr-2" />
                  Tipo do Animal *
                </label>
                <div class="relative">
                  <select
                    v-model="formulario.tipo"
                    class="w-full px-4 py-3 pr-12 border-2 border-blue-200 rounded-xl focus:border-blue-500 focus:ring-4 focus:ring-blue-500/20 transition-all duration-300 bg-white appearance-none cursor-pointer hover:border-blue-400"
                    :class="{ 'border-red-300 focus:border-red-500': erros.tipo }"
                    required
                  >
                    <option value="">Selecione o tipo</option>
                    <optgroup label="🐕 Mais Comuns">
                      <option value="Cachorro">🐕 Cachorro</option>
                      <option value="Gato">🐱 Gato</option>
                    </optgroup>
                    <optgroup label="🐦 Outros Pets">
                      <option value="Pássaro">🐦 Pássaro</option>
                      <option value="Peixe">🐟 Peixe</option>
                      <option value="Hamster">🐹 Hamster</option>
                      <option value="Coelho">🐰 Coelho</option>
                    </optgroup>
                    <optgroup label="✨ Outros">
                      <option value="Outro">🐾 Outro</option>
                    </optgroup>
                  </select>
                  <!-- Ícone de seta customizado -->
                  <div class="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
                    <FontAwesomeIcon :icon="['fas', 'chevron-down']" class="text-blue-500" />
                  </div>
                </div>
                <div class="flex justify-between items-center mt-1">
                  <span v-if="erros.tipo" class="text-sm text-red-600">{{ erros.tipo }}</span>
                  <span class="text-xs text-gray-500 ml-auto">
                    {{ formulario.tipo ? `${getIconeTipoAnimal(formulario.tipo)} ${formulario.tipo}` : 'Tipo do pet' }}
                  </span>
                </div>
              </div>

              <!-- Raça do Animal -->
              <div class="animate-slide-up">
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  <FontAwesomeIcon :icon="['fas', 'paw']" class="text-purple-600 mr-2" />
                  Raça (Opcional)
                </label>
                <input
                  v-model="formulario.raca"
                  type="text"
                  placeholder="Ex: Labrador, Persa, SRD..."
                  class="w-full px-4 py-3 border-2 border-purple-200 rounded-xl focus:border-purple-500 focus:ring-4 focus:ring-purple-500/20 transition-all duration-300 bg-white"
                  :class="{ 'border-red-300 focus:border-red-500': erros.raca }"
                >
                <div class="flex justify-between items-center mt-1">
                  <span v-if="erros.raca" class="text-sm text-red-600">{{ erros.raca }}</span>
                  <span class="text-xs text-gray-500 ml-auto">Raça ou linhagem do pet</span>
                </div>
              </div>

              <!-- Peso do Animal -->
              <div class="animate-slide-up">
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  <FontAwesomeIcon :icon="['fas', 'calculator']" class="text-orange-600 mr-2" />
                  Peso em kg (Opcional)
                </label>
                <input
                  v-model="formulario.peso"
                  type="number"
                  step="0.01"
                  min="0"
                  max="999.99"
                  placeholder="Ex: 5.5, 12.8, 0.8..."
                  class="w-full px-4 py-3 border-2 border-orange-200 rounded-xl focus:border-orange-500 focus:ring-4 focus:ring-orange-500/20 transition-all duration-300 bg-white"
                  :class="{ 'border-red-300 focus:border-red-500': erros.peso }"
                >
                <div class="flex justify-between items-center mt-1">
                  <span v-if="erros.peso" class="text-sm text-red-600">{{ erros.peso }}</span>
                  <span class="text-xs text-gray-500 ml-auto">Peso atual do animal</span>
                </div>
              </div>
            </div>

            <!-- Código SimplesVet -->
            <div class="animate-slide-up">
              <label class="block text-sm font-medium text-gray-700 mb-2">
                <FontAwesomeIcon :icon="['fas', 'qrcode']" class="text-blue-600 mr-2" />
                Código SimplesVet (Opcional)
              </label>
              <input
                v-model="formulario.codigoSimplesVet"
                type="number"
                min="0"
                placeholder="Ex: 12345"
                class="w-full px-4 py-3 border-2 border-blue-200 rounded-xl focus:border-blue-500 focus:ring-4 focus:ring-blue-500/20 transition-all duration-300 bg-white"
                :class="{ 'border-red-300 focus:border-red-500': erros.codigoSimplesVet }"
              >
              <div class="flex justify-between items-center mt-1">
                <span v-if="erros.codigoSimplesVet" class="text-sm text-red-600">{{ erros.codigoSimplesVet }}</span>
                <span class="text-xs text-gray-500 ml-auto">Código de identificação no sistema SimplesVet</span>
              </div>
            </div>

            <!-- Visualização do Código do Sistema -->
            <div class="bg-gradient-to-r from-gray-50 to-blue-50 rounded-xl p-4 border border-blue-200">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 bg-gradient-to-br from-gray-500 to-gray-600 rounded-xl flex items-center justify-center">
                    <FontAwesomeIcon :icon="['fas', 'barcode']" class="text-white" />
                  </div>
                  <div>
                    <h4 class="font-semibold text-gray-800">Código do Sistema</h4>
                    <p class="text-sm text-gray-600">ID único gerado automaticamente</p>
                  </div>
                </div>
                <div class="text-right">
                  <div class="text-lg font-bold text-gray-700">
                    #{{ animalOriginal?.codigoAnimalSistema || 'Gerando...' }}
                  </div>
                  <div class="text-xs text-gray-500">não editável</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 👤 Seção: Cliente Responsável -->
        <div v-if="dadosCliente" class="bg-gradient-to-r from-gray-50 via-blue-50/50 to-purple-50/30 rounded-2xl shadow-md border border-gray-200/80 overflow-hidden animate-fade-in-up">
          <div class="px-6 py-4 border-b border-gray-200/50">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-gradient-to-br from-purple-500 to-blue-600 rounded-full flex items-center justify-center">
                  <FontAwesomeIcon :icon="['fas', 'user']" class="text-sm text-white" />
                </div>
                <div>
                  <h3 class="text-lg font-semibold text-gray-800">Cliente Responsável</h3>
                  <p class="text-sm text-gray-600">{{ dadosCliente.nomeCompleto }}</p>
                </div>
              </div>

              <!-- Botão discreto para ver mais detalhes -->
              <RouterLink :to="`/clientes/${dadosCliente.id}/editar`">
                <button
                  type="button"
                  class="group flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-purple-100 to-blue-100 hover:from-purple-200 hover:to-blue-200 text-purple-700 hover:text-purple-800 rounded-lg text-sm font-medium transition-all duration-300 transform hover:-translate-y-0.5"
                >
                  <FontAwesomeIcon :icon="['fas', 'eye']" class="group-hover:scale-110 transition-transform" />
                  Ver Cliente
                </button>
              </RouterLink>
            </div>
          </div>

          <div class="px-6 py-4">
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
                  <FontAwesomeIcon :icon="['fas', 'id-card']" class="text-blue-600 text-sm" />
                </div>
                <div>
                  <div class="text-xs font-medium text-gray-500 uppercase tracking-wider">CPF</div>
                  <div class="font-semibold text-gray-800">{{ formatarCpf(dadosCliente.cpf) }}</div>
                </div>
              </div>

              <div class="flex items-center gap-3">
                <div class="w-8 h-8 bg-green-100 rounded-lg flex items-center justify-center">
                  <FontAwesomeIcon :icon="['fas', 'hashtag']" class="text-green-600 text-sm" />
                </div>
                <div>
                  <div class="text-xs font-medium text-gray-500 uppercase tracking-wider">ID Cliente</div>
                  <div class="font-mono text-sm bg-gray-100 px-2 py-1 rounded">#{{ dadosCliente.id }}</div>
                </div>
              </div>

              <div class="flex items-center gap-3">
                <div class="w-8 h-8 bg-purple-100 rounded-lg flex items-center justify-center">
                  <FontAwesomeIcon :icon="['fas', 'at']" class="text-purple-600 text-sm" />
                </div>
                <div>
                  <div class="text-xs font-medium text-gray-500 uppercase tracking-wider">SimplesVet</div>
                  <div class="font-semibold text-gray-800">{{ dadosCliente.codigoSimplesVet || 'Não informado' }}</div>
                </div>
              </div>

              <div class="flex items-center gap-3">
                <div class="w-8 h-8 bg-amber-100 rounded-lg flex items-center justify-center">
                  <FontAwesomeIcon :icon="['fas', 'paw']" class="text-amber-600 text-sm" />
                </div>
                <div>
                  <div class="text-xs font-medium text-gray-500 uppercase tracking-wider">Total Pets</div>
                  <div class="font-semibold text-gray-800">{{ dadosCliente.animais?.length || 0 }} {{ (dadosCliente.animais?.length || 0) === 1 ? 'animal' : 'animais' }}</div>
                </div>
              </div>
            </div>

            <!-- Telefones (se houver) -->
            <div v-if="dadosCliente.telefones && dadosCliente.telefones.length > 0" class="mt-4 pt-4 border-t border-gray-200/50">
              <div class="flex items-center gap-2 mb-2">
                <FontAwesomeIcon :icon="['fas', 'phone']" class="text-gray-500 text-sm" />
                <span class="text-xs font-medium text-gray-500 uppercase tracking-wider">Contatos</span>
              </div>
              <div class="flex gap-2 flex-wrap">
                <div
                  v-for="telefone in dadosCliente.telefones.slice(0, 2)"
                  :key="telefone.id"
                  class="inline-flex items-center gap-1 px-3 py-1 bg-blue-100 text-blue-700 rounded-full text-sm"
                >
                  <FontAwesomeIcon :icon="['fas', 'phone']" class="text-xs" />
                  {{ telefone.telefone }}
                </div>
                <div
                  v-if="dadosCliente.telefones.length > 2"
                  class="inline-flex items-center px-3 py-1 bg-gray-100 text-gray-600 rounded-full text-sm"
                >
                  +{{ dadosCliente.telefones.length - 2 }} mais
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- ⚠️ Fallback: Animal sem cliente -->
        <div v-else-if="animalCarregado && !dadosCliente" class="bg-gradient-to-r from-yellow-50 to-orange-50 rounded-2xl shadow-md border border-yellow-200/80 overflow-hidden animate-fade-in-up">
          <div class="px-6 py-4 border-b border-yellow-200/50">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-gradient-to-br from-yellow-500 to-orange-600 rounded-full flex items-center justify-center">
                <FontAwesomeIcon :icon="['fas', 'exclamation-triangle']" class="text-sm text-white" />
              </div>
              <div>
                <h3 class="text-lg font-semibold text-gray-800">Cliente não encontrado</h3>
                <p class="text-sm text-gray-600">Este animal não possui um cliente responsável associado</p>
              </div>
            </div>
          </div>

          <div class="px-6 py-4">
            <div class="text-sm text-gray-600">
              <p class="mb-3">Este animal (ID #{{ animalOriginal?.id }}) não está associado a nenhum cliente no sistema.</p>

              <div class="bg-blue-50 border-l-4 border-blue-400 p-4 rounded-r-lg">
                <div class="flex">
                  <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-blue-400 mr-2 mt-0.5" />
                  <div>
                    <h4 class="text-blue-800 font-medium">Como resolver?</h4>
                    <p class="text-blue-700 text-sm mt-1">
                      Para associar este animal a um cliente, vá até a <RouterLink to="/clientes" class="underline font-medium">lista de clientes</RouterLink>
                      e adicione este animal ao cliente desejado.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 📋 Informações sobre Edição -->
        <BaseCard class="bg-gradient-to-r from-green-50 to-emerald-50 border-green-200 animate-fade-in-up">
          <div class="flex gap-4">
            <div class="flex-shrink-0">
              <div class="w-12 h-12 bg-gradient-to-br from-green-500 to-emerald-600 rounded-xl flex items-center justify-center">
                <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-white text-lg" />
              </div>
            </div>
            <div>
              <h3 class="font-semibold text-green-800 mb-2">Informações Importantes</h3>
              <ul class="space-y-2 text-sm text-green-700">
                <li class="flex items-center gap-2">
                  <div class="w-1.5 h-1.5 bg-blue-500 rounded-full"></div>
                  As alterações serão salvas imediatamente no sistema
                </li>
                <li class="flex items-center gap-2">
                  <div class="w-1.5 h-1.5 bg-green-500 rounded-full"></div>
                  O código do sistema é gerado automaticamente e não pode ser editado
                </li>
                <li class="flex items-center gap-2">
                  <div class="w-1.5 h-1.5 bg-yellow-500 rounded-full"></div>
                  O código SimplesVet é opcional mas recomendado para integração
                </li>
                <li class="flex items-center gap-2">
                  <div class="w-1.5 h-1.5 bg-purple-500 rounded-full"></div>
                  ID do animal: <span class="font-mono bg-gray-100 px-2 py-0.5 rounded">#{{ $route.params.id }}</span>
                </li>
              </ul>
            </div>
          </div>
        </BaseCard>

        <!-- 🎯 Botões de Ação -->
        <div class="flex flex-col sm:flex-row gap-4 justify-between animate-fade-in-up">
          <div class="flex flex-col sm:flex-row gap-4">
            <RouterLink to="/animais">
              <button
                type="button"
                class="w-full sm:w-auto px-8 py-3 text-lg font-medium border-2 border-gray-300 hover:border-gray-400 bg-white hover:bg-gray-50 text-gray-700 hover:text-gray-800 rounded-lg transition-all duration-300 flex items-center justify-center gap-2"
              >
                <FontAwesomeIcon :icon="['fas', 'arrow-left']" />
                Voltar
              </button>
            </RouterLink>

            <BaseButton
              @click="confirmarExclusao"
              variant="danger"
              :disabled="loading || excluindo"
              class="w-full sm:w-auto px-8 py-3 text-lg font-medium shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1"
            >
              <FontAwesomeIcon
                v-if="excluindo"
                :icon="['fas', 'spinner']"
                class="mr-2 animate-spin"
              />
              <FontAwesomeIcon
                v-else
                :icon="['fas', 'trash']"
                class="mr-2"
              />
              {{ excluindo ? 'Excluindo...' : 'Excluir Animal' }}
            </BaseButton>
          </div>

          <BaseButton
            type="submit"
            variant="primary"
            :disabled="loading || excluindo"
            class="w-full sm:w-auto bg-gradient-to-r from-blue-600 to-indigo-700 hover:from-blue-700 hover:to-indigo-800 px-8 py-3 text-lg font-medium shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 border-0"
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
            {{ loading ? 'Salvando...' : 'Salvar Alterações' }}
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
      title="Dados Salvos!"
    >
      <div class="text-center space-y-6 p-6">
        <!-- Ícone de sucesso animado -->
        <div class="relative mx-auto w-24 h-24">
          <div class="absolute inset-0 bg-gradient-to-r from-blue-400 to-indigo-500 rounded-full animate-pulse"></div>
          <div class="relative w-24 h-24 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-full flex items-center justify-center shadow-2xl">
            <FontAwesomeIcon :icon="['fas', 'check']" class="text-3xl text-white animate-bounce" />
          </div>
          <!-- Confetti effect -->
          <div class="absolute -inset-4 opacity-75">
            <div class="absolute top-2 left-2 w-2 h-2 bg-yellow-400 rounded-full animate-bounce" style="animation-delay: 0.1s;"></div>
            <div class="absolute top-4 right-3 w-1.5 h-1.5 bg-pink-400 rounded-full animate-bounce" style="animation-delay: 0.3s;"></div>
            <div class="absolute bottom-3 left-4 w-2 h-2 bg-green-400 rounded-full animate-bounce" style="animation-delay: 0.2s;"></div>
            <div class="absolute bottom-2 right-2 w-1.5 h-1.5 bg-purple-400 rounded-full animate-bounce" style="animation-delay: 0.4s;"></div>
          </div>
        </div>

        <!-- Mensagem de sucesso -->
        <div>
          <h2 class="text-2xl font-bold text-gray-800 mb-2">Dados do Animal Atualizados!</h2>
          <p class="text-gray-600 mb-6">As alterações foram salvas e já estão disponíveis no sistema.</p>
        </div>

        <!-- Detalhes do animal atualizado -->
        <div v-if="animalAtualizado" class="bg-gradient-to-r from-gray-50 to-blue-50 rounded-xl p-4 border border-blue-200">
          <div class="space-y-3">
            <div class="flex items-center justify-between">
              <span class="text-sm font-medium text-gray-600">Nome:</span>
              <span class="font-semibold text-gray-800 flex items-center gap-1">
                {{ getIconeTipoAnimal(animalAtualizado.tipo) }} {{ animalAtualizado.nome }}
              </span>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm font-medium text-gray-600">Tipo:</span>
              <span class="font-semibold text-blue-600">{{ animalAtualizado.tipo }}</span>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm font-medium text-gray-600">SimplesVet:</span>
              <span class="font-semibold text-purple-600">{{ animalAtualizado.codigoSimplesVet || 'Não informado' }}</span>
            </div>
            <div v-if="animalAtualizado.raca" class="flex items-center justify-between">
              <span class="text-sm font-medium text-gray-600">Raça:</span>
              <span class="font-semibold text-purple-600">{{ animalAtualizado.raca }}</span>
            </div>
            <div v-if="animalAtualizado.peso" class="flex items-center justify-between">
              <span class="text-sm font-medium text-gray-600">Peso:</span>
              <span class="font-semibold text-orange-600">{{ animalAtualizado.peso }}kg</span>
            </div>
            <div class="flex items-center justify-between">
              <span class="text-sm font-medium text-gray-600">ID do Animal:</span>
              <span class="font-mono text-sm bg-gray-100 px-2 py-1 rounded">#{{ animalAtualizado.id }}</span>
            </div>
          </div>
        </div>

        <!-- Botões do modal -->
        <div class="flex flex-col sm:flex-row gap-3 justify-center">
          <BaseButton
            @click="continuarEditando"
            variant="secondary"
            class="bg-gradient-to-r from-green-600 to-emerald-700 hover:from-green-700 hover:to-emerald-800 text-white border-0"
          >
            <FontAwesomeIcon :icon="['fas', 'edit']" class="mr-2" />
            Continuar Editando
          </BaseButton>

          <BaseButton
            @click="voltarParaLista"
            variant="primary"
            class="bg-gradient-to-r from-blue-600 to-indigo-700 hover:from-blue-700 hover:to-indigo-800 border-0"
          >
            <FontAwesomeIcon :icon="['fas', 'list']" class="mr-2" />
            Ver Lista de Animais
          </BaseButton>
        </div>
      </div>
    </BaseModal>

    <!-- 🔄 Loading Overlay (Salvando) -->
    <div v-if="loading" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl p-8 shadow-2xl max-w-sm w-full mx-4">
        <div class="text-center space-y-4">
          <div class="relative mx-auto w-16 h-16">
            <div class="absolute inset-0 bg-gradient-to-r from-blue-400 to-indigo-500 rounded-2xl animate-pulse"></div>
            <div class="relative w-16 h-16 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-2xl flex items-center justify-center shadow-xl">
              <FontAwesomeIcon :icon="['fas', 'dog']" class="text-2xl text-white animate-pulse" />
            </div>
          </div>
          <div>
            <h3 class="text-lg font-semibold text-gray-800">Salvando Dados</h3>
            <p class="text-gray-600">Atualizando informações do animal...</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 🗑️ Loading Overlay (Excluindo) -->
    <div v-if="excluindo" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl p-8 shadow-2xl max-w-sm w-full mx-4">
        <div class="text-center space-y-4">
          <div class="relative mx-auto w-16 h-16">
            <div class="absolute inset-0 bg-gradient-to-r from-red-400 to-pink-500 rounded-2xl animate-pulse"></div>
            <div class="relative w-16 h-16 bg-gradient-to-br from-red-500 to-pink-600 rounded-2xl flex items-center justify-center shadow-xl">
              <FontAwesomeIcon :icon="['fas', 'trash']" class="text-2xl text-white animate-pulse" />
            </div>
          </div>
          <div>
            <h3 class="text-lg font-semibold text-gray-800">Excluindo Animal</h3>
            <p class="text-gray-600">Removendo do sistema...</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { BaseCard, BaseButton, BaseModal } from '@/components/UI'
import { animaisService, clientesService } from '@/services/api'
import { getIconeTipoAnimal } from '@/utils/formatters'
import type { NovoAnimal, Animal, Cliente } from '@/types/api'
import { TIPOS_ANIMAIS } from '@/types/api'

// 🎯 Configuração inicial
const router = useRouter()
const route = useRoute()

// 📋 Estados do formulário
const loading = ref(false)
const loadingAnimal = ref(true)
const errorCarregando = ref('')
const animalCarregado = ref(false)
const mostrarSucesso = ref(false)
const animalAtualizado = ref<Animal | null>(null)
const animalOriginal = ref<Animal | null>(null)
const dadosCliente = ref<Cliente | null>(null)
const excluindo = ref(false)

// 📝 Dados do formulário
const formulario = reactive({
  nome: '',
  tipo: '',
  raca: '',
  peso: '',
  codigoSimplesVet: 0
})

// ⚠️ Controle de erros
const erros = reactive({
  nome: '',
  tipo: '',
  raca: '',
  peso: '',
  codigoSimplesVet: ''
})

// 📊 Status do formulário
const formStatus = ref<{
  icon: string
  title: string
  message: string
} | null>(null)

// 💡 Computed properties
const animalId = computed(() => Number(route.params.id))
const tiposAnimais = computed(() => TIPOS_ANIMAIS)

// 🔧 Funções utilitárias
const formatarCpf = (cpf: string): string => {
  if (!cpf) return ''
  const cleaned = cpf.replace(/\D/g, '')
  return cleaned.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
}

const validarFormulario = (): boolean => {
  // Reset erros
  Object.keys(erros).forEach(key => {
    erros[key as keyof typeof erros] = ''
  })

  let valido = true

  // Validar nome
  if (!formulario.nome.trim()) {
    erros.nome = 'Nome do animal é obrigatório'
    valido = false
  } else if (formulario.nome.trim().length < 2) {
    erros.nome = 'Nome deve ter pelo menos 2 caracteres'
    valido = false
  }

  // Validar tipo
  if (!formulario.tipo) {
    erros.tipo = 'Tipo do animal é obrigatório'
    valido = false
  }

  // Validar raça (opcional, mas se preenchida deve ter pelo menos 2 caracteres)
  if (formulario.raca && formulario.raca.trim().length > 0) {
    if (formulario.raca.trim().length < 2) {
      erros.raca = 'Raça deve ter pelo menos 2 caracteres'
      valido = false
    }
  }

  // Validar peso (opcional, mas se preenchido deve ser válido)
  if (formulario.peso !== '' && formulario.peso != null && formulario.peso !== undefined) {
    // Converter para string primeiro, depois para número
    const pesoStr = String(formulario.peso).trim()
    const peso = parseFloat(pesoStr)
    if (pesoStr === '' || isNaN(peso) || peso <= 0 || peso > 999.99) {
      erros.peso = 'Peso deve ser um número positivo até 999.99 kg'
      valido = false
    }
  }

  // Validar código SimplesVet (opcional, mas se informado deve ser válido)
  if (formulario.codigoSimplesVet && formulario.codigoSimplesVet < 0) {
    erros.codigoSimplesVet = 'Código SimplesVet deve ser um número positivo'
    valido = false
  }

  return valido
}

const limparErros = () => {
  Object.keys(erros).forEach(key => {
    erros[key as keyof typeof erros] = ''
  })
  formStatus.value = null
}

// 🎯 Ações principais
const carregarAnimal = async () => {
  loadingAnimal.value = true
  errorCarregando.value = ''
  animalCarregado.value = false

  try {
    const animal = await animaisService.buscarPorId(animalId.value)

    // Preenche o formulário com os dados existentes
    formulario.nome = animal.nome
    formulario.tipo = animal.tipo
    formulario.raca = animal.raca || ''
    formulario.peso = animal.peso ? String(animal.peso) : ''
    formulario.codigoSimplesVet = animal.codigoSimplesVet || 0

    animalOriginal.value = animal
    animalCarregado.value = true

    // Carrega dados do cliente (dono)
    // Como o backend não retorna cliente devido ao @JsonBackReference,
    // vamos buscar o cliente que possui este animal
    console.log('🔍 Buscando cliente do animal ID:', animal.id)
    try {
      console.log('📞 Carregando todos os clientes para encontrar o dono...')
      const todosClientes = await clientesService.buscarTodos()
      console.log('📊 Total de clientes encontrados:', todosClientes.length)

      // Encontra o cliente que possui este animal
      const clienteEncontrado = todosClientes.find(cliente => {
        return cliente.animais?.some(animalCliente => animalCliente.id === animal.id)
      })

      if (clienteEncontrado) {
        dadosCliente.value = clienteEncontrado
        console.log('✅ Cliente dono encontrado:', clienteEncontrado.nomeCompleto)
      } else {
        console.log('⚠️ Nenhum cliente possui este animal')
        dadosCliente.value = null
      }
    } catch (error) {
      console.warn('⚠️ Erro ao buscar cliente do animal:', error)
      dadosCliente.value = null
    }

    console.log('✅ Animal carregado:', animal)

  } catch (error) {
    console.error('❌ Erro ao carregar animal:', error)
    errorCarregando.value = error instanceof Error ? error.message : 'Erro ao carregar animal'
  } finally {
    loadingAnimal.value = false
  }
}

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
    // Como o backend usa salvar() que funciona como INSERT/UPDATE
    const dadosAnimal = {
      id: animalId.value,
      nome: formulario.nome.trim(),
      tipo: formulario.tipo,
      raca: formulario.raca && formulario.raca.trim() ? formulario.raca.trim() : null,
      peso: formulario.peso !== '' && formulario.peso != null && formulario.peso !== undefined ? parseFloat(String(formulario.peso)) : null,
      codigoSimplesVet: formulario.codigoSimplesVet || 0,
      // Manter dados do cliente original
      cliente: animalOriginal.value?.cliente
    }

    // Usando atualizar() que chama PUT /animal/atualizarcompleto/{id}
    const resultado = await animaisService.atualizar(animalId.value, dadosAnimal)

    animalAtualizado.value = resultado
    mostrarSucesso.value = true
    limparErros()

    console.log('✅ Animal atualizado com sucesso:', resultado)

  } catch (error) {
    console.error('❌ Erro ao atualizar animal:', error)
    formStatus.value = {
      icon: 'exclamation-triangle',
      title: 'Erro ao Atualizar',
      message: error instanceof Error ? error.message : 'Erro inesperado ao atualizar animal.'
    }
  } finally {
    loading.value = false
  }
}

const continuarEditando = () => {
  mostrarSucesso.value = false
  animalAtualizado.value = null
  limparErros()
}

const voltarParaLista = () => {
  router.push('/animais')
}

const confirmarExclusao = (): void => {
  if (!animalOriginal.value) return

  const nomeAnimal = animalOriginal.value.nome
  const nomeDono = dadosCliente.value?.nomeCompleto || 'Dono não informado'

  const confirmacao = confirm(
    `⚠️ ATENÇÃO!\n\nTem certeza que deseja excluir o animal "${nomeAnimal}"?\n\n` +
    `Esta ação NÃO pode ser desfeita e irá:\n` +
    `• Remover permanentemente o animal\n` +
    `• Remover todo o histórico de serviços do ${nomeAnimal}\n` +
    `• Animal do cliente: ${nomeDono}\n\n` +
    `Digite "EXCLUIR" abaixo para confirmar:`
  )

  if (confirmacao) {
    const confirmacaoTexto = prompt('Digite "EXCLUIR" para confirmar:')
    if (confirmacaoTexto === 'EXCLUIR') {
      excluirAnimal()
    } else {
      alert('Exclusão cancelada. Texto de confirmação incorreto.')
    }
  }
}

const excluirAnimal = async (): Promise<void> => {
  if (!animalOriginal.value) return

  try {
    console.log('🗑️ Excluindo animal...')
    excluindo.value = true

    await animaisService.excluir(animalOriginal.value.id)

    console.log('✅ Animal excluído com sucesso!')
    alert(`Animal "${animalOriginal.value.nome}" foi excluído com sucesso!`)

    // Redireciona para lista de animais
    router.push('/animais')

  } catch (err) {
    console.error('❌ Erro ao excluir animal:', err)
    const mensagem = err instanceof Error ? err.message : 'Erro inesperado ao excluir animal'
    alert(`Erro ao excluir animal:\n${mensagem}`)
  } finally {
    excluindo.value = false
  }
}

// 🎬 Lifecycle
onMounted(() => {
  carregarAnimal()
})
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
