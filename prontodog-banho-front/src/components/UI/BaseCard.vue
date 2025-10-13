<template>
  <div :class="cardClasses">
    <!-- Header do card (opcional) -->
    <div v-if="$slots.header" class="px-6 py-4 border-b border-gray-200">
      <slot name="header" />
    </div>

    <!-- Conteúdo principal -->
    <div :class="contentClasses">
      <slot />
    </div>

    <!-- Footer do card (opcional) -->
    <div v-if="$slots.footer" class="px-6 py-4 border-t border-gray-200 bg-gray-50">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'default' | 'outlined' | 'elevated' | 'gradient'
  padding?: 'none' | 'sm' | 'md' | 'lg'
  rounded?: 'sm' | 'md' | 'lg' | 'xl'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  padding: 'md',
  rounded: 'lg',
})

const cardClasses = computed(() => {
  const baseClasses = 'bg-white overflow-hidden transition-colors'

  const variantClasses = {
    default: 'shadow-md',
    outlined: 'border border-gray-200',
    elevated: 'shadow-xl',
    gradient: 'bg-gradient-primary text-white',
  }

  const roundedClasses = {
    sm: 'rounded',
    md: 'rounded-md',
    lg: 'rounded-lg',
    xl: 'rounded-xl',
  }

  return [baseClasses, variantClasses[props.variant], roundedClasses[props.rounded]].join(' ')
})

const contentClasses = computed(() => {
  const paddingClasses = {
    none: '',
    sm: 'p-4',
    md: 'p-6',
    lg: 'p-8',
  }

  return paddingClasses[props.padding]
})
</script>
