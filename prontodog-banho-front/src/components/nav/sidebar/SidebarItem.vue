<template>
  <router-link
    v-if="to"
    :to="to"
    :class="itemClasses"
    @click="$emit('click')"
    :title="collapsed ? title : ''"
  >
    <FontAwesomeIcon v-if="icon" :icon="icon" class="w-5 h-5 flex-shrink-0" />
    <span v-show="!collapsed" class="truncate">{{ title }}</span>
  </router-link>

  <div v-else :class="itemClasses" @click="$emit('click')" :title="collapsed ? title : ''">
    <FontAwesomeIcon v-if="icon" :icon="icon" class="w-5 h-5 flex-shrink-0" />
    <span v-show="!collapsed" class="truncate">{{ title }}</span>
    <slot name="arrow" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

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

defineEmits<{
  click: []
}>()

const itemClasses = computed(() => {
  const paddingX = props.collapsed ? 'px-3' : 'px-4'
  const baseClasses = `flex items-center gap-3 ${paddingX} py-3 rounded-lg transition-colors cursor-pointer`

  if (props.active) {
    return `${baseClasses} bg-primary-500 text-white`
  }

  return `${baseClasses} text-gray-700 hover:bg-primary-50 hover:text-primary-700`
})
</script>
