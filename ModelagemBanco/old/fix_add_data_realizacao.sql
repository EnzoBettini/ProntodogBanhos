-- =====================================================
-- FIX RÁPIDO: Adicionar data_realizacao em servicos_adicionais
-- =====================================================
-- Este script adiciona apenas a coluna data_realizacao que está faltando

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

-- 3. Tornar a coluna NOT NULL após preencher os valores existentes
ALTER TABLE banhoetosa.servicos_adicionais
ALTER COLUMN data_realizacao SET NOT NULL;

-- 4. Adicionar comentário para documentação
COMMENT ON COLUMN banhoetosa.servicos_adicionais.data_realizacao IS
'Data em que o serviço adicional foi realizado (por padrão, herda do serviço principal)';

-- 5. Criar índice para melhorar performance de consultas por data
CREATE INDEX IF NOT EXISTS idx_servicos_adicionais_data_realizacao
ON banhoetosa.servicos_adicionais(data_realizacao);

-- Verificar se foi criado corretamente
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'banhoetosa'
  AND table_name = 'servicos_adicionais'
  AND column_name = 'data_realizacao';

