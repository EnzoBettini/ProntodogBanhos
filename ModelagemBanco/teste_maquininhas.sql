-- ================================================================================
-- SCRIPT DE TESTES - Sistema de Maquininhas
-- ================================================================================
-- Este script cria dados de teste para validar o sistema de maquininhas
-- Execute APÓS o script sistema_maquininhas.sql
-- ================================================================================

-- ================================================================================
-- TESTE 1: Criar Contas Bancárias
-- ================================================================================

INSERT INTO banhoetosa.contas_bancarias (nome, banco, agencia, conta, tipo, pix_chave)
VALUES
    ('ITAU NAV Principal', 'Itaú', '0001', '12345-6', 'corrente', 'clinica@prontodog.com'),
    ('Bradesco Movimento', 'Bradesco', '0002', '98765-4', 'corrente', '11999887766'),
    ('Nubank PIX', 'Nubank', NULL, '11122233344', 'pagamento', 'contato@prontodog.com')
ON CONFLICT (nome) DO NOTHING;

SELECT '✅ Contas bancárias criadas' as status;
SELECT * FROM banhoetosa.contas_bancarias;


-- ================================================================================
-- TESTE 2: Criar Maquininhas
-- ================================================================================

-- Maquininha 1: Getnet com antecipação automática
INSERT INTO banhoetosa.maquininhas (
    nome,
    adquirente_id,
    conta_bancaria_id,
    prazo_recebimento_debito,
    prazo_recebimento_credito,
    aceita_antecipacao,
    antecipacao_automatica,
    taxa_antecipacao_mensal,
    aceita_pix,
    taxa_pix,
    prazo_recebimento_pix
) VALUES (
    'Getnet Loja 1 (Antecipação Automática)',
    (SELECT id FROM banhoetosa.adquirentes WHERE nome = 'Getnet'),
    (SELECT id FROM banhoetosa.contas_bancarias WHERE nome = 'ITAU NAV Principal'),
    1,        -- Débito em 1 dia
    30,       -- Crédito em 30 dias (mas com antecipação automática)
    true,     -- Aceita antecipação
    true,     -- ANTECIPAÇÃO AUTOMÁTICA
    2.50,     -- 2.5% ao mês
    true,     -- Aceita PIX
    0.99,     -- PIX com taxa de 0.99%
    0         -- PIX no mesmo dia
);

-- Maquininha 2: Stone sem antecipação
INSERT INTO banhoetosa.maquininhas (
    nome,
    adquirente_id,
    conta_bancaria_id,
    prazo_recebimento_debito,
    prazo_recebimento_credito,
    aceita_antecipacao,
    antecipacao_automatica,
    taxa_antecipacao_mensal,
    aceita_pix,
    taxa_pix,
    prazo_recebimento_pix
) VALUES (
    'Stone Loja Principal',
    (SELECT id FROM banhoetosa.adquirentes WHERE nome = 'Stone'),
    (SELECT id FROM banhoetosa.contas_bancarias WHERE nome = 'Bradesco Movimento'),
    1,        -- Débito em 1 dia
    30,       -- Crédito em 30 dias
    false,    -- Não aceita antecipação
    false,
    0,
    true,     -- Aceita PIX
    0.00,     -- PIX sem taxa
    0         -- PIX no mesmo dia
);

SELECT '✅ Maquininhas criadas' as status;
SELECT * FROM banhoetosa.vw_maquininhas_completas;


-- ================================================================================
-- TESTE 3: Configurar Taxas - Getnet
-- ================================================================================

-- Getnet - Visa
DO $$
DECLARE
    v_maquininha_id BIGINT;
    v_bandeira_id BIGINT;
BEGIN
    -- IDs
    SELECT id INTO v_maquininha_id FROM banhoetosa.maquininhas WHERE nome LIKE 'Getnet%' LIMIT 1;
    SELECT id INTO v_bandeira_id FROM banhoetosa.bandeiras WHERE nome = 'Visa';

    -- Débito Visa: 2.00%
    INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas, taxa_percentual)
    VALUES (v_maquininha_id, v_bandeira_id, 'debito', NULL, 2.00);

    -- Crédito à vista Visa: 3.50%
    INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas, taxa_percentual)
    VALUES (v_maquininha_id, v_bandeira_id, 'credito_avista', 1, 3.50);

    -- Crédito parcelado Visa: 2x a 12x (4% + 0.5% por parcela)
    FOR parcela IN 2..12 LOOP
        INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas, taxa_percentual)
        VALUES (v_maquininha_id, v_bandeira_id, 'credito_parcelado', parcela, 3.5 + (parcela * 0.5));
    END LOOP;

    RAISE NOTICE '✅ Taxas Visa configuradas na Getnet';
END $$;

-- Getnet - Mastercard
DO $$
DECLARE
    v_maquininha_id BIGINT;
    v_bandeira_id BIGINT;
BEGIN
    SELECT id INTO v_maquininha_id FROM banhoetosa.maquininhas WHERE nome LIKE 'Getnet%' LIMIT 1;
    SELECT id INTO v_bandeira_id FROM banhoetosa.bandeiras WHERE nome = 'Mastercard';

    -- Débito: 2.00%
    INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas, taxa_percentual)
    VALUES (v_maquininha_id, v_bandeira_id, 'debito', NULL, 2.00);

    -- Crédito à vista: 3.50%
    INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas, taxa_percentual)
    VALUES (v_maquininha_id, v_bandeira_id, 'credito_avista', 1, 3.50);

    -- Parcelado: 2x a 12x
    FOR parcela IN 2..12 LOOP
        INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas, taxa_percentual)
        VALUES (v_maquininha_id, v_bandeira_id, 'credito_parcelado', parcela, 3.5 + (parcela * 0.5));
    END LOOP;

    RAISE NOTICE '✅ Taxas Mastercard configuradas na Getnet';
END $$;

-- Getnet - Elo (taxas mais altas)
DO $$
DECLARE
    v_maquininha_id BIGINT;
    v_bandeira_id BIGINT;
BEGIN
    SELECT id INTO v_maquininha_id FROM banhoetosa.maquininhas WHERE nome LIKE 'Getnet%' LIMIT 1;
    SELECT id INTO v_bandeira_id FROM banhoetosa.bandeiras WHERE nome = 'Elo';

    -- Débito: 2.20%
    INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas, taxa_percentual)
    VALUES (v_maquininha_id, v_bandeira_id, 'debito', NULL, 2.20);

    -- Crédito à vista: 3.80%
    INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas, taxa_percentual)
    VALUES (v_maquininha_id, v_bandeira_id, 'credito_avista', 1, 3.80);

    -- Parcelado: 2x a 12x (taxas mais altas)
    FOR parcela IN 2..12 LOOP
        INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas, taxa_percentual)
        VALUES (v_maquininha_id, v_bandeira_id, 'credito_parcelado', parcela, 4.0 + (parcela * 0.5));
    END LOOP;

    RAISE NOTICE '✅ Taxas Elo configuradas na Getnet';
END $$;


-- ================================================================================
-- TESTE 4: Configurar Taxas - Stone
-- ================================================================================

-- Stone - Visa
DO $$
DECLARE
    v_maquininha_id BIGINT;
    v_bandeira_id BIGINT;
BEGIN
    SELECT id INTO v_maquininha_id FROM banhoetosa.maquininhas WHERE nome LIKE 'Stone%' LIMIT 1;
    SELECT id INTO v_bandeira_id FROM banhoetosa.bandeiras WHERE nome = 'Visa';

    -- Débito: 1.80% (Stone tem taxas menores)
    INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas, taxa_percentual)
    VALUES (v_maquininha_id, v_bandeira_id, 'debito', NULL, 1.80);

    -- Crédito à vista: 3.20%
    INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas, taxa_percentual)
    VALUES (v_maquininha_id, v_bandeira_id, 'credito_avista', 1, 3.20);

    -- Parcelado: 2x a 12x
    FOR parcela IN 2..12 LOOP
        INSERT INTO banhoetosa.maquininha_taxas (maquininha_id, bandeira_id, tipo_transacao, numero_parcelas, taxa_percentual)
        VALUES (v_maquininha_id, v_bandeira_id, 'credito_parcelado', parcela, 3.0 + (parcela * 0.4));
    END LOOP;

    RAISE NOTICE '✅ Taxas Visa configuradas na Stone';
END $$;

SELECT '✅ Todas as taxas configuradas' as status;
SELECT * FROM banhoetosa.vw_taxas_maquininhas ORDER BY maquininha_nome, bandeira_nome, tipo_transacao, numero_parcelas;


-- ================================================================================
-- TESTE 5: Testar Funções - Cálculo de Taxa
-- ================================================================================

SELECT '🧪 TESTE: Calcular taxa Getnet - Visa Crédito 3x de R$ 300' as teste;
SELECT * FROM banhoetosa.calcular_taxa_maquininha(
    (SELECT id FROM banhoetosa.maquininhas WHERE nome LIKE 'Getnet%' LIMIT 1),
    (SELECT id FROM banhoetosa.bandeiras WHERE nome = 'Visa'),
    'credito_parcelado',
    3,
    300.00
);
-- Esperado: 5% = R$ 15,00 de taxa | Líquido = R$ 285,00

SELECT '🧪 TESTE: Calcular taxa Stone - Visa Débito de R$ 100' as teste;
SELECT * FROM banhoetosa.calcular_taxa_maquininha(
    (SELECT id FROM banhoetosa.maquininhas WHERE nome LIKE 'Stone%' LIMIT 1),
    (SELECT id FROM banhoetosa.bandeiras WHERE nome = 'Visa'),
    'debito',
    NULL,
    100.00
);
-- Esperado: 1.80% = R$ 1,80 de taxa | Líquido = R$ 98,20


-- ================================================================================
-- TESTE 6: Testar Funções - Cálculo de Data de Recebimento
-- ================================================================================

SELECT '🧪 TESTE: Calcular data recebimento Getnet (antecipação automática)' as teste;
SELECT banhoetosa.calcular_data_recebimento(
    (SELECT id FROM banhoetosa.maquininhas WHERE nome LIKE 'Getnet%' LIMIT 1),
    'credito_avista',
    1,
    CURRENT_DATE
) as data_prevista;
-- Esperado: CURRENT_DATE + 1 (antecipação automática reduz para 1 dia)

SELECT '🧪 TESTE: Calcular data recebimento Stone (sem antecipação)' as teste;
SELECT banhoetosa.calcular_data_recebimento(
    (SELECT id FROM banhoetosa.maquininhas WHERE nome LIKE 'Stone%' LIMIT 1),
    'credito_avista',
    1,
    CURRENT_DATE
) as data_prevista;
-- Esperado: CURRENT_DATE + 30


-- ================================================================================
-- TESTE 7: Testar Função de PIX
-- ================================================================================

SELECT '🧪 TESTE: Taxa PIX Getnet de R$ 200' as teste;
SELECT * FROM banhoetosa.buscar_taxa_pix_maquininha(
    (SELECT id FROM banhoetosa.maquininhas WHERE nome LIKE 'Getnet%' LIMIT 1),
    200.00
);
-- Esperado: 0.99% = R$ 1,98 de taxa | Líquido = R$ 198,02

SELECT '🧪 TESTE: Taxa PIX Stone de R$ 200' as teste;
SELECT * FROM banhoetosa.buscar_taxa_pix_maquininha(
    (SELECT id FROM banhoetosa.maquininhas WHERE nome LIKE 'Stone%' LIMIT 1),
    200.00
);
-- Esperado: 0% = R$ 0,00 de taxa | Líquido = R$ 200,00


-- ================================================================================
-- TESTE 8: Simular Venda com Maquininha
-- ================================================================================

DO $$
DECLARE
    v_cliente_id BIGINT;
    v_usuario_id BIGINT;
    v_animal_id BIGINT;
    v_servico_id BIGINT;
    v_venda_id BIGINT;
    v_animal_servico_id BIGINT;
    v_forma_pagamento_id BIGINT;
    v_maquininha_id BIGINT;
    v_bandeira_id BIGINT;
    v_taxa_info RECORD;
    v_data_recebimento DATE;
BEGIN
    -- Buscar dados necessários
    SELECT id INTO v_cliente_id FROM banhoetosa.clientes LIMIT 1;
    SELECT id INTO v_usuario_id FROM banhoetosa.usuarios LIMIT 1;
    SELECT id INTO v_animal_id FROM banhoetosa.animais LIMIT 1;
    SELECT id INTO v_servico_id FROM banhoetosa.servicos LIMIT 1;
    SELECT id INTO v_forma_pagamento_id FROM banhoetosa.formas_pagamento WHERE nome = 'Crédito 3x' LIMIT 1;
    SELECT id INTO v_maquininha_id FROM banhoetosa.maquininhas WHERE nome LIKE 'Getnet%' LIMIT 1;
    SELECT id INTO v_bandeira_id FROM banhoetosa.bandeiras WHERE nome = 'Visa' LIMIT 1;

    IF v_cliente_id IS NOT NULL AND v_usuario_id IS NOT NULL THEN
        -- Criar venda
        INSERT INTO banhoetosa.vendas (cliente_id, usuario_id, tipo_venda, valor_bruto, valor_total, valor_pendente)
        VALUES (v_cliente_id, v_usuario_id, 'presencial', 300.00, 300.00, 300.00)
        RETURNING id INTO v_venda_id;

        RAISE NOTICE '✅ Venda #% criada', v_venda_id;

        IF v_animal_id IS NOT NULL AND v_servico_id IS NOT NULL THEN
            -- Criar animal_servico
            INSERT INTO banhoetosa.animal_servico (animal_id, servico_id, usuario_id, data_servico, banhos_usados, valor_cobrado, venda_id)
            VALUES (v_animal_id, v_servico_id, v_usuario_id, CURRENT_DATE, 1, 300.00, v_venda_id)
            RETURNING id INTO v_animal_servico_id;

            -- Criar item da venda
            INSERT INTO banhoetosa.venda_itens (venda_id, animal_servico_id, valor_item, valor_final_item)
            VALUES (v_venda_id, v_animal_servico_id, 300.00, 300.00);

            RAISE NOTICE '✅ Item adicionado à venda';
        END IF;

        -- Calcular taxa
        SELECT * INTO v_taxa_info FROM banhoetosa.calcular_taxa_maquininha(
            v_maquininha_id,
            v_bandeira_id,
            'credito_parcelado',
            3,
            300.00
        );

        -- Calcular data de recebimento
        v_data_recebimento := banhoetosa.calcular_data_recebimento(
            v_maquininha_id,
            'credito_parcelado',
            3,
            CURRENT_DATE
        );

        RAISE NOTICE '💰 Taxa calculada: % %% = R$ % | Líquido: R$ %',
            v_taxa_info.taxa_percentual, v_taxa_info.valor_taxa, v_taxa_info.valor_liquido;
        RAISE NOTICE '📅 Data prevista recebimento: %', v_data_recebimento;

        -- Registrar baixa COM MAQUININHA
        INSERT INTO banhoetosa.venda_baixas (
            venda_id,
            forma_pagamento_id,
            maquininha_id,
            bandeira_id,
            tipo_transacao,
            valor_baixa,
            valor_taxa,
            valor_liquido,
            numero_parcelas,
            data_prevista_recebimento,
            status_recebimento,
            usuario_id
        ) VALUES (
            v_venda_id,
            v_forma_pagamento_id,
            v_maquininha_id,
            v_bandeira_id,
            'credito_parcelado',
            300.00,
            v_taxa_info.valor_taxa,
            v_taxa_info.valor_liquido,
            3,
            v_data_recebimento,
            'pendente',
            v_usuario_id
        );

        -- Atualizar venda
        UPDATE banhoetosa.vendas
        SET valor_pago = 300.00, valor_pendente = 0, status_venda = 'pago'
        WHERE id = v_venda_id;

        RAISE NOTICE '✅ Baixa registrada com maquininha!';
        RAISE NOTICE '🎉 Venda #% completa com pagamento em maquininha', v_venda_id;
    ELSE
        RAISE NOTICE '❌ Dados insuficientes para criar venda de teste';
    END IF;
END $$;


-- ================================================================================
-- TESTE 9: Ver Recebimentos Pendentes
-- ================================================================================

SELECT '📊 RECEBIMENTOS PENDENTES (Fluxo de Caixa):' as titulo;
SELECT
    codigo_venda,
    cliente_nome,
    maquininha_nome,
    adquirente_nome,
    bandeira_nome,
    tipo_transacao,
    valor_baixa,
    valor_taxa,
    valor_liquido,
    data_baixa::date,
    data_prevista_recebimento,
    dias_ate_receber,
    status_recebimento,
    conta_destino
FROM banhoetosa.vw_recebimentos_pendentes
ORDER BY data_prevista_recebimento;


-- ================================================================================
-- TESTE 10: Resumo Final
-- ================================================================================

SELECT '📊 RESUMO DO SISTEMA:' as titulo;

SELECT
    'Contas Bancárias' as item, COUNT(*) as quantidade FROM banhoetosa.contas_bancarias WHERE ativo = TRUE
UNION ALL
SELECT 'Adquirentes', COUNT(*) FROM banhoetosa.adquirentes WHERE ativo = TRUE
UNION ALL
SELECT 'Bandeiras', COUNT(*) FROM banhoetosa.bandeiras WHERE ativo = TRUE
UNION ALL
SELECT 'Maquininhas', COUNT(*) FROM banhoetosa.maquininhas WHERE ativo = TRUE
UNION ALL
SELECT 'Taxas Configuradas', COUNT(*) FROM banhoetosa.maquininha_taxas
UNION ALL
SELECT 'Recebimentos Pendentes', COUNT(*) FROM banhoetosa.vw_recebimentos_pendentes;


-- ================================================================================
-- FIM DOS TESTES
-- ================================================================================

SELECT '✅ TODOS OS TESTES CONCLUÍDOS!' as status;
SELECT '🎉 Sistema de Maquininhas está funcionando!' as mensagem;

