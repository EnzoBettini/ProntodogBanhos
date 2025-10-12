<template>
  <div class="p-8">
    <BaseCard>
      <template #header>
        <h1 class="text-2xl font-bold text-primary-700 flex items-center gap-2">
          <FontAwesomeIcon icon="users" />
          Lista de Clientes
        </h1>
      </template>

      <p class="text-gray-600">Gerencie todos os clientes do ProntoDog Banhos.</p>

      <div class="mt-6">
        <BaseButton variant="primary">
          <FontAwesomeIcon icon="users" class="mr-2" />
          Adicionar Cliente
        </BaseButton>
      </div>
    </BaseCard>
  </div>
</template>

<script setup lang="ts">
import BaseCard from '@/components/UI/BaseCard.vue'
import BaseButton from '@/components/UI/BaseButton.vue'
</script>
