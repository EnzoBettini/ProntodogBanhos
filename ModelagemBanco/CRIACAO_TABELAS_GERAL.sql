-- ================================================================================
-- CRIAÇÃO GERAL DE TABELAS - ProntodogBanhos
-- ================================================================================
-- Arquivo consolidado com todas as tabelas do sistema
-- Data: 24/10/2025
-- Ordem de execução: Respeita dependências de chaves estrangeiras
-- ================================================================================

-- ================================================================================
-- SESSÃO 1: TABELAS BASE (Clientes, Animais, Usuários)
-- ================================================================================

CREATE TABLE IF NOT EXISTS banhoetosa.clientes (
    id BIGSERIAL PRIMARY KEY,
    codigo_cliente_sistema BIGSERIAL UNIQUE,
    nome_completo VARCHAR NOT NULL,
    cpf VARCHAR(11) UNIQUE,
    codigo_simplesvet BIGINT UNIQUE
);

CREATE TABLE IF NOT EXISTS banhoetosa.telefones_clientes (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    telefone VARCHAR NOT NULL,
    CONSTRAINT fk_cliente_id_telefone FOREIGN KEY (cliente_id)
        REFERENCES banhoetosa.clientes (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS banhoetosa.email_clientes (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    email VARCHAR NOT NULL,
    CONSTRAINT fk_cliente_id_email FOREIGN KEY (cliente_id)
        REFERENCES banhoetosa.clientes (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS banhoetosa.usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS banhoetosa.animais (
    id BIGSERIAL PRIMARY KEY,
    codigo_animal_sistema BIGSERIAL UNIQUE,
    nome VARCHAR NOT NULL,
    codigo_simplesvet BIGINT UNIQUE,
    tipo VARCHAR,
    raca VARCHAR(100),
    peso NUMERIC(5, 2),
    status VARCHAR(20) DEFAULT 'vivo' NOT NULL CHECK (status IN ('vivo', 'falecido')),
    cliente_id BIGINT NOT NULL,
    CONSTRAINT fk_cliente FOREIGN KEY (cliente_id)
        REFERENCES banhoetosa.clientes (id) ON DELETE CASCADE
);

-- ================================================================================
-- SESSÃO 2: SERVIÇOS E ATENDIMENTOS
-- ================================================================================

CREATE TABLE IF NOT EXISTS banhoetosa.servicos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR NOT NULL UNIQUE,
    descricao TEXT,
    quantidade INT,
    valor NUMERIC(10, 2) NOT NULL,
    pode_ser_adicional BOOLEAN DEFAULT TRUE,
    categoria VARCHAR(50) DEFAULT 'geral'
);

CREATE TABLE IF NOT EXISTS banhoetosa.animal_servico (
    id BIGSERIAL PRIMARY KEY,
    data_servico DATE NOT NULL,
    banhos_usados INT NOT NULL,
    data_expiracao DATE,
    valor_cobrado DECIMAL(10,2),
    status_pagamento VARCHAR(20) DEFAULT 'em_aberto' NOT NULL
        CHECK (status_pagamento IN ('em_aberto', 'parcial', 'pago', 'cancelado')),
    data_pagamento DATE,
    status_servico VARCHAR(20),
    data_realizacao DATE,
    animal_id BIGINT NOT NULL,
    servico_id BIGINT NOT NULL,
    usuario_id BIGINT,
    venda_id BIGINT,
    CONSTRAINT fk_animal_id FOREIGN KEY (animal_id)
        REFERENCES banhoetosa.animais (id) ON DELETE CASCADE,
    CONSTRAINT fk_servico_id FOREIGN KEY (servico_id)
        REFERENCES banhoetosa.servicos (id) ON DELETE RESTRICT,
    CONSTRAINT fk_usuario FOREIGN KEY (usuario_id)
        REFERENCES banhoetosa.usuarios (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS banhoetosa.banhos_individuais (
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
        REFERENCES banhoetosa.usuarios (id) ON DELETE SET NULL,
    CONSTRAINT unique_banho_por_servico UNIQUE (animal_servico_id, numero_banho)
);

CREATE TABLE IF NOT EXISTS banhoetosa.servicos_adicionais (
    id BIGSERIAL PRIMARY KEY,
    animal_servico_principal_id BIGINT NOT NULL,
    servico_adicional_id BIGINT NOT NULL,
    quantidade_adicional INTEGER NOT NULL DEFAULT 1,
    valor_unitario DECIMAL(10,2) NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    data_adicao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_realizacao DATE NOT NULL,
    observacoes TEXT,
    status_pagamento VARCHAR(20) DEFAULT 'em_aberto'
        CHECK (status_pagamento IN ('em_aberto', 'parcial', 'pago', 'cancelado')),
    data_pagamento DATE,
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_servico_adicional_principal FOREIGN KEY (animal_servico_principal_id)
        REFERENCES banhoetosa.animal_servico (id) ON DELETE CASCADE,
    CONSTRAINT fk_servico_adicional_servico FOREIGN KEY (servico_adicional_id)
        REFERENCES banhoetosa.servicos (id) ON DELETE RESTRICT,
    CONSTRAINT fk_servico_adicional_usuario FOREIGN KEY (usuario_id)
        REFERENCES banhoetosa.usuarios (id) ON DELETE RESTRICT,
    CONSTRAINT chk_quantidade_positiva CHECK (quantidade_adicional > 0),
    CONSTRAINT chk_valor_positivo CHECK (valor_unitario >= 0 AND valor_total >= 0)
);

-- ================================================================================
-- SESSÃO 3: MAQUININHAS E CONTAS BANCÁRIAS
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

CREATE TABLE IF NOT EXISTS banhoetosa.adquirentes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    codigo VARCHAR(50),
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS banhoetosa.bandeiras (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    codigo VARCHAR(50),
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS banhoetosa.maquininhas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    adquirente_id BIGINT NOT NULL,
    conta_bancaria_id BIGINT NOT NULL,
    prazo_recebimento_debito INTEGER DEFAULT 1,
    prazo_recebimento_credito INTEGER DEFAULT 30,
    aceita_antecipacao BOOLEAN DEFAULT FALSE,
    antecipacao_automatica BOOLEAN DEFAULT FALSE,
    taxa_antecipacao_mensal DECIMAL(5,2) DEFAULT 0,
    aceita_pix BOOLEAN DEFAULT FALSE,
    conta_pix_id BIGINT,
    taxa_pix DECIMAL(5,2) DEFAULT 0,
    prazo_recebimento_pix INTEGER DEFAULT 0,
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_maquininha_adquirente FOREIGN KEY (adquirente_id)
        REFERENCES banhoetosa.adquirentes(id) ON DELETE RESTRICT,
    CONSTRAINT fk_maquininha_conta FOREIGN KEY (conta_bancaria_id)
        REFERENCES banhoetosa.contas_bancarias(id) ON DELETE RESTRICT,
    CONSTRAINT fk_maquininha_conta_pix FOREIGN KEY (conta_pix_id)
        REFERENCES banhoetosa.contas_bancarias(id) ON DELETE SET NULL,
    CONSTRAINT chk_prazo_debito_positivo CHECK (prazo_recebimento_debito >= 0),
    CONSTRAINT chk_prazo_credito_positivo CHECK (prazo_recebimento_credito >= 0),
    CONSTRAINT chk_taxa_antecipacao_valida CHECK (taxa_antecipacao_mensal >= 0 AND taxa_antecipacao_mensal <= 100),
    CONSTRAINT chk_taxa_pix_valida CHECK (taxa_pix >= 0 AND taxa_pix <= 100),
    CONSTRAINT chk_prazo_pix_positivo CHECK (prazo_recebimento_pix >= 0)
);

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
    CONSTRAINT fk_taxa_maquininha FOREIGN KEY (maquininha_id)
        REFERENCES banhoetosa.maquininhas(id) ON DELETE CASCADE,
    CONSTRAINT fk_taxa_bandeira FOREIGN KEY (bandeira_id)
        REFERENCES banhoetosa.bandeiras(id) ON DELETE RESTRICT,
    CONSTRAINT chk_taxa_percentual_valida CHECK (taxa_percentual >= 0 AND taxa_percentual <= 100),
    CONSTRAINT chk_taxa_fixa_positiva CHECK (taxa_fixa >= 0),
    CONSTRAINT chk_parcelas_validas CHECK (
        (tipo_transacao = 'debito' AND numero_parcelas IS NULL) OR
        (tipo_transacao = 'credito_avista' AND (numero_parcelas IS NULL OR numero_parcelas = 1)) OR
        (tipo_transacao = 'credito_parcelado' AND numero_parcelas >= 2)
    ),
    CONSTRAINT unique_taxa_configuracao UNIQUE (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas)
);

-- ================================================================================
-- SESSÃO 4: VENDAS E PAGAMENTOS
-- ================================================================================

CREATE SEQUENCE IF NOT EXISTS banhoetosa.seq_codigo_venda START 100000;

CREATE TABLE IF NOT EXISTS banhoetosa.formas_pagamento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    tipo VARCHAR(50) NOT NULL,
    taxa_percentual DECIMAL(5,2) DEFAULT 0,
    taxa_fixa DECIMAL(10,2) DEFAULT 0,
    parcelas_max INTEGER DEFAULT 1,
    dias_recebimento INTEGER DEFAULT 0,
    ativo BOOLEAN DEFAULT TRUE,
    maquininha_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_parcelas_positivas CHECK (parcelas_max > 0),
    CONSTRAINT fk_formas_pagamento_maquininha FOREIGN KEY (maquininha_id)
        REFERENCES banhoetosa.maquininhas(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS banhoetosa.vendas (
    id BIGSERIAL PRIMARY KEY,
    codigo_venda BIGINT NOT NULL UNIQUE DEFAULT nextval('banhoetosa.seq_codigo_venda'),
    cliente_id BIGINT NOT NULL,
    tipo_venda VARCHAR(50) DEFAULT 'presencial',
    status_venda VARCHAR(20) NOT NULL DEFAULT 'em_aberto',
    usuario_id BIGINT NOT NULL,
    data_venda TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valor_bruto DECIMAL(10,2) NOT NULL DEFAULT 0,
    desconto DECIMAL(10,2) DEFAULT 0,
    valor_total DECIMAL(10,2) NOT NULL DEFAULT 0,
    valor_pago DECIMAL(10,2) NOT NULL DEFAULT 0,
    valor_pendente DECIMAL(10,2) NOT NULL DEFAULT 0,
    observacoes TEXT,
    cancelado_em TIMESTAMP,
    motivo_cancelamento TEXT,
    CONSTRAINT fk_venda_cliente FOREIGN KEY (cliente_id)
        REFERENCES banhoetosa.clientes(id) ON DELETE RESTRICT,
    CONSTRAINT fk_venda_usuario FOREIGN KEY (usuario_id)
        REFERENCES banhoetosa.usuarios(id) ON DELETE RESTRICT,
    CONSTRAINT chk_status_venda CHECK (status_venda IN ('em_aberto', 'pago', 'parcial', 'cancelado')),
    CONSTRAINT chk_desconto_valido CHECK (desconto >= 0 AND desconto <= valor_bruto)
);

CREATE TABLE IF NOT EXISTS banhoetosa.venda_itens (
    id BIGSERIAL PRIMARY KEY,
    venda_id BIGINT NOT NULL,
    animal_servico_id BIGINT NOT NULL,
    valor_item DECIMAL(10,2) NOT NULL DEFAULT 0,
    desconto_item DECIMAL(10,2) DEFAULT 0,
    valor_final_item DECIMAL(10,2) NOT NULL DEFAULT 0,
    valor_pago_item DECIMAL(10,2) NOT NULL DEFAULT 0,
    observacoes TEXT,
    CONSTRAINT fk_item_venda FOREIGN KEY (venda_id)
        REFERENCES banhoetosa.vendas(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_animal_servico FOREIGN KEY (animal_servico_id)
        REFERENCES banhoetosa.animal_servico(id) ON DELETE RESTRICT,
    CONSTRAINT chk_valor_pago_item_valido CHECK (valor_pago_item >= 0 AND valor_pago_item <= valor_final_item)
);

CREATE TABLE IF NOT EXISTS banhoetosa.venda_baixas (
    id BIGSERIAL PRIMARY KEY,
    venda_id BIGINT NOT NULL,
    forma_pagamento_id BIGINT NOT NULL,
    valor_baixa DECIMAL(10,2) NOT NULL,
    valor_taxa DECIMAL(10,2) DEFAULT 0,
    valor_liquido DECIMAL(10,2) NOT NULL DEFAULT 0,
    data_baixa TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    numero_parcelas INTEGER DEFAULT 1,
    data_primeira_parcela DATE,
    observacoes TEXT,
    usuario_id BIGINT NOT NULL,
    maquininha_id BIGINT,
    bandeira_id BIGINT,
    tipo_transacao VARCHAR(50),
    data_prevista_recebimento DATE,
    data_efetiva_recebimento DATE,
    status_recebimento VARCHAR(50) DEFAULT 'pendente',
    CONSTRAINT fk_baixa_venda FOREIGN KEY (venda_id)
        REFERENCES banhoetosa.vendas(id) ON DELETE CASCADE,
    CONSTRAINT fk_baixa_forma_pagamento FOREIGN KEY (forma_pagamento_id)
        REFERENCES banhoetosa.formas_pagamento(id) ON DELETE RESTRICT,
    CONSTRAINT fk_baixa_usuario FOREIGN KEY (usuario_id)
        REFERENCES banhoetosa.usuarios(id) ON DELETE RESTRICT,
    CONSTRAINT fk_baixa_maquininha FOREIGN KEY (maquininha_id)
        REFERENCES banhoetosa.maquininhas(id) ON DELETE SET NULL,
    CONSTRAINT fk_baixa_bandeira FOREIGN KEY (bandeira_id)
        REFERENCES banhoetosa.bandeiras(id) ON DELETE SET NULL,
    CONSTRAINT chk_status_recebimento CHECK (status_recebimento IN ('pendente', 'recebido', 'antecipado', 'estornado')),
    CONSTRAINT chk_tipo_transacao_baixa CHECK (tipo_transacao IS NULL OR tipo_transacao IN ('debito', 'credito_avista', 'credito_parcelado', 'pix'))
);

-- ================================================================================
-- SESSÃO 5: FOREIGN KEYS ADICIONAIS
-- ================================================================================

ALTER TABLE banhoetosa.animal_servico
DROP CONSTRAINT IF EXISTS fk_animal_servico_venda;

ALTER TABLE banhoetosa.animal_servico
ADD CONSTRAINT fk_animal_servico_venda FOREIGN KEY (venda_id)
    REFERENCES banhoetosa.vendas(id) ON DELETE SET NULL;

-- ================================================================================
-- SESSÃO 6: ÍNDICES PARA PERFORMANCE
-- ================================================================================

CREATE INDEX IF NOT EXISTS idx_contas_bancarias_ativo ON banhoetosa.contas_bancarias(ativo);
CREATE INDEX IF NOT EXISTS idx_maquininhas_adquirente ON banhoetosa.maquininhas(adquirente_id);
CREATE INDEX IF NOT EXISTS idx_maquininhas_conta ON banhoetosa.maquininhas(conta_bancaria_id);
CREATE INDEX IF NOT EXISTS idx_maquininhas_ativo ON banhoetosa.maquininhas(ativo);
CREATE INDEX IF NOT EXISTS idx_taxa_maquininha ON banhoetosa.maquininha_taxas(maquininha_id);
CREATE INDEX IF NOT EXISTS idx_taxa_bandeira ON banhoetosa.maquininha_taxas(bandeira_id);
CREATE INDEX IF NOT EXISTS idx_taxa_tipo ON banhoetosa.maquininha_taxas(tipo_transacao);
CREATE INDEX IF NOT EXISTS idx_formas_pagamento_maquininha ON banhoetosa.formas_pagamento(maquininha_id);
CREATE INDEX IF NOT EXISTS idx_vendas_cliente ON banhoetosa.vendas(cliente_id);
CREATE INDEX IF NOT EXISTS idx_vendas_data ON banhoetosa.vendas(data_venda);
CREATE INDEX IF NOT EXISTS idx_vendas_status ON banhoetosa.vendas(status_venda);
CREATE INDEX IF NOT EXISTS idx_vendas_codigo ON banhoetosa.vendas(codigo_venda);
CREATE INDEX IF NOT EXISTS idx_vendas_usuario ON banhoetosa.vendas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_venda_itens_venda ON banhoetosa.venda_itens(venda_id);
CREATE INDEX IF NOT EXISTS idx_venda_itens_animal_servico ON banhoetosa.venda_itens(animal_servico_id);
CREATE INDEX IF NOT EXISTS idx_baixas_venda ON banhoetosa.venda_baixas(venda_id);
CREATE INDEX IF NOT EXISTS idx_baixas_forma_pagamento ON banhoetosa.venda_baixas(forma_pagamento_id);
CREATE INDEX IF NOT EXISTS idx_baixas_data ON banhoetosa.venda_baixas(data_baixa);
CREATE INDEX IF NOT EXISTS idx_baixa_maquininha ON banhoetosa.venda_baixas(maquininha_id);
CREATE INDEX IF NOT EXISTS idx_baixa_bandeira ON banhoetosa.venda_baixas(bandeira_id);
CREATE INDEX IF NOT EXISTS idx_baixa_status_recebimento ON banhoetosa.venda_baixas(status_recebimento);
CREATE INDEX IF NOT EXISTS idx_baixa_data_prevista ON banhoetosa.venda_baixas(data_prevista_recebimento);
CREATE INDEX IF NOT EXISTS idx_baixa_data_efetiva ON banhoetosa.venda_baixas(data_efetiva_recebimento);
CREATE INDEX IF NOT EXISTS idx_servicos_adicionais_principal ON banhoetosa.servicos_adicionais(animal_servico_principal_id);
CREATE INDEX IF NOT EXISTS idx_servicos_adicionais_servico ON banhoetosa.servicos_adicionais(servico_adicional_id);
CREATE INDEX IF NOT EXISTS idx_servicos_adicionais_data ON banhoetosa.servicos_adicionais(data_adicao);
CREATE INDEX IF NOT EXISTS idx_servicos_adicionais_status ON banhoetosa.servicos_adicionais(status_pagamento);
CREATE INDEX IF NOT EXISTS idx_servicos_adicionais_data_realizacao ON banhoetosa.servicos_adicionais(data_realizacao);

-- ================================================================================
-- SESSÃO 7: DADOS INICIAIS ESSENCIAIS
-- ================================================================================

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
('Boleto', 'boleto', 0, 2.50, 1, 3)
ON CONFLICT (nome) DO NOTHING;

INSERT INTO banhoetosa.usuarios (nome, email, senha, role) VALUES
('Admin Sistema', 'admin@prontodog.com', 'senha123', 'ADMIN')
ON CONFLICT (email) DO NOTHING;

-- ================================================================================
-- SESSÃO 8: FUNÇÕES AUXILIARES
-- ================================================================================

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

-- ================================================================================
-- FIM DO SCRIPT
-- ================================================================================

SELECT '✅ Todas as tabelas foram criadas com sucesso!' as status;
