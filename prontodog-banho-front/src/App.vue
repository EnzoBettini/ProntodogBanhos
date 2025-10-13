<script setup lang="ts">
import { computed } from 'vue'
import { RouterView } from 'vue-router'
import { TheSidebar, TheNavbar } from '@/components/nav'
import { useSidebarStore } from '@/stores/sidebar'

const sidebarStore = useSidebarStore()

const mainContentClasses = computed(() => {
  // Margem esquerda baseada no estado da sidebar
  const marginLeft = sidebarStore.isOpen ? 'ml-64' : 'ml-16'
  return `${marginLeft} pt-16`
})
</script>

<template>
  <div id="app" class="min-h-screen bg-gray-50">
    <!-- Navbar fixa no topo -->
    <TheNavbar class="fixed top-0 left-0 right-0 z-50" />

    <!-- Sidebar -->
    <TheSidebar />

    <!-- Conteúdo principal -->
    <main :class="mainContentClasses" class="min-h-screen transition-all duration-300">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
/* Estilos personalizados do projeto aqui */
</style>
