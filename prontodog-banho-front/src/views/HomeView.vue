<template>
  <div class="container mx-auto px-4 py-8">
    <!-- Header com gradiente da marca -->
    <BaseCard variant="gradient" padding="lg" class="text-center mb-8">
      <h1 class="text-4xl font-bold mb-4">🐕 ProntoDog Banhos</h1>
      <p class="text-xl opacity-90">Componentes Base Criados! ✅</p>
    </BaseCard>

    <!-- Demonstração dos Componentes Base -->
    <div class="space-y-8">
      <!-- Botões -->
      <BaseCard>
        <template #header>
          <h2 class="text-xl font-semibold text-primary-700">🔘 BaseButton - Variantes</h2>
        </template>

        <div class="space-y-4">
          <div class="flex flex-wrap gap-3">
            <BaseButton variant="primary">Primário</BaseButton>
            <BaseButton variant="secondary">Secundário</BaseButton>
            <BaseButton variant="outline">Outline</BaseButton>
            <BaseButton variant="ghost">Ghost</BaseButton>
            <BaseButton variant="danger">Perigo</BaseButton>
          </div>

          <div class="flex flex-wrap gap-3">
            <BaseButton size="sm">Pequeno</BaseButton>
            <BaseButton size="md">Médio</BaseButton>
            <BaseButton size="lg">Grande</BaseButton>
            <BaseButton disabled>Desabilitado</BaseButton>
          </div>
        </div>
      </BaseCard>

      <!-- Cards -->
      <BaseCard>
        <template #header>
          <h2 class="text-xl font-semibold text-primary-700">📦 BaseCard - Variantes</h2>
        </template>

        <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-4">
          <BaseCard variant="default" padding="sm">
            <h3 class="font-semibold mb-2">Card Padrão</h3>
            <p class="text-sm text-gray-600">Com sombra sutil</p>
          </BaseCard>

          <BaseCard variant="outlined" padding="sm">
            <h3 class="font-semibold mb-2">Card Outlined</h3>
            <p class="text-sm text-gray-600">Com borda</p>
          </BaseCard>

          <BaseCard variant="elevated" padding="sm">
            <h3 class="font-semibold mb-2">Card Elevado</h3>
            <p class="text-sm text-gray-600">Sombra intensa</p>
          </BaseCard>

          <BaseCard variant="gradient" padding="sm">
            <h3 class="font-semibold mb-2">Card Gradiente</h3>
            <p class="text-sm opacity-90">Com gradiente da marca</p>
          </BaseCard>
        </div>
      </BaseCard>

      <!-- Inputs -->
      <BaseCard>
        <template #header>
          <h2 class="text-xl font-semibold text-primary-700">📝 BaseInput - Exemplos</h2>
        </template>

        <div class="grid md:grid-cols-2 gap-6">
          <BaseInput
            label="Nome do Cliente"
            placeholder="Digite o nome..."
            hint="Nome completo do cliente"
          />

          <BaseInput label="E-mail" type="email" placeholder="cliente@email.com" required />

          <BaseInput label="Telefone" type="tel" placeholder="(11) 99999-9999" />

          <BaseInput
            label="Campo com Erro"
            placeholder="Valor inválido"
            error="Este campo é obrigatório"
          />
        </div>
      </BaseCard>

      <!-- Badges -->
      <BaseCard>
        <template #header>
          <h2 class="text-xl font-semibold text-primary-700">🏷️ BaseBadge - Status</h2>
        </template>

        <div class="space-y-4">
          <div class="flex flex-wrap gap-3">
            <BaseBadge variant="primary">Agendado</BaseBadge>
            <BaseBadge variant="secondary">Em Andamento</BaseBadge>
            <BaseBadge variant="success">Concluído</BaseBadge>
            <BaseBadge variant="warning">Aguardando</BaseBadge>
            <BaseBadge variant="danger">Cancelado</BaseBadge>
            <BaseBadge variant="info">Informação</BaseBadge>
            <BaseBadge variant="gray">Inativo</BaseBadge>
          </div>

          <div class="flex flex-wrap gap-3">
            <BaseBadge size="sm" variant="primary">Pequeno</BaseBadge>
            <BaseBadge size="md" variant="secondary">Médio</BaseBadge>
            <BaseBadge size="lg" variant="success">Grande</BaseBadge>
            <BaseBadge variant="warning" rounded>Arredondado</BaseBadge>
          </div>
        </div>
      </BaseCard>

      <!-- Modal (demonstração) -->
      <BaseCard>
        <template #header>
          <h2 class="text-xl font-semibold text-primary-700">🔲 BaseModal - Pop-ups</h2>
        </template>

        <div class="space-y-4">
          <BaseButton @click="showModal = true">Abrir Modal</BaseButton>

          <BaseModal v-model="showModal" title="Exemplo de Modal" size="md">
            <p class="text-gray-600 mb-4">
              Este é um exemplo de modal usando o componente BaseModal.
            </p>
            <BaseInput label="Nome do Pet" placeholder="Digite o nome do pet..." />

            <template #footer>
              <BaseButton variant="ghost" @click="showModal = false"> Cancelar </BaseButton>
              <BaseButton variant="primary" @click="showModal = false"> Salvar </BaseButton>
            </template>
          </BaseModal>
        </div>
      </BaseCard>

      <!-- Guia de uso -->
      <BaseCard variant="outlined" class="bg-primary-50">
        <template #header>
          <h2 class="text-xl font-semibold text-primary-700">📚 Como usar os componentes:</h2>
        </template>

        <div class="grid md:grid-cols-2 gap-6">
          <div>
            <h3 class="font-semibold text-primary-600 mb-3">Importação:</h3>
            <pre
              class="bg-gray-100 p-3 rounded text-sm overflow-x-auto"
            ><code>import { BaseButton, BaseCard, BaseInput } from '@/components/UI'</code></pre>
          </div>

          <div>
            <h3 class="font-semibold text-primary-600 mb-3">Uso no template:</h3>
            <pre
              class="bg-gray-100 p-3 rounded text-sm overflow-x-auto"
            ><code>&lt;BaseButton variant="primary"&gt;
  Texto do Botão
&lt;/BaseButton&gt;</code></pre>
          </div>
        </div>
      </BaseCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import BaseButton from '@/components/UI/BaseButton.vue'
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseInput from '@/components/UI/BaseInput.vue'
import BaseModal from '@/components/UI/BaseModal.vue'
import BaseBadge from '@/components/UI/BaseBadge.vue'

// Estado para o modal de demonstração
const showModal = ref(false)
</script>
