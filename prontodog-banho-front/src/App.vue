<script setup lang="ts">
import { RouterView } from 'vue-router'
import { TheSidebar } from '@/components/nav'
</script>

<template>
  <div id="app" class="min-h-screen bg-gray-50 flex">
    <TheSidebar />
    <main class="flex-1 overflow-auto">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
/* Estilos personalizados do projeto aqui */
</style>
