# ✅ CHECKLIST PRÉ-MERGE - Sistema de Maquininhas

**Data:** 24/10/2025
**Branch:** feature/maquininhas → main

---

## 🔍 Validações Rápidas

### 1️⃣ Compilação
```bash
cd prontodog-banho
mvn clean compile
```
- [ ] ✅ Compila sem erros
- [ ] ✅ Sem warnings críticos

---

### 2️⃣ Testes Unitários (se houver)
```bash
mvn test
```
- [ ] ✅ Todos os testes passam
- [ ] ⚠️ OU: Não há testes (não é bloqueante)

---

### 3️⃣ Banco de Dados

**Scripts SQL executados:**
- [ ] ✅ `sistema_maquininhas.sql` (criação de tabelas)
- [ ] ✅ Migrações aplicadas sem erro
- [ ] ✅ Tabelas criadas:
  - `contas_bancarias`
  - `adquirentes`
  - `bandeiras`
  - `maquininhas`
  - `maquininha_taxas`
- [ ] ✅ Colunas adicionadas em `venda_baixas`:
  - `maquininha_id`
  - `bandeira_id`
  - `tipo_transacao`
  - `data_prevista_recebimento`
  - `data_efetiva_recebimento`
  - `status_recebimento`

---

### 4️⃣ APIs Funcionando

**Endpoints que NÃO podem quebrar:**

#### Vendas (existentes)
```bash
# Criar venda
POST /api/vendas
```
- [ ] ✅ Funciona normalmente

```bash
# Registrar pagamento (baixa)
POST /api/vendas/{id}/baixas
```
- [ ] ✅ Funciona normalmente
- [ ] ✅ Campos de maquininha ficam null (OK por enquanto)
- [ ] ✅ valorTaxa e valorLiquido calculam corretamente

```bash
# Listar vendas
GET /api/vendas
```
- [ ] ✅ Funciona normalmente

---

#### Maquininhas (novas)
```bash
# Listar maquininhas
GET /api/maquininhas
```
- [ ] ✅ Funciona (mesmo que vazio)

```bash
# Criar maquininha
POST /api/maquininhas
```
- [ ] ✅ Funciona
- [ ] ✅ Cria forma de pagamento automaticamente

```bash
# Buscar maquininha por ID
GET /api/maquininhas/{id}
```
- [ ] ✅ Funciona

---

### 5️⃣ Código Limpo

- [ ] ✅ Sem `System.out.println` desnecessários (ou comentados como debug)
- [ ] ✅ Sem código comentado em excesso
- [ ] ✅ Imports organizados
- [ ] ✅ Sem variáveis não utilizadas

---

### 6️⃣ Documentação

- [ ] ✅ README atualizado (se necessário)
- [ ] ✅ Documentação em `/docs` atualizada
- [ ] ✅ Scripts SQL documentados (`ORDEM_EXECUCAO.txt`)

---

### 7️⃣ Git

```bash
# Verificar arquivos modificados
git status

# Verificar diff
git diff main
```

- [ ] ✅ Apenas arquivos relevantes commitados
- [ ] ✅ Sem arquivos de build (`target/`, `node_modules/`)
- [ ] ✅ Mensagens de commit descritivas

---

### 8️⃣ Testes Manuais Críticos

#### Cenário 1: Criar venda e pagar com dinheiro
1. Criar nova venda
2. Adicionar item
3. Registrar baixa (forma: Dinheiro)
4. **Verificar:** Venda paga, valores corretos

- [ ] ✅ Funciona

#### Cenário 2: Criar venda e pagar com cartão (sem maquininha)
1. Criar nova venda
2. Adicionar item
3. Registrar baixa (forma: Cartão Crédito - sem vincular maquininha)
4. **Verificar:** Venda paga, campos de maquininha null

- [ ] ✅ Funciona

#### Cenário 3: Cadastrar maquininha
1. Criar conta bancária
2. Criar maquininha vinculada à conta
3. **Verificar:** Maquininha criada, forma de pagamento criada automaticamente

- [ ] ✅ Funciona

---

### 9️⃣ Performance

- [ ] ✅ Queries não estão com N+1 (verificar logs SQL)
- [ ] ✅ Lazy loading funcionando corretamente
- [ ] ✅ Sem memory leaks aparentes

---

### 🔟 Rollback Plan

**Se algo der errado após merge:**

```bash
# Reverter merge
git revert <commit-hash>

# OU fazer rollback no banco
psql -U usuario -d banco < backup_pre_merge.sql
```

- [ ] ✅ Backup do banco criado antes do merge
- [ ] ✅ Sei como reverter se necessário

---

## 🚦 Decisão Final

### ✅ APROVAR MERGE se:
- Todos os itens marcados ✅
- Sistema funciona normalmente
- Sem erros de compilação
- Testes manuais OK

### ⚠️ REVISAR se:
- 1-2 itens falharam mas não são críticos
- Warnings de compilação não resolvidos

### ❌ NÃO MERGEAR se:
- Erros de compilação
- Testes críticos falhando
- APIs existentes quebradas

---

## 📝 Notas Adicionais

### Limitações Conhecidas (OK para merge):
- ❌ Integração automática de maquininha no pagamento não implementada
- ❌ Cálculo de taxas específicas por bandeira não implementado
- ❌ Controle de recebimentos futuros não implementado

### Essas features serão implementadas em PRs futuras! ✅

---

**Status Final:** [ ] APROVADO PARA MERGE

**Responsável:** Enzo Bettini
**Data de Aprovação:** ___/___/2025
**Hora:** ___:___

---

*Checklist gerado automaticamente - GitHub Copilot*
