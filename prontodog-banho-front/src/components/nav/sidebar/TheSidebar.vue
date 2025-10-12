<template>
  <div
    :class="sidebarClasses"
    class="bg-white border-r border-gray-200 h-screen flex flex-col transition-all duration-300"
  >
    <!-- Header com logo e toggle -->
    <div class="p-4 border-b border-gray-200 flex items-center justify-between">
      <div v-show="sidebarStore.isOpen" class="flex items-center gap-2">
        <FontAwesomeIcon icon="bath" class="text-primary-500 text-xl" />
        <h1 class="text-lg font-bold text-primary-600">ProntoDog Banhos</h1>
      </div>

      <FontAwesomeIcon
        v-show="!sidebarStore.isOpen"
        icon="bath"
        class="text-primary-500 text-xl mx-auto"
      />

      <button
        @click="sidebarStore.toggle()"
        class="p-2 rounded-lg hover:bg-primary-50 text-primary-600 transition-colors"
        :class="{ 'ml-auto': !sidebarStore.isOpen }"
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
