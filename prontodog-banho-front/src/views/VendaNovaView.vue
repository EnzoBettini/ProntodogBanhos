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
          <div v-else class="space-y-4">
            <div
              v-for="animal in animaisCliente"
              :key="animal.id"
              class="border-2 border-gray-200 rounded-xl p-5 hover:border-violet-300 transition"
            >
              <!-- Header do Animal -->
              <div class="flex items-center justify-between mb-4">
                <div class="flex items-center gap-3">
                  <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white font-bold shadow-lg">
                    {{ animal.nome.charAt(0).toUpperCase() }}
                  </div>
                  <div>
                    <h3 class="font-bold text-lg text-gray-800">{{ animal.nome }}</h3>
                    <p class="text-sm text-gray-500">
                      {{ animal.tipo || 'Animal' }}
                      {{ animal.raca ? `• ${animal.raca}` : '' }}
                      {{ animal.peso ? `• ${animal.peso}kg` : '' }}
                    </p>
                  </div>
                </div>
                <button
                  @click="abrirModalAdicionarServico(animal)"
                  class="px-4 py-2 bg-gradient-to-r from-violet-500 to-purple-600 text-white rounded-lg hover:from-violet-600 hover:to-purple-700 transition shadow-lg text-sm font-medium"
                >
                  <FontAwesome icon="plus" class="mr-2" />
                  Adicionar Serviço
                </button>
              </div>

              <!-- Serviços Existentes do Animal -->
              <div v-if="getServicosExistentesAnimal(animal.id).length > 0" class="mb-3">
                <p class="text-xs font-semibold text-gray-500 uppercase mb-2">Serviços Já Cadastrados:</p>
                <div class="space-y-2">
                  <div
                    v-for="servico in getServicosExistentesAnimal(animal.id)"
                    :key="servico.id"
                    class="bg-blue-50 border border-blue-200 rounded-lg p-3 flex items-center justify-between cursor-pointer hover:bg-blue-100 transition"
                    :class="{ 'ring-2 ring-violet-400 bg-violet-50 border-violet-300': servicosExistentesSelecionados.includes(servico.id) }"
                    @click="toggleServicoExistente(servico.id)"
                  >
                    <div class="flex items-center gap-3 flex-1">
                      <input
                        type="checkbox"
                        :checked="servicosExistentesSelecionados.includes(servico.id)"
                        @click.stop
                        class="w-5 h-5 text-violet-600 rounded focus:ring-violet-500"
                      />
                      <div>
                        <p class="font-semibold text-gray-800">{{ servico.servico?.nome || 'Serviço' }}</p>
                        <p class="text-xs text-gray-500">
                          {{ servico.servico?.quantidade }} banho(s) • Data: {{ formatarData(servico.dataServico) }}
                        </p>
                      </div>
                    </div>
                    <div class="text-right">
                      <p class="font-bold text-gray-800">{{ formatarMoeda(servico.servico?.valor || 0) }}</p>
                      <span
                        :class="{
                          'text-xs px-2 py-1 rounded-full font-semibold': true,
                          'bg-green-100 text-green-700': servico.statusPagamento === 'pago',
                          'bg-yellow-100 text-yellow-700': servico.statusPagamento === 'em_aberto'
                        }"
                      >
                        {{ servico.statusPagamento === 'pago' ? 'Pago' : 'Em Aberto' }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Novos Serviços Adicionados -->
              <div v-if="getServicosNovosAnimal(animal.id).length > 0" class="mt-3">
                <p class="text-xs font-semibold text-gray-500 uppercase mb-2">Novos Serviços a Adicionar:</p>
                <div class="space-y-2">
                  <div
                    v-for="(item, index) in getServicosNovosAnimal(animal.id)"
                    :key="`novo-${index}`"
                    class="bg-green-50 border border-green-200 rounded-lg p-3 flex items-center justify-between"
                  >
                    <div class="flex-1">
                      <p class="font-semibold text-gray-800">{{ item.servicoNome }}</p>
                      <p class="text-xs text-gray-500">{{ item.servicoQuantidade }} banho(s)</p>
                    </div>
                    <div class="flex items-center gap-3">
                      <p class="font-bold text-gray-800">{{ formatarMoeda(item.valorItem) }}</p>
                      <button
                        @click="removerNovoServico(animal.id, index)"
                        class="text-red-500 hover:text-red-700 transition"
                      >
                        <FontAwesome icon="trash" />
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Mensagem se não há serviços -->
              <div v-if="getServicosExistentesAnimal(animal.id).length === 0 && getServicosNovosAnimal(animal.id).length === 0" class="text-center py-4 text-gray-400 text-sm">
                <FontAwesome icon="box-open" class="text-2xl mb-1" />
                <p>Nenhum serviço adicionado ainda</p>
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
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
      @click.self="fecharModal"
    >
      <div class="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div class="sticky top-0 bg-gradient-to-r from-violet-600 to-purple-600 text-white p-6 rounded-t-xl">
          <div class="flex items-center justify-between">
            <div>
              <h2 class="text-2xl font-bold">Adicionar Serviço</h2>
              <p class="text-violet-100 mt-1">{{ animalSelecionado?.nome }}</p>
            </div>
            <button
              @click="fecharModal"
              class="text-white hover:text-violet-200 transition"
            >
              <FontAwesome icon="times" class="text-2xl" />
            </button>
          </div>
        </div>

        <div class="p-6 space-y-4">
          <!-- Seleção de Serviço com Busca -->
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Selecione o Serviço *
            </label>

            <!-- Campo de busca -->
            <div class="relative mb-3">
              <input
                v-model="buscaServico"
                type="text"
                placeholder="Busque por nome do serviço..."
                class="w-full px-4 py-3 pl-12 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500 focus:border-transparent transition"
                @focus="mostrarListaServicos = true"
              />
              <FontAwesome
                icon="search"
                class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400"
              />
              <div v-if="buscaServico" class="absolute right-4 top-1/2 -translate-y-1/2">
                <button
                  @click="limparBuscaServico"
                  class="text-gray-400 hover:text-gray-600 transition"
                >
                  <FontAwesome icon="times" />
                </button>
              </div>
            </div>

            <!-- Serviço selecionado -->
            <div v-if="servicoSelecionadoNoModal" class="mb-3 p-4 bg-violet-50 border-2 border-violet-200 rounded-lg">
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 bg-violet-500 rounded-full flex items-center justify-center text-white">
                    <FontAwesome icon="cut" class="text-lg" />
                  </div>
                  <div>
                    <p class="font-bold text-gray-800">{{ servicoSelecionadoNoModal.nome }}</p>
                    <p class="text-sm text-gray-600">
                      {{ formatarMoeda(servicoSelecionadoNoModal.valor || 0) }}
                      <span v-if="servicoSelecionadoNoModal.quantidade > 1" class="ml-2">
                        • Pacote {{ servicoSelecionadoNoModal.quantidade }}x
                      </span>
                      <span v-else class="ml-2">• Avulso</span>
                    </p>
                  </div>
                </div>
                <button
                  @click="desselecionarServico"
                  class="text-gray-400 hover:text-red-500 transition"
                  title="Remover seleção"
                >
                  <FontAwesome icon="times-circle" class="text-xl" />
                </button>
              </div>
            </div>

            <!-- Lista de serviços filtrados -->
            <div
              v-if="mostrarListaServicos && !servicoSelecionadoNoModal && servicosFiltrados.length > 0"
              class="max-h-60 overflow-y-auto border border-gray-300 rounded-lg shadow-lg bg-white"
            >
              <button
                v-for="servico in servicosFiltrados"
                :key="servico.id"
                @click="selecionarServico(servico)"
                class="w-full px-4 py-3 text-left hover:bg-violet-50 transition border-b border-gray-100 last:border-0"
              >
                <div class="flex items-center gap-3">
                  <div class="w-8 h-8 bg-violet-100 rounded-full flex items-center justify-center text-violet-600">
                    <FontAwesome icon="cut" class="text-sm" />
                  </div>
                  <div class="flex-1">
                    <p class="font-semibold text-gray-800">{{ servico.nome }}</p>
                    <p class="text-xs text-gray-500">
                      {{ formatarMoeda(servico.valor || 0) }}
                      <span v-if="servico.quantidade > 1"> • Pacote {{ servico.quantidade }}x</span>
                      <span v-else> • Avulso</span>
                    </p>
                  </div>
                </div>
              </button>
            </div>

            <!-- Mensagem quando não há resultados -->
            <div
              v-if="mostrarListaServicos && !servicoSelecionadoNoModal && buscaServico && servicosFiltrados.length === 0"
              class="p-4 bg-gray-50 border border-gray-300 rounded-lg text-center text-gray-600"
            >
              <FontAwesome icon="search" class="text-2xl mb-2 text-gray-400" />
              <p class="text-sm">Nenhum serviço encontrado com <strong>"{{ buscaServico }}"</strong></p>
            </div>
          </div>

          <!-- Detalhes do Serviço Selecionado -->
          <div v-if="novoServico.servicoId" class="bg-violet-50 border border-violet-200 rounded-lg p-4">
            <h3 class="font-semibold text-gray-700 mb-2">Detalhes do Serviço</h3>
            <div class="space-y-1 text-sm">
              <p><strong>Nome:</strong> {{ novoServico.servicoNome }}</p>
              <p><strong>Quantidade:</strong> {{ novoServico.servicoQuantidade }} banho(s)</p>
              <p><strong>Valor:</strong> {{ formatarMoeda(novoServico.valorItem) }}</p>
              <p v-if="servicoSelecionadoDetalhes?.descricao" class="text-gray-600">
                <strong>Descrição:</strong> {{ servicoSelecionadoDetalhes.descricao }}
              </p>
            </div>
          </div>

          <!-- Valor Customizado (opcional) -->
          <div v-if="novoServico.servicoId">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Valor Customizado (opcional)
            </label>
            <input
              v-model.number="novoServico.valorItem"
              type="number"
              step="0.01"
              min="0"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500"
              placeholder="Deixe vazio para usar o valor padrão"
            />
            <p class="text-xs text-gray-500 mt-1">
              Valor padrão: {{ formatarMoeda(servicoSelecionadoDetalhes?.valor || 0) }}
            </p>
          </div>

          <!-- Desconto no Item (opcional) -->
          <div v-if="novoServico.servicoId">
            <label class="block text-sm font-medium text-gray-700 mb-2">
              Desconto no Item (opcional)
            </label>
            <input
              v-model.number="novoServico.descontoItem"
              type="number"
              step="0.01"
              min="0"
              :max="novoServico.valorItem"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-violet-500"
              placeholder="0.00"
            />
          </div>

          <!-- Botões -->
          <div class="flex gap-3 pt-4">
            <button
              @click="fecharModal"
              class="flex-1 px-6 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition font-medium"
            >
              Cancelar
            </button>
            <button
              @click="adicionarServico"
              :disabled="!novoServico.servicoId"
              class="flex-1 px-6 py-3 bg-gradient-to-r from-violet-500 to-purple-600 text-white rounded-lg hover:from-violet-600 hover:to-purple-700 transition font-medium disabled:opacity-50"
            >
              <FontAwesome icon="plus" class="mr-2" />
              Adicionar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
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
  descontoItem: 0
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

  // Valor dos novos serviços
  const valorNovos = novosServicos.value
    .reduce((acc, s: NovoServicoItem) => acc + (s.valorItem - s.descontoItem), 0)

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

const abrirModalAdicionarServico = (animal: Animal) => {
  animalSelecionado.value = animal
  novoServico.value = {
    servicoId: '',
    servicoNome: '',
    servicoQuantidade: 0,
    valorItem: 0,
    descontoItem: 0
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

  novosServicos.value.push({
    animalId: animalSelecionado.value.id,
    animalNome: animalSelecionado.value.nome,
    servicoId: Number(novoServico.value.servicoId),
    servicoNome: novoServico.value.servicoNome,
    servicoQuantidade: novoServico.value.servicoQuantidade,
    valorItem: novoServico.value.valorItem,
    descontoItem: novoServico.value.descontoItem || 0
  })

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
      // Novos serviços a criar
      itens: novosServicos.value.map(s => ({
        animalId: s.animalId,
        servicoId: s.servicoId,
        valorItem: s.valorItem,
        descontoItem: s.descontoItem > 0 ? s.descontoItem : undefined
      }))
    }

    console.log('📤 Dados da venda:', dados)

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
  const d = new Date(data)
  return d.toLocaleDateString('pt-BR')
}

const formatarMoeda = (valor: number) => formatarValor(valor)

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
