# 🎯 Sistema de Pagamento Parcial Proporcional

## 📋 Resumo

Implementamos um sistema completo de **pagamento parcial proporcional** para vendas, onde:

- Pagamentos são distribuídos proporcionalmente entre todos os itens da venda
- Cada item rastreia seu próprio valor pago
- Status do `AnimalServico` reflete automaticamente o pagamento: `em_aberto`, `parcial`, ou `pago`
- Sincronização bidirecional entre venda e itens individuais

---

## 🏗️ Arquitetura

### 1. **Banco de Dados**

- Novo campo `valor_pago_item` em `venda_itens` (rastreia quanto foi pago de cada item)
- Novo status `parcial` para `animal_servico` (além de `em_aberto`, `pago`, `cancelado`)
- Constraint: `valor_pago_item >= 0 AND valor_pago_item <= valor_final_item`

### 2. **Backend Java**

- **`VendaItem.java`**: Métodos para calcular percentual pago, valor pendente, e verificar status
- **`VendaService.java`**: Lógica de distribuição proporcional de pagamentos

---

## 💡 Como Funciona

### Cenário 1: Pagamento Parcial na Venda

**Exemplo:**

- Venda com 2 serviços: Banho (R$ 70) + Tosa (R$ 70) = **R$ 140 total**
- Cliente paga apenas **R$ 70** (50%)

**O que acontece:**

1. Sistema registra baixa de R$ 70 na venda
2. Distribui **proporcionalmente** aos itens:
   - Banho: recebe R$ 35 (70 × 70/140) → **50% pago** → status `parcial`
   - Tosa: recebe R$ 35 (70 × 70/140) → **50% pago** → status `parcial`
3. Venda fica com status `parcial`

**Logs no console:**

```
💾 Baixa manual registrada: R$ 70.00
💰 Valor pago: 0.00 -> 70.00 | Total: 140.00 | isPago: false
💸 Distribuindo R$ 70.00 aos itens...
  📊 Total dos itens: R$ 140.00
  💸 Item #1: R$ 0.00 -> R$ 35.00 (+35.00)
  💸 Item #2: R$ 0.00 -> R$ 35.00 (+35.00)
  🔄 AnimalServico #25: em_aberto -> parcial (50.00%)
  🔄 AnimalServico #26: em_aberto -> parcial (50.00%)
```

---

### Cenário 2: Pagamento do Restante

**Continuando o exemplo anterior:**

- Cliente volta e paga mais **R$ 70**

**O que acontece:**

1. Sistema registra segunda baixa de R$ 70
2. Distribui proporcionalmente:
   - Banho: recebe mais R$ 35 (total **R$ 70**) → **100% pago** → status `pago`
   - Tosa: recebe mais R$ 35 (total **R$ 70**) → **100% pago** → status `pago`
3. Venda fica com status `pago`

---

### Cenário 3: Pagamento Individual de Item

**Exemplo:**

- Mesma venda de R$ 140 (50% já paga)
- Vai no **Banho individual** e marca como "pago"

**O que acontece:**

1. Sistema detecta que o Banho está em uma venda
2. Calcula quanto falta pagar do Banho: R$ 70 - R$ 35 = **R$ 35**
3. Registra baixa automática de **R$ 35** na venda
4. Distribui proporcionalmente:
   - Banho: recebe R$ 17,50 (agora tem **R$ 52,50** = 75% pago)
   - Tosa: recebe R$ 17,50 (agora tem **R$ 52,50** = 75% pago)
5. Ambos ficam com status `parcial` (75%)

**Logs no console:**

```
🔍 Verificando pagamento individual do AnimalServico #25
  📋 Venda #10 encontrada
  💵 Valor pendente do item: R$ 35.00
  💾 Baixa de R$ 35.00 registrada
  💰 Valor pago da venda: 70.00 -> 105.00
💸 Distribuindo R$ 35.00 aos itens...
  💸 Item #1: R$ 35.00 -> R$ 52.50 (+17.50)
  💸 Item #2: R$ 35.00 -> R$ 52.50 (+17.50)
  🔄 AnimalServico #25: parcial -> parcial (75.00%)
  🔄 AnimalServico #26: parcial -> parcial (75.00%)
  ✅ Pagamento individual processado!
```

---

### Cenário 4: Remoção de Pagamento

**Exemplo:**

- Venda com R$ 140 já totalmente paga
- Remove uma baixa de R$ 70

**O que acontece:**

1. Sistema remove a baixa
2. Remove **proporcionalmente** dos itens:
   - Banho: perde R$ 35 (70 → 35) → **50% pago** → status `parcial`
   - Tosa: perde R$ 35 (70 → 35) → **50% pago** → status `parcial`
3. Venda volta para `parcial`

**Logs no console:**

```
🗑️ Removendo baixa #15 da venda #10
  💵 Valor da baixa: 70.00
  ✅ Baixa deletada do banco
  💰 Valor pago antes: 140.00 | depois: 70.00
  📊 Status: parcial
  ↩️ Removendo R$ 70.00 dos itens proporcionalmente...
  ↩️ Item #1: R$ 70.00 -> R$ 35.00 (-35.00)
  ↩️ Item #2: R$ 70.00 -> R$ 35.00 (-35.00)
  🔄 AnimalServico #25: pago -> parcial (50.00%)
  🔄 AnimalServico #26: pago -> parcial (50.00%)
```

---

## 🔄 Sincronização Bidirecional

### Venda → Itens

- **Registra baixa na venda** → Distribui proporcionalmente aos itens → Atualiza status dos `AnimalServico`

### Itens → Venda

- **Marca item como pago** → Registra baixa na venda pelo valor pendente → Distribui aos itens

---

## 📊 Status dos AnimalServico

| Status      | Condição                      | Descrição                 |
| ----------- | ----------------------------- | ------------------------- |
| `em_aberto` | `valor_pago_item == 0`        | Nada foi pago ainda       |
| `parcial`   | `0 < valor_pago_item < total` | Pagamento parcial (1-99%) |
| `pago`      | `valor_pago_item >= total`    | Totalmente pago (100%)    |
| `cancelado` | Manual                        | Serviço cancelado         |

---

## 🎯 Vantagens do Sistema

### ✅ **Flexibilidade Total**

- Cliente pode pagar qualquer valor (R$ 10, R$ 50, R$ 100...)
- Sistema distribui automaticamente entre os itens

### ✅ **Rastreamento Preciso**

- Cada item sabe exatamente quanto foi pago
- Percentual pago visível para cada serviço

### ✅ **Sincronização Automática**

- Pagar na venda → atualiza itens
- Pagar item individual → atualiza venda
- Sempre consistente!

### ✅ **Proteção Contra Erros**

- `valor_pago_item` nunca ultrapassa `valor_final_item`
- `valor_pago` nunca fica negativo
- Constraints do banco garantem integridade

---

## 🧪 Testes Recomendados

### Teste 1: Pagamento Parcial Simples

1. Crie venda com 2 serviços de R$ 70 (total R$ 140)
2. Pague R$ 70 (50%)
3. Verifique: ambos itens devem estar `parcial` com 50%

### Teste 2: Pagamento Completo Gradual

1. Continue do Teste 1
2. Pague mais R$ 70 (agora 100%)
3. Verifique: ambos itens devem estar `pago` com 100%

### Teste 3: Pagamento Individual

1. Crie venda com 2 serviços
2. Não pague nada na venda
3. Vá em um serviço individual e marque como "pago"
4. Verifique: deve criar baixa na venda e distribuir proporcionalmente

### Teste 4: Remoção de Pagamento

1. Crie venda e pague R$ 100 de R$ 140
2. Remova a baixa
3. Verifique: ambos itens devem voltar para `em_aberto`

### Teste 5: Múltiplas Baixas

1. Crie venda de R$ 140
2. Pague R$ 30, depois R$ 40, depois R$ 70
3. Verifique: percentuais devem somar corretamente

---

## 🔍 Debugging

Todos os métodos têm **logs detalhados** no console:

- 💰 Valores de pagamento
- 💸 Distribuição aos itens
- 🔄 Mudanças de status
- ↩️ Remoções de pagamento

**Exemplo de log completo:**

```
💾 Baixa manual registrada: R$ 70.00
💰 Valor pago: 0.00 -> 70.00 | Total: 140.00 | isPago: false
💸 Distribuindo R$ 70.00 aos itens...
  📊 Total dos itens: R$ 140.00
  💸 Item #1: R$ 0.00 -> R$ 35.00 (+35.00)
  💸 Item #2: R$ 0.00 -> R$ 35.00 (+35.00)
  🔄 AnimalServico #25: em_aberto -> parcial (50.00%)
  🔄 AnimalServico #26: em_aberto -> parcial (50.00%)
```

---

## 📝 Próximos Passos (Frontend)

Para mostrar isso na interface:

1. **Na lista de itens da venda:**

   ```
   Banho - Totó
   R$ 70,00 | Pago: R$ 35,00 (50%) | Pendente: R$ 35,00
   Status: PARCIAL
   ```

2. **Barra de progresso por item:**

   ```
   [████████░░░░░░░░] 50%
   ```

3. **Na lista de AnimalServico:**
   - Filtro por status: "Em Aberto", "Parcial", "Pago"
   - Badge colorido: 🟡 Parcial | 🟢 Pago | ⚪ Em Aberto

---

## 🎉 Conclusão

O sistema está **100% funcional** e pronto para uso! Agora você tem:

- ✅ Pagamentos parciais proporcionais
- ✅ Rastreamento individual por item
- ✅ Status `parcial` para serviços
- ✅ Sincronização bidirecional automática
- ✅ Logs detalhados para debugging
- ✅ Proteção contra erros

**Teste e aproveite!** 🚀
