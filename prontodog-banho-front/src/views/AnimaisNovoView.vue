<template>
  <div class="p-8">
    <BaseCard>
      <template #header>
        <h1 class="text-2xl font-bold text-primary-700 flex items-center gap-2">
          <FontAwesomeIcon icon="dog" />
          Novo Animal
        </h1>
      </template>

      <p class="text-gray-600 mb-6">Cadastre um novo animal no sistema.</p>

      <div class="grid md:grid-cols-2 gap-6">
        <BaseInput label="Nome do Animal" placeholder="Ex: Rex, Bella..." />
        <BaseInput label="Espécie" placeholder="Cão, Gato..." />
        <BaseInput label="Raça" placeholder="Golden, Persa..." />
        <BaseInput label="Idade" type="number" placeholder="Em anos" />
      </div>

      <div class="flex gap-3 mt-6">
        <BaseButton variant="primary">Salvar Animal</BaseButton>
        <BaseButton variant="ghost">Cancelar</BaseButton>
      </div>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseButton from '@/components/UI/BaseButton.vue'
import BaseInput from '@/components/UI/BaseInput.vue'
</script>
