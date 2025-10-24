-- Adicionar campo para rastrear valor pago de cada item
-- E adicionar status "parcial" para animal_servico

-- 1. Adicionar coluna valor_pago_item em venda_itens
ALTER TABLE banhoetosa.venda_itens
ADD COLUMN IF NOT EXISTS valor_pago_item DECIMAL(10,2) DEFAULT 0.00 NOT NULL;

-- Constraint para garantir que valor_pago_item não seja maior que valor_final_item
ALTER TABLE banhoetosa.venda_itens
ADD CONSTRAINT chk_valor_pago_item_valido
CHECK (valor_pago_item >= 0 AND valor_pago_item <= valor_final_item);

-- 2. Atualizar constraint de status_pagamento em animal_servico para incluir 'parcial'
ALTER TABLE banhoetosa.animal_servico
DROP CONSTRAINT IF EXISTS chk_status_pagamento;

ALTER TABLE banhoetosa.animal_servico
ADD CONSTRAINT chk_status_pagamento
CHECK (status_pagamento IN ('em_aberto', 'parcial', 'pago', 'cancelado'));

-- 3. Comentários
COMMENT ON COLUMN banhoetosa.venda_itens.valor_pago_item IS
'Valor já pago deste item específico (distribuído proporcionalmente das baixas da venda)';

COMMENT ON CONSTRAINT chk_status_pagamento ON banhoetosa.animal_servico IS
'Status do pagamento: em_aberto (0%), parcial (1-99%), pago (100%), cancelado';

-- 4. Inicializar valor_pago_item dos itens existentes baseado no status atual
-- Se o animal_servico está pago, marcar o item como totalmente pago
UPDATE banhoetosa.venda_itens vi
SET valor_pago_item = vi.valor_final_item
FROM banhoetosa.animal_servico as_tbl
WHERE vi.animal_servico_id = as_tbl.id
  AND as_tbl.status_pagamento = 'pago';

-- 5. Criar função para calcular percentual pago de um item
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

COMMENT ON FUNCTION banhoetosa.calcular_percentual_pago_item IS
'Calcula o percentual pago de um item da venda (0-100%)';

