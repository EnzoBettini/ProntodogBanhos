<template>
  <div class="min-h-screen bg-gradient-to-br from-yellow-50 via-white to-green-50 p-4 pb-20 relative overflow-hidden">
    <!-- 🎨 Background decorativo -->
    <div class="absolute inset-0 overflow-hidden pointer-events-none z-0">
      <!-- Círculos decorativos -->
      <div class="absolute top-20 left-10 w-72 h-72 bg-yellow-200/20 rounded-full blur-3xl"></div>
      <div class="absolute bottom-20 right-10 w-96 h-96 bg-green-200/20 rounded-full blur-3xl"></div>
      <div class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-gradient-to-r from-yellow-100/10 to-green-100/10 rounded-full blur-3xl"></div>
    </div>

    <div class="relative z-20 max-w-4xl mx-auto pt-8">
      <!-- 📋 Header -->
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-yellow-400 to-amber-500 rounded-2xl mb-6 shadow-2xl">
          <FontAwesomeIcon :icon="['fas', 'clipboard-list']" class="text-white text-3xl" />
        </div>
        <h1 class="text-4xl font-bold text-gray-800 mb-3">Cadastrar Animal Serviço</h1>
        <p class="text-gray-600 max-w-2xl mx-auto text-lg">
          Registre um novo serviço para um animal específico. Controle quando o serviço foi contratado e quantos banhos já foram utilizados.
        </p>
      </div>

      <!-- 📝 Formulário -->
      <BaseCard class="backdrop-blur-sm bg-white/80 border-0 shadow-2xl">
        <form @submit.prevent="cadastrarAnimalServico" class="space-y-8">
          <!-- 🐕 Seleção do Animal -->
          <div class="space-y-3">
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <FontAwesomeIcon :icon="['fas', 'dog']" class="mr-2 text-amber-600" />
              Animal *
            </label>
            <SearchSelect
              v-model="formulario.animalId"
              :options="animaisFiltrados"
              :loading="carregandoAnimais"
              :disabled="loading"
              required
              placeholder="Digite o nome do animal, tipo ou código..."
              value-key="id"
              label-key="nome"
              description-key="animalDescription"
              @search="buscarAnimais"
            />
          </div>

          <!-- 💼 Seleção do Serviço -->
          <div class="space-y-3">
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <FontAwesomeIcon :icon="['fas', 'wrench']" class="mr-2 text-green-600" />
              Serviço *
            </label>
            <SearchSelect
              v-model="formulario.servicoId"
              :options="servicosFiltrados"
              :loading="carregandoServicos"
              :disabled="loading"
              required
              placeholder="Digite o nome do serviço..."
              value-key="id"
              label-key="nome"
              description-key="servicoDescription"
              @search="buscarServicos"
            />
          </div>

          <!-- 📅 Data do Serviço -->
          <div class="space-y-3">
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <FontAwesomeIcon :icon="['fas', 'calendar-alt']" class="mr-2 text-blue-600" />
              Data do Serviço *
            </label>
            <input
              v-model="formulario.dataServico"
              type="date"
              :disabled="loading"
              required
              class="w-full px-4 py-4 bg-white border-2 border-gray-200 rounded-xl focus:border-blue-500 focus:ring-4 focus:ring-blue-100 transition-all duration-300 text-lg font-medium hover:border-gray-300 disabled:bg-gray-50 disabled:cursor-not-allowed"
            />
            <p class="text-sm text-gray-500 mt-1">
              Data em que o serviço foi contratado ou realizado
            </p>
          </div>

          <!-- 🛁 Banhos Usados -->
          <div class="space-y-3">
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <FontAwesomeIcon :icon="['fas', 'bath']" class="mr-2 text-cyan-600" />
              Banhos Já Utilizados
            </label>
            <BaseInput
              v-model.number="formulario.banhosUsados"
              type="number"
              min="0"
              :max="maxBanhosPermitidos"
              :disabled="loading || !servicoSelecionado"
              class="text-lg font-medium"
              :class="{
                'border-red-300 bg-red-50': formulario.banhosUsados > maxBanhosPermitidos
              }"
              placeholder="0"
            />
            <div class="text-sm mt-1 space-y-1">
              <p v-if="servicoSelecionado" class="text-gray-600">
                <span class="font-medium">Máximo permitido:</span> {{ maxBanhosPermitidos }} banhos
                <span v-if="servicoSelecionado.quantidade === 1" class="text-amber-600 font-medium ml-2">
                  (Banho Único)
                </span>
                <span v-else class="text-blue-600 font-medium ml-2">
                  (Pacote de {{ servicoSelecionado.quantidade }} banhos)
                </span>
              </p>
              <p v-if="formulario.banhosUsados > maxBanhosPermitidos" class="text-red-600 font-medium">
                ⚠️ Valor não pode ser maior que {{ maxBanhosPermitidos }}
              </p>
              <p v-else class="text-gray-500">
                Quantidade de banhos já utilizados deste serviço (geralmente inicia com 0)
              </p>
            </div>
          </div>

          <!-- 👤 Usuário Responsável -->
          <div class="space-y-3">
            <label class="block text-sm font-semibold text-gray-700 mb-2">
              <FontAwesomeIcon :icon="['fas', 'user']" class="mr-2 text-purple-600" />
              Usuário Responsável *
            </label>
            <SearchSelect
              v-model="formulario.usuarioId"
              :options="usuariosFiltrados"
              :loading="carregandoUsuarios"
              :disabled="loading"
              required
              placeholder="Digite o nome do usuário..."
              value-key="id"
              label-key="nome"
              description-key="usuarioDescription"
              @search="buscarUsuarios"
            />
          </div>

          <!-- 🎯 Resumo (se serviço selecionado) -->
          <div v-if="servicoSelecionado" class="bg-gradient-to-r from-yellow-50 to-green-50 border-2 border-yellow-200 rounded-xl p-6">
            <div class="flex items-center mb-4">
              <FontAwesomeIcon :icon="['fas', 'info-circle']" class="text-amber-600 mr-2" />
              <h3 class="text-lg font-semibold text-gray-800">Resumo do Serviço</h3>
            </div>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
              <div>
                <p class="text-gray-600">Serviço:</p>
                <p class="font-semibold text-gray-800">{{ servicoSelecionado.nome }}</p>
              </div>
              <div>
                <p class="text-gray-600">Valor:</p>
                <p class="font-semibold text-green-600">R$ {{ formatarValor(servicoSelecionado.valor) }}</p>
              </div>
              <div>
                <p class="text-gray-600">Quantidade de Banhos:</p>
                <p class="font-semibold text-blue-600">{{ servicoSelecionado.quantidade }}</p>
              </div>
            </div>
          </div>

          <!-- 🎭 Botões de Ação -->
          <div class="flex flex-col sm:flex-row gap-4 pt-6 border-t border-gray-200">
            <!-- Cancelar -->
            <button
              type="button"
              @click="$router.push('/servicos')"
              :disabled="loading"
              class="inline-flex items-center justify-center font-semibold rounded-xl transition-all duration-300 focus:outline-none focus:ring-4 disabled:opacity-50 disabled:cursor-not-allowed px-8 py-4 text-lg bg-gray-100 text-gray-700 hover:bg-gray-200 focus:ring-gray-300 border-2 border-gray-200 hover:border-gray-300"
            >
              <FontAwesomeIcon :icon="['fas', 'arrow-left']" class="mr-2" />
              Cancelar
            </button>

            <!-- Cadastrar -->
            <BaseButton
              type="submit"
              :disabled="loading || !formularioValido"
              class="flex-1 px-8 py-4 text-lg font-semibold shadow-xl hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-1 bg-gradient-to-r from-amber-500 to-yellow-500 hover:from-amber-600 hover:to-yellow-600"
            >
              <FontAwesomeIcon
                v-if="loading"
                :icon="['fas', 'spinner']"
                class="mr-2 animate-spin"
              />
              <FontAwesomeIcon
                v-else
                :icon="['fas', 'plus-circle']"
                class="mr-2"
              />
              {{ loading ? 'Cadastrando...' : 'Cadastrar Animal Serviço' }}
            </BaseButton>
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
                  <span class="font-bold text-gray-800 ml-2">{{ new Date(animalServicoRegistrado?.dataServico || '').toLocaleDateString('pt-BR') }}</span>
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
import type { Animal, ServicoCompleto, NovoAnimalServico, Usuario } from '@/types/api'

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
  banhosUsados: 0,
  usuarioId: ''
})

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

    const novoAnimalServico: NovoAnimalServico = {
      dataServico: formulario.value.dataServico,
      banhosUsados: formulario.value.banhosUsados,
      animal: { id: Number(formulario.value.animalId) },
      servico: { id: Number(formulario.value.servicoId) },
      usuario: { id: Number(formulario.value.usuarioId) }
    }

    const resultado = await animalServicoService.criar(novoAnimalServico)
    console.log('✅ Animal serviço cadastrado com sucesso!', resultado)

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

  // Limpar formulário
  formulario.value = {
    animalId: '',
    servicoId: '',
    dataServico: new Date().toISOString().split('T')[0] || '',
    banhosUsados: 0,
    usuarioId: ''
  }
}

// 👀 Watchers
// Ajustar banhos automaticamente quando serviço mudar
watch(servicoSelecionado, (novoServico) => {
  if (novoServico && formulario.value.banhosUsados > novoServico.quantidade) {
    console.log(`⚠️ Ajustando banhos de ${formulario.value.banhosUsados} para ${novoServico.quantidade} (limite do serviço)`)
    formulario.value.banhosUsados = 0 // Resetar para 0 para evitar confusão
  }
})

// 🔄 Lifecycle
onMounted(async () => {
  console.log('🚀 Inicializando cadastro de animal serviço...')
  await Promise.all([
    carregarAnimais(),
    carregarServicos(),
    carregarUsuarios()
  ])

  // Definir data padrão como hoje
  const hoje = new Date().toISOString().split('T')[0]
  formulario.value.dataServico = hoje || ''
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

/* 📱 Responsividade do formulário */
@media (max-width: 640px) {
  .grid-cols-1 {
    grid-template-columns: 1fr;
  }
}
</style>
