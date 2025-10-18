<template>
  <div class="relative">
    <!-- 🔍 Input de busca -->
    <div class="relative">
      <input
        ref="inputRef"
        v-model="searchTerm"
        type="text"
        :placeholder="placeholder"
        :disabled="disabled"
        :required="required"
        class="w-full px-4 py-4 pr-12 bg-white border-2 border-gray-200 rounded-xl focus:border-amber-500 focus:ring-4 focus:ring-amber-100 transition-all duration-300 text-lg font-medium hover:border-gray-300 disabled:bg-gray-50 disabled:cursor-not-allowed"
        @focus="handleFocus"
        @blur="handleBlur"
        @input="handleInput"
        @keydown="handleKeydown"
      />

      <!-- Ícone de loading/chevron -->
      <div class="absolute right-4 top-1/2 transform -translate-y-1/2 flex items-center">
        <FontAwesomeIcon
          v-if="loading"
          :icon="['fas', 'spinner']"
          class="text-gray-400 animate-spin mr-2"
        />
        <FontAwesomeIcon
          :icon="['fas', showDropdown ? 'chevron-up' : 'chevron-down']"
          class="text-gray-400 transition-transform duration-200"
        />
      </div>
    </div>

    <!-- 📋 Dropdown com resultados -->
    <Teleport to="body">
      <div
        v-if="showDropdown && (filteredOptions.length > 0 || loading)"
        :style="dropdownStyle"
        class="absolute z-50 w-full max-h-60 bg-white border border-gray-200 rounded-xl shadow-xl overflow-hidden"
      >
        <!-- Estado de loading -->
        <div v-if="loading" class="p-4 text-center text-gray-500">
          <FontAwesomeIcon :icon="['fas', 'spinner']" class="animate-spin mr-2" />
          Carregando...
        </div>

        <!-- Sem resultados -->
        <div v-else-if="searchTerm && searchTerm.length > 0 && filteredOptions && filteredOptions.length === 0" class="p-4 text-center text-gray-500">
          <FontAwesomeIcon :icon="['fas', 'search']" class="mr-2" />
          Nenhum resultado encontrado
        </div>

        <!-- Lista de opções -->
        <div v-else class="max-h-60 overflow-y-auto custom-scrollbar">
          <button
            v-for="(option, index) in filteredOptions"
            :key="option[valueKey]"
            type="button"
            class="w-full px-4 py-3 text-left hover:bg-gray-50 focus:bg-gray-50 focus:outline-none transition-colors duration-150 border-b border-gray-100 last:border-b-0"
            :class="{ 'bg-amber-50': index === selectedIndex }"
            @mousedown.prevent="selectOption(option)"
            @mouseenter="selectedIndex = index"
          >
            <div class="font-medium text-gray-800">{{ option[labelKey] }}</div>
            <div v-if="option[descriptionKey]" class="text-sm text-gray-500 mt-1">
              {{ option[descriptionKey] }}
            </div>
          </button>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted, onUnmounted, watch } from 'vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

interface Props {
  modelValue?: any
  options: any[]
  loading?: boolean
  placeholder?: string
  disabled?: boolean
  required?: boolean
  valueKey?: string
  labelKey?: string
  descriptionKey?: string
  searchFunction?: (term: string) => void
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: 'Digite para buscar...',
  disabled: false,
  required: false,
  valueKey: 'id',
  labelKey: 'name',
  descriptionKey: 'description',
})

const emit = defineEmits<{
  'update:modelValue': [value: any]
  'search': [term: string]
  'select': [option: any]
}>()

// 📊 Estados reativas
const inputRef = ref<HTMLInputElement>()
const searchTerm = ref('')
const showDropdown = ref(false)
const selectedIndex = ref(-1)
const dropdownStyle = ref({})
const isEditing = ref(false)
const selectedOption = ref<any>(null)

// 🧮 Computadas
const displayValue = computed(() => {
  if (isEditing.value) {
    return searchTerm.value
  }
  return selectedOption.value ? selectedOption.value[props.labelKey] : searchTerm.value
})

const filteredOptions = computed(() => {
  if (!props.options || !Array.isArray(props.options)) {
    return []
  }

  // Se não estiver editando e tiver uma seleção, não mostra opções
  if (!isEditing.value && selectedOption.value) {
    return []
  }

  // Se o campo estiver vazio, mostra todas as opções
  if (!searchTerm.value) {
    return props.options.slice(0, 20) // Limita a 20 resultados para performance
  }

  const term = searchTerm.value.toLowerCase()
  return props.options.filter(option => {
    const label = String(option[props.labelKey] || '').toLowerCase()
    const description = String(option[props.descriptionKey] || '').toLowerCase()
    return label.includes(term) || description.includes(term)
  }).slice(0, 20) // Limita a 20 resultados
})

// 🎯 Métodos
const updateDropdownPosition = async (): Promise<void> => {
  await nextTick()
  if (!inputRef.value) return

  const rect = inputRef.value.getBoundingClientRect()
  const dropdownHeight = 240 // max-h-60 = 240px
  const spaceBelow = window.innerHeight - rect.bottom
  const spaceAbove = rect.top

  const shouldShowAbove = spaceBelow < dropdownHeight && spaceAbove > dropdownHeight

  dropdownStyle.value = {
    position: 'fixed',
    left: `${rect.left}px`,
    width: `${rect.width}px`,
    ...(shouldShowAbove
      ? { bottom: `${window.innerHeight - rect.top}px` }
      : { top: `${rect.bottom}px` }
    )
  }
}

const handleFocus = (): void => {
  isEditing.value = true
  showDropdown.value = true
  if (selectedOption.value) {
    searchTerm.value = selectedOption.value[props.labelKey]
  }
  updateDropdownPosition()
}

const handleInput = (): void => {
  selectedIndex.value = -1
  showDropdown.value = true
  isEditing.value = true

  // Se o termo de busca foi limpo completamente
  if (!searchTerm.value) {
    selectedOption.value = null
    emit('update:modelValue', null)
    // Força a atualização do estado de edição
    isEditing.value = false
    nextTick(() => {
      isEditing.value = true
    })
  }

  updateDropdownPosition()
}

const handleBlur = (): void => {
  // Delay para permitir clique nas opções
  setTimeout(() => {
    showDropdown.value = false
    selectedIndex.value = -1
    
    // Se não houver seleção ou o campo estiver vazio, limpa tudo
    if (!selectedOption.value || !searchTerm.value) {
      searchTerm.value = ''
      selectedOption.value = null
      emit('update:modelValue', null)
      isEditing.value = false
    } else {
      // Restaura o texto para o valor selecionado apenas se houver uma seleção válida
      searchTerm.value = selectedOption.value[props.labelKey]
      isEditing.value = false
    }
  }, 200)
}

const handleKeydown = (event: KeyboardEvent): void => {
  switch (event.key) {
    case 'ArrowDown':
      if (!showDropdown.value) {
        showDropdown.value = true
        return
      }
      event.preventDefault()
      selectedIndex.value = Math.min(selectedIndex.value + 1, (filteredOptions.value?.length || 0) - 1)
      break
    case 'ArrowUp':
      event.preventDefault()
      selectedIndex.value = Math.max(selectedIndex.value - 1, -1)
      break
    case 'Enter':
      event.preventDefault()
      if (selectedIndex.value >= 0 && filteredOptions.value[selectedIndex.value]) {
        selectOption(filteredOptions.value[selectedIndex.value])
      }
      break
    case 'Escape':
      event.preventDefault()
      showDropdown.value = false
      selectedIndex.value = -1
      inputRef.value?.blur()
      break
    case 'Backspace':
      if (searchTerm.value === '' && selectedOption.value) {
        // Se o campo está vazio e tem uma seleção, limpa a seleção
        selectedOption.value = null
        emit('update:modelValue', null)
      }
      break
  }
}

const selectOption = (option: any | null): void => {
  if (!option) {
    selectedOption.value = null
    searchTerm.value = ''
    isEditing.value = false
    showDropdown.value = true
    selectedIndex.value = -1
    emit('update:modelValue', null)
    return
  }

  selectedOption.value = option
  const selectedValue = option[props.valueKey]
  searchTerm.value = option[props.labelKey]
  isEditing.value = false
  showDropdown.value = false
  selectedIndex.value = -1
  emit('update:modelValue', selectedValue)
  emit('select', option)
}

// 👀 Watchers
watch(() => props.modelValue, (newValue) => {
  if (newValue && props.options && Array.isArray(props.options)) {
    const option = props.options.find(opt => opt[props.valueKey] === newValue)
    if (option) {
      selectedOption.value = option
      if (!isEditing.value) {
        searchTerm.value = option[props.labelKey]
      }
    }
  } else if (newValue === null) {
    selectedOption.value = null
    if (!isEditing.value) {
      searchTerm.value = ''
    }
  }
}, { immediate: true })

// Watcher para quando as options chegam depois do modelValue
watch(() => props.options, (newOptions) => {
  if (props.modelValue && newOptions && Array.isArray(newOptions)) {
    const option = newOptions.find(opt => opt[props.valueKey] === props.modelValue)
    if (option) {
      selectedOption.value = option
      if (!isEditing.value) {
        searchTerm.value = option[props.labelKey]
      }
    }
  }
}, { immediate: true })

watch(showDropdown, (show) => {
  if (show) {
    updateDropdownPosition()
  }
})

// 🔄 Lifecycle
onMounted(() => {
  window.addEventListener('scroll', updateDropdownPosition)
  window.addEventListener('resize', updateDropdownPosition)
})

onUnmounted(() => {
  window.removeEventListener('scroll', updateDropdownPosition)
  window.removeEventListener('resize', updateDropdownPosition)
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
