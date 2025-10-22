-- ================================================================================
-- SISTEMA DE MAQUININHAS - ProntodogBanhos
-- ================================================================================
-- Este arquivo implementa o sistema completo de maquininhas de cartão com:
-- ✅ Cadastro de contas bancárias
-- ✅ Cadastro de adquirentes (Getnet, Stone, etc.)
-- ✅ Cadastro de bandeiras (Visa, Master, Elo, etc.)
-- ✅ Cadastro de maquininhas com configurações detalhadas
-- ✅ Taxas específicas por bandeira e parcelamento
-- ✅ Controle de recebimentos futuros
-- ✅ Suporte a PIX pela maquininha
-- ✅ Controle de antecipação
--
-- Data de criação: 21/10/2025
-- Compatível com: sistema de vendas existente
-- IMPORTANTE: Não quebra nada do sistema atual!
-- ================================================================================


-- ================================================================================
-- ETAPA 1: CONTAS BANCÁRIAS
-- ================================================================================

CREATE TABLE IF NOT EXISTS banhoetosa.contas_bancarias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL UNIQUE,
    banco VARCHAR(100) NOT NULL,
    agencia VARCHAR(20),
    conta VARCHAR(50),
    tipo VARCHAR(50) CHECK (tipo IN ('corrente', 'poupanca', 'pagamento')),
    pix_chave VARCHAR(200),
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_contas_bancarias_ativo ON banhoetosa.contas_bancarias(ativo);

-- Comentários
COMMENT ON TABLE banhoetosa.contas_bancarias IS 'Contas bancárias da clínica para recebimento';
COMMENT ON COLUMN banhoetosa.contas_bancarias.nome IS 'Nome de identificação da conta (ex: ITAU NAV Principal)';
COMMENT ON COLUMN banhoetosa.contas_bancarias.banco IS 'Nome do banco (ex: Itaú, Bradesco)';
COMMENT ON COLUMN banhoetosa.contas_bancarias.tipo IS 'Tipo da conta: corrente, poupanca, pagamento';
COMMENT ON COLUMN banhoetosa.contas_bancarias.pix_chave IS 'Chave PIX da conta (se houver)';


-- ================================================================================
-- ETAPA 2: ADQUIRENTES (Empresas de Maquininha)
-- ================================================================================

CREATE TABLE IF NOT EXISTS banhoetosa.adquirentes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    codigo VARCHAR(50),
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inserir adquirentes principais
INSERT INTO banhoetosa.adquirentes (nome, codigo) VALUES
('Getnet', 'GETNET'),
('Stone', 'STONE'),
('PagSeguro', 'PAGSEGURO'),
('Rede', 'REDE'),
('Cielo', 'CIELO'),
('Mercado Pago', 'MERCADOPAGO'),
('SafraPay', 'SAFRAPAY'),
('Sumup', 'SUMUP'),
('InfinitePay', 'INFINITEPAY'),
('Ton', 'TON')
ON CONFLICT (nome) DO NOTHING;

-- Comentários
COMMENT ON TABLE banhoetosa.adquirentes IS 'Empresas de maquininhas de cartão';
COMMENT ON COLUMN banhoetosa.adquirentes.nome IS 'Nome da empresa (ex: Getnet, Stone)';


-- ================================================================================
-- ETAPA 3: BANDEIRAS DE CARTÃO
-- ================================================================================

CREATE TABLE IF NOT EXISTS banhoetosa.bandeiras (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    codigo VARCHAR(50),
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inserir bandeiras principais
INSERT INTO banhoetosa.bandeiras (nome, codigo) VALUES
('Visa', 'VISA'),
('Mastercard', 'MASTER'),
('Elo', 'ELO'),
('American Express', 'AMEX'),
('Hipercard', 'HIPER'),
('Discover', 'DISCOVER'),
('Diners Club', 'DINERS'),
('JCB', 'JCB'),
('Aura', 'AURA')
ON CONFLICT (nome) DO NOTHING;

-- Comentários
COMMENT ON TABLE banhoetosa.bandeiras IS 'Bandeiras de cartões de crédito/débito';
COMMENT ON COLUMN banhoetosa.bandeiras.nome IS 'Nome da bandeira (ex: Visa, Mastercard)';


-- ================================================================================
-- ETAPA 4: MAQUININHAS
-- ================================================================================

CREATE TABLE IF NOT EXISTS banhoetosa.maquininhas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    adquirente_id BIGINT NOT NULL,
    conta_bancaria_id BIGINT NOT NULL,

    -- Prazos de recebimento
    prazo_recebimento_debito INTEGER DEFAULT 1,
    prazo_recebimento_credito INTEGER DEFAULT 30,

    -- Antecipação
    aceita_antecipacao BOOLEAN DEFAULT FALSE,
    antecipacao_automatica BOOLEAN DEFAULT FALSE,
    taxa_antecipacao_mensal DECIMAL(5,2) DEFAULT 0,

    -- PIX
    aceita_pix BOOLEAN DEFAULT FALSE,
    conta_pix_id BIGINT,
    taxa_pix DECIMAL(5,2) DEFAULT 0,
    prazo_recebimento_pix INTEGER DEFAULT 0,

    -- Controle
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_maquininha_adquirente FOREIGN KEY (adquirente_id)
        REFERENCES banhoetosa.adquirentes(id),
    CONSTRAINT fk_maquininha_conta FOREIGN KEY (conta_bancaria_id)
        REFERENCES banhoetosa.contas_bancarias(id),
    CONSTRAINT fk_maquininha_conta_pix FOREIGN KEY (conta_pix_id)
        REFERENCES banhoetosa.contas_bancarias(id),
    CONSTRAINT chk_prazo_debito_positivo CHECK (prazo_recebimento_debito >= 0),
    CONSTRAINT chk_prazo_credito_positivo CHECK (prazo_recebimento_credito >= 0),
    CONSTRAINT chk_taxa_antecipacao_valida CHECK (taxa_antecipacao_mensal >= 0 AND taxa_antecipacao_mensal <= 100),
    CONSTRAINT chk_taxa_pix_valida CHECK (taxa_pix >= 0 AND taxa_pix <= 100),
    CONSTRAINT chk_prazo_pix_positivo CHECK (prazo_recebimento_pix >= 0)
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_maquininhas_adquirente ON banhoetosa.maquininhas(adquirente_id);
CREATE INDEX IF NOT EXISTS idx_maquininhas_conta ON banhoetosa.maquininhas(conta_bancaria_id);
CREATE INDEX IF NOT EXISTS idx_maquininhas_ativo ON banhoetosa.maquininhas(ativo);

-- Comentários
COMMENT ON TABLE banhoetosa.maquininhas IS 'Cadastro de maquininhas de cartão da clínica';
COMMENT ON COLUMN banhoetosa.maquininhas.nome IS 'Nome de identificação da maquininha (ex: Getnet Loja 1)';
COMMENT ON COLUMN banhoetosa.maquininhas.prazo_recebimento_debito IS 'Dias úteis para receber transações de débito';
COMMENT ON COLUMN banhoetosa.maquininhas.prazo_recebimento_credito IS 'Dias úteis para receber transações de crédito à vista';
COMMENT ON COLUMN banhoetosa.maquininhas.aceita_antecipacao IS 'Se a maquininha oferece antecipação de recebíveis';
COMMENT ON COLUMN banhoetosa.maquininhas.antecipacao_automatica IS 'Se a antecipação é automática ou sob demanda';
COMMENT ON COLUMN banhoetosa.maquininhas.taxa_antecipacao_mensal IS 'Taxa de antecipação ao mês (%)';
COMMENT ON COLUMN banhoetosa.maquininhas.aceita_pix IS 'Se aceita pagamentos PIX pela maquininha';
COMMENT ON COLUMN banhoetosa.maquininhas.conta_pix_id IS 'Conta destino do PIX (pode ser diferente da conta principal)';
COMMENT ON COLUMN banhoetosa.maquininhas.taxa_pix IS 'Taxa percentual do PIX (%)';
COMMENT ON COLUMN banhoetosa.maquininhas.prazo_recebimento_pix IS 'Dias para receber PIX (geralmente 0 = mesmo dia)';


-- ================================================================================
-- ETAPA 5: TAXAS DAS MAQUININHAS
-- ================================================================================

CREATE TABLE IF NOT EXISTS banhoetosa.maquininha_taxas (
    id BIGSERIAL PRIMARY KEY,
    maquininha_id BIGINT NOT NULL,
    bandeira_id BIGINT NOT NULL,
    tipo_transacao VARCHAR(50) NOT NULL CHECK (tipo_transacao IN ('debito', 'credito_avista', 'credito_parcelado')),
    numero_parcelas INTEGER,
    taxa_percentual DECIMAL(5,2) NOT NULL DEFAULT 0,
    taxa_fixa DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_taxa_maquininha FOREIGN KEY (maquininha_id)
        REFERENCES banhoetosa.maquininhas(id) ON DELETE CASCADE,
    CONSTRAINT fk_taxa_bandeira FOREIGN KEY (bandeira_id)
        REFERENCES banhoetosa.bandeiras(id),
    CONSTRAINT chk_taxa_percentual_valida CHECK (taxa_percentual >= 0 AND taxa_percentual <= 100),
    CONSTRAINT chk_taxa_fixa_positiva CHECK (taxa_fixa >= 0),
    CONSTRAINT chk_parcelas_validas CHECK (
        (tipo_transacao = 'debito' AND numero_parcelas IS NULL) OR
        (tipo_transacao = 'credito_avista' AND (numero_parcelas IS NULL OR numero_parcelas = 1)) OR
        (tipo_transacao = 'credito_parcelado' AND numero_parcelas >= 2)
    ),
    -- Evita duplicação de taxas
    CONSTRAINT unique_taxa_configuracao UNIQUE (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas)
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_taxa_maquininha ON banhoetosa.maquininha_taxas(maquininha_id);
CREATE INDEX IF NOT EXISTS idx_taxa_bandeira ON banhoetosa.maquininha_taxas(bandeira_id);
CREATE INDEX IF NOT EXISTS idx_taxa_tipo ON banhoetosa.maquininha_taxas(tipo_transacao);

-- Comentários
COMMENT ON TABLE banhoetosa.maquininha_taxas IS 'Taxas cobradas por cada maquininha, por bandeira e tipo de transação';
COMMENT ON COLUMN banhoetosa.maquininha_taxas.tipo_transacao IS 'debito, credito_avista, credito_parcelado';
COMMENT ON COLUMN banhoetosa.maquininha_taxas.numero_parcelas IS 'Número de parcelas (NULL para débito, 1 para à vista, 2-12+ para parcelado)';
COMMENT ON COLUMN banhoetosa.maquininha_taxas.taxa_percentual IS 'Taxa percentual cobrada (%)';
COMMENT ON COLUMN banhoetosa.maquininha_taxas.taxa_fixa IS 'Taxa fixa em reais (opcional)';


-- ================================================================================
-- ETAPA 6: ALTERAR VENDA_BAIXAS (Adicionar suporte a maquininhas)
-- ================================================================================

-- Adicionar colunas relacionadas a maquininhas
ALTER TABLE banhoetosa.venda_baixas
ADD COLUMN IF NOT EXISTS maquininha_id BIGINT,
ADD COLUMN IF NOT EXISTS bandeira_id BIGINT,
ADD COLUMN IF NOT EXISTS tipo_transacao VARCHAR(50),
ADD COLUMN IF NOT EXISTS data_prevista_recebimento DATE,
ADD COLUMN IF NOT EXISTS data_efetiva_recebimento DATE,
ADD COLUMN IF NOT EXISTS status_recebimento VARCHAR(50) DEFAULT 'pendente';

-- Adicionar constraints
ALTER TABLE banhoetosa.venda_baixas
DROP CONSTRAINT IF EXISTS fk_baixa_maquininha,
ADD CONSTRAINT fk_baixa_maquininha FOREIGN KEY (maquininha_id)
    REFERENCES banhoetosa.maquininhas(id);

ALTER TABLE banhoetosa.venda_baixas
DROP CONSTRAINT IF EXISTS fk_baixa_bandeira,
ADD CONSTRAINT fk_baixa_bandeira FOREIGN KEY (bandeira_id)
    REFERENCES banhoetosa.bandeiras(id);

ALTER TABLE banhoetosa.venda_baixas
DROP CONSTRAINT IF EXISTS chk_status_recebimento,
ADD CONSTRAINT chk_status_recebimento
    CHECK (status_recebimento IN ('pendente', 'recebido', 'antecipado', 'estornado'));

ALTER TABLE banhoetosa.venda_baixas
DROP CONSTRAINT IF EXISTS chk_tipo_transacao_baixa,
ADD CONSTRAINT chk_tipo_transacao_baixa
    CHECK (tipo_transacao IS NULL OR tipo_transacao IN ('debito', 'credito_avista', 'credito_parcelado', 'pix'));

-- Índices
CREATE INDEX IF NOT EXISTS idx_baixa_maquininha ON banhoetosa.venda_baixas(maquininha_id);
CREATE INDEX IF NOT EXISTS idx_baixa_bandeira ON banhoetosa.venda_baixas(bandeira_id);
CREATE INDEX IF NOT EXISTS idx_baixa_status_recebimento ON banhoetosa.venda_baixas(status_recebimento);
CREATE INDEX IF NOT EXISTS idx_baixa_data_prevista ON banhoetosa.venda_baixas(data_prevista_recebimento);
CREATE INDEX IF NOT EXISTS idx_baixa_data_efetiva ON banhoetosa.venda_baixas(data_efetiva_recebimento);

-- Comentários
COMMENT ON COLUMN banhoetosa.venda_baixas.maquininha_id IS 'Maquininha utilizada no pagamento (NULL para dinheiro, PIX direto, etc.)';
COMMENT ON COLUMN banhoetosa.venda_baixas.bandeira_id IS 'Bandeira do cartão utilizado (NULL se não for cartão)';
COMMENT ON COLUMN banhoetosa.venda_baixas.tipo_transacao IS 'Tipo: debito, credito_avista, credito_parcelado, pix';
COMMENT ON COLUMN banhoetosa.venda_baixas.data_prevista_recebimento IS 'Data prevista para receber o valor (calculada automaticamente)';
COMMENT ON COLUMN banhoetosa.venda_baixas.data_efetiva_recebimento IS 'Data em que o valor efetivamente caiu na conta';
COMMENT ON COLUMN banhoetosa.venda_baixas.status_recebimento IS 'Status: pendente, recebido, antecipado, estornado';


-- ================================================================================
-- ETAPA 7: VIEWS ÚTEIS
-- ================================================================================

-- View: Maquininhas completas com informações detalhadas
CREATE OR REPLACE VIEW banhoetosa.vw_maquininhas_completas AS
SELECT
    m.id,
    m.nome,
    m.ativo,

    -- Adquirente
    a.id as adquirente_id,
    a.nome as adquirente_nome,

    -- Conta principal
    cb.id as conta_id,
    cb.nome as conta_nome,
    cb.banco as conta_banco,

    -- Prazos
    m.prazo_recebimento_debito,
    m.prazo_recebimento_credito,

    -- Antecipação
    m.aceita_antecipacao,
    m.antecipacao_automatica,
    m.taxa_antecipacao_mensal,

    -- PIX
    m.aceita_pix,
    CASE WHEN m.conta_pix_id IS NOT NULL THEN cbp.nome ELSE cb.nome END as conta_pix_nome,
    m.taxa_pix,
    m.prazo_recebimento_pix,

    -- Contadores
    COUNT(DISTINCT mt.id) as total_taxas_configuradas,
    COUNT(DISTINCT mt.bandeira_id) as total_bandeiras,

    m.created_at,
    m.updated_at

FROM banhoetosa.maquininhas m
JOIN banhoetosa.adquirentes a ON m.adquirente_id = a.id
JOIN banhoetosa.contas_bancarias cb ON m.conta_bancaria_id = cb.id
LEFT JOIN banhoetosa.contas_bancarias cbp ON m.conta_pix_id = cbp.id
LEFT JOIN banhoetosa.maquininha_taxas mt ON m.id = mt.maquininha_id
GROUP BY m.id, a.id, cb.id, cbp.id
ORDER BY m.ativo DESC, m.nome;

COMMENT ON VIEW banhoetosa.vw_maquininhas_completas IS 'View com informações completas de todas as maquininhas';


-- View: Taxas de maquininhas formatadas
CREATE OR REPLACE VIEW banhoetosa.vw_taxas_maquininhas AS
SELECT
    mt.id,
    m.id as maquininha_id,
    m.nome as maquininha_nome,
    b.id as bandeira_id,
    b.nome as bandeira_nome,
    mt.tipo_transacao,
    mt.numero_parcelas,
    mt.taxa_percentual,
    mt.taxa_fixa,
    CASE
        WHEN mt.tipo_transacao = 'debito' THEN 'Débito'
        WHEN mt.tipo_transacao = 'credito_avista' THEN 'Crédito à Vista'
        WHEN mt.tipo_transacao = 'credito_parcelado' THEN CONCAT('Crédito ', mt.numero_parcelas, 'x')
    END as descricao_transacao
FROM banhoetosa.maquininha_taxas mt
JOIN banhoetosa.maquininhas m ON mt.maquininha_id = m.id
JOIN banhoetosa.bandeiras b ON mt.bandeira_id = b.id
WHERE m.ativo = TRUE
ORDER BY m.nome, b.nome, mt.tipo_transacao, mt.numero_parcelas;

COMMENT ON VIEW banhoetosa.vw_taxas_maquininhas IS 'View com todas as taxas configuradas por maquininha';


-- View: Recebimentos pendentes (Fluxo de Caixa)
CREATE OR REPLACE VIEW banhoetosa.vw_recebimentos_pendentes AS
SELECT
    vb.id as baixa_id,
    vb.venda_id,
    v.codigo_venda,
    v.cliente_id,
    c.nome_completo as cliente_nome,

    -- Forma de pagamento
    fp.nome as forma_pagamento,

    -- Maquininha
    m.id as maquininha_id,
    m.nome as maquininha_nome,
    a.nome as adquirente_nome,

    -- Bandeira
    b.nome as bandeira_nome,

    -- Valores
    vb.valor_baixa,
    vb.valor_taxa,
    vb.valor_liquido,

    -- Datas
    vb.data_baixa,
    vb.data_prevista_recebimento,
    vb.data_efetiva_recebimento,
    vb.status_recebimento,

    -- Conta destino
    cb.nome as conta_destino,

    -- Informações adicionais
    vb.tipo_transacao,
    vb.numero_parcelas,

    -- Dias até receber
    CASE
        WHEN vb.status_recebimento = 'recebido' THEN 0
        WHEN vb.data_prevista_recebimento IS NULL THEN NULL
        ELSE (vb.data_prevista_recebimento - CURRENT_DATE)
    END as dias_ate_receber

FROM banhoetosa.venda_baixas vb
JOIN banhoetosa.vendas v ON vb.venda_id = v.id
JOIN banhoetosa.clientes c ON v.cliente_id = c.id
JOIN banhoetosa.formas_pagamento fp ON vb.forma_pagamento_id = fp.id
LEFT JOIN banhoetosa.maquininhas m ON vb.maquininha_id = m.id
LEFT JOIN banhoetosa.adquirentes a ON m.adquirente_id = a.id
LEFT JOIN banhoetosa.bandeiras b ON vb.bandeira_id = b.id
LEFT JOIN banhoetosa.contas_bancarias cb ON m.conta_bancaria_id = cb.id
WHERE vb.status_recebimento IN ('pendente', 'antecipado')
ORDER BY vb.data_prevista_recebimento NULLS LAST;

COMMENT ON VIEW banhoetosa.vw_recebimentos_pendentes IS 'Recebimentos futuros para controle de fluxo de caixa';


-- ================================================================================
-- ETAPA 8: FUNÇÕES AUXILIARES
-- ================================================================================

-- Função: Calcular taxa de uma transação
CREATE OR REPLACE FUNCTION banhoetosa.calcular_taxa_maquininha(
    p_maquininha_id BIGINT,
    p_bandeira_id BIGINT,
    p_tipo_transacao VARCHAR,
    p_numero_parcelas INTEGER,
    p_valor DECIMAL(10,2)
) RETURNS TABLE (
    taxa_percentual DECIMAL(5,2),
    taxa_fixa DECIMAL(10,2),
    valor_taxa DECIMAL(10,2),
    valor_liquido DECIMAL(10,2)
) AS $$
DECLARE
    v_taxa_percentual DECIMAL(5,2);
    v_taxa_fixa DECIMAL(10,2);
    v_valor_taxa DECIMAL(10,2);
    v_valor_liquido DECIMAL(10,2);
BEGIN
    -- Buscar a taxa configurada
    SELECT mt.taxa_percentual, mt.taxa_fixa
    INTO v_taxa_percentual, v_taxa_fixa
    FROM banhoetosa.maquininha_taxas mt
    WHERE mt.maquininha_id = p_maquininha_id
      AND mt.bandeira_id = p_bandeira_id
      AND mt.tipo_transacao = p_tipo_transacao
      AND (
          (p_tipo_transacao = 'debito' AND mt.numero_parcelas IS NULL) OR
          (p_tipo_transacao = 'credito_avista' AND (mt.numero_parcelas IS NULL OR mt.numero_parcelas = 1)) OR
          (p_tipo_transacao = 'credito_parcelado' AND mt.numero_parcelas = p_numero_parcelas)
      )
    LIMIT 1;

    -- Se não encontrou, retornar zeros
    IF v_taxa_percentual IS NULL THEN
        v_taxa_percentual := 0;
        v_taxa_fixa := 0;
    END IF;

    -- Calcular valor da taxa
    v_valor_taxa := (p_valor * v_taxa_percentual / 100) + COALESCE(v_taxa_fixa, 0);
    v_valor_liquido := p_valor - v_valor_taxa;

    -- Retornar resultado
    RETURN QUERY SELECT v_taxa_percentual, v_taxa_fixa, v_valor_taxa, v_valor_liquido;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION banhoetosa.calcular_taxa_maquininha IS
'Calcula a taxa e valor líquido de uma transação baseado na maquininha, bandeira e tipo';


-- Função: Calcular data de recebimento
CREATE OR REPLACE FUNCTION banhoetosa.calcular_data_recebimento(
    p_maquininha_id BIGINT,
    p_tipo_transacao VARCHAR,
    p_numero_parcelas INTEGER,
    p_data_transacao DATE
) RETURNS DATE AS $$
DECLARE
    v_prazo_dias INTEGER;
    v_antecipacao_automatica BOOLEAN;
BEGIN
    -- Buscar configurações da maquininha
    SELECT
        CASE
            WHEN p_tipo_transacao = 'debito' THEN m.prazo_recebimento_debito
            WHEN p_tipo_transacao = 'pix' THEN m.prazo_recebimento_pix
            ELSE m.prazo_recebimento_credito
        END,
        m.antecipacao_automatica
    INTO v_prazo_dias, v_antecipacao_automatica
    FROM banhoetosa.maquininhas m
    WHERE m.id = p_maquininha_id;

    -- Se não encontrou, retornar data da transação
    IF v_prazo_dias IS NULL THEN
        RETURN p_data_transacao;
    END IF;

    -- Se tem antecipação automática, prazo é reduzido (considerar 1 dia)
    IF v_antecipacao_automatica THEN
        v_prazo_dias := 1;
    END IF;

    -- Calcular data de recebimento
    RETURN p_data_transacao + v_prazo_dias;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION banhoetosa.calcular_data_recebimento IS
'Calcula a data prevista de recebimento baseado na maquininha e tipo de transação';


-- Função: Buscar taxa PIX de uma maquininha
CREATE OR REPLACE FUNCTION banhoetosa.buscar_taxa_pix_maquininha(
    p_maquininha_id BIGINT,
    p_valor DECIMAL(10,2)
) RETURNS TABLE (
    taxa_percentual DECIMAL(5,2),
    valor_taxa DECIMAL(10,2),
    valor_liquido DECIMAL(10,2)
) AS $$
DECLARE
    v_taxa_pix DECIMAL(5,2);
    v_valor_taxa DECIMAL(10,2);
    v_valor_liquido DECIMAL(10,2);
BEGIN
    -- Buscar taxa PIX da maquininha
    SELECT m.taxa_pix
    INTO v_taxa_pix
    FROM banhoetosa.maquininhas m
    WHERE m.id = p_maquininha_id
      AND m.aceita_pix = TRUE;

    -- Se não encontrou ou não aceita PIX, retornar zeros
    IF v_taxa_pix IS NULL THEN
        v_taxa_pix := 0;
    END IF;

    -- Calcular taxa
    v_valor_taxa := p_valor * v_taxa_pix / 100;
    v_valor_liquido := p_valor - v_valor_taxa;

    -- Retornar resultado
    RETURN QUERY SELECT v_taxa_pix, v_valor_taxa, v_valor_liquido;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION banhoetosa.buscar_taxa_pix_maquininha IS
'Busca a taxa de PIX de uma maquininha e calcula os valores';


-- ================================================================================
-- ETAPA 9: DADOS INICIAIS (Exemplo)
-- ================================================================================

-- Inserir uma conta bancária de exemplo (OPCIONAL - comentado por padrão)
/*
INSERT INTO banhoetosa.contas_bancarias (nome, banco, agencia, conta, tipo, pix_chave)
VALUES ('Conta Principal', 'Itaú', '0001', '12345-6', 'corrente', 'seuemail@exemplo.com')
ON CONFLICT (nome) DO NOTHING;
*/


-- ================================================================================
-- ETAPA 10: VERIFICAÇÃO
-- ================================================================================

-- Verificar se tudo foi criado
SELECT 'Tabelas criadas com sucesso!' as status;

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


-- ================================================================================
-- FIM DO SCRIPT
-- ================================================================================

-- PRÓXIMOS PASSOS:
-- 1. Execute este script no seu banco de dados PostgreSQL
-- 2. Verifique se todas as tabelas foram criadas
-- 3. Teste as views e funções
-- 4. Seguir para implementação do backend Java
-- 5. Depois implementar o frontend Vue.js

-- NOTAS IMPORTANTES:
-- ✅ Este script é SEGURO e não quebra nada do sistema atual
-- ✅ Vendas antigas continuam funcionando normalmente
-- ✅ O campo maquininha_id em venda_baixas é OPCIONAL (NULLABLE)
-- ✅ Você pode usar tanto o sistema antigo quanto o novo
-- ✅ Migração gradual é possível

