# 🔧 CORREÇÃO CRÍTICA: Taxa Deve Ser SUBTRAÍDA, Não Somada

## 🐛 ERRO CONCEITUAL

**ESTAVA FAZENDO (ERRADO):**

```
Cliente paga:     R$ 100,00
+ Taxa (1%):      R$ 1,00
─────────────────────────
= Cliente paga:   R$ 101,00  ❌ ERRADO!
Você recebe:      R$ 100,00
```

**CORRETO:**

```
Cliente paga:     R$ 100,00
- Taxa (1%):      R$ 1,00
─────────────────────────
= Você recebe:    R$ 99,00   ✅ CORRETO!
```

---

## 💡 CONCEITO

**Taxa de maquininha é DESPESA, não receita!**

Quando você recebe um pagamento via maquininha:

1. Cliente paga R$ 100,00
2. Maquininha **cobra** taxa de R$ 1,00
3. Você **recebe** R$ 99,00 líquido
4. Os R$ 1,00 ficam com a operadora da maquininha (Stone, Cielo, etc)

---

## 🔧 CORREÇÕES IMPLEMENTADAS

### **1. Backend - `MaquininhaService.java`**

#### **Antes (ERRADO):**

```java
// Calcular valor final (cliente paga valor + taxa)
BigDecimal valorFinal = valorOriginal.add(valorTaxa);  // ❌ SOMANDO!

Map<String, Object> resultado = new HashMap<>();
resultado.put("valorOriginal", valorOriginal);
resultado.put("valorTaxa", valorTaxa);
resultado.put("valorFinal", valorFinal);  // ❌ Valor errado
```

#### **Depois (CORRETO):**

```java
// Calcular valor líquido (valor que você recebe = valor - taxa)
BigDecimal valorLiquido = valorOriginal.subtract(valorTaxa);  // ✅ SUBTRAINDO!

Map<String, Object> resultado = new HashMap<>();
resultado.put("valorOriginal", valorOriginal);     // Valor que cliente paga
resultado.put("valorTaxa", valorTaxa);              // Taxa da maquininha (despesa)
resultado.put("valorFinal", valorLiquido);          // ✅ Valor líquido que você recebe
```

---

### **2. Frontend - `VendaDetalhesView.vue`**

#### **Antes (CONFUSO):**

```html
<div>Valor que você digitou: R$ 100,00</div>
<div>+ Taxa (1%): R$ 1,00</div>
❌ Ícone de MAIS (errado!)
<div>Cliente deve pagar: R$ 101,00</div>
❌ Errado!
<div>Você recebe: R$ 100,00</div>
```

#### **Depois (CLARO):**

```html
<div>Cliente paga: R$ 100,00</div>
<div>- Taxa (1%): R$ 1,00</div>
✅ Ícone de MENOS (correto!)
<div>Você recebe (líquido): R$ 99,00</div>
✅ Correto!
<div>Previsão: 21/10/2025</div>
<div>Parcelas: 2x de R$ 49,50</div>
```

---

## 🎨 MUDANÇAS NA UI

### **Ícones e Cores:**

**Antes:**

```html
<div class="text-orange-700">
  <FontAwesome icon="plus" class="text-xs" /> ❌ Ícone de MAIS Taxa da
  maquininha (1%):
</div>
```

**Depois:**

```html
<div class="text-red-600">
  <!-- Mudou para vermelho (despesa) -->
  <FontAwesome icon="minus" class="text-xs" /> ✅ Ícone de MENOS Taxa da
  maquininha (1%):
</div>
```

---

### **Labels:**

| Antes (Errado)               | Depois (Correto)               |
| ---------------------------- | ------------------------------ |
| "Valor que você digitou"     | "Cliente paga"                 |
| **+** Taxa da maquininha     | **-** Taxa da maquininha       |
| "Cliente deve pagar: R$ 101" | _(removido)_                   |
| "Você recebe: R$ 100"        | "Você recebe (líquido): R$ 99" |

---

### **Novo campo:**

Adicionado linha mostrando valor parcelado:

```html
<div>
  <span>Parcelas:</span>
  <span>2x de R$ 49,50</span>
</div>
```

---

## 📊 EXEMPLOS PRÁTICOS

### **Exemplo 1: Pagamento de R$ 100 com taxa de 1%**

```
┌──────────────────────────────────┐
│ 💰 Resumo do Pagamento           │
├──────────────────────────────────┤
│ Cliente paga:        R$ 100,00   │
│ - Taxa (1%):         R$ 1,00     │
│ ──────────────────────────────   │
│ Você recebe:         R$ 99,00 ✅ │
│ Recebimento:         21/10/2025  │
│ Parcelas:            2x de R$ 49,50 │
└──────────────────────────────────┘
```

**Fluxo do dinheiro:**

1. Cliente passa cartão de R$ 100,00
2. Operadora (Stone/Cielo) retém R$ 1,00 de taxa
3. Você recebe R$ 99,00 na conta
4. Cada parcela: R$ 49,50 (R$ 99 / 2)

---

### **Exemplo 2: Pagamento de R$ 300 com taxa de 3.5%**

```
┌──────────────────────────────────┐
│ 💰 Resumo do Pagamento           │
├──────────────────────────────────┤
│ Cliente paga:        R$ 300,00   │
│ - Taxa (3.5%):       R$ 10,50    │
│ ──────────────────────────────   │
│ Você recebe:         R$ 289,50 ✅│
│ Recebimento:         30/10/2025  │
│ Parcelas:            1x de R$ 289,50 │
└──────────────────────────────────┘
```

**Fluxo do dinheiro:**

1. Cliente passa cartão de R$ 300,00
2. Operadora retém R$ 10,50 de taxa
3. Você recebe R$ 289,50 na conta

---

## 🧮 FÓRMULAS

### **Cálculo correto:**

```
valorLiquido = valorOriginal - valorTaxa
valorTaxa = valorOriginal × (taxaPercentual / 100)
```

**Exemplo:**

```
valorOriginal = 100.00
taxaPercentual = 1.0
valorTaxa = 100.00 × (1.0 / 100) = 1.00
valorLiquido = 100.00 - 1.00 = 99.00
```

---

### **Por parcela:**

```
valorPorParcela = valorLiquido / numeroParcelas
```

**Exemplo (2x):**

```
valorLiquido = 99.00
numeroParcelas = 2
valorPorParcela = 99.00 / 2 = 49.50
```

---

## 🧪 COMO TESTAR

### **Teste 1: Valor simples**

```bash
1. Vá em uma venda
2. Registrar Pagamento
3. Digite: R$ 100,00
4. Selecione maquininha com taxa de 1%
5. Selecione bandeira
6. Verifique:
   ✅ Cliente paga: R$ 100,00
   ✅ Taxa: R$ 1,00 (com ícone MENOS em vermelho)
   ✅ Você recebe: R$ 99,00
   ✅ Parcelas: 2x de R$ 49,50
```

---

### **Teste 2: Diferentes taxas**

| Valor  | Taxa | Resultado Esperado |
| ------ | ---- | ------------------ |
| R$ 100 | 1%   | Recebe: R$ 99,00   |
| R$ 100 | 3.5% | Recebe: R$ 96,50   |
| R$ 300 | 1%   | Recebe: R$ 297,00  |
| R$ 300 | 3.5% | Recebe: R$ 289,50  |
| R$ 50  | 2%   | Recebe: R$ 49,00   |

---

### **Teste 3: Parcelamento**

```bash
1. Digite: R$ 100,00
2. Selecione: 3x de R$ 33,33
3. Taxa: 4.5%
4. Verifique:
   ✅ Cliente paga: R$ 100,00
   ✅ Taxa (4.5%): R$ 4,50
   ✅ Você recebe: R$ 95,50
   ✅ Parcelas: 3x de R$ 31,83
```

---

## ✅ CHECKLIST DE VALIDAÇÃO

- [x] Backend subtrai taxa ao invés de somar
- [x] Frontend mostra ícone de MENOS (não MAIS)
- [x] Cor da taxa é vermelha (despesa)
- [x] Label "Você recebe (líquido)" ao invés de "Cliente paga"
- [x] Valor líquido é calculado corretamente (valor - taxa)
- [x] Parcelas são calculadas sobre valor líquido
- [x] Removido botão "Usar valor final" (não faz mais sentido)

---

## 📝 ARQUIVOS MODIFICADOS

### **Backend:**

- ✅ `MaquininhaService.java`
  - Método `calcularValorComTaxa()`:
    - `valorFinal = valorOriginal.add(valorTaxa)` → **REMOVIDO**
    - `valorLiquido = valorOriginal.subtract(valorTaxa)` → **ADICIONADO**
    - `resultado.put("valorFinal", valorFinal)` → `resultado.put("valorFinal", valorLiquido)`

### **Frontend:**

- ✅ `VendaDetalhesView.vue`
  - Preview do cálculo:
    - Label: "Valor que você digitou" → "Cliente paga"
    - Ícone: `icon="plus"` → `icon="minus"`
    - Cor: `text-orange-700` → `text-red-600`
    - Label: "Cliente deve pagar" → **REMOVIDO**
    - Label: "Você recebe: R$ 100" → "Você recebe (líquido): R$ 99"
    - Linha de parcelas: **ADICIONADA**
    - Botão "Usar valor final": **REMOVIDO**

---

## 🎯 RESULTADO FINAL

### **Antes (ERRADO):**

```
Cliente paga:     R$ 100,00
+ Taxa (1%):      R$ 1,00
─────────────────────────
Cliente paga:     R$ 101,00  ❌
Você recebe:      R$ 100,00  ❌
```

### **Depois (CORRETO):**

```
Cliente paga:     R$ 100,00  ✅
- Taxa (1%):      R$ 1,00    ✅
─────────────────────────
Você recebe:      R$ 99,00   ✅
Parcelas:         2x de R$ 49,50 ✅
```

---

## 💭 CONCEITO IMPORTANTE

**POR QUE A TAXA É SUBTRAÍDA?**

Quando você usa maquininha, você está pagando pela **conveniência**:

- Cliente pode pagar parcelado
- Você recebe mais rápido (30 dias vs 90+ dias de boleto)
- Menos risco de inadimplência
- Automatização do processo

**A operadora cobra por esse serviço!**

Ela retém a taxa **antes** de te repassar o dinheiro:

```
Transação: R$ 100
Operadora retém: R$ 1
Você recebe: R$ 99
```

**NÃO:**

```
Transação: R$ 100
Você cobra do cliente: R$ 101 (valor + taxa)
Você recebe: R$ 100
```

Isso seria **repassar a taxa ao cliente**, o que geralmente não é feito porque:

1. Cliente fechou negócio por R$ 100
2. Cobrar R$ 101 seria "taxa surpresa"
3. Forma de pagamento é escolha sua, não deve encarecer para o cliente

---

**Correção implementada com sucesso! 🎉**

**Agora o sistema reflete corretamente como funciona uma maquininha de cartão! 💳**
