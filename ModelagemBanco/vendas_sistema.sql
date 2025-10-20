-- =====================================================
-- MIGRAÇÃO: Sistema de Vendas Completo
-- =====================================================
-- Este arquivo implementa o sistema de vendas que permite:
-- ✅ Agrupar múltiplos serviços em uma única venda
-- ✅ Controle de formas de pagamento com taxas
-- ✅ Baixas parciais e múltiplas formas de pagamento
-- ✅ Histórico completo de vendas por cliente
-- ✅ Integração com sistema atual de serviços
-- =====================================================

-- =====================================================
-- PARTE 1: TABELA DE FORMAS DE PAGAMENTO
-- Execute esta parte primeiro para cadastrar os meios de pagamento
-- =====================================================

-- 1.1. Criar tabela de formas de pagamento
CREATE TABLE banhoetosa.formas_pagamento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('dinheiro', 'debito', 'credito', 'pix', 'boleto', 'outro')),
    taxa_percentual DECIMAL(5,2) DEFAULT 0,
    taxa_fixa DECIMAL(10,2) DEFAULT 0,
    parcelas_max INTEGER DEFAULT 1,
    dias_recebimento INTEGER DEFAULT 0, -- Prazo para receber (útil para boleto)
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
-- Execute após validar a Parte 1
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
    valor_bruto DECIMAL(10,2) NOT NULL DEFAULT 0, -- Soma dos itens sem desconto
    desconto DECIMAL(10,2) DEFAULT 0, -- Desconto aplicado
    valor_total DECIMAL(10,2) NOT NULL DEFAULT 0, -- Valor final (bruto - desconto)
    valor_pago DECIMAL(10,2) NOT NULL DEFAULT 0, -- Quanto já foi pago
    valor_pendente DECIMAL(10,2) NOT NULL DEFAULT 0, -- Quanto falta pagar

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
-- Execute após validar a Parte 2
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
-- Execute após validar a Parte 3
-- =====================================================

-- 4.1. Criar tabela de baixas (pagamentos)
CREATE TABLE banhoetosa.venda_baixas (
    id BIGSERIAL PRIMARY KEY,
    venda_id BIGINT NOT NULL,
    forma_pagamento_id BIGINT NOT NULL,
    valor_baixa DECIMAL(10,2) NOT NULL,
    valor_taxa DECIMAL(10,2) DEFAULT 0, -- Taxa cobrada pela operadora
    valor_liquido DECIMAL(10,2) NOT NULL, -- Valor que entra na conta (valor_baixa - valor_taxa)
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
-- Execute após validar a Parte 4
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

-- 5.2. View para detalhes de itens da venda
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
COMMENT ON VIEW banhoetosa.vw_venda_itens_detalhados IS 'View com detalhes de cada item das vendas';
COMMENT ON VIEW banhoetosa.vw_baixas_detalhadas IS 'View com histórico completo de baixas/pagamentos';

-- =====================================================
-- PARTE 6: DADOS DE TESTE (OPCIONAL)
-- Execute apenas se quiser testar o sistema
-- =====================================================

-- 6.1. Inserir uma venda de teste
-- (Descomente as linhas abaixo se quiser criar dados de teste)
/*
-- Buscar IDs necessários
DO $$
DECLARE
    v_cliente_id BIGINT;
    v_usuario_id BIGINT;
    v_animal_id BIGINT;
    v_servico_id BIGINT;
    v_venda_id BIGINT;
    v_animal_servico_id BIGINT;
    v_forma_pagamento_id BIGINT;
BEGIN
    -- Pegar primeiro cliente
    SELECT id INTO v_cliente_id FROM banhoetosa.clientes LIMIT 1;

    -- Pegar primeiro usuário
    SELECT id INTO v_usuario_id FROM banhoetosa.usuarios LIMIT 1;

    -- Pegar primeiro animal
    SELECT id INTO v_animal_id FROM banhoetosa.animais LIMIT 1;

    -- Pegar primeiro serviço
    SELECT id INTO v_servico_id FROM banhoetosa.servicos LIMIT 1;

    -- Pegar forma de pagamento (Dinheiro)
    SELECT id INTO v_forma_pagamento_id FROM banhoetosa.formas_pagamento WHERE nome = 'Dinheiro' LIMIT 1;

    IF v_cliente_id IS NOT NULL AND v_usuario_id IS NOT NULL THEN
        -- Criar venda
        INSERT INTO banhoetosa.vendas (cliente_id, usuario_id, tipo_venda, valor_bruto, valor_total, valor_pendente)
        VALUES (v_cliente_id, v_usuario_id, 'presencial', 100.00, 100.00, 100.00)
        RETURNING id INTO v_venda_id;

        RAISE NOTICE 'Venda criada com ID: %', v_venda_id;

        IF v_animal_id IS NOT NULL AND v_servico_id IS NOT NULL THEN
            -- Criar animal_servico
            INSERT INTO banhoetosa.animal_servico (animal_id, servico_id, usuario_id, data_servico, banhos_usados, venda_id)
            VALUES (v_animal_id, v_servico_id, v_usuario_id, CURRENT_DATE, 1, v_venda_id)
            RETURNING id INTO v_animal_servico_id;

            -- Criar item da venda
            INSERT INTO banhoetosa.venda_itens (venda_id, animal_servico_id, valor_item, valor_final_item)
            VALUES (v_venda_id, v_animal_servico_id, 100.00, 100.00);

            RAISE NOTICE 'Item adicionado à venda';
        END IF;

        IF v_forma_pagamento_id IS NOT NULL THEN
            -- Registrar baixa (pagamento)
            INSERT INTO banhoetosa.venda_baixas (venda_id, forma_pagamento_id, valor_baixa, valor_liquido, usuario_id)
            VALUES (v_venda_id, v_forma_pagamento_id, 100.00, 100.00, v_usuario_id);

            -- Atualizar venda
            UPDATE banhoetosa.vendas
            SET valor_pago = 100.00, valor_pendente = 0, status_venda = 'pago'
            WHERE id = v_venda_id;

            RAISE NOTICE 'Pagamento registrado. Venda finalizada!';
        END IF;
    END IF;
END $$;
*/

-- =====================================================
-- FIM DA MIGRAÇÃO
-- =====================================================

-- Para verificar se tudo foi criado corretamente, execute:
/*
SELECT 'Formas de Pagamento' as tabela, COUNT(*) as registros FROM banhoetosa.formas_pagamento
UNION ALL
SELECT 'Vendas', COUNT(*) FROM banhoetosa.vendas
UNION ALL
SELECT 'Venda Itens', COUNT(*) FROM banhoetosa.venda_itens
UNION ALL
SELECT 'Venda Baixas', COUNT(*) FROM banhoetosa.venda_baixas;
*/


-- Remover o constraint antigo
ALTER TABLE banhoetosa.vendas
DROP CONSTRAINT IF EXISTS vendas_tipo_venda_check;

-- Adicionar o novo constraint
ALTER TABLE banhoetosa.vendas
ADD CONSTRAINT vendas_tipo_venda_check
CHECK (tipo_venda IN ('presencial', 'agendamento', 'busca_entrega'));
