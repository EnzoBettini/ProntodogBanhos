<template>
  <div class="p-8">
    <BaseCard>
      <template #header>
        <h1 class="text-2xl font-bold text-primary-700 flex items-center gap-2">
          <FontAwesomeIcon icon="users" />
          Novo Cliente
        </h1>
      </template>

      <p class="text-gray-600 mb-6">Cadastre um novo cliente no sistema.</p>

      <div class="grid md:grid-cols-2 gap-6">
        <BaseInput label="Nome Completo" placeholder="João da Silva" required />
        <BaseInput label="E-mail" type="email" placeholder="joao@email.com" />
        <BaseInput label="Telefone" type="tel" placeholder="(11) 99999-9999" />
        <BaseInput label="CPF" placeholder="000.000.000-00" />
      </div>

      <div class="flex gap-3 mt-6">
        <BaseButton variant="primary">Salvar Cliente</BaseButton>
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
