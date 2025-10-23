<template>
  <button :class="buttonClasses" :disabled="disabled" @click="$emit('click', $event)">
    <slot />
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger'
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
  disabled: false,
  loading: false,
})

defineEmits<{
  click: [event: MouseEvent]
}>()

const buttonClasses = computed(() => {
  const baseClasses =
    'inline-flex items-center justify-center font-semibold rounded-lg transition-colors focus:outline-none disabled:opacity-50 disabled:cursor-not-allowed'

  // Tamanhos
  const sizeClasses = {
    sm: 'px-3 py-2 text-sm',
    md: 'px-4 py-2.5 text-sm',
    lg: 'px-6 py-3 text-base',
  }

  // Variantes com nossa paleta Verde Natureza
  const variantClasses = {
    primary: 'bg-primary-500 text-white hover:bg-primary-600 border-2 border-transparent',
    secondary: 'bg-secondary-500 text-white hover:bg-secondary-600 border-2 border-transparent',
    outline:
      'border-2 border-primary-500 text-primary-600 hover:bg-primary-50',
    ghost: 'text-primary-600 hover:bg-primary-50 border-2 border-transparent',
    danger: 'bg-red-500 text-white hover:bg-red-600 border-2 border-transparent',
  }

  return [
    baseClasses,
    sizeClasses[props.size],
    variantClasses[props.variant],
    props.loading ? 'cursor-wait' : '',
  ].join(' ')
})
</script>
