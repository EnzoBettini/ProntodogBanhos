# 🔗 Integração Maquininhas ↔ Formas de Pagamento

## 📋 RESUMO

Implementamos a integração completa entre **Maquininhas** e **Formas de Pagamento**, fazendo com que:

✅ Ao cadastrar uma **maquininha**, uma **forma de pagamento** é criada automaticamente
✅ A forma de pagamento aparece na lista de seleção no lançamento de vendas
✅ Ao selecionar uma forma vinculada à maquininha, os campos específicos aparecem automaticamente
✅ O sistema calcula em tempo real o valor final (com taxa) que o cliente deve pagar
✅ A taxa é sempre repassada ao cliente

---

## 🏗️ ARQUITETURA DA SOLUÇÃO

```
┌─────────────────────────────────────────────────────────────┐
│                   CADASTRO DE MAQUININHA                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. Usuário cadastra "Stone Loja 1"                        │
│  2. Sistema cria a maquininha no BD                        │
│  3. Sistema AUTOMATICAMENTE cria:                          │
│     └─> FormaPagamento                                     │
│         ├─ nome: "Stone Loja 1"                           │
│         ├─ tipo: "credito"                                │
│         ├─ maquininha_id: [ID da maquininha]              │
│         └─ ativo: true                                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                 LANÇAMENTO DE PAGAMENTO                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. Lista de formas de pagamento mostra:                   │
│     - Dinheiro                                             │
│     - PIX                                                  │
│     - Boleto                                               │
│     - Stone Loja 1 ← (criada automaticamente!)            │
│     - Cielo Delivery ← (criada automaticamente!)           │
│                                                             │
│  2. Ao selecionar "Stone Loja 1":                          │
│     ✓ Detecta que tem maquininha_id                        │
│     ✓ Exibe seção "Pagamento via Maquininha"              │
│     ✓ Maquininha já vem pré-selecionada (desabilitada)    │
│     ✓ Usuário seleciona bandeira + tipo de transação      │
│     ✓ Sistema calcula valor final em tempo real           │
│                                                             │
│  3. Preview do cálculo:                                    │
│     Valor da venda:      R$ 100,00                         │
│     + Taxa (3.5%):       R$ 3,50                           │
│     ────────────────────────────────                        │
│     Cliente paga:        R$ 103,50                         │
│     Você recebe:         R$ 100,00                         │
│     Recebimento em:      30/10/2025                        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🗄️ MUDANÇAS NO BANCO DE DADOS

### **Nova Coluna**

```sql
-- Tabela: formas_pagamento
ALTER TABLE banhoetosa.formas_pagamento
ADD COLUMN maquininha_id INTEGER REFERENCES banhoetosa.maquininhas(id);
```

### **Script de Migração**

Execute este script no PostgreSQL:

```bash
psql -U postgres -d prontodog_banhos -f ModelagemBanco/add_maquininha_id_formas_pagamento.sql
```

---

## 🔧 MUDANÇAS NO BACKEND

### **1. Entidade `FormaPagamento.java`**

**Adicionado:**

- Campo `maquininha_id` (relacionamento opcional com `Maquininha`)
- Método `getMaquininhaId()` para serialização
- Método `isMaquininha()` para verificação
- Anotação `@JsonIgnore` para evitar serialização circular

### **2. Repository `FormaPagamentoRepository.java`**

**Adicionado:**

```java
Optional<FormaPagamento> findByMaquininha(Maquininha maquininha);
```

### **3. Service `MaquininhaService.java`**

**Modificado:**

#### **Método `criar()`**

Agora cria automaticamente a `FormaPagamento` ao criar uma maquininha:

```java
// Criar forma de pagamento automaticamente
FormaPagamento formaPagamento = new FormaPagamento();
formaPagamento.setNome(maquininha.getNome());
formaPagamento.setTipo("credito");
formaPagamento.setMaquininha(maquininha);
formaPagamento.setAtivo(true);
formaPagamento.setParcelasMax(12);
formaPagamento.setDiasRecebimento(maquininha.getPrazoRecebimentoCredito());
formaPagamentoRepository.save(formaPagamento);
```

#### **Método `excluir()` e `ativar()`**

Sincronizam o status da maquininha com a forma de pagamento vinculada.

---

## 🎨 MUDANÇAS NO FRONTEND

### **1. Type `FormaPagamento` (`types/api.ts`)**

**Adicionado:**

```typescript
export interface FormaPagamento {
  // ... campos existentes
  maquininhaId?: number | null; // ← NOVO!
}
```

### **2. View `VendaDetalhesView.vue`**

#### **Computed `usaMaquininha`**

Agora verifica se a forma tem `maquininhaId`:

```typescript
const usaMaquininha = computed(() => {
  const forma = formaPagamentoSelecionada.value;
  if (!forma) return false;
  return forma.maquininhaId != null; // ← Verifica se tem maquininha
});
```

#### **Watcher para pré-preencher maquininha**

Quando o usuário seleciona uma forma de pagamento que tem `maquininhaId`, o campo é preenchido automaticamente:

```typescript
watch(
  () => formPagamento.value.formaPagamentoId,
  (novaFormaId) => {
    const forma = formasPagamento.value.find(
      (f) => f.id === Number(novaFormaId)
    );
    if (forma?.maquininhaId) {
      formPagamento.value.maquininhaId = forma.maquininhaId;
    }
  }
);
```

#### **UI do campo Maquininha**

O select da maquininha fica **desabilitado** quando já vem vinculado:

```vue
<select
  v-model.number="formPagamento.maquininhaId"
  :disabled="formaPagamentoSelecionada?.maquininhaId != null"
  :class="[
    'w-full px-4 py-2 border border-gray-300 rounded-lg',
    formaPagamentoSelecionada?.maquininhaId
      ? 'bg-gray-100 cursor-not-allowed'
      : 'focus:ring-2 focus:ring-blue-500'
  ]"
>
```

---

## 🚀 COMO TESTAR

### **1. Execute o script SQL**

```bash
cd ModelagemBanco
psql -U postgres -d prontodog_banhos -f add_maquininha_id_formas_pagamento.sql
```

### **2. Reinicie o backend**

```bash
cd prontodog-banho
./mvnw spring-boot:run
```

### **3. Reinicie o frontend**

```bash
cd prontodog-banho-front
npm run dev
```

### **4. Cadastre uma maquininha**

1. Acesse: `http://localhost:5173/maquininhas/nova`
2. Preencha os dados (Nome: "Stone Loja 1", Adquirente: Stone, etc.)
3. Clique em "Salvar"

### **5. Verifique se a forma de pagamento foi criada**

```sql
SELECT * FROM banhoetosa.formas_pagamento WHERE maquininha_id IS NOT NULL;
```

Deve aparecer:

```
| id | nome          | tipo    | maquininha_id |
|----|---------------|---------|---------------|
| 10 | Stone Loja 1  | credito | 1             |
```

### **6. Lance um pagamento**

1. Vá em uma venda: `http://localhost:5173/vendas/:id`
2. Clique em "Registrar Pagamento"
3. Selecione "Stone Loja 1" na lista de formas
4. Veja a mágica acontecer! 🎩✨
   - Maquininha aparece pré-selecionada
   - Campos de bandeira e tipo aparecem
   - Cálculo em tempo real do valor final

---

## 🎯 FUNCIONAMENTO NA PRÁTICA

### **Fluxo Completo:**

```
┌────────────────────────────────────────────────────────────────┐
│ 1️⃣ CADASTRO                                                    │
├────────────────────────────────────────────────────────────────┤
│ Usuário cadastra "Stone Loja 1"                               │
│   ↓                                                            │
│ Backend cria:                                                  │
│   • Maquininha (id=1, nome="Stone Loja 1")                   │
│   • FormaPagamento (id=10, nome="Stone Loja 1", maq_id=1)    │
└────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│ 2️⃣ LISTAGEM DE FORMAS                                         │
├────────────────────────────────────────────────────────────────┤
│ GET /api/formas-pagamento/ativas                              │
│   ↓                                                            │
│ Retorna:                                                       │
│   [                                                            │
│     { id: 1, nome: "Dinheiro", maquininhaId: null },          │
│     { id: 2, nome: "PIX", maquininhaId: null },               │
│     { id: 10, nome: "Stone Loja 1", maquininhaId: 1 } ← ✨   │
│   ]                                                            │
└────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│ 3️⃣ SELEÇÃO NO FRONTEND                                        │
├────────────────────────────────────────────────────────────────┤
│ Usuário seleciona "Stone Loja 1"                              │
│   ↓                                                            │
│ Watcher detecta: forma.maquininhaId = 1                       │
│   ↓                                                            │
│ formPagamento.maquininhaId = 1 (preenche automaticamente)     │
│   ↓                                                            │
│ usaMaquininha = true (exibe campos)                           │
│   ↓                                                            │
│ Usuário seleciona:                                             │
│   • Bandeira: Visa (id=1)                                     │
│   • Tipo: Crédito à vista                                     │
│   ↓                                                            │
│ Watcher dispara calcularValorComTaxa()                        │
│   ↓                                                            │
│ GET /api/maquininhas/1/calcular-valor-final                   │
│     ?bandeiraId=1                                             │
│     &tipoTransacao=credito_avista                             │
│     &valorOriginal=100                                        │
│   ↓                                                            │
│ Backend retorna:                                               │
│   {                                                            │
│     valorOriginal: 100.00,                                    │
│     valorTaxa: 3.50,                                          │
│     valorFinal: 103.50,  ← Cliente paga                       │
│     dataRecebimento: "2025-10-30"                             │
│   }                                                            │
│   ↓                                                            │
│ Frontend exibe preview:                                        │
│   ┌─────────────────────────────────┐                         │
│   │ 💰 Resumo do Pagamento          │                         │
│   ├─────────────────────────────────┤                         │
│   │ Valor da venda:  R$ 100,00      │                         │
│   │ + Taxa (3.5%):   R$ 3,50        │                         │
│   │ ─────────────────────────────    │                         │
│   │ Cliente paga:    R$ 103,50      │                         │
│   │ Você recebe:     R$ 100,00      │                         │
│   │ Recebimento em:  30/10/2025     │                         │
│   └─────────────────────────────────┘                         │
└────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│ 4️⃣ SALVAMENTO                                                 │
├────────────────────────────────────────────────────────────────┤
│ POST /api/vendas/:id/baixas                                   │
│   {                                                            │
│     formaPagamentoId: 10,                                     │
│     valorBaixa: 103.50,  ← Valor final (com taxa)            │
│     maquininhaId: 1,                                          │
│     bandeiraId: 1,                                            │
│     tipoTransacao: "credito_avista"                           │
│   }                                                            │
│   ↓                                                            │
│ Backend salva em venda_baixas:                                │
│   • forma_pagamento_id = 10                                   │
│   • valor_baixa = 103.50                                      │
│   • maquininha_id = 1                                         │
│   • bandeira_id = 1                                           │
│   • tipo_transacao = "credito_avista"                         │
│   • data_prevista_recebimento = "2025-10-30"                  │
│   • status_recebimento = "pendente"                           │
└────────────────────────────────────────────────────────────────┘
```

---

## ✅ CHECKLIST DE VALIDAÇÃO

Após executar todos os passos, verifique:

- [ ] Script SQL executado sem erros
- [ ] Backend reiniciado e subiu corretamente
- [ ] Ao cadastrar maquininha, forma de pagamento é criada automaticamente
- [ ] Forma de pagamento aparece na lista de seleção em vendas
- [ ] Ao selecionar forma vinculada a maquininha, campos específicos aparecem
- [ ] Campo maquininha fica desabilitado (já pré-selecionado)
- [ ] Ao selecionar bandeira + tipo, cálculo é exibido em tempo real
- [ ] Preview mostra valor original, taxa, valor final e data de recebimento
- [ ] Ao salvar pagamento, dados são persistidos corretamente no BD

---

## 🎓 CONCEITOS IMPORTANTES

### **1. Por que maquininhas são formas de pagamento?**

Cada maquininha (Stone Loja 1, Cielo Delivery) tem:

- ✅ Taxas específicas por bandeira e tipo
- ✅ Prazos de recebimento diferentes
- ✅ Configurações únicas (PIX, antecipação)

Logo, **não faz sentido** ter uma "forma de pagamento genérica Cartão de Crédito" que precisa escolher a maquininha depois.

Faz mais sentido:

- **Stone Loja 1** = forma de pagamento
- **Cielo Delivery** = forma de pagamento
- **Dinheiro** = forma de pagamento

### **2. Por que o campo maquininha fica desabilitado?**

Porque a forma de pagamento **JÁ DEFINE** qual maquininha usar.

Se o usuário quiser usar outra maquininha, ele deve:

1. Criar uma **nova forma de pagamento** vinculada a ela
2. **OU** cadastrar uma nova maquininha (que criará automaticamente a forma)

### **3. Por que sempre repassar a taxa?**

O sistema assume que **você quer receber o valor cheio** da venda.

Se uma venda é R$ 100, você quer R$ 100 na conta.

Logo, se a maquininha cobra 3,5% de taxa:

- **Cliente paga:** R$ 103,50
- **Você recebe:** R$ 100,00
- **Taxa fica com:** maquininha

Se você **NÃO** quiser repassar a taxa:

- Use forma "Dinheiro" ou "Boleto" (sem taxa)
- Ou ajuste manualmente o valor no lançamento

---

## 📚 ARQUIVOS MODIFICADOS

### **Backend:**

- ✅ `FormaPagamento.java` - Adicionado campo `maquininha_id`
- ✅ `MaquininhaService.java` - Criação automática de forma
- ✅ `FormaPagamentoRepository.java` - Novo método `findByMaquininha`

### **Frontend:**

- ✅ `types/api.ts` - Adicionado campo `maquininhaId` em `FormaPagamento`
- ✅ `VendaDetalhesView.vue` - Lógica de detecção e pré-preenchimento

### **SQL:**

- ✅ `add_maquininha_id_formas_pagamento.sql` - Script de migração

---

## 🎉 RESULTADO FINAL

Agora o sistema está **100% integrado**:

```
🔗 Maquininha ↔ Forma de Pagamento ↔ Venda
```

✅ **UX melhorada:** usuário vê "Stone Loja 1" direto na lista
✅ **Automação:** forma criada automaticamente ao cadastrar maquininha
✅ **Cálculo em tempo real:** cliente sabe quanto vai pagar antes de confirmar
✅ **Rastreabilidade:** todos os dados ficam registrados no BD

---

**Implementado com sucesso! 🚀**
