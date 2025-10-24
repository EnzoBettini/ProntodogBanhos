-- ================================================================================
-- SCHEMA COMPLETO - ProntodogBanhos
-- ================================================================================
-- Este arquivo consolida todos os scripts SQL necessários para criar o banco
-- de dados completo do sistema ProntodogBanhos.
--
-- Data de criação: 21/10/2025
-- Ordem de execução: Respeitada conforme ORDEM_EXECUCAO.txt
--
-- IMPORTANTE: Execute este arquivo em um banco de dados limpo ou certifique-se
-- de que não há conflitos com estruturas existentes.
-- ================================================================================


-- ================================================================================
-- ETAPA 1: CRIAR TABELAS BASE
-- Fonte: criar_tables.sql
-- Descrição: Cria todas as tabelas base do sistema
-- ================================================================================

CREATE TABLE banhoetosa.clientes (
    id BIGSERIAL PRIMARY KEY,
    codigo_cliente_sistema BIGSERIAL UNIQUE,
    nome_completo VARCHAR NOT NULL,
    cpf VARCHAR(11) UNIQUE,
    codigo_simplesvet BIGINT UNIQUE
);

CREATE TABLE banhoetosa.animais (
    id BIGSERIAL PRIMARY KEY,
    codigo_animal_sistema BIGSERIAL UNIQUE,
    nome VARCHAR NOT NULL,
    codigo_simplesvet BIGINT UNIQUE,
    tipo VARCHAR,
    cliente_id BIGINT NOT NULL,
    CONSTRAINT fk_cliente FOREIGN KEY (cliente_id) REFERENCES banhoetosa.clientes (id)
);

CREATE TABLE banhoetosa.servicos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR NOT NULL UNIQUE,
    descricao TEXT,
    quantidade INT,
    valor NUMERIC(10, 2) NOT NULL
);

CREATE TABLE banhoetosa.animal_servico (
    id BIGSERIAL PRIMARY KEY,
    data_servico DATE NOT NULL,
    banhos_usados INT NOT NULL,
    animal_id BIGINT NOT NULL,
    servico_id BIGINT NOT NULL,
    CONSTRAINT fk_animal_id FOREIGN KEY (animal_id) REFERENCES banhoetosa.animais (id),
    CONSTRAINT fk_servico_id FOREIGN KEY (servico_id) REFERENCES banhoetosa.servicos (id)
);

CREATE TABLE banhoetosa.telefones_clientes (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    telefone VARCHAR NOT NULL,
    CONSTRAINT fk_cliente_id_telefone FOREIGN KEY (cliente_id) REFERENCES banhoetosa.clientes (id)
);

CREATE TABLE banhoetosa.email_clientes (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    email VARCHAR NOT NULL,
    CONSTRAINT fk_cliente_id_email FOREIGN KEY (cliente_id) REFERENCES banhoetosa.clientes (id)
);

CREATE TABLE banhoetosa.usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

ALTER TABLE banhoetosa.animal_servico ADD COLUMN usuario_id BIGINT;

ALTER TABLE banhoetosa.animal_servico
ADD CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES banhoetosa.usuarios (id);

ALTER TABLE banhoetosa.animais
ADD COLUMN status VARCHAR(20) DEFAULT 'vivo' NOT NULL CHECK (
    status IN ('vivo', 'falecido')
);

-- Tabela para registrar cada banho individual de um pacote
CREATE TABLE banhoetosa.banhos_individuais (
    id BIGSERIAL PRIMARY KEY,
    animal_servico_id BIGINT NOT NULL,
    data_banho DATE NOT NULL,
    numero_banho INTEGER NOT NULL,
    observacoes TEXT,
    usuario_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_animal_servico FOREIGN KEY (animal_servico_id)
        REFERENCES banhoetosa.animal_servico (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_banho FOREIGN KEY (usuario_id)
        REFERENCES banhoetosa.usuarios (id),
    CONSTRAINT unique_banho_por_servico
        UNIQUE (animal_servico_id, numero_banho)
);

-- Adicionar novos atributos na tabela de animais
ALTER TABLE banhoetosa.animais
ADD COLUMN raca VARCHAR(100);

ALTER TABLE banhoetosa.animais
ADD COLUMN peso NUMERIC(5, 2);

-- Adicionar data de expiração na tabela animal_servico para controle de pacotes
ALTER TABLE banhoetosa.animal_servico
ADD COLUMN data_expiracao DATE;

-- Adicionar controle de pagamento na tabela animal_servico
ALTER TABLE banhoetosa.animal_servico
ADD COLUMN status_pagamento VARCHAR(20) DEFAULT 'em_aberto' NOT NULL CHECK (
    status_pagamento IN ('pago', 'em_aberto', 'cancelado')
);

ALTER TABLE banhoetosa.animal_servico
ADD COLUMN data_pagamento DATE;

INSERT INTO banhoetosa.usuarios (nome, email, senha, role)
VALUES ('Admin Sistema', 'admin@prontodog.com', 'senha123', 'ADMIN');


-- ================================================================================
-- ETAPA 2: SISTEMA DE VENDAS
-- Fonte: vendas_sistema.sql
-- Descrição: Implementa o sistema completo de vendas
-- ================================================================================

-- =====================================================
-- PARTE 1: TABELA DE FORMAS DE PAGAMENTO
-- =====================================================

-- 1.1. Criar tabela de formas de pagamento
CREATE TABLE banhoetosa.formas_pagamento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('dinheiro', 'debito', 'credito', 'pix', 'boleto', 'outro')),
    taxa_percentual DECIMAL(5,2) DEFAULT 0,
    taxa_fixa DECIMAL(10,2) DEFAULT 0,
    parcelas_max INTEGER DEFAULT 1,
    dias_recebimento INTEGER DEFAULT 0,
    ativo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_taxa_percentual_positiva CHECK (taxa_percentual >= 0),
    CONSTRAINT chk_taxa_fixa_positiva CHECK (taxa_fixa >= 0),
    CONSTRAINT chk_parcelas_positivas CHECK (parcelas_max > 0)
);

-- 1.2. Inserir formas de pagamento padrão
INSERT INTO banhoetosa.formas_pagamento (nome, tipo, taxa_percentual, taxa_fixa, parcelas_max, dias_recebimento) VALUES
('Dinheiro', 'dinheiro', 0, 0, 1, 0),
('Débito', 'debito', 2.5, 0, 1, 1),
('PIX', 'pix', 0, 0, 1, 0),
('Crédito à Vista', 'credito', 3.5, 0, 1, 30),
('Crédito 2x', 'credito', 4.0, 0, 2, 30),
('Crédito 3x', 'credito', 4.5, 0, 3, 30),
('Crédito 4x', 'credito', 5.0, 0, 4, 30),
('Crédito 5x', 'credito', 5.5, 0, 5, 30),
('Crédito 6x', 'credito', 6.0, 0, 6, 30),
('Crédito 7x', 'credito', 6.5, 0, 7, 30),
('Crédito 8x', 'credito', 7.0, 0, 8, 30),
('Crédito 9x', 'credito', 7.5, 0, 9, 30),
('Crédito 10x', 'credito', 8.0, 0, 10, 30),
('Crédito 11x', 'credito', 8.5, 0, 11, 30),
('Crédito 12x', 'credito', 9.0, 0, 12, 30),
('Boleto', 'boleto', 0, 2.50, 1, 3);

-- 1.3. Comentários para documentação
COMMENT ON TABLE banhoetosa.formas_pagamento IS 'Cadastro de formas de pagamento aceitas pela clínica';
COMMENT ON COLUMN banhoetosa.formas_pagamento.nome IS 'Nome da forma de pagamento (ex: Crédito 3x)';
COMMENT ON COLUMN banhoetosa.formas_pagamento.tipo IS 'Tipo: dinheiro, debito, credito, pix, boleto, outro';
COMMENT ON COLUMN banhoetosa.formas_pagamento.taxa_percentual IS 'Taxa percentual cobrada pela operadora (%)';
COMMENT ON COLUMN banhoetosa.formas_pagamento.taxa_fixa IS 'Taxa fixa em reais cobrada por transação';
COMMENT ON COLUMN banhoetosa.formas_pagamento.parcelas_max IS 'Número máximo de parcelas permitidas';
COMMENT ON COLUMN banhoetosa.formas_pagamento.dias_recebimento IS 'Prazo em dias para receber o pagamento';

-- =====================================================
-- PARTE 2: TABELA DE VENDAS (Principal)
-- =====================================================

-- 2.1. Criar sequência para código de venda
CREATE SEQUENCE banhoetosa.seq_codigo_venda START 100000;

-- 2.2. Criar tabela de vendas
CREATE TABLE banhoetosa.vendas (
    id BIGSERIAL PRIMARY KEY,
    codigo_venda BIGINT UNIQUE NOT NULL DEFAULT nextval('banhoetosa.seq_codigo_venda'),
    cliente_id BIGINT NOT NULL,
    tipo_venda VARCHAR(50) DEFAULT 'presencial' CHECK (tipo_venda IN ('presencial', 'agendamento', 'busca_entrega')),
    status_venda VARCHAR(20) DEFAULT 'em_aberto' CHECK (status_venda IN ('em_aberto', 'pago', 'parcial', 'cancelado')),
    usuario_id BIGINT NOT NULL,
    data_venda TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Valores financeiros
    valor_bruto DECIMAL(10,2) NOT NULL DEFAULT 0,
    desconto DECIMAL(10,2) DEFAULT 0,
    valor_total DECIMAL(10,2) NOT NULL DEFAULT 0,
    valor_pago DECIMAL(10,2) NOT NULL DEFAULT 0,
    valor_pendente DECIMAL(10,2) NOT NULL DEFAULT 0,

    observacoes TEXT,
    cancelado_em TIMESTAMP,
    motivo_cancelamento TEXT,

    CONSTRAINT fk_venda_cliente FOREIGN KEY (cliente_id) REFERENCES banhoetosa.clientes(id),
    CONSTRAINT fk_venda_usuario FOREIGN KEY (usuario_id) REFERENCES banhoetosa.usuarios(id),
    CONSTRAINT chk_valores_positivos CHECK (valor_bruto >= 0 AND valor_total >= 0 AND valor_pago >= 0 AND valor_pendente >= 0),
    CONSTRAINT chk_desconto_valido CHECK (desconto >= 0 AND desconto <= valor_bruto)
);

-- 2.3. Índices para performance
CREATE INDEX idx_vendas_cliente ON banhoetosa.vendas(cliente_id);
CREATE INDEX idx_vendas_data ON banhoetosa.vendas(data_venda);
CREATE INDEX idx_vendas_status ON banhoetosa.vendas(status_venda);
CREATE INDEX idx_vendas_codigo ON banhoetosa.vendas(codigo_venda);
CREATE INDEX idx_vendas_usuario ON banhoetosa.vendas(usuario_id);

-- 2.4. Comentários para documentação
COMMENT ON TABLE banhoetosa.vendas IS 'Registro de vendas realizadas na clínica (agrupador de serviços)';
COMMENT ON COLUMN banhoetosa.vendas.codigo_venda IS 'Código único da venda para identificação';
COMMENT ON COLUMN banhoetosa.vendas.tipo_venda IS 'Tipo da venda: presencial (balcão), agendamento ou delivery';
COMMENT ON COLUMN banhoetosa.vendas.status_venda IS 'Status: em_aberto, pago, parcial (pago parcialmente), cancelado';
COMMENT ON COLUMN banhoetosa.vendas.valor_bruto IS 'Soma de todos os itens sem desconto';
COMMENT ON COLUMN banhoetosa.vendas.desconto IS 'Desconto aplicado na venda';
COMMENT ON COLUMN banhoetosa.vendas.valor_total IS 'Valor final (bruto - desconto)';
COMMENT ON COLUMN banhoetosa.vendas.valor_pago IS 'Total já recebido';
COMMENT ON COLUMN banhoetosa.vendas.valor_pendente IS 'Saldo devedor';

-- =====================================================
-- PARTE 3: ITENS DA VENDA E VINCULAÇÃO
-- =====================================================

-- 3.1. Criar tabela de itens da venda
CREATE TABLE banhoetosa.venda_itens (
    id BIGSERIAL PRIMARY KEY,
    venda_id BIGINT NOT NULL,
    animal_servico_id BIGINT NOT NULL,
    valor_item DECIMAL(10,2) NOT NULL,
    desconto_item DECIMAL(10,2) DEFAULT 0,
    valor_final_item DECIMAL(10,2) NOT NULL,
    observacoes TEXT,

    CONSTRAINT fk_venda_item_venda FOREIGN KEY (venda_id) REFERENCES banhoetosa.vendas(id) ON DELETE CASCADE,
    CONSTRAINT fk_venda_item_animal_servico FOREIGN KEY (animal_servico_id) REFERENCES banhoetosa.animal_servico(id),
    CONSTRAINT unique_animal_servico_venda UNIQUE (animal_servico_id),
    CONSTRAINT chk_valor_item_positivo CHECK (valor_item >= 0),
    CONSTRAINT chk_desconto_item_valido CHECK (desconto_item >= 0 AND desconto_item <= valor_item),
    CONSTRAINT chk_valor_final_item_positivo CHECK (valor_final_item >= 0)
);

-- 3.2. Adicionar venda_id na tabela animal_servico (vinculação opcional)
ALTER TABLE banhoetosa.animal_servico
ADD COLUMN venda_id BIGINT,
ADD CONSTRAINT fk_animal_servico_venda FOREIGN KEY (venda_id) REFERENCES banhoetosa.vendas(id);

-- 3.3. Índices
CREATE INDEX idx_venda_itens_venda ON banhoetosa.venda_itens(venda_id);
CREATE INDEX idx_venda_itens_animal_servico ON banhoetosa.venda_itens(animal_servico_id);
CREATE INDEX idx_animal_servico_venda ON banhoetosa.animal_servico(venda_id);

-- 3.4. Comentários
COMMENT ON TABLE banhoetosa.venda_itens IS 'Itens que compõem uma venda (cada item é um animal_servico)';
COMMENT ON COLUMN banhoetosa.venda_itens.valor_item IS 'Valor do serviço no momento da venda';
COMMENT ON COLUMN banhoetosa.venda_itens.desconto_item IS 'Desconto aplicado neste item específico';
COMMENT ON COLUMN banhoetosa.venda_itens.valor_final_item IS 'Valor final do item (valor_item - desconto_item)';

-- =====================================================
-- PARTE 4: BAIXAS (Pagamentos Recebidos)
-- =====================================================

-- 4.1. Criar tabela de baixas (pagamentos)
CREATE TABLE banhoetosa.venda_baixas (
    id BIGSERIAL PRIMARY KEY,
    venda_id BIGINT NOT NULL,
    forma_pagamento_id BIGINT NOT NULL,
    valor_baixa DECIMAL(10,2) NOT NULL,
    valor_taxa DECIMAL(10,2) DEFAULT 0,
    valor_liquido DECIMAL(10,2) NOT NULL,
    data_baixa TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    numero_parcelas INTEGER DEFAULT 1,
    data_primeira_parcela DATE,
    observacoes TEXT,
    usuario_id BIGINT NOT NULL,

    CONSTRAINT fk_baixa_venda FOREIGN KEY (venda_id) REFERENCES banhoetosa.vendas(id) ON DELETE CASCADE,
    CONSTRAINT fk_baixa_forma_pagamento FOREIGN KEY (forma_pagamento_id) REFERENCES banhoetosa.formas_pagamento(id),
    CONSTRAINT fk_baixa_usuario FOREIGN KEY (usuario_id) REFERENCES banhoetosa.usuarios(id),
    CONSTRAINT chk_valor_baixa_positivo CHECK (valor_baixa > 0),
    CONSTRAINT chk_parcelas_positivas CHECK (numero_parcelas > 0),
    CONSTRAINT chk_valor_taxa_positivo CHECK (valor_taxa >= 0),
    CONSTRAINT chk_valor_liquido_positivo CHECK (valor_liquido >= 0)
);

-- 4.2. Índices
CREATE INDEX idx_venda_baixas_venda ON banhoetosa.venda_baixas(venda_id);
CREATE INDEX idx_venda_baixas_data ON banhoetosa.venda_baixas(data_baixa);
CREATE INDEX idx_venda_baixas_forma_pagamento ON banhoetosa.venda_baixas(forma_pagamento_id);

-- 4.3. Comentários
COMMENT ON TABLE banhoetosa.venda_baixas IS 'Registro de pagamentos recebidos para cada venda';
COMMENT ON COLUMN banhoetosa.venda_baixas.valor_baixa IS 'Valor pago pelo cliente';
COMMENT ON COLUMN banhoetosa.venda_baixas.valor_taxa IS 'Taxa cobrada pela operadora do cartão/pagamento';
COMMENT ON COLUMN banhoetosa.venda_baixas.valor_liquido IS 'Valor líquido recebido (valor_baixa - valor_taxa)';
COMMENT ON COLUMN banhoetosa.venda_baixas.numero_parcelas IS 'Número de parcelas (para cartão de crédito)';
COMMENT ON COLUMN banhoetosa.venda_baixas.data_primeira_parcela IS 'Data da primeira parcela (para crédito)';

-- =====================================================
-- PARTE 5: VIEWS E TRIGGERS ÚTEIS
-- =====================================================

-- 5.1. View para resumo de vendas com detalhes completos
CREATE VIEW banhoetosa.vw_vendas_completas AS
SELECT
    v.id as venda_id,
    v.codigo_venda,
    v.data_venda,
    v.tipo_venda,
    v.status_venda,

    -- Dados do cliente
    c.id as cliente_id,
    c.nome_completo as cliente_nome,
    c.cpf as cliente_cpf,

    -- Dados do usuário
    u.id as usuario_id,
    u.nome as usuario_nome,

    -- Valores financeiros
    v.valor_bruto,
    v.desconto,
    v.valor_total,
    v.valor_pago,
    v.valor_pendente,

    -- Quantidade de itens
    COUNT(DISTINCT vi.id) as total_itens,
    COUNT(DISTINCT vb.id) as total_baixas,

    -- Observações
    v.observacoes

FROM banhoetosa.vendas v
JOIN banhoetosa.clientes c ON v.cliente_id = c.id
JOIN banhoetosa.usuarios u ON v.usuario_id = u.id
LEFT JOIN banhoetosa.venda_itens vi ON v.id = vi.venda_id
LEFT JOIN banhoetosa.venda_baixas vb ON v.id = vb.venda_id
GROUP BY v.id, c.id, u.id
ORDER BY v.data_venda DESC;

-- 5.2. View para detalhes de itens da venda (sem servicos_adicionais por enquanto)
CREATE VIEW banhoetosa.vw_venda_itens_detalhados_temp AS
SELECT
    vi.id as item_id,
    vi.venda_id,
    v.codigo_venda,

    -- Dados do animal e serviço
    a.id as animal_id,
    a.nome as animal_nome,
    a.tipo as animal_tipo,
    a.raca as animal_raca,

    s.id as servico_id,
    s.nome as servico_nome,

    -- Dados do animal_servico
    asp.id as animal_servico_id,
    asp.data_servico,
    asp.banhos_usados,

    -- Valores do item
    vi.valor_item,
    vi.desconto_item,
    vi.valor_final_item

FROM banhoetosa.venda_itens vi
JOIN banhoetosa.vendas v ON vi.venda_id = v.id
JOIN banhoetosa.animal_servico asp ON vi.animal_servico_id = asp.id
JOIN banhoetosa.animais a ON asp.animal_id = a.id
JOIN banhoetosa.servicos s ON asp.servico_id = s.id
ORDER BY v.data_venda DESC, a.nome;

-- 5.3. View para histórico de baixas
CREATE VIEW banhoetosa.vw_baixas_detalhadas AS
SELECT
    vb.id as baixa_id,
    vb.venda_id,
    v.codigo_venda,

    -- Dados da forma de pagamento
    fp.id as forma_pagamento_id,
    fp.nome as forma_pagamento_nome,
    fp.tipo as forma_pagamento_tipo,

    -- Valores
    vb.valor_baixa,
    vb.valor_taxa,
    vb.valor_liquido,

    -- Data e parcelas
    vb.data_baixa,
    vb.numero_parcelas,
    vb.data_primeira_parcela,

    -- Usuário que registrou
    u.id as usuario_id,
    u.nome as usuario_nome,

    vb.observacoes

FROM banhoetosa.venda_baixas vb
JOIN banhoetosa.vendas v ON vb.venda_id = v.id
JOIN banhoetosa.formas_pagamento fp ON vb.forma_pagamento_id = fp.id
JOIN banhoetosa.usuarios u ON vb.usuario_id = u.id
ORDER BY vb.data_baixa DESC;

-- 5.4. Função para calcular taxa de uma forma de pagamento
CREATE OR REPLACE FUNCTION banhoetosa.calcular_taxa_pagamento(
    p_forma_pagamento_id BIGINT,
    p_valor DECIMAL(10,2)
) RETURNS DECIMAL(10,2) AS $$
DECLARE
    v_taxa_percentual DECIMAL(5,2);
    v_taxa_fixa DECIMAL(10,2);
    v_taxa_total DECIMAL(10,2);
BEGIN
    -- Buscar taxas da forma de pagamento
    SELECT taxa_percentual, taxa_fixa
    INTO v_taxa_percentual, v_taxa_fixa
    FROM banhoetosa.formas_pagamento
    WHERE id = p_forma_pagamento_id;

    -- Calcular taxa total
    v_taxa_total := (p_valor * v_taxa_percentual / 100) + v_taxa_fixa;

    RETURN v_taxa_total;
END;
$$ LANGUAGE plpgsql;

-- 5.5. Comentários nas views
COMMENT ON VIEW banhoetosa.vw_vendas_completas IS 'View com resumo completo de todas as vendas';
COMMENT ON VIEW banhoetosa.vw_venda_itens_detalhados_temp IS 'View com detalhes de cada item das vendas (temporária, será substituída)';
COMMENT ON VIEW banhoetosa.vw_baixas_detalhadas IS 'View com histórico completo de baixas/pagamentos';


-- ================================================================================
-- ETAPA 3: CORRIGIR CONSTRAINT DE STATUS_PAGAMENTO
-- Fonte: fix_status_pagamento_constraint.sql
-- Descrição: Adiciona suporte para status "parcial" em animal_servico
-- ================================================================================

-- Remover todos os possíveis constraints de status_pagamento
ALTER TABLE banhoetosa.animal_servico
DROP CONSTRAINT IF EXISTS animal_servico_status_pagamento_check;

ALTER TABLE banhoetosa.animal_servico
DROP CONSTRAINT IF EXISTS chk_status_pagamento;

-- Adicionar o novo constraint com suporte a 'parcial'
ALTER TABLE banhoetosa.animal_servico
ADD CONSTRAINT animal_servico_status_pagamento_check
CHECK (status_pagamento IN ('em_aberto', 'parcial', 'pago', 'cancelado'));

-- Comentário
COMMENT ON CONSTRAINT animal_servico_status_pagamento_check ON banhoetosa.animal_servico IS
'Status do pagamento: em_aberto (0%), parcial (1-99%), pago (100%), cancelado';


-- ================================================================================
-- ETAPA 4: ADICIONAR VALOR_COBRADO EM ANIMAL_SERVICO
-- Fonte: add_valor_cobrado_animal_servico.sql
-- Descrição: Adiciona coluna valor_cobrado em animal_servico
-- ================================================================================

ALTER TABLE banhoetosa.animal_servico
ADD COLUMN IF NOT EXISTS valor_cobrado DECIMAL(10,2);

-- Comentário
COMMENT ON COLUMN banhoetosa.animal_servico.valor_cobrado IS
'Valor efetivamente cobrado por este serviço (pode ser diferente do valor de catálogo devido a descontos ou acréscimos aplicados na venda)';

-- Inicializar com o valor do catálogo para registros existentes
UPDATE banhoetosa.animal_servico asrv
SET valor_cobrado = s.valor
FROM banhoetosa.servicos s
WHERE asrv.servico_id = s.id
  AND asrv.valor_cobrado IS NULL;


-- ================================================================================
-- ETAPA 5: ADICIONAR VALOR_PAGO_ITEM EM VENDA_ITENS
-- Fonte: add_valor_pago_item.sql
-- Descrição: Adiciona coluna valor_pago_item e função de cálculo
-- ================================================================================

-- 1. Adicionar coluna valor_pago_item em venda_itens
ALTER TABLE banhoetosa.venda_itens
ADD COLUMN IF NOT EXISTS valor_pago_item DECIMAL(10,2) DEFAULT 0.00 NOT NULL;

-- Constraint para garantir que valor_pago_item não seja maior que valor_final_item
ALTER TABLE banhoetosa.venda_itens
ADD CONSTRAINT chk_valor_pago_item_valido
CHECK (valor_pago_item >= 0 AND valor_pago_item <= valor_final_item);

-- 2. Atualizar constraint de status_pagamento em animal_servico para incluir 'parcial'
ALTER TABLE banhoetosa.animal_servico
DROP CONSTRAINT IF EXISTS chk_status_pagamento;

ALTER TABLE banhoetosa.animal_servico
ADD CONSTRAINT chk_status_pagamento
CHECK (status_pagamento IN ('em_aberto', 'parcial', 'pago', 'cancelado'));

-- 3. Comentários
COMMENT ON COLUMN banhoetosa.venda_itens.valor_pago_item IS
'Valor já pago deste item específico (distribuído proporcionalmente das baixas da venda)';

COMMENT ON CONSTRAINT chk_status_pagamento ON banhoetosa.animal_servico IS
'Status do pagamento: em_aberto (0%), parcial (1-99%), pago (100%), cancelado';

-- 4. Inicializar valor_pago_item dos itens existentes baseado no status atual
UPDATE banhoetosa.venda_itens vi
SET valor_pago_item = vi.valor_final_item
FROM banhoetosa.animal_servico as_tbl
WHERE vi.animal_servico_id = as_tbl.id
  AND as_tbl.status_pagamento = 'pago';

-- 5. Criar função para calcular percentual pago de um item
CREATE OR REPLACE FUNCTION banhoetosa.calcular_percentual_pago_item(
    p_valor_pago DECIMAL(10,2),
    p_valor_total DECIMAL(10,2)
) RETURNS DECIMAL(5,2) AS $$
BEGIN
    IF p_valor_total = 0 THEN
        RETURN 0;
    END IF;
    RETURN ROUND((p_valor_pago / p_valor_total) * 100, 2);
END;
$$ LANGUAGE plpgsql IMMUTABLE;

COMMENT ON FUNCTION banhoetosa.calcular_percentual_pago_item IS
'Calcula o percentual pago de um item da venda (0-100%)';


-- ================================================================================
-- ETAPA 6: ADICIONAR STATUS_SERVICO E DATA_REALIZACAO
-- Fonte: adicionar_status_servico.sql
-- Descrição: Controla serviços únicos (não-pacotes)
-- ================================================================================

-- Adicionar coluna status_servico
ALTER TABLE banhoetosa.animal_servico
ADD COLUMN IF NOT EXISTS status_servico VARCHAR(20);

-- Adicionar coluna data_realizacao
ALTER TABLE banhoetosa.animal_servico
ADD COLUMN IF NOT EXISTS data_realizacao DATE;

-- Comentários nas colunas para documentação
COMMENT ON COLUMN banhoetosa.animal_servico.status_servico IS 'Status do serviço único: "pendente" ou "realizado". NULL para pacotes (múltiplos banhos)';
COMMENT ON COLUMN banhoetosa.animal_servico.data_realizacao IS 'Data em que o serviço único foi realizado. NULL se ainda pendente ou se for um pacote';


-- ================================================================================
-- ETAPA 7: RESOLVER CONFLITO DE VIEWS
-- Fonte: fix_view_conflict.sql
-- Descrição: Remove view vw_atendimentos_completos e prepara para servicos_adicionais
-- ================================================================================

-- Remover a view temporariamente
DROP VIEW IF EXISTS banhoetosa.vw_atendimentos_completos CASCADE;

-- Verificar se a tabela servicos_adicionais existe e criar estrutura básica se necessário
DO $$
BEGIN
    -- Adicionar coluna pode_ser_adicional
    IF NOT EXISTS (SELECT FROM information_schema.columns
                   WHERE table_schema = 'banhoetosa'
                   AND table_name = 'servicos'
                   AND column_name = 'pode_ser_adicional') THEN
        ALTER TABLE banhoetosa.servicos
        ADD COLUMN pode_ser_adicional BOOLEAN DEFAULT TRUE;
    END IF;

    -- Adicionar coluna categoria
    IF NOT EXISTS (SELECT FROM information_schema.columns
                   WHERE table_schema = 'banhoetosa'
                   AND table_name = 'servicos'
                   AND column_name = 'categoria') THEN
        ALTER TABLE banhoetosa.servicos
        ADD COLUMN categoria VARCHAR(100) DEFAULT 'geral';
    END IF;
END $$;


-- ================================================================================
-- ETAPA 8: CRIAR TABELA SERVICOS_ADICIONAIS E VIEW
-- Fonte: servicos_adicionais.sql
-- Descrição: Implementa sistema de serviços adicionais
-- ================================================================================

-- 1. Nova tabela para relacionar serviços principais com adicionais
CREATE TABLE IF NOT EXISTS banhoetosa.servicos_adicionais (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    animal_servico_principal_id BIGINT NOT NULL,
    servico_adicional_id BIGINT NOT NULL,
    quantidade_adicional INTEGER NOT NULL DEFAULT 1,
    valor_unitario DECIMAL(10,2) NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    data_adicao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observacoes TEXT,
    status_pagamento VARCHAR(20) DEFAULT 'em_aberto',
    data_pagamento DATE,
    usuario_id BIGINT NOT NULL,

    -- Chaves estrangeiras
    CONSTRAINT fk_servico_adicional_principal
        FOREIGN KEY (animal_servico_principal_id)
        REFERENCES banhoetosa.animal_servico(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_servico_adicional_servico
        FOREIGN KEY (servico_adicional_id)
        REFERENCES banhoetosa.servicos(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_servico_adicional_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES banhoetosa.usuarios(id)
        ON DELETE RESTRICT,

    -- Constraints
    CONSTRAINT chk_quantidade_positiva CHECK (quantidade_adicional > 0),
    CONSTRAINT chk_valor_positivo CHECK (valor_unitario >= 0 AND valor_total >= 0),
    CONSTRAINT chk_servicos_adicionais_status_pagamento CHECK (status_pagamento IN ('em_aberto', 'pago', 'cancelado'))
);

-- 2. Índices para performance
CREATE INDEX IF NOT EXISTS idx_servicos_adicionais_principal ON banhoetosa.servicos_adicionais(animal_servico_principal_id);
CREATE INDEX IF NOT EXISTS idx_servicos_adicionais_servico ON banhoetosa.servicos_adicionais(servico_adicional_id);
CREATE INDEX IF NOT EXISTS idx_servicos_adicionais_data ON banhoetosa.servicos_adicionais(data_adicao);
CREATE INDEX IF NOT EXISTS idx_servicos_adicionais_status ON banhoetosa.servicos_adicionais(status_pagamento);

-- 3. Inserir categorias nos serviços existentes
UPDATE banhoetosa.servicos SET categoria = 'banho' WHERE nome ILIKE '%banho%';
UPDATE banhoetosa.servicos SET categoria = 'tosa' WHERE nome ILIKE '%tosa%';
UPDATE banhoetosa.servicos SET categoria = 'pacote' WHERE quantidade > 1;

-- 4. Comentários para documentação
COMMENT ON TABLE banhoetosa.servicos_adicionais IS 'Registra serviços adicionais realizados durante um atendimento principal';
COMMENT ON COLUMN banhoetosa.servicos_adicionais.animal_servico_principal_id IS 'ID do serviço principal (ex: banho agendado)';
COMMENT ON COLUMN banhoetosa.servicos_adicionais.servico_adicional_id IS 'ID do serviço adicional realizado (ex: tosa)';
COMMENT ON COLUMN banhoetosa.servicos_adicionais.quantidade_adicional IS 'Quantidade do serviço adicional realizado';
COMMENT ON COLUMN banhoetosa.servicos_adicionais.valor_unitario IS 'Valor unitário do adicional no momento da realização';
COMMENT ON COLUMN banhoetosa.servicos_adicionais.valor_total IS 'Valor total (quantidade * valor_unitario)';

-- 5. View para facilitar consultas
CREATE VIEW banhoetosa.vw_atendimentos_completos AS
SELECT
    asp.id as animal_servico_id,
    asp.data_servico,
    asp.status_pagamento as status_principal,
    a.nome as animal_nome,
    c.nome_completo as cliente_nome,
    s.nome as servico_principal,
    s.valor as valor_principal,

    -- Adicionais
    sa.id as adicional_id,
    s_add.nome as servico_adicional,
    sa.quantidade_adicional,
    sa.valor_unitario as valor_adicional_unitario,
    sa.valor_total as valor_adicional_total,
    sa.status_pagamento as status_adicional,
    sa.data_adicao,

    -- Valores totais
    s.valor + COALESCE(SUM(sa.valor_total) OVER (PARTITION BY asp.id), 0) as valor_total_atendimento

FROM banhoetosa.animal_servico asp
JOIN banhoetosa.animais a ON asp.animal_id = a.id
JOIN banhoetosa.clientes c ON a.cliente_id = c.id
JOIN banhoetosa.servicos s ON asp.servico_id = s.id
LEFT JOIN banhoetosa.servicos_adicionais sa ON asp.id = sa.animal_servico_principal_id
LEFT JOIN banhoetosa.servicos s_add ON sa.servico_adicional_id = s_add.id
ORDER BY asp.data_servico DESC, sa.data_adicao;


-- ================================================================================
-- ETAPA 9: ADICIONAR DATA_REALIZACAO EM SERVICOS_ADICIONAIS
-- Fonte: adicionar_data_realizacao_servico_adicional.sql
-- Descrição: Permite edição da data de realização do serviço adicional
-- ================================================================================

-- 1. Adicionar coluna data_realizacao à tabela servicos_adicionais
ALTER TABLE banhoetosa.servicos_adicionais
ADD COLUMN IF NOT EXISTS data_realizacao DATE;

-- 2. Atualizar registros existentes: herdar data_servico do serviço pai
UPDATE banhoetosa.servicos_adicionais sa
SET data_realizacao = (
    SELECT asp.data_servico
    FROM banhoetosa.animal_servico asp
    WHERE asp.id = sa.animal_servico_principal_id
)
WHERE sa.data_realizacao IS NULL;

-- 3. Tornar a coluna NOT NULL após preencher os valores existentes
-- Comentado para não falhar caso não existam registros ainda
-- ALTER TABLE banhoetosa.servicos_adicionais
-- ALTER COLUMN data_realizacao SET NOT NULL;

-- 4. Adicionar comentário para documentação
COMMENT ON COLUMN banhoetosa.servicos_adicionais.data_realizacao IS 'Data em que o serviço adicional foi realizado (por padrão, herda do serviço principal)';

-- 5. Criar índice para melhorar performance de consultas por data
CREATE INDEX IF NOT EXISTS idx_servicos_adicionais_data_realizacao
ON banhoetosa.servicos_adicionais(data_realizacao);


-- ================================================================================
-- ETAPA 10: FIX ADICIONAL PARA DATA_REALIZACAO
-- Fonte: fix_add_data_realizacao.sql
-- Descrição: Garante que data_realizacao existe em servicos_adicionais
-- ================================================================================

-- 1. Adicionar coluna data_realizacao (se não existir)
ALTER TABLE banhoetosa.servicos_adicionais
ADD COLUMN IF NOT EXISTS data_realizacao DATE;

-- 2. Atualizar registros existentes: herdar data_servico do serviço pai
UPDATE banhoetosa.servicos_adicionais sa
SET data_realizacao = (
    SELECT asp.data_servico
    FROM banhoetosa.animal_servico asp
    WHERE asp.id = sa.animal_servico_principal_id
)
WHERE sa.data_realizacao IS NULL;

-- 3. Adicionar comentário para documentação
COMMENT ON COLUMN banhoetosa.servicos_adicionais.data_realizacao IS
'Data em que o serviço adicional foi realizado (por padrão, herda do serviço principal)';

-- 4. Criar índice para melhorar performance de consultas por data
CREATE INDEX IF NOT EXISTS idx_servicos_adicionais_data_realizacao
ON banhoetosa.servicos_adicionais(data_realizacao);


-- ================================================================================
-- ETAPA 11: CORRIGIR CONSTRAINT DE STATUS EM SERVICOS_ADICIONAIS
-- Fonte: fix_servicos_adicionais_status_constraint.sql
-- Descrição: Adiciona suporte para status "parcial" em servicos_adicionais
-- ================================================================================

-- Remover constraint antiga
ALTER TABLE banhoetosa.servicos_adicionais
DROP CONSTRAINT IF EXISTS chk_status_pagamento;

ALTER TABLE banhoetosa.servicos_adicionais
DROP CONSTRAINT IF EXISTS chk_servicos_adicionais_status_pagamento;

-- Adicionar nova constraint com suporte a 'parcial'
ALTER TABLE banhoetosa.servicos_adicionais
ADD CONSTRAINT chk_status_pagamento
CHECK (status_pagamento IN ('em_aberto', 'parcial', 'pago', 'cancelado'));

-- Comentário
COMMENT ON CONSTRAINT chk_status_pagamento ON banhoetosa.servicos_adicionais IS
'Status do pagamento: em_aberto (0%), parcial (1-99%), pago (100%), cancelado';

COMMENT ON COLUMN banhoetosa.servicos_adicionais.status_pagamento IS
'Status do pagamento do serviço adicional (herda do serviço principal se não especificado): em_aberto, parcial, pago, cancelado';


-- ================================================================================
-- ETAPA 12: RECRIAR VIEW DE ITENS DETALHADOS COM SERVICOS_ADICIONAIS
-- Descrição: Substitui a view temporária pela versão final
-- ================================================================================

-- Remover view temporária
DROP VIEW IF EXISTS banhoetosa.vw_venda_itens_detalhados_temp;

-- Criar view final com suporte a serviços adicionais
CREATE VIEW banhoetosa.vw_venda_itens_detalhados AS
SELECT
    vi.id as item_id,
    vi.venda_id,
    v.codigo_venda,

    -- Dados do animal e serviço
    a.id as animal_id,
    a.nome as animal_nome,
    a.tipo as animal_tipo,
    a.raca as animal_raca,

    s.id as servico_id,
    s.nome as servico_nome,

    -- Dados do animal_servico
    asp.id as animal_servico_id,
    asp.data_servico,
    asp.banhos_usados,

    -- Valores do item
    vi.valor_item,
    vi.desconto_item,
    vi.valor_final_item,

    -- Serviços adicionais deste animal_servico
    COALESCE(SUM(sa.valor_total), 0) as valor_adicionais,
    COUNT(sa.id) as quantidade_adicionais

FROM banhoetosa.venda_itens vi
JOIN banhoetosa.vendas v ON vi.venda_id = v.id
JOIN banhoetosa.animal_servico asp ON vi.animal_servico_id = asp.id
JOIN banhoetosa.animais a ON asp.animal_id = a.id
JOIN banhoetosa.servicos s ON asp.servico_id = s.id
LEFT JOIN banhoetosa.servicos_adicionais sa ON asp.id = sa.animal_servico_principal_id
GROUP BY vi.id, v.id, a.id, s.id, asp.id
ORDER BY v.data_venda DESC, a.nome;

COMMENT ON VIEW banhoetosa.vw_venda_itens_detalhados IS 'View com detalhes de cada item das vendas';


-- ================================================================================
-- FIM DO SCHEMA COMPLETO
-- ================================================================================

-- Para verificar se tudo foi criado corretamente, execute:
SELECT 'Tabelas criadas com sucesso!' as mensagem;

SELECT
    'clientes' as tabela, COUNT(*) as registros FROM banhoetosa.clientes
UNION ALL
SELECT 'animais', COUNT(*) FROM banhoetosa.animais
UNION ALL
SELECT 'servicos', COUNT(*) FROM banhoetosa.servicos
UNION ALL
SELECT 'animal_servico', COUNT(*) FROM banhoetosa.animal_servico
UNION ALL
SELECT 'usuarios', COUNT(*) FROM banhoetosa.usuarios
UNION ALL
SELECT 'formas_pagamento', COUNT(*) FROM banhoetosa.formas_pagamento
UNION ALL
SELECT 'vendas', COUNT(*) FROM banhoetosa.vendas
UNION ALL
SELECT 'venda_itens', COUNT(*) FROM banhoetosa.venda_itens
UNION ALL
SELECT 'venda_baixas', COUNT(*) FROM banhoetosa.venda_baixas
UNION ALL
SELECT 'servicos_adicionais', COUNT(*) FROM banhoetosa.servicos_adicionais;

-- ================================================================================
-- OBSERVAÇÕES FINAIS
-- ================================================================================
--
-- 1. Este arquivo consolida todos os scripts de migração em ordem
-- 2. É seguro executar múltiplas vezes (usa IF EXISTS/IF NOT EXISTS)
-- 3. O arquivo testes_banco.sql foi excluído pois contém apenas queries de teste
-- 4. Todos os índices, comentários e constraints estão incluídos
-- 5. As views foram recriadas na ordem correta para evitar conflitos
--
-- Para suporte ou dúvidas, consulte os arquivos originais na pasta ModelagemBanco/
--
-- ================================================================================

