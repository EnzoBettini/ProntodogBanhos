-- =====================================================
-- MIGRAÇÃO: Adicionar data_realizacao em servicos_adicionais
-- Feature: Permitir edição da data de realização do serviço adicional
-- =====================================================

-- 1. Adicionar coluna data_realizacao à tabela servicos_adicionais
ALTER TABLE banhoetosa.servicos_adicionais
ADD COLUMN data_realizacao DATE;

-- 2. Atualizar registros existentes: herdar data_servico do serviço pai
UPDATE banhoetosa.servicos_adicionais sa
SET data_realizacao = (
    SELECT asp.data_servico
    FROM banhoetosa.animal_servico asp
    WHERE asp.id = sa.animal_servico_principal_id
)
WHERE sa.data_realizacao IS NULL;

-- 3. Tornar a coluna NOT NULL após preencher os valores existentes
ALTER TABLE banhoetosa.servicos_adicionais
ALTER COLUMN data_realizacao SET NOT NULL;

-- 4. Adicionar comentário para documentação
COMMENT ON COLUMN banhoetosa.servicos_adicionais.data_realizacao IS 'Data em que o serviço adicional foi realizado (por padrão, herda do serviço principal)';

-- 5. Criar índice para melhorar performance de consultas por data
CREATE INDEX idx_servicos_adicionais_data_realizacao 
ON banhoetosa.servicos_adicionais(data_realizacao);

-- =====================================================
-- ROLLBACK (caso necessário)
-- =====================================================
-- DROP INDEX IF EXISTS banhoetosa.idx_servicos_adicionais_data_realizacao;
-- ALTER TABLE banhoetosa.servicos_adicionais DROP COLUMN IF EXISTS data_realizacao;
