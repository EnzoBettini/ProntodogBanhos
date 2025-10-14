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
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="opacity-0 max-h-0 -translate-y-2"
      enter-to-class="opacity-100 max-h-96 translate-y-0"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="opacity-100 max-h-96 translate-y-0"
      leave-to-class="opacity-0 max-h-0 -translate-y-2"
    >
      <div v-show="isOpen && !collapsed" class="ml-1 mt-2 space-y-2 overflow-hidden relative">
        <!-- ✨ Linha conectora visual -->
        <div class="absolute left-0 top-0 bottom-0 w-px bg-gradient-to-b from-primary-300/50 via-primary-200/30 to-transparent ml-1"></div>

        <div class="relative pl-3 space-y-1">
          <slot />
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useSidebarStore } from '@/stores/sidebar'
import SidebarItem from './SidebarItem.vue'

interface Props {
  title: string
  icon?: string
  basePath?: string
  collapsed?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  collapsed: false,
})

const route = useRoute()
const isOpen = ref(false)
const sidebarStore = useSidebarStore()

const active = computed(() => {
  if (!props.basePath) return false
  return route.path.startsWith(props.basePath)
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
