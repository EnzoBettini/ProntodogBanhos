# Sistema Avançado de Formas de Pagamento - Maquininhas

## 📋 Visão Geral

Sistema completo para gerenciamento de formas de pagamento, com foco especial em **maquininhas de cartão**, inspirado em sistemas modernos mas melhorado para as necessidades do ProntodogBanhos.

## ✨ Funcionalidades Principais

### 1. **Cadastro de Contas Bancárias**

- Múltiplas contas para diferentes finalidades
- Suporte a chave PIX
- Diferenciação entre conta corrente, poupança e pagamento

### 2. **Cadastro de Maquininhas**

Cada maquininha pode ter:

- ✅ Operadora/Adquirente (Getnet, Stone, PagSeguro, etc.)
- ✅ Conta bancária de destino
- ✅ Prazos de recebimento (débito e crédito)
- ✅ Configuração de antecipação automática
- ✅ Taxa de antecipação mensal
- ✅ Suporte a PIX pela maquininha
- ✅ Conta específica para PIX (se diferente da principal)

### 3. **Configuração de Taxas Detalhadas**

Por cada maquininha, você pode configurar:

- ✅ Taxa diferente por bandeira (Visa, Master, Elo, etc.)
- ✅ Taxa específica para débito
- ✅ Taxa para crédito à vista
- ✅ Taxa para cada parcelamento (2x, 3x, até 12x ou mais)
- ✅ Taxa percentual + taxa fixa opcional

### 4. **Controle de Recebimentos**

- 📅 Data prevista de recebimento calculada automaticamente
- 📅 Data efetiva de recebimento
- 📊 Status: pendente, recebido, antecipado, estornado
- 💰 Valor líquido após taxas
- 📈 Fluxo de caixa projetado

---

## 🗂️ Estrutura do Banco de Dados

### Tabelas Criadas

1. **`contas_bancarias`** - Contas da clínica
2. **`adquirentes`** - Empresas de maquininha (Stone, Getnet, etc.)
3. **`bandeiras`** - Bandeiras de cartão (Visa, Master, etc.)
4. **`maquininhas`** - Cadastro das maquininhas
5. **`maquininha_taxas`** - Taxas por bandeira e parcelamento
6. **`venda_baixas`** (atualizada) - Agora com suporte a maquininhas

### Views Criadas

1. **`vw_maquininhas_completas`** - Visão geral das maquininhas
2. **`vw_taxas_maquininhas`** - Todas as taxas configuradas
3. **`vw_recebimentos_pendentes`** - Recebimentos futuros para fluxo de caixa

---

## 🚀 Como Usar

### Passo 1: Executar o Script SQL

```bash
# Executar o script principal (se ainda não executou)
psql -U seu_usuario -d seu_banco < ModelagemBanco/schema_completo.sql

# Executar o sistema avançado de pagamentos
psql -U seu_usuario -d seu_banco < ModelagemBanco/sistema_pagamento_avancado.sql
```

### Passo 2: Cadastrar Conta Bancária

```sql
INSERT INTO banhoetosa.contas_bancarias (nome, banco, agencia, conta, tipo, pix_chave)
VALUES ('ITAU NAV Principal', 'Itaú', '0001', '12345-6', 'corrente', 'seuemail@exemplo.com');
```

### Passo 3: Cadastrar Maquininha

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
    taxa_pix
) VALUES (
    'Getnet Loja 1',
    (SELECT id FROM banhoetosa.adquirentes WHERE nome = 'Getnet'),
    (SELECT id FROM banhoetosa.contas_bancarias WHERE nome = 'ITAU NAV Principal'),
    1,        -- Recebe débito em 1 dia
    30,       -- Recebe crédito em 30 dias
    true,     -- Aceita antecipação
    false,    -- Não antecipa automaticamente
    2.00,     -- 2% ao mês de antecipação
    true,     -- Aceita PIX
    0.00      -- 0% de taxa no PIX
);
```

### Passo 4: Configurar Taxas

```sql
-- Exemplo: Configurar taxas para Visa na Getnet

-- Débito Visa: 2%
INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, taxa_percentual)
VALUES (
    (SELECT id FROM banhoetosa.maquininhas WHERE nome = 'Getnet Loja 1'),
    (SELECT id FROM banhoetosa.bandeiras WHERE nome = 'Visa'),
    'debito',
    2.00
);

-- Crédito à vista Visa: 3.5%
INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, taxa_percentual)
VALUES (
    (SELECT id FROM banhoetosa.maquininhas WHERE nome = 'Getnet Loja 1'),
    (SELECT id FROM banhoetosa.bandeiras WHERE nome = 'Visa'),
    'credito_avista',
    3.50
);

-- Crédito 2x Visa: 4%
INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas, taxa_percentual)
VALUES (
    (SELECT id FROM banhoetosa.maquininhas WHERE nome = 'Getnet Loja 1'),
    (SELECT id FROM banhoetosa.bandeiras WHERE nome = 'Visa'),
    'credito_parcelado',
    2,
    4.00
);

-- Repetir para 3x, 4x... até 12x
```

### Passo 5: Registrar uma Venda com Maquininha

```sql
-- Ao registrar uma baixa de pagamento, agora você pode especificar:
INSERT INTO banhoetosa.venda_baixas (
    venda_id,
    forma_pagamento_id,
    maquininha_id,
    bandeira_id,
    tipo_transacao,
    valor_baixa,
    valor_taxa,
    valor_liquido,
    numero_parcelas,
    data_prevista_recebimento,
    status_recebimento,
    usuario_id
) VALUES (
    1,  -- ID da venda
    (SELECT id FROM banhoetosa.formas_pagamento WHERE nome = 'Crédito 3x'),
    (SELECT id FROM banhoetosa.maquininhas WHERE nome = 'Getnet Loja 1'),
    (SELECT id FROM banhoetosa.bandeiras WHERE nome = 'Visa'),
    'credito_parcelado',
    300.00,  -- Valor pago
    13.50,   -- Taxa (4.5% de 300)
    286.50,  -- Valor líquido
    3,       -- 3 parcelas
    CURRENT_DATE + 30,  -- Recebe em 30 dias
    'pendente',
    1  -- ID do usuário
);
```

---

## 🎨 Fluxo de Cadastro no Frontend

### Etapa 1/4: Tipo de Recebimento

```
○ Dinheiro
○ PIX Direto
● Maquininha de Cartão
○ Boleto
○ Cheque
○ Outros

[Avançar]
```

### Etapa 2/4: Dados da Maquininha

```
┌─────────────────────────────────────┐
│ Empresa da maquininha*              │
│ [Getnet ▼]                          │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ Nome da maquininha*                 │
│ [Getnet Loja 1________________]     │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ Conta destino*                      │
│ [ITAU NAV Principal ▼]              │
└─────────────────────────────────────┘
ℹ️ Para cada pagamento recebido, será lançado nesta conta.

┌─────────────────────────────────────┐
│ Prazo para recebimento no débito*   │
│ [1 dia ▼]                           │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ Prazo para recebimento no crédito*  │
│ [30 dias ▼]                         │
└─────────────────────────────────────┘

Antecipação automática*
● Sim    ○ Não

┌─────────────────────────────────────┐
│ Taxa de antecipação*                │
│ [2.00__%] ao mês                    │
└─────────────────────────────────────┘

[Voltar]  [Avançar]
```

### Etapa 3/4: PIX na Maquininha

```
A maquininha aceita PIX?*

● Sim    ○ Não

ℹ️ Marque "Sim" se você aceita PIX através desta maquininha

┌─────────────────────────────────────┐
│ Conta destino do PIX*               │
│ [ITAU NAV Principal ▼]              │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ Taxa do PIX*                        │
│ [0.00__%]                           │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ Prazo para recebimento*             │
│ [Mesmo dia ▼]                       │
└─────────────────────────────────────┘

[Voltar]  [Avançar]
```

### Etapa 4/4: Bandeiras e Taxas

```
Hora de configurar as taxas por bandeira

┌─────────────────────────────────────┐
│ Bandeira*                           │
│ [Visa ▼]                            │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ Taxa no débito*                     │
│ [2.00__%]                           │
└─────────────────────────────────────┘

Crédito
┌─────────────────────────────────────┐
│ Quantidade máxima de parcelas*      │
│ [12x ▼]                             │
└─────────────────────────────────────┘

┌───────────────┬───────────────┐
│ À vista*      │ Parcelado 2x* │
│ [3.50____%]   │ [4.00____%]   │
├───────────────┼───────────────┤
│ Parcelado 3x* │ Parcelado 4x* │
│ [4.50____%]   │ [5.00____%]   │
├───────────────┼───────────────┤
│ Parcelado 5x* │ Parcelado 6x* │
│ [5.50____%]   │ [6.00____%]   │
├───────────────┼───────────────┤
│   ... até 12x ...              │
└───────────────────────────────┘

[Adicionar outra bandeira]

[Voltar]  [Salvar]
```

---

## 📊 Relatórios e Consultas Úteis

### Ver todas as maquininhas configuradas

```sql
SELECT * FROM banhoetosa.vw_maquininhas_completas;
```

### Ver todas as taxas

```sql
SELECT * FROM banhoetosa.vw_taxas_maquininhas
WHERE maquininha_nome = 'Getnet Loja 1';
```

### Ver recebimentos pendentes (Fluxo de Caixa)

```sql
SELECT * FROM banhoetosa.vw_recebimentos_pendentes
ORDER BY data_prevista_recebimento;
```

### Calcular taxa de uma transação

```sql
SELECT * FROM banhoetosa.calcular_taxa_maquininha(
    1,              -- ID da maquininha
    1,              -- ID da bandeira (Visa)
    'credito_parcelado',
    3,              -- 3 parcelas
    300.00          -- Valor
);
```

### Calcular data de recebimento

```sql
SELECT * FROM banhoetosa.calcular_data_recebimento(
    1,              -- ID da maquininha
    'credito_parcelado',
    3,              -- 3 parcelas
    CURRENT_DATE    -- Data da transação
);
```

---

## 🔄 Integração com Sistema Atual

Este sistema **complementa** o sistema existente de vendas:

1. ✅ **Compatível** com a tabela `formas_pagamento` atual
2. ✅ **Adiciona colunas** na tabela `venda_baixas` existente
3. ✅ **Não quebra** nada do sistema atual
4. ✅ **Opcional** - pode usar o sistema simples ou avançado

### Migração Gradual

Você pode:

- Continuar usando `formas_pagamento` para dinheiro, PIX direto, etc.
- Usar `maquininhas` para pagamentos com cartão
- Migrar gradualmente conforme necessário

---

## 💡 Melhorias em Relação ao Sistema de Referência

1. ✅ **Banco de dados normalizado** - Sem repetição de dados
2. ✅ **Múltiplas maquininhas** - Cadastre quantas precisar
3. ✅ **Flexibilidade** - Configure exatamente como sua operadora cobra
4. ✅ **Fluxo de caixa** - Saiba exatamente quando vai receber
5. ✅ **Relatórios** - Views prontas para análise
6. ✅ **Funções auxiliares** - Cálculos automáticos de taxas e datas
7. ✅ **Histórico completo** - Rastreie cada transação

---

## 🎯 Próximos Passos

### Backend (Java/Spring Boot)

- [ ] Criar DTOs para maquininhas
- [ ] Criar endpoints REST para CRUD de maquininhas
- [ ] Criar endpoints para configuração de taxas
- [ ] Endpoint para calcular taxas em tempo real
- [ ] Endpoint para listar recebimentos pendentes

### Frontend (Vue.js)

- [ ] Componente de cadastro de maquininhas (wizard 4 etapas)
- [ ] Componente de configuração de taxas por bandeira
- [ ] Tela de listagem de maquininhas
- [ ] Integração no fluxo de vendas
- [ ] Dashboard de recebimentos futuros

---

## 📝 Notas Importantes

1. **Taxas por bandeira**: Configure todas as bandeiras que você aceita
2. **Antecipação**: Se trabalha com antecipação, configure corretamente as taxas
3. **Contas bancárias**: Crie contas separadas se necessário (ex: uma para PIX, outra para cartão)
4. **Prazos**: Os prazos são em dias úteis na maioria das operadoras
5. **Taxas**: Verifique com sua operadora as taxas reais praticadas

---

## 🆘 Suporte

Para dúvidas sobre:

- **Estrutura do banco**: Veja os comentários no arquivo `sistema_pagamento_avancado.sql`
- **Integração**: Consulte este documento
- **Frontend**: Veja o exemplo de componente Vue (será criado)

---

## 📄 Licença

Parte do sistema ProntodogBanhos - Uso interno
