<template>
  <router-link
    v-if="to"
    :to="to"
    :class="itemClasses"
    @click="handleClick"
    :title="collapsed ? title : ''"
  >
    <FontAwesomeIcon v-if="icon" :icon="icon" class="w-5 h-5 flex-shrink-0" />
    <span v-show="!collapsed" class="truncate">{{ title }}</span>
  </router-link>

  <div v-else :class="itemClasses" @click="handleClick" :title="collapsed ? title : ''">
    <FontAwesomeIcon v-if="icon" :icon="icon" class="w-5 h-5 flex-shrink-0" />
    <span v-show="!collapsed" class="truncate">{{ title }}</span>
    <slot name="arrow" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useSidebarStore } from '@/stores/sidebar'

interface Props {
  title: string
  icon?: string
  to?: string
  active?: boolean
  hasChildren?: boolean
  collapsed?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  active: false,
  hasChildren: false,
  collapsed: false,
})

const emit = defineEmits<{
  click: []
}>()

const sidebarStore = useSidebarStore()

const handleClick = () => {
  // Se a sidebar está colapsada, abrir ela
  if (props.collapsed) {
    sidebarStore.open()
  }

  // Emitir o evento click original
  emit('click')
}

const itemClasses = computed(() => {
  const paddingX = props.collapsed ? 'px-3' : 'px-4'
  const justifyContent = props.collapsed ? 'justify-center' : 'justify-start'
  const baseClasses = `flex items-center gap-3 ${paddingX} ${justifyContent} py-3 rounded-lg transition-colors cursor-pointer`

  if (props.active) {
    return `${baseClasses} bg-primary-500 text-white`
  }

  return `${baseClasses} text-gray-700 hover:bg-primary-50 hover:text-primary-700`
})
</script>
