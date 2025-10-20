-- ========================================
-- Script para adicionar campos de status de serviço único
-- Data: 2025-10-19
-- Descrição: Adiciona os campos status_servico e data_realizacao
--            para controlar serviços únicos (não-pacotes)
-- ========================================

-- Adicionar coluna status_servico
ALTER TABLE banhoetosa.animal_servico 
ADD COLUMN IF NOT EXISTS status_servico VARCHAR(20);

-- Adicionar coluna data_realizacao
ALTER TABLE banhoetosa.animal_servico 
ADD COLUMN IF NOT EXISTS data_realizacao DATE;

-- Comentários nas colunas para documentação
COMMENT ON COLUMN banhoetosa.animal_servico.status_servico IS 'Status do serviço único: "pendente" ou "realizado". NULL para pacotes (múltiplos banhos)';
COMMENT ON COLUMN banhoetosa.animal_servico.data_realizacao IS 'Data em que o serviço único foi realizado. NULL se ainda pendente ou se for um pacote';

-- Exibir estrutura atualizada da tabela
SELECT 
    column_name, 
    data_type, 
    is_nullable, 
    column_default
FROM information_schema.columns 
WHERE table_schema = 'banhoetosa' 
  AND table_name = 'animal_servico'
ORDER BY ordinal_position;
