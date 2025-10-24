# 📚 Guia Didático - Chamadas de API com Axios

Este guia explica como foi configurada a integração com a API de forma simples e organizada.

## 🗂️ Estrutura dos Arquivos

### 1. 📋 Types (`src/types/api.ts`)

Define todas as interfaces TypeScript baseadas nos dados da API:

```typescript
// Estrutura de um cliente
interface Cliente {
  id: number
  codigoClienteSistema: number
  nomeCompleto: string
  cpf: string
  // ... outros campos
}
```

### 2. 🌐 Serviço de API (`src/services/api.ts`)

Contém toda a lógica de comunicação com o backend:

```typescript
// Configuração do axios
const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
})

// Método para buscar clientes
export const clientesService = {
  async buscarTodos(): Promise<ClientesResponse> {
    const response = await api.get<ClientesResponse>('/cliente')
    return response.data
  },
}
```

### 3. 🎨 Componente (`src/views/ClientesView.vue`)

Usa o serviço para exibir os dados:

```vue
<script setup>
import { clientesService } from '@/services/api'

const clientes = ref([])
const loading = ref(false)
const error = ref(null)

const carregarClientes = async () => {
  try {
    loading.value = true
    const response = await clientesService.buscarTodos()
    clientes.value = response
  } catch (err) {
    error.value = 'Erro ao carregar clientes'
  } finally {
    loading.value = false
  }
}
</script>
```

## 🎯 Estados da Aplicação

A página de clientes gerencia 3 estados principais:

1. **⏳ Loading**: Mostra spinner enquanto carrega
2. **❌ Erro**: Mostra mensagem de erro se falhar
3. **✅ Sucesso**: Exibe os dados em cards organizados

## 🔧 Funcionalidades Implementadas

- ✅ Listagem de clientes da API
- ✅ Estados de loading/erro/sucesso
- ✅ Formatação de CPF e telefones
- ✅ Busca por nome/CPF
- ✅ Contadores e badges
- ✅ Design responsivo
- ✅ Logs detalhados no console

## 🚀 Como Testar

1. **Certifique-se que sua API Spring Boot está rodando** em `http://localhost:8080`
2. **Navegue para `/clientes`** no frontend
3. **Verifique o console** para ver os logs detalhados
4. **Teste os estados**:
   - Deixe a API online → vê os dados
   - Desligue a API → vê mensagem de erro
   - Clique "Atualizar" → vê o loading

## 📊 Próximos Passos

Para expandir o sistema, você pode:

1. **Criar outros endpoints** (animais, serviços)
2. **Adicionar paginação** para muitos clientes
3. **Implementar CRUD completo** (criar, editar, deletar)
4. **Adicionar filtros avançados**
5. **Implementar cache** para performance

## 🐛 Debug e Logs

Todos os passos são logados no console:

```javascript
console.log('🔄 Iniciando carregamento de clientes...')
console.log('✅ 2 clientes carregados com sucesso!')
console.error('❌ Erro ao carregar clientes:', error)
```

Abra as **Ferramentas de Desenvolvedor (F12) > Console** para acompanhar!
