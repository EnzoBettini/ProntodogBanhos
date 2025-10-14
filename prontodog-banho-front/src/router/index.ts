import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/animais',
      name: 'animais',
      component: () => import('../views/AnimaisView.vue'),
    },
    {
      path: '/animais/novo',
      name: 'animais-novo',
      component: () => import('../views/AnimaisNovoView.vue'),
    },
    {
      path: '/clientes',
      name: 'clientes',
      component: () => import('../views/ClientesView.vue'),
    },
    {
      path: '/clientes/novo',
      name: 'clientes-novo',
      component: () => import('../views/ClientesNovoView.vue'),
    },
    {
      path: '/servicos',
      name: 'servicos',
      component: () => import('../views/ServicosView.vue'),
    },
    {
      path: '/servicos/novo',
      name: 'servicos-novo',
      component: () => import('../views/ServicosNovoView.vue'),
    },
    {
      path: '/configuracoes',
      name: 'configuracoes',
      component: () => import('../views/ConfiguracoesView.vue'),
    },
    {
      path: '/style-guide',
      name: 'style-guide',
      component: () => import('../views/StyleGuideView.vue'),
    },
  ],
})

export default router
