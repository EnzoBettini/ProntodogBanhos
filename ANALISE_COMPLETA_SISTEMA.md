# 📊 Análise Completa do Sistema - ProntodogBanhos

**Data da Análise:** 21/10/2025
**Objetivo:** Entender arquitetura atual antes de implementar feature complexa de vendas e pagamentos

---

## 🏗️ Arquitetura Geral

### Stack Tecnológica

- **Backend:** Java 17 + Spring Boot
- **Frontend:** Vue 3 + TypeScript + Tailwind CSS
- **Banco de Dados:** PostgreSQL (schema: `banhoetosa`)
- **Build:** Maven (backend) + Vite (frontend)

### Estrutura do Projeto

```
ProntodogBanhos/
├── prontodog-banho/           # Backend Java
│   └── src/main/java/backend/prontodogbanho/
│       ├── model/             # Entidades JPA
│       ├── repository/        # Repositórios Spring Data
│       ├── service/           # Lógica de negócio
│       ├── controller/        # REST Controllers
│       └── dto/               # Data Transfer Objects
├── prontodog-banho-front/     # Frontend Vue
│   └── src/
│       ├── views/             # Páginas
│       ├── components/        # Componentes reutilizáveis
│       ├── services/          # API calls
│       └── types/             # TypeScript types
└── ModelagemBanco/            # Scripts SQL
```

---

## 💾 Modelo de Dados Atual

### Entidades Principais

#### 1. **Cliente** (`clientes`)

```java
- id: Long (PK)
- codigo_cliente_sistema: Long (UNIQUE)
- nome_completo: String
- cpf: String (UNIQUE)
- codigo_simplesvet: Long (UNIQUE)
// Relacionamentos:
- telefones: List<Telefone>
- emails: List<EmailCliente>
- animais: List<Animal>
- vendas: List<Venda>
```

#### 2. **Animal** (`animais`)

```java
- id: Long (PK)
- codigo_animal_sistema: Long (UNIQUE)
- nome: String
- codigo_simplesvet: Long (UNIQUE)
- tipo: String (Cachorro/Gato)
- raca: String
- peso: BigDecimal
- status: String (vivo/falecido)
- cliente_id: Long (FK)
// Relacionamentos:
- cliente: Cliente
- animalServicos: List<AnimalServico>
```

#### 3. **Servico** (`servicos`)

```java
- id: Long (PK)
- nome: String (UNIQUE)
- descricao: Text
- quantidade: Integer (número de banhos no pacote)
- valor: BigDecimal
- pode_ser_adicional: Boolean
- categoria: String (banho/tosa/pacote/geral)
// Relacionamentos:
- animalServicos: List<AnimalServico>
```

#### 4. **AnimalServico** (`animal_servico`) ⭐ CENTRAL

```java
- id: Long (PK)
- data_servico: LocalDate
- banhos_usados: Integer
- data_expiracao: LocalDate
- status_pagamento: String (em_aberto/parcial/pago/cancelado)
- data_pagamento: LocalDate
- status_servico: String (pendente/agendado/realizado)
- data_realizacao: LocalDate
- valor_cobrado: BigDecimal
- animal_id: Long (FK)
- servico_id: Long (FK)
- usuario_id: Long (FK)
- venda_id: Long (FK, NULLABLE)
// Relacionamentos:
- animal: Animal
- servico: Servico
- usuario: Usuario
- venda: Venda
- banhosIndividuais: List<BanhoIndividual>
- servicosAdicionais: List<ServicoAdicional>
- vendaItem: VendaItem (OneToOne reverso)
```

**Conceito:** Um `AnimalServico` representa um **pacote de serviços** contratado para um animal. Pode ser:

- **Pacote:** Múltiplos banhos (ex: 4 banhos)
- **Serviço Único:** 1 banho/tosa

#### 5. **BanhoIndividual** (`banhos_individuais`)

```java
- id: Long (PK)
- animal_servico_id: Long (FK)
- data_banho: LocalDate
- numero_banho: Integer
- observacoes: Text
- usuario_id: Long (FK)
- created_at: Timestamp
// Relacionamento:
- animalServico: AnimalServico
- usuario: Usuario
```

**Conceito:** Cada banho individual realizado dentro de um pacote.

#### 6. **ServicoAdicional** (`servicos_adicionais`) ✨

```java
- id: Long (PK)
- animal_servico_principal_id: Long (FK)
- servico_adicional_id: Long (FK)
- quantidade_adicional: Integer
- valor_unitario: BigDecimal
- valor_total: BigDecimal
- data_adicao: Timestamp
- data_realizacao: LocalDate
- status_pagamento: String (em_aberto/parcial/pago/cancelado)
- data_pagamento: LocalDate
- observacoes: Text
- usuario_id: Long (FK)
// Relacionamentos:
- animalServicoPrincipal: AnimalServico
- servicoAdicional: Servico
- usuario: Usuario
```

**Conceito:** Serviços extras adicionados durante um atendimento (ex: Cliente vem para banho, mas também faz tosa).

---

## 💰 Sistema de Vendas e Pagamentos ⭐ COMPLEXO

### Estrutura Hierárquica

```
VENDA (Agrupador)
  ├─ VENDA_ITEM 1 → AnimalServico 1
  │   └─ ServicoAdicional 1.1
  │   └─ ServicoAdicional 1.2
  ├─ VENDA_ITEM 2 → AnimalServico 2
  │   └─ ServicoAdicional 2.1
  └─ VENDA_BAIXAS (Pagamentos)
      ├─ Baixa 1: R$ 50 (PIX)
      ├─ Baixa 2: R$ 30 (Dinheiro)
      └─ Baixa 3: R$ 20 (Cartão)
```

### Entidades do Sistema de Vendas

#### 7. **Venda** (`vendas`)

```java
- id: Long (PK)
- codigo_venda: Long (UNIQUE, auto-incremento)
- cliente_id: Long (FK)
- tipo_venda: String (presencial/agendamento/busca_entrega)
- status_venda: String (em_aberto/parcial/pago/cancelado)
- usuario_id: Long (FK)
- data_venda: Timestamp
// Valores Financeiros:
- valor_bruto: BigDecimal (soma dos itens)
- desconto: BigDecimal
- valor_total: BigDecimal (bruto - desconto)
- valor_pago: BigDecimal
- valor_pendente: BigDecimal
- observacoes: Text
- cancelado_em: Timestamp
- motivo_cancelamento: Text
// Relacionamentos:
- cliente: Cliente
- usuario: Usuario
- itens: List<VendaItem>
- baixas: List<VendaBaixa>
- animalServicos: List<AnimalServico> (reverso)
```

**Métodos Importantes:**

- `recalcularValores()`: Recalcula total, pendente e status
- `atualizarStatus()`: Define status baseado em valor pago

#### 8. **VendaItem** (`venda_itens`)

```java
- id: Long (PK)
- venda_id: Long (FK)
- animal_servico_id: Long (FK, UNIQUE) ⚠️ Cada AS só pode estar em 1 venda
- valor_item: BigDecimal
- desconto_item: BigDecimal
- valor_final_item: BigDecimal
- valor_pago_item: BigDecimal ⭐ RASTREAMENTO PROPORCIONAL
- observacoes: Text
// Relacionamentos:
- venda: Venda
- animalServico: AnimalServico (OneToOne)
```

**Conceito IMPORTANTE:**

- `valor_pago_item` rastreia quanto deste item específico já foi pago
- Atualizado **proporcionalmente** quando há pagamentos na venda
- Usado para calcular `status_pagamento` do `AnimalServico`

#### 9. **VendaBaixa** (`venda_baixas`) 💳

```java
- id: Long (PK)
- venda_id: Long (FK)
- forma_pagamento_id: Long (FK)
- valor_baixa: BigDecimal
- valor_taxa: BigDecimal (calculado automaticamente)
- valor_liquido: BigDecimal (valor_baixa - taxa)
- data_baixa: Timestamp
- numero_parcelas: Integer
- data_primeira_parcela: LocalDate
- observacoes: Text
- usuario_id: Long (FK)
// Relacionamentos:
- venda: Venda
- formaPagamento: FormaPagamento
- usuario: Usuario
```

**Conceito:** Cada pagamento recebido para uma venda. Pode ter múltiplas baixas.

#### 10. **FormaPagamento** (`formas_pagamento`)

```java
- id: Long (PK)
- nome: String (UNIQUE)
- tipo: String (dinheiro/debito/credito/pix/boleto/outro)
- taxa_percentual: BigDecimal
- taxa_fixa: BigDecimal
- parcelas_max: Integer
- dias_recebimento: Integer
- ativo: Boolean
- created_at: Timestamp
// Relacionamento:
- baixas: List<VendaBaixa>
```

**Métodos:**

- `calcularTaxa(valor)`: Calcula taxa total (% + fixa)
- `calcularValorLiquido(valorBruto)`: Retorna valor após taxa

---

## 🔄 Fluxo de Pagamento Proporcional ⭐ CRÍTICO

### Sistema Implementado: **Distribuição Proporcional**

Quando um pagamento é registrado na venda, ele é distribuído **proporcionalmente** entre todos os itens.

### Exemplo Prático

#### Cenário Inicial

```
VENDA #100
├─ Item 1: Banho Totó     → R$ 70,00
├─ Item 2: Tosa Rex       → R$ 70,00
└─ TOTAL:                   R$ 140,00
```

#### Pagamento Parcial de R$ 70

```
Registrar Baixa: R$ 70,00 (PIX)
↓
DISTRIBUIÇÃO PROPORCIONAL:
├─ Item 1: R$ 70 × (70/140) = R$ 35,00 → 50% pago → status: PARCIAL
├─ Item 2: R$ 70 × (70/140) = R$ 35,00 → 50% pago → status: PARCIAL
└─ Venda: R$ 70 / R$ 140 = 50% → status: PARCIAL
```

#### Pagamento do Restante: R$ 70

```
Registrar Baixa: R$ 70,00 (Dinheiro)
↓
DISTRIBUIÇÃO PROPORCIONAL:
├─ Item 1: R$ 70 × (70/140) = R$ 35,00 → Total: R$ 70 (100%) → status: PAGO
├─ Item 2: R$ 70 × (70/140) = R$ 35,00 → Total: R$ 70 (100%) → status: PAGO
└─ Venda: R$ 140 / R$ 140 = 100% → status: PAGO
```

### Código Responsável

#### `VendaService.java` - Método Principal

```java
private void distribuirPagamentoAosItens(Long vendaId, BigDecimal valorDistribuir) {
    List<VendaItem> itens = vendaItemRepository.findByVenda_Id(vendaId);
    BigDecimal totalItens = itens.stream()
        .map(VendaItem::getValorFinalItem)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    for (VendaItem item : itens) {
        // Calcular proporcional
        BigDecimal proporcao = item.getValorFinalItem().divide(totalItens, 10, RoundingMode.HALF_UP);
        BigDecimal valorParaItem = valorDistribuir.multiply(proporcao);

        // Adicionar ao valor já pago
        item.setValorPagoItem(item.getValorPagoItem().add(valorParaItem));
        vendaItemRepository.save(item);
    }

    atualizarStatusItensBaseadoEmPagamento(vendaId);
}

private void atualizarStatusItensBaseadoEmPagamento(Long vendaId) {
    List<VendaItem> itens = vendaItemRepository.findByVenda_Id(vendaId);

    for (VendaItem item : itens) {
        AnimalServico as = item.getAnimalServico();
        BigDecimal percentualPago = calcularPercentualPago(item);

        if (percentualPago.compareTo(BigDecimal.ZERO) == 0) {
            as.setStatusPagamento("em_aberto");
        } else if (percentualPago.compareTo(new BigDecimal("100")) >= 0) {
            as.setStatusPagamento("pago");
            as.setDataPagamento(LocalDate.now());
        } else {
            as.setStatusPagamento("parcial");
        }

        animalServicoRepository.save(as);
    }
}
```

### Sincronização Bidirecional

#### 1. Venda → Itens (Normal)

```
Registrar baixa na venda
  ↓
Distribuir proporcionalmente aos itens
  ↓
Atualizar status_pagamento dos AnimalServico
```

#### 2. Item → Venda (Reverso - Implementado!)

```
Marcar AnimalServico individual como "pago"
  ↓
Detectar que está em uma venda
  ↓
Calcular valor pendente do item
  ↓
Registrar baixa automática na venda
  ↓
Distribuir proporcionalmente a TODOS os itens
```

**Código:**

```java
// AnimalServicoService.java
public void marcarComoPago(Long id, LocalDate dataPagamento) {
    AnimalServico as = findById(id);

    // Se está em uma venda, usar sistema proporcional
    if (as.getVenda() != null) {
        VendaItem item = vendaItemRepository.findByAnimalServico_Id(id);
        BigDecimal valorPendente = item.getValorFinalItem().subtract(item.getValorPagoItem());

        // Criar baixa automática
        vendaService.registrarBaixaAutomatica(as.getVenda().getId(), valorPendente, ...);
    }
}
```

---

## 🔑 Pontos Críticos do Sistema Atual

### ✅ O Que Funciona Bem

1. **Sistema de Pacotes Completo**

   - Controle de banhos usados/restantes
   - Data de expiração com alertas
   - Banhos individuais rastreados
   - Status visual (vencido, vencendo, válido)

2. **Serviços Adicionais**

   - Adicionar serviços durante atendimento
   - Controle financeiro independente
   - Integração com vendas

3. **Pagamento Proporcional**

   - Distribuição automática entre itens
   - Sincronização bidirecional
   - Rastreamento por item (`valor_pago_item`)
   - Status automático (em_aberto/parcial/pago)

4. **Interface Moderna**
   - Busca inteligente (#ID, @animal, %serviço)
   - Badges coloridos de status
   - Animações e alertas visuais
   - Responsiva (Tailwind CSS)

### ⚠️ Limitações Identificadas

#### 1. **Formas de Pagamento Simplificadas**

```sql
formas_pagamento:
- nome (ex: "Crédito 3x")
- tipo (credito/debito/pix)
- taxa_percentual
- taxa_fixa
- parcelas_max
- dias_recebimento
```

**PROBLEMA:**

- Não rastreia **maquininha** específica
- Não diferencia **bandeira** (Visa, Master, Elo)
- Não rastreia **data prevista de recebimento**
- Não rastreia **data efetiva de recebimento**
- Não tem **conta bancária destino**
- Não controla **antecipação**

#### 2. **VendaBaixa Limitada**

```sql
venda_baixas:
- forma_pagamento_id (genérica)
- valor_baixa
- valor_taxa
- numero_parcelas
- data_primeira_parcela
```

**FALTA:**

- maquininha_id
- bandeira_id
- tipo_transacao
- data_prevista_recebimento
- data_efetiva_recebimento
- status_recebimento (pendente/recebido/antecipado)
- conta_bancaria_destino_id

#### 3. **Não Há Fluxo de Caixa**

- Sem projeção de recebimentos futuros
- Não sabe quando vai cair na conta
- Não rastreia se o dinheiro chegou
- Não controla antecipação de recebíveis

#### 4. **Relatórios Financeiros Básicos**

- Não tem DRE (Demonstrativo de Resultados)
- Não separa por forma de pagamento
- Não calcula taxas reais por operadora
- Não projeta fluxo de caixa

---

## 📊 Análise da Feature Complexa Necessária

### O Que Você Provavelmente Precisa

Com base na análise e nas imagens que você mostrou, a feature complexa deve incluir:

#### 1. **Sistema Avançado de Maquininhas**

```
Estrutura Proposta:
├─ Cadastro de Contas Bancárias
├─ Cadastro de Adquirentes (Stone, Getnet, PagSeguro)
├─ Cadastro de Bandeiras (Visa, Master, Elo)
├─ Cadastro de Maquininhas
│   ├─ Nome/Identificação
│   ├─ Adquirente
│   ├─ Conta Destino
│   ├─ Prazos (débito/crédito)
│   ├─ Antecipação (sim/não, taxa)
│   ├─ PIX na maquininha (sim/não, taxa)
│   └─ Taxas por Bandeira e Parcelamento
│       ├─ Débito: 2%
│       ├─ Crédito à vista: 3.5%
│       ├─ 2x: 4%, 3x: 4.5%, ... até 12x
│       └─ Por bandeira (Visa diferente de Elo)
└─ Sistema de Recebimentos
    ├─ Data Prevista (calculada automaticamente)
    ├─ Data Efetiva (quando caiu na conta)
    ├─ Status (pendente/recebido/antecipado)
    └─ Reconciliação bancária
```

#### 2. **Fluxo de Caixa Projetado**

```
Dashboard:
├─ Recebimentos Hoje
├─ Recebimentos Próximos 7 dias
├─ Recebimentos Próximos 30 dias
├─ Por maquininha/bandeira
└─ Gráfico de evolução
```

#### 3. **Relatórios Financeiros**

```
Relatórios:
├─ Vendas por Forma de Pagamento
├─ Taxas Pagas por Operadora
├─ Taxa Efetiva Média
├─ Ticket Médio por Forma
├─ DRE (Receitas - Custos - Taxas)
└─ Comparativo de Operadoras
```

---

## 🎯 Arquitetura Proposta para Feature

### Banco de Dados - Novas Tabelas

```sql
1. contas_bancarias
   - id, nome, banco, agencia, conta, tipo, pix_chave

2. adquirentes
   - id, nome, codigo

3. bandeiras
   - id, nome, codigo

4. maquininhas
   - id, nome, adquirente_id, conta_bancaria_id
   - prazo_recebimento_debito, prazo_recebimento_credito
   - aceita_antecipacao, antecipacao_automatica, taxa_antecipacao_mensal
   - aceita_pix, taxa_pix

5. maquininha_taxas
   - id, maquininha_id, bandeira_id
   - tipo_transacao (debito/credito_avista/credito_parcelado)
   - numero_parcelas (NULL para débito, 2-12 para parcelado)
   - taxa_percentual, taxa_fixa

6. venda_baixas (ADICIONAR COLUNAS)
   + maquininha_id
   + bandeira_id
   + tipo_transacao
   + data_prevista_recebimento
   + data_efetiva_recebimento
   + status_recebimento
```

### Backend - Novas Entidades

```java
1. ContaBancaria.java
2. Adquirente.java
3. Bandeira.java
4. Maquininha.java
5. MaquininhaTaxa.java
6. VendaBaixa.java (ATUALIZAR)
```

### Backend - Novos Services

```java
1. MaquininhaService
   - calcularTaxaTransacao()
   - calcularDataRecebimento()
   - calcularValorLiquido()
   - listarRecebimentosPendentes()

2. FluxoCaixaService
   - projetarRecebimentos()
   - listarRecebimentosPorPeriodo()
   - calcularSaldoProjetado()

3. RelatorioFinanceiroService
   - gerarDRE()
   - vendas PorFormaPagamento()
   - taxasPorOperadora()
```

### Frontend - Novas Views

```vue
1. MaquininhasView.vue (lista) 2. MaquininhaNovaView.vue (cadastro wizard 4
etapas) 3. MaquininhaEditarView.vue 4. FluxoCaixaView.vue (dashboard projeção)
5. RelatoriosFinanceirosView.vue 6. ReconciliacaoBancariaView.vue
```

---

## 🔄 Integração com Sistema Atual

### O Que MANTER

✅ Todo o sistema de `AnimalServico` e pacotes
✅ Sistema de `Venda` e distribuição proporcional
✅ `VendaItem` com `valor_pago_item`
✅ Lógica de status automático
✅ Serviços Adicionais

### O Que ADICIONAR

➕ Novas tabelas de maquininhas
➕ Colunas em `venda_baixas`
➕ Novos serviços e controllers
➕ Views de gerenciamento

### O Que NÃO QUEBRAR

⚠️ `forma_pagamento` continua existindo para dinheiro, PIX direto, etc.
⚠️ Vendas antigas sem maquininha continuam funcionando
⚠️ Sistema proporcional mantém intacto

---

## 📈 Complexidade Estimada

### Backend

- **Banco:** 6 novas tabelas + alterações = **Médio** (2-3 dias)
- **Entidades:** 5 novas + atualização = **Médio** (2 dias)
- **Services:** 3 novos com lógica complexa = **Alto** (5-7 dias)
- **Controllers:** 4 novos = **Baixo** (1-2 dias)
- **Testes:** Unitários + integração = **Alto** (3-4 dias)

**Total Backend:** 13-18 dias (2.5-3.5 semanas)

### Frontend

- **Views Cadastro:** Wizard 4 etapas = **Alto** (3-4 dias)
- **Fluxo de Caixa:** Dashboard complexo = **Alto** (4-5 dias)
- **Relatórios:** Gráficos e tabelas = **Médio** (3-4 dias)
- **Integração API:** Calls e tipos = **Médio** (2-3 dias)
- **Testes UX:** = **Baixo** (1-2 dias)

**Total Frontend:** 13-18 dias (2.5-3.5 semanas)

### TOTAL ESTIMADO: **5-7 semanas** (1 desenvolvedor)

---

## 💡 Recomendações para Implementação

### Fase 1: MVP Maquininhas (2 semanas)

1. Criar tabelas básicas (contas, adquirentes, bandeiras, maquininhas)
2. CRUD de maquininhas (backend + frontend)
3. Adicionar colunas em `venda_baixas`
4. Permitir selecionar maquininha ao registrar pagamento

### Fase 2: Taxas e Cálculos (1.5 semanas)

1. Tabela `maquininha_taxas`
2. Lógica de cálculo de taxas
3. Cálculo automático de data de recebimento
4. Atualizar baixa para usar taxa correta

### Fase 3: Fluxo de Caixa (1.5 semanas)

1. Service de projeção
2. Dashboard de recebimentos
3. Marcar como recebido
4. Relatórios básicos

### Fase 4: Refinamentos (2 semanas)

1. Reconciliação bancária
2. Relatórios avançados
3. Gráficos e métricas
4. Polimento UX

---

## 🎯 Próximos Passos Imediatos

1. ✅ **Validar esta análise** - Está alinhado com sua visão?
2. ✅ **Definir prioridades** - Quais features são essenciais?
3. ✅ **Criar protótipo de telas** - Desenhar fluxos antes de codar
4. ✅ **Modelar banco detalhadamente** - Validar relacionamentos
5. ✅ **Dividir em sprints** - Entregas incrementais

---

## 📞 Perguntas para Você

1. **Escopo:** Esta análise cobre tudo que você precisa ou faltou algo?
2. **Prioridade:** Qual parte é mais urgente? (Maquininhas, Fluxo Caixa, Relatórios?)
3. **Complexidade:** Prefere começar simples e evoluir ou já fazer completo?
4. **Integrações:** Tem alguma API externa para integrar? (Conciliação bancária automática?)
5. **Usuários:** Quantas maquininhas vocês têm? Quantas vendas por dia?

---

**🚀 Estou pronto para começar a implementação assim que você validar a abordagem!**
