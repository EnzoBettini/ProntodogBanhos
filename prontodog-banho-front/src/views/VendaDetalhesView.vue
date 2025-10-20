<template>
  <div class="min-h-screen bg-gradient-to-br from-violet-50 via-white to-purple-100 pb-8">
    <!-- Header -->
    <div class="relative overflow-hidden bg-gradient-to-r from-violet-600 via-purple-600 to-indigo-700 text-white pt-16 pb-24">
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
            <FontAwesome icon="receipt" class="text-2xl" />
          </div>

          <div>
            <h1 class="text-4xl font-bold">
              Venda #{{ venda?.codigoVenda }}
            </h1>
            <p class="text-violet-100 mt-1">{{ formatarDataCompleta(venda?.dataVenda) }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center py-12">
      <FontAwesome icon="spinner" class="text-4xl text-violet-500 animate-spin" />
    </div>

    <!-- Erro -->
    <div v-else-if="error" class="max-w-7xl mx-auto px-6 -mt-16">
      <div class="bg-red-50 border border-red-200 rounded-xl p-6 text-center">
        <FontAwesome icon="exclamation-triangle" class="text-3xl text-red-500 mb-3" />
        <h3 class="text-lg font-semibold text-red-700 mb-2">Erro ao carregar venda</h3>
        <p class="text-red-600">{{ error }}</p>
      </div>
    </div>

    <!-- Conteúdo Principal -->
    <div v-else-if="venda" class="max-w-7xl mx-auto px-6 -mt-16 relative z-30">

      <!-- Grid Principal -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

        <!-- Coluna Esquerda - Itens e Detalhes -->
        <div class="lg:col-span-2 space-y-6">

          <!-- Card: Informações do Cliente -->
          <div class="bg-white rounded-xl shadow-md p-6">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-xl font-bold text-gray-800 flex items-center gap-2">
                <FontAwesome icon="user" class="text-violet-500" />
                Informações do Cliente
              </h2>
              <span
                :class="[
                  'px-3 py-1 rounded-full text-xs font-semibold',
                  getStatusBadgeClass(venda.statusVenda)
                ]"
              >
                {{ getStatusLabel(venda.statusVenda) }}
              </span>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-sm text-gray-500">Nome</p>
                <p class="font-semibold text-gray-800">{{ venda.clienteNome }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-500">CPF</p>
                <p class="font-semibold text-gray-800">{{ venda.clienteCpf || '-' }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-500">Tipo de Venda</p>
                <p class="font-semibold text-gray-800">{{ getTipoVendaLabel(venda.tipoVenda) }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-500">Atendente</p>
                <p class="font-semibold text-gray-800">{{ venda.usuarioNome }}</p>
              </div>
            </div>

            <div v-if="venda.observacoes" class="mt-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
              <p class="text-sm text-gray-600">
                <FontAwesome icon="sticky-note" class="text-yellow-500 mr-2" />
                {{ venda.observacoes }}
              </p>
            </div>
          </div>

          <!-- Card: Itens da Venda -->
          <div class="bg-white rounded-xl shadow-md p-6">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-xl font-bold text-gray-800 flex items-center gap-2">
                <FontAwesome icon="box" class="text-violet-500" />
                Itens da Venda ({{ venda.itens?.length || 0 }})
              </h2>
              <button
                @click="abrirModalAdicionarItem"
                class="text-sm px-3 py-1 bg-violet-100 text-violet-700 rounded-lg hover:bg-violet-200 transition"
              >
                <FontAwesome icon="plus" class="mr-1" />
                Adicionar Item
              </button>
            </div>

            <div class="space-y-3">
              <div
                v-for="item in venda.itens"
                :key="item.id"
                class="border border-gray-200 rounded-lg p-4 hover:border-violet-300 hover:shadow-md transition cursor-pointer group"
                @click="navegarParaAnimalServico(item.animalServicoId)"
              >
                <div class="flex items-start justify-between">
                  <div class="flex-1">
                    <div class="flex items-center gap-2 mb-2">
                      <span class="px-2 py-1 bg-blue-100 text-blue-700 rounded text-xs font-semibold group-hover:bg-blue-200 transition">
                        {{ item.animalNome }}
                      </span>
                      <span v-if="item.animalTipo" class="text-xs text-gray-500">
                        {{ item.animalTipo }}
                      </span>
                      <span class="text-xs text-violet-500 opacity-0 group-hover:opacity-100 transition">
                        <FontAwesome icon="external-link-alt" class="ml-1" />
                        Ver detalhes
                      </span>
                    </div>

                    <p class="font-semibold text-gray-800 mb-1 group-hover:text-violet-600 transition">{{ item.servicoNome }}</p>

                    <!-- Serviços adicionais -->
                    <div v-if="item.servicosAdicionais && item.servicosAdicionais.length > 0" class="mt-2 ml-4 space-y-1">
                      <div
                        v-for="adicional in item.servicosAdicionais"
                        :key="adicional.id"
                        class="flex items-start gap-2 text-sm text-purple-600"
                      >
                        <FontAwesome icon="plus-circle" class="mt-0.5 flex-shrink-0" />
                        <div class="flex-1">
                          <span class="font-medium">{{ adicional.servicoNome }}</span>
                          <span v-if="adicional.quantidade > 1" class="text-purple-500">
                            ({{ adicional.quantidade }}x)
                          </span>
                          <span class="text-gray-600 ml-2">
                            {{ formatarMoeda(adicional.valorTotal) }}
                          </span>
                          <p v-if="adicional.observacoes" class="text-xs text-gray-500 mt-0.5">
                            {{ adicional.observacoes }}
                          </p>
                        </div>
                      </div>
                    </div>

                    <div v-if="item.descontoItem > 0" class="text-sm text-green-600 mt-1">
                      <FontAwesome icon="tag" class="mr-1" />
                      Desconto: {{ formatarMoeda(item.descontoItem) }}
                    </div>
                  </div>

                  <div class="text-right">
                    <p class="text-xs text-gray-500 line-through" v-if="item.descontoItem > 0">
                      {{ formatarMoeda(item.valorItem) }}
                    </p>
                    <p class="text-lg font-bold text-gray-800">
                      {{ formatarMoeda(item.valorFinalItem) }}
                    </p>
                    <button
                      @click.stop.prevent="removerItem(item.id)"
                      class="mt-2 text-xs px-2 py-1 bg-red-100 text-red-600 hover:bg-red-200 rounded transition"
                    >
                      <FontAwesome icon="trash" class="mr-1" />
                      Remover
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Card: Histórico de Pagamentos -->
          <div class="bg-white rounded-xl shadow-md p-6">
            <div class="flex items-center justify-between mb-4">
              <h2 class="text-xl font-bold text-gray-800 flex items-center gap-2">
                <FontAwesome icon="credit-card" class="text-violet-500" />
                Pagamentos ({{ venda.baixas?.length || 0 }})
              </h2>
              <button
                @click="mostrarModalPagamento = true"
                class="text-sm px-3 py-1 bg-green-100 text-green-700 rounded-lg hover:bg-green-200 transition"
              >
                <FontAwesome icon="plus" class="mr-1" />
                Registrar Pagamento
              </button>
            </div>

            <div v-if="venda.baixas && venda.baixas.length > 0" class="space-y-3">
              <div
                v-for="baixa in venda.baixas"
                :key="baixa.id"
                class="border border-gray-200 rounded-lg p-4"
              >
                <div class="flex items-start justify-between">
                  <div class="flex-1">
                    <div class="flex items-center gap-2 mb-2">
                      <FontAwesome
                        :icon="getIconePagamento(baixa.formaPagamentoTipo)"
                        class="text-green-500"
                      />
                      <span class="font-semibold text-gray-800">{{ baixa.formaPagamentoNome }}</span>
                    </div>

                    <p class="text-sm text-gray-600">
                      {{ formatarDataHora(baixa.dataBaixa) }}
                    </p>

                    <div v-if="baixa.numeroParcelas > 1" class="text-sm text-blue-600 mt-1">
                      <FontAwesome icon="calendar-alt" class="mr-1" />
                      {{ baixa.numeroParcelas }}x de {{ formatarMoeda(baixa.valorParcela) }}
                    </div>

                    <p v-if="baixa.observacoes" class="text-xs text-gray-500 mt-2">
                      {{ baixa.observacoes }}
                    </p>
                  </div>

                  <div class="text-right">
                    <p class="text-lg font-bold text-green-600">
                      {{ formatarMoeda(baixa.valorBaixa) }}
                    </p>
                    <p v-if="baixa.valorTaxa > 0" class="text-xs text-red-600">
                      Taxa: -{{ formatarMoeda(baixa.valorTaxa) }}
                    </p>
                    <p v-if="baixa.valorTaxa > 0" class="text-xs text-gray-600">
                      Líquido: {{ formatarMoeda(baixa.valorLiquido) }}
                    </p>
                    <button
                      @click="removerBaixa(baixa.id)"
                      class="mt-2 text-xs text-red-600 hover:text-red-700 transition"
                    >
                      <FontAwesome icon="trash" />
                      Remover
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div v-else class="text-center py-8 text-gray-500">
              <FontAwesome icon="credit-card" class="text-3xl mb-2" />
              <p>Nenhum pagamento registrado</p>
            </div>
          </div>
        </div>

        <!-- Coluna Direita - Resumo Financeiro -->
        <div class="space-y-6">

          <!-- Card: Resumo Total -->
          <div class="bg-gradient-to-br from-violet-500 to-purple-600 rounded-xl shadow-lg p-6 text-white sticky top-6">
            <h3 class="text-lg font-semibold mb-4 flex items-center gap-2">
              <FontAwesome icon="calculator" />
              Resumo Financeiro
            </h3>

            <div class="space-y-3">
              <div class="flex justify-between items-center pb-2 border-b border-white border-opacity-20">
                <span class="text-violet-100">Valor Bruto</span>
                <span class="font-semibold">{{ formatarMoeda(venda.valorBruto) }}</span>
              </div>

              <div v-if="venda.desconto > 0" class="flex justify-between items-center pb-2 border-b border-white border-opacity-20">
                <span class="text-violet-100">Desconto</span>
                <span class="font-semibold text-green-300">-{{ formatarMoeda(venda.desconto) }}</span>
              </div>

              <div class="flex justify-between items-center pb-3 border-b border-white border-opacity-30 text-lg">
                <span class="font-bold">TOTAL</span>
                <span class="font-bold text-2xl">{{ formatarMoeda(venda.valorTotal) }}</span>
              </div>

              <div class="flex justify-between items-center pb-2 border-b border-white border-opacity-20">
                <span class="text-green-200">Pago</span>
                <span class="font-semibold text-green-300">{{ formatarMoeda(venda.valorPago) }}</span>
              </div>

              <div class="flex justify-between items-center text-lg">
                <span class="font-bold">Pendente</span>
                <span :class="[
                  'font-bold text-2xl',
                  venda.valorPendente > 0 ? 'text-yellow-300' : 'text-green-300'
                ]">
                  {{ formatarMoeda(venda.valorPendente) }}
                </span>
              </div>
            </div>

            <!-- Barra de progresso -->
            <div v-if="venda.valorTotal > 0" class="mt-6">
              <div class="flex justify-between text-sm mb-2">
                <span>Progresso</span>
                <span>{{ venda.percentualPago.toFixed(1) }}%</span>
              </div>
              <div class="h-3 bg-white bg-opacity-20 rounded-full overflow-hidden">
                <div
                  class="h-full bg-gradient-to-r from-green-300 to-emerald-400 transition-all duration-500"
                  :style="{ width: `${venda.percentualPago}%` }"
                ></div>
              </div>
            </div>

            <!-- Ações -->
            <div class="mt-6 space-y-2">
              <button
                v-if="venda.statusVenda !== 'pago'"
                @click="mostrarModalPagamento = true"
                class="w-full py-3 bg-white text-violet-600 font-semibold rounded-lg hover:bg-violet-50 transition shadow-lg"
              >
                <FontAwesome icon="credit-card" class="mr-2" />
                Registrar Pagamento
              </button>

              <button
                v-if="venda.statusVenda !== 'cancelado'"
                @click="cancelarVenda"
                :disabled="venda.valorPago > 0"
                :class="[
                  'w-full py-2 font-semibold rounded-lg transition mb-2',
                  venda.valorPago > 0
                    ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                    : 'bg-red-500 bg-opacity-20 text-white hover:bg-opacity-30'
                ]"
                :title="venda.valorPago > 0 ? 'Não é possível cancelar vendas com pagamentos. Remova os pagamentos primeiro.' : ''"
              >
                <FontAwesome icon="times-circle" class="mr-2" />
                Cancelar Venda
              </button>
              <div v-if="venda.valorPago > 0 && venda.statusVenda !== 'cancelado'" class="flex items-center justify-center gap-2 text-xs text-amber-700 bg-amber-50 border border-amber-200 rounded-lg py-2 px-3 mb-2">
                <FontAwesome icon="exclamation-triangle" class="text-amber-600" />
                <span class="font-medium">Remova os pagamentos para cancelar</span>
              </div>

              <button
                @click="confirmarExclusao"
                class="w-full py-2 bg-red-700 text-white font-semibold rounded-lg hover:bg-red-800 transition"
              >
                <FontAwesome icon="trash" class="mr-2" />
                Excluir Permanentemente
              </button>
            </div>
          </div>

          <!-- Card: Informações Adicionais -->
          <div class="bg-white rounded-xl shadow-md p-6">
            <h3 class="text-lg font-semibold mb-4 text-gray-800">Informações</h3>

            <div class="space-y-3 text-sm">
              <div class="flex justify-between">
                <span class="text-gray-600">Total de itens</span>
                <span class="font-semibold">{{ venda.quantidadeItens }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">Pagamentos</span>
                <span class="font-semibold">{{ venda.quantidadeBaixas }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-600">Data da venda</span>
                <span class="font-semibold">{{ formatarData(venda.dataVenda) }}</span>
              </div>
              <div v-if="venda.canceladoEm" class="flex justify-between text-red-600">
                <span>Cancelado em</span>
                <span class="font-semibold">{{ formatarData(venda.canceladoEm) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal: Registrar Pagamento -->
    <BaseModal v-model="mostrarModalPagamento" title="Registrar Pagamento" size="lg">
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Forma de Pagamento</label>
          <select
            v-model="formPagamento.formaPagamentoId"
            @change="atualizarParcelasPagamento"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500"
          >
            <option value="">Selecione...</option>
            <option v-for="forma in formasPagamento" :key="forma.id" :value="forma.id">
              {{ forma.nome }}
              <template v-if="forma.taxaPercentual > 0">
                (Taxa: {{ forma.taxaPercentual }}%)
              </template>
            </option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Valor do Pagamento</label>
          <div class="flex gap-2">
            <input
              v-model.number="formPagamento.valorBaixa"
              type="number"
              step="0.01"
              class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500"
              placeholder="0.00"
            />
            <button
              @click="preencherValorTotal"
              type="button"
              class="px-4 py-2 bg-green-500 text-white font-medium rounded-lg hover:bg-green-600 transition flex items-center gap-2 whitespace-nowrap"
              title="Preencher com o valor total pendente"
            >
              <FontAwesome icon="check-circle" />
              Pagar Total
            </button>
          </div>
          <p class="text-sm text-gray-500 mt-1">
            Pendente: {{ formatarMoeda(venda?.valorPendente || 0) }}
          </p>
        </div>

        <div v-if="formaPagamentoSelecionada?.parcelasMax > 1">
          <label class="block text-sm font-medium text-gray-700 mb-2">Parcelas</label>
          <select
            v-model.number="formPagamento.numeroParcelas"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500"
          >
            <option v-for="n in formaPagamentoSelecionada.parcelasMax" :key="n" :value="n">
              {{ n }}x de {{ formatarMoeda((formPagamento.valorBaixa || 0) / n) }}
            </option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Observações</label>
          <textarea
            v-model="formPagamento.observacoes"
            rows="3"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500"
            placeholder="Observações sobre o pagamento (opcional)"
          ></textarea>
        </div>

        <div class="flex gap-3 pt-4 border-t">
          <button
            @click="mostrarModalPagamento = false"
            class="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition"
          >
            Cancelar
          </button>
          <button
            @click="registrarPagamento"
            :disabled="!podeSalvarPagamento"
            class="flex-1 px-4 py-2 bg-violet-600 text-white rounded-lg hover:bg-violet-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <FontAwesome icon="check" class="mr-2" />
            Confirmar Pagamento
          </button>
        </div>
      </div>
    </BaseModal>

    <!-- Modal: Adicionar Item -->
    <BaseModal
      :model-value="mostrarModalAdicionarItem"
      @update:model-value="mostrarModalAdicionarItem = $event"
      title="Adicionar Novo Item à Venda"
      size="lg"
    >
      <div class="space-y-4">
        <p class="text-sm text-gray-600">
          Selecione um animal e um serviço do catálogo para adicionar à venda
        </p>

        <!-- Seleção de Animal -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Animal *
          </label>
          <select
            v-model.number="formNovoItem.animalId"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500"
          >
            <option value="">Selecione um animal...</option>
            <option
              v-for="animal in animaisCliente"
              :key="animal.id"
              :value="animal.id"
            >
              {{ animal.nome }} {{ animal.raca ? `- ${animal.raca}` : '' }} {{ animal.peso ? `(${animal.peso}kg)` : '' }}
            </option>
          </select>
        </div>

        <!-- Seleção de Serviço -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Serviço do Catálogo *
          </label>
          <select
            v-model.number="formNovoItem.servicoId"
            @change="preencherValorServico"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500"
          >
            <option value="">Selecione um serviço...</option>
            <option
              v-for="servico in servicosCatalogo"
              :key="servico.id"
              :value="servico.id"
            >
              {{ servico.nome }} - R$ {{ (servico.valor || 0).toFixed(2) }}
              {{ servico.quantidade > 1 ? `(Pacote ${servico.quantidade}x)` : '(Avulso)' }}
            </option>
          </select>
        </div>

        <!-- Detalhes do Serviço Selecionado -->
        <div v-if="formNovoItem.servicoId && servicoSelecionado" class="bg-violet-50 border border-violet-200 rounded-lg p-4">
          <h3 class="font-semibold text-gray-700 mb-2">Detalhes do Serviço</h3>
          <div class="space-y-1 text-sm">
            <p><strong>Nome:</strong> {{ servicoSelecionado.nome }}</p>
            <p><strong>Quantidade:</strong> {{ servicoSelecionado.quantidade }} banho(s)</p>
            <p><strong>Valor:</strong> R$ {{ (servicoSelecionado.valor || 0).toFixed(2).replace('.', ',') }}</p>
            <p v-if="servicoSelecionado.descricao" class="text-gray-600">
              <strong>Descrição:</strong> {{ servicoSelecionado.descricao }}
            </p>
          </div>
        </div>

        <!-- Valor Customizado -->
        <div v-if="formNovoItem.servicoId">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Valor Customizado (opcional)
          </label>
          <input
            v-model.number="formNovoItem.valorItem"
            type="number"
            step="0.01"
            min="0"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500"
            placeholder="Deixe vazio para usar o valor padrão"
          />
          <p class="text-xs text-gray-500 mt-1">
            Valor padrão: R$ {{ (servicoSelecionado?.valor || 0).toFixed(2).replace('.', ',') }}
          </p>
        </div>

        <!-- Desconto no Item -->
        <div v-if="formNovoItem.servicoId">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Desconto no Item (opcional)
          </label>
          <input
            v-model.number="formNovoItem.descontoItem"
            type="number"
            step="0.01"
            min="0"
            :max="formNovoItem.valorItem || servicoSelecionado?.valor || 0"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500"
            placeholder="0.00"
          />
        </div>

        <!-- Botões -->
        <div class="flex gap-3 pt-4 border-t">
          <button
            @click="fecharModalNovoItem"
            class="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition"
          >
            Cancelar
          </button>
          <button
            @click="adicionarNovoItem"
            :disabled="!formNovoItem.animalId || !formNovoItem.servicoId"
            class="flex-1 px-4 py-2 bg-violet-600 text-white rounded-lg hover:bg-violet-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <FontAwesome icon="plus" class="mr-2" />
            Adicionar Item
          </button>
        </div>
      </div>
    </BaseModal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { vendasService, formasPagamentoService } from '@/services/api'
import { FontAwesomeIcon as FontAwesome } from '@fortawesome/vue-fontawesome'
import { formatarValor } from '@/utils/formatters'
import BaseModal from '@/components/UI/BaseModal.vue'

const route = useRoute()
const router = useRouter()

const venda = ref<any>(null)
const loading = ref(false)
const error = ref<string | null>(null)

const formasPagamento = ref<any[]>([])
const mostrarModalPagamento = ref(false)
const mostrarModalAdicionarItem = ref(false)
const animaisCliente = ref<any[]>([])
const servicosCatalogo = ref<any[]>([])

const formPagamento = ref({
  formaPagamentoId: '',
  valorBaixa: 0,
  numeroParcelas: 1,
  observacoes: '',
  usuarioId: 1 // TODO: pegar do contexto de autenticação
})

const formNovoItem = ref({
  animalId: null as number | null,
  servicoId: null as number | null,
  valorItem: null as number | null,
  descontoItem: 0
})

const formaPagamentoSelecionada = computed(() => {
  return formasPagamento.value.find(f => f.id === formPagamento.value.formaPagamentoId)
})

const servicoSelecionado = computed(() => {
  if (!formNovoItem.value.servicoId) return null
  return servicosCatalogo.value.find(s => s.id === formNovoItem.value.servicoId)
})

const podeSalvarPagamento = computed(() => {
  return formPagamento.value.formaPagamentoId && formPagamento.value.valorBaixa > 0
})

const carregarVenda = async () => {
  try {
    loading.value = true
    error.value = null
    const id = Number(route.params.id)
    venda.value = await vendasService.buscarPorId(id)
  } catch (err: any) {
    error.value = err.message || 'Erro ao carregar venda'
    console.error('Erro ao carregar venda:', err)
  } finally {
    loading.value = false
  }
}

const carregarFormasPagamento = async () => {
  try {
    formasPagamento.value = await formasPagamentoService.buscarAtivas()
  } catch (err) {
    console.error('Erro ao carregar formas de pagamento:', err)
  }
}

const preencherValorTotal = () => {
  if (venda.value?.valorPendente) {
    formPagamento.value.valorBaixa = venda.value.valorPendente
  }
}

const atualizarParcelasPagamento = () => {
  formPagamento.value.numeroParcelas = 1
}

const registrarPagamento = async () => {
  try {
    const dados = {
      vendaId: venda.value.id,
      ...formPagamento.value,
      formaPagamentoId: Number(formPagamento.value.formaPagamentoId)
    }

    const vendaAtualizada = await vendasService.registrarBaixa(dados)
    venda.value = vendaAtualizada
    mostrarModalPagamento.value = false

    // Limpar formulário
    formPagamento.value = {
      formaPagamentoId: '',
      valorBaixa: 0,
      numeroParcelas: 1,
      observacoes: '',
      usuarioId: 1
    }
  } catch (err: any) {
    alert('Erro ao registrar pagamento: ' + (err.message || 'Erro desconhecido'))
  }
}

const removerBaixa = async (baixaId: number) => {
  if (!confirm('Deseja realmente remover este pagamento?')) return

  try {
    console.log('🗑️ Removendo baixa:', baixaId)
    console.log('📋 Baixas antes:', venda.value.baixas.length)

    const vendaAtualizada = await vendasService.removerBaixa(venda.value.id, baixaId)

    console.log('✅ Venda atualizada recebida do backend:', vendaAtualizada)
    console.log('📋 Baixas na resposta:', vendaAtualizada.baixas?.length || 0)

    // Limpar referência antiga completamente
    venda.value = null

    // Aguardar próximo tick do Vue
    await new Promise(resolve => setTimeout(resolve, 0))

    // Atribuir nova venda
    venda.value = vendaAtualizada

    console.log('📦 Venda atualizada no Vue:', venda.value.baixas?.length || 0, 'baixas')

    alert('✅ Pagamento removido com sucesso!')
  } catch (err: any) {
    console.error('❌ Erro ao remover baixa:', err)
    alert('Erro ao remover pagamento: ' + (err.message || 'Erro desconhecido'))
  }
}

const removerItem = async (itemId: number) => {
  console.log('🗑️ removerItem chamado com ID:', itemId)

  // 🔍 Verificar se é o último item ANTES de remover
  const isUltimoItem = venda.value.itens?.length === 1

  let mensagemConfirmacao = '⚠️ Deseja realmente remover este item da venda?\n\n'

  if (isUltimoItem) {
    // ⚠️ AVISO ESPECIAL: É o último item - a venda será excluída!
    mensagemConfirmacao =
      '🚨 ATENÇÃO: Este é o último item da venda!\n\n' +
      '⚠️ Ao remover este item, a VENDA INTEIRA será EXCLUÍDA automaticamente.\n\n' +
      '📋 O que será removido:\n' +
      `   • Venda #${venda.value.codigoVenda}\n` +
      `   • ${venda.value.quantidadeBaixas || 0} pagamento(s) registrado(s)\n` +
      `   • Todos os dados relacionados\n\n` +
      '❓ Tem certeza que deseja continuar?'
  } else {
    // Mensagem normal para remoção de item
    const item = venda.value.itens?.find((i: any) => i.id === itemId)
    mensagemConfirmacao =
      '⚠️ Deseja realmente remover este item da venda?\n\n' +
      `📦 Item: ${item?.servicoNome || 'Item'}\n` +
      `🐕 Animal: ${item?.animalNome || '-'}\n` +
      `💰 Valor: R$ ${(item?.valorFinalItem || 0).toFixed(2)}\n\n` +
      'Esta ação não pode ser desfeita.'
  }

  if (!confirm(mensagemConfirmacao)) {
    console.log('❌ Usuário cancelou a remoção')
    return
  }

  try {
    console.log('📤 Chamando API para remover item...')
    console.log('📊 Estado antes:', { vendaId: venda.value.id, itemId, quantidadeItens: venda.value.itens?.length })

    const vendaAtualizada = await vendasService.removerItem(venda.value.id, itemId)

    console.log('📦 Resposta da API:', vendaAtualizada)
    console.log('🔍 vendaAtualizada === null?', vendaAtualizada === null)
    console.log('🔍 vendaAtualizada === undefined?', vendaAtualizada === undefined)
    console.log('🔍 vendaAtualizada === ""?', vendaAtualizada === '')
    console.log('🔍 tipo:', typeof vendaAtualizada)

    // 🔍 Verificar se a venda foi excluída (retornou null/undefined/string vazia porque ficou sem itens)
    if (vendaAtualizada === null || vendaAtualizada === undefined || vendaAtualizada === '') {
      console.log('⚠️  Venda foi excluída automaticamente (ficou sem itens)')
      console.log('🔄 Redirecionando para /vendas...')

      alert(
        '✅ Operação concluída com sucesso!\n\n' +
        '📋 A venda foi excluída porque ficou sem itens.\n\n' +
        'Você será redirecionado para a lista de vendas.'
      )

      router.push('/vendas')
      console.log('✅ Redirecionamento executado!')
      return
    }

    console.log('✅ Item removido, venda ainda existe')
    venda.value = vendaAtualizada
    alert('✅ Item removido com sucesso!')
  } catch (err: any) {
    console.error('❌ Erro ao remover item:', err)
    alert('Erro ao remover item: ' + (err.message || 'Erro desconhecido'))
  }
}

const carregarAnimaisCliente = async () => {
  console.log('🔍 carregarAnimaisCliente chamado')
  console.log('📦 venda.value:', venda.value)
  console.log('🆔 clienteId:', venda.value?.clienteId)

  if (!venda.value?.clienteId) {
    console.warn('⚠️ Cliente ID não encontrado!')
    return
  }

  try {
    const url = `http://localhost:8080/animal/cliente/${venda.value.clienteId}`
    console.log('📞 Chamando API:', url)
    const response = await fetch(url)
    console.log('📡 Response status:', response.status)
    const dados = await response.json()
    console.log('📦 Dados recebidos:', dados)
    animaisCliente.value = dados
    console.log('🐾 Animais carregados:', animaisCliente.value.length)
  } catch (err) {
    console.error('❌ Erro ao carregar animais:', err)
    animaisCliente.value = []
  }
}

const carregarServicosCatalogo = async () => {
  try {
    const response = await fetch('http://localhost:8080/servico')
    servicosCatalogo.value = await response.json()
    console.log('📋 Serviços do catálogo:', servicosCatalogo.value.length)
  } catch (err) {
    console.error('❌ Erro ao carregar serviços:', err)
    servicosCatalogo.value = []
  }
}

const abrirModalAdicionarItem = async () => {
  console.log('🎯 abrirModalAdicionarItem chamado')
  console.log('📊 Estado da venda:', {
    id: venda.value?.id,
    clienteId: venda.value?.clienteId,
    clienteNome: venda.value?.clienteNome
  })

  await carregarAnimaisCliente()
  await carregarServicosCatalogo()

  console.log('📊 Após carregar:')
  console.log('  - Animais:', animaisCliente.value.length)
  console.log('  - Serviços:', servicosCatalogo.value.length)

  mostrarModalAdicionarItem.value = true
}

const preencherValorServico = () => {
  const servico = servicoSelecionado.value
  if (servico) {
    formNovoItem.value.valorItem = servico.valor || 0
  }
}

const fecharModalNovoItem = () => {
  mostrarModalAdicionarItem.value = false
  formNovoItem.value = {
    animalId: null,
    servicoId: null,
    valorItem: null,
    descontoItem: 0
  }
}

const adicionarNovoItem = async () => {
  if (!formNovoItem.value.animalId || !formNovoItem.value.servicoId) return

  try {
    // Criar objeto no formato esperado pela API
    const novoItem = {
      animalId: formNovoItem.value.animalId,
      servicoId: formNovoItem.value.servicoId,
      valorItem: formNovoItem.value.valorItem || undefined,
      descontoItem: formNovoItem.value.descontoItem || undefined
    }

    const vendaAtualizada = await vendasService.adicionarItemNovo(venda.value.id, novoItem)

    venda.value = vendaAtualizada
    fecharModalNovoItem()
    alert('✅ Item adicionado com sucesso!')
  } catch (err: any) {
    console.error('❌ Erro ao adicionar item:', err)
    alert('Erro ao adicionar item: ' + (err.message || 'Erro desconhecido'))
  }
}

const cancelarVenda = async () => {
  // Validação: não pode cancelar venda com pagamentos
  if (venda.value.valorPago > 0) {
    alert(
      '⚠️ Não é possível cancelar uma venda com pagamentos registrados.\n\n' +
      `Valor pago: R$ ${formatarMoeda(venda.value.valorPago)}\n\n` +
      'Para cancelar esta venda, primeiro remova todos os pagamentos.'
    )
    return
  }

  const motivo = prompt('Digite o motivo do cancelamento:')
  if (!motivo) return

  try {
    const vendaAtualizada = await vendasService.cancelar(venda.value.id, motivo)
    venda.value = vendaAtualizada
    alert('✅ Venda cancelada com sucesso!')
  } catch (err: any) {
    alert('❌ Erro ao cancelar venda: ' + (err.message || 'Erro desconhecido'))
  }
}

const confirmarExclusao = async () => {
  const confirmacao = confirm(
    `⚠️ ATENÇÃO: Você está prestes a EXCLUIR PERMANENTEMENTE a venda #${venda.value.codigoVenda}!\n\n` +
    `Esta ação irá:\n` +
    `- Remover a venda do sistema\n` +
    `- Desvincular todos os pacotes/banhos desta venda\n` +
    `- Excluir todos os registros de pagamento\n\n` +
    `Esta operação NÃO PODE SER DESFEITA!\n\n` +
    `Tem certeza que deseja continuar?`
  )

  if (!confirmacao) return

  const segundaConfirmacao = confirm(
    `⚠️ ÚLTIMA CONFIRMAÇÃO!\n\n` +
    `Digite OK para confirmar a exclusão permanente da venda #${venda.value.codigoVenda}`
  )

  if (!segundaConfirmacao) return

  try {
    await vendasService.excluir(venda.value.id)
    alert('✅ Venda excluída com sucesso!')
    router.push('/vendas')
  } catch (err: any) {
    alert('❌ Erro ao excluir venda: ' + (err.message || 'Erro desconhecido'))
  }
}

const formatarMoeda = (valor: number) => formatarValor(valor)

const formatarData = (data: string) => {
  const d = new Date(data)
  return d.toLocaleDateString('pt-BR')
}

const formatarDataCompleta = (data: string) => {
  const d = new Date(data)
  return d.toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatarDataHora = (data: string) => {
  const d = new Date(data)
  return d.toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getStatusBadgeClass = (status: string) => {
  const classes: Record<string, string> = {
    em_aberto: 'bg-orange-100 text-orange-700',
    parcial: 'bg-yellow-100 text-yellow-700',
    pago: 'bg-green-100 text-green-700',
    cancelado: 'bg-red-100 text-red-700'
  }
  return classes[status] || 'bg-gray-100 text-gray-700'
}

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    em_aberto: 'Em Aberto',
    parcial: 'Parcial',
    pago: 'Pago',
    cancelado: 'Cancelado'
  }
  return labels[status] || status
}

const getTipoVendaLabel = (tipo: string) => {
  const labels: Record<string, string> = {
    presencial: 'Presencial',
    agendamento: 'Agendamento',
    busca_entrega: 'Busca e Entrega'
  }
  return labels[tipo] || tipo
}

const getIconePagamento = (tipo: string) => {
  const icones: Record<string, string> = {
    dinheiro: 'money-bill-wave',
    debito: 'credit-card',
    credito: 'credit-card',
    pix: 'qrcode',
    boleto: 'barcode',
    outro: 'wallet'
  }
  return icones[tipo] || 'wallet'
}

const navegarParaAnimalServico = (animalServicoId: number) => {
  router.push(`/animal-servico/${animalServicoId}`)
}

onMounted(() => {
  carregarVenda()
  carregarFormasPagamento()
})
</script>

<style scoped>
.bg-pattern {
  background-image: radial-gradient(circle, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 20px 20px;
}
</style>

