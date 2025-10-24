# 🗑️ Exclusão Real (Hard Delete) - Maquininhas e Formas de Pagamento

## 🐛 PROBLEMA

**Bug:** Ao clicar em "Excluir" maquininha ou forma de pagamento, o sistema apenas **desativava** (soft delete), mas **não excluía** do banco de dados.

**Comportamento anterior:**

- ❌ DELETE /api/maquininhas/1 → Apenas desativa (ativo=false)
- ❌ DELETE /api/formas-pagamento/10 → Apenas desativa (ativo=false)
- ❌ Registros continuam no banco

**Esperado:**

- ✅ DELETE → Remove permanentemente do banco de dados
- ✅ Remove também o registro vinculado (maquininha ↔ forma)

---

## ✅ SOLUÇÃO IMPLEMENTADA

### **Hard Delete (Exclusão Real):**

Agora ao clicar em "Excluir":

- ✅ Remove **permanentemente** do banco de dados
- ✅ Remove **ambos** os registros vinculados (maquininha + forma)
- ✅ Transação atômica (tudo ou nada)

---

## 🔧 MUDANÇAS NO CÓDIGO

### **1. MaquininhaService.java**

#### **Antes (Soft Delete):**

```java
// Excluir (soft delete)
@Transactional
public void excluir(Long id) {
    Maquininha maquininha = buscarPorId(id);
    maquininha.setAtivo(false);  // ❌ Apenas desativa
    maquininhaRepository.save(maquininha);

    // Desativar também a forma de pagamento vinculada
    formaPagamentoRepository.findByMaquininha(maquininha).ifPresent(forma -> {
        forma.setAtivo(false);  // ❌ Apenas desativa
        formaPagamentoRepository.save(forma);
    });
}
```

#### **Depois (Hard Delete):**

```java
// Excluir (hard delete)
@Transactional
public void excluir(Long id) {
    Maquininha maquininha = buscarPorId(id);

    // Excluir também a forma de pagamento vinculada
    formaPagamentoRepository.findByMaquininha(maquininha).ifPresent(forma -> {
        formaPagamentoRepository.delete(forma);  // ✅ Exclui de verdade
    });

    // Excluir maquininha
    maquininhaRepository.delete(maquininha);  // ✅ Exclui de verdade
}
```

---

### **2. FormaPagamentoService.java**

#### **Antes (Misto):**

```java
@Transactional
public void excluir(Long id) {
    FormaPagamento forma = formaPagamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada com ID: " + id));

    // Se tem maquininha vinculada, desativar também
    if (forma.getMaquininha() != null) {
        Maquininha maquininha = forma.getMaquininha();
        maquininha.setAtivo(false);  // ❌ Apenas desativa
        maquininhaRepository.save(maquininha);
    }

    formaPagamentoRepository.deleteById(id);  // ✅ Exclui forma
}
```

#### **Depois (Hard Delete Completo):**

```java
@Transactional
public void excluir(Long id) {
    FormaPagamento forma = formaPagamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada com ID: " + id));

    // Se tem maquininha vinculada, excluir também
    if (forma.getMaquininha() != null) {
        Maquininha maquininha = forma.getMaquininha();
        maquininhaRepository.delete(maquininha);  // ✅ Exclui maquininha
    }

    // Excluir forma de pagamento
    formaPagamentoRepository.delete(forma);  // ✅ Exclui forma
}
```

---

## 📊 FLUXO COMPLETO

### **Fluxo 1: Excluir Maquininha**

```
1️⃣ Usuário: DELETE /api/maquininhas/1
   ↓
2️⃣ MaquininhaService.excluir(1)
   ↓
3️⃣ Busca Maquininha (id=1)
   ├─ nome: "Stone Loja 1"
   └─ ativo: true
   ↓
4️⃣ Busca FormaPagamento vinculada
   ├─ id: 10
   ├─ nome: "Stone Loja 1"
   └─ maquininha_id: 1
   ↓
5️⃣ DELETE FROM formas_pagamento WHERE id = 10
   ✅ Forma excluída do banco!
   ↓
6️⃣ DELETE FROM maquininhas WHERE id = 1
   ✅ Maquininha excluída do banco!
```

**Resultado:**

- ✅ Maquininha **removida** do banco
- ✅ Forma de pagamento **removida** do banco
- ✅ Nenhum registro órfão

---

### **Fluxo 2: Excluir Forma de Pagamento**

```
1️⃣ Usuário: DELETE /api/formas-pagamento/10
   ↓
2️⃣ FormaPagamentoService.excluir(10)
   ↓
3️⃣ Busca FormaPagamento (id=10)
   ├─ nome: "Stone Loja 1"
   └─ maquininha_id: 1
   ↓
4️⃣ Verifica: forma.getMaquininha() != null?
   ✅ SIM! Tem maquininha vinculada
   ↓
5️⃣ Busca Maquininha (id=1)
   ├─ nome: "Stone Loja 1"
   └─ ativo: true
   ↓
6️⃣ DELETE FROM maquininhas WHERE id = 1
   ✅ Maquininha excluída do banco!
   ↓
7️⃣ DELETE FROM formas_pagamento WHERE id = 10
   ✅ Forma excluída do banco!
```

**Resultado:**

- ✅ Forma de pagamento **removida** do banco
- ✅ Maquininha **removida** do banco
- ✅ Nenhum registro órfão

---

## ⚠️ IMPORTANTE: Constraints de Foreign Key

### **Possível Problema:**

Se houver vendas que usaram essa forma de pagamento/maquininha, a exclusão pode **falhar** por constraint de foreign key.

**Exemplo de erro:**

```
ERROR: update or delete on table "formas_pagamento" violates foreign key constraint "fk_venda_baixas_forma_pagamento"
DETAIL: Key (id)=(10) is still referenced from table "venda_baixas".
```

---

### **Solução 1: Desativar ao invés de Excluir**

Se houver vendas vinculadas, use o endpoint de **desativar**:

```bash
# Desativa ao invés de excluir
PUT /api/maquininhas/1/desativar
PUT /api/formas-pagamento/10/desativar
```

**Comportamento:**

- ✅ Maquininha/forma ficam **inativas** (ativo=false)
- ✅ Não aparecem mais na lista de seleção
- ✅ Histórico de vendas permanece íntegro
- ✅ Não quebra constraints

---

### **Solução 2: Adicionar ON DELETE CASCADE**

Para permitir exclusão mesmo com vendas vinculadas, adicione CASCADE nas foreign keys:

```sql
-- Exemplo para venda_baixas
ALTER TABLE banhoetosa.venda_baixas
DROP CONSTRAINT IF EXISTS fk_venda_baixas_forma_pagamento;

ALTER TABLE banhoetosa.venda_baixas
ADD CONSTRAINT fk_venda_baixas_forma_pagamento
FOREIGN KEY (forma_pagamento_id)
REFERENCES banhoetosa.formas_pagamento(id)
ON DELETE SET NULL;  -- Ou ON DELETE CASCADE
```

⚠️ **CUIDADO:** Isso pode excluir vendas em cascata!

---

### **Solução 3: Verificar Antes de Excluir (Recomendado)**

Adicionar validação no serviço:

```java
@Transactional
public void excluir(Long id) {
    Maquininha maquininha = buscarPorId(id);

    // Verificar se há vendas vinculadas
    long vendasCount = vendaBaixaRepository.countByMaquininhaId(id);
    if (vendasCount > 0) {
        throw new IllegalStateException(
            "Não é possível excluir esta maquininha pois há " + vendasCount +
            " pagamentos registrados. Desative-a ao invés de excluir."
        );
    }

    // Continuar com exclusão...
}
```

---

## 🧪 COMO TESTAR

### **Teste 1: Excluir maquininha sem vendas**

```bash
# Criar maquininha de teste
POST /api/maquininhas
{
  "nome": "Teste Exclusão",
  "adquirenteId": 1,
  "contaBancariaId": 1,
  ...
}
# Resposta: { "id": 99, ... }
```

```sql
-- Verificar criação
SELECT * FROM banhoetosa.maquininhas WHERE id = 99;
-- Resultado: id=99, nome="Teste Exclusão", ativo=true

SELECT * FROM banhoetosa.formas_pagamento WHERE maquininha_id = 99;
-- Resultado: id=X, nome="Teste Exclusão", ativo=true
```

```bash
# Excluir
DELETE /api/maquininhas/99
```

```sql
-- Verificar exclusão
SELECT * FROM banhoetosa.maquininhas WHERE id = 99;
-- Resultado: (vazio) ✅

SELECT * FROM banhoetosa.formas_pagamento WHERE maquininha_id = 99;
-- Resultado: (vazio) ✅
```

---

### **Teste 2: Tentar excluir maquininha com vendas**

```bash
# Tentar excluir maquininha que tem vendas
DELETE /api/maquininhas/1
```

**Resultado esperado:**

- ❌ Erro 500 ou constraint violation
- OU
- ✅ Mensagem amigável: "Não é possível excluir. Desative ao invés."

---

### **Teste 3: Desativar ao invés de excluir**

```bash
# Desativar maquininha
PUT /api/maquininhas/1/desativar
```

```sql
-- Verificar
SELECT id, nome, ativo FROM banhoetosa.maquininhas WHERE id = 1;
-- Resultado: id=1, nome="Stone Loja 1", ativo=false ✅

SELECT id, nome, ativo FROM banhoetosa.formas_pagamento WHERE maquininha_id = 1;
-- Resultado: id=10, nome="Stone Loja 1", ativo=false ✅
```

---

## ✅ CHECKLIST DE VALIDAÇÃO

- [x] Excluir maquininha → Remove do banco
- [x] Excluir maquininha → Remove forma de pagamento vinculada
- [x] Excluir forma de pagamento → Remove do banco
- [x] Excluir forma de pagamento → Remove maquininha vinculada
- [ ] Verificar constraints de foreign key (testar com vendas)
- [ ] Adicionar validação se houver vendas vinculadas
- [x] Transações são atômicas

---

## 📝 RESUMO DAS MUDANÇAS

### **Antes:**

```
❌ DELETE → Soft delete (apenas desativa)
❌ Registros continuam no banco
❌ Pode gerar confusão (lista de inativos)
```

### **Depois:**

```
✅ DELETE → Hard delete (remove do banco)
✅ Remove ambos os registros vinculados
✅ Limpa banco de dados de registros indesejados
⚠️ Pode falhar se houver vendas (constraint)
```

---

## 🎯 RECOMENDAÇÃO

**Para produção:**

1. **Manter soft delete** para maquininhas/formas usadas em vendas
2. **Permitir hard delete** apenas para registros sem vendas
3. **Adicionar validação** no serviço antes de excluir
4. **Oferecer opção** de desativar ao invés de excluir

**Implementação sugerida:**

```java
@Transactional
public void excluir(Long id) {
    Maquininha maquininha = buscarPorId(id);

    // Verificar se há vendas
    if (temVendasVinculadas(id)) {
        // Soft delete se tiver vendas
        maquininha.setAtivo(false);
        maquininhaRepository.save(maquininha);

        formaPagamentoRepository.findByMaquininha(maquininha).ifPresent(forma -> {
            forma.setAtivo(false);
            formaPagamentoRepository.save(forma);
        });
    } else {
        // Hard delete se não tiver vendas
        formaPagamentoRepository.findByMaquininha(maquininha).ifPresent(forma -> {
            formaPagamentoRepository.delete(forma);
        });
        maquininhaRepository.delete(maquininha);
    }
}
```

---

## 📚 ARQUIVOS MODIFICADOS

### **Backend:**

- ✅ `MaquininhaService.java`

  - Método `excluir()`: `.save()` → `.delete()` (hard delete)

- ✅ `FormaPagamentoService.java`
  - Método `excluir()`: `.save()` → `.delete()` (hard delete)

---

**Exclusão real (hard delete) implementada! 🗑️**

**IMPORTANTE:** Reinicie o backend para aplicar as mudanças!

```bash
cd prontodog-banho
./mvnw spring-boot:run
```
