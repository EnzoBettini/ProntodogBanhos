# 📋 Sidebar com Toggle - Como Expandir

## 🚀 Adicionar Novo Item Simples

```vue
<!-- Em TheSidebar.vue -->
<SidebarItem
  title="Relatórios"
  icon="chart-bar"
  to="/relatorios"
  :active="$route.path === '/relatorios'"
/>
```

## 📁 Adicionar Novo Item com Dropdown

```vue
<!-- Em TheSidebar.vue -->
<SidebarDropdown title="Agendamentos" icon="calendar-days" basePath="/agendamentos">
  <SidebarItem title="Lista" to="/agendamentos" />
  <SidebarItem title="Novo Agendamento" to="/agendamentos/novo" />
  <SidebarItem title="Histórico" to="/agendamentos/historico" />
</SidebarDropdown>
```

## 🎯 Adicionar Novo Ícone

1. Importe no `main.ts`:

```ts
import { faNewIcon } from '@fortawesome/free-solid-svg-icons'
library.add(faNewIcon)
```

2. Use no componente:

```vue
<SidebarItem icon="new-icon" title="Novo Item" />
```

## 📂 Estrutura dos Componentes

- `SidebarItem.vue` - Item base (link simples)
- `SidebarDropdown.vue` - Item expansível
- `TheSidebar.vue` - Sidebar principal

## 🔄 Funcionalidade Toggle

A sidebar possui toggle para minimizar/expandir:

```ts
// Usando o store
import { useSidebarStore } from '@/stores/sidebar'

const sidebarStore = useSidebarStore()

// Controles disponíveis
sidebarStore.toggle() // Alterna estado
sidebarStore.open() // Abre sidebar
sidebarStore.close() // Fecha sidebar
sidebarStore.isOpen // Estado atual (true/false)
```

## ✨ Funcionalidades

- ✅ **Toggle suave** (botão X / hamburger)
- ✅ **Ícones sempre visíveis** quando colapsada
- ✅ **Tooltips** nos itens quando minimizada
- ✅ **Ícones Font Awesome**
- ✅ **Highlight automático** da rota ativa
- ✅ **Animações suaves** nos dropdowns
- ✅ **Cores da paleta Verde Natureza**
- ✅ **Totalmente responsivo**
