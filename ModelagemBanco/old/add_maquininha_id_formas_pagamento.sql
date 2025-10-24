-- ================================================================================
-- Adicionar campo maquininha_id em formas_pagamento
-- ================================================================================
-- Este script vincula formas de pagamento às maquininhas cadastradas.
-- Permite que uma forma de pagamento seja criada automaticamente ao cadastrar
-- uma maquininha, facilitando o fluxo de registro de pagamentos.
-- ================================================================================

-- 1. Adicionar coluna maquininha_id (opcional)
ALTER TABLE banhoetosa.formas_pagamento
ADD COLUMN IF NOT EXISTS maquininha_id INTEGER;

-- 2. Adicionar foreign key
ALTER TABLE banhoetosa.formas_pagamento
ADD CONSTRAINT fk_formas_pagamento_maquininha
FOREIGN KEY (maquininha_id)
REFERENCES banhoetosa.maquininhas(id)
ON DELETE SET NULL;

-- 3. Adicionar índice para performance
CREATE INDEX IF NOT EXISTS idx_formas_pagamento_maquininha
ON banhoetosa.formas_pagamento(maquininha_id);

-- 4. Adicionar comentário
COMMENT ON COLUMN banhoetosa.formas_pagamento.maquininha_id IS
'ID da maquininha vinculada a esta forma de pagamento (se for pagamento via maquininha)';

-- ================================================================================
-- VERIFICAÇÃO
-- ================================================================================

SELECT
    column_name,
    data_type,
    is_nullable
FROM information_schema.columns
WHERE table_schema = 'banhoetosa'
  AND table_name = 'formas_pagamento'
  AND column_name = 'maquininha_id';

-- ================================================================================
SELECT '✅ Coluna maquininha_id adicionada com sucesso!' as status;

