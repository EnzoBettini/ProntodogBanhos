# 🚀 COMO EXECUTAR OS SCRIPTS SQL

## ⚡ Quick Start (Execução Rápida)

### 1️⃣ Execute o Script Principal

```bash
# Navegue até a pasta do projeto
cd /Users/enzoayresbettini/Desktop/Repos/ProntodogBanhos

# Execute o script de maquininhas
psql -U seu_usuario -d seu_banco -f ModelagemBanco/sistema_maquininhas.sql
```

**Substitua:**

- `seu_usuario` → seu usuário PostgreSQL
- `seu_banco` → nome do banco (provavelmente o mesmo que tem o schema `banhoetosa`)

### 2️⃣ Execute os Testes (Recomendado)

```bash
# Execute o script de testes
psql -U seu_usuario -d seu_banco -f ModelagemBanco/teste_maquininhas.sql
```

---

## 📋 O que será criado

### ✅ 5 Novas Tabelas:

1. `contas_bancarias` - Contas da clínica
2. `adquirentes` - Getnet, Stone, PagSeguro, etc. (10 pré-cadastrados)
3. `bandeiras` - Visa, Master, Elo, etc. (9 pré-cadastradas)
4. `maquininhas` - Cadastro de maquininhas
5. `maquininha_taxas` - Taxas por bandeira e parcelamento

### ✅ Alteração em 1 Tabela Existente:

- `venda_baixas` - 6 novas colunas (todas NULLABLE)

### ✅ 3 Views:

1. `vw_maquininhas_completas`
2. `vw_taxas_maquininhas`
3. `vw_recebimentos_pendentes`

### ✅ 3 Funções:

1. `calcular_taxa_maquininha()`
2. `calcular_data_recebimento()`
3. `buscar_taxa_pix_maquininha()`

---

## 🔍 Verificar se funcionou

Após executar, rode estas consultas para verificar:

```sql
-- 1. Ver quantas tabelas foram criadas
SELECT
    'contas_bancarias' as tabela, COUNT(*) as registros FROM banhoetosa.contas_bancarias
UNION ALL
SELECT 'adquirentes', COUNT(*) FROM banhoetosa.adquirentes
UNION ALL
SELECT 'bandeiras', COUNT(*) FROM banhoetosa.bandeiras
UNION ALL
SELECT 'maquininhas', COUNT(*) FROM banhoetosa.maquininhas
UNION ALL
SELECT 'maquininha_taxas', COUNT(*) FROM banhoetosa.maquininha_taxas;

-- Resultado esperado:
-- contas_bancarias: 0 (você vai cadastrar)
-- adquirentes: 10 ✅
-- bandeiras: 9 ✅
-- maquininhas: 0 (você vai cadastrar)
-- maquininha_taxas: 0 (você vai cadastrar)
```

```sql
-- 2. Ver se as colunas foram adicionadas em venda_baixas
SELECT column_name, data_type
FROM information_schema.columns
WHERE table_schema = 'banhoetosa'
  AND table_name = 'venda_baixas'
  AND column_name IN (
      'maquininha_id',
      'bandeira_id',
      'tipo_transacao',
      'data_prevista_recebimento',
      'data_efetiva_recebimento',
      'status_recebimento'
  );

-- Deve retornar 6 linhas
```

```sql
-- 3. Ver as views criadas
SELECT * FROM banhoetosa.vw_maquininhas_completas;
SELECT * FROM banhoetosa.vw_taxas_maquininhas;
SELECT * FROM banhoetosa.vw_recebimentos_pendentes;
```

---

## ✅ Checklist de Execução

- [ ] ✅ Executei `sistema_maquininhas.sql`
- [ ] ✅ Sem erros na execução
- [ ] ✅ 10 adquirentes cadastrados
- [ ] ✅ 9 bandeiras cadastradas
- [ ] ✅ 3 views criadas
- [ ] ✅ 3 funções criadas
- [ ] 🧪 Executei `teste_maquininhas.sql` (opcional)
- [ ] 🧪 Testes passaram com sucesso

---

## 🎯 Próximo Passo

Após executar e validar os scripts SQL:

**👉 Avise para seguir para a PARTE 2: Backend Java**

---

## 📚 Documentação Completa

- 📄 **Script Principal:** `ModelagemBanco/sistema_maquininhas.sql`
- 🧪 **Script de Testes:** `ModelagemBanco/teste_maquininhas.sql`
- 📖 **README Completo:** `ModelagemBanco/README_MAQUININHAS.md`
- 📊 **Resumo Executivo:** `PARTE1_SQL_MAQUININHAS.md`
- 🚀 **Este Guia:** `EXECUTAR_SCRIPTS_SQL.md`

---

## 🆘 Problemas?

### Erro: "permission denied"

```bash
# Tente com sudo ou com um usuário com permissões
sudo -u postgres psql -d seu_banco -f ModelagemBanco/sistema_maquininhas.sql
```

### Erro: "database does not exist"

```bash
# Liste os bancos disponíveis
psql -U seu_usuario -l

# Use o banco correto
psql -U seu_usuario -d nome_banco_correto -f ModelagemBanco/sistema_maquininhas.sql
```

### Erro: "relation already exists"

**Isso é normal!** O script usa `IF NOT EXISTS`, então é seguro executar múltiplas vezes.

---

**✅ Pronto para executar!** 🚀
