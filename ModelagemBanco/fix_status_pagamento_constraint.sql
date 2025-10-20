-- Script para corrigir o constraint de status_pagamento no animal_servico
-- Adiciona suporte para o status "parcial"

-- Remover todos os possíveis constraints de status_pagamento
ALTER TABLE banhoetosa.animal_servico
DROP CONSTRAINT IF EXISTS animal_servico_status_pagamento_check;

ALTER TABLE banhoetosa.animal_servico
DROP CONSTRAINT IF EXISTS chk_status_pagamento;

-- Adicionar o novo constraint com suporte a 'parcial'
ALTER TABLE banhoetosa.animal_servico
ADD CONSTRAINT animal_servico_status_pagamento_check
CHECK (status_pagamento IN ('em_aberto', 'parcial', 'pago', 'cancelado'));

-- Verificar se funcionou
SELECT conname, pg_get_constraintdef(oid)
FROM pg_constraint
WHERE conrelid = 'banhoetosa.animal_servico'::regclass
  AND conname LIKE '%status_pagamento%';

-- Comentário
COMMENT ON CONSTRAINT animal_servico_status_pagamento_check ON banhoetosa.animal_servico IS
'Status do pagamento: em_aberto (0%), parcial (1-99%), pago (100%), cancelado';

