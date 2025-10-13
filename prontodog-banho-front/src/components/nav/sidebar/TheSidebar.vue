<template>
  <div
    :class="sidebarClasses"
    class="bg-white border-r border-gray-200 flex flex-col transition-all duration-300 fixed left-0 z-40"
    :style="{ top: '64px', height: 'calc(100vh - 64px)' }"
  >
    <!-- Botão Toggle apenas -->
    <div class="p-3 border-b border-gray-200 flex items-center justify-center">
      <button
        @click="sidebarStore.toggle()"
        class="p-2 rounded-lg hover:bg-primary-50 text-primary-600 transition-colors"
      >
        <FontAwesomeIcon :icon="sidebarStore.isOpen ? 'xmark' : 'bars'" class="w-4 h-4" />
      </button>
    </div>

    <nav class="flex-1 p-3 space-y-1 overflow-y-auto">
      <SidebarItem
        title="Home"
        icon="home"
        to="/"
        :active="$route.path === '/'"
        :collapsed="!sidebarStore.isOpen"
      />

      <SidebarDropdown
        title="Animais"
        icon="dog"
        basePath="/animais"
        :collapsed="!sidebarStore.isOpen"
      >
        <SidebarItem title="Lista de Animais" to="/animais" :collapsed="false" />
        <SidebarItem title="Novo Animal" to="/animais/novo" :collapsed="false" />
        <SidebarItem title="Histórico" to="/animais/historico" :collapsed="false" />
      </SidebarDropdown>

      <SidebarDropdown
        title="Clientes"
        icon="users"
        basePath="/clientes"
        :collapsed="!sidebarStore.isOpen"
      >
        <SidebarItem title="Lista de Clientes" to="/clientes" :collapsed="false" />
        <SidebarItem title="Novo Cliente" to="/clientes/novo" :collapsed="false" />
        <SidebarItem title="Relatórios" to="/clientes/relatorios" :collapsed="false" />
      </SidebarDropdown>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useSidebarStore } from '@/stores/sidebar'
import SidebarItem from './SidebarItem.vue'
import SidebarDropdown from './SidebarDropdown.vue'

const sidebarStore = useSidebarStore()

const sidebarClasses = computed(() => {
  return sidebarStore.isOpen ? 'w-64' : 'w-16'
})
</script>
