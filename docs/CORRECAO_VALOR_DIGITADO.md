# 🐛 Correção: Usar Valor Digitado ao Invés de Valor Pendente

## 📋 PROBLEMA

**Bug:** Sistema estava usando o **valor pendente total da venda** para calcular a taxa, ao invés do **valor digitado pelo usuário**.

**Exemplo:**

- Venda total: R$ 300,00
- Usuário digita: R$ 100,00
- Sistema mostrava:
  - ❌ Valor da venda: R$ 300,00 (ERRADO!)
  - ❌ Taxa: R$ 3,00 (calculada sobre R$ 300,00)
  - ❌ Cliente paga: R$ 303,00 (ERRADO!)

**Deveria mostrar:**

- ✅ Valor: R$ 100,00 (o que foi digitado)
- ✅ Taxa: R$ 1,00 (calculada sobre R$ 100,00 com taxa de 1%)
- ✅ Cliente paga: R$ 101,00

---

## 🔧 SOLUÇÃO IMPLEMENTADA

### **1. Usar valor digitado para calcular taxa**

**Antes:**

```typescript
const calcularValorComTaxa = async () => {
  // ❌ Pegava valor pendente total
  const valorOriginal = venda.value.valorPendente;

  const resultado = await maquininhasService.calcularValorFinal(
    // ...
    valorOriginal // ← ERRADO!
  );

  // Atualizava o campo com valor final (sobrescrevia o que usuário digitou)
  formPagamento.value.valorBaixa = resultado.valorFinal;
};
```

**Depois:**

```typescript
const calcularValorComTaxa = async () => {
  // ✅ Pega valor digitado pelo usuário
  const valorDigitado = formPagamento.value.valorBaixa || 0;

  // Só calcula se tiver valor digitado
  if (valorDigitado <= 0) {
    calculoMaquininha.value = null;
    return;
  }

  const resultado = await maquininhasService.calcularValorFinal(
    formPagamento.value.maquininhaId,
    formPagamento.value.bandeiraId,
    tipoTransacao,
    formPagamento.value.numeroParcelas > 1
      ? formPagamento.value.numeroParcelas
      : null,
    valorDigitado // ✅ USA O VALOR DIGITADO!
  );

  calculoMaquininha.value = resultado;

  // NÃO sobrescreve o valor digitado
  // Usuário decide se quer usar o valor final via botão
};
```

---

### **2. Watcher para recalcular ao mudar valor**

**Adicionado:**

```typescript
// Watcher para recalcular quando mudar bandeira OU valor digitado
watch(
  () => [formPagamento.value.bandeiraId, formPagamento.value.valorBaixa],
  () => {
    if (
      formPagamento.value.maquininhaId &&
      formPagamento.value.bandeiraId &&
      formPagamento.value.valorBaixa > 0
    ) {
      calcularValorComTaxa();
    } else {
      calculoMaquininha.value = null;
    }
  }
);
```

**Comportamento:**

- Usuário digita R$ 100 → Calcula
- Usuário muda para R$ 150 → Recalcula automaticamente
- Usuário apaga o valor → Limpa o preview

---

### **3. UI mais clara no preview**

**Labels atualizadas:**

```html
<div class="space-y-2 text-sm">
  <!-- ANTES: "Valor da venda" (confuso!) -->
  <!-- DEPOIS: -->
  <div class="flex justify-between items-center">
    <span class="text-gray-600">Valor que você digitou:</span>
    <span class="font-semibold text-gray-800"
      >{{ formatarMoeda(calculoMaquininha.valorOriginal) }}</span
    >
  </div>

  <div class="flex justify-between items-center text-orange-700">
    <span class="flex items-center gap-1">
      <FontAwesome icon="plus" class="text-xs" />
      Taxa da maquininha ({{ calculoMaquininha.taxaPercentual }}%):
    </span>
    <span class="font-semibold"
      >{{ formatarMoeda(calculoMaquininha.valorTaxa) }}</span
    >
  </div>

  <div
    class="flex justify-between items-center pt-2 border-t-2 border-green-300"
  >
    <span class="text-green-900 font-bold">Cliente deve pagar:</span>
    <span class="font-bold text-lg text-green-900"
      >{{ formatarMoeda(calculoMaquininha.valorFinal) }}</span
    >
  </div>

  <div
    class="flex justify-between items-center text-xs text-gray-600 pt-2 border-t border-green-200"
  >
    <span>Você recebe (líquido):</span>
    <span class="font-semibold"
      >{{ formatarMoeda(calculoMaquininha.valorOriginal) }}</span
    >
  </div>

  <div class="flex justify-between items-center text-xs text-gray-600">
    <span>Previsão de recebimento:</span>
    <span class="font-semibold"
      >{{ formatarData(calculoMaquininha.dataRecebimento) }}</span
    >
  </div>
</div>
```

---

### **4. Botão para usar valor final**

**Novo recurso:** Botão opcional para atualizar o campo com o valor final calculado.

```html
<!-- Botão para usar valor final -->
<div class="mt-3 pt-3 border-t border-green-200">
  <button
    type="button"
    @click="formPagamento.valorBaixa = calculoMaquininha.valorFinal"
    class="w-full px-3 py-2 text-sm bg-green-100 hover:bg-green-200 text-green-800 rounded-lg transition-colors flex items-center justify-center gap-2"
  >
    <FontAwesome icon="check-circle" />
    Usar valor final ({{ formatarMoeda(calculoMaquininha.valorFinal) }})
  </button>
  <p class="text-xs text-gray-500 text-center mt-2">
    Clique para atualizar o campo "Valor do Pagamento" com o valor que o cliente
    deve pagar (incluindo taxa)
  </p>
</div>
```

**Comportamento:**

- Usuário digita R$ 100,00
- Sistema calcula: R$ 100,00 + R$ 1,00 (taxa 1%) = R$ 101,00
- Usuário vê preview: "Cliente deve pagar: R$ 101,00"
- Usuário clica no botão "Usar valor final"
- Campo "Valor do Pagamento" é atualizado para R$ 101,00
- Ao salvar, registra R$ 101,00 como valor pago

---

## 🎯 FLUXO COMPLETO

### **Cenário 1: Pagamento parcial simples**

```
1️⃣ Venda total: R$ 300,00
2️⃣ Usuário quer pagar apenas R$ 100,00 hoje
3️⃣ Digita: R$ 100,00
4️⃣ Seleciona:
   - Maquininha: "Essa tem elo 2% a vista"
   - Bandeira: Elo
   - Parcelas: 2x de R$ 50,00
5️⃣ Sistema calcula:
   ┌─────────────────────────────────┐
   │ 💰 Resumo do Pagamento          │
   ├─────────────────────────────────┤
   │ Valor que você digitou:         │
   │                    R$ 100,00    │
   │ + Taxa (1%):       R$ 1,00      │
   │ ─────────────────────────────   │
   │ Cliente deve pagar: R$ 101,00   │
   │ Você recebe:       R$ 100,00    │
   │ Recebimento:       21/10/2025   │
   └─────────────────────────────────┘
6️⃣ Usuário clica "Usar valor final"
7️⃣ Campo atualiza para R$ 101,00
8️⃣ Salva pagamento de R$ 101,00
9️⃣ Sistema registra:
   - valor_baixa = 101.00
   - Valor distribuído proporcionalmente nos itens
   - Status da venda: "parcial" (ainda falta R$ 199,00)
```

---

### **Cenário 2: Pagamento total**

```
1️⃣ Venda total: R$ 300,00
2️⃣ Usuário quer pagar tudo
3️⃣ Clica "Pagar Total" (preenche R$ 300,00 automaticamente)
4️⃣ Seleciona maquininha e bandeira
5️⃣ Sistema calcula:
   ┌─────────────────────────────────┐
   │ 💰 Resumo do Pagamento          │
   ├─────────────────────────────────┤
   │ Valor que você digitou:         │
   │                    R$ 300,00    │
   │ + Taxa (1%):       R$ 3,00      │
   │ ─────────────────────────────   │
   │ Cliente deve pagar: R$ 303,00   │
   │ Você recebe:       R$ 300,00    │
   │ Recebimento:       21/10/2025   │
   └─────────────────────────────────┘
6️⃣ Usuário clica "Usar valor final"
7️⃣ Campo atualiza para R$ 303,00
8️⃣ Salva pagamento de R$ 303,00
9️⃣ Sistema registra:
   - valor_baixa = 303.00
   - R$ 300 vai para a venda (valor líquido)
   - R$ 3 é a taxa repassada ao cliente
   - Status da venda: "pago"
```

---

## 🧪 COMO TESTAR

### **Teste 1: Valor parcial**

```bash
1. Vá em uma venda com valor pendente de R$ 300,00
2. Registrar Pagamento
3. Digite R$ 100,00
4. Selecione maquininha com taxa de 1%
5. Selecione bandeira Elo
6. Verifique:
   ✅ Preview mostra: "Valor que você digitou: R$ 100,00"
   ✅ Taxa: R$ 1,00
   ✅ Cliente deve pagar: R$ 101,00
   ✅ Você recebe: R$ 100,00
7. Mude para R$ 150,00
8. Verifique:
   ✅ Preview atualiza automaticamente
   ✅ Taxa: R$ 1,50
   ✅ Cliente deve pagar: R$ 151,50
```

---

### **Teste 2: Botão "Usar valor final"**

```bash
1. Digite R$ 100,00
2. Selecione maquininha e bandeira
3. Veja preview: "Cliente deve pagar: R$ 101,00"
4. Clique no botão "Usar valor final (R$ 101,00)"
5. Verifique:
   ✅ Campo "Valor do Pagamento" muda para R$ 101,00
   ✅ Preview recalcula: taxa agora é R$ 1,01
   ✅ Novo valor final: R$ 102,01
   (usuário pode clicar novamente se quiser)
```

---

### **Teste 3: Limpar valor**

```bash
1. Digite R$ 100,00
2. Selecione maquininha e bandeira
3. Veja preview aparecendo
4. Apague o valor do campo
5. Verifique:
   ✅ Preview desaparece
   ✅ Não mostra erro
```

---

## ✅ CHECKLIST DE VALIDAÇÃO

- [x] Taxa é calculada sobre o valor digitado, não sobre o valor pendente total
- [x] Preview mostra "Valor que você digitou" ao invés de "Valor da venda"
- [x] Cálculo atualiza automaticamente ao mudar o valor no campo
- [x] Botão "Usar valor final" atualiza o campo corretamente
- [x] Preview desaparece quando campo está vazio
- [x] Ao salvar, registra o valor correto (o que está no campo)

---

## 📊 COMPARAÇÃO

### **ANTES (Bugado):**

```
Campo: R$ 100,00
Preview:
  Valor da venda: R$ 300,00 ❌
  Taxa (1%): R$ 3,00 ❌
  Cliente paga: R$ 303,00 ❌
```

### **DEPOIS (Correto):**

```
Campo: R$ 100,00
Preview:
  Valor que você digitou: R$ 100,00 ✅
  Taxa (1%): R$ 1,00 ✅
  Cliente deve pagar: R$ 101,00 ✅

  [Botão: Usar valor final (R$ 101,00)]
```

---

**Correção implementada com sucesso! 🎉**
