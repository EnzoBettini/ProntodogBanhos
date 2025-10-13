# 📋 Navegação - Componentes de Layout

## 🚀 TheSidebar - Menu Lateral

### Adicionar Novo Item Simples

```vue
<!-- Em TheSidebar.vue -->
<SidebarItem
  title="Relatórios"
  icon="chart-bar"
  to="/relatorios"
  :active="$route.path === '/relatorios'"
/>
```

### Adicionar Novo Item com Dropdown

```vue
<!-- Em TheSidebar.vue -->
<SidebarDropdown title="Agendamentos" icon="calendar-days" basePath="/agendamentos">
  <SidebarItem title="Lista" to="/agendamentos" />
  <SidebarItem title="Novo Agendamento" to="/agendamentos/novo" />
  <SidebarItem title="Histórico" to="/agendamentos/historico" />
</SidebarDropdown>
```

### Controles da Sidebar

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

## 🔝 TheNavbar - Barra Superior

### Uso Básico

```vue
<TheNavbar />
<!-- ou com título personalizado -->
<TheNavbar pageTitle="Minha Página" />
```

### Recursos da Navbar

- ✅ **Menu do usuário** com dropdown
- ✅ **Avatar automático** com iniciais
- ✅ **Notificações** (placeholder futuro)
- ✅ **Logout funcional** com confirmação
- ✅ **Navegação para configurações**
- ✅ **Responsivo** e sticky

### Mock de Usuário

Atualmente usa dados estáticos (futuro: vir de store de autenticação):

```ts
// Em TheNavbar.vue
const currentUser = ref({
  name: 'João Silva',
  email: 'joao@prontodogbanhos.com',
  role: 'Administrador',
  status: 'online',
})
```

## 🎯 Adicionar Novos Ícones

1. Importe no `main.ts`:

```ts
import { faNewIcon } from '@fortawesome/free-solid-svg-icons'
library.add(faNewIcon)
```

2. Use no componente:

```vue
<FontAwesomeIcon icon="new-icon" />
```

## 📂 Estrutura dos Componentes

```
nav/
├── sidebar/
│   ├── SidebarItem.vue      # Item base (link simples)
│   ├── SidebarDropdown.vue  # Item expansível
│   └── TheSidebar.vue       # Sidebar principal
├── TheNavbar.vue            # Navbar superior
├── index.ts                 # Exports
└── README.md               # Este arquivo
```

## ✨ Funcionalidades Sidebar

- ✅ **Toggle suave** (botão X / hamburger)
- ✅ **Ícones sempre visíveis** quando colapsada
- ✅ **Auto-abertura** ao clicar itens quando colapsada
- ✅ **Tooltips** nos itens quando minimizada
- ✅ **Ícones Font Awesome**
- ✅ **Highlight automático** da rota ativa
- ✅ **Animações suaves** nos dropdowns
- ✅ **Cores da paleta Verde Natureza**
- ✅ **Totalmente responsivo**

## ✨ Funcionalidades Navbar

- ✅ **Menu de usuário** com avatar e dropdown
- ✅ **Logout** com confirmação
- ✅ **Link para configurações**
- ✅ **Notificações** (placeholder)
- ✅ **Status online/offline**
- ✅ **Sticky** no topo
- ✅ **Click outside** para fechar menu
- ✅ **Responsivo** (oculta nome em mobile)
