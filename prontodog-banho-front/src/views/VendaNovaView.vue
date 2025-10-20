<template>
  <div class="min-h-screen bg-gradient-to-br from-violet-50 via-white to-purple-100 pb-8">
    <!-- Header -->
    <div class="relative overflow-hidden bg-gradient-to-r from-violet-600 via-purple-600 to-indigo-700 text-white pt-16 pb-16">
      <div class="absolute inset-0 opacity-10 bg-pattern"></div>

      <div class="relative px-6 py-6 z-20">
        <button
          @click="$router.push('/vendas')"
          class="flex items-center gap-2 text-white hover:text-violet-200 transition mb-4"
        >
          <FontAwesome icon="arrow-left" />
          <span>Voltar para lista</span>
        </button>

        <div class="flex items-center gap-4">
          <div class="w-16 h-16 bg-white bg-opacity-20 backdrop-blur-sm rounded-2xl flex items-center justify-center shadow-lg">
            <FontAwesome icon="plus-circle" class="text-2xl" />
          </div>

          <div>
            <h1 class="text-4xl font-bold">Nova Venda</h1>
            <p class="text-violet-100 mt-1">Crie uma nova venda para um cliente</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Conteúdo Principal -->
    <div class="max-w-6xl mx-auto px-6 -mt-8 relative z-30">
      <div class="bg-white rounded-xl shadow-md p-6 space-y-6">

        <!-- Seleção do Cliente com Busca Inteligente -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            <FontAwesome icon="user" class="mr-2 text-violet-500" />
            Cliente *
          </label>

          <!-- Campo de busca -->
          <div class="relative mb-3">
            <input
              v-model="buscaCliente"
              type="text"
              placeholder="Busque por nome, CPF ou código do sistema..."
              class="w-full px-4 py-3 pl-12 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500 focus:border-transparent transition"
              @focus="mostrarListaClientes = true"
            />
            <FontAwesome
              icon="search"
              class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400"
            />
            <div v-if="buscaCliente" class="absolute right-4 top-1/2 -translate-y-1/2">
              <button
                @click="limparBuscaCliente"
                class="text-gray-400 hover:text-gray-600 transition"
              >
                <FontAwesome icon="times" />
              </button>
            </div>
          </div>

          <!-- Cliente selecionado -->
          <div v-if="clienteSelecionado" class="mb-3 p-4 bg-violet-50 border-2 border-violet-200 rounded-lg">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-violet-500 rounded-full flex items-center justify-center text-white font-bold">
                  {{ clienteSelecionado.nomeCompleto?.charAt(0).toUpperCase() }}
                </div>
                <div>
                  <p class="font-bold text-gray-800">{{ clienteSelecionado.nomeCompleto }}</p>
                  <p class="text-sm text-gray-600">
                    <span v-if="clienteSelecionado.cpf">CPF: {{ clienteSelecionado.cpf }}</span>
                    <span v-if="clienteSelecionado.cpf && clienteSelecionado.codigoClienteSistema"> • </span>
                    <span v-if="clienteSelecionado.codigoClienteSistema">Código: #{{ clienteSelecionado.codigoClienteSistema }}</span>
                  </p>
                </div>
              </div>
              <button
                @click="desselecionarCliente"
                class="text-gray-400 hover:text-red-500 transition"
                title="Remover seleção"
              >
                <FontAwesome icon="times-circle" class="text-xl" />
              </button>
            </div>
          </div>

          <!-- Lista de clientes filtrados -->
          <div
            v-if="mostrarListaClientes && !clienteSelecionado && clientesFiltrados.length > 0"
            class="max-h-60 overflow-y-auto border border-gray-300 rounded-lg shadow-lg bg-white"
          >
            <button
              v-for="cliente in clientesFiltrados"
              :key="cliente.id"
              @click="selecionarCliente(cliente)"
              class="w-full px-4 py-3 text-left hover:bg-violet-50 transition border-b border-gray-100 last:border-0"
            >
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 bg-violet-100 rounded-full flex items-center justify-center text-violet-600 font-bold text-sm">
                  {{ cliente.nomeCompleto?.charAt(0).toUpperCase() }}
                </div>
                <div class="flex-1">
                  <p class="font-semibold text-gray-800">{{ cliente.nomeCompleto }}</p>
                  <p class="text-xs text-gray-500">
                    <span v-if="cliente.cpf">CPF: {{ cliente.cpf }}</span>
                    <span v-if="cliente.cpf && cliente.codigoClienteSistema"> • </span>
                    <span v-if="cliente.codigoClienteSistema">Código: #{{ cliente.codigoClienteSistema }}</span>
                  </p>
                </div>
              </div>
            </button>
          </div>

          <!-- Mensagem quando não há resultados -->
          <div
            v-if="mostrarListaClientes && !clienteSelecionado && buscaCliente && clientesFiltrados.length === 0"
            class="p-4 bg-gray-50 border border-gray-300 rounded-lg text-center text-gray-600"
          >
            <FontAwesome icon="search" class="text-2xl mb-2 text-gray-400" />
            <p class="text-sm">Nenhum cliente encontrado com <strong>"{{ buscaCliente }}"</strong></p>
          </div>
        </div>

        <!-- Tipo de Venda -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            <FontAwesome icon="store" class="mr-2 text-violet-500" />
            Tipo de Venda
          </label>
          <div class="grid grid-cols-3 gap-3">
            <button
              v-for="tipo in tiposVenda"
              :key="tipo.value"
              @click="form.tipoVenda = tipo.value"
              :class="[
                'px-4 py-3 rounded-lg font-medium transition border-2',
                form.tipoVenda === tipo.value
                  ? 'bg-violet-500 text-white border-violet-500'
                  : 'bg-white text-gray-700 border-gray-300 hover:border-violet-300'
              ]"
            >
              <FontAwesome :icon="tipo.icon" class="mr-2" />
              {{ tipo.label }}
            </button>
          </div>
        </div>

        <!-- Animais e Serviços -->
        <div v-if="form.clienteId">
          <div class="flex items-center justify-between mb-3">
            <label class="block text-sm font-medium text-gray-700">
              <FontAwesome icon="paw" class="mr-2 text-violet-500" />
              Adicionar Serviços por Animal
            </label>
            <span class="text-sm text-gray-500">{{ animaisCliente.length }} animal(is) disponível(is)</span>
          </div>

          <div v-if="animaisCliente.length === 0" class="text-center py-8 text-gray-500">
            <FontAwesome icon="inbox" class="text-3xl mb-2" />
            <p>Este cliente não possui animais cadastrados</p>
            <p class="text-sm">Cadastre um animal antes de fazer a venda</p>
          </div>

          <!-- Card por Animal -->
          <div v-else class="space-y-5">
            <div
              v-for="animal in animaisCliente"
              :key="animal.id"
              class="bg-white border border-gray-200 rounded-2xl overflow-hidden shadow-sm hover:shadow-md transition-all"
            >
              <!-- Header do Animal -->
              <div class="bg-gradient-to-r from-gray-50 to-gray-100 px-5 py-4 border-b border-gray-200">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-3">
                    <div class="w-11 h-11 bg-gradient-to-br from-violet-500 to-purple-600 rounded-xl flex items-center justify-center text-white font-bold text-lg shadow-sm">
                      {{ animal.nome.charAt(0).toUpperCase() }}
                    </div>
                    <div>
                      <h3 class="font-bold text-gray-900">{{ animal.nome }}</h3>
                      <p class="text-xs text-gray-500 mt-0.5">
                        {{ animal.tipo || 'Animal' }}
                        {{ animal.raca ? `• ${animal.raca}` : '' }}
                        {{ animal.peso ? `• ${animal.peso}kg` : '' }}
                      </p>
                    </div>
                  </div>
                  <button
                    @click="abrirModalAdicionarServico(animal)"
                    class="px-4 py-2 bg-violet-600 hover:bg-violet-700 text-white rounded-xl text-sm font-medium transition-colors shadow-sm"
                  >
                    <FontAwesome icon="plus" class="mr-1.5" />
                    Adicionar
                  </button>
                </div>
              </div>

              <!-- Conteúdo: Serviços -->
              <div class="p-4 space-y-2">
                <!-- Serviços Existentes -->
                <div
                  v-for="servico in getServicosExistentesAnimal(animal.id)"
                  :key="servico.id"
                  class="group bg-blue-50/50 border border-blue-200 rounded-xl p-3.5 cursor-pointer hover:bg-blue-50 hover:border-blue-300 transition-all"
                  :class="{ 'bg-violet-50 border-violet-300 ring-2 ring-violet-200': servicosExistentesSelecionados.includes(servico.id) }"
                  @click="toggleServicoExistente(servico.id)"
                >
                  <div class="flex gap-3">
                    <input
                      type="checkbox"
                      :checked="servicosExistentesSelecionados.includes(servico.id)"
                      @click.stop
                      class="mt-0.5 w-4 h-4 text-violet-600 rounded focus:ring-violet-500 flex-shrink-0"
                    />
                    <div class="flex-1 min-w-0">
                      <div class="flex items-center gap-2 flex-wrap mb-1">
                        <p class="font-bold text-gray-900">{{ servico.servico?.nome || 'Serviço' }}</p>
                        <span v-if="servico.servico?.quantidade && servico.servico.quantidade > 1" class="inline-flex items-center gap-1 px-2 py-0.5 bg-blue-100 text-blue-700 rounded-md text-[10px] font-semibold">
                          <FontAwesome icon="box" class="text-[9px]" />
                          Pacote {{ servico.servico?.quantidade }}x
                        </span>
                        <span
                          class="inline-flex items-center gap-1 text-[10px] px-2 py-0.5 rounded-md font-semibold"
                          :class="{
                            'bg-green-100 text-green-700': servico.statusPagamento === 'pago',
                            'bg-yellow-100 text-yellow-700': servico.statusPagamento === 'em_aberto'
                          }"
                        >
                          <FontAwesome
                            :icon="servico.statusPagamento === 'pago' ? 'check-circle' : 'clock'"
                            class="text-[9px]"
                          />
                          {{ servico.statusPagamento === 'pago' ? 'Pago' : 'Em Aberto' }}
                        </span>
                      </div>
                      <div class="flex items-center gap-3 text-[11px] text-gray-600">
                        <div class="flex items-center gap-1.5">
                          <FontAwesome icon="calendar" class="text-gray-400 text-[10px]" />
                          <span>{{ formatarData(servico.dataServico) }}</span>
                        </div>
                        <div v-if="servico.servico?.quantidade && servico.servico.quantidade > 1" class="flex items-center gap-1.5">
                          <FontAwesome icon="check-circle" class="text-blue-500 text-[10px]" />
                          <span>{{ servico.banhosUsados || 0 }}/{{ servico.servico.quantidade }} realizados</span>
                        </div>
                      </div>
                    </div>
                    <div class="text-right flex-shrink-0">
                      <p class="text-[10px] text-gray-500 uppercase tracking-wide font-medium mb-0.5">Valor</p>
                      <p class="font-bold text-gray-900">{{ formatarMoeda(servico.servico?.valor || 0) }}</p>
                    </div>
                  </div>
                </div>

                <!-- Novos Serviços -->
                <div
                  v-for="(item, index) in getServicosNovosAnimal(animal.id)"
                  :key="`novo-${index}`"
                  class="bg-gradient-to-br from-green-50 to-emerald-50 border border-green-300 rounded-xl p-3 hover:shadow-sm transition-all"
                >
                  <div class="flex items-center justify-between gap-3">
                    <!-- Lado Esquerdo: Info -->
                    <div class="flex-1 min-w-0">
                      <!-- Nome e Badges -->
                      <div class="flex items-center gap-2 mb-1 flex-wrap">
                        <h4 class="font-bold text-gray-900 text-sm">{{ item.servicoNome }}</h4>
                        <span class="px-2 py-0.5 bg-green-500 text-white rounded-md text-[10px] font-semibold">NOVO</span>
                        <span v-if="item.servicoUnico" class="px-2 py-0.5 bg-violet-100 text-violet-700 rounded-md text-[10px] font-semibold">
                          Completo
                        </span>
                        <span v-if="item.servicoQuantidade > 1" class="px-2 py-0.5 bg-blue-100 text-blue-700 rounded-md text-[10px] font-semibold">
                          {{ item.servicoQuantidade }}x
                        </span>
                      </div>

                      <!-- Detalhes em linha única -->
                      <div class="flex items-center gap-3 text-xs text-gray-600 flex-wrap">
                        <span>{{ formatarMoeda(item.valorItem) }}</span>
                        <span v-if="item.descontoItem && item.descontoItem > 0" class="text-green-600 font-medium">
                          -{{ formatarMoeda(item.descontoItem) }}
                        </span>
                        <span v-if="!item.servicoUnico && item.banhosUsados !== undefined && item.banhosUsados > 0">
                          {{ item.banhosUsados }}/{{ item.servicoQuantidade }} usados
                        </span>
                        <span v-if="item.dataExpiracao" class="text-orange-600">
                          Expira {{ formatarData(item.dataExpiracao) }}
                        </span>
                      </div>

                      <!-- Serviços Adicionais (hierarquia visual) -->
                      <div v-if="item.servicosAdicionais && item.servicosAdicionais.length > 0" class="mt-3 pl-4 border-l-4 border-pink-300 space-y-2">
                        <div class="flex items-center gap-2 mb-2">
                          <FontAwesome icon="plus-circle" class="text-pink-600 text-xs" />
                          <span class="text-xs font-semibold text-gray-700">Serviços Adicionais:</span>
                        </div>
                        <div
                          v-for="(adicional, idx) in item.servicosAdicionais"
                          :key="idx"
                          class="p-2 bg-white/70 border border-pink-200 rounded-lg"
                        >
                          <div class="flex items-start justify-between gap-2">
                            <div class="flex-1 min-w-0">
                              <div class="flex items-center gap-2 mb-1">
                                <span class="text-xs font-semibold text-gray-800">{{ adicional.servicoNome }}</span>
                                <span class="px-1.5 py-0.5 bg-pink-100 text-pink-700 rounded text-[9px] font-medium">
                                  Qtd: {{ adicional.quantidade }}
                                </span>
                              </div>
                              <div class="flex items-center gap-2 text-[10px] text-gray-600">
                                <span>Unit: {{ formatarMoeda(adicional.valorUnitario) }}</span>
                                <span class="w-1 h-1 rounded-full bg-gray-300"></span>
                                <span class="font-semibold">Total: {{ formatarMoeda(adicional.valorTotal) }}</span>
                              </div>
                              <p v-if="adicional.observacoes" class="text-[10px] text-gray-500 mt-1">
                                {{ adicional.observacoes }}
                              </p>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- Lado Direito: Valor e Ação -->
                    <div class="flex items-center gap-3 flex-shrink-0">
                      <div class="text-right">
                        <p class="font-bold text-gray-900 text-lg whitespace-nowrap">
                          {{ formatarMoeda(item.valorItem - (item.descontoItem || 0) + (item.servicosAdicionais?.reduce((sum, a) => sum + a.valorTotal, 0) || 0)) }}
                        </p>
                        <p v-if="item.descontoItem && item.descontoItem > 0" class="text-xs text-gray-500 line-through">
                          {{ formatarMoeda(item.valorItem) }}
                        </p>
                        <p v-if="item.servicosAdicionais && item.servicosAdicionais.length > 0" class="text-xs text-pink-600 font-medium">
                          +{{ formatarMoeda(item.servicosAdicionais.reduce((sum, a) => sum + a.valorTotal, 0)) }} adicionais
                        </p>
                      </div>
                      <button
                        @click="removerNovoServico(animal.id, index)"
                        class="w-8 h-8 flex items-center justify-center text-red-500 hover:text-white hover:bg-red-500 rounded-lg transition-all"
                        title="Remover"
                      >
                        <FontAwesome icon="trash" class="text-sm" />
                      </button>
                    </div>
                  </div>
                </div>

                <!-- Mensagem vazia -->
                <div v-if="getServicosExistentesAnimal(animal.id).length === 0 && getServicosNovosAnimal(animal.id).length === 0" class="text-center py-8 text-gray-400">
                  <FontAwesome icon="inbox" class="text-2xl mb-2 opacity-50" />
                  <p class="text-sm">Nenhum serviço adicionado</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Desconto (opcional) -->
        <div v-if="temServicos">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            <FontAwesome icon="tag" class="mr-2 text-green-500" />
            Desconto (opcional)
          </label>
          <input
            v-model.number="form.desconto"
            type="number"
            step="0.01"
            min="0"
            :max="valorTotalBruto"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500"
            placeholder="0.00"
          />
        </div>

        <!-- Observações -->
        <div v-if="temServicos">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            <FontAwesome icon="sticky-note" class="mr-2 text-yellow-500" />
            Observações (opcional)
          </label>
          <textarea
            v-model="form.observacoes"
            rows="3"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500"
            placeholder="Observações sobre a venda..."
          ></textarea>
        </div>

        <!-- Resumo da Venda -->
        <div v-if="temServicos" class="bg-gradient-to-br from-violet-50 to-purple-50 rounded-xl p-6 border-2 border-violet-200">
          <h3 class="font-bold text-lg text-gray-800 mb-4">Resumo da Venda</h3>

          <div class="space-y-2 text-sm">
            <div class="flex justify-between">
              <span class="text-gray-600">Serviços existentes:</span>
              <span class="font-semibold">{{ servicosExistentesSelecionados.length }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">Novos serviços:</span>
              <span class="font-semibold">{{ novosServicos.length }}</span>
            </div>
            <div class="flex justify-between font-semibold">
              <span class="text-gray-600">Total de itens:</span>
              <span>{{ totalItens }}</span>
            </div>
            <div class="flex justify-between pt-2 border-t">
              <span class="text-gray-600">Valor Bruto:</span>
              <span class="font-semibold">{{ formatarMoeda(valorTotalBruto) }}</span>
            </div>
            <div v-if="form.desconto > 0" class="flex justify-between text-green-600">
              <span>Desconto:</span>
              <span class="font-semibold">-{{ formatarMoeda(form.desconto) }}</span>
            </div>
            <div class="flex justify-between text-lg font-bold text-violet-600 pt-2 border-t-2 border-violet-200">
              <span>TOTAL:</span>
              <span>{{ formatarMoeda(valorTotal) }}</span>
            </div>
          </div>
        </div>

        <!-- Botões de ação -->
        <div class="flex gap-3 pt-4 border-t">
          <button
            @click="$router.push('/vendas')"
            class="flex-1 px-6 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition font-medium"
          >
            <FontAwesome icon="times" class="mr-2" />
            Cancelar
          </button>
          <button
            @click="criarVenda"
            :disabled="!podecriarVenda || salvando"
            class="flex-1 px-6 py-3 bg-gradient-to-r from-violet-500 to-purple-600 text-white rounded-lg hover:from-violet-600 hover:to-purple-700 transition font-medium shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <FontAwesome :icon="salvando ? 'spinner' : 'check'" :class="{ 'animate-spin': salvando }" class="mr-2" />
            {{ salvando ? 'Criando...' : 'Criar Venda' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Modal Adicionar Serviço -->
    <div
      v-if="modalAberto"
      class="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4"
      @click.self="fecharModal"
    >
      <div class="bg-white rounded-2xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-hidden flex flex-col">
        <!-- Header -->
        <div class="sticky top-0 bg-gradient-to-br from-violet-600 via-purple-600 to-violet-700 text-white px-6 py-5 flex items-center justify-between">
          <div>
            <h2 class="text-2xl font-bold tracking-tight">Adicionar Serviço</h2>
            <div class="flex items-center gap-2 mt-1 text-violet-100">
              <div class="w-1.5 h-1.5 rounded-full bg-violet-300"></div>
              <p class="text-sm font-medium">{{ animalSelecionado?.nome }}</p>
            </div>
          </div>
          <button
            @click="fecharModal"
            class="w-10 h-10 rounded-full hover:bg-white/10 transition-colors flex items-center justify-center"
          >
            <FontAwesome icon="times" class="text-xl" />
          </button>
        </div>

        <!-- Content com scroll -->
        <div class="flex-1 overflow-y-auto">
          <div class="p-6 space-y-5">
            <!-- Seleção de Serviço com Busca -->
            <div class="space-y-3">
              <label class="block text-sm font-semibold text-gray-900">
                Selecione o Serviço
                <span class="text-violet-600">*</span>
              </label>

              <!-- Campo de busca -->
              <div class="relative">
                <input
                  v-model="buscaServico"
                  type="text"
                  placeholder="Busque por nome do serviço..."
                  class="w-full px-4 py-3 pl-11 pr-10 border-2 border-gray-200 rounded-xl focus:border-violet-400 focus:ring-4 focus:ring-violet-100 transition-all outline-none"
                  @focus="mostrarListaServicos = true"
                />
                <FontAwesome
                  icon="search"
                  class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 text-sm"
                />
                <button
                  v-if="buscaServico"
                  @click="limparBuscaServico"
                  class="absolute right-3 top-1/2 -translate-y-1/2 w-6 h-6 rounded-full hover:bg-gray-100 flex items-center justify-center transition-colors"
                >
                  <FontAwesome icon="times" class="text-gray-400 text-xs" />
                </button>
              </div>

              <!-- Serviço selecionado -->
              <div v-if="servicoSelecionadoNoModal" class="p-4 bg-gradient-to-br from-violet-50 to-purple-50 border-2 border-violet-200 rounded-xl">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-3">
                    <div class="w-11 h-11 bg-gradient-to-br from-violet-500 to-purple-600 rounded-xl flex items-center justify-center text-white shadow-lg">
                      <FontAwesome icon="cut" class="text-lg" />
                    </div>
                    <div>
                      <p class="font-bold text-gray-900">{{ servicoSelecionadoNoModal.nome }}</p>
                      <div class="flex items-center gap-2 text-sm text-gray-600 mt-0.5">
                        <span class="font-semibold text-violet-600">{{ formatarMoeda(servicoSelecionadoNoModal.valor || 0) }}</span>
                        <span class="w-1 h-1 rounded-full bg-gray-300"></span>
                        <span v-if="servicoSelecionadoNoModal.quantidade > 1">
                          Pacote {{ servicoSelecionadoNoModal.quantidade }}x
                        </span>
                        <span v-else>Avulso</span>
                      </div>
                    </div>
                  </div>
                  <button
                    @click="desselecionarServico"
                    class="w-8 h-8 rounded-lg hover:bg-red-50 text-gray-400 hover:text-red-500 transition-all flex items-center justify-center"
                    title="Remover seleção"
                  >
                    <FontAwesome icon="times" class="text-lg" />
                  </button>
                </div>
              </div>

              <!-- Lista de serviços filtrados -->
              <div
                v-if="mostrarListaServicos && !servicoSelecionadoNoModal && servicosFiltrados.length > 0"
                class="max-h-60 overflow-y-auto border-2 border-gray-200 rounded-xl shadow-sm bg-white"
              >
                <button
                  v-for="servico in servicosFiltrados"
                  :key="servico.id"
                  @click="selecionarServico(servico)"
                  class="w-full px-4 py-3 text-left hover:bg-violet-50 transition-colors border-b border-gray-100 last:border-0 group"
                >
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 bg-violet-100 group-hover:bg-violet-200 rounded-lg flex items-center justify-center text-violet-600 transition-colors">
                      <FontAwesome icon="cut" class="text-sm" />
                    </div>
                    <div class="flex-1">
                      <p class="font-semibold text-gray-900">{{ servico.nome }}</p>
                      <div class="flex items-center gap-2 text-xs text-gray-500 mt-0.5">
                        <span>{{ formatarMoeda(servico.valor || 0) }}</span>
                        <span class="w-1 h-1 rounded-full bg-gray-300"></span>
                        <span v-if="servico.quantidade > 1">Pacote {{ servico.quantidade }}x</span>
                        <span v-else>Avulso</span>
                      </div>
                    </div>
                  </div>
                </button>
              </div>

              <!-- Mensagem quando não há resultados -->
              <div
                v-if="mostrarListaServicos && !servicoSelecionadoNoModal && buscaServico && servicosFiltrados.length === 0"
                class="p-6 bg-gray-50 border-2 border-gray-200 rounded-xl text-center"
              >
                <div class="w-12 h-12 mx-auto bg-gray-200 rounded-full flex items-center justify-center mb-3">
                  <FontAwesome icon="search" class="text-xl text-gray-400" />
                </div>
                <p class="text-sm text-gray-600">Nenhum serviço encontrado com <strong>"{{ buscaServico }}"</strong></p>
              </div>
            </div>

            <!-- Configurações do Serviço -->
            <div v-if="novoServico.servicoId" class="space-y-4">

              <!-- Checkbox Serviço Único -->
              <div
                v-if="servicoSelecionadoDetalhes && servicoSelecionadoDetalhes.podeSerAdicional === true"
                class="p-4 bg-violet-50/50 border-2 border-violet-200 rounded-xl"
              >
                <label class="flex items-start gap-3 cursor-pointer group">
                  <input
                    id="servicoUnicoModal"
                    v-model="novoServico.servicoUnico"
                    type="checkbox"
                    class="mt-0.5 w-5 h-5 text-violet-600 bg-white border-2 border-violet-300 rounded focus:ring-4 focus:ring-violet-100 transition-all"
                  />
                  <div class="flex-1">
                    <span class="text-sm font-semibold text-gray-900 group-hover:text-violet-700 transition-colors">
                      Serviço já realizado (completo)
                    </span>
                    <p class="text-xs text-gray-600 mt-1">
                      Marque se o serviço já foi realizado e todos os banhos estão completos
                    </p>
                  </div>
                </label>
              </div>

              <!-- Data de Expiração -->
              <div
                v-if="servicoSelecionadoDetalhes && servicoSelecionadoDetalhes.podeSerAdicional !== true && servicoSelecionadoDetalhes.quantidade > 1"
                class="space-y-2"
              >
                <label class="block text-sm font-semibold text-gray-900">
                  Data de Expiração do Pacote
                  <span class="text-gray-500 font-normal">(opcional)</span>
                </label>
                <input
                  v-model="novoServico.dataExpiracao"
                  type="date"
                  class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-xl focus:border-violet-400 focus:ring-4 focus:ring-violet-100 transition-all outline-none"
                />
                <p class="text-xs text-gray-500">
                  Data limite para usar todos os banhos do pacote
                </p>
              </div>

              <!-- Banhos Já Utilizados -->
              <div
                v-if="servicoSelecionadoDetalhes && servicoSelecionadoDetalhes.podeSerAdicional !== true"
                class="space-y-2"
              >
                <label class="block text-sm font-semibold text-gray-900">
                  Banhos Já Utilizados
                  <span class="text-gray-500 font-normal">(opcional)</span>
                </label>
                <input
                  v-model.number="novoServico.banhosUsados"
                  type="number"
                  min="0"
                  :max="maxBanhosPermitidosModal"
                  class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-xl focus:border-violet-400 focus:ring-4 focus:ring-violet-100 transition-all outline-none"
                  placeholder="0"
                  @input="validarBanhosUsadosModal"
                />
                <p class="text-xs text-gray-500">
                  Máximo: {{ maxBanhosPermitidosModal }} banhos
                </p>

                <!-- Progress bar -->
                <div v-if="novoServico.banhosUsados > 0" class="mt-3">
                  <div class="flex items-center justify-between text-xs text-gray-600 mb-2">
                    <span>Progresso</span>
                    <span class="font-semibold">{{ novoServico.banhosUsados }} / {{ maxBanhosPermitidosModal }}</span>
                  </div>
                  <div class="bg-gray-200 rounded-full h-2.5 overflow-hidden">
                    <div
                      class="h-full transition-all duration-500 rounded-full"
                      :class="novoServico.banhosUsados > maxBanhosPermitidosModal ? 'bg-red-500' : 'bg-gradient-to-r from-violet-500 to-purple-600'"
                      :style="{ width: Math.min((novoServico.banhosUsados / maxBanhosPermitidosModal) * 100, 100) + '%' }"
                    ></div>
                  </div>
                </div>
              </div>

              <!-- Datas dos Banhos Já Realizados -->
              <div v-if="servicoSelecionadoDetalhes && servicoSelecionadoDetalhes.podeSerAdicional !== true && novoServico.banhosUsados > 0" class="space-y-3">
                <div class="flex items-center justify-between">
                  <label class="text-sm font-semibold text-gray-900">
                    Datas dos Banhos Realizados
                  </label>
                  <span class="px-2.5 py-1 bg-violet-100 text-violet-700 rounded-lg text-xs font-bold">
                    {{ novoServico.banhosUsados }} banho{{ novoServico.banhosUsados > 1 ? 's' : '' }}
                  </span>
                </div>

                <div class="space-y-3 max-h-64 overflow-y-auto pr-1">
                  <div
                    v-for="(data, index) in novoServico.datasBanhosRealizados"
                    :key="index"
                    class="p-3 bg-gray-50 border border-gray-200 rounded-xl"
                  >
                    <div class="flex gap-3">
                      <div class="flex-shrink-0 w-7 h-7 bg-violet-500 rounded-lg flex items-center justify-center text-white font-bold text-xs">
                        {{ index + 1 }}
                      </div>
                      <div class="flex-grow space-y-2.5">
                        <div>
                          <label class="block text-xs font-medium text-gray-700 mb-1">
                            Data do {{ index + 1 }}º Banho
                            <span class="text-violet-600">*</span>
                          </label>
                          <input
                            v-model="novoServico.datasBanhosRealizados[index]"
                            type="date"
                            required
                            class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:border-violet-400 focus:ring-2 focus:ring-violet-100 transition-all outline-none"
                          />
                        </div>
                        <div>
                          <label class="block text-xs font-medium text-gray-700 mb-1">
                            Observações <span class="font-normal">(opcional)</span>
                          </label>
                          <input
                            v-model="novoServico.observacoesBanhos[index]"
                            type="text"
                            placeholder="Ex: Banho completo..."
                            class="w-full px-3 py-2 text-sm border border-gray-300 rounded-lg focus:border-violet-400 focus:ring-2 focus:ring-violet-100 transition-all outline-none"
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Valores -->
              <div class="grid grid-cols-2 gap-4">
                <!-- Valor Customizado -->
                <div class="space-y-2">
                  <label class="block text-sm font-semibold text-gray-900">
                    Valor Customizado
                    <span class="text-gray-500 font-normal">(opcional)</span>
                  </label>
                  <input
                    v-model.number="novoServico.valorItem"
                    type="number"
                    step="0.01"
                    min="0"
                    class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-xl focus:border-violet-400 focus:ring-4 focus:ring-violet-100 transition-all outline-none"
                    :placeholder="formatarMoeda(servicoSelecionadoDetalhes?.valor || 0)"
                  />
                </div>

                <!-- Desconto -->
                <div class="space-y-2">
                  <label class="block text-sm font-semibold text-gray-900">
                    Desconto
                    <span class="text-gray-500 font-normal">(opcional)</span>
                  </label>
                  <input
                    v-model.number="novoServico.descontoItem"
                    type="number"
                    step="0.01"
                    min="0"
                    :max="novoServico.valorItem"
                    class="w-full px-4 py-2.5 border-2 border-gray-200 rounded-xl focus:border-violet-400 focus:ring-4 focus:ring-violet-100 transition-all outline-none"
                    placeholder="0.00"
                  />
                </div>
              </div>

              <!-- 🔧 Serviços Adicionais -->
              <div class="space-y-4 p-5 rounded-xl bg-gradient-to-br from-pink-50 to-rose-50 border-2 border-pink-200">
                <div class="flex items-center justify-between">
                  <label class="flex items-center text-base font-bold text-gray-800">
                    <FontAwesome icon="plus-circle" class="mr-2 text-pink-600" />
                    Serviços Adicionais
                  </label>
                  <div class="flex items-center gap-1">
                    <div class="w-2 h-2 bg-pink-500 rounded-full animate-pulse"></div>
                    <div class="w-1.5 h-1.5 bg-rose-400 rounded-full animate-pulse" style="animation-delay: 0.2s"></div>
                  </div>
                </div>

                <!-- Lista de Serviços Adicionais -->
                <div v-if="novoServico.servicosAdicionais && novoServico.servicosAdicionais.length > 0" class="space-y-2">
                  <div
                    v-for="(adicional, index) in novoServico.servicosAdicionais"
                    :key="index"
                    class="p-3 bg-white/80 border border-pink-200 rounded-lg hover:shadow-md transition-all"
                  >
                    <div class="flex items-start justify-between">
                      <div class="flex-1">
                        <div class="flex items-center gap-2 mb-1">
                          <span class="font-semibold text-gray-800 text-sm">{{ adicional.servicoNome }}</span>
                          <span class="px-2 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-700">
                            Herda status do pai
                          </span>
                        </div>
                        <div class="flex items-center gap-3 text-xs text-gray-600">
                          <span>Qtd: {{ adicional.quantidade }}</span>
                          <span>Unit: {{ formatarMoeda(adicional.valorUnitario) }}</span>
                          <span class="font-semibold">Total: {{ formatarMoeda(adicional.valorTotal) }}</span>
                        </div>
                        <p v-if="adicional.observacoes" class="text-xs text-gray-500 mt-1">{{ adicional.observacoes }}</p>
                      </div>
                      <button
                        type="button"
                        @click="removerServicoAdicional(index)"
                        class="p-1.5 text-red-500 hover:text-red-700 hover:bg-red-50 rounded-lg transition-colors"
                        title="Remover serviço adicional"
                      >
                        <FontAwesome icon="trash" class="text-sm" />
                      </button>
                    </div>
                  </div>
                </div>

                <!-- Botão Adicionar Serviço -->
                <button
                  type="button"
                  @click="abrirModalServicoAdicional"
                  class="w-full flex items-center justify-center gap-2 p-3 bg-gradient-to-r from-pink-100 to-rose-100 border-2 border-dashed border-pink-300 rounded-lg hover:from-pink-200 hover:to-rose-200 hover:border-pink-400 transition-all"
                >
                  <FontAwesome icon="plus" class="text-pink-600" />
                  <span class="font-semibold text-pink-700 text-sm">Adicionar Serviço Extra</span>
                </button>

                <p class="text-xs text-gray-500 flex items-center gap-2">
                  <FontAwesome icon="info-circle" class="text-pink-500" />
                  Adicione serviços extras que serão cobrados junto com o serviço principal
                </p>
              </div>

              <!-- Resumo -->
              <div class="p-4 bg-gradient-to-br from-violet-500 to-purple-600 rounded-xl text-white">
                <div class="space-y-2">
                  <div class="flex justify-between items-center text-sm">
                    <span class="text-violet-100">Valor do Serviço</span>
                    <span class="font-semibold">{{ formatarMoeda(novoServico.valorItem) }}</span>
                  </div>
                  <div v-if="novoServico.descontoItem > 0" class="flex justify-between items-center text-sm">
                    <span class="text-violet-100">Desconto</span>
                    <span class="font-semibold">-{{ formatarMoeda(novoServico.descontoItem) }}</span>
                  </div>
                  <div v-if="valorTotalAdicionaisNovoServico > 0" class="flex justify-between items-center text-sm">
                    <span class="text-violet-100">Serviços Adicionais</span>
                    <span class="font-semibold">+{{ formatarMoeda(valorTotalAdicionaisNovoServico) }}</span>
                  </div>
                  <div class="flex justify-between items-center pt-2 border-t border-violet-400/30 text-lg font-bold">
                    <span>TOTAL</span>
                    <span>{{ formatarMoeda(novoServico.valorItem - (novoServico.descontoItem || 0) + valorTotalAdicionaisNovoServico) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Footer com Botões -->
        <div class="sticky bottom-0 bg-white border-t border-gray-200 px-6 py-4 flex gap-3">
          <button
            @click="fecharModal"
            class="flex-1 px-6 py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-xl font-medium transition-colors"
          >
            Cancelar
          </button>
          <button
            @click="adicionarServico"
            :disabled="!podeAdicionarServico"
            class="flex-1 px-6 py-3 bg-gradient-to-r from-violet-500 to-purple-600 hover:from-violet-600 hover:to-purple-700 text-white rounded-xl font-semibold shadow-lg disabled:opacity-50 disabled:cursor-not-allowed transition-all"
            :title="!podeAdicionarServico ? 'Selecione um serviço' : ''"
          >
            Adicionar Serviço
          </button>
        </div>
      </div>
    </div>

    <!-- Modal Adicionar Serviço Adicional -->
    <div
      v-if="modalServicoAdicionalAberto"
      class="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-[60] p-4"
      @click.self="fecharModalServicoAdicional"
    >
      <div class="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-hidden flex flex-col">
        <!-- Header -->
        <div class="bg-gradient-to-br from-pink-600 via-rose-600 to-pink-700 text-white px-6 py-4 flex items-center justify-between">
          <div>
            <h2 class="text-xl font-bold">Adicionar Serviço Extra</h2>
            <p class="text-sm text-pink-100 mt-0.5">Será vinculado ao serviço principal</p>
          </div>
          <button
            @click="fecharModalServicoAdicional"
            class="w-10 h-10 rounded-full hover:bg-white/10 transition-colors flex items-center justify-center"
          >
            <FontAwesome icon="times" class="text-xl" />
          </button>
        </div>

        <!-- Content -->
        <div class="flex-1 overflow-y-auto p-6 space-y-5">
          <!-- Seleção do Serviço Adicional -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              Serviço Adicional *
            </label>
            <select
              v-model="formularioAdicional.servicoId"
              @change="atualizarValorServicoAdicional"
              class="w-full px-4 py-3 bg-white border-2 border-gray-200 rounded-xl focus:border-pink-400 focus:ring-4 focus:ring-pink-100 transition-all outline-none"
              required
            >
              <option value="">Selecione um serviço...</option>
              <option v-for="servico in servicosAdicionaisDisponiveis" :key="servico.id" :value="servico.id">
                {{ servico.nome }} - {{ formatarMoeda(servico.valor) }}
              </option>
            </select>
          </div>

          <!-- Quantidade -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              Quantidade *
            </label>
            <input
              v-model.number="formularioAdicional.quantidade"
              type="number"
              min="1"
              required
              class="w-full px-4 py-3 bg-white border-2 border-gray-200 rounded-xl focus:border-pink-400 focus:ring-4 focus:ring-pink-100 transition-all outline-none"
              @input="calcularValorTotalAdicional"
            />
          </div>

          <!-- Valor -->
          <div class="space-y-3">
            <div>
              <label class="block text-sm font-semibold text-gray-700 mb-2">
                Valor do Serviço
              </label>
              <div class="p-3 bg-gray-50 rounded-xl border border-gray-200">
                <div class="font-semibold text-gray-800">
                  {{ formatarMoeda(formularioAdicional.valorUnitarioOriginal) }}
                </div>
                <div class="text-xs text-gray-500">Valor padrão do serviço</div>
              </div>
            </div>

            <!-- Checkbox para alterar valor -->
            <div class="flex items-start gap-3">
              <input
                v-model="formularioAdicional.alterarValor"
                type="checkbox"
                id="alterarValorAdicional"
                class="mt-1 w-4 h-4 text-pink-600 bg-gray-100 border-gray-300 rounded focus:ring-pink-500"
                @change="toggleAlterarValorAdicional"
              />
              <div class="flex-1">
                <label for="alterarValorAdicional" class="text-sm font-medium text-gray-700 cursor-pointer">
                  Alterar valor para este atendimento específico
                </label>
                <p class="text-xs text-gray-500 mt-1">
                  Marque para cobrar um valor diferente do padrão
                </p>
              </div>
            </div>

            <!-- Valor Personalizado -->
            <div v-if="formularioAdicional.alterarValor">
              <label class="block text-sm font-semibold text-gray-700 mb-2">
                Valor Personalizado *
              </label>
              <input
                v-model.number="formularioAdicional.valorUnitario"
                type="number"
                step="0.01"
                min="0"
                required
                class="w-full px-4 py-3 bg-white border-2 border-pink-200 rounded-xl focus:border-pink-400 focus:ring-4 focus:ring-pink-100 transition-all outline-none"
                @input="calcularValorTotalAdicional"
              />
            </div>
          </div>

          <!-- Info sobre herança -->
          <div class="p-4 bg-blue-50 border border-blue-200 rounded-xl">
            <div class="flex items-start gap-2">
              <FontAwesome icon="info-circle" class="text-blue-600 mt-0.5" />
              <div class="text-sm text-blue-800">
                <p class="font-medium">Status e Data de Pagamento</p>
                <p>Herdados automaticamente do serviço principal</p>
              </div>
            </div>
          </div>

          <!-- Observações -->
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              Observações (opcional)
            </label>
            <textarea
              v-model="formularioAdicional.observacoes"
              rows="3"
              class="w-full px-4 py-3 bg-white border-2 border-gray-200 rounded-xl focus:border-pink-400 focus:ring-4 focus:ring-pink-100 transition-all outline-none resize-none"
              placeholder="Ex: Tosa completa, apenas patas, etc."
            ></textarea>
          </div>

          <!-- Resumo do Valor -->
          <div class="p-4 bg-pink-50 rounded-xl border border-pink-200">
            <div class="flex items-center justify-between">
              <span class="font-medium text-gray-700">Valor Total:</span>
              <span class="text-2xl font-bold text-pink-600">
                {{ formatarMoeda(formularioAdicional.valorTotal) }}
              </span>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="border-t border-gray-200 px-6 py-4 flex gap-3">
          <button
            @click="fecharModalServicoAdicional"
            class="flex-1 px-6 py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-xl font-medium transition-colors"
          >
            Cancelar
          </button>
          <button
            @click="adicionarServicoAdicionalAoServico"
            :disabled="!formularioAdicionalValido"
            class="flex-1 px-6 py-3 bg-gradient-to-r from-pink-500 to-rose-500 hover:from-pink-600 hover:to-rose-600 text-white rounded-xl font-semibold shadow-lg disabled:opacity-50 disabled:cursor-not-allowed transition-all"
          >
            Adicionar
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { vendasService, clientesService, animalServicoService, servicosService } from '@/services/api'
import { FontAwesomeIcon as FontAwesome } from '@fortawesome/vue-fontawesome'
import { formatarValor } from '@/utils/formatters'
import type { Animal, AnimalServico, ServicoCompleto } from '@/types/api'

const router = useRouter()

const clientes = ref<any[]>([])
const animaisCliente = ref<Animal[]>([])
const servicosExistentesCliente = ref<AnimalServico[]>([])
const servicosExistentesSelecionados = ref<number[]>([])
const servicosCatalogo = ref<ServicoCompleto[]>([])
const salvando = ref(false)

// Busca de clientes
const buscaCliente = ref('')
const mostrarListaClientes = ref(false)
const clienteSelecionado = ref<any>(null)

// Busca de serviços
const buscaServico = ref('')
const mostrarListaServicos = ref(false)
const servicoSelecionadoNoModal = ref<ServicoCompleto | null>(null)

interface NovoServicoItem {
  animalId: number
  animalNome: string
  servicoId: number
  servicoNome: string
  servicoQuantidade: number
  valorItem: number
  descontoItem: number
  // Configurações avançadas
  servicoUnico?: boolean
  dataExpiracao?: string
  banhosUsados?: number
  datasBanhosRealizados?: string[]
  observacoesBanhos?: string[]
  servicosAdicionais?: Array<{
    servicoId: number
    servicoNome: string
    quantidade: number
    valorUnitario: number
    valorTotal: number
    observacoes?: string
    dataRealizacao?: string // ✅ Adicionar data de realização
  }>
}

const novosServicos = ref<NovoServicoItem[]>([])

const form = ref({
  clienteId: '',
  tipoVenda: 'presencial',
  desconto: 0,
  observacoes: '',
  usuarioId: 1 // TODO: pegar do contexto de autenticação
})

const tiposVenda = [
  { value: 'presencial', label: 'Presencial', icon: 'store' },
  { value: 'agendamento', label: 'Agendamento', icon: 'calendar-check' },
  { value: 'busca_entrega', label: 'Busca e Entrega', icon: 'truck' }
]

// Modal
const modalAberto = ref(false)
const animalSelecionado = ref<Animal | null>(null)
const novoServico = ref({
  servicoId: '',
  servicoNome: '',
  servicoQuantidade: 0,
  valorItem: 0,
  descontoItem: 0,
  // Configurações avançadas (como no AnimalServicoNovoView)
  servicoUnico: false,
  dataExpiracao: '',
  banhosUsados: 0,
  datasBanhosRealizados: [] as string[],
  observacoesBanhos: [] as string[],
  servicosAdicionais: [] as Array<{
    servicoId: number
    servicoNome: string
    quantidade: number
    valorUnitario: number
    valorTotal: number
    observacoes?: string
    dataRealizacao?: string // ✅ Adicionar data de realização
  }>
})

// Modal de Serviços Adicionais
const modalServicoAdicionalAberto = ref(false)
const servicosAdicionaisDisponiveis = ref<ServicoCompleto[]>([])
const formularioAdicional = ref({
  servicoId: '',
  servicoNome: '',
  quantidade: 1,
  valorUnitario: 0,
  valorUnitarioOriginal: 0,
  valorTotal: 0,
  observacoes: '',
  alterarValor: false
})

// Função para normalizar texto (remover acentos e caracteres especiais)
const normalizarTexto = (texto: string | null | undefined): string => {
  if (!texto) return ''
  return texto
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '') // Remove acentos
    .replace(/[^\w\s]/g, '') // Remove caracteres especiais
    .trim()
}

// Filtrar clientes baseado na busca
const clientesFiltrados = computed(() => {
  if (!buscaCliente.value.trim()) {
    return clientes.value
  }

  const termoBusca = normalizarTexto(buscaCliente.value)

  return clientes.value.filter(cliente => {
    // Buscar por nome
    const nome = normalizarTexto(cliente.nomeCompleto)
    if (nome.includes(termoBusca)) return true

    // Buscar por CPF (remove formatação)
    const cpf = cliente.cpf ? cliente.cpf.replace(/\D/g, '') : ''
    const cpfBusca = buscaCliente.value.replace(/\D/g, '')
    if (cpf.includes(cpfBusca)) return true

    // Buscar por código do sistema
    const codigo = cliente.codigoClienteSistema?.toString() || ''
    if (codigo.includes(buscaCliente.value.trim())) return true

    return false
  })
})

// Filtrar serviços baseado na busca
const servicosFiltrados = computed(() => {
  if (!buscaServico.value.trim()) {
    return servicosCatalogo.value
  }

  const termoBusca = normalizarTexto(buscaServico.value)

  return servicosCatalogo.value.filter(servico => {
    // Buscar por nome
    const nome = normalizarTexto(servico.nome)
    if (nome.includes(termoBusca)) return true

    // Buscar por descrição
    const descricao = normalizarTexto(servico.descricao)
    if (descricao.includes(termoBusca)) return true

    return false
  })
})

const servicoSelecionadoDetalhes = computed(() => {
  if (!novoServico.value.servicoId) return null
  return servicosCatalogo.value.find(s => s.id === Number(novoServico.value.servicoId))
})

const maxBanhosPermitidosModal = computed(() => {
  return servicoSelecionadoDetalhes.value?.quantidade || 1
})

const valorTotalAdicionaisModal = computed(() => {
  return novoServico.value.servicosAdicionais.reduce((total, adicional) => {
    return total + adicional.valorTotal
  }, 0)
})

const podeAdicionarServico = computed(() => {
  // Precisa ter um serviço selecionado
  return !!novoServico.value.servicoId
})

// Computadas para serviços adicionais
const valorTotalAdicionaisNovoServico = computed(() => {
  return novoServico.value.servicosAdicionais.reduce((total, adicional) => {
    return total + adicional.valorTotal
  }, 0)
})

const formularioAdicionalValido = computed(() => {
  return formularioAdicional.value.servicoId !== '' &&
         formularioAdicional.value.quantidade > 0 &&
         formularioAdicional.value.valorTotal > 0
})

const getServicosExistentesAnimal = (animalId: number) => {
  return servicosExistentesCliente.value.filter(s => s.animal?.id === animalId && !s.vendaId)
}

const getServicosNovosAnimal = (animalId: number) => {
  return novosServicos.value.filter(s => s.animalId === animalId)
}

const temServicos = computed(() => {
  return servicosExistentesSelecionados.value.length > 0 || novosServicos.value.length > 0
})

const totalItens = computed(() => {
  return servicosExistentesSelecionados.value.length + novosServicos.value.length
})

const valorTotalBruto = computed(() => {
  // Valor dos serviços existentes selecionados
  const valorExistentes = servicosExistentesCliente.value
    .filter(s => servicosExistentesSelecionados.value.includes(s.id))
    .reduce((acc, s) => acc + (s.servico?.valor || 0), 0)

  // Valor dos novos serviços (incluindo serviços adicionais)
  const valorNovos = novosServicos.value
    .reduce((acc, s: NovoServicoItem) => {
      const valorServico = s.valorItem - s.descontoItem
      const valorAdicionais = s.servicosAdicionais?.reduce((sum, a) => sum + a.valorTotal, 0) || 0
      return acc + valorServico + valorAdicionais
    }, 0)

  return valorExistentes + valorNovos
})

const valorTotal = computed(() => {
  return Math.max(0, valorTotalBruto.value - (form.value.desconto || 0))
})

const podecriarVenda = computed(() => {
  return form.value.clienteId && temServicos.value && valorTotal.value >= 0
})

const selecionarCliente = (cliente: any) => {
  clienteSelecionado.value = cliente
  form.value.clienteId = cliente.id
  buscaCliente.value = ''
  mostrarListaClientes.value = false
  carregarAnimaisCliente()
}

const desselecionarCliente = () => {
  clienteSelecionado.value = null
  form.value.clienteId = ''
  buscaCliente.value = ''
  animaisCliente.value = []
  servicosExistentesCliente.value = []
  servicosExistentesSelecionados.value = []
  novosServicos.value = []
}

const limparBuscaCliente = () => {
  buscaCliente.value = ''
  if (!clienteSelecionado.value) {
    mostrarListaClientes.value = false
  }
}

const selecionarServico = (servico: ServicoCompleto) => {
  servicoSelecionadoNoModal.value = servico
  novoServico.value.servicoId = servico.id.toString()
  novoServico.value.servicoNome = servico.nome
  novoServico.value.servicoQuantidade = servico.quantidade
  novoServico.value.valorItem = servico.valor || 0
  novoServico.value.descontoItem = 0
  // Manter os campos de pagamento que o usuário já configurou
  // (não resetar statusPagamento e dataPagamento)
  buscaServico.value = ''
  mostrarListaServicos.value = false
}

const desselecionarServico = () => {
  servicoSelecionadoNoModal.value = null
  novoServico.value.servicoId = ''
  novoServico.value.servicoNome = ''
  novoServico.value.servicoQuantidade = 0
  novoServico.value.valorItem = 0
  novoServico.value.descontoItem = 0
  buscaServico.value = ''
}

const limparBuscaServico = () => {
  buscaServico.value = ''
  if (!servicoSelecionadoNoModal.value) {
    mostrarListaServicos.value = false
  }
}

const carregarClientes = async () => {
  try {
    clientes.value = await clientesService.buscarTodos()
  } catch (err) {
    console.error('Erro ao carregar clientes:', err)
    alert('Erro ao carregar clientes')
  }
}

const carregarCatalogo = async () => {
  try {
    servicosCatalogo.value = await servicosService.buscarTodosSimples()
  } catch (err) {
    console.error('Erro ao carregar catálogo:', err)
    alert('Erro ao carregar catálogo de serviços')
  }
}

const carregarAnimaisCliente = async () => {
  if (!form.value.clienteId) {
    animaisCliente.value = []
    servicosExistentesCliente.value = []
    servicosExistentesSelecionados.value = []
    novosServicos.value = []
    return
  }

  try {
    // Buscar o cliente completo com seus animais
    const clienteCompleto = clientes.value.find(c => c.id === Number(form.value.clienteId))
    animaisCliente.value = clienteCompleto?.animais || []

    // Buscar todos os serviços existentes e filtrar pelo cliente
    const todosServicos = await animalServicoService.buscarTodos()
    servicosExistentesCliente.value = todosServicos.filter((s: AnimalServico) => {
      const pertenceAoCliente = s.animal?.cliente?.id === Number(form.value.clienteId)
      const naoTemVenda = !s.vendaId
      return pertenceAoCliente && naoTemVenda
    })

    servicosExistentesSelecionados.value = []
    novosServicos.value = []
  } catch (err) {
    console.error('Erro ao carregar dados do cliente:', err)
    alert('Erro ao carregar dados do cliente')
  }
}

const toggleServicoExistente = (servicoId: number) => {
  const index = servicosExistentesSelecionados.value.indexOf(servicoId)
  if (index > -1) {
    servicosExistentesSelecionados.value.splice(index, 1)
  } else {
    servicosExistentesSelecionados.value.push(servicoId)
  }
}

// Funções auxiliares para gerenciar datas dos banhos no modal
const obterDataLocal = (): string => {
  const agora = new Date()
  const ano = agora.getFullYear()
  const mes = String(agora.getMonth() + 1).padStart(2, '0')
  const dia = String(agora.getDate()).padStart(2, '0')
  return `${ano}-${mes}-${dia}`
}

const gerenciarDatasBanhosModal = (quantidadeBanhos: number): void => {
  const maxBanhosPermitidosReal = maxBanhosPermitidosModal.value || 10

  if (quantidadeBanhos > maxBanhosPermitidosReal) {
    console.warn(`⚠️ Quantidade de banhos (${quantidadeBanhos}) maior que o permitido (${maxBanhosPermitidosReal}). Limitando...`)
    quantidadeBanhos = maxBanhosPermitidosReal
    novoServico.value.banhosUsados = quantidadeBanhos
  }

  const hoje = obterDataLocal()

  if (quantidadeBanhos === 0) {
    novoServico.value.datasBanhosRealizados = []
    novoServico.value.observacoesBanhos = []
  } else {
    while (novoServico.value.datasBanhosRealizados.length < quantidadeBanhos) {
      novoServico.value.datasBanhosRealizados.push(hoje)
      novoServico.value.observacoesBanhos.push('')
    }

    if (novoServico.value.datasBanhosRealizados.length > quantidadeBanhos) {
      novoServico.value.datasBanhosRealizados.splice(quantidadeBanhos)
      novoServico.value.observacoesBanhos.splice(quantidadeBanhos)
    }
  }
}

const validarBanhosUsadosModal = (): void => {
  const maxPermitido = maxBanhosPermitidosModal.value || 10

  if (novoServico.value.banhosUsados > maxPermitido) {
    console.warn(`⚠️ Limitando banhos de ${novoServico.value.banhosUsados} para ${maxPermitido}`)
    novoServico.value.banhosUsados = maxPermitido
  }

  if (novoServico.value.banhosUsados < 0) {
    novoServico.value.banhosUsados = 0
  }

  gerenciarDatasBanhosModal(novoServico.value.banhosUsados)
}

const abrirModalAdicionarServico = (animal: Animal) => {
  animalSelecionado.value = animal
  novoServico.value = {
    servicoId: '',
    servicoNome: '',
    servicoQuantidade: 0,
    valorItem: 0,
    descontoItem: 0,
    // Configurações avançadas
    servicoUnico: false,
    dataExpiracao: '',
    banhosUsados: 0,
    datasBanhosRealizados: [],
    observacoesBanhos: [],
    servicosAdicionais: []
  }
  // Limpar busca de serviços
  buscaServico.value = ''
  servicoSelecionadoNoModal.value = null
  mostrarListaServicos.value = false

  modalAberto.value = true
}

const fecharModal = () => {
  modalAberto.value = false
  animalSelecionado.value = null

  // Limpar busca de serviços ao fechar modal
  buscaServico.value = ''
  servicoSelecionadoNoModal.value = null
  mostrarListaServicos.value = false
}

// Métodos para Serviços Adicionais
const abrirModalServicoAdicional = async () => {
  // Carregar serviços adicionais disponíveis
  try {
    const todosServicos = await servicosService.buscarTodos()
    servicosAdicionaisDisponiveis.value = todosServicos.filter(s => s.podeSerAdicional === true)
  } catch (error) {
    console.error('Erro ao carregar serviços adicionais:', error)
  }
  modalServicoAdicionalAberto.value = true
}

const fecharModalServicoAdicional = () => {
  modalServicoAdicionalAberto.value = false
  formularioAdicional.value = {
    servicoId: '',
    servicoNome: '',
    quantidade: 1,
    valorUnitario: 0,
    valorUnitarioOriginal: 0,
    valorTotal: 0,
    observacoes: '',
    alterarValor: false
  }
}

const atualizarValorServicoAdicional = () => {
  const servicoId = formularioAdicional.value.servicoId
  if (!servicoId) return

  const servico = servicosAdicionaisDisponiveis.value.find(s => s.id === Number(servicoId))
  if (servico) {
    formularioAdicional.value.servicoNome = servico.nome
    formularioAdicional.value.valorUnitario = servico.valor || 0
    formularioAdicional.value.valorUnitarioOriginal = servico.valor || 0
    formularioAdicional.value.alterarValor = false
    calcularValorTotalAdicional()
  }
}

const calcularValorTotalAdicional = () => {
  const quantidade = formularioAdicional.value.quantidade || 1
  const valorUnitario = formularioAdicional.value.alterarValor
    ? formularioAdicional.value.valorUnitario
    : formularioAdicional.value.valorUnitarioOriginal
  formularioAdicional.value.valorTotal = quantidade * valorUnitario
}

const toggleAlterarValorAdicional = () => {
  if (!formularioAdicional.value.alterarValor) {
    // Se desmarcou, volta ao valor original
    formularioAdicional.value.valorUnitario = formularioAdicional.value.valorUnitarioOriginal
  }
  calcularValorTotalAdicional()
}

const adicionarServicoAdicionalAoServico = () => {
  if (!formularioAdicionalValido.value) return

  // Preparar observações com informação sobre alteração de valor
  let observacoes = formularioAdicional.value.observacoes || ''

  if (formularioAdicional.value.alterarValor) {
    const valorOriginal = formularioAdicional.value.valorUnitarioOriginal
    const valorPersonalizado = formularioAdicional.value.valorUnitario
    const diferenca = valorPersonalizado - valorOriginal
    const tipoAlteracao = diferenca > 0 ? 'Acréscimo' : 'Desconto'

    const infoAlteracao = `${tipoAlteracao} de R$ ${Math.abs(diferenca).toFixed(2)} (valor padrão: R$ ${valorOriginal.toFixed(2)})`
    observacoes = observacoes ? `${observacoes} | ${infoAlteracao}` : infoAlteracao
  }

  // Obter data atual no formato YYYY-MM-DD
  const dataAtual = new Date().toISOString().split('T')[0]

  const novoAdicional = {
    servicoId: Number(formularioAdicional.value.servicoId),
    servicoNome: formularioAdicional.value.servicoNome,
    quantidade: formularioAdicional.value.quantidade,
    valorUnitario: formularioAdicional.value.alterarValor
      ? formularioAdicional.value.valorUnitario
      : formularioAdicional.value.valorUnitarioOriginal,
    valorTotal: formularioAdicional.value.valorTotal,
    observacoes: observacoes || undefined,
    dataRealizacao: dataAtual // ✅ Adicionar data de realização (data atual por padrão)
  }

  novoServico.value.servicosAdicionais.push(novoAdicional)
  fecharModalServicoAdicional()
}

const removerServicoAdicional = (index: number) => {
  novoServico.value.servicosAdicionais.splice(index, 1)
}

const preencherDadosServico = () => {
  const servico = servicosCatalogo.value.find(s => s.id === Number(novoServico.value.servicoId))
  if (servico) {
    novoServico.value.servicoNome = servico.nome
    novoServico.value.servicoQuantidade = servico.quantidade || 1
    novoServico.value.valorItem = servico.valor || 0
    novoServico.value.descontoItem = 0
  }
}

const adicionarServico = () => {
  if (!animalSelecionado.value || !novoServico.value.servicoId) return

  const servicoParaAdicionar = {
    animalId: animalSelecionado.value.id,
    animalNome: animalSelecionado.value.nome,
    servicoId: Number(novoServico.value.servicoId),
    servicoNome: novoServico.value.servicoNome,
    servicoQuantidade: novoServico.value.servicoQuantidade,
    valorItem: novoServico.value.valorItem,
    descontoItem: novoServico.value.descontoItem || 0,
    // Configurações avançadas
    servicoUnico: novoServico.value.servicoUnico,
    dataExpiracao: novoServico.value.dataExpiracao || undefined,
    banhosUsados: novoServico.value.banhosUsados,
    datasBanhosRealizados: [...novoServico.value.datasBanhosRealizados],
    observacoesBanhos: [...novoServico.value.observacoesBanhos],
    servicosAdicionais: [...novoServico.value.servicosAdicionais]
  }

  novosServicos.value.push(servicoParaAdicionar)

  fecharModal()
}

const removerNovoServico = (animalId: number, index: number) => {
  const servicosAnimal = getServicosNovosAnimal(animalId)
  const servicoParaRemover = servicosAnimal[index]
  if (servicoParaRemover) {
    const indexGlobal = novosServicos.value.indexOf(servicoParaRemover)
    if (indexGlobal > -1) {
      novosServicos.value.splice(indexGlobal, 1)
    }
  }
}

const criarVenda = async () => {
  if (!podecriarVenda.value) return

  try {
    salvando.value = true

    const dados = {
      clienteId: Number(form.value.clienteId),
      usuarioId: form.value.usuarioId,
      tipoVenda: form.value.tipoVenda,
      desconto: form.value.desconto || 0,
      observacoes: form.value.observacoes || undefined,
      // Serviços existentes (AnimalServico já criados)
      animalServicoIds: servicosExistentesSelecionados.value,
      // Novos serviços a criar (com todas as configurações avançadas)
      itens: novosServicos.value.map(s => ({
          animalId: s.animalId,
          servicoId: s.servicoId,
          valorItem: s.valorItem,
          descontoItem: s.descontoItem > 0 ? s.descontoItem : undefined,
          // Configurações avançadas
          servicoUnico: s.servicoUnico || undefined,
          dataExpiracao: s.dataExpiracao || undefined,
          banhosUsados: s.banhosUsados || 0,
          datasBanhosRealizados: s.datasBanhosRealizados && s.datasBanhosRealizados.length > 0 ? s.datasBanhosRealizados : undefined,
          observacoesBanhos: s.observacoesBanhos && s.observacoesBanhos.length > 0 ? s.observacoesBanhos : undefined,
          servicosAdicionais: s.servicosAdicionais && s.servicosAdicionais.length > 0 ? s.servicosAdicionais.map(adic => ({
            servicoId: adic.servicoId,
            quantidade: adic.quantidade,
            valorUnitario: adic.valorUnitario,
            observacoes: adic.observacoes || undefined,
            dataRealizacao: adic.dataRealizacao || undefined // ✅ Incluir data de realização
          })) : undefined
        }))
    }

    const vendaCriada = await vendasService.criar(dados)

    alert(`Venda #${vendaCriada.codigoVenda} criada com sucesso!`)
    router.push(`/vendas/${vendaCriada.id}`)
  } catch (err: any) {
    console.error('Erro ao criar venda:', err)
    alert('Erro ao criar venda: ' + (err.message || 'Erro desconhecido'))
  } finally {
    salvando.value = false
  }
}

const formatarData = (data: string) => {
  // Evita problema de timezone: separa a data e formata diretamente
  if (!data) return ''

  // Se já está em formato ISO (YYYY-MM-DD), converte diretamente
  if (data.includes('-')) {
    const partes = data.split('-')
    if (partes.length === 3) {
      const [ano, mes, dia] = partes
      return `${dia?.padStart(2, '0')}/${mes?.padStart(2, '0')}/${ano}`
    }
  }

  // Fallback para outros formatos
  const d = new Date(data + 'T00:00:00') // Força meia-noite local
  return d.toLocaleDateString('pt-BR')
}

const formatarMoeda = (valor: number) => formatarValor(valor)

// Watchers para o modal
watch(servicoSelecionadoDetalhes, (servicoDetalhes) => {
  if (!servicoDetalhes) return

  // Limpar data de expiração se for banho único
  if (servicoDetalhes.quantidade === 1) {
    novoServico.value.dataExpiracao = ''
  }

  // Resetar checkbox serviço único quando mudar para serviço que NÃO pode ser adicional
  if (servicoDetalhes.podeSerAdicional !== true && novoServico.value.servicoUnico) {
    novoServico.value.servicoUnico = false
  }

  // Ajustar banhos se exceder o permitido
  if (novoServico.value.banhosUsados > servicoDetalhes.quantidade) {
    console.warn(`⚠️ Ajustando banhos no modal de ${novoServico.value.banhosUsados} para 0`)
    novoServico.value.banhosUsados = 0
    gerenciarDatasBanhosModal(0)
  }
})

onMounted(() => {
  carregarClientes()
  carregarCatalogo()

  // Fechar lista de clientes ao clicar fora
  document.addEventListener('click', (e: MouseEvent) => {
    const target = e.target as HTMLElement
    if (!target.closest('.relative.mb-3') && !target.closest('.max-h-60')) {
      mostrarListaClientes.value = false
    }
  })
})
</script>

<style scoped>
.bg-pattern {
  background-image: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 20px 20px;
}
</style>
