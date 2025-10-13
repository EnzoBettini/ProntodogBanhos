<template>
  <div class="mb-4">
    <!-- 🏷️ Label -->
    <label
      v-if="label"
      :for="inputId"
      class="block text-sm font-medium text-gray-700 mb-1"
    >
      {{ label }}
      <span v-if="required" class="text-red-500 ml-1">*</span>
    </label>

    <!-- 📝 Input Field -->
    <div class="relative">
      <!-- Ícone à esquerda (opcional) -->
      <div
        v-if="icon"
        class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none"
      >
        <FontAwesomeIcon :icon="icon" class="text-gray-400" />
      </div>

      <!-- Campo de Input -->
      <input
        :id="inputId"
        :type="type"
        :value="modelValue"
        @input="handleInput"
        @blur="handleBlur"
        :placeholder="placeholder"
        :disabled="disabled"
        :maxlength="maxLength"
        :class="inputClasses"
        :autocomplete="autocomplete"
      />

      <!-- Contador de caracteres -->
      <div
        v-if="maxLength && showCount"
        class="absolute inset-y-0 right-0 pr-3 flex items-center text-xs text-gray-400 pointer-events-none"
      >
        {{ characterCount }}/{{ maxLength }}
      </div>
    </div>

    <!-- 📝 Dica de ajuda -->
    <p
      v-if="hint && !error"
      class="mt-1 text-xs text-gray-500"
    >
      {{ hint }}
    </p>

    <!-- ❌ Mensagem de erro -->
    <p
      v-if="error"
      class="mt-1 text-xs text-red-600 flex items-center"
    >
      <FontAwesomeIcon icon="exclamation-circle" class="mr-1" />
      {{ error }}
    </p>

    <!-- ✅ Mensagem de sucesso -->
    <p
      v-if="success"
      class="mt-1 text-xs text-green-600 flex items-center"
    >
      <FontAwesomeIcon icon="check-circle" class="mr-1" />
      {{ success }}
    </p>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

// 📋 Props
interface Props {
  modelValue: string
  label?: string
  type?: string
  placeholder?: string
  icon?: string
  disabled?: boolean
  required?: boolean
  error?: string | null
  success?: string | null
  hint?: string
  maxLength?: number
  showCount?: boolean
  autocomplete?: string
  mask?: 'cpf' | 'telefone' | null
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  disabled: false,
  required: false,
  showCount: false,
  autocomplete: 'off',
  mask: null
})

// 📤 Emits
const emit = defineEmits<{
  'update:modelValue': [value: string]
  blur: [event: FocusEvent]
}>()

// 🆔 ID único para o input
const inputId = ref(`input-${Math.random().toString(36).substr(2, 9)}`)

// 💻 Computed properties
const inputClasses = computed(() => {
  const baseClasses = 'block w-full rounded-lg border transition-colors focus:ring-2 focus:ring-offset-0 disabled:opacity-50 disabled:cursor-not-allowed'

  const paddingClasses = props.icon ? 'pl-10 pr-3 py-2.5' : 'px-3 py-2.5'

  const countPaddingClasses = props.maxLength && props.showCount ? 'pr-16' : ''

  if (props.error) {
    return `${baseClasses} ${paddingClasses} ${countPaddingClasses} border-red-300 text-red-900 placeholder-red-300 focus:border-red-500 focus:ring-red-500`
  }

  if (props.success) {
    return `${baseClasses} ${paddingClasses} ${countPaddingClasses} border-green-300 text-green-900 focus:border-green-500 focus:ring-green-500`
  }

  return `${baseClasses} ${paddingClasses} ${countPaddingClasses} border-gray-300 text-gray-900 placeholder-gray-400 focus:border-primary-500 focus:ring-primary-500`
})

const characterCount = computed(() => {
  return props.modelValue?.length || 0
})

// 🔧 Funções de formatação/máscara
const aplicarMascara = (valor: string): string => {
  if (!props.mask) return valor

  // Remove tudo que não é número
  const numeroLimpo = valor.replace(/\D/g, '')

  switch (props.mask) {
    case 'cpf':
      // Máscara: 000.000.000-00
      return numeroLimpo
        .substring(0, 11)
        .replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
        .replace(/(\d{3})(\d{3})(\d{3})/, '$1.$2.$3')
        .replace(/(\d{3})(\d{3})/, '$1.$2')

    case 'telefone':
      // Máscara: (00) 00000-0000 ou (00) 0000-0000
      if (numeroLimpo.length <= 10) {
        return numeroLimpo
          .substring(0, 10)
          .replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3')
          .replace(/(\d{2})(\d{4})/, '($1) $2')
          .replace(/(\d{2})/, '($1')
      } else {
        return numeroLimpo
          .substring(0, 11)
          .replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3')
          .replace(/(\d{2})(\d{5})/, '($1) $2')
          .replace(/(\d{2})/, '($1')
      }

    default:
      return valor
  }
}

// 🎬 Event handlers
const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  let valor = target.value

  // Aplica máscara se configurada
  if (props.mask) {
    valor = aplicarMascara(valor)
  }

  emit('update:modelValue', valor)
}

const handleBlur = (event: FocusEvent) => {
  emit('blur', event)
}
</script>
