// 🎬 Composable para Auto-Animate
// Facilita a aplicação de animações automáticas em listas e containers

import { autoAnimate } from '@formkit/auto-animate'
import { onMounted, type Ref } from 'vue'

/**
 * Hook para aplicar auto-animate em um elemento
 * @param target Referência para o elemento HTML que terá animações
 * @param options Opções de configuração do auto-animate
 */
export function useAutoAnimate(
  target: Ref<HTMLElement | undefined>,
  options?: Parameters<typeof autoAnimate>[1]
) {
  onMounted(() => {
    if (target.value) {
      console.log('🎬 Aplicando auto-animate ao elemento:', target.value)
      autoAnimate(target.value, options)
    }
  })
}

/**
 * Hook específico para listas com configuração otimizada
 * @param target Referência para o container da lista
 */
export function useAutoAnimateList(target: Ref<HTMLElement | undefined>) {
  return useAutoAnimate(target, {
    duration: 300,      // Animação rápida e suave
    easing: 'ease-out'  // Easing natural
  })
}

/**
 * Hook específico para modals e dropdowns
 * @param target Referência para o container do modal/dropdown
 */
export function useAutoAnimateModal(target: Ref<HTMLElement | undefined>) {
  return useAutoAnimate(target, {
    duration: 200,       // Animação mais rápida para modals
    easing: 'ease-in-out'
  })
}
