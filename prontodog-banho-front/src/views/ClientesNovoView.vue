<template>
  <div class="p-8 max-w-4xl mx-auto">
    <!-- 🎯 Cabeçalho -->
    <div class="mb-8">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <BaseButton
            variant="ghost"
            size="sm"
            @click="voltar"
            class="text-gray-600"
          >
            <FontAwesomeIcon icon="arrow-left" class="mr-2" />
            Voltar
          </BaseButton>
          <div class="h-6 border-l border-gray-300"></div>
          <h1 class="text-3xl font-bold text-primary-700 flex items-center gap-3">
            <FontAwesomeIcon icon="user-plus" class="text-2xl" />
          Novo Cliente
        </h1>
        </div>

        <!-- 💾 Status do formulário -->
        <div v-if="formStatus" class="flex items-center gap-2">
          <FontAwesomeIcon
            :icon="formStatus.icon"
            :class="formStatus.color"
          />
          <span :class="formStatus.color" class="text-sm font-medium">
            {{ formStatus.message }}
          </span>
        </div>
      </div>
      <p class="text-gray-600 mt-2">
        Preencha os dados para cadastrar um novo cliente no sistema ProntoDog Banhos.
      </p>
    </div>

    <form @submit.prevent="salvarCliente">
      <!-- 👤 DADOS PESSOAIS -->
      <BaseCard class="mb-8">
        <template #header>
          <h2 class="text-xl font-semibold text-gray-800 flex items-center gap-2">
            <FontAwesomeIcon icon="user" class="text-primary-500" />
            Dados Pessoais
          </h2>
      </template>

      <div class="grid md:grid-cols-2 gap-6">
          <!-- Nome Completo -->
          <div class="md:col-span-2">
            <FormInput
              v-model="formulario.nomeCompleto"
              label="Nome Completo"
              placeholder="João da Silva"
              icon="user"
              required
              :error="erros.nomeCompleto"
              hint="Digite nome e sobrenome completos"
              :maxLength="100"
              :showCount="true"
              autocomplete="name"
              @blur="validarCampo('nomeCompleto')"
            />
          </div>

          <!-- CPF -->
          <FormInput
            v-model="formulario.cpf"
            label="CPF"
            placeholder="000.000.000-00"
            icon="id-card"
            required
            :error="erros.cpf"
            hint="Documento obrigatório para cadastro"
            mask="cpf"
            autocomplete="off"
            @blur="validarCampo('cpf')"
          />

          <!-- Código SimplesVet -->
          <FormInput
            v-model="formulario.codigoSimplesVet"
            label="Código SimplesVet"
            placeholder="123456"
            icon="hashtag"
            required
            :error="erros.codigoSimplesVet"
            hint="Código único do cliente no sistema"
            type="number"
            @blur="validarCampo('codigoSimplesVet')"
          />
        </div>
      </BaseCard>

      <!-- 📞 CONTATOS -->
      <BaseCard class="mb-8">
        <template #header>
          <div class="flex items-center justify-between">
            <h2 class="text-xl font-semibold text-gray-800 flex items-center gap-2">
              <FontAwesomeIcon icon="phone" class="text-primary-500" />
              Telefones
            </h2>
            <BaseButton
              type="button"
              variant="outline"
              size="sm"
              @click="adicionarTelefone"
              :disabled="formulario.telefones.length >= 3"
            >
              <FontAwesomeIcon icon="plus" class="mr-2" />
              Adicionar Telefone
            </BaseButton>
          </div>
        </template>

        <div v-if="formulario.telefones.length === 0" class="text-center py-8 text-gray-500">
          <FontAwesomeIcon icon="phone" class="text-4xl mb-4 text-gray-300" />
          <p class="font-medium">Nenhum telefone adicionado</p>
          <p class="text-sm">Clique em "Adicionar Telefone" para começar</p>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="(telefone, index) in formulario.telefones"
            :key="`telefone-${index}`"
            class="flex items-start gap-4 p-4 bg-gray-50 rounded-lg"
          >
            <div class="flex-1">
              <FormInput
                v-model="formulario.telefones[index]!"
                :label="`Telefone ${index + 1}`"
                placeholder="(11) 99999-9999"
                icon="phone"
                :required="index === 0"
                :error="erros.telefones?.[index] || null"
                :hint="index === 0 ? 'Telefone principal (obrigatório)' : 'Telefone adicional (opcional)'"
                mask="telefone"
                @blur="validarTelefone(index)"
              />
            </div>

            <!-- Botão remover (só se tiver mais de 1) -->
            <BaseButton
              v-if="formulario.telefones.length > 1"
              type="button"
              variant="ghost"
              size="sm"
              @click="removerTelefone(index)"
              class="text-red-600 hover:text-red-700 mt-7"
              title="Remover telefone"
            >
              <FontAwesomeIcon icon="trash" />
            </BaseButton>
          </div>
        </div>
      </BaseCard>

      <!-- 📧 EMAILS -->
      <BaseCard class="mb-8">
        <template #header>
          <div class="flex items-center justify-between">
            <h2 class="text-xl font-semibold text-gray-800 flex items-center gap-2">
              <FontAwesomeIcon icon="envelope" class="text-primary-500" />
              Emails
              <span class="text-sm font-normal text-gray-500">(Opcional)</span>
            </h2>
            <BaseButton
              type="button"
              variant="outline"
              size="sm"
              @click="adicionarEmail"
              :disabled="formulario.emailClientes.length >= 3"
            >
              <FontAwesomeIcon icon="plus" class="mr-2" />
              Adicionar Email
            </BaseButton>
          </div>
        </template>

        <div v-if="formulario.emailClientes.length === 0" class="text-center py-8 text-gray-500">
          <FontAwesomeIcon icon="envelope" class="text-4xl mb-4 opacity-50" />
          <p class="text-lg font-medium mb-2">Nenhum email cadastrado</p>
          <p class="text-sm">
            Clique em "Adicionar Email" para incluir endereços de email do cliente
          </p>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="(email, index) in formulario.emailClientes"
            :key="`email-${index}`"
            class="flex items-start gap-4 p-4 bg-gray-50 rounded-lg"
          >
            <div class="flex-1">
              <FormInput
                v-model="formulario.emailClientes[index]!"
                :label="`Email ${index + 1}`"
                placeholder="exemplo@email.com"
                icon="at"
                type="email"
                :error="erros.emailClientes?.[index] || null"
                :hint="index === 0 ? 'Email principal (opcional)' : 'Email adicional (opcional)'"
                :maxLength="100"
                autocomplete="email"
                @blur="validarEmailCliente(index)"
              />
            </div>
            <BaseButton
              type="button"
              variant="ghost"
              size="sm"
              @click="removerEmail(index)"
              class="text-red-600 hover:text-red-700 mt-7"
              title="Remover email"
            >
              <FontAwesomeIcon icon="trash" />
            </BaseButton>
          </div>
        </div>
      </BaseCard>

      <!-- 🐕 ANIMAIS -->
      <BaseCard class="mb-8">
        <template #header>
          <div class="flex items-center justify-between">
            <h2 class="text-xl font-semibold text-gray-800 flex items-center gap-2">
              <FontAwesomeIcon icon="dog" class="text-primary-500" />
              Animais de Estimação
            </h2>
            <BaseButton
              type="button"
              variant="outline"
              size="sm"
              @click="adicionarAnimal"
              :disabled="formulario.animais.length >= 5"
            >
              <FontAwesomeIcon icon="plus" class="mr-2" />
              Adicionar Animal
            </BaseButton>
          </div>
        </template>

        <div v-if="formulario.animais.length === 0" class="text-center py-8 text-gray-500">
          <FontAwesomeIcon icon="dog" class="text-4xl mb-4 text-gray-300" />
          <p class="font-medium">Nenhum animal cadastrado</p>
          <p class="text-sm">Adicione pelo menos um pet para continuar</p>
        </div>

        <div v-else class="space-y-6">
          <div
            v-for="(animal, index) in formulario.animais"
            :key="`animal-${index}`"
            class="p-6 bg-gradient-to-r from-primary-50 to-secondary-50 rounded-lg border border-primary-100"
          >
            <div class="flex items-center justify-between mb-4">
              <h3 class="font-medium text-gray-800 flex items-center gap-2">
                <FontAwesomeIcon icon="dog" class="text-primary-600" />
                Animal {{ index + 1 }}
              </h3>

              <BaseButton
                v-if="formulario.animais.length > 1"
                type="button"
                variant="ghost"
                size="sm"
                @click="removerAnimal(index)"
                class="text-red-600 hover:text-red-700"
                title="Remover animal"
              >
                <FontAwesomeIcon icon="trash" />
              </BaseButton>
            </div>

            <div class="grid md:grid-cols-3 gap-4">
              <!-- Nome do Animal -->
              <FormInput
                v-model="formulario.animais[index]!.nome"
                label="Nome do Animal"
                placeholder="Rex, Luna, etc."
                icon="dog"
                required
                :error="erros.animais?.[index]?.nome || null"
                :maxLength="50"
                @blur="validarAnimal(index, 'nome')"
              />

              <!-- Tipo -->
              <div class="mb-4">
                <label class="block text-sm font-medium text-gray-700 mb-1">
                  Tipo de Animal
                  <span class="text-red-500 ml-1">*</span>
                </label>
                <select
                  v-model="formulario.animais[index]!.tipo"
                  class="block w-full px-3 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-colors"
                  :class="erros.animais?.[index]?.tipo ? 'border-red-300 text-red-900' : ''"
                  @blur="validarAnimal(index, 'tipo')"
                >
                  <option value="">Selecione o tipo</option>
                  <option
                    v-for="tipo in TIPOS_ANIMAIS"
                    :key="tipo"
                    :value="tipo"
                  >
                    {{ tipo }}
                  </option>
                </select>
                <p
                  v-if="erros.animais?.[index]?.tipo"
                  class="mt-1 text-xs text-red-600 flex items-center"
                >
                  <FontAwesomeIcon icon="exclamation-circle" class="mr-1" />
                  {{ erros.animais[index]?.tipo }}
                </p>
              </div>

              <!-- Código SimplesVet do Animal -->
              <FormInput
                v-model="formulario.animais[index]!.codigoSimplesVet"
                label="Código SimplesVet"
                placeholder="123"
                icon="hashtag"
                required
                :error="erros.animais?.[index]?.codigoSimplesVet || null"
                type="number"
                hint="Código único do animal"
                @blur="validarAnimal(index, 'codigoSimplesVet')"
              />
            </div>
          </div>
        </div>
      </BaseCard>

      <!-- 💾 AÇÕES -->
      <BaseCard>
        <div class="flex items-center justify-between">
          <div class="text-sm text-gray-600">
            <p class="font-medium">Dados obrigatórios:</p>
            <ul class="list-disc list-inside mt-1 space-y-1">
              <li>Nome completo, CPF e código SimplesVet</li>
              <li>Pelo menos um telefone</li>
              <li>Pelo menos um animal</li>
            </ul>
      </div>

          <div class="flex gap-4">
            <BaseButton
              type="button"
              variant="outline"
              @click="voltar"
              :disabled="salvando"
            >
              <FontAwesomeIcon icon="times" class="mr-2" />
              Cancelar
            </BaseButton>

            <BaseButton
              type="submit"
              variant="primary"
              :disabled="!formularioValido || salvando"
              class="min-w-32"
            >
              <FontAwesomeIcon
                :icon="salvando ? 'spinner' : 'save'"
                :class="{ 'animate-spin': salvando }"
                class="mr-2"
              />
              {{ salvando ? 'Salvando...' : 'Salvar Cliente' }}
            </BaseButton>
          </div>
      </div>
    </BaseCard>
    </form>
  </div>
</template>

<script setup lang="ts">
// 📚 Imports
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseButton from '@/components/UI/BaseButton.vue'
import FormInput from '@/components/UI/FormInput.vue'
import { clientesService } from '@/services/api'
import type { FormularioCliente, NovoCliente } from '@/types/api'
import { TIPOS_ANIMAIS } from '@/types/api'
import {
  validarNomeCompleto,
  validarCpf,
  validarCodigoSimplesVet,
  validarTelefone as validarTelefoneUtil,
  validarEmail,
  validarNomeAnimal,
  validarTipoAnimal,
  limparFormatacao
} from '@/utils/validations'

// 🎯 Configurações
const router = useRouter()

// 📊 Estado do componente
const salvando = ref(false)
const erros = ref<any>({})
const formStatus = ref<{
  icon: string
  color: string
  message: string
} | null>(null)

// 📋 Dados do formulário
const formulario = ref<FormularioCliente>({
  nomeCompleto: '',
  cpf: '',
  codigoSimplesVet: '',
  telefones: [''], // Inicia com 1 telefone
  emailClientes: [], // Inicia vazio, email é opcional
  animais: [
    {
      nome: '',
      codigoSimplesVet: '',
      tipo: ''
    }
  ] // Inicia com 1 animal
})

// 💻 Computed Properties
const formularioValido = computed(() => {
  return (
    // Dados pessoais
    formulario.value.nomeCompleto.trim() &&
    formulario.value.cpf.trim() &&
    formulario.value.codigoSimplesVet.trim() &&

    // Pelo menos um telefone válido
    formulario.value.telefones.some(tel => tel.trim()) &&

    // Pelo menos um animal com dados válidos
    formulario.value.animais.some(animal =>
      animal.nome.trim() &&
      animal.tipo.trim() &&
      animal.codigoSimplesVet.trim()
    ) &&

    // Nenhum erro
    Object.keys(erros.value).length === 0
  )
})

// 🔧 Funções de validação
const validarCampo = (campo: keyof FormularioCliente): void => {
  switch (campo) {
    case 'nomeCompleto':
      erros.value.nomeCompleto = validarNomeCompleto(formulario.value.nomeCompleto)
      break
    case 'cpf':
      erros.value.cpf = validarCpf(formulario.value.cpf)
      break
    case 'codigoSimplesVet':
      erros.value.codigoSimplesVet = validarCodigoSimplesVet(formulario.value.codigoSimplesVet)
      break
  }

  // Remove erro se válido
  if (!erros.value[campo]) {
    delete erros.value[campo]
  }
}

const validarTelefone = (index: number): void => {
  if (!erros.value.telefones) erros.value.telefones = []

  const telefone = formulario.value.telefones[index]
  if (telefone !== undefined) {
    erros.value.telefones[index] = validarTelefoneUtil(telefone)
  }

  if (!erros.value.telefones[index]) {
    delete erros.value.telefones[index]
    if (Object.keys(erros.value.telefones).length === 0) {
      delete erros.value.telefones
    }
  }
}

const validarEmailCliente = (index: number): void => {
  if (!erros.value.emailClientes) erros.value.emailClientes = []

  const email = formulario.value.emailClientes[index]
  if (email !== undefined) {
    erros.value.emailClientes[index] = validarEmail(email)
  }

  if (!erros.value.emailClientes[index]) {
    delete erros.value.emailClientes[index]
    if (Object.keys(erros.value.emailClientes).length === 0) {
      delete erros.value.emailClientes
    }
  }
}

const validarAnimal = (index: number, campo: 'nome' | 'tipo' | 'codigoSimplesVet'): void => {
  if (!erros.value.animais) erros.value.animais = []
  if (!erros.value.animais[index]) erros.value.animais[index] = {}

  const animal = formulario.value.animais[index]
  if (!animal) return

  switch (campo) {
    case 'nome':
      erros.value.animais[index].nome = validarNomeAnimal(animal.nome)
      break
    case 'tipo':
      erros.value.animais[index].tipo = validarTipoAnimal(animal.tipo)
      break
    case 'codigoSimplesVet':
      erros.value.animais[index].codigoSimplesVet = validarCodigoSimplesVet(animal.codigoSimplesVet)
      break
  }

  if (!erros.value.animais[index][campo]) {
    delete erros.value.animais[index][campo]
    if (Object.keys(erros.value.animais[index]).length === 0) {
      delete erros.value.animais[index]
      if (Object.keys(erros.value.animais).length === 0) {
        delete erros.value.animais
      }
    }
  }
}

// 📞 Gerenciar telefones
const adicionarTelefone = (): void => {
  if (formulario.value.telefones.length < 3) {
    formulario.value.telefones.push('')
  }
}

const removerTelefone = (index: number): void => {
  if (formulario.value.telefones.length > 1) {
    formulario.value.telefones.splice(index, 1)

    // Limpa erros do telefone removido
    if (erros.value.telefones) {
      erros.value.telefones.splice(index, 1)
      if (erros.value.telefones.length === 0) {
        delete erros.value.telefones
      }
    }
  }
}

// 📧 Gerenciar emails
const adicionarEmail = (): void => {
  if (formulario.value.emailClientes.length < 3) {
    formulario.value.emailClientes.push('')
  }
}

const removerEmail = (index: number): void => {
  formulario.value.emailClientes.splice(index, 1)

  // Limpa erros do email removido
  if (erros.value.emailClientes) {
    erros.value.emailClientes.splice(index, 1)
    if (erros.value.emailClientes.length === 0) {
      delete erros.value.emailClientes
    }
  }
}

// 🐕 Gerenciar animais
const adicionarAnimal = (): void => {
  if (formulario.value.animais.length < 5) {
    formulario.value.animais.push({
      nome: '',
      codigoSimplesVet: '',
      tipo: ''
    })
  }
}

const removerAnimal = (index: number): void => {
  if (formulario.value.animais.length > 1) {
    formulario.value.animais.splice(index, 1)

    // Limpa erros do animal removido
    if (erros.value.animais) {
      erros.value.animais.splice(index, 1)
      if (erros.value.animais.length === 0) {
        delete erros.value.animais
      }
    }
  }
}

// 💾 Salvar cliente
const salvarCliente = async (): Promise<void> => {
  try {
    console.log('📝 Iniciando validação do formulário...')

    // Limpa status anterior
    formStatus.value = null

    // Valida todos os campos
    validarCampo('nomeCompleto')
    validarCampo('cpf')
    validarCampo('codigoSimplesVet')

    formulario.value.telefones.forEach((_, index) => validarTelefone(index))
    formulario.value.animais.forEach((_, index) => {
      validarAnimal(index, 'nome')
      validarAnimal(index, 'tipo')
      validarAnimal(index, 'codigoSimplesVet')
    })

    // Verifica se há erros
    if (!formularioValido.value) {
      formStatus.value = {
        icon: 'exclamation-circle',
        color: 'text-red-600',
        message: 'Corrija os erros antes de continuar'
      }
      return
    }

    console.log('✅ Formulário válido! Preparando dados...')
    salvando.value = true

    // Monta o objeto para enviar à API
    const novoCliente: NovoCliente = {
      nomeCompleto: formulario.value.nomeCompleto.trim(),
      cpf: limparFormatacao(formulario.value.cpf),
      codigoSimplesVet: parseInt(formulario.value.codigoSimplesVet),
      telefones: formulario.value.telefones
        .filter(tel => tel.trim())
        .map(tel => ({ telefone: limparFormatacao(tel) })),
      emailClientes: formulario.value.emailClientes
        .filter(email => email.trim())
        .map(email => ({ email: email.trim() })),
      animais: formulario.value.animais
        .filter(animal => animal.nome.trim() && animal.tipo.trim() && animal.codigoSimplesVet.trim())
        .map(animal => ({
          nome: animal.nome.trim(),
          tipo: animal.tipo,
          codigoSimplesVet: parseInt(animal.codigoSimplesVet)
        }))
    }

    console.log('🚀 Enviando dados:', novoCliente)

    // Chama a API
    const clienteCriado = await clientesService.criar(novoCliente)

    console.log('🎉 Cliente criado com sucesso!', clienteCriado)

    // Sucesso!
    formStatus.value = {
      icon: 'check-circle',
      color: 'text-green-600',
      message: 'Cliente cadastrado com sucesso!'
    }

    // Aguarda um pouco para mostrar o sucesso
    setTimeout(() => {
      router.push('/clientes')
    }, 2000)

  } catch (error) {
    console.error('❌ Erro ao salvar cliente:', error)

    formStatus.value = {
      icon: 'exclamation-circle',
      color: 'text-red-600',
      message: error instanceof Error ? error.message : 'Erro inesperado ao salvar cliente'
    }

  } finally {
    salvando.value = false
  }
}

// 🔙 Voltar
const voltar = (): void => {
  if (confirm('Tem certeza que deseja sair? Os dados não salvos serão perdidos.')) {
    router.push('/clientes')
  }
}

// 🎬 Lifecycle
onMounted(() => {
  console.log('🎬 Página de novo cliente carregada!')
})
</script>
