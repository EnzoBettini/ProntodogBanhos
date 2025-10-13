<template>
  <div class="container mx-auto px-4 py-8">
    <!-- Header -->
    <BaseCard variant="gradient" padding="lg" class="text-center mb-8">
      <h1 class="text-4xl font-bold mb-2">⚙️ Configurações</h1>
      <p class="text-xl opacity-90">Personalize sua experiência</p>
    </BaseCard>

    <!-- Configurações do Sistema -->
    <BaseCard class="mb-6">
      <template #header>
        <h2 class="text-xl font-semibold text-primary-700">🎨 Aparência</h2>
      </template>

      <div class="space-y-6">
        <!-- Placeholder para configurações futuras -->
        <div class="text-center py-8">
          <FontAwesomeIcon icon="palette" class="text-4xl text-gray-400 mb-4" />
          <h3 class="text-lg font-medium text-gray-600 mb-2">Configurações de Tema</h3>
          <p class="text-gray-500">
            Personalizações de aparência serão adicionadas em breve.
          </p>
        </div>
      </div>
    </BaseCard>

    <!-- Configurações Futuras (placeholder) -->
    <BaseCard variant="outlined">
      <template #header>
        <h2 class="text-xl font-semibold text-primary-700">🔧 Configurações Avançadas</h2>
      </template>

      <div class="text-center py-8">
        <FontAwesomeIcon icon="cogs" class="text-4xl text-gray-400 mb-4" />
        <h3 class="text-lg font-medium text-gray-600 mb-2">Em Desenvolvimento</h3>
        <p class="text-gray-500">
          Mais configurações serão adicionadas em breve, incluindo notificações, preferências de usuário e muito mais.
        </p>
      </div>
    </BaseCard>

    <!-- Botão de Voltar -->
    <div class="flex justify-center mt-8">
      <BaseButton variant="outline" @click="$router.go(-1)">
        <FontAwesomeIcon icon="arrow-left" class="mr-2" />
        Voltar
      </BaseButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import BaseButton from '@/components/UI/BaseButton.vue'
import BaseCard from '@/components/UI/BaseCard.vue'
</script>
