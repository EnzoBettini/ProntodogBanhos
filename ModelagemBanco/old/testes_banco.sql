insert into
    banhoetosa.clientes (
        codigo_simplesvet,
        nome_completo,
        cpf
    )
values (
        23050,
        'Enzo Teste 2',
        '11122233355'
    );

insert into
    banhoetosa.email_clientes (cliente_id, email)
values (1, 'testedasilva@hotmail.com');

insert into
    banhoetosa.telefones_clientes (cliente_id, telefone)
values (1, '44912345678');

insert into
    banhoetosa.animais (
        nome,
        codigo_simplesvet,
        tipo,
        cliente_id
    )
values ('Meg', 2025, 'Cachorro', 1);

insert into
    banhoetosa.servicos (
        nome,
        descricao,
        quantidade,
        valor
    )
values (
        'Pacote pequeno porte',
        'Pacote de banho para pequenos animais',
        4,
        130.50
    );

insert into
    banhoetosa.animal_servico (
        data_servico,
        banhos_usados,
        animal_id,
        servico_id
    )
values (CURRENT_DATE, 0, 1, 1);

select id, nome
from banhoetosa.servicos
where
    nome = 'Pacote pequeno porte';

select id, data_servico
from banhoetosa.animal_servico
where
    animal_id = 1;

SELECT
    id,
    codigo_cliente_sistema,
    nome_completo,
    cpf,
    codigo_simplesvet
FROM banhoetosa.clientes;

select c.nome_completo, t.telefone
from banhoetosa.clientes c
    left join banhoetosa.telefones_clientes t on c.id = t.cliente_id;



SELECT * FROM banhoetosa.clientes;

SELECT * FROM banhoetosa.telefones_clientes;

SELECT * FROM banhoetosa.email_clientes;

SELECT * FROM banhoetosa.animais;

SELECT * FROM banhoetosa.servicos;

SELECT * FROM banhoetosa.animal_servico;




select c.nome_completo, t.telefone
from banhoetosa.clientes c
    left join banhoetosa.telefones_clientes t on c.id = t.cliente_id;

select n.nome_completo, a.nome
from banhoetosa.clientes n
    left join banhoetosa.animais a on n.id = a.cliente_id;




SELECT * FROM banhoetosa.clientes;

SELECT * FROM banhoetosa.telefones_clientes;

SELECT * FROM banhoetosa.email_clientes;

SELECT * FROM banhoetosa.animais;

SELECT * FROM banhoetosa.servicos;

SELECT * FROM banhoetosa.animal_servico;

insert into
    banhoetosa.email_clientes (cliente_id, email)
values (5, 'bruno@teste.com');

select c.nome_completo, t.telefone
from banhoetosa.clientes c
    left join banhoetosa.telefones_clientes t on c.id = t.cliente_id;

select n.nome_completo, a.nome
from banhoetosa.clientes n
    left join banhoetosa.animais a on n.id = a.cliente_id;

select na.nome AS animal,
    --ns.nome AS servico,
    b.banhos_usados, b.data_servico
FROM banhoetosa.animais na
    LEFT JOIN banhoetosa.animal_servico b ON na.id = b.animal_id
    LEFT JOIN banhoetosa.servicos ns ON b.servico_id = ns.id;

select
    nc.id,
    nc.nome_completo,
    na.nome AS nome_animal,
    ns.nome AS nome_servico,
    ec.email,
    b.banhos_usados,
    b.data_servico
FROM
    banhoetosa.clientes nc
    LEFT JOIN banhoetosa.animais na ON nc.id = na.cliente_id
    LEFT JOIN banhoetosa.animal_servico b ON na.id = b.animal_id
    LEFT JOIN banhoetosa.servicos ns ON b.servico_id = ns.id
    left join banhoetosa.email_clientes ec on nc.id = ec.cliente_id;

UPDATE banhoetosa.animal_servico
SET
    banhos_usados = banhos_usados + 1
WHERE
    id = 1;

SELECT *
FROM clientes c
    LEFT JOIN animais a ON c.id = a.cliente_id;
