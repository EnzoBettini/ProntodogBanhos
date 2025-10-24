-- Adicionar campo valor_cobrado em animal_servico
-- Este campo armazena o valor efetivamente cobrado quando o serviço está em uma venda

ALTER TABLE banhoetosa.animal_servico
ADD COLUMN IF NOT EXISTS valor_cobrado DECIMAL(10,2);

-- Comentário
COMMENT ON COLUMN banhoetosa.animal_servico.valor_cobrado IS
'Valor efetivamente cobrado por este serviço (pode ser diferente do valor de catálogo devido a descontos ou acréscimos aplicados na venda)';

-- Inicializar com o valor do catálogo para registros existentes
UPDATE banhoetosa.animal_servico asrv
SET valor_cobrado = s.valor
FROM banhoetosa.servicos s
WHERE asrv.servico_id = s.id
  AND asrv.valor_cobrado IS NULL;

