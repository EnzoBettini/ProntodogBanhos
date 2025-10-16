-- =====================================================
-- MIGRAÇÃO: Implementar Serviços Adicionais
-- =====================================================

-- 1. Nova tabela para relacionar serviços principais com adicionais
CREATE TABLE banhoetosa.servicos_adicionais (
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
    CONSTRAINT chk_status_pagamento CHECK (status_pagamento IN ('em_aberto', 'pago', 'cancelado'))
);

-- 2. Adicionar campo opcional na tabela servicos para identificar se pode ser adicional
ALTER TABLE banhoetosa.servicos
ADD COLUMN pode_ser_adicional BOOLEAN DEFAULT true,
ADD COLUMN categoria VARCHAR(50) DEFAULT 'geral';

-- 3. Índices para performance
CREATE INDEX idx_servicos_adicionais_principal ON banhoetosa.servicos_adicionais(animal_servico_principal_id);
CREATE INDEX idx_servicos_adicionais_servico ON banhoetosa.servicos_adicionais(servico_adicional_id);
CREATE INDEX idx_servicos_adicionais_data ON banhoetosa.servicos_adicionais(data_adicao);
CREATE INDEX idx_servicos_adicionais_status ON banhoetosa.servicos_adicionais(status_pagamento);

-- 4. Inserir categorias nos serviços existentes
UPDATE banhoetosa.servicos SET categoria = 'banho' WHERE nome ILIKE '%banho%';
UPDATE banhoetosa.servicos SET categoria = 'tosa' WHERE nome ILIKE '%tosa%';
UPDATE banhoetosa.servicos SET categoria = 'pacote' WHERE quantidade > 1;

-- 5. Comentários para documentação
COMMENT ON TABLE banhoetosa.servicos_adicionais IS 'Registra serviços adicionais realizados durante um atendimento principal';
COMMENT ON COLUMN banhoetosa.servicos_adicionais.animal_servico_principal_id IS 'ID do serviço principal (ex: banho agendado)';
COMMENT ON COLUMN banhoetosa.servicos_adicionais.servico_adicional_id IS 'ID do serviço adicional realizado (ex: tosa)';
COMMENT ON COLUMN banhoetosa.servicos_adicionais.quantidade_adicional IS 'Quantidade do serviço adicional realizado';
COMMENT ON COLUMN banhoetosa.servicos_adicionais.valor_unitario IS 'Valor unitário do adicional no momento da realização';
COMMENT ON COLUMN banhoetosa.servicos_adicionais.valor_total IS 'Valor total (quantidade * valor_unitario)';

-- 6. View para facilitar consultas
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
