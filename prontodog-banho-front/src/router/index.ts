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
      path: '/servicos/:id/editar',
      name: 'servicos-editar',
      component: () => import('../views/ServicosEditarView.vue'),
      props: true,
    },
    {
      path: '/animal-servico',
      name: 'animal-servico',
      component: () => import('../views/AnimalServicoView.vue'),
    },
    {
      path: '/animal-servico/novo',
      name: 'animal-servico-novo',
      component: () => import('../views/AnimalServicoNovoView.vue'),
    },
    {
      path: '/animal-servico/:id',
      name: 'animal-servico-detalhes',
      component: () => import('../views/AnimalServicoDetalhesView.vue'),
      props: true,
    },
    {
      path: '/animais/:id/editar',
      name: 'animais-editar',
      component: () => import('../views/AnimaisEditarView.vue'),
      props: true,
    },
    {
      path: '/clientes/:id/editar',
      name: 'clientes-editar',
      component: () => import('../views/ClientesEditarView.vue'),
      props: true,
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
