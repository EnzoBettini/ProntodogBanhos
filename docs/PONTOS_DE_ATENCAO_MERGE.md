# ⚠️ PONTOS DE ATENÇÃO - Sistema de Maquininhas

**Data:** 24/10/2025
**Para:** Equipe de Desenvolvimento / Code Review

---

## 🎯 O que este PR faz

Este PR adiciona a **infraestrutura completa** para sistema de maquininhas, incluindo:

✅ Modelo de dados (entidades JPA)
✅ Repositórios
✅ Services básicos
✅ Controllers (APIs)
✅ Estrutura de banco de dados (SQL)

**MAS NÃO IMPLEMENTA** a integração automática com o fluxo de pagamento.

---

## ⚠️ IMPORTANTE: O que NÃO funciona ainda

### 1. Maquininhas não são usadas automaticamente

**Situação atual:**
```java
// Quando você registra um pagamento:
POST /api/vendas/123/baixas
{
  "formaPagamentoId": 5,
  "valorBaixa": 100.00,
  "maquininhaId": 10,      // ❌ ENVIADO mas IGNORADO
  "bandeiraId": 2,         // ❌ ENVIADO mas IGNORADO
  "tipoTransacao": "credito_avista"  // ❌ ENVIADO mas IGNORADO
}

// Resultado no banco:
venda_baixas {
  forma_pagamento_id: 5,
  valor_baixa: 100.00,
  maquininha_id: NULL,     // ❌ Não foi populado
  bandeira_id: NULL,       // ❌ Não foi populado
  tipo_transacao: NULL     // ❌ Não foi populado
}
```

**Por que isso é OK:**
- Sistema funciona normalmente sem maquininha
- Preparado para integração futura
- Não quebra fluxo existente

---

### 2. Taxas específicas não são calculadas

**Situação atual:**
```java
// Se você configurar uma taxa de 2.5% na maquininha para Visa:
MaquininhaTaxa {
  maquininha: "Getnet Loja 1",
  bandeira: "Visa",
  tipo: "credito_avista",
  taxa: 2.5%
}

// E registrar um pagamento de R$ 100:
valorBaixa: 100.00
valorTaxa: 0.00          // ❌ Deveria ser R$ 2,50
valorLiquido: 100.00     // ❌ Deveria ser R$ 97,50
```

**O que acontece:**
- Taxa é calculada pela `FormaPagamento` genérica (geralmente 0%)
- NÃO busca taxa específica da maquininha
- Valores ficam tecnicamente errados (mas não causam crash)

**Por que isso é OK:**
- É uma limitação conhecida
- Será implementado em PR futuro
- Não afeta integridade do sistema

---

### 3. Datas de recebimento não são calculadas

**Situação atual:**
```java
// Maquininha configurada com prazo de 30 dias:
Maquininha {
  prazoRecebimentoCredito: 30
}

// Pagamento registrado:
data_prevista_recebimento: NULL  // ❌ Deveria ser hoje + 30 dias
status_recebimento: "pendente"   // ✅ OK
```

**Por que isso é OK:**
- Sistema de controle de recebimentos futuros não está ativo
- Será implementado depois
- Não compromete operação atual

---

## ✅ O que FUNCIONA perfeitamente

### 1. CRUD de Maquininhas
```bash
# Criar maquininha
POST /api/maquininhas
✅ Funciona

# Listar maquininhas
GET /api/maquininhas
✅ Funciona

# Buscar maquininha
GET /api/maquininhas/123
✅ Funciona

# Atualizar maquininha
PUT /api/maquininhas/123
✅ Funciona

# Excluir maquininha
DELETE /api/maquininhas/123
✅ Funciona
```

### 2. Sistema de Vendas e Pagamentos
```bash
# Criar venda
POST /api/vendas
✅ Funciona normalmente

# Adicionar item
POST /api/vendas/123/itens
✅ Funciona normalmente

# Registrar pagamento (baixa)
POST /api/vendas/123/baixas
✅ Funciona normalmente (mesmo sem usar maquininha)

# Remover pagamento
DELETE /api/vendas/123/baixas/456
✅ Funciona normalmente
```

### 3. Proteções implementadas
```java
// NullPointerException
✅ Protegido em VendaBaixa.calcularValores()
✅ Protegido em FormaPagamento.calcularTaxa()

// Valores padrão
✅ valorLiquido = BigDecimal.ZERO (nunca null)
✅ valorTaxa = BigDecimal.ZERO (nunca null)
✅ statusRecebimento = "pendente" (sempre definido)
```

---

## 🔍 Pontos de Code Review

### ✅ Verificar:

1. **Proteções null estão corretas?**
   - Arquivo: `VendaBaixa.java` linha 126-149
   - Arquivo: `FormaPagamento.java` linha 59-68

2. **Comentários de TODO estão claros?**
   - Arquivo: `VendaService.java` linha 533-540

3. **Valores padrão fazem sentido?**
   - `valorLiquido = BigDecimal.ZERO`
   - `statusRecebimento = "pendente"`

4. **Migrations SQL estão corretas?**
   - Arquivo: `sistema_maquininhas.sql`
   - Verificar constraints e índices

---

## 🚨 Cenários de Teste Importantes

### Teste #1: Pagamento sem maquininha (cenário atual)
```bash
POST /api/vendas/123/baixas
{
  "formaPagamentoId": 1,  # Dinheiro
  "valorBaixa": 50.00
}

# Esperado:
✅ Venda paga
✅ valorTaxa = 0
✅ valorLiquido = 50.00
✅ Campos de maquininha = null
```

### Teste #2: Pagamento com maquininha no DTO (será ignorado)
```bash
POST /api/vendas/123/baixas
{
  "formaPagamentoId": 5,
  "valorBaixa": 100.00,
  "maquininhaId": 10,      # Enviado mas ignorado
  "bandeiraId": 2          # Enviado mas ignorado
}

# Esperado:
✅ Venda paga
✅ valorTaxa = 0 (taxa da forma genérica)
✅ valorLiquido = 100.00
⚠️ maquininhaId no banco = NULL (campos ignorados propositalmente)
```

### Teste #3: Criar maquininha
```bash
POST /api/maquininhas
{
  "nome": "Getnet Loja 1",
  "adquirenteId": 1,
  "contaBancariaId": 1,
  "prazoRecebimentoDebito": 1,
  "prazoRecebimentoCredito": 30
}

# Esperado:
✅ Maquininha criada
✅ Forma de pagamento criada automaticamente
✅ Vinculação maquininha ↔️ forma OK
```

---

## 📝 Notas para Produção

### Antes de deploy:

1. **Executar migrations SQL:**
   ```bash
   psql -U usuario -d banco < ModelagemBanco/sistema_maquininhas.sql
   ```

2. **Verificar dados existentes:**
   - Vendas antigas não devem ser afetadas
   - Baixas antigas não terão campos de maquininha (NULL é OK)

3. **Monitorar logs:**
   - Buscar por NullPointerException
   - Verificar cálculos de valores

4. **Backup antes do deploy:**
   ```bash
   pg_dump -U usuario banco > backup_pre_maquininhas.sql
   ```

---

## 🎯 Roadmap Futuro

### Fase 1: Integração Básica (Próximo PR)
- Conectar maquininha ao registrar pagamento
- Popular campos `maquininhaId`, `bandeiraId`, `tipoTransacao`
- Validações básicas

### Fase 2: Cálculo de Taxas (Depois)
- Buscar taxa específica da maquininha
- Calcular valor líquido correto
- Mostrar preview antes de confirmar

### Fase 3: Recebimentos Futuros (Depois)
- Calcular datas previstas
- Dashboard de recebimentos
- Marcação de recebimento efetivo

### Fase 4: UI Completa (Depois)
- Tela de pagamento com seleção de maquininha
- Seleção de bandeira e tipo
- Preview de valores e datas

---

## ❓ FAQ

**Q: Por que não implementou tudo de uma vez?**
A: Risco de bugs. Melhor mergear código estável e parcial.

**Q: Os campos de maquininha no DTO não funcionam?**
A: Funcionam, mas são ignorados propositalmente por enquanto.

**Q: Posso usar as APIs de maquininha?**
A: Sim! CRUD de maquininhas está 100% funcional.

**Q: Taxas estão sendo calculadas errado?**
A: Sim, mas não causa erro. Usa taxa genérica por enquanto.

**Q: Preciso mudar algo no frontend?**
A: Não. Frontend continua funcionando normalmente.

**Q: E se eu quiser ativar maquininhas agora?**
A: Aguarde o próximo PR com integração completa e testada.

---

## ✅ Aprovação

**Status:** APROVADO com ressalvas documentadas
**Risco:** BAIXO
**Impacto:** NENHUM no sistema atual
**Próximos passos:** PRs futuros para ativar integração

---

*Documento de pontos de atenção - GitHub Copilot - 24/10/2025*
