# 🎯 Proposta de Interface - Serviços Adicionais

## 📱 1. Tela de Detalhes do Animal Serviço - Melhorias

### 🔧 Seção "Controle & Status" - Adicionais

Adicionar uma nova seção na coluna "Controle & Status" da tela de detalhes:

```typescript
// Seção de Serviços Adicionais
interface ServicoAdicional {
  id: number;
  servicoAdicionalId: number;
  servicoAdicionalNome: string;
  quantidadeAdicional: number;
  valorUnitario: number;
  valorTotal: number;
  statusPagamento: "em_aberto" | "pago" | "cancelado";
  dataAdicao: string;
  observacoes?: string;
  usuarioNome: string;
}

// Componente da tela de detalhes
const servicosAdicionais = ref<ServicoAdicional[]>([]);
const mostrandoModalAdicionar = ref(false);
const servicosDisponiveis = ref<Servico[]>([]);
```

### 🎨 Layout Visual Proposto

```html
<!-- Nova seção na coluna "Controle & Status" -->
<div
  class="p-4 bg-gradient-to-r from-purple-50 to-indigo-50 rounded-xl border border-purple-200"
>
  <div class="flex items-center justify-between mb-4">
    <div class="flex items-center gap-3">
      <FontAwesome :icon="['fas', 'plus-circle']" class="text-purple-600" />
      <div>
        <p class="text-sm font-medium text-purple-700">Serviços Adicionais</p>
        <p class="text-xs text-purple-600">
          {{ servicosAdicionais.length }} adicional(is)
        </p>
      </div>
    </div>

    <!-- Botão Adicionar -->
    <BaseButton
      @click="mostrarModalAdicionar = true"
      variant="primary"
      size="sm"
      class="bg-purple-500 hover:bg-purple-600"
    >
      <FontAwesome :icon="['fas', 'plus']" class="mr-1" />
      Adicionar
    </BaseButton>
  </div>

  <!-- Lista de Adicionais -->
  <div v-if="servicosAdicionais.length > 0" class="space-y-3">
    <div
      v-for="adicional in servicosAdicionais"
      :key="adicional.id"
      class="flex items-center justify-between p-3 bg-white/70 rounded-lg border border-purple-200"
    >
      <div class="flex-1">
        <div class="flex items-center gap-2 mb-1">
          <p class="font-semibold text-gray-800">
            {{ adicional.servicoAdicionalNome }}
          </p>
          <BaseBadge
            :variant="getStatusVariant(adicional.statusPagamento)"
            size="sm"
          >
            {{ getStatusTexto(adicional.statusPagamento) }}
          </BaseBadge>
        </div>

        <div class="flex items-center gap-4 text-sm text-gray-600">
          <span>Qtd: {{ adicional.quantidadeAdicional }}</span>
          <span>R$ {{ formatarValor(adicional.valorTotal) }}</span>
          <span>{{ formatarDataHora(adicional.dataAdicao) }}</span>
        </div>

        <p v-if="adicional.observacoes" class="text-xs text-gray-500 mt-1">
          {{ adicional.observacoes }}
        </p>
      </div>

      <!-- Ações -->
      <div class="flex items-center gap-2">
        <!-- Alterar status de pagamento -->
        <BaseButton
          @click="alterarStatusAdicional(adicional)"
          variant="ghost"
          size="sm"
          class="text-blue-600 hover:text-blue-700"
        >
          <FontAwesome :icon="['fas', 'dollar-sign']" />
        </BaseButton>

        <!-- Excluir (só se não pago) -->
        <BaseButton
          v-if="adicional.statusPagamento !== 'pago'"
          @click="excluirAdicional(adicional)"
          variant="ghost"
          size="sm"
          class="text-red-600 hover:text-red-700"
        >
          <FontAwesome :icon="['fas', 'trash']" />
        </BaseButton>
      </div>
    </div>
  </div>

  <!-- Resumo Financeiro -->
  <div
    v-if="servicosAdicionais.length > 0"
    class="mt-4 pt-3 border-t border-purple-200"
  >
    <div class="grid grid-cols-2 gap-4 text-sm">
      <div>
        <p class="text-gray-600">Total Adicionais:</p>
        <p class="font-bold text-purple-700">R$ {{ valorTotalAdicionais }}</p>
      </div>
      <div>
        <p class="text-gray-600">Valor Total (c/ adicionais):</p>
        <p class="font-bold text-green-700">R$ {{ valorTotalGeral }}</p>
      </div>
    </div>
  </div>
</div>
```

## 📋 2. Modal de Adicionar Serviço

```html
<BaseModal
  v-model="mostrandoModalAdicionar"
  title="Adicionar Serviço"
  size="lg"
>
  <div class="space-y-6">
    <div>
      <label class="block text-sm font-medium text-gray-700 mb-2">
        <FontAwesome :icon="['fas', 'search']" class="mr-2" />
        Selecionar Serviço Adicional
      </label>

      <SearchSelect
        v-model="formularioAdicional.servicoId"
        :options="servicosDisponiveis"
        option-label="nome"
        option-value="id"
        placeholder="Buscar serviço..."
        @change="atualizarValorServico"
      />
    </div>

    <div class="grid grid-cols-2 gap-4">
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2"
          >Quantidade</label
        >
        <input
          v-model="formularioAdicional.quantidade"
          type="number"
          min="1"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg"
        />
      </div>

      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2"
          >Valor Unitário</label
        >
        <input
          v-model="formularioAdicional.valorUnitario"
          type="number"
          step="0.01"
          class="w-full px-3 py-2 border border-gray-300 rounded-lg"
        />
      </div>
    </div>

    <div>
      <label class="block text-sm font-medium text-gray-700 mb-2"
        >Observações</label
      >
      <textarea
        v-model="formularioAdicional.observacoes"
        rows="3"
        class="w-full px-3 py-2 border border-gray-300 rounded-lg"
        placeholder="Observações sobre este serviço adicional..."
      />
    </div>

    <div class="bg-blue-50 p-4 rounded-lg">
      <div class="flex items-center justify-between">
        <span class="font-medium text-gray-700">Valor Total:</span>
        <span class="text-xl font-bold text-blue-600">
          R$ {{ (formularioAdicional.quantidade *
          formularioAdicional.valorUnitario).toFixed(2) }}
        </span>
      </div>
    </div>

    <div class="flex gap-3 pt-4 border-t">
      <BaseButton
        @click="mostrandoModalAdicionar = false"
        variant="ghost"
        class="flex-1"
      >
        Cancelar
      </BaseButton>
      <BaseButton
        @click="adicionarServico"
        variant="primary"
        :disabled="!podeAdicionarServico"
        class="flex-1"
      >
        <FontAwesome :icon="['fas', 'plus']" class="mr-2" />
        Adicionar Serviço
      </BaseButton>
    </div>
  </div>
</BaseModal>
```

## 📊 3. Dashboard - Métricas de Adicionais

Adicionar novas métricas no dashboard principal:

```typescript
// Métricas para o dashboard
const metricasAdicionais = computed(() => ({
  receitaAdicionais: servicosAdicionais.reduce(
    (acc, s) => acc + (s.isPago ? s.valorTotal : 0),
    0
  ),
  adicionaisEmAberto: servicosAdicionais.filter(
    (s) => s.statusPagamento === "em_aberto"
  ).length,
  servicoAdicionalMaisVendido: getMaisVendido(servicosAdicionais),
  percentualAdicionais:
    (servicosComAdicionais.length / totalServicos.length) * 100,
}));
```

## 🎯 4. Fluxo de UX Proposto

### Cenário: Cliente veio para banho, precisa de tosa

1. **Funcionário acessa** detalhes do serviço de banho
2. **Clica em "Adicionar"** na seção de adicionais
3. **Busca "tosa"** no modal de seleção
4. **Define quantidade e valor** (pode ser diferente da tabela)
5. **Adiciona observação**: "Cliente solicitou durante o banho"
6. **Confirma adição** - adicional aparece na lista
7. **Valor total é recalculado** automaticamente
8. **Status de pagamento** pode ser gerenciado independentemente

### Vantagens desta Abordagem:

✅ **Flexibilidade**: Valores podem variar por situação
✅ **Rastreabilidade**: Histórico completo de quando foi adicionado
✅ **Financeiro**: Controle independente de pagamento
✅ **Relatórios**: Métricas de vendas de adicionais
✅ **UX**: Processo rápido e intuitivo
✅ **Escalabilidade**: Suporte a múltiplos adicionais

## 🔄 5. Integração com Sistema Atual

- **Métricas dashboard** incluem valores de adicionais
- **Relatórios PDF** mostram breakdown de adicionais
- **Busca/filtros** permitem filtrar por serviços com adicionais
- **Notificações** para adicionais em aberto há muito tempo
