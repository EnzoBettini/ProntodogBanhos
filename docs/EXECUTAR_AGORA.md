# 🚀 EXECUTAR AGORA - Guia Rápido

## 📋 3 PASSOS SIMPLES

### **1️⃣ EXECUTAR SQL**

```bash
cd /Users/enzoayresbettini/Desktop/Repos/ProntodogBanhos/ModelagemBanco
psql -U postgres -d prontodog_banhos -f add_maquininha_id_formas_pagamento.sql
```

### **2️⃣ REINICIAR BACKEND**

```bash
cd /Users/enzoayresbettini/Desktop/Repos/ProntodogBanhos/prontodog-banho
./mvnw spring-boot:run
```

### **3️⃣ VERIFICAR FRONTEND**

O frontend já está pronto! Basta acessar:

```
http://localhost:5173/maquininhas/nova
```

---

## ✅ TESTE RÁPIDO

### **Cadastrar maquininha:**

1. Acesse: `http://localhost:5173/maquininhas/nova`
2. Preencha:
   - Nome: "Stone Teste"
   - Adquirente: Stone
   - Conta: (qualquer)
3. Salve

### **Verificar se forma foi criada:**

```sql
SELECT id, nome, tipo, maquininha_id
FROM banhoetosa.formas_pagamento
WHERE maquininha_id IS NOT NULL;
```

Deve mostrar:

```
| id | nome         | tipo    | maquininha_id |
|----|--------------|---------|---------------|
| X  | Stone Teste  | credito | Y             |
```

### **Testar pagamento:**

1. Vá em uma venda existente
2. Clique "Registrar Pagamento"
3. Na lista de formas de pagamento, **procure "Stone Teste"**
4. Selecione ela
5. ✨ **DEVE APARECER:**
   - Campo "Maquininha" (desabilitado, já pré-selecionado)
   - Campo "Bandeira do Cartão"
   - Campo "Tipo de Transação"
6. Selecione bandeira + tipo
7. 🎯 **DEVE CALCULAR AUTOMATICAMENTE:**
   - Valor original
   - Taxa
   - Valor final (que cliente paga)
   - Data de recebimento

---

## 🆘 SE DER ERRO

### **Erro no SQL:**

```
ERROR: column "maquininha_id" already exists
```

**Solução:** A coluna já existe, pode pular o passo 1.

### **Erro no Backend:**

```
Failed to initialize JPA EntityManagerFactory
```

**Solução:**

1. Pare o backend (Ctrl+C)
2. Execute:
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

### **Maquininha não aparece na lista:**

**Verifique:**

1. Forma de pagamento foi criada?
   ```sql
   SELECT * FROM banhoetosa.formas_pagamento WHERE maquininha_id IS NOT NULL;
   ```
2. Forma está ativa?
   ```sql
   SELECT * FROM banhoetosa.formas_pagamento WHERE ativo = true;
   ```

### **Campos de maquininha não aparecem:**

**Verifique no console do navegador:**

```javascript
// Deve ter maquininhaId
console.log(formaPagamentoSelecionada.value.maquininhaId);
```

---

## 📊 VALIDAÇÃO FINAL

Execute este SQL para ver tudo funcionando:

```sql
-- Ver maquininhas cadastradas
SELECT id, nome, ativo FROM banhoetosa.maquininhas;

-- Ver formas de pagamento vinculadas
SELECT
  fp.id,
  fp.nome AS forma_nome,
  fp.tipo,
  fp.maquininha_id,
  m.nome AS maquininha_nome
FROM banhoetosa.formas_pagamento fp
LEFT JOIN banhoetosa.maquininhas m ON fp.maquininha_id = m.id
WHERE fp.ativo = true
ORDER BY fp.maquininha_id NULLS FIRST;

-- Ver pagamentos com maquininha
SELECT
  vb.id,
  vb.valor_baixa,
  fp.nome AS forma_pagamento,
  m.nome AS maquininha,
  b.nome AS bandeira,
  vb.tipo_transacao,
  vb.data_prevista_recebimento
FROM banhoetosa.venda_baixas vb
JOIN banhoetosa.formas_pagamento fp ON vb.forma_pagamento_id = fp.id
LEFT JOIN banhoetosa.maquininhas m ON vb.maquininha_id = m.id
LEFT JOIN banhoetosa.bandeiras b ON vb.bandeira_id = b.id
WHERE vb.maquininha_id IS NOT NULL
ORDER BY vb.created_at DESC
LIMIT 10;
```

---

## ✨ ESTÁ PRONTO!

Agora o sistema:

- ✅ Cria formas de pagamento automaticamente ao cadastrar maquininhas
- ✅ Lista maquininhas junto com formas genéricas
- ✅ Pré-seleciona maquininha ao escolher forma vinculada
- ✅ Calcula taxa em tempo real
- ✅ Mostra preview do valor final

---

**QUALQUER DÚVIDA, ME CHAME! 🚀**
