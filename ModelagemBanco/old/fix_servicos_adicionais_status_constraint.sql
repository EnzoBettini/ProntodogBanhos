-- ============================================
-- FIX: Adicionar 'parcial' à constraint de status_pagamento em servicos_adicionais
-- ============================================
-- Data: 2025-10-20
-- Descrição: A constraint chk_status_pagamento da tabela servicos_adicionais
--           não incluía o valor 'parcial', causando erro ao criar serviços
--           adicionais em vendas parcialmente pagas.
-- ============================================

-- Remover constraint antiga
ALTER TABLE banhoetosa.servicos_adicionais
DROP CONSTRAINT IF EXISTS chk_status_pagamento;

-- Adicionar nova constraint com suporte a 'parcial'
ALTER TABLE banhoetosa.servicos_adicionais
ADD CONSTRAINT chk_status_pagamento
CHECK (status_pagamento IN ('em_aberto', 'parcial', 'pago', 'cancelado'));

-- Verificar se funcionou
SELECT
    conname AS constraint_name,
    pg_get_constraintdef(oid) AS constraint_definition
FROM pg_constraint
WHERE conrelid = 'banhoetosa.servicos_adicionais'::regclass
  AND conname = 'chk_status_pagamento';

-- Comentário
COMMENT ON CONSTRAINT chk_status_pagamento ON banhoetosa.servicos_adicionais IS
'Status do pagamento: em_aberto (0%), parcial (1-99%), pago (100%), cancelado';

COMMENT ON COLUMN banhoetosa.servicos_adicionais.status_pagamento IS
'Status do pagamento do serviço adicional (herda do serviço principal se não especificado): em_aberto, parcial, pago, cancelado';

