# 🏦 Sistema de Maquininhas - Guia de Implementação

## 📋 Resumo

Este guia detalha como implementar o **Sistema Completo de Maquininhas** no ProntodogBanhos.

---

## 🗂️ O que foi criado

### Novas Tabelas:

1. ✅ **contas_bancarias** - Contas da clínica
2. ✅ **adquirentes** - Empresas (Getnet, Stone, PagSeguro, etc.)
3. ✅ **bandeiras** - Visa, Master, Elo, etc.
4. ✅ **maquininhas** - Cadastro completo de maquininhas
5. ✅ **maquininha_taxas** - Taxas por bandeira e parcelamento

### Alterações em Tabelas Existentes:

6. ✅ **venda_baixas** - Novas colunas:
   - `maquininha_id` (FK)
   - `bandeira_id` (FK)
   - `tipo_transacao` (debito/credito_avista/credito_parcelado/pix)
   - `data_prevista_recebimento` (DATE)
   - `data_efetiva_recebimento` (DATE)
   - `status_recebimento` (pendente/recebido/antecipado/estornado)

### Views Criadas:

1. ✅ **vw_maquininhas_completas** - Visão geral
2. ✅ **vw_taxas_maquininhas** - Todas as taxas
3. ✅ **vw_recebimentos_pendentes** - Fluxo de caixa

### Funções Criadas:

1. ✅ **calcular_taxa_maquininha()** - Calcula taxa e valor líquido
2. ✅ **calcular_data_recebimento()** - Calcula data prevista
3. ✅ **buscar_taxa_pix_maquininha()** - Taxa PIX da maquininha

---

## 🚀 Como Executar

### Passo 1: Executar o Script

```bash
# Navegue até a pasta do projeto
cd /Users/enzoayresbettini/Desktop/Repos/ProntodogBanhos

# Execute o script
psql -U seu_usuario -d seu_banco -f ModelagemBanco/sistema_maquininhas.sql
```

### Passo 2: Verificar Criação

O script já inclui verificação automática no final. Você verá:

```
status
------------------------------------
Tabelas criadas com sucesso!

tabela              | registros
--------------------+----------
contas_bancarias    |         0
adquirentes         |        10
bandeiras           |         9
maquininhas         |         0
maquininha_taxas    |         0
```

**Dados Pré-Cadastrados:**

- ✅ 10 adquirentes (Getnet, Stone, PagSeguro, etc.)
- ✅ 9 bandeiras (Visa, Master, Elo, Amex, etc.)

---

## 📝 Exemplo de Uso

### 1. Cadastrar uma Conta Bancária

```sql
INSERT INTO banhoetosa.contas_bancarias (nome, banco, agencia, conta, tipo, pix_chave)
VALUES (
    'ITAU NAV Principal',
    'Itaú',
    '0001',
    '12345-6',
    'corrente',
    'seuemail@exemplo.com'
);
```

### 2. Cadastrar uma Maquininha

```sql
INSERT INTO banhoetosa.maquininhas (
    nome,
    adquirente_id,
    conta_bancaria_id,
    prazo_recebimento_debito,
    prazo_recebimento_credito,
    aceita_antecipacao,
    antecipacao_automatica,
    taxa_antecipacao_mensal,
    aceita_pix,
    taxa_pix,
    prazo_recebimento_pix
) VALUES (
    'Getnet Loja 1',
    (SELECT id FROM banhoetosa.adquirentes WHERE nome = 'Getnet'),
    (SELECT id FROM banhoetosa.contas_bancarias WHERE nome = 'ITAU NAV Principal'),
    1,        -- Débito em 1 dia
    30,       -- Crédito em 30 dias
    true,     -- Aceita antecipação
    false,    -- Não é automática
    2.00,     -- 2% ao mês
    true,     -- Aceita PIX
    0.00,     -- PIX sem taxa
    0         -- PIX no mesmo dia
);
```

### 3. Configurar Taxas - Visa Débito

```sql
INSERT INTO banhoetosa.maquininha_taxas (
    maquininha_id,
    bandeira_id,
    tipo_transacao,
    numero_parcelas,
    taxa_percentual
) VALUES (
    (SELECT id FROM banhoetosa.maquininhas WHERE nome = 'Getnet Loja 1'),
    (SELECT id FROM banhoetosa.bandeiras WHERE nome = 'Visa'),
    'debito',
    NULL,
    2.00
);
```

### 4. Configurar Taxas - Visa Crédito à Vista

```sql
INSERT INTO banhoetosa.maquininha_taxas (
    maquininha_id,
    bandeira_id,
    tipo_transacao,
    numero_parcelas,
    taxa_percentual
) VALUES (
    (SELECT id FROM banhoetosa.maquininhas WHERE nome = 'Getnet Loja 1'),
    (SELECT id FROM banhoetosa.bandeiras WHERE nome = 'Visa'),
    'credito_avista',
    1,
    3.50
);
```

### 5. Configurar Taxas - Visa Parcelado (2x a 12x)

```sql
-- Exemplo: 2x = 4%, 3x = 4.5%, 4x = 5%, etc.
INSERT INTO banhoetosa.maquininha_taxas (
    maquininha_id,
    bandeira_id,
    tipo_transacao,
    numero_parcelas,
    taxa_percentual
)
SELECT
    (SELECT id FROM banhoetosa.maquininhas WHERE nome = 'Getnet Loja 1'),
    (SELECT id FROM banhoetosa.bandeiras WHERE nome = 'Visa'),
    'credito_parcelado',
    parcela,
    3.5 + (parcela * 0.5) -- Taxa cresce 0.5% por parcela
FROM generate_series(2, 12) AS parcela;
```

---

## 🔍 Consultas Úteis

### Ver Todas as Maquininhas

```sql
SELECT * FROM banhoetosa.vw_maquininhas_completas;
```

### Ver Taxas de uma Maquininha

```sql
SELECT * FROM banhoetosa.vw_taxas_maquininhas
WHERE maquininha_nome = 'Getnet Loja 1'
ORDER BY bandeira_nome, tipo_transacao, numero_parcelas;
```

### Ver Recebimentos Pendentes (Fluxo de Caixa)

```sql
SELECT
    codigo_venda,
    cliente_nome,
    maquininha_nome,
    bandeira_nome,
    valor_liquido,
    data_prevista_recebimento,
    dias_ate_receber
FROM banhoetosa.vw_recebimentos_pendentes
WHERE status_recebimento = 'pendente'
ORDER BY data_prevista_recebimento;
```

### Calcular Taxa de uma Transação

```sql
SELECT * FROM banhoetosa.calcular_taxa_maquininha(
    1,                      -- ID da maquininha
    1,                      -- ID da bandeira (Visa)
    'credito_parcelado',    -- Tipo
    3,                      -- 3 parcelas
    300.00                  -- Valor
);
```

### Calcular Data de Recebimento

```sql
SELECT banhoetosa.calcular_data_recebimento(
    1,                      -- ID da maquininha
    'credito_avista',       -- Tipo
    1,                      -- Parcelas
    CURRENT_DATE            -- Data da transação
);
```

---

## 🎯 Estrutura do Cadastro (Wizard)

### Etapa 1/4: Tipo de Recebimento

- Radio buttons: Boleto, Cheque, Depósito, Espécie, **Maquininha**, Pix, Outros
- Se selecionar "Maquininha" → vai para Etapa 2

### Etapa 2/4: Dados da Maquininha

- **Empresa:** Dropdown (adquirentes)
- **Nome:** Input text (ex: "Getnet Loja 1")
- **Conta destino:** Dropdown (contas_bancarias)
- **Prazo débito:** Select (0, 1, 2... dias)
- **Prazo crédito:** Select (0, 1, 2... 30, 60 dias)
- **Antecipação automática:** Radio (Sim/Não)
- **Taxa antecipação:** Input number (% ao mês)

### Etapa 3/4: PIX na Maquininha

- **Aceita PIX?** Radio (Sim/Não)
- Se SIM:
  - **Conta destino PIX:** Dropdown (pode ser diferente)
  - **Taxa PIX:** Input number (%)
  - **Prazo PIX:** Select (0, 1 dia)

### Etapa 4/4: Bandeiras e Taxas

- **Loop para adicionar bandeiras:**
  - Selecionar bandeira (Visa, Master, etc.)
  - Taxa débito (%)
  - Quantidade máxima de parcelas (1-12)
  - Grid de taxas:
    - À vista (%)
    - 2x (%), 3x (%), ..., 12x (%)
- Botão "Adicionar outra bandeira"
- Botão "Salvar"

---

## ✅ Compatibilidade

### ✅ NÃO QUEBRA NADA:

- Vendas antigas continuam funcionando
- `formas_pagamento` continua existindo
- `maquininha_id` é NULLABLE
- Sistema proporcional intacto

### ✅ MIGRAÇÃO GRADUAL:

- Use `formas_pagamento` para: Dinheiro, PIX direto, Boleto
- Use `maquininhas` para: Cartões com maquininha
- Ambos coexistem perfeitamente

---

## 📊 Exemplo de Fluxo Completo

### Cenário: Venda com Cartão de Crédito

1. **Cliente compra:** R$ 300,00 no crédito 3x Visa pela Getnet
2. **Sistema calcula automaticamente:**
   - Taxa: 4.5% = R$ 13,50
   - Valor líquido: R$ 286,50
   - Data prevista: hoje + 30 dias
   - Status: pendente
3. **Registra na baixa:**
   ```sql
   INSERT INTO venda_baixas (
       venda_id, forma_pagamento_id, maquininha_id, bandeira_id,
       tipo_transacao, valor_baixa, valor_taxa, valor_liquido,
       numero_parcelas, data_prevista_recebimento, status_recebimento
   ) VALUES (
       1, 5, 1, 1, 'credito_parcelado',
       300.00, 13.50, 286.50, 3,
       CURRENT_DATE + 30, 'pendente'
   );
   ```
4. **Quando o dinheiro cair:**
   ```sql
   UPDATE venda_baixas
   SET data_efetiva_recebimento = '2025-11-20',
       status_recebimento = 'recebido'
   WHERE id = 1;
   ```

---

## 🚦 Próximos Passos

Após executar este script:

1. ✅ **Testar no banco** - Execute consultas de teste
2. ⏳ **Backend Java** - Criar entidades, repositories, services
3. ⏳ **Frontend Vue** - Criar wizard de 4 etapas
4. ⏳ **Integração** - Conectar com sistema de vendas

---

## 🆘 Problemas Comuns

### Erro: "relation already exists"

- **Solução:** O script usa `IF NOT EXISTS`, então é seguro executar múltiplas vezes

### Erro: "column already exists"

- **Solução:** O script usa `IF NOT EXISTS` para colunas também

### Tabelas vazias

- **Normal!** Você precisa cadastrar manualmente:
  - Contas bancárias
  - Maquininhas
  - Taxas

---

## 📞 Suporte

Dúvidas? Verifique:

- Script SQL: `ModelagemBanco/sistema_maquininhas.sql`
- Este README: `ModelagemBanco/README_MAQUININHAS.md`

---

**✅ Script pronto para execução!** 🚀
