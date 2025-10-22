# 🐛 Correção de Bugs - Sistema de Maquininhas

## 📋 BUGS CORRIGIDOS

### **1. Taxa zerada (R$ 0,00)**

**Problema:** Sistema não estava calculando a taxa corretamente.

**Causa:** Não estava usando as bandeiras/taxas cadastradas para aquela maquininha específica.

**Solução:**

- ✅ Carrega taxas da maquininha ao selecioná-la
- ✅ Filtra bandeiras disponíveis baseado nas taxas cadastradas
- ✅ Calcula taxa apenas para combinações válidas (maquininha + bandeira + parcelas)

---

### **2. Bandeiras erradas aparecendo**

**Problema:** Mostrava TODAS as bandeiras do sistema, mesmo as não configuradas.

**Exemplo:** JCB aparecia mesmo sem ter taxa cadastrada para aquela maquininha.

**Solução:**

- ✅ Computed `bandeirasDisponiveis` filtra apenas bandeiras com taxa configurada
- ✅ Filtro considera:
  - Maquininha selecionada
  - Número de parcelas (1x, 2x, 3x, etc)
  - Tipo de transação (à vista ou parcelado)

---

### **3. Tipo de transação confuso**

**Problema:** Usuário selecionava "2x de R$ 150,00" mas podia escolher "Crédito à Vista" manualmente.

**Solução:**

- ✅ **Tipo agora é inferido automaticamente!**
  - 1 parcela → `credito_avista`
  - 2+ parcelas → `credito_parcelado`
- ✅ Removido select de "Tipo de Transação"
- ✅ Adicionado badge informativo mostrando tipo detectado

---

### **4. Bandeira resetando ao mudar parcelas**

**Problema:** Ao mudar número de parcelas, bandeira selecionada permanecia mesmo se não tivesse taxa para novo número.

**Solução:**

- ✅ Watcher limpa bandeira ao mudar parcelas
- ✅ Bandeiras disponíveis são recalculadas automaticamente
- ✅ Mensagem avisa quando não há bandeiras configuradas para aquele número de parcelas

---

## 🏗️ ARQUITETURA DA SOLUÇÃO

### **Fluxo de Dados:**

```
┌─────────────────────────────────────────────────────────────┐
│ 1️⃣ USUÁRIO SELECIONA FORMA DE PAGAMENTO                    │
├─────────────────────────────────────────────────────────────┤
│ Seleciona: "Stone Loja 1"                                  │
│   ↓                                                         │
│ Watcher detecta: forma.maquininhaId = 1                    │
│   ↓                                                         │
│ formPagamento.maquininhaId = 1                             │
│   ↓                                                         │
│ carregarTaxasMaquininha(1)                                 │
│   ↓                                                         │
│ GET /api/maquininhas/1                                     │
│   ↓                                                         │
│ taxasMaquininha = [                                        │
│   { bandeiraId: 1, tipoTransacao: 'credito_avista',       │
│     numeroParcelas: null, taxaPercentual: 3.5 },          │
│   { bandeiraId: 1, tipoTransacao: 'credito_parcelado',    │
│     numeroParcelas: 2, taxaPercentual: 4.0 },             │
│   { bandeiraId: 2, tipoTransacao: 'credito_avista',       │
│     numeroParcelas: null, taxaPercentual: 3.5 },          │
│   ...                                                      │
│ ]                                                          │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ 2️⃣ USUÁRIO SELECIONA NÚMERO DE PARCELAS                    │
├─────────────────────────────────────────────────────────────┤
│ Seleciona: 2x de R$ 150,00                                 │
│   ↓                                                         │
│ formPagamento.numeroParcelas = 2                           │
│   ↓                                                         │
│ tipoTransacaoInferido = 'credito_parcelado'                │
│   ↓                                                         │
│ Watcher limpa: formPagamento.bandeiraId = null             │
│   ↓                                                         │
│ bandeirasDisponiveis recalcula:                            │
│   Filtra taxasMaquininha onde:                             │
│     tipoTransacao = 'credito_parcelado'                    │
│     numeroParcelas = 2                                     │
│   ↓                                                         │
│ Retorna apenas: [                                          │
│   { id: 1, nome: 'Visa' },                                 │
│   { id: 2, nome: 'Mastercard' }                            │
│ ]                                                          │
│   ↓                                                         │
│ JCB NÃO aparece (não tem taxa configurada para 2x)        │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ 3️⃣ USUÁRIO SELECIONA BANDEIRA                              │
├─────────────────────────────────────────────────────────────┤
│ Seleciona: Visa                                            │
│   ↓                                                         │
│ formPagamento.bandeiraId = 1                               │
│   ↓                                                         │
│ Watcher dispara: calcularValorComTaxa()                    │
│   ↓                                                         │
│ GET /api/maquininhas/1/calcular-valor-final                │
│     ?bandeiraId=1                                          │
│     &tipoTransacao=credito_parcelado                       │
│     &numeroParcelas=2                                      │
│     &valorOriginal=300.00                                  │
│   ↓                                                         │
│ Backend busca taxa:                                        │
│   SELECT * FROM maquininha_taxas                           │
│   WHERE maquininha_id = 1                                  │
│     AND bandeira_id = 1                                    │
│     AND tipo_transacao = 'credito_parcelado'               │
│     AND numero_parcelas = 2                                │
│   → taxa = 4.0%                                            │
│   ↓                                                         │
│ Calcula:                                                   │
│   valorOriginal = 300.00                                   │
│   valorTaxa = 300.00 * 4.0% = 12.00                        │
│   valorFinal = 300.00 + 12.00 = 312.00                     │
│   dataRecebimento = hoje + prazo (30 dias)                │
│   ↓                                                         │
│ Retorna:                                                   │
│   {                                                         │
│     valorOriginal: 300.00,                                 │
│     valorTaxa: 12.00,                                      │
│     valorFinal: 312.00,                                    │
│     taxaPercentual: 4.0,                                   │
│     dataRecebimento: "2025-11-19"                          │
│   }                                                         │
│   ↓                                                         │
│ Frontend exibe:                                            │
│   ┌───────────────────────────────┐                        │
│   │ 💰 Resumo do Pagamento        │                        │
│   ├───────────────────────────────┤                        │
│   │ Valor da venda:  R$ 300,00    │                        │
│   │ Taxa (4.0%):     R$ 12,00     │                        │
│   │ ─────────────────────────────  │                        │
│   │ Cliente paga:    R$ 312,00    │                        │
│   │ Você recebe:     R$ 300,00    │                        │
│   │ Recebimento em:  19/11/2025   │                        │
│   └───────────────────────────────┘                        │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 MUDANÇAS NO CÓDIGO

### **Vue Component (`VendaDetalhesView.vue`)**

#### **Novos Refs:**

```typescript
const todasBandeiras = ref<any[]>([]); // Todas as bandeiras do sistema
const taxasMaquininha = ref<any[]>([]); // Taxas da maquininha selecionada
```

#### **Computed Properties:**

##### **1. `tipoTransacaoInferido`**

Infere automaticamente o tipo baseado no número de parcelas:

```typescript
const tipoTransacaoInferido = computed(() => {
  const parcelas = formPagamento.value.numeroParcelas;
  if (parcelas === 1) return "credito_avista";
  else if (parcelas > 1) return "credito_parcelado";
  return "";
});
```

##### **2. `bandeirasDisponiveis`**

Filtra bandeiras baseado em:

- Maquininha selecionada
- Tipo de transação (inferido)
- Número de parcelas

```typescript
const bandeirasDisponiveis = computed(() => {
  if (!formPagamento.value.maquininhaId || taxasMaquininha.value.length === 0) {
    return [];
  }

  const tipoTransacao = tipoTransacaoInferido.value;
  const parcelas = formPagamento.value.numeroParcelas;

  const bandeiraIdsDisponiveis = new Set(
    taxasMaquininha.value
      .filter((taxa) => {
        if (taxa.tipoTransacao !== tipoTransacao) return false;
        if (
          tipoTransacao === "credito_parcelado" &&
          taxa.numeroParcelas !== parcelas
        ) {
          return false;
        }
        return true;
      })
      .map((taxa) => taxa.bandeiraId)
  );

  return todasBandeiras.value.filter((bandeira) =>
    bandeiraIdsDisponiveis.has(bandeira.id)
  );
});
```

#### **Novas Funções:**

##### **`carregarTaxasMaquininha`**

```typescript
const carregarTaxasMaquininha = async (maquininhaId: number) => {
  try {
    const response = await maquininhasService.buscarPorId(maquininhaId);
    taxasMaquininha.value = response.taxas || [];
  } catch (err) {
    console.error("Erro ao carregar taxas da maquininha:", err);
    taxasMaquininha.value = [];
  }
};
```

#### **Watchers Modificados:**

##### **1. Carregar taxas ao selecionar maquininha:**

```typescript
watch(
  () => formPagamento.value.maquininhaId,
  async (novaMaquininhaId) => {
    if (novaMaquininhaId) {
      await carregarTaxasMaquininha(novaMaquininhaId);
    } else {
      taxasMaquininha.value = [];
    }
    formPagamento.value.bandeiraId = null;
    calculoMaquininha.value = null;
  }
);
```

##### **2. Limpar bandeira ao mudar parcelas:**

```typescript
watch(
  () => formPagamento.value.numeroParcelas,
  () => {
    formPagamento.value.bandeiraId = null;
    calculoMaquininha.value = null;
  }
);
```

##### **3. Recalcular ao selecionar bandeira:**

```typescript
watch(
  () => formPagamento.value.bandeiraId,
  () => {
    if (formPagamento.value.maquininhaId && formPagamento.value.bandeiraId) {
      calcularValorComTaxa();
    }
  }
);
```

#### **Função `calcularValorComTaxa` Modificada:**

```typescript
const calcularValorComTaxa = async () => {
  const tipoTransacao = tipoTransacaoInferido.value; // ← USA TIPO INFERIDO

  if (
    !formPagamento.value.maquininhaId ||
    !formPagamento.value.bandeiraId ||
    !tipoTransacao ||
    !venda.value?.valorPendente ||
    venda.value.valorPendente <= 0
  ) {
    calculoMaquininha.value = null;
    return;
  }

  try {
    calculandoValor.value = true;
    const valorOriginal = venda.value.valorPendente;

    const resultado = await maquininhasService.calcularValorFinal(
      formPagamento.value.maquininhaId,
      formPagamento.value.bandeiraId,
      tipoTransacao, // ← USA TIPO INFERIDO
      formPagamento.value.numeroParcelas > 1
        ? formPagamento.value.numeroParcelas
        : null,
      valorOriginal
    );

    calculoMaquininha.value = resultado;
    formPagamento.value.valorBaixa = resultado.valorFinal;
    formPagamento.value.tipoTransacao = tipoTransacao; // ← ATUALIZA FORM
  } catch (error) {
    console.error("Erro ao calcular valor:", error);
    calculoMaquininha.value = null;
  } finally {
    calculandoValor.value = false;
  }
};
```

---

## 🎨 MUDANÇAS NA UI

### **Antes:**

```html
<select v-model.number="formPagamento.bandeiraId">
  <option v-for="bandeira in bandeiras">
    ← TODAS as bandeiras {{ bandeira.nome }}
  </option>
</select>

<select v-model="formPagamento.tipoTransacao">
  ← Campo manual
  <option value="credito_avista">Crédito à Vista</option>
  <option value="credito_parcelado">Crédito Parcelado</option>
</select>
```

### **Depois:**

```html
<select
  v-model.number="formPagamento.bandeiraId"
  :disabled="bandeirasDisponiveis.length === 0"
>
  <option v-for="bandeira in bandeirasDisponiveis">
    ← FILTRADAS {{ bandeira.nome }}
  </option>
</select>

<!-- Campo de tipo REMOVIDO - agora é inferido -->

<div class="bg-blue-50 border border-blue-200 rounded-lg p-3">
  <p class="font-semibold">
    Tipo detectado:
    <span>
      {{ formPagamento.numeroParcelas === 1 ? 'Crédito à Vista' : `Crédito
      Parcelado ${formPagamento.numeroParcelas}x` }}
    </span>
  </p>
  <p class="text-xs">
    O tipo de transação é detectado automaticamente pelo número de parcelas.
  </p>
</div>
```

---

## 🧪 COMO TESTAR

### **1. Testar filtro de bandeiras:**

```bash
# 1. Cadastre maquininha com taxas específicas:
#    - Visa: 1x (3.5%), 2x (4.0%), 3x (4.5%)
#    - Mastercard: 1x (3.5%), 2x (4.0%)
#    - Elo: 1x (3.5%)

# 2. Vá em uma venda
# 3. Registrar pagamento
# 4. Selecione a maquininha
# 5. Teste:
#    - 1x: deve mostrar Visa, Mastercard, Elo
#    - 2x: deve mostrar Visa, Mastercard
#    - 3x: deve mostrar APENAS Visa
#    - 4x: NÃO deve mostrar nenhuma (aviso laranja)
```

### **2. Testar inferência de tipo:**

```bash
# 1. Selecione 1 parcela
#    → Deve mostrar: "Tipo detectado: Crédito à Vista"
# 2. Selecione 2 parcelas
#    → Deve mostrar: "Tipo detectado: Crédito Parcelado 2x"
# 3. Bandeiras disponíveis devem mudar automaticamente
```

### **3. Testar cálculo de taxa:**

```bash
# Cenário:
# - Valor: R$ 300,00
# - Maquininha: Stone Loja 1
# - Bandeira: Visa
# - Parcelas: 2x
# - Taxa cadastrada: 4.0%

# Resultado esperado:
# - Valor da venda:  R$ 300,00
# - Taxa (4.0%):     R$ 12,00
# - Cliente paga:    R$ 312,00
# - Você recebe:     R$ 300,00
```

---

## ✅ CHECKLIST DE VALIDAÇÃO

- [ ] Taxa é calculada corretamente (não mostra R$ 0,00)
- [ ] Bandeiras filtradas aparecem baseado na maquininha
- [ ] Bandeiras mudam ao alterar número de parcelas
- [ ] Tipo é detectado automaticamente (badge azul aparece)
- [ ] Mensagem de aviso aparece quando não há bandeiras para aquele número de parcelas
- [ ] Bandeira é limpa ao mudar parcelas
- [ ] Cálculo é recalculado ao selecionar nova bandeira
- [ ] Preview mostra valores corretos

---

## 🎯 RESULTADO FINAL

### **Antes (com bugs):**

```
❌ Taxa: R$ 0,00 (errado!)
❌ Bandeiras: Todas aparecendo (JCB, Diners, etc)
❌ Tipo: "Crédito à Vista" com 2x selecionado
❌ Confusão total!
```

### **Depois (corrigido):**

```
✅ Taxa: R$ 12,00 (correto!)
✅ Bandeiras: Apenas Visa e Mastercard (configuradas para 2x)
✅ Tipo: "Crédito Parcelado 2x" (inferido automaticamente)
✅ UX perfeita!
```

---

**Todas as correções implementadas com sucesso! 🎉**
