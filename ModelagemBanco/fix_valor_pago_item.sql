-- ================================================================================
-- FIX: Adicionar coluna valor_pago_item em venda_itens
-- ================================================================================
-- Este script corrige o erro:
-- "ERROR: column i1_0.valor_pago_item does not exist"
--
-- A coluna valor_pago_item é necessária para o sistema de pagamento proporcional
-- que rastreia quanto foi pago de cada item da venda.
-- ================================================================================

-- 1. Adicionar coluna valor_pago_item em venda_itens
ALTER TABLE banhoetosa.venda_itens
ADD COLUMN IF NOT EXISTS valor_pago_item DECIMAL(10,2) DEFAULT 0.00 NOT NULL;

-- 2. Adicionar constraint para validação
ALTER TABLE banhoetosa.venda_itens
DROP CONSTRAINT IF EXISTS chk_valor_pago_item_valido;

ALTER TABLE banhoetosa.venda_itens
ADD CONSTRAINT chk_valor_pago_item_valido
CHECK (valor_pago_item >= 0 AND valor_pago_item <= valor_final_item);

-- 3. Inicializar valor_pago_item dos itens existentes baseado no status atual
-- Se o animal_servico está com status 'pago', marca o item como 100% pago
UPDATE banhoetosa.venda_itens vi
SET valor_pago_item = vi.valor_final_item
FROM banhoetosa.animal_servico as_tbl
WHERE vi.animal_servico_id = as_tbl.id
  AND as_tbl.status_pagamento = 'pago'
  AND vi.valor_pago_item = 0;

-- 4. Adicionar comentário para documentação
COMMENT ON COLUMN banhoetosa.venda_itens.valor_pago_item IS
'Valor já pago deste item específico (distribuído proporcionalmente das baixas da venda)';

-- ================================================================================
-- VERIFICAÇÃO
-- ================================================================================

-- Verificar se a coluna foi criada corretamente
SELECT
    column_name,
    data_type,
    column_default,
    is_nullable
FROM information_schema.columns
WHERE table_schema = 'banhoetosa'
  AND table_name = 'venda_itens'
  AND column_name = 'valor_pago_item';

-- Verificar dados atualizados
SELECT
    vi.id,
    vi.venda_id,
    vi.valor_final_item,
    vi.valor_pago_item,
    CASE
        WHEN vi.valor_pago_item = 0 THEN 'Em aberto'
        WHEN vi.valor_pago_item >= vi.valor_final_item THEN 'Pago'
        ELSE 'Parcial'
    END as status
FROM banhoetosa.venda_itens vi
ORDER BY vi.id
LIMIT 10;

-- ================================================================================
-- RESULTADO ESPERADO
-- ================================================================================
-- Se tudo correr bem, você verá:
--
-- column_name      | data_type | column_default | is_nullable
-- -----------------+-----------+----------------+-------------
-- valor_pago_item  | numeric   | 0.00           | NO
--
-- ✅ Coluna criada com sucesso!
-- ✅ Constraint adicionado!
-- ✅ Dados inicializados!
-- ================================================================================

SELECT '✅ Script executado com sucesso!' as status;

