<template>
  <nav class="bg-white border-b border-gray-200 shadow-sm w-full">
    <div class="w-full px-4 sm:px-6 lg:px-8">
      <div class="flex justify-between items-center h-16">
        <!-- Logo/Title (lado esquerdo) -->
        <div class="flex items-center">
          <FontAwesomeIcon icon="bath" class="text-primary-500 text-xl mr-3" />
          <h1 class="text-xl font-semibold text-primary-700">
            {{ pageTitle }}
          </h1>
        </div>

        <!-- Menu do Usuário (lado direito) -->
        <div class="flex items-center space-x-4">
          <!-- Notificações (placeholder futuro) -->
          <button
            class="p-2 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-gray-100 transition-colors"
            title="Notificações (Em breve)"
          >
            <FontAwesomeIcon icon="bell" class="w-5 h-5" />
          </button>

          <!-- Dropdown do Usuário -->
          <div class="relative" ref="userMenuRef">
            <button
              @click="toggleUserMenu"
              class="flex items-center space-x-3 p-2 rounded-lg hover:bg-gray-100 transition-colors focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2"
            >
              <!-- Avatar -->
              <div class="w-8 h-8 bg-primary-600 rounded-full flex items-center justify-center">
                <span class="text-sm font-medium text-white">
                  {{ userInitials }}
                </span>
              </div>

              <!-- Nome do usuário -->
              <div class="hidden md:block text-left">
                <p class="text-sm font-medium text-gray-700">{{ currentUser.name }}</p>
                <p class="text-xs text-gray-500">{{ currentUser.role }}</p>
              </div>

              <!-- Ícone dropdown -->
              <FontAwesomeIcon
                :icon="isUserMenuOpen ? 'chevron-up' : 'chevron-down'"
                class="w-4 h-4 text-gray-500"
              />
            </button>

            <!-- Menu Dropdown -->
            <Transition
              enter-active-class="transition ease-out duration-200"
              enter-from-class="transform opacity-0 scale-95"
              enter-to-class="transform opacity-100 scale-100"
              leave-active-class="transition ease-in duration-150"
              leave-from-class="transform opacity-100 scale-100"
              leave-to-class="transform opacity-0 scale-95"
            >
              <div
                v-show="isUserMenuOpen"
                class="absolute right-0 mt-2 w-64 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-50"
              >
                <!-- Informações do usuário -->
                <div class="px-4 py-3 border-b border-gray-200">
                  <div class="flex items-center space-x-3">
                    <div class="w-10 h-10 bg-primary-600 rounded-full flex items-center justify-center">
                      <span class="text-base font-medium text-white">
                        {{ userInitials }}
                      </span>
                    </div>
                    <div>
                      <p class="text-sm font-medium text-gray-900">{{ currentUser.name }}</p>
                      <p class="text-xs text-gray-500">{{ currentUser.email }}</p>
                      <BaseBadge :variant="currentUser.status === 'online' ? 'success' : 'gray'" size="sm" class="mt-1">
                        {{ currentUser.status === 'online' ? 'Online' : 'Offline' }}
                      </BaseBadge>
                    </div>
                  </div>
                </div>

                <!-- Menu Items -->
                <div class="py-1">
                  <button
                    @click="goToProfile"
                    class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                  >
                    <FontAwesomeIcon icon="user" class="w-4 h-4 mr-3 text-gray-400" />
                    Meu Perfil
                  </button>

                  <button
                    @click="goToSettings"
                    class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                  >
                    <FontAwesomeIcon icon="cog" class="w-4 h-4 mr-3 text-gray-400" />
                    Configurações
                  </button>

                  <div class="border-t border-gray-200 my-1"></div>

                  <button
                    @click="logout"
                    class="flex items-center w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
                  >
                    <FontAwesomeIcon icon="sign-out-alt" class="w-4 h-4 mr-3 text-red-500" />
                    Sair
                  </button>
                </div>
              </div>
            </Transition>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import BaseBadge from '@/components/UI/BaseBadge.vue'

// Props
interface Props {
  pageTitle?: string
}

const props = withDefaults(defineProps<Props>(), {
  pageTitle: 'ProntoDog Banhos'
})

// Router
const route = useRoute()
const router = useRouter()

// Estado
const isUserMenuOpen = ref(false)
const userMenuRef = ref<HTMLElement>()

// Mock do usuário atual (futuro: vir de store de autenticação)
const currentUser = ref({
  name: 'João Silva',
  email: 'joao@prontodogbanhos.com',
  role: 'Administrador',
  status: 'online' as 'online' | 'offline',
  avatar: null // futuro: URL da imagem
})

// Computadas
const userInitials = computed(() => {
  return currentUser.value.name
    .split(' ')
    .map(name => name.charAt(0))
    .join('')
    .substring(0, 2)
    .toUpperCase()
})

// Métodos
const toggleUserMenu = () => {
  isUserMenuOpen.value = !isUserMenuOpen.value
}

const closeUserMenu = () => {
  isUserMenuOpen.value = false
}

const goToProfile = () => {
  closeUserMenu()
  // TODO: Implementar rota de perfil
  console.log('Navegando para perfil do usuário')
  // router.push('/perfil')
}

const goToSettings = () => {
  closeUserMenu()
  router.push('/configuracoes')
}

const logout = () => {
  closeUserMenu()

  // TODO: Implementar logout real
  console.log('Fazendo logout...')

  // Placeholder: mostrar confirmação
  if (confirm('Tem certeza que deseja sair?')) {
    // Futuro: limpar store de autenticação
    // authStore.logout()

    // Futuro: redirecionar para login
    // router.push('/login')

    console.log('Usuário deslogado!')
  }
}

// Click fora do menu para fechar
const handleClickOutside = (event: MouseEvent) => {
  if (userMenuRef.value && !userMenuRef.value.contains(event.target as Node)) {
    closeUserMenu()
  }
}

// Lifecycle
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

// Fechar menu quando mudar de rota
import { watch } from 'vue'
watch(route, () => {
  closeUserMenu()
})
</script>
