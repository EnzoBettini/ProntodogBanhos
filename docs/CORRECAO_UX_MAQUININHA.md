# 🎨 Correção de UX - Ordem dos Campos e Lógica de Parcelas

## 🐛 PROBLEMAS IDENTIFICADOS

### **Problema 1: Ordem dos Campos Errada**

❌ Campo "Bandeira do Cartão" aparecia **ANTES** do "Tipo de Transação"

- Não faz sentido, pois bandeira depende do tipo!
- Usuário tinha que selecionar bandeira sem saber qual tipo usaria

### **Problema 2: Débito e PIX Não Podem Ser Parcelados**

❌ Sistema permitia selecionar múltiplas parcelas mesmo com débito ou PIX selecionado

- Débito sempre é à vista (1x)
- PIX sempre é à vista (1x)
- Campo de parcelas deveria estar desabilitado

### **Problema 3: Lógica de Crédito Parcelado**

❌ Não validava se a forma de pagamento permitia parcelamento

- Deveria verificar `parcelasMax > 1` antes de mostrar opção de crédito parcelado

---

## ✅ SOLUÇÕES IMPLEMENTADAS

### **1. Ordem Correta dos Campos**

**Antes:**

```
1. Bandeira do Cartão ← ANTES
2. Tipo de Transação ← DEPOIS
```

**Depois:**

```
1. Tipo de Transação ← PRIMEIRO (lógico!)
2. Bandeira do Cartão ← DEPOIS (depende do tipo)
```

---

### **2. Débito e PIX Travam Parcelas em 1x**

**Comportamento Novo:**

- ✅ Ao selecionar **Débito** ou **PIX**, parcelas é travado em **1x**
- ✅ Campo de parcelas fica **desabilitado** (cinza)
- ✅ Mensagem informativa: "(débito não pode ser parcelado)" ou "(PIX não pode ser parcelado)"

**Fluxo:**

```
1. Usuário seleciona "Débito" ou "PIX"
   ↓
2. Sistema automaticamente:
   - formPagamento.numeroParcelas = 1
   - Desabilita campo de parcelas
   - Limpa bandeira selecionada (se houver)
   ↓
3. Usuário vê:
   [Parcelas: 1x ▼] (desabilitado)
   (débito não pode ser parcelado) OU (PIX não pode ser parcelado)
```

---

### **3. Validação de Crédito Parcelado**

**Computed Properties Criadas:**

```typescript
// Pode usar crédito à vista?
const podeUsarCreditoAVista = computed(() => {
  return formPagamento.value.numeroParcelas === 1;
});

// Pode usar crédito parcelado?
const podeUsarCreditoParcelado = computed(() => {
  const forma = formaPagamentoSelecionada.value;
  // Só pode parcelar se:
  // 1. Tiver mais de 1 parcela selecionada
  // 2. Forma de pagamento permitir parcelamento (parcelasMax > 1)
  return (
    formPagamento.value.numeroParcelas > 1 && (forma?.parcelasMax || 1) > 1
  );
});
```

**No Template:**

```html
<option value="credito_avista" v-if="podeUsarCreditoAVista">
  Crédito à Vista
</option>
<option value="credito_parcelado" v-if="podeUsarCreditoParcelado">
  Crédito Parcelado ({{ formPagamento.numeroParcelas }}x)
</option>
```

---

## 🔧 MUDANÇAS NO CÓDIGO

### **1. Template - Ordem dos Campos Invertida**

**Antes:**

```html
<!-- 1º: Bandeira -->
<div v-if="formPagamento.maquininhaId">
  <label>Bandeira do Cartão</label>
  <select v-model="formPagamento.bandeiraId">
    ...
  </select>
</div>

<!-- 2º: Tipo -->
<div v-if="formPagamento.maquininhaId">
  <label>Tipo de Transação</label>
  <select v-model="tipoTransacaoManual">
    ...
  </select>
</div>
```

**Depois:**

```html
<!-- 1º: Tipo (PRIMEIRO!) -->
<div v-if="formPagamento.maquininhaId">
  <label>Tipo de Transação</label>
  <select v-model="tipoTransacaoManual">
    <option value="">Selecione o tipo...</option>
    <option value="debito">Débito</option>
    <option value="credito_avista" v-if="podeUsarCreditoAVista">
      Crédito à Vista
    </option>
    <option value="credito_parcelado" v-if="podeUsarCreditoParcelado">
      Crédito Parcelado ({{ formPagamento.numeroParcelas }}x)
    </option>
    <option value="pix" v-if="maquininhaSelecionadaAceitaPix">PIX</option>
  </select>
</div>

<!-- 2º: Bandeira (só aparece DEPOIS de selecionar tipo) -->
<div
  v-if="formPagamento.maquininhaId && tipoTransacaoManual && tipoTransacaoManual !== 'pix'"
>
  <label>Bandeira do Cartão</label>
  <select v-model="formPagamento.bandeiraId">
    ...
  </select>
</div>
```

**Mudanças:**

- ✅ Tipo aparece **primeiro**
- ✅ Bandeira só aparece **se tipo estiver selecionado**
- ✅ Bandeira **não aparece** se tipo for PIX

---

### **2. Campo de Parcelas - Desabilita para Débito**

**Antes:**

```html
<select v-model.number="formPagamento.numeroParcelas">
  <option v-for="n in formaPagamentoSelecionada.parcelasMax">
    {{ n }}x de ...
  </option>
</select>
```

**Depois:**

```html
<label>
  Parcelas
  <span v-if="tipoTransacaoManual === 'debito'" class="text-orange-600">
    (débito não pode ser parcelado)
  </span>
  <span v-else-if="tipoTransacaoManual === 'pix'" class="text-orange-600">
    (PIX não pode ser parcelado)
  </span>
</label>
<select
  v-model.number="formPagamento.numeroParcelas"
  :disabled="tipoTransacaoManual === 'debito' || tipoTransacaoManual === 'pix'"
  :class="[
    'w-full px-4 py-2 border rounded-lg',
    tipoTransacaoManual === 'debito' || tipoTransacaoManual === 'pix'
      ? 'bg-gray-100 cursor-not-allowed'
      : 'focus:ring-2 focus:ring-violet-500'
  ]"
>
  <option v-for="n in formaPagamentoSelecionada.parcelasMax">
    {{ n }}x de ...
  </option>
</select>
```

**Mudanças:**

- ✅ `:disabled="tipoTransacaoManual === 'debito' || tipoTransacaoManual === 'pix'"` - desabilita para débito e PIX
- ✅ Classes condicionais - cinza quando desabilitado
- ✅ Mensagem informativa quando débito ou PIX

---

### **3. Watcher - Trava Parcelas quando Débito ou PIX**

**Novo Watcher:**

```typescript
watch(
  () => tipoTransacaoManual.value,
  (novoTipo) => {
    // PIX: limpa bandeira E trava em 1x (PIX não parcela!)
    if (novoTipo === "pix") {
      formPagamento.value.bandeiraId = null;
      formPagamento.value.numeroParcelas = 1; // ← FORÇA 1x
    }

    // DÉBITO: trava em 1 parcela + limpa bandeira
    if (novoTipo === "debito") {
      formPagamento.value.numeroParcelas = 1; // ← FORÇA 1x
      formPagamento.value.bandeiraId = null;
    }

    calculoMaquininha.value = null;
  }
);
```

**Comportamento:**

- ✅ Ao selecionar débito ou PIX → força `numeroParcelas = 1`
- ✅ Limpa bandeira para recalcular
- ✅ Limpa cálculo para forçar recalcular

---

### **4. Computed Properties - Validação de Crédito**

**Adicionados:**

```typescript
// Verificar se pode usar crédito à vista
const podeUsarCreditoAVista = computed(() => {
  return formPagamento.value.numeroParcelas === 1;
});

// Verificar se pode usar crédito parcelado
const podeUsarCreditoParcelado = computed(() => {
  const forma = formaPagamentoSelecionada.value;
  // Só pode parcelar se:
  // 1. Tiver mais de 1 parcela selecionada
  // 2. Forma de pagamento permitir parcelamento (parcelasMax > 1)
  return (
    formPagamento.value.numeroParcelas > 1 && (forma?.parcelasMax || 1) > 1
  );
});
```

**Uso no Template:**

```html
<option value="credito_avista" v-if="podeUsarCreditoAVista">
  Crédito à Vista
</option>
<option value="credito_parcelado" v-if="podeUsarCreditoParcelado">
  Crédito Parcelado ({{ formPagamento.numeroParcelas }}x)
</option>
```

---

## 📊 FLUXOS COMPLETOS

### **Fluxo 1: Pagamento com Débito**

```
1️⃣ Usuário seleciona forma de pagamento via maquininha
   ↓
2️⃣ Aparece campo "Tipo de Transação" (PRIMEIRO!)
   ↓
3️⃣ Usuário seleciona: "Débito"
   ↓
4️⃣ Sistema automaticamente:
   ✅ Trava parcelas em 1x
   ✅ Desabilita campo de parcelas
   ✅ Mostra "(débito não pode ser parcelado)"
   ✅ Limpa bandeira
   ↓
5️⃣ Aparece campo "Bandeira do Cartão" (DEPOIS!)
   ↓
6️⃣ Usuário seleciona bandeira (Visa, Master, Elo...)
   ↓
7️⃣ Sistema calcula taxa de débito
   ↓
8️⃣ Preview mostra:
   Cliente paga:  R$ 100,00
   - Taxa (2.5%): R$ 2,50
   Você recebe:   R$ 97,50
```

---

### **Fluxo 2: Pagamento com Crédito Parcelado**

```
1️⃣ Usuário seleciona forma de pagamento que permite parcelamento
   ↓
2️⃣ Usuário seleciona: 3 parcelas
   ↓
3️⃣ Aparece campo "Tipo de Transação"
   Opções disponíveis:
   - Débito
   - Crédito Parcelado (3x) ✅ (pois parcelasMax > 1)
   - PIX (se maquininha aceita)
   ↓
4️⃣ Usuário seleciona: "Crédito Parcelado (3x)"
   ↓
5️⃣ Aparece campo "Bandeira do Cartão"
   (mostra apenas bandeiras com taxa configurada para 3x)
   ↓
6️⃣ Usuário seleciona bandeira
   ↓
7️⃣ Sistema calcula taxa de crédito parcelado 3x
   ↓
8️⃣ Preview mostra:
   Cliente paga:  R$ 300,00
   - Taxa (5.5%): R$ 16,50
   Você recebe:   R$ 283,50
   Parcelas:      3x de R$ 94,50
```

---

### **Fluxo 3: Tentativa de Parcelar com Forma que Não Permite**

```
1️⃣ Usuário seleciona forma de pagamento com parcelasMax = 1
   (Ex: "Dinheiro", "Boleto à vista")
   ↓
2️⃣ Campo de parcelas NÃO aparece (pois parcelasMax = 1)
   ↓
3️⃣ Aparece "Tipo de Transação" (se for maquininha)
   Opções disponíveis:
   - Débito
   - Crédito à Vista ✅ (única opção de crédito!)
   - ❌ Crédito Parcelado NÃO APARECE (pois parcelasMax = 1)
   - PIX
```

---

## 🧪 COMO TESTAR

### **Teste 1: Débito e PIX (não parcelam)**

```bash
# Teste Débito:
1. Vá em uma venda
2. Registrar Pagamento
3. Selecione forma vinculada a maquininha
4. Valor: R$ 100
5. Parcelas: 2x (apenas para testar)
6. Tipo de Transação: selecione "Débito"
7. Verificar:
   ✅ Campo "Parcelas" volta para 1x automaticamente
   ✅ Campo "Parcelas" fica desabilitado (cinza)
   ✅ Mensagem: "(débito não pode ser parcelado)"
   ✅ Campo "Bandeira" aparece DEPOIS do tipo

# Teste PIX:
1. Parcelas: 2x (apenas para testar)
2. Tipo de Transação: selecione "PIX"
3. Verificar:
   ✅ Campo "Parcelas" volta para 1x automaticamente
   ✅ Campo "Parcelas" fica desabilitado (cinza)
   ✅ Mensagem: "(PIX não pode ser parcelado)"
   ✅ Campo "Bandeira" NÃO aparece (PIX não precisa)
```

---

### **Teste 2: Crédito Parcelado (valida parcelasMax)**

```bash
# Cenário A: Forma permite parcelamento (parcelasMax = 12)
1. Selecione forma com parcelasMax > 1
2. Parcelas: 3x
3. Tipo de Transação:
   ✅ Mostra "Crédito Parcelado (3x)"
4. Selecione crédito parcelado
5. Bandeira aparece DEPOIS
6. Sistema calcula taxa correta

# Cenário B: Forma NÃO permite parcelamento (parcelasMax = 1)
1. Selecione forma com parcelasMax = 1
2. Campo "Parcelas" NÃO aparece
3. Tipo de Transação:
   ✅ Mostra "Crédito à Vista"
   ❌ NÃO mostra "Crédito Parcelado"
```

---

### **Teste 3: Ordem dos Campos**

```bash
1. Selecione forma vinculada a maquininha
2. Verificar ordem:
   1º: Tipo de Transação ✅
   2º: Bandeira do Cartão ✅ (só aparece DEPOIS de selecionar tipo)
3. Selecione "PIX"
4. Verificar:
   ✅ Campo "Bandeira" desaparece (PIX não precisa)
```

---

## ✅ CHECKLIST DE VALIDAÇÃO

- [x] Tipo de Transação aparece ANTES da Bandeira
- [x] Bandeira só aparece DEPOIS de selecionar tipo
- [x] Bandeira não aparece para PIX
- [x] Débito trava parcelas em 1x automaticamente
- [x] PIX trava parcelas em 1x automaticamente
- [x] Campo de parcelas fica desabilitado quando débito ou PIX
- [x] Mensagem "(débito não pode ser parcelado)" aparece
- [x] Mensagem "(PIX não pode ser parcelado)" aparece
- [x] Crédito à Vista só aparece se numeroParcelas = 1
- [x] Crédito Parcelado só aparece se numeroParcelas > 1 E parcelasMax > 1
- [x] Ao selecionar débito ou PIX, limpa bandeira e força recálculo

---

## 🎯 RESUMO DAS MUDANÇAS

### **Template:**

- ✅ Invertida ordem: Tipo → Bandeira
- ✅ Bandeira só aparece se tipo selecionado e não for PIX
- ✅ Campo de parcelas desabilitado quando débito
- ✅ Mensagem informativa quando débito

### **Script:**

- ✅ Adicionado: `podeUsarCreditoAVista` computed
- ✅ Adicionado: `podeUsarCreditoParcelado` computed
- ✅ Modificado: Watcher de `tipoTransacaoManual` para travar parcelas em débito

### **Comportamento:**

- ✅ Débito e PIX forçam 1x automaticamente
- ✅ Crédito parcelado valida `parcelasMax`
- ✅ Ordem lógica dos campos (tipo → bandeira)
- ✅ Feedback visual claro (desabilitado = cinza)
- ✅ Mensagens informativas específicas para cada tipo

---

## 📝 ARQUIVOS MODIFICADOS

### **Frontend:**

- ✅ `VendaDetalhesView.vue`
  - Template: Ordem dos campos invertida (tipo → bandeira)
  - Template: Campo de parcelas com `:disabled` para débito e PIX
  - Template: Mensagens informativas para débito e PIX
  - Template: Validações `v-if` nos tipos de crédito
  - Script: Computed `podeUsarCreditoAVista`
  - Script: Computed `podeUsarCreditoParcelado`
  - Script: Watcher `tipoTransacaoManual` (trava parcelas em débito e PIX)

---

**UX de Maquininha melhorada! Ordem lógica + Débito e PIX não parcelam mais! 🎨**

**Recarregue a página (F5) para ver as mudanças! 🚀**
