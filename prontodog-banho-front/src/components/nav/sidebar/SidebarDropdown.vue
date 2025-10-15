<template>
  <div>
    <SidebarItem
      :title="title"
      :icon="icon"
      :active="active"
      :hasChildren="true"
      :collapsed="collapsed"
      @click="toggleOpen"
    >
      <template #arrow>
        <div v-show="!collapsed" class="ml-auto flex items-center">
          <FontAwesomeIcon
            :icon="isOpen ? 'chevron-up' : 'chevron-down'"
            class="w-4 h-4 transition-all duration-300 transform group-hover:scale-110"
            :class="{
              'rotate-180 text-primary-200': isOpen && active,
              'text-white/70': active,
              'text-gray-500 group-hover:text-primary-500': !active
            }"
          />
        </div>
      </template>
    </SidebarItem>

    <Transition
      name="slide-down"
      appear
    >
      <div v-if="isOpen && !collapsed" class="mt-2 overflow-hidden relative">
        <!-- ✨ Fundo suave para os subitens -->
        <div class="bg-gradient-to-r from-primary-25/40 to-secondary-25/20 rounded-lg border border-primary-100/30 shadow-inner">
          <!-- ✨ Linha conectora visual mais elegante -->
          <div class="absolute left-3 top-0 bottom-0 w-0.5 bg-gradient-to-b from-primary-400/60 via-primary-300/40 to-primary-200/20 rounded-full"></div>

          <div class="relative pl-8 pr-3 py-2 space-y-1.5">
            <slot />
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useSidebarStore } from '@/stores/sidebar'
import SidebarItem from './SidebarItem.vue'

interface Props {
  title: string
  icon?: string
  basePath?: string
  additionalPaths?: string[]
  collapsed?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  collapsed: false,
  additionalPaths: () => []
})

const route = useRoute()
const isOpen = ref(false)
const sidebarStore = useSidebarStore()

const active = computed(() => {
  if (!props.basePath) return false

  // Verifica o basePath principal
  if (route.path.startsWith(props.basePath)) {
    return true
  }

  // Verifica caminhos adicionais
  if (props.additionalPaths && props.additionalPaths.length > 0) {
    return props.additionalPaths.some(path => route.path.startsWith(path))
  }

  return false
})

// Inicializar dropdown aberto se a rota atual corresponde
onMounted(() => {
  if (active.value && !props.collapsed) {
    isOpen.value = true
  }
})

const toggleOpen = () => {
  // Se a sidebar está colapsada, abrir ela primeiro
  if (props.collapsed) {
    sidebarStore.open()
  } else {
    // Se não está colapsada, funciona normalmente (toggle do dropdown)
    isOpen.value = !isOpen.value
  }
}
</script>

<style scoped>
/* 🎨 Animações otimizadas para os dropdowns da sidebar */
.slide-down-enter-active {
  transition: all 0.25s ease-out;
  transform-origin: top;
  overflow: hidden;
}

.slide-down-leave-active {
  transition: all 0.2s ease-in;
  transform-origin: top;
  overflow: hidden;
}

/* Estados das transições - mais suaves */
.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-4px);
  max-height: 0;
}

.slide-down-enter-to {
  opacity: 1;
  transform: translateY(0);
  max-height: 300px;
}

.slide-down-leave-from {
  opacity: 1;
  transform: translateY(0);
  max-height: 300px;
}

.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-4px);
  max-height: 0;
}

</style>
