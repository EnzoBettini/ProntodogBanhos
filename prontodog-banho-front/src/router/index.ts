import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ClientesView from '../views/ClientesView.vue'
import AnimaisView from '../views/AnimaisView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/clientes', name: 'clientes', component: ClientesView },
    { path: '/animais', name: 'animais', component: AnimaisView }
  ]
})

export default router
