# 💸 Suporte a PIX via Maquininha

## 🐛 PROBLEMA

**Bug:** Ao selecionar uma forma de pagamento via maquininha, não havia opção para selecionar **PIX**. O sistema inferida automaticamente como "Crédito à Vista" ou "Crédito Parcelado" baseado no número de parcelas.

**Problema:**

- ❌ Não tinha como selecionar PIX via maquininha
- ❌ Tipo era inferido automaticamente (1x = crédito à vista, 2+x = crédito parcelado)
- ❌ Não mostrava débito como opção
- ❌ Usuário não tinha controle sobre o tipo de transação

---

## ✅ SOLUÇÃO IMPLEMENTADA

### **Seletor Manual de Tipo de Transação:**

Agora o usuário **escolhe manualmente** o tipo de transação:

- ✅ **Débito**
- ✅ **Crédito à Vista** (apenas se 1 parcela)
- ✅ **Crédito Parcelado** (apenas se 2+ parcelas)
- ✅ **PIX** (apenas se maquininha aceita PIX)

---

## 🔧 MUDANÇAS NO CÓDIGO

### **1. Novo Ref: `tipoTransacaoManual`**

```typescript
const tipoTransacaoManual = ref<string>(""); // Tipo selecionado manualmente
```

---

### **2. Novo Computed: `maquininhaSelecionadaAceitaPix`**

Verifica se a maquininha selecionada aceita PIX:

```typescript
const maquininhaSelecionadaAceitaPix = computed(() => {
  if (!formPagamento.value.maquininhaId) return false;
  const maquininha = maquininhas.value.find(
    (m) => m.id === formPagamento.value.maquininhaId
  );
  return maquininha?.aceitaPix === true;
});
```

---

### **3. Computed `bandeirasDisponiveis` Modificado:**

Agora usa `tipoTransacaoManual` ao invés de `tipoTransacaoInferido`:

```typescript
const bandeirasDisponiveis = computed(() => {
  if (
    !formPagamento.value.maquininhaId ||
    taxasMaquininha.value.length === 0 ||
    !tipoTransacaoManual.value
  ) {
    return [];
  }

  const tipoTransacao = tipoTransacaoManual.value;

  // PIX não precisa de bandeira
  if (tipoTransacao === "pix") {
    return [];
  }

  // ... resto do código
});
```

---

### **4. Função `calcularValorComTaxa` Modificada:**

Agora usa `tipoTransacaoManual` e trata PIX (que não precisa de bandeira):

```typescript
const calcularValorComTaxa = async () => {
  const tipoTransacao = tipoTransacaoManual.value; // ← USO MANUAL
  const valorDigitado = formPagamento.value.valorBaixa || 0;

  // PIX não precisa de bandeira, mas outros sim
  const precisaBandeira = tipoTransacao !== "pix";

  // Só calcula se tiver maquininha, tipo e valor (bandeira só se não for PIX)
  if (
    !formPagamento.value.maquininhaId ||
    !tipoTransacao ||
    (precisaBandeira && !formPagamento.value.bandeiraId) ||
    valorDigitado <= 0
  ) {
    calculoMaquininha.value = null;
    return;
  }

  const resultado = await maquininhasService.calcularValorFinal(
    formPagamento.value.maquininhaId,
    formPagamento.value.bandeiraId || 1, // PIX pode mandar qualquer bandeira (será ignorado)
    tipoTransacao,
    formPagamento.value.numeroParcelas > 1
      ? formPagamento.value.numeroParcelas
      : null,
    valorDigitado
  );

  calculoMaquininha.value = resultado;
  formPagamento.value.tipoTransacao = tipoTransacao;
};
```

---

### **5. Novos Watchers:**

#### **Limpar tipo quando mudar parcelas:**

```typescript
watch(
  () => formPagamento.value.numeroParcelas,
  () => {
    formPagamento.value.bandeiraId = null;
    tipoTransacaoManual.value = ""; // ← LIMPA TIPO
    calculoMaquininha.value = null;
  }
);
```

#### **Limpar bandeira quando mudar para PIX:**

```typescript
watch(
  () => tipoTransacaoManual.value,
  (novoTipo) => {
    // Se mudou para PIX, limpa bandeira
    if (novoTipo === "pix") {
      formPagamento.value.bandeiraId = null;
    }
    calculoMaquininha.value = null;
  }
);
```

#### **Recalcular quando tipo mudar:**

```typescript
watch(
  () => [
    formPagamento.value.bandeiraId,
    formPagamento.value.valorBaixa,
    tipoTransacaoManual.value,
  ],
  () => {
    const tipoTransacao = tipoTransacaoManual.value;
    const precisaBandeira = tipoTransacao !== "pix";

    if (
      formPagamento.value.maquininhaId &&
      tipoTransacao &&
      (!precisaBandeira || formPagamento.value.bandeiraId) &&
      formPagamento.value.valorBaixa > 0
    ) {
      calcularValorComTaxa();
    } else {
      calculoMaquininha.value = null;
    }
  }
);
```

---

## 🎨 MUDANÇAS NA UI

### **Antes (Automático):**

```html
<div class="bg-blue-50 border border-blue-200 rounded-lg p-3">
  <p class="font-semibold">
    Tipo detectado:
    <span>Crédito à Vista</span>
    <!-- ❌ Inferido automaticamente -->
  </p>
  <p class="text-xs">
    O tipo é detectado automaticamente pelo número de parcelas.
  </p>
</div>
```

---

### **Depois (Manual):**

```html
<div>
  <label>Tipo de Transação</label>
  <select v-model="tipoTransacaoManual">
    <option value="">Selecione o tipo...</option>
    <option value="debito">Débito</option>
    <option value="credito_avista" v-if="formPagamento.numeroParcelas === 1">
      Crédito à Vista
    </option>
    <option value="credito_parcelado" v-if="formPagamento.numeroParcelas > 1">
      Crédito Parcelado ({{ formPagamento.numeroParcelas }}x)
    </option>
    <option value="pix" v-if="maquininhaSelecionadaAceitaPix">
      PIX ← ✅ NOVO!
    </option>
  </select>
</div>
```

---

### **Campo de Bandeira Atualizado:**

Agora **esconde** o campo de bandeira quando PIX está selecionado:

```html
<div v-if="formPagamento.maquininhaId && tipoTransacaoManual !== 'pix'">
  <!-- ↑ Não mostra para PIX! -->
  <label>Bandeira do Cartão</label>
  <select v-model.number="formPagamento.bandeiraId">
    <option v-for="bandeira in bandeirasDisponiveis">
      {{ bandeira.nome }}
    </option>
  </select>
</div>
```

---

## 📊 FLUXO COMPLETO

### **Fluxo 1: Pagamento com PIX**

```
1️⃣ Usuário seleciona forma: "Stone Loja 1"
   ↓
2️⃣ Sistema detecta: tem maquininha vinculada
   ↓
3️⃣ Exibe campo "Tipo de Transação"
   Opções:
   - Débito
   - Crédito à Vista (1x)
   - PIX ✅ (maquininha aceita PIX)
   ↓
4️⃣ Usuário seleciona: PIX
   ↓
5️⃣ Campo "Bandeira" é ESCONDIDO (PIX não precisa)
   ↓
6️⃣ Usuário digita: R$ 100,00
   ↓
7️⃣ Sistema busca taxa PIX da maquininha
   GET /api/maquininhas/1/calcular-valor-final
     ?tipoTransacao=pix
     &valorOriginal=100
   ↓
8️⃣ Backend retorna:
   {
     valorOriginal: 100.00,
     valorTaxa: 2.00,      // Taxa PIX: 2%
     valorFinal: 98.00,     // Valor líquido
     dataRecebimento: "2025-10-22" // D+1 (PIX é rápido)
   }
   ↓
9️⃣ Frontend exibe:
   ┌────────────────────────────────┐
   │ 💰 Resumo do Pagamento         │
   ├────────────────────────────────┤
   │ Cliente paga:     R$ 100,00    │
   │ - Taxa (2%):      R$ 2,00      │
   │ ──────────────────────────────  │
   │ Você recebe:      R$ 98,00     │
   │ Recebimento:      22/10/2025   │
   │ Parcelas:         1x de R$ 98  │
   └────────────────────────────────┘
```

---

### **Fluxo 2: Pagamento com Débito**

```
1️⃣ Usuário seleciona: Débito
   ↓
2️⃣ Campo "Bandeira" aparece
   Opções: Visa, Mastercard, Elo (configuradas para débito)
   ↓
3️⃣ Usuário seleciona: Visa
   ↓
4️⃣ Sistema calcula taxa de débito (ex: 2.5%)
   ↓
5️⃣ Preview mostra:
   Cliente paga:  R$ 100,00
   - Taxa (2.5%): R$ 2,50
   Você recebe:   R$ 97,50
```

---

### **Fluxo 3: Maquininha NÃO aceita PIX**

```
1️⃣ Usuário seleciona maquininha que não aceita PIX
   ↓
2️⃣ Campo "Tipo de Transação" mostra:
   - Débito
   - Crédito à Vista (1x)
   - ❌ PIX NÃO APARECE (maquininha não aceita)
```

---

## 🧪 COMO TESTAR

### **Teste 1: PIX via Maquininha**

```bash
# Pré-requisito: Maquininha configurada para aceitar PIX
1. Vá em Vendas
2. Registrar Pagamento
3. Selecione forma vinculada a maquininha que aceita PIX
4. Tipo de Transação: selecione "PIX"
5. Verifique:
   ✅ Campo "Bandeira" NÃO aparece
   ✅ Digite valor: R$ 100
   ✅ Preview mostra taxa PIX
   ✅ Data de recebimento: D+1
```

---

### **Teste 2: Débito**

```bash
1. Selecione forma vinculada a maquininha
2. Tipo de Transação: selecione "Débito"
3. Verifique:
   ✅ Campo "Bandeira" APARECE
   ✅ Opções: apenas bandeiras com taxa para débito
   ✅ Selecione bandeira
   ✅ Preview mostra taxa de débito
```

---

### **Teste 3: Crédito Parcelado**

```bash
1. Selecione forma vinculada a maquininha
2. Parcelas: 2x de R$ 50,00
3. Tipo de Transação: selecione "Crédito Parcelado (2x)"
4. Verifique:
   ✅ Opção "Crédito à Vista" NÃO aparece (1 parcela apenas)
   ✅ Campo "Bandeira" APARECE
   ✅ Opções: apenas bandeiras com taxa para 2x
   ✅ Selecione bandeira
   ✅ Preview mostra taxa de crédito parcelado
```

---

### **Teste 4: Maquininha sem PIX**

```bash
1. Cadastre maquininha com "Aceita PIX: NÃO"
2. Selecione esta maquininha
3. Verifique:
   ✅ Tipo de Transação: NÃO mostra opção "PIX"
   ✅ Apenas: Débito, Crédito à Vista, Crédito Parcelado
```

---

## ✅ CHECKLIST DE VALIDAÇÃO

- [x] Opção PIX aparece se maquininha aceita PIX
- [x] Opção PIX NÃO aparece se maquininha não aceita PIX
- [x] Campo Bandeira é escondido quando PIX selecionado
- [x] Campo Bandeira aparece para débito e crédito
- [x] Tipo é limpo ao mudar parcelas
- [x] Tipo é limpo ao mudar maquininha
- [x] Cálculo funciona para PIX sem bandeira
- [x] Cálculo funciona para outros tipos com bandeira
- [x] Opções de tipo são contextuais (1x vs 2+x)

---

## 🎯 RESUMO

### **Antes:**

```
❌ Tipo inferido automaticamente
❌ Não tinha opção PIX
❌ Não tinha opção Débito
❌ Usuário sem controle
```

### **Depois:**

```
✅ Usuário seleciona manualmente
✅ Opção PIX disponível (se maquininha aceita)
✅ Opção Débito disponível
✅ Opções contextuais (baseado em parcelas)
✅ Campo bandeira esconde para PIX
✅ Cálculo correto para todos os tipos
```

---

## 📝 ARQUIVOS MODIFICADOS

### **Frontend:**

- ✅ `VendaDetalhesView.vue`
  - Adicionado: `tipoTransacaoManual` ref
  - Adicionado: `maquininhaSelecionadaAceitaPix` computed
  - Modificado: `bandeirasDisponiveis` computed (usa tipo manual)
  - Modificado: `calcularValorComTaxa()` (suporta PIX sem bandeira)
  - Adicionado: Watcher para limpar tipo quando mudar parcelas
  - Adicionado: Watcher para limpar bandeira quando PIX
  - Modificado: Template - campo select de tipo
  - Modificado: Template - campo bandeira (esconde para PIX)

---

**Suporte a PIX via Maquininha implementado! 💸**

**Recarregue a página (F5) para ver as mudanças! 🚀**
