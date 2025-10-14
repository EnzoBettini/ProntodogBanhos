<template>
  <div
    :class="sidebarClasses"
    class="bg-gradient-to-b from-white via-primary-50/30 to-secondary-50/20 backdrop-blur-sm border-r border-primary-200/30 flex flex-col transition-all duration-300 fixed left-0 z-30 shadow-2xl overflow-hidden"
    :style="{ top: '0', height: '100vh' }"
  >
    <!-- ✨ Efeito de brilho lateral (suavizado) -->
    <div class="absolute inset-y-0 right-0 w-px bg-gradient-to-b from-transparent via-primary-300/20 to-transparent opacity-60"></div>

    <!-- Espaço para a navbar -->
    <div class="h-16 flex-shrink-0"></div>

    <!-- Botão Toggle apenas -->
    <div class="p-4 border-b border-primary-200/30 flex items-center justify-center relative">
      <!-- ✨ Gradiente de fundo sutil -->
      <div class="absolute inset-0 bg-gradient-to-r from-primary-50/50 to-secondary-50/30 opacity-50"></div>

      <button
        @click="sidebarStore.toggle()"
        class="relative z-10 p-3 rounded-xl bg-gradient-to-br from-white to-primary-50 hover:from-primary-50 hover:to-secondary-50 text-primary-600 hover:text-primary-700 transition-all duration-300 transform hover:scale-105 hover:shadow-lg border border-primary-200/50 hover:border-primary-300/60 group"
      >
        <FontAwesomeIcon
          :icon="sidebarStore.isOpen ? 'xmark' : 'bars'"
          class="w-4 h-4 transition-all duration-300 group-hover:rotate-90"
        />
        <!-- ✨ Efeito de brilho no hover -->
        <div class="absolute inset-0 bg-gradient-to-r from-primary-400/20 to-secondary-400/20 rounded-xl opacity-0 group-hover:opacity-100 transition-opacity duration-300 blur-sm"></div>
      </button>
    </div>

    <nav class="flex-1 p-4 overflow-y-auto custom-scrollbar" :class="sidebarStore.isOpen ? 'space-y-2' : 'space-y-3'">
      <div :class="sidebarStore.isOpen ? 'mb-6' : 'mb-4'">
        <SidebarItem
          title="Home"
          icon="home"
          to="/"
          :active="$route.path === '/'"
          :collapsed="!sidebarStore.isOpen"
        />
      </div>

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

    <!-- ✨ Footer com gradiente sutil (apenas quando aberto) -->
    <div v-if="sidebarStore.isOpen" class="p-4 border-t border-primary-200/30 bg-gradient-to-r from-primary-50/30 to-secondary-50/20">
      <div class="text-xs text-center text-gray-500 font-medium">
        <div class="flex items-center justify-center gap-1 mb-1">
          <FontAwesomeIcon icon="bath" class="text-primary-500 text-sm" />
          <span class="font-semibold">ProntoDog</span>
        </div>
        <div class="opacity-75">v1.0.0</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useSidebarStore } from '@/stores/sidebar'
import SidebarItem from './SidebarItem.vue'
import SidebarDropdown from './SidebarDropdown.vue'

const sidebarStore = useSidebarStore()

const sidebarClasses = computed(() => {
  return sidebarStore.isOpen ? 'w-64' : 'w-20'
})
</script>

<style scoped>
/* 🎨 Scrollbar customizada */
.custom-scrollbar {
  scrollbar-width: thin;
  scrollbar-color: rgba(17, 153, 142, 0.3) transparent;
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, rgba(17, 153, 142, 0.4) 0%, rgba(56, 239, 125, 0.4) 100%);
  border-radius: 3px;
  transition: background 0.3s ease;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, rgba(17, 153, 142, 0.6) 0%, rgba(56, 239, 125, 0.6) 100%);
}
</style>
