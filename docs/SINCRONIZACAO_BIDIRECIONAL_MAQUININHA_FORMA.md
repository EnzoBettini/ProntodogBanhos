# 🔄 Sincronização Bidirecional: Maquininha ↔ Forma de Pagamento

## 🐛 PROBLEMA

**Bug:** Ao excluir uma forma de pagamento vinculada a uma maquininha, a maquininha **não** era desativada automaticamente.

**Comportamento anterior:**

- ✅ Exclui maquininha → Desativa forma de pagamento (funcionava)
- ❌ Exclui forma de pagamento → Maquininha continua ativa (BUG!)

---

## ✅ SOLUÇÃO IMPLEMENTADA

### **Sincronização bidirecional completa:**

```
Maquininha ↔ Forma de Pagamento
     ↓              ↓
  Desativa      Desativa
     ↓              ↓
  Forma de      Maquininha
  Pagamento
```

Agora funciona nas **duas direções**:

- ✅ Exclui/Desativa **maquininha** → Desativa **forma de pagamento**
- ✅ Exclui/Desativa **forma de pagamento** → Desativa **maquininha**
- ✅ Ativa **maquininha** → Ativa **forma de pagamento**
- ✅ Ativa **forma de pagamento** → Ativa **maquininha**

---

## 🔧 MUDANÇAS NO CÓDIGO

### **Arquivo:** `FormaPagamentoService.java`

#### **1. Injetar `MaquininhaRepository`**

**Antes:**

```java
@Service
public class FormaPagamentoService {
    private final FormaPagamentoRepository formaPagamentoRepository;

    public FormaPagamentoService(FormaPagamentoRepository formaPagamentoRepository) {
        this.formaPagamentoRepository = formaPagamentoRepository;
    }
}
```

**Depois:**

```java
@Service
public class FormaPagamentoService {
    private final FormaPagamentoRepository formaPagamentoRepository;
    private final MaquininhaRepository maquininhaRepository;  // ← ADICIONADO

    public FormaPagamentoService(
            FormaPagamentoRepository formaPagamentoRepository,
            MaquininhaRepository maquininhaRepository  // ← ADICIONADO
    ) {
        this.formaPagamentoRepository = formaPagamentoRepository;
        this.maquininhaRepository = maquininhaRepository;  // ← ADICIONADO
    }
}
```

---

#### **2. Método `excluir()` - Desativa maquininha vinculada**

**Antes:**

```java
public void excluir(Long id) {
    formaPagamentoRepository.deleteById(id);  // ❌ Só exclui forma
}
```

**Depois:**

```java
@Transactional
public void excluir(Long id) {
    FormaPagamento forma = formaPagamentoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada com ID: " + id));

    // Se tem maquininha vinculada, desativar também
    if (forma.getMaquininha() != null) {
        Maquininha maquininha = forma.getMaquininha();
        maquininha.setAtivo(false);
        maquininhaRepository.save(maquininha);
    }

    formaPagamentoRepository.deleteById(id);
}
```

---

#### **3. Método `desativar()` - Desativa maquininha vinculada**

**Antes:**

```java
public void desativar(Long id) {
    formaPagamentoRepository.findById(id).ifPresent(forma -> {
        forma.setAtivo(false);
        formaPagamentoRepository.save(forma);
        // ❌ Não desativa maquininha
    });
}
```

**Depois:**

```java
@Transactional
public void desativar(Long id) {
    formaPagamentoRepository.findById(id).ifPresent(forma -> {
        forma.setAtivo(false);
        formaPagamentoRepository.save(forma);

        // Se tem maquininha vinculada, desativar também
        if (forma.getMaquininha() != null) {
            Maquininha maquininha = forma.getMaquininha();
            maquininha.setAtivo(false);
            maquininhaRepository.save(maquininha);
        }
    });
}
```

---

#### **4. Método `ativar()` - Ativa maquininha vinculada**

**Antes:**

```java
public void ativar(Long id) {
    formaPagamentoRepository.findById(id).ifPresent(forma -> {
        forma.setAtivo(true);
        formaPagamentoRepository.save(forma);
        // ❌ Não ativa maquininha
    });
}
```

**Depois:**

```java
@Transactional
public void ativar(Long id) {
    formaPagamentoRepository.findById(id).ifPresent(forma -> {
        forma.setAtivo(true);
        formaPagamentoRepository.save(forma);

        // Se tem maquininha vinculada, ativar também
        if (forma.getMaquininha() != null) {
            Maquininha maquininha = forma.getMaquininha();
            maquininha.setAtivo(true);
            maquininhaRepository.save(maquininha);
        }
    });
}
```

---

## 📊 FLUXOS COMPLETOS

### **Fluxo 1: Excluir Forma de Pagamento**

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
6️⃣ maquininha.setAtivo(false)
   ↓
7️⃣ maquininhaRepository.save(maquininha)
   ✅ Maquininha desativada!
   ↓
8️⃣ formaPagamentoRepository.deleteById(10)
   ✅ Forma de pagamento excluída!
```

**Resultado:**

- ✅ Forma de pagamento excluída
- ✅ Maquininha desativada automaticamente

---

### **Fluxo 2: Desativar Forma de Pagamento**

```
1️⃣ Usuário: PUT /api/formas-pagamento/10/desativar
   ↓
2️⃣ FormaPagamentoService.desativar(10)
   ↓
3️⃣ Busca FormaPagamento (id=10)
   ├─ nome: "Stone Loja 1"
   ├─ ativo: true
   └─ maquininha_id: 1
   ↓
4️⃣ forma.setAtivo(false)
   ↓
5️⃣ formaPagamentoRepository.save(forma)
   ✅ Forma desativada!
   ↓
6️⃣ Verifica: forma.getMaquininha() != null?
   ✅ SIM! Tem maquininha vinculada
   ↓
7️⃣ maquininha.setAtivo(false)
   ↓
8️⃣ maquininhaRepository.save(maquininha)
   ✅ Maquininha desativada!
```

**Resultado:**

- ✅ Forma de pagamento desativada
- ✅ Maquininha desativada automaticamente

---

### **Fluxo 3: Ativar Forma de Pagamento**

```
1️⃣ Usuário: PUT /api/formas-pagamento/10/ativar
   ↓
2️⃣ FormaPagamentoService.ativar(10)
   ↓
3️⃣ Busca FormaPagamento (id=10)
   ├─ nome: "Stone Loja 1"
   ├─ ativo: false
   └─ maquininha_id: 1
   ↓
4️⃣ forma.setAtivo(true)
   ↓
5️⃣ formaPagamentoRepository.save(forma)
   ✅ Forma ativada!
   ↓
6️⃣ Verifica: forma.getMaquininha() != null?
   ✅ SIM! Tem maquininha vinculada
   ↓
7️⃣ maquininha.setAtivo(true)
   ↓
8️⃣ maquininhaRepository.save(maquininha)
   ✅ Maquininha ativada!
```

**Resultado:**

- ✅ Forma de pagamento ativada
- ✅ Maquininha ativada automaticamente

---

## 🧪 COMO TESTAR

### **Teste 1: Excluir forma de pagamento**

```sql
-- Antes
SELECT id, nome, ativo FROM banhoetosa.formas_pagamento WHERE id = 10;
-- Resultado: id=10, nome="Stone Loja 1", ativo=true

SELECT id, nome, ativo FROM banhoetosa.maquininhas WHERE id = 1;
-- Resultado: id=1, nome="Stone Loja 1", ativo=true
```

```bash
# Excluir forma de pagamento
curl -X DELETE http://localhost:8080/api/formas-pagamento/10
```

```sql
-- Depois
SELECT id, nome, ativo FROM banhoetosa.formas_pagamento WHERE id = 10;
-- Resultado: (vazio - forma foi excluída)

SELECT id, nome, ativo FROM banhoetosa.maquininhas WHERE id = 1;
-- Resultado: id=1, nome="Stone Loja 1", ativo=false ✅
```

---

### **Teste 2: Desativar forma de pagamento**

```bash
# Desativar forma de pagamento
curl -X PUT http://localhost:8080/api/formas-pagamento/10/desativar
```

```sql
-- Verificar
SELECT
  fp.id AS forma_id,
  fp.nome AS forma_nome,
  fp.ativo AS forma_ativa,
  m.id AS maquininha_id,
  m.nome AS maquininha_nome,
  m.ativo AS maquininha_ativa
FROM banhoetosa.formas_pagamento fp
LEFT JOIN banhoetosa.maquininhas m ON fp.maquininha_id = m.id
WHERE fp.id = 10;

-- Resultado esperado:
-- forma_id | forma_nome     | forma_ativa | maquininha_id | maquininha_nome | maquininha_ativa
-- 10       | Stone Loja 1   | false       | 1             | Stone Loja 1    | false ✅
```

---

### **Teste 3: Ativar forma de pagamento**

```bash
# Ativar forma de pagamento
curl -X PUT http://localhost:8080/api/formas-pagamento/10/ativar
```

```sql
-- Verificar
SELECT
  fp.id AS forma_id,
  fp.nome AS forma_nome,
  fp.ativo AS forma_ativa,
  m.id AS maquininha_id,
  m.nome AS maquininha_nome,
  m.ativo AS maquininha_ativa
FROM banhoetosa.formas_pagamento fp
LEFT JOIN banhoetosa.maquininhas m ON fp.maquininha_id = m.id
WHERE fp.id = 10;

-- Resultado esperado:
-- forma_id | forma_nome     | forma_ativa | maquininha_id | maquininha_nome | maquininha_ativa
-- 10       | Stone Loja 1   | true        | 1             | Stone Loja 1    | true ✅
```

---

### **Teste 4: Formas sem maquininha vinculada**

```bash
# Desativar forma de pagamento genérica (ex: Dinheiro)
curl -X PUT http://localhost:8080/api/formas-pagamento/1/desativar
```

**Comportamento esperado:**

- ✅ Forma "Dinheiro" é desativada
- ✅ **NÃO** tenta desativar maquininha (porque não tem)
- ✅ Sem erros

---

## ✅ CHECKLIST DE VALIDAÇÃO

- [x] Excluir forma de pagamento vinculada → desativa maquininha
- [x] Desativar forma de pagamento vinculada → desativa maquininha
- [x] Ativar forma de pagamento vinculada → ativa maquininha
- [x] Operações em formas sem maquininha → funcionam normalmente
- [x] Transações são atômicas (tudo ou nada)
- [x] Sem erros ao desativar forma sem maquininha

---

## 🎯 SINCRONIZAÇÃO COMPLETA

### **Matriz de Sincronização:**

| Ação na Maquininha | Resultado na Forma   | Já Funcionava? |
| ------------------ | -------------------- | -------------- |
| Criar              | Cria automaticamente | ✅ SIM         |
| Desativar          | Desativa             | ✅ SIM         |
| Ativar             | Ativa                | ✅ SIM         |

| Ação na Forma de Pagamento | Resultado na Maquininha | Já Funcionava?    |
| -------------------------- | ----------------------- | ----------------- |
| Criar manual               | - (sem vinculação)      | ✅ SIM            |
| Excluir                    | Desativa maquininha     | ❌ **AGORA SIM!** |
| Desativar                  | Desativa maquininha     | ❌ **AGORA SIM!** |
| Ativar                     | Ativa maquininha        | ❌ **AGORA SIM!** |

---

## 🚨 IMPORTANTE

### **Por que desativa ao invés de excluir?**

Quando uma forma de pagamento vinculada é **excluída**, a maquininha é **desativada** (não excluída) porque:

1. **Histórico:** Vendas antigas podem referenciar a maquininha
2. **Segurança:** Soft delete permite recuperação
3. **Auditoria:** Mantém registros financeiros íntegros

**Se quiser excluir permanentemente:**

1. Exclua a forma de pagamento (desativa maquininha)
2. Depois, exclua manualmente a maquininha no endpoint `/api/maquininhas/{id}`

---

## 📝 ARQUIVOS MODIFICADOS

### **Backend:**

- ✅ `FormaPagamentoService.java`
  - Adicionado: `MaquininhaRepository` no construtor
  - Modificado: `excluir()` - desativa maquininha vinculada
  - Modificado: `desativar()` - desativa maquininha vinculada
  - Modificado: `ativar()` - ativa maquininha vinculada
  - Adicionado: `@Transactional` nos 3 métodos

---

**Sincronização bidirecional implementada com sucesso! 🎉**

**Agora Maquininha ↔ Forma de Pagamento estão sempre sincronizadas! 🔄**
